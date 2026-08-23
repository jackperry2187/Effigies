package jackperry2187.effigies.gametest;

import jackperry2187.effigies.Effigies;
import jackperry2187.effigies.GrimoireTracker;
import jackperry2187.effigies.PikeRegistry;
import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.SpawnPreventionHandler;
import jackperry2187.effigies.block.AntiPikeBlock;
import jackperry2187.effigies.block.PikeBlock;
import jackperry2187.effigies.block.entity.AntiPikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeBlockEntity;
import jackperry2187.effigies.block.entity.PikeHeadBlockEntity;
import jackperry2187.effigies.config.DefaultSettings;
import jackperry2187.effigies.config.ConfigSettings;
import jackperry2187.effigies.config.ConfigSyncPayload;
import jackperry2187.effigies.config.InitializeConfig;
import jackperry2187.effigies.item.AntiSpearItem;
import jackperry2187.effigies.item.PikeItem;
import jackperry2187.effigies.registry.ModBlockEntities;
import jackperry2187.effigies.registry.ModBlocks;
import jackperry2187.effigies.registry.ModCreativeTabs;
import jackperry2187.effigies.registry.ModItems;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class EffigiesGameTests {
    static final Object TEST_STATE_LOCK = new Object();

    private EffigiesGameTests() {
    }

    static void resetSharedTestState() {
        synchronized (TEST_STATE_LOCK) {
            PikeRegistry.clearAll();
            SpawnPreventionHandler.disableAntiPikeBypass();
            ConfigSettings.reload();
        }
    }

    public static void registryAndDefaultMappings(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        String[] blockIds = {"wooden_pike", "stone_pike", "copper_pike", "iron_pike", "golden_pike", "diamond_pike", "netherite_pike", "pike_head", "anti_pike"};
        for (String blockId : blockIds) {
            helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(Effigies.id(blockId)), blockId + " block is missing");
        }
        String[] itemIds = {"wooden_pike", "stone_pike", "copper_pike", "iron_pike", "golden_pike", "diamond_pike", "netherite_pike", "anti_spear", "anti_pike", "grimoire"};
        for (String itemId : itemIds) {
            helper.assertTrue(BuiltInRegistries.ITEM.containsKey(Effigies.id(itemId)), itemId + " item is missing");
        }
        helper.assertTrue(BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(Effigies.id("pike")), "pike block entity type is missing");
        helper.assertTrue(BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(Effigies.id("pike_head")), "pike head block entity type is missing");
        helper.assertTrue(BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(Effigies.id("anti_pike")), "anti-pike block entity type is missing");

        EntityType<?> skeleton = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("skeleton"));
        EntityType<?> zombie = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("zombie"));
        helper.assertTrue(PikeBlockEntity.getHeadTypeFromItem(new ItemStack(Items.SKELETON_SKULL)) == skeleton,
            "skeleton skull should resolve to skeleton");
        helper.assertTrue(PikeBlockEntity.getHeadTypeFromItem(new ItemStack(Items.ZOMBIE_HEAD)) == zombie,
            "zombie head should resolve to zombie");
        helper.assertTrue(PikeBlockEntity.getHeadTypeFromItem(new ItemStack(Items.DIAMOND)) == null,
            "unmapped items should not resolve to an entity");
        for (Map.Entry<String, String> mapping : defaultMappings().entrySet()) {
            helper.assertTrue(mapping.getValue().equals(ConfigSettings.getEntityIdForBlock(mapping.getKey())),
                "default mapping is missing: " + mapping.getKey());
        }
        PikeTier[] tiers = PikeTier.values();
        Block[] pikeBlocks = {
            ModBlocks.woodenPike(), ModBlocks.stonePike(), ModBlocks.copperPike(), ModBlocks.ironPike(),
            ModBlocks.goldenPike(), ModBlocks.diamondPike(), ModBlocks.netheritePike()
        };
        List<Item> pikeItems = ModItems.getAllPikeItems();
        helper.assertTrue(pikeItems.size() == 8, "all seven Pike items and the Anti-Pike item should be registered");
        for (int index = 0; index < tiers.length; index++) {
            helper.assertTrue(pikeItems.get(index) instanceof PikeItem,
                "the item for " + tiers[index] + " should be a PikeItem");
            PikeItem pikeItem = (PikeItem) pikeItems.get(index);
            helper.assertTrue(pikeItem.getTier() == tiers[index] && ((PikeBlock) pikeBlocks[index]).tier() == tiers[index],
                "the Pike item and block should retain their " + tiers[index] + " tier");
            helper.assertTrue(pikeItem.getBlock() == pikeBlocks[index],
                "the Pike item should place its matching Pike block");
        }
        helper.assertTrue(AntiSpearItem.MATERIAL.durability() == 2500
                && AntiSpearItem.MATERIAL.enchantmentValue() == 15
                && AntiSpearItem.MATERIAL.repairItems().equals(
                    TagKey.create(Registries.ITEM, dataId("repairs_anti_spear"))
                ), "Anti-Spear material properties should match the declared contract");
        helper.assertTrue(new ItemStack(ModItems.grimoire()).getMaxStackSize() == 1,
            "the Grimoire should be limited to one per stack");
        helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(Effigies.id("effigies")),
            "the Effigies creative tab should be registered");
        ModCreativeTabs.effigiesTab().buildContents(new net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters(
            level.enabledFeatures(),
            false,
            level.registryAccess()
        ));
        helper.assertTrue(ModCreativeTabs.effigiesTab().contains(new ItemStack(ModBlocks.woodenPike()))
                && ModCreativeTabs.effigiesTab().contains(new ItemStack(ModItems.grimoire())),
            "the creative tab should expose Effigies content");
        helper.succeed();
    }

    public static void dataResourcesLoad(GameTestHelper helper) {
        var resourceManager = helper.getLevel().getServer().getResourceManager();
        String[] recipes = {
            "wooden_pike", "stone_pike", "stone_pike_upgrade", "copper_pike", "copper_pike_upgrade",
            "iron_pike", "iron_pike_upgrade", "golden_pike", "golden_pike_upgrade", "diamond_pike",
            "diamond_pike_upgrade", "netherite_pike", "netherite_pike_smithing", "anti_spear", "anti_pike",
            "anti_pike_upgrade", "grimoire"
        };
        for (String recipe : recipes) {
            helper.assertTrue(resourceManager.getResource(dataId("recipe/" + recipe + ".json")).isPresent(),
                "recipe resource is missing: " + recipe);
        }

        String[] lootTables = {"wooden_pike", "stone_pike", "copper_pike", "iron_pike", "golden_pike", "diamond_pike", "netherite_pike", "pike_head", "anti_pike"};
        for (String lootTable : lootTables) {
            helper.assertTrue(resourceManager.getResource(dataId("loot_table/blocks/" + lootTable + ".json")).isPresent(),
                "loot-table resource is missing: " + lootTable);
        }
        helper.assertTrue(resourceManager.getResource(dataId("tags/item/repairs_anti_spear.json")).isPresent(),
            "Anti-Spear repair tag is missing");
        helper.succeed();
    }

    public static void dataResourcesBehaveAsDeclared(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        RecipeManager recipes = level.getServer().getRecipeManager();
        String[][] recipeOutputs = {
            {"wooden_pike", "wooden_pike"},
            {"stone_pike", "stone_pike"},
            {"stone_pike_upgrade", "stone_pike"},
            {"copper_pike", "copper_pike"},
            {"copper_pike_upgrade", "copper_pike"},
            {"iron_pike", "iron_pike"},
            {"iron_pike_upgrade", "iron_pike"},
            {"golden_pike", "golden_pike"},
            {"golden_pike_upgrade", "golden_pike"},
            {"diamond_pike", "diamond_pike"},
            {"diamond_pike_upgrade", "diamond_pike"},
            {"netherite_pike", "netherite_pike"},
            {"netherite_pike_smithing", "netherite_pike"},
            {"anti_spear", "anti_spear"},
            {"anti_pike", "anti_pike"},
            {"anti_pike_upgrade", "anti_pike"},
            {"grimoire", "grimoire"}
        };
        for (String[] recipeOutput : recipeOutputs) {
            RecipeInput input = recipeOutput[0].equals("netherite_pike_smithing")
                ? new SmithingRecipeInput(
                    itemStack("minecraft:netherite_upgrade_smithing_template"),
                    itemStack("effigies:diamond_pike"),
                    itemStack("minecraft:netherite_ingot")
                )
                : recipeInputFor(recipeOutput[0]);
            ItemStack result = assembleRecipe(helper, recipes, recipeOutput[0], input);
            helper.assertTrue(result.is(item(recipeOutput[1])),
                "recipe " + recipeOutput[0] + " should produce " + recipeOutput[1]);
            helper.assertTrue(result.getCount() == 1,
                "recipe " + recipeOutput[0] + " should produce one item");
        }

        CraftingInput woodenPikeInput = CraftingInput.of(1, 3, List.of(
            itemStack("minecraft:wooden_spear"),
            new ItemStack(Items.STICK),
            new ItemStack(Items.STICK)
        ));
        Optional<RecipeHolder<CraftingRecipe>> matchingRecipe = recipes.getRecipeFor(
            RecipeType.CRAFTING,
            woodenPikeInput,
            level
        );
        helper.assertTrue(matchingRecipe.isPresent() && assembleRecipeValue(
            helper,
            matchingRecipe.get().value(),
            woodenPikeInput
        ).is(ModItems.getAllPikeItems().getFirst()),
            "the wooden Pike ingredients should match a loaded crafting recipe");

        String[][] lootOutputs = {
            {"wooden_pike", "wooden_pike"},
            {"stone_pike", "stone_pike"},
            {"copper_pike", "copper_pike"},
            {"iron_pike", "iron_pike"},
            {"golden_pike", "golden_pike"},
            {"diamond_pike", "diamond_pike"},
            {"netherite_pike", "netherite_pike"},
            {"anti_pike", "anti_pike"}
        };
        for (String[] lootOutput : lootOutputs) {
            Block block = BuiltInRegistries.BLOCK.getValue(dataId(lootOutput[0]));
            ResourceKey<LootTable> lootKey = ResourceKey.create(
                Registries.LOOT_TABLE,
                dataId("blocks/" + lootOutput[0])
            );
            LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootKey);
            LootParams lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(new BlockPos(2, 1, 2)))
                .withParameter(LootContextParams.BLOCK_STATE, block.defaultBlockState())
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .create(LootContextParamSets.BLOCK);
            List<ItemStack> drops = lootTable.getRandomItems(lootParams);
            helper.assertTrue(drops.stream().anyMatch(stack -> stack.is(item(lootOutput[1]))),
                "loot table " + lootOutput[0] + " should drop " + lootOutput[1]);
        }
        ResourceKey<LootTable> headLootKey = ResourceKey.create(
            Registries.LOOT_TABLE,
            dataId("blocks/pike_head")
        );
        LootTable headLootTable = level.getServer().reloadableRegistries().getLootTable(headLootKey);
        helper.assertTrue(headLootTable.getRandomItems(new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(new BlockPos(2, 1, 2)))
                .withParameter(LootContextParams.BLOCK_STATE, ModBlocks.pikeHead().defaultBlockState())
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .create(LootContextParamSets.BLOCK)).isEmpty(),
            "the Pike head loot table should remain empty because its stored item is handled by the block");

        TagKey<Item> repairTag = AntiSpearItem.MATERIAL.repairItems();
        helper.assertTrue(new ItemStack(Items.DRAGON_BREATH).is(repairTag),
            "Dragon's Breath should be in the Anti-Spear repair tag");
        helper.assertTrue(!new ItemStack(Items.DIAMOND).is(repairTag),
            "unlisted items should not be in the Anti-Spear repair tag");
        helper.assertTrue(new ItemStack(ModItems.antiSpear()).isValidRepairItem(new ItemStack(Items.DRAGON_BREATH)),
            "the Anti-Spear should accept items from its repair tag");
        helper.succeed();
    }

    public static void pikeRegistryRadiusAndEntityMatching(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos origin = new BlockPos(0, 1, 0);
            BlockPos stonePike = new BlockPos(32, 1, 0);
            EntityType<?> skeleton = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("skeleton"));
            EntityType<?> zombie = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("zombie"));

            try {
                PikeRegistry.registerPike(level, origin, PikeTier.WOODEN, skeleton);
                helper.assertTrue(PikeRegistry.getBlockingPike(level, new BlockPos(15, 1, 15), skeleton) != null,
                    "radius-zero pike should block within its chunk");
                helper.assertTrue(PikeRegistry.getBlockingPike(level, new BlockPos(16, 1, 0), skeleton) == null,
                    "radius-zero pike should not block an adjacent chunk");
                helper.assertTrue(PikeRegistry.getBlockingPike(level, origin, zombie) == null,
                    "pike should only block its mapped entity type");

                PikeRegistry.registerPike(level, stonePike, PikeTier.STONE, skeleton);
                helper.assertTrue(PikeRegistry.getBlockingPike(level, new BlockPos(16, 1, 0), skeleton) != null,
                    "radius-one pike should block one adjacent chunk");
                helper.assertTrue(PikeRegistry.getBlockingPike(level, new BlockPos(64, 1, 0), skeleton) == null,
                    "radius-one pike should not block two chunks away");
            } finally {
                PikeRegistry.unregisterPike(level, origin);
                PikeRegistry.unregisterPike(level, stonePike);
            }
        }
        helper.succeed();
    }

    public static void pikeRegistryCoversAllTiersAndLifecycle(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            int[] expectedRadii = {0, 1, 2, 4, 6, 8, 16};
            EntityType<?> skeleton = skeleton(level);
            EntityType<?> zombie = zombie(level);

            try {
                ConfigSettings.applySyncedValues(withDefaultPikeRadii(original));
                PikeTier[] tiers = PikeTier.values();
                helper.assertTrue(tiers.length == expectedRadii.length, "all Pike tiers should have explicit radius coverage");
                for (int index = 0; index < tiers.length; index++) {
                    BlockPos pikePos = new BlockPos(-128 + index * 64, 1, -128);
                    int pikeChunkX = pikePos.getX() >> 4;
                    int pikeChunkZ = pikePos.getZ() >> 4;
                    int radius = expectedRadii[index];
                    BlockPos inside = new BlockPos((pikeChunkX + radius) * 16, 1, pikeChunkZ * 16);
                    BlockPos outside = new BlockPos((pikeChunkX + radius + 1) * 16, 1, pikeChunkZ * 16);

                    PikeRegistry.registerPike(level, pikePos, tiers[index], skeleton);
                    helper.assertTrue(PikeRegistry.getBlockingPike(level, inside, skeleton) != null,
                        tiers[index] + " Pike should block through its configured radius");
                    helper.assertTrue(PikeRegistry.getBlockingPike(level, outside, skeleton) == null,
                        tiers[index] + " Pike should not block beyond its configured radius");
                    PikeRegistry.unregisterPike(level, pikePos);
                }

                BlockPos duplicatePos = new BlockPos(-1, 1, -1);
                PikeRegistry.registerPike(level, duplicatePos, PikeTier.WOODEN, skeleton);
                PikeRegistry.registerPike(level, duplicatePos, PikeTier.NETHERITE, zombie);
                helper.assertTrue(PikeRegistry.getBlockingPike(level, duplicatePos, skeleton) != null,
                    "duplicate registration should retain the first entity mapping");
                helper.assertTrue(PikeRegistry.getBlockingPike(level, duplicatePos, zombie) == null,
                    "duplicate registration should not append a second pike at the same position");

                PikeRegistry.updatePikeEntityType(level, duplicatePos, zombie);
                helper.assertTrue(PikeRegistry.getBlockingPike(level, duplicatePos, skeleton) == null,
                    "updating a pike should remove its old entity mapping");
                helper.assertTrue(PikeRegistry.getBlockingPike(level, duplicatePos, zombie) != null,
                    "updating a pike should apply its new entity mapping");
                PikeRegistry.unregisterPike(level, duplicatePos);

                BlockPos negativePos = new BlockPos(-1, 1, -1);
                PikeRegistry.registerPike(level, negativePos, PikeTier.WOODEN, skeleton);
                helper.assertTrue(PikeRegistry.getBlockingPike(level, negativePos, skeleton) != null,
                    "a Pike in negative coordinates should block its own chunk");
                helper.assertTrue(PikeRegistry.getBlockingPike(level, new BlockPos(-17, 1, -1), skeleton) == null,
                    "a radius-zero Pike should not cross a negative chunk boundary");
                PikeRegistry.unregisterPike(level, negativePos);

                BlockPos unloadPos = new BlockPos(64, 1, 64);
                PikeRegistry.registerPike(level, unloadPos, PikeTier.STONE, skeleton);
                PikeRegistry.onWorldUnload(level.dimension());
                helper.assertTrue(PikeRegistry.getBlockingPike(level, unloadPos, skeleton) == null,
                    "world unload should clear the dimension registry");

                PikeRegistry.registerPike(level, unloadPos, PikeTier.STONE, skeleton);
                PikeRegistry.clearAll();
                helper.assertTrue(PikeRegistry.getBlockingPike(level, unloadPos, skeleton) == null,
                    "clearAll should clear every dimension registry");
                PikeRegistry.unregisterPike(level, new BlockPos(9999, 1, 9999));
                PikeRegistry.onChunkUnload(level, 9999, 9999);
            } finally {
                PikeRegistry.clearAll();
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void pikeChunkLoadIgnoresInactiveAndInvalidHeads(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos inactivePos = new BlockPos(1024, 1, 1024);
            BlockPos invalidPos = new BlockPos(1027, 1, 1024);
            EntityType<?> skeleton = skeleton(level);

            helper.setBlock(inactivePos.below(), Blocks.STONE);
            helper.setBlock(inactivePos, ModBlocks.woodenPike());
            LevelChunk inactiveChunk = level.getChunkAt(helper.absolutePos(inactivePos));
            PikeRegistry.clearAll();
            PikeRegistry.onChunkLoad(level, inactiveChunk);
            helper.assertTrue(PikeRegistry.getBlockingPike(level, helper.absolutePos(inactivePos), skeleton) == null,
                "chunk loading should ignore inactive Pikes");

            helper.setBlock(invalidPos.below(), Blocks.STONE);
            helper.setBlock(invalidPos, ModBlocks.woodenPike());
            helper.setBlock(invalidPos.above(), ModBlocks.pikeHead());
            PikeHeadBlockEntity head = helper.getBlockEntity(invalidPos.above(), PikeHeadBlockEntity.class);
            head.setStoredHead("minecraft:diamond", 0);
            BlockPos absoluteInvalidPos = helper.absolutePos(invalidPos);
            level.setBlock(
                absoluteInvalidPos,
                ModBlocks.woodenPike().defaultBlockState().setValue(PikeBlock.ACTIVATED, true),
                Block.UPDATE_ALL
            );
            LevelChunk invalidChunk = level.getChunkAt(absoluteInvalidPos);
            PikeRegistry.clearAll();
            PikeRegistry.onChunkLoad(level, invalidChunk);
            helper.assertTrue(PikeRegistry.getBlockingPike(level, absoluteInvalidPos, skeleton) == null,
                "chunk loading should ignore Pikes with invalid stored heads");
            PikeRegistry.clearAll();
        }
        helper.succeed();
    }

    public static void pikeRegistryHonorsDisabledRadius(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos pikePos = new BlockPos(48, 1, 0);
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload disabled = withConfig(original, -1, original.dimensionWhitelist());
            EntityType<?> skeleton = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("skeleton"));

            try {
                ConfigSettings.applySyncedValues(disabled);
                PikeRegistry.registerPike(level, pikePos, PikeTier.WOODEN, skeleton);
                helper.assertTrue(PikeRegistry.getBlockingPike(level, pikePos, skeleton) == null,
                    "a radius-minus-one pike should be disabled");
            } finally {
                PikeRegistry.unregisterPike(level, pikePos);
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void pikeActivationRegistersStoredHead(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos pikePos = new BlockPos(2, 1, 2);
            EntityType<?> skeleton = BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("skeleton"));

            placeActivePike(helper, pikePos);
            helper.assertTrue(helper.getBlockState(pikePos).getValue(PikeBlock.ACTIVATED),
                "placing a mapped head should activate the pike");
            helper.assertTrue(PikeRegistry.getBlockingPike(level, helper.absolutePos(pikePos), skeleton) != null,
                "an activated pike should register its mapped entity type");
            PikeRegistry.unregisterPike(level, helper.absolutePos(pikePos));
        }
        helper.succeed();
    }

    public static void pikeHeadRemovalDeactivatesAndDropsStoredItem(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos pikePos = new BlockPos(2, 1, 2);
            BlockPos headPos = pikePos.above();
            PikeHeadBlockEntity head = placeActivePike(helper, pikePos);

            Player player = helper.makeMockServerPlayerInLevel();
            player.setShiftKeyDown(true);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIRT));
            helper.useBlock(pikePos, player);

            helper.assertTrue(!helper.getBlockState(pikePos).getValue(PikeBlock.ACTIVATED),
                "removing a head should deactivate the pike");
            helper.assertBlockNotPresent(ModBlocks.pikeHead(), headPos);
            helper.assertTrue(PikeRegistry.getBlockingPike(level, helper.absolutePos(pikePos), skeleton(level)) == null,
                "removing a head should unregister the pike");
            helper.assertTrue(head.getStoredItemStack().isEmpty(),
                "removing a head should clear its stored item before dropping it");
            helper.runAfterDelay(5, () -> {
                assertItemEntityNear(helper, level, Items.SKELETON_SKULL, helper.absolutePos(headPos));
                helper.succeed();
            });
            return;
        }
    }

    public static void pikePlacementAndBreakingDropsHead(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos pikePos = new BlockPos(2, 1, 2);
            BlockPos headPos = pikePos.above();
            BlockPos absolutePikePos = helper.absolutePos(pikePos);
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            helper.setBlock(pikePos.below(), Blocks.STONE);
            ItemStack pikeStack = new ItemStack(ModBlocks.woodenPike().asItem());
            player.setItemInHand(InteractionHand.MAIN_HAND, pikeStack);
            helper.placeAt(player, pikeStack, pikePos.below(), Direction.UP);
            helper.assertBlockPresent(ModBlocks.woodenPike(), pikePos);
            helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
                "placing a Pike should consume its block item");

            ItemStack headStack = new ItemStack(Items.SKELETON_SKULL);
            player.setItemInHand(InteractionHand.MAIN_HAND, headStack);
            helper.useBlock(pikePos, player);
            helper.assertBlockPresent(ModBlocks.pikeHead(), headPos);
            helper.assertTrue(helper.getBlockState(pikePos).getValue(PikeBlock.ACTIVATED),
                "placing a valid head through the interaction path should activate the Pike");
            helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
                "placing a head should consume its item");
            helper.assertTrue(PikeRegistry.getBlockingPike(level, absolutePikePos, skeleton(level)) != null,
                "an interacted active Pike should register");

            destroyBlockWithDrops(helper, pikePos, player);
            helper.assertBlockNotPresent(ModBlocks.woodenPike(), pikePos);
            helper.assertBlockNotPresent(ModBlocks.pikeHead(), headPos);
            helper.assertTrue(PikeRegistry.getBlockingPike(level, absolutePikePos, skeleton(level)) == null,
                "breaking an active Pike should unregister it");
            helper.runAfterDelay(5, () -> {
                helper.assertItemEntityPresent(Items.SKELETON_SKULL, headPos, 2.0D);
                helper.assertItemEntityPresent(ModBlocks.woodenPike().asItem(), pikePos, 2.0D);
                helper.succeed();
            });
            return;
        }
    }

    public static void antiPikePlacementAndBreakingDropsHead(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            BlockPos antiPikePos = new BlockPos(2, 1, 2);
            BlockPos headPos = antiPikePos.above();
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            helper.setBlock(antiPikePos.below(), Blocks.STONE);
            ItemStack antiPikeStack = new ItemStack(ModItems.antiPike());
            player.setItemInHand(InteractionHand.MAIN_HAND, antiPikeStack);
            helper.placeAt(player, antiPikeStack, antiPikePos.below(), Direction.UP);
            helper.assertBlockPresent(ModBlocks.antiPike(), antiPikePos);
            helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
                "placing an Anti-Pike should consume its block item");

            ItemStack headStack = new ItemStack(Items.SKELETON_SKULL);
            player.setItemInHand(InteractionHand.MAIN_HAND, headStack);
            helper.useBlock(antiPikePos, player);
            helper.assertBlockPresent(ModBlocks.pikeHead(), headPos);
            helper.assertTrue(helper.getBlockState(antiPikePos).getValue(AntiPikeBlock.ACTIVATED),
                "placing a valid head through the interaction path should activate the Anti-Pike");
            helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
                "placing a head on an Anti-Pike should consume its item");

            destroyBlockWithDrops(helper, antiPikePos, player);
            helper.assertBlockNotPresent(ModBlocks.antiPike(), antiPikePos);
            helper.assertBlockNotPresent(ModBlocks.pikeHead(), headPos);
            helper.runAfterDelay(5, () -> {
                helper.assertItemEntityPresent(Items.SKELETON_SKULL, headPos, 2.0D);
                helper.assertItemEntityPresent(ModItems.antiPike(), antiPikePos, 2.0D);
                helper.succeed();
            });
            return;
        }
    }

    public static void pikeHeadBreakingDropsStoredItem(GameTestHelper helper) {
        BlockPos headPos = new BlockPos(2, 1, 2);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.setBlock(headPos, ModBlocks.pikeHead());
        PikeHeadBlockEntity head = helper.getBlockEntity(headPos, PikeHeadBlockEntity.class);
        head.setStoredHead("minecraft:skeleton_skull", 0);

        destroyBlockWithDrops(helper, headPos, player);
        helper.assertBlockNotPresent(ModBlocks.pikeHead(), headPos);
        helper.assertTrue(head.getStoredItemStack().isEmpty(),
            "breaking a Pike head should clear its stored item before dropping it");
        helper.runAfterDelay(5, () -> {
            assertItemEntityNear(helper, helper.getLevel(), Items.SKELETON_SKULL, helper.absolutePos(headPos));
            helper.succeed();
        });
    }

    public static void pikeChunkUnloadAndReloadRehydratesRegistry(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos pikePos = new BlockPos(2, 1, 2);
            placeActivePike(helper, pikePos);
            BlockPos absolutePikePos = helper.absolutePos(pikePos);
            LevelChunk chunk = level.getChunkAt(absolutePikePos);
            PikeRegistry.unregisterPike(level, absolutePikePos);

            PikeRegistry.onChunkLoad(level, chunk);
            helper.assertTrue(PikeRegistry.getBlockingPike(level, absolutePikePos, skeleton(level)) != null,
                "loading an active pike chunk should rebuild its registry entry");
            PikeRegistry.onChunkUnload(level, absolutePikePos.getX() >> 4, absolutePikePos.getZ() >> 4);
            helper.assertTrue(PikeRegistry.getBlockingPike(level, absolutePikePos, skeleton(level)) == null,
                "unloading a pike chunk should clear its registry entry");
            PikeRegistry.onChunkLoad(level, chunk);
            helper.assertTrue(PikeRegistry.getBlockingPike(level, absolutePikePos, skeleton(level)) != null,
                "reloading a pike chunk should restore its registry entry");
            PikeRegistry.unregisterPike(level, absolutePikePos);
        }
        helper.succeed();
    }

    public static void dimensionWhitelistAcceptsOnlyConfiguredDimension(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            String currentDimension = level.dimension().identifier().toString();
            ConfigSyncPayload restricted = withConfig(original, original.woodenPikeRadius(), List.of("minecraft:the_nether"));

            try {
                ConfigSettings.applySyncedValues(restricted);
                helper.assertTrue(!ConfigSettings.isDimensionAllowed(currentDimension),
                    "a non-whitelisted dimension should be rejected");
                helper.assertTrue(ConfigSettings.isDimensionAllowed("minecraft:the_nether"),
                    "a whitelisted dimension should be accepted");
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void antiPikeSpawnsWithNearbyPlayerAndBypassesPike(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos antiPikePos = new BlockPos(3, 1, 3);
            BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
            BlockPos pikePos = antiPikePos.offset(1, 0, 0);
            BlockPos absolutePikePos = helper.absolutePos(pikePos);
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload fastAntiPike = new ConfigSyncPayload(
                original.giveGrimoireOnJoin(),
                original.woodenPikeRadius(),
                original.stonePikeRadius(),
                original.copperPikeRadius(),
                original.ironPikeRadius(),
                original.goldenPikeRadius(),
                original.diamondPikeRadius(),
                original.netheritePikeRadius(),
                1,
                2,
                6,
                2,
                16,
                original.blockEntityMappings(),
                original.dimensionWhitelist()
            );
            ConfigSyncPayload configured = withConfig(fastAntiPike, 0, fastAntiPike.dimensionWhitelist());

            try {
                ConfigSettings.applySyncedValues(configured);
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        if (x != 0 || z != 0) {
                            helper.setBlock(antiPikePos.offset(x, -1, z), Blocks.STONE);
                        }
                    }
                }
                helper.setBlock(antiPikePos, ModBlocks.antiPike());
                helper.setBlock(antiPikePos.above(), ModBlocks.pikeHead());
                PikeHeadBlockEntity head = helper.getBlockEntity(antiPikePos.above(), PikeHeadBlockEntity.class);
                head.setStoredHead("minecraft:skeleton_skull", 0);
                AntiPikeBlock antiPike = (AntiPikeBlock) ModBlocks.antiPike();
                level.setBlock(absoluteAntiPikePos, ModBlocks.antiPike().defaultBlockState(), Block.UPDATE_ALL);
                antiPike.setPlacedBy(level, absoluteAntiPikePos, level.getBlockState(absoluteAntiPikePos), null, ItemStack.EMPTY);
                helper.assertTrue(helper.getBlockState(antiPikePos).getValue(AntiPikeBlock.ACTIVATED),
                    "placing a mapped head should activate the anti-pike");
                helper.assertTrue(PikeBlockEntity.getHeadTypeFromWorld(level, absoluteAntiPikePos.above()) == skeleton(level),
                    "the anti-pike head should resolve to skeleton");

                Player player = helper.makeMockServerPlayerInLevel();
                player.teleportTo(absoluteAntiPikePos.getX() + 0.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);
                helper.assertTrue(level.hasNearbyAlivePlayer(
                    absoluteAntiPikePos.getX() + 0.5,
                    absoluteAntiPikePos.getY() + 0.5,
                    absoluteAntiPikePos.getZ() + 0.5,
                    ConfigSettings.antiPikeActivationRange
                ), "the mock player should be inside the anti-pike activation range");
                placeActivePike(helper, pikePos, (PikeBlock) ModBlocks.stonePike());
                AntiPikeBlockEntity blockEntity = helper.getBlockEntity(antiPikePos, AntiPikeBlockEntity.class);
                BlockState state = level.getBlockState(absoluteAntiPikePos);
                for (int tick = 0; tick < 20; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                AABB entityBounds = antiPikeBounds(absoluteAntiPikePos, configured.antiPikeSpawnRange());
                int antiPikeSpawnCount = countEntities(level, skeleton(level), entityBounds);
                helper.assertTrue(antiPikeSpawnCount > 0,
                    "the Anti-Pike should bypass a matching Pike when it spawns a mob");

                helper.assertTrue(PikeRegistry.getBlockingPike(
                    level,
                    absoluteAntiPikePos.offset(1, 0, 1),
                    skeleton(level)
                ) != null, "the matching Pike should still block the ordinary spawn position");
                Entity ordinarySpawn = skeleton(level).spawn(
                    level,
                    absoluteAntiPikePos.offset(1, 0, 1),
                    EntitySpawnReason.SPAWNER
                );
                helper.assertTrue(countEntities(level, skeleton(level), entityBounds) == antiPikeSpawnCount,
                    "the Anti-Pike bypass should be cleared after its spawn attempt");
            } finally {
                PikeRegistry.unregisterPike(level, absolutePikePos);
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void antiPikeHonorsActivationRangeAndSpawnDelay(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos antiPikePos = new BlockPos(3, 1, 3);
            BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload configured = withAntiPikeConfig(original, 2, 4, 6, 1, 1);

            try {
                ConfigSettings.applySyncedValues(configured);
                AntiPikeBlockEntity blockEntity = placeActiveAntiPike(helper, antiPikePos, 2);
                ServerPlayer player = helper.makeMockServerPlayerInLevel();
                player.teleportTo(absoluteAntiPikePos.getX() + 3.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);
                BlockState state = level.getBlockState(absoluteAntiPikePos);
                AABB entityBounds = antiPikeBounds(absoluteAntiPikePos, configured.antiPikeSpawnRange());

                for (int tick = 0; tick < 4; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                helper.assertTrue(countEntities(level, skeleton(level), entityBounds) == 0,
                    "an Anti-Pike outside its activation range should not spawn");

                player.teleportTo(absoluteAntiPikePos.getX() + 0.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);
                for (int tick = 0; tick < 3; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                helper.assertTrue(countEntities(level, skeleton(level), entityBounds) == 0,
                    "an Anti-Pike should wait for its configured minimum spawn delay");

                for (int tick = 0; tick < 7 && countEntities(level, skeleton(level), entityBounds) == 0; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                helper.assertTrue(countEntities(level, skeleton(level), entityBounds) > 0,
                    "an active Anti-Pike should spawn after its configured delay");
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void antiPikeHonorsNearbyEntityLimit(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos antiPikePos = new BlockPos(3, 1, 3);
            BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload configured = withAntiPikeConfig(original, 1, 2, 1, 2, 16);

            try {
                ConfigSettings.applySyncedValues(configured);
                AntiPikeBlockEntity blockEntity = placeActiveAntiPike(helper, antiPikePos, 2);
                ServerPlayer player = helper.makeMockServerPlayerInLevel();
                player.teleportTo(absoluteAntiPikePos.getX() + 0.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);
                Entity existingMob = skeleton(level).spawn(
                    level,
                    absoluteAntiPikePos.offset(1, 0, 0),
                    EntitySpawnReason.SPAWNER
                );
                helper.assertTrue(existingMob != null, "the nearby mob used for the density cap should spawn");
                BlockState state = level.getBlockState(absoluteAntiPikePos);
                AABB entityBounds = antiPikeBounds(absoluteAntiPikePos, configured.antiPikeSpawnRange());

                for (int tick = 0; tick < 6; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                helper.assertTrue(countEntities(level, skeleton(level), entityBounds) == 1,
                    "the Anti-Pike should pause when its nearby-entity limit is reached");

                existingMob.discard();
                for (int tick = 0; tick < 8 && countEntities(level, skeleton(level), entityBounds) == 0; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                helper.assertTrue(countEntities(level, skeleton(level), entityBounds) > 0,
                    "the Anti-Pike should resume after the nearby-entity limit is cleared");
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void antiPikeHonorsSpawnRange(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos antiPikePos = new BlockPos(3, 1, 3);
            BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload configured = withAntiPikeConfig(original, 1, 2, 6, 2, 16);

            try {
                ConfigSettings.applySyncedValues(configured);
                AntiPikeBlockEntity blockEntity = placeActiveAntiPike(helper, antiPikePos, 4);
                ServerPlayer player = helper.makeMockServerPlayerInLevel();
                player.teleportTo(absoluteAntiPikePos.getX() + 0.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);
                BlockState state = level.getBlockState(absoluteAntiPikePos);
                AABB entityBounds = antiPikeBounds(absoluteAntiPikePos, configured.antiPikeSpawnRange());

                for (int tick = 0; tick < 30 && countEntities(level, skeleton(level), entityBounds) == 0; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                List<? extends Entity> spawnedMobs = level.getEntities(
                    skeleton(level),
                    entityBounds,
                    entity -> true
                );
                helper.assertTrue(!spawnedMobs.isEmpty(), "the Anti-Pike should spawn a mob inside its configured range");
                for (Entity entity : spawnedMobs) {
                    helper.assertTrue(entity.getX() >= absoluteAntiPikePos.getX() - configured.antiPikeSpawnRange()
                            && entity.getX() < absoluteAntiPikePos.getX() + configured.antiPikeSpawnRange() + 1
                            && entity.getZ() >= absoluteAntiPikePos.getZ() - configured.antiPikeSpawnRange()
                            && entity.getZ() < absoluteAntiPikePos.getZ() + configured.antiPikeSpawnRange() + 1,
                        "the Anti-Pike spawned a mob outside its configured range");
                }
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void antiPikeSkipsInactiveAndMissingHead(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos antiPikePos = new BlockPos(3, 1, 3);
            BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload configured = withAntiPikeConfig(original, 1, 1, 6, 2, 16);

            try {
                ConfigSettings.applySyncedValues(configured);
                helper.setBlock(antiPikePos.below(), Blocks.STONE);
                helper.setBlock(antiPikePos, ModBlocks.antiPike());
                AntiPikeBlockEntity blockEntity = helper.getBlockEntity(antiPikePos, AntiPikeBlockEntity.class);
                ServerPlayer player = helper.makeMockServerPlayerInLevel();
                player.teleportTo(absoluteAntiPikePos.getX() + 0.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);
                BlockState inactiveState = level.getBlockState(absoluteAntiPikePos);
                helper.assertTrue(((AntiPikeBlock) ModBlocks.antiPike()).getTicker(
                    level, inactiveState, ModBlockEntities.antiPike()) != null,
                    "an Anti-Pike should expose a server ticker");

                blockEntity.serverTick(level, absoluteAntiPikePos, inactiveState);
                helper.assertTrue(countEntities(level, skeleton(level), antiPikeBounds(absoluteAntiPikePos, 2)) == 0,
                    "an inactive Anti-Pike should not spawn");

                level.setBlock(
                    absoluteAntiPikePos,
                    inactiveState.setValue(AntiPikeBlock.ACTIVATED, true),
                    Block.UPDATE_ALL
                );
                blockEntity.serverTick(level, absoluteAntiPikePos, level.getBlockState(absoluteAntiPikePos));
                helper.assertTrue(countEntities(level, skeleton(level), antiPikeBounds(absoluteAntiPikePos, 2)) == 0,
                    "an Anti-Pike without a valid head should not spawn");
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void antiPikeRejectsInvalidSpawnLocationsAndEqualDelay(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos antiPikePos = new BlockPos(3, 1, 3);
            BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload configured = withAntiPikeConfig(original, 1, 1, 6, 2, 16);

            try {
                ConfigSettings.applySyncedValues(configured);
                AntiPikeBlockEntity blockEntity = placeActiveAntiPike(helper, antiPikePos, 2);
                ServerPlayer player = helper.makeMockServerPlayerInLevel();
                player.teleportTo(absoluteAntiPikePos.getX() + 0.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);

                for (int x = -2; x <= 2; x++) {
                    for (int y = 0; y <= 1; y++) {
                        for (int z = -2; z <= 2; z++) {
                            helper.setBlock(antiPikePos.offset(x, y, z), Blocks.STONE);
                        }
                    }
                }
                helper.setBlock(antiPikePos, ModBlocks.antiPike());
                helper.setBlock(antiPikePos.above(), ModBlocks.pikeHead());
                PikeHeadBlockEntity head = helper.getBlockEntity(antiPikePos.above(), PikeHeadBlockEntity.class);
                head.setStoredHead("minecraft:skeleton_skull", 0);
                AntiPikeBlock antiPike = (AntiPikeBlock) ModBlocks.antiPike();
                level.setBlock(absoluteAntiPikePos, antiPike.defaultBlockState(), Block.UPDATE_ALL);
                antiPike.setPlacedBy(level, absoluteAntiPikePos, level.getBlockState(absoluteAntiPikePos), null, ItemStack.EMPTY);
                blockEntity = helper.getBlockEntity(antiPikePos, AntiPikeBlockEntity.class);
                BlockState state = level.getBlockState(absoluteAntiPikePos);

                for (int tick = 0; tick < 4; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                helper.assertTrue(countEntities(level, skeleton(level), antiPikeBounds(absoluteAntiPikePos, 2)) == 0,
                    "an Anti-Pike should skip solid and unsupported spawn locations");

                for (int x = -2; x <= 2; x++) {
                    for (int y = 0; y <= 1; y++) {
                        for (int z = -2; z <= 2; z++) {
                        if (x != 0 || z != 0) {
                                helper.setBlock(antiPikePos.offset(x, y, z), Blocks.AIR);
                            }
                        }
                    }
                }
                for (int tick = 0; tick < 30 && countEntities(level, skeleton(level), antiPikeBounds(absoluteAntiPikePos, 2)) == 0; tick++) {
                    blockEntity.serverTick(level, absoluteAntiPikePos, state);
                }
                helper.assertTrue(countEntities(level, skeleton(level), antiPikeBounds(absoluteAntiPikePos, 2)) > 0,
                    "an Anti-Pike with equal delay bounds should resume after valid locations return");
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void antiPikeHeadRemovalDeactivatesAndDropsStoredItem(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos antiPikePos = new BlockPos(2, 1, 2);
            BlockPos headPos = antiPikePos.above();
            placeActiveAntiPike(helper, antiPikePos, 2);
            PikeHeadBlockEntity head = helper.getBlockEntity(headPos, PikeHeadBlockEntity.class);

            Player player = helper.makeMockServerPlayerInLevel();
            player.setShiftKeyDown(true);
            helper.useBlock(antiPikePos, player);

            helper.assertTrue(!helper.getBlockState(antiPikePos).getValue(AntiPikeBlock.ACTIVATED),
                "removing an Anti-Pike head should deactivate the Anti-Pike");
            helper.assertBlockNotPresent(ModBlocks.pikeHead(), headPos);
            helper.assertTrue(head.getStoredItemStack().isEmpty(),
                "removing an Anti-Pike head should clear its stored item before dropping it");
            helper.runAfterDelay(5, () -> {
                assertItemEntityNear(helper, level, Items.SKELETON_SKULL, helper.absolutePos(headPos));
                helper.succeed();
            });
            return;
        }
    }

    public static void pikeHeadStoresAndResolvesOriginalBlock(GameTestHelper helper) {
        BlockPos headPos = new BlockPos(2, 1, 2);
        helper.setBlock(headPos, ModBlocks.pikeHead());
        PikeHeadBlockEntity head = helper.getBlockEntity(headPos, PikeHeadBlockEntity.class);
        head.setStoredHead("minecraft:skeleton_skull", 7);

        helper.assertTrue("minecraft:skeleton_skull".equals(head.getStoredBlockId()), "stored head id was not retained");
        helper.assertTrue(head.getRotation() == 7, "stored head rotation was not retained");
        helper.assertTrue(head.getStoredBlockState() != null, "stored head state should resolve");
        helper.assertTrue(head.getStoredItemStack().is(Items.SKELETON_SKULL), "stored item should be the original skull");
        helper.succeed();
    }

    public static void blockEntityStateRoundTripsThroughPersistence(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            BlockPos headPos = new BlockPos(2, 1, 2);
            helper.setBlock(headPos, ModBlocks.pikeHead());
            PikeHeadBlockEntity head = helper.getBlockEntity(headPos, PikeHeadBlockEntity.class);
            head.setStoredHead("minecraft:skeleton_skull", 7);

            CompoundTag headTag = head.saveWithFullMetadata(level.registryAccess());
            BlockEntity restoredHead = BlockEntity.loadStatic(
                helper.absolutePos(headPos),
                ModBlocks.pikeHead().defaultBlockState(),
                headTag,
                level.registryAccess()
            );
            helper.assertTrue(restoredHead instanceof PikeHeadBlockEntity,
                "a persisted Pike head should restore its block entity type");
            PikeHeadBlockEntity restoredPikeHead = (PikeHeadBlockEntity) restoredHead;
            helper.assertTrue(restoredPikeHead.getStoredBlockId().equals("minecraft:skeleton_skull")
                    && restoredPikeHead.getRotation() == 7,
                "a persisted Pike head should restore its stored block and rotation");
            helper.assertTrue(restoredPikeHead.getStoredItemStack().is(Items.SKELETON_SKULL),
                "a persisted Pike head should restore its stored item");
            helper.assertTrue(headTag.equals(restoredPikeHead.saveWithFullMetadata(level.registryAccess())),
                "a Pike head should serialize identically after restoration");

            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            try {
                ConfigSettings.applySyncedValues(withAntiPikeConfig(original, 7, 8, 6, 2, 16));
                BlockPos antiPikePos = new BlockPos(5, 1, 5);
                AntiPikeBlockEntity antiPike = placeActiveAntiPike(helper, antiPikePos, 2);
                ServerPlayer player = helper.makeMockServerPlayerInLevel();
                BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
                player.teleportTo(absoluteAntiPikePos.getX() + 0.5, absoluteAntiPikePos.getY() + 1.0, absoluteAntiPikePos.getZ() + 0.5);
                BlockState state = level.getBlockState(absoluteAntiPikePos);
                antiPike.serverTick(level, absoluteAntiPikePos, state);

                CompoundTag antiPikeTag = antiPike.saveWithFullMetadata(level.registryAccess());
                helper.assertTrue(antiPikeTag.contains("SpawnDelay"),
                    "an Anti-Pike should persist its spawn delay");
                BlockEntity restoredAntiPike = BlockEntity.loadStatic(
                    absoluteAntiPikePos,
                    ModBlocks.antiPike().defaultBlockState(),
                    antiPikeTag,
                    level.registryAccess()
                );
                helper.assertTrue(restoredAntiPike instanceof AntiPikeBlockEntity,
                    "a persisted Anti-Pike should restore its block entity type");
                helper.assertTrue(antiPikeTag.equals(restoredAntiPike.saveWithFullMetadata(level.registryAccess())),
                    "an Anti-Pike should serialize identically after restoration");
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void configSyncAppliesAndRestoresValues(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            ConfigSyncPayload replacement = sentinelConfig(original);

            try {
                ConfigSettings.applySyncedValues(replacement);
                assertAppliedConfig(helper, replacement);
                helper.assertTrue(ConfigSettings.getMaxPikeRadius() == 17,
                    "the maximum Pike radius should include the Netherite value");
            } finally {
                ConfigSettings.applySyncedValues(original);
            }

            assertAppliedConfig(helper, original);
        }
        helper.succeed();
    }

    public static void configSyncRoundTripsThroughCodec(GameTestHelper helper) {
        ConfigSyncPayload expected = sentinelConfig(ConfigSyncPayload.fromCurrentConfig());
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(
            Unpooled.buffer(),
            helper.getLevel().registryAccess()
        );
        try {
            ConfigSyncPayload.STREAM_CODEC.encode(buffer, expected);
            ConfigSyncPayload decoded = ConfigSyncPayload.STREAM_CODEC.decode(buffer);
            helper.assertTrue(expected.equals(decoded), "config sync payload did not survive codec round-trip");
        } finally {
            buffer.release();
        }
        helper.succeed();
    }

    public static void configFileLoadsNewSettingsAndRegeneratesOnVersionMismatch(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            Path configPath = InitializeConfig.getConfigFilePath();
            List<String> originalLines = null;
            try {
                originalLines = Files.readAllLines(configPath);
                Files.write(configPath, List.of(
                    "configVersion=" + DefaultSettings.CONFIG_VERSION,
                    "give_grimoire_on_join=false",
                    "wooden_pike_radius=11",
                    "stone_pike_radius=12",
                    "copper_pike_radius=13",
                    "iron_pike_radius=14",
                    "golden_pike_radius=15",
                    "diamond_pike_radius=16",
                    "netherite_pike_radius=17",
                    "anti_pike_min_spawn_delay=21",
                    "anti_pike_max_spawn_delay=22",
                    "anti_pike_max_nearby_entities=23",
                    "anti_pike_spawn_range=24",
                    "anti_pike_activation_range=25",
                    "dimension_whitelist=minecraft:the_nether",
                    "minecraft:skeleton_skull=minecraft:zombie",
                    "minecraft:zombie_head=minecraft:zombie"
                ), StandardOpenOption.TRUNCATE_EXISTING);
                ConfigSettings.reload();
                helper.assertTrue(ConfigSettings.configVersion == DefaultSettings.CONFIG_VERSION,
                    "config version was not parsed");
                helper.assertTrue(!ConfigSettings.giveGrimoireOnJoin, "grimoire setting was not parsed");
                helper.assertTrue(ConfigSettings.woodenPikeRadius == 11 && ConfigSettings.stonePikeRadius == 12
                        && ConfigSettings.copperPikeRadius == 13 && ConfigSettings.ironPikeRadius == 14
                        && ConfigSettings.goldenPikeRadius == 15 && ConfigSettings.diamondPikeRadius == 16
                        && ConfigSettings.netheritePikeRadius == 17,
                    "pike radius settings were not parsed");
                helper.assertTrue(ConfigSettings.antiPikeMinSpawnDelay == 21
                        && ConfigSettings.antiPikeMaxSpawnDelay == 22
                        && ConfigSettings.antiPikeMaxNearbyEntities == 23
                        && ConfigSettings.antiPikeSpawnRange == 24
                        && ConfigSettings.antiPikeActivationRange == 25,
                    "anti-pike settings were not parsed");
                helper.assertTrue(ConfigSettings.getDimensionWhitelist().equals(List.of("minecraft:the_nether")),
                    "dimension whitelist was not parsed");
                helper.assertTrue(ConfigSettings.getBlockToEntityMappings().equals(Map.of(
                    "minecraft:skeleton_skull", "minecraft:zombie",
                    "minecraft:zombie_head", "minecraft:zombie"
                )), "block-to-entity mappings were not parsed");

                Files.write(configPath, List.of(
                    "configVersion=" + DefaultSettings.CONFIG_VERSION,
                    "give_grimoire_on_join=false",
                    "wooden_pike_radius=-2",
                    "anti_pike_min_spawn_delay=0",
                    "anti_pike_max_spawn_delay=-1",
                    "anti_pike_max_nearby_entities=invalid",
                    "anti_pike_spawn_range=0",
                    "anti_pike_activation_range=-2"
                ), StandardOpenOption.TRUNCATE_EXISTING);
                ConfigSettings.reload();
                helper.assertTrue(ConfigSettings.woodenPikeRadius == DefaultSettings.WOODEN_PIKE_RADIUS,
                    "invalid pike radius should fall back to its default");
                helper.assertTrue(ConfigSettings.antiPikeMinSpawnDelay == DefaultSettings.ANTI_PIKE_MIN_SPAWN_DELAY
                        && ConfigSettings.antiPikeMaxSpawnDelay == DefaultSettings.ANTI_PIKE_MAX_SPAWN_DELAY
                        && ConfigSettings.antiPikeMaxNearbyEntities == DefaultSettings.ANTI_PIKE_MAX_NEARBY_ENTITIES
                        && ConfigSettings.antiPikeSpawnRange == DefaultSettings.ANTI_PIKE_SPAWN_RANGE
                        && ConfigSettings.antiPikeActivationRange == DefaultSettings.ANTI_PIKE_ACTIVATION_RANGE,
                    "invalid anti-pike settings should fall back to their defaults");

                Files.write(configPath, List.of(
                    "configVersion=" + DefaultSettings.CONFIG_VERSION,
                    "anti_pike_min_spawn_delay=10",
                    "anti_pike_max_spawn_delay=9"
                ), StandardOpenOption.TRUNCATE_EXISTING);
                ConfigSettings.reload();
                helper.assertTrue(ConfigSettings.antiPikeMinSpawnDelay == 10
                        && ConfigSettings.antiPikeMaxSpawnDelay == 9,
                    "positive Anti-Pike delay bounds should be retained for runtime guard handling");

                Files.write(configPath, List.of(
                    "configVersion=" + DefaultSettings.CONFIG_VERSION,
                    "badblock=minecraft:zombie",
                    "minecraft:skeleton_skull=badentity",
                    "minecraft:zombie_head=minecraft:zombie:extra",
                    "minecraft:skeleton_skull=minecraft:zombie",
                    "minecraft:zombie_head=minecraft:zombie"
                ), StandardOpenOption.TRUNCATE_EXISTING);
                ConfigSettings.reload();
                helper.assertTrue(ConfigSettings.getBlockToEntityMappings().equals(Map.of(
                    "minecraft:skeleton_skull", "minecraft:zombie",
                    "minecraft:zombie_head", "minecraft:zombie"
                )), "invalid mappings should be ignored and valid mappings should replace then extend defaults");

                Files.write(configPath, List.of(
                    "configVersion=" + DefaultSettings.CONFIG_VERSION,
                    "dimension_whitelist="
                ), StandardOpenOption.TRUNCATE_EXISTING);
                ConfigSettings.reload();
                helper.assertTrue(ConfigSettings.getDimensionWhitelist().isEmpty()
                        && ConfigSettings.isDimensionAllowed("minecraft:overworld"),
                    "an empty dimension whitelist should allow every dimension");
                helper.assertTrue(ConfigSettings.getBlockToEntityMappings().equals(defaultMappings()),
                    "missing mappings should restore all default mappings");
                helper.assertTrue(ConfigSettings.giveGrimoireOnJoin == DefaultSettings.GIVE_GRIMOIRE_ON_JOIN
                        && ConfigSettings.woodenPikeRadius == DefaultSettings.WOODEN_PIKE_RADIUS
                        && ConfigSettings.netheritePikeRadius == DefaultSettings.NETHERITE_PIKE_RADIUS,
                    "missing config keys should restore their defaults");

                List<String> regeneratedLines = null;
                for (String versionLine : List.of("configVersion=2", "configVersion=invalid", "give_grimoire_on_join=true")) {
                    Files.write(configPath, List.of(versionLine), StandardOpenOption.TRUNCATE_EXISTING);
                    InitializeConfig.generateConfigFile();
                    regeneratedLines = Files.readAllLines(configPath);
                    helper.assertTrue(regeneratedLines.contains("configVersion=" + DefaultSettings.CONFIG_VERSION),
                        "a missing or invalid config version should regenerate the current template");
                }
                String[] generatedKeys = {
                    "give_grimoire_on_join", "wooden_pike_radius", "stone_pike_radius", "copper_pike_radius",
                    "iron_pike_radius", "golden_pike_radius", "diamond_pike_radius", "netherite_pike_radius",
                    "anti_pike_min_spawn_delay", "anti_pike_max_spawn_delay", "anti_pike_max_nearby_entities",
                    "anti_pike_spawn_range", "anti_pike_activation_range", "dimension_whitelist"
                };
                for (String key : generatedKeys) {
                    helper.assertTrue(regeneratedLines.stream().anyMatch(line -> line.startsWith(key + "=")),
                        "regenerated config is missing " + key);
                }
            } catch (IOException exception) {
                throw new AssertionError("config file test could not access the isolated test config", exception);
            } finally {
                if (originalLines != null) {
                    try {
                        Files.write(configPath, originalLines, StandardOpenOption.TRUNCATE_EXISTING);
                        ConfigSettings.reload();
                    } catch (IOException exception) {
                        throw new AssertionError("config file test could not restore the isolated test config", exception);
                    }
                }
            }
        }
        helper.succeed();
    }

    public static void grimoireOnJoinHonorsConfigAndTracksFirstJoin(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();

            try {
                ConfigSettings.applySyncedValues(withGrimoireSetting(original, true));
                ServerPlayer joinedPlayer = helper.makeMockServerPlayerInLevel();
                helper.assertTrue(countInventoryItem(joinedPlayer, ModItems.grimoire()) == 1,
                    "an enabled first join should give one grimoire");
                helper.assertTrue(!GrimoireTracker.giveGrimoireIfNeeded(level.getServer(), joinedPlayer),
                    "a player should not receive a second grimoire");
                helper.assertTrue(countInventoryItem(joinedPlayer, ModItems.grimoire()) == 1,
                    "a repeated join check should not add another grimoire");

                ConfigSettings.applySyncedValues(withGrimoireSetting(original, false));
                ServerPlayer disabledPlayer = helper.makeMockServerPlayerInLevel();
                helper.assertTrue(countInventoryItem(disabledPlayer, ModItems.grimoire()) == 0,
                    "a disabled grimoire setting should not give an item on join");

            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
        helper.succeed();
    }

    public static void grimoireTrackerPersistsAndDropsWhenInventoryIsFull(GameTestHelper helper) {
        synchronized (TEST_STATE_LOCK) {
            ServerLevel level = helper.getLevel();
            ConfigSyncPayload original = ConfigSyncPayload.fromCurrentConfig();
            try {
                ConfigSettings.applySyncedValues(withGrimoireSetting(original, true));
                ServerPlayer player = makeUniqueServerPlayer(helper);
                BlockPos dropPos = helper.absolutePos(new BlockPos(4, 1, 4));
                player.setPos(dropPos.getX() + 0.5, dropPos.getY() + 1.0, dropPos.getZ() + 0.5);
                fillInventory(player);
                GrimoireTracker tracker = GrimoireTracker.get(level.getServer());
                UUID playerUuid = player.getUUID();

                helper.assertTrue(tracker == GrimoireTracker.get(level.getServer()),
                    "the Grimoire tracker should be shared by the server saved-data store");
                helper.assertTrue(GrimoireTracker.giveGrimoireIfNeeded(level.getServer(), player),
                    "a full-inventory first join should still be processed");
                helper.assertTrue(countInventoryItem(player, ModItems.grimoire()) == 0,
                    "a full inventory should force the Grimoire through the world-drop branch");
                helper.assertTrue(tracker.hasReceivedGrimoire(playerUuid),
                    "a full-inventory first join should be persisted in the tracker");

                CompoundTag savedTracker = (CompoundTag) GrimoireTracker.TYPE.codec()
                    .encodeStart(NbtOps.INSTANCE, tracker)
                    .getOrThrow();
                GrimoireTracker restoredTracker = GrimoireTracker.TYPE.codec()
                    .parse(NbtOps.INSTANCE, savedTracker)
                    .getOrThrow();
                helper.assertTrue(restoredTracker.hasReceivedGrimoire(playerUuid),
                    "the Grimoire tracker codec should retain given player UUIDs");
                helper.assertTrue(!GrimoireTracker.giveGrimoireIfNeeded(level.getServer(), player),
                    "a persisted first-join record should suppress duplicate grants");
                helper.runAfterDelay(2, () -> {
                    assertItemEntityNear(helper, level, ModItems.grimoire(), dropPos);
                    helper.succeed();
                });
                return;
            } finally {
                ConfigSettings.applySyncedValues(original);
            }
        }
    }

    private static AntiPikeBlockEntity placeActiveAntiPike(GameTestHelper helper, BlockPos antiPikePos, int floorRadius) {
        ServerLevel level = helper.getLevel();
        BlockPos headPos = antiPikePos.above();
        BlockPos absoluteAntiPikePos = helper.absolutePos(antiPikePos);
        for (int x = -floorRadius; x <= floorRadius; x++) {
            for (int z = -floorRadius; z <= floorRadius; z++) {
                helper.setBlock(antiPikePos.offset(x, -1, z), Blocks.STONE);
            }
        }
        helper.setBlock(antiPikePos, ModBlocks.antiPike());
        helper.setBlock(headPos, ModBlocks.pikeHead());
        PikeHeadBlockEntity head = helper.getBlockEntity(headPos, PikeHeadBlockEntity.class);
        head.setStoredHead("minecraft:skeleton_skull", 0);
        AntiPikeBlock antiPike = (AntiPikeBlock) ModBlocks.antiPike();
        level.setBlock(absoluteAntiPikePos, ModBlocks.antiPike().defaultBlockState(), Block.UPDATE_ALL);
        antiPike.setPlacedBy(level, absoluteAntiPikePos, level.getBlockState(absoluteAntiPikePos), null, ItemStack.EMPTY);
        helper.assertTrue(level.getBlockState(absoluteAntiPikePos).getValue(AntiPikeBlock.ACTIVATED),
            "the test Anti-Pike should be activated before ticking");
        return helper.getBlockEntity(antiPikePos, AntiPikeBlockEntity.class);
    }

    private static int countEntities(ServerLevel level, EntityType<?> entityType, AABB bounds) {
        return level.getEntities(entityType, bounds, entity -> true).size();
    }

    private static int countItemEntities(ServerLevel level, Item item, AABB bounds) {
        return level.getEntities(EntityType.ITEM, bounds, entity -> entity.getItem().is(item)).size();
    }

    private static void assertItemEntityNear(GameTestHelper helper, ServerLevel level, Item item, BlockPos pos) {
        helper.assertTrue(countItemEntities(level, item, new AABB(pos).inflate(8.0D)) > 0,
            "expected a nearby dropped item: " + BuiltInRegistries.ITEM.getKey(item));
    }

    private static ServerPlayer makeUniqueServerPlayer(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ServerPlayer player = new ServerPlayer(
            level.getServer(),
            level,
            new GameProfile(UUID.randomUUID(), "effigies-test-player"),
            ClientInformation.createDefault()
        );
        player.getAbilities().instabuild = false;
        player.getAbilities().mayBuild = true;
        return player;
    }

    private static void destroyBlockWithDrops(GameTestHelper helper, BlockPos pos, Player player) {
        ServerLevel level = helper.getLevel();
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockState state = level.getBlockState(absolutePos);
        state.getBlock().playerWillDestroy(level, absolutePos, state, player);
        level.destroyBlock(absolutePos, true, player, 512);
    }

    private static AABB antiPikeBounds(BlockPos center, int radius) {
        return new AABB(
            center.getX() - radius, center.getY() - radius, center.getZ() - radius,
            center.getX() + radius + 1, center.getY() + radius + 1, center.getZ() + radius + 1
        );
    }

    private static int countInventoryItem(Player player, Item item) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void fillInventory(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            player.getInventory().setItem(slot, new ItemStack(Items.DIRT, 64));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ItemStack assembleRecipe(
        GameTestHelper helper,
        RecipeManager recipes,
        String recipeName,
        RecipeInput input
    ) {
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, dataId(recipeName));
        Optional<RecipeHolder<?>> holder = recipes.byKey(recipeKey);
        helper.assertTrue(holder.isPresent(), "recipe is not loaded: " + recipeName);
        if (holder.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Recipe recipe = holder.get().value();
        return assembleRecipeValue(helper, recipe, input);
    }

    @SuppressWarnings("rawtypes")
    private static ItemStack assembleRecipeValue(GameTestHelper helper, Recipe recipe, RecipeInput input) {
        //? if mc12011 {
        return recipe.assemble(input, helper.getLevel().registryAccess());
        //?} else {
        /*return recipe.assemble(input);
        *///?}
    }

    private static Item item(String id) {
        Identifier identifier = id.contains(":")
            ? Identifier.tryParse(id)
            : Effigies.id(id);
        return BuiltInRegistries.ITEM.getValue(identifier);
    }

    private static ItemStack itemStack(String id) {
        return new ItemStack(item(id));
    }

    private static CraftingInput recipeInputFor(String recipeName) {
        return switch (recipeName) {
            case "wooden_pike" -> spearRecipeInput("minecraft:wooden_spear");
            case "stone_pike" -> spearRecipeInput("minecraft:stone_spear");
            case "copper_pike" -> spearRecipeInput("minecraft:copper_spear");
            case "iron_pike" -> spearRecipeInput("minecraft:iron_spear");
            case "golden_pike" -> spearRecipeInput("minecraft:golden_spear");
            case "diamond_pike" -> spearRecipeInput("minecraft:diamond_spear");
            case "netherite_pike" -> spearRecipeInput("minecraft:netherite_spear");
            case "stone_pike_upgrade" -> upgradeRecipeInput("minecraft:cobblestone", "effigies:wooden_pike");
            case "copper_pike_upgrade" -> upgradeRecipeInput("minecraft:copper_ingot", "effigies:stone_pike");
            case "iron_pike_upgrade" -> upgradeRecipeInput("minecraft:iron_ingot", "effigies:copper_pike");
            case "golden_pike_upgrade" -> upgradeRecipeInput("minecraft:gold_ingot", "effigies:iron_pike");
            case "diamond_pike_upgrade" -> upgradeRecipeInput("minecraft:diamond", "effigies:golden_pike");
            case "anti_spear" -> CraftingInput.of(3, 3, List.of(
                ItemStack.EMPTY, ItemStack.EMPTY, itemStack("minecraft:dragon_breath"),
                ItemStack.EMPTY, itemStack("minecraft:netherite_spear"), ItemStack.EMPTY,
                itemStack("minecraft:nether_star"), ItemStack.EMPTY, ItemStack.EMPTY
            ));
            case "anti_pike" -> spearRecipeInput("effigies:anti_spear", "minecraft:breeze_rod");
            case "anti_pike_upgrade" -> CraftingInput.of(3, 3, List.of(
                ItemStack.EMPTY, itemStack("effigies:netherite_pike"), itemStack("minecraft:dragon_breath"),
                ItemStack.EMPTY, itemStack("minecraft:breeze_rod"), ItemStack.EMPTY,
                itemStack("minecraft:nether_star"), itemStack("minecraft:breeze_rod"), ItemStack.EMPTY
            ));
            case "grimoire" -> CraftingInput.of(3, 3, List.of(
                itemStack("minecraft:blackstone"), itemStack("minecraft:gilded_blackstone"), itemStack("minecraft:blackstone"),
                itemStack("minecraft:gilded_blackstone"), itemStack("minecraft:book"), itemStack("minecraft:gilded_blackstone"),
                itemStack("minecraft:blackstone"), itemStack("minecraft:gilded_blackstone"), itemStack("minecraft:blackstone")
            ));
            default -> throw new IllegalArgumentException("No crafting input fixture for " + recipeName);
        };
    }

    private static CraftingInput spearRecipeInput(String spearId) {
        return spearRecipeInput(spearId, "minecraft:stick");
    }

    private static CraftingInput spearRecipeInput(String topId, String lowerId) {
        return CraftingInput.of(1, 3, List.of(
            itemStack(topId),
            itemStack(lowerId),
            itemStack(lowerId)
        ));
    }

    private static CraftingInput upgradeRecipeInput(String materialId, String pikeId) {
        return CraftingInput.of(1, 3, List.of(
            itemStack(materialId),
            itemStack(pikeId),
            itemStack("minecraft:stick")
        ));
    }

    private static void assertAppliedConfig(GameTestHelper helper, ConfigSyncPayload expected) {
        helper.assertTrue(ConfigSettings.giveGrimoireOnJoin == expected.giveGrimoireOnJoin(),
            "synced grimoire setting was not applied");
        helper.assertTrue(ConfigSettings.woodenPikeRadius == expected.woodenPikeRadius()
                && ConfigSettings.stonePikeRadius == expected.stonePikeRadius()
                && ConfigSettings.copperPikeRadius == expected.copperPikeRadius()
                && ConfigSettings.ironPikeRadius == expected.ironPikeRadius()
                && ConfigSettings.goldenPikeRadius == expected.goldenPikeRadius()
                && ConfigSettings.diamondPikeRadius == expected.diamondPikeRadius()
                && ConfigSettings.netheritePikeRadius == expected.netheritePikeRadius(),
            "synced pike radii were not applied");
        helper.assertTrue(ConfigSettings.antiPikeMinSpawnDelay == expected.antiPikeMinSpawnDelay()
                && ConfigSettings.antiPikeMaxSpawnDelay == expected.antiPikeMaxSpawnDelay()
                && ConfigSettings.antiPikeMaxNearbyEntities == expected.antiPikeMaxNearbyEntities()
                && ConfigSettings.antiPikeSpawnRange == expected.antiPikeSpawnRange()
                && ConfigSettings.antiPikeActivationRange == expected.antiPikeActivationRange(),
            "synced anti-pike settings were not applied");
        helper.assertTrue(ConfigSettings.getBlockToEntityMappings().equals(expected.blockEntityMappings()),
            "synced mappings were not applied");
        helper.assertTrue(ConfigSettings.getDimensionWhitelist().equals(expected.dimensionWhitelist()),
            "synced dimension whitelist was not applied");
    }

    private static ConfigSyncPayload sentinelConfig(ConfigSyncPayload original) {
        return new ConfigSyncPayload(
            !original.giveGrimoireOnJoin(),
            11,
            12,
            13,
            14,
            15,
            16,
            17,
            21,
            22,
            23,
            24,
            25,
            Map.of("minecraft:skeleton_skull", "minecraft:zombie"),
            List.of("minecraft:the_nether")
        );
    }

    private static ConfigSyncPayload withDefaultPikeRadii(ConfigSyncPayload original) {
        return new ConfigSyncPayload(
            original.giveGrimoireOnJoin(),
            DefaultSettings.WOODEN_PIKE_RADIUS,
            DefaultSettings.STONE_PIKE_RADIUS,
            DefaultSettings.COPPER_PIKE_RADIUS,
            DefaultSettings.IRON_PIKE_RADIUS,
            DefaultSettings.GOLDEN_PIKE_RADIUS,
            DefaultSettings.DIAMOND_PIKE_RADIUS,
            DefaultSettings.NETHERITE_PIKE_RADIUS,
            original.antiPikeMinSpawnDelay(),
            original.antiPikeMaxSpawnDelay(),
            original.antiPikeMaxNearbyEntities(),
            original.antiPikeSpawnRange(),
            original.antiPikeActivationRange(),
            original.blockEntityMappings(),
            original.dimensionWhitelist()
        );
    }

    private static Map<String, String> defaultMappings() {
        return Map.of(
            "minecraft:skeleton_skull", "minecraft:skeleton",
            "minecraft:wither_skeleton_skull", "minecraft:wither_skeleton",
            "minecraft:zombie_head", "minecraft:zombie",
            "minecraft:creeper_head", "minecraft:creeper",
            "minecraft:piglin_head", "minecraft:piglin"
        );
    }

    private static ConfigSyncPayload withGrimoireSetting(ConfigSyncPayload original, boolean giveGrimoireOnJoin) {
        return new ConfigSyncPayload(
            giveGrimoireOnJoin,
            original.woodenPikeRadius(),
            original.stonePikeRadius(),
            original.copperPikeRadius(),
            original.ironPikeRadius(),
            original.goldenPikeRadius(),
            original.diamondPikeRadius(),
            original.netheritePikeRadius(),
            original.antiPikeMinSpawnDelay(),
            original.antiPikeMaxSpawnDelay(),
            original.antiPikeMaxNearbyEntities(),
            original.antiPikeSpawnRange(),
            original.antiPikeActivationRange(),
            original.blockEntityMappings(),
            original.dimensionWhitelist()
        );
    }

    private static ConfigSyncPayload withAntiPikeConfig(
        ConfigSyncPayload original,
        int minSpawnDelay,
        int maxSpawnDelay,
        int maxNearbyEntities,
        int spawnRange,
        int activationRange
    ) {
        return new ConfigSyncPayload(
            original.giveGrimoireOnJoin(),
            original.woodenPikeRadius(),
            original.stonePikeRadius(),
            original.copperPikeRadius(),
            original.ironPikeRadius(),
            original.goldenPikeRadius(),
            original.diamondPikeRadius(),
            original.netheritePikeRadius(),
            minSpawnDelay,
            maxSpawnDelay,
            maxNearbyEntities,
            spawnRange,
            activationRange,
            original.blockEntityMappings(),
            original.dimensionWhitelist()
        );
    }

    private static PikeHeadBlockEntity placeActivePike(GameTestHelper helper, BlockPos pikePos) {
        return placeActivePike(helper, pikePos, (PikeBlock) ModBlocks.woodenPike());
    }

    private static PikeHeadBlockEntity placeActivePike(GameTestHelper helper, BlockPos pikePos, PikeBlock pike) {
        ServerLevel level = helper.getLevel();
        BlockPos headPos = pikePos.above();
        BlockPos absolutePikePos = helper.absolutePos(pikePos);
        helper.setBlock(pikePos.below(), Blocks.STONE);
        helper.setBlock(pikePos, pike);
        helper.setBlock(headPos, ModBlocks.pikeHead());
        PikeHeadBlockEntity head = helper.getBlockEntity(headPos, PikeHeadBlockEntity.class);
        head.setStoredHead("minecraft:skeleton_skull", 0);
        level.setBlock(absolutePikePos, pike.defaultBlockState(), Block.UPDATE_ALL);
        pike.setPlacedBy(level, absolutePikePos, level.getBlockState(absolutePikePos), null, ItemStack.EMPTY);
        return head;
    }

    private static EntityType<?> skeleton(ServerLevel level) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("skeleton"));
    }

    private static EntityType<?> zombie(ServerLevel level) {
        return BuiltInRegistries.ENTITY_TYPE.getValue(minecraftId("zombie"));
    }

    private static ConfigSyncPayload withConfig(ConfigSyncPayload original, int woodenRadius, List<String> whitelist) {
        return new ConfigSyncPayload(
            !original.giveGrimoireOnJoin(),
            woodenRadius,
            original.stonePikeRadius(),
            original.copperPikeRadius(),
            original.ironPikeRadius(),
            original.goldenPikeRadius(),
            original.diamondPikeRadius(),
            original.netheritePikeRadius(),
            original.antiPikeMinSpawnDelay(),
            original.antiPikeMaxSpawnDelay(),
            original.antiPikeMaxNearbyEntities(),
            original.antiPikeSpawnRange(),
            original.antiPikeActivationRange(),
            Map.of("minecraft:skeleton_skull", "minecraft:skeleton"),
            whitelist
        );
    }

    private static Identifier minecraftId(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    private static Identifier dataId(String path) {
        return Identifier.fromNamespaceAndPath(Effigies.MOD_ID, path);
    }
}
