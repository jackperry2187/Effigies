package jackperry2187.effigies.client;

import jackperry2187.effigies.PikeTier;
import jackperry2187.effigies.config.ConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if mc12011 {
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?}
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

public class GrimoireScreen extends Screen {

    private enum Section { HOME, PIKES, HEADS, ANTI, GRIMOIRE }

    // ========== Layout ==========
    private static final int BW = 312;
    private static final int BH = 210;
    private static final int MARGIN = 15;
    private static final int CT = 16;
    private static final int SPINE_HALF = 11;
    private static final int LP_X = MARGIN;
    private static final int RP_X = BW / 2 + SPINE_HALF;
    private static final int PW = BW / 2 - SPINE_HALF - MARGIN;
    private static final int SLOT = 18;
    private static final int GRID = SLOT * 3;

    // ========== Colors (Blackstone & Gilded Grimoire) ==========
    private static final int C_CVR_DK  = 0xFF0E0B12;
    private static final int C_CVR     = 0xFF1E1926;
    private static final int C_CVR_LT  = 0xFF2D2638;
    private static final int C_GOLD    = 0xFFC9A84C;
    private static final int C_GOLD_DK = 0xFF8B7530;
    private static final int C_PAGE    = 0xFF4A3E2A;
    private static final int C_PG_EDGE = 0xFF342C1C;
    private static final int C_PG_STAIN= 0xFF3E3422;
    private static final int C_SPINE   = 0xFF161220;
    private static final int C_TITLE   = 0xFFD0B4E8;
    private static final int C_BODY    = 0xFFBDA0D8;
    private static final int C_SUB     = 0xFFA088C0;
    private static final int C_PGNUM   = 0xFF8878A8;
    private static final int C_SL_BD   = 0xFF1E1828;
    private static final int C_SL_BG   = 0xFF3A3248;
    private static final int C_SL_HI   = 0x60C9A84C;
    private static final int C_ARROW   = 0xFFA088C0;
    private static final int C_GRID_BG = 0xFF1E1926;

    // ========== Recipe Data ==========
    private static final String[][] PIKE_NORMAL = {
        {"minecraft:wooden_spear", "effigies:wooden_pike"},
        {"minecraft:stone_spear", "effigies:stone_pike"},
        {"minecraft:copper_spear", "effigies:copper_pike"},
        {"minecraft:iron_spear", "effigies:iron_pike"},
        {"minecraft:golden_spear", "effigies:golden_pike"},
        {"minecraft:diamond_spear", "effigies:diamond_pike"},
        {"minecraft:netherite_spear", "effigies:netherite_pike"},
    };

    private static final String[][] PIKE_UPGRADE = {
        null,
        {"minecraft:cobblestone", "effigies:wooden_pike", "effigies:stone_pike"},
        {"minecraft:copper_ingot", "effigies:stone_pike", "effigies:copper_pike"},
        {"minecraft:iron_ingot", "effigies:copper_pike", "effigies:iron_pike"},
        {"minecraft:gold_ingot", "effigies:iron_pike", "effigies:golden_pike"},
        {"minecraft:diamond", "effigies:golden_pike", "effigies:diamond_pike"},
        null,
    };

    private static final String[] NETHERITE_SMITHING = {
        "minecraft:netherite_upgrade_smithing_template",
        "effigies:diamond_pike",
        "minecraft:netherite_ingot",
        "effigies:netherite_pike"
    };

    private static final String[][] ANTI_SPEAR_GRID = {
        {null, null, "minecraft:dragon_breath"},
        {null, "minecraft:netherite_spear", null},
        {"minecraft:nether_star", null, null}
    };

    private static final String[][] ANTI_PIKE_GRID = {
        {"effigies:anti_spear", null, null},
        {"minecraft:breeze_rod", null, null},
        {"minecraft:breeze_rod", null, null}
    };

    private static final String[][] ANTI_PIKE_UPGRADE_GRID = {
        {null, "effigies:netherite_pike", "minecraft:dragon_breath"},
        {null, "minecraft:breeze_rod", null},
        {"minecraft:nether_star", "minecraft:breeze_rod", null}
    };

    private static final String[][] GRIMOIRE_GRID = {
        {"minecraft:blackstone", "minecraft:gilded_blackstone", "minecraft:blackstone"},
        {"minecraft:gilded_blackstone", "minecraft:book", "minecraft:gilded_blackstone"},
        {"minecraft:blackstone", "minecraft:gilded_blackstone", "minecraft:blackstone"}
    };

    private static final String[] PIKE_TIER_KEYS = {
        "wooden", "stone", "copper", "iron", "golden", "diamond", "netherite"
    };

    // ========== State ==========
    private Section currentSection = Section.HOME;
    private int currentPage = 0;
    private int bookX, bookY;
    private final HashMap<String, ItemStack> itemStackCache = new HashMap<>();
    private List<Map.Entry<String, String>> headEntries;
    private ItemStack hoveredStack = ItemStack.EMPTY;
    private String hoveredEntityId = null;

    private AbstractWidget backButton;
    private AbstractWidget prevButton;
    private AbstractWidget nextButton;
    private AbstractWidget tocPikesButton;
    private AbstractWidget tocHeadsButton;
    private AbstractWidget tocAntiButton;
    private AbstractWidget tocGrimoireButton;

    public GrimoireScreen() {
        super(Component.translatable("effigies.guide.title"));
    }

    // ========== Version-conditional rendering wrappers ==========
    //? if mc12011 {
    private void txt(GuiGraphics g, Component t, int x, int y, int c) { g.drawString(font, t, x, y, c, false); }
    private void txt(GuiGraphics g, FormattedCharSequence t, int x, int y, int c) { g.drawString(font, t, x, y, c, false); }
    private void txt(GuiGraphics g, String t, int x, int y, int c) { g.drawString(font, t, x, y, c, false); }
    private void itm(GuiGraphics g, ItemStack s, int x, int y) { g.renderItem(s, x, y); }
    //?} else {
    /*private void txt(GuiGraphicsExtractor g, Component t, int x, int y, int c) { g.text(font, t, x, y, c, false); }
    private void txt(GuiGraphicsExtractor g, FormattedCharSequence t, int x, int y, int c) { g.text(font, t, x, y, c, false); }
    private void txt(GuiGraphicsExtractor g, String t, int x, int y, int c) { g.text(font, t, x, y, c, false); }
    private void itm(GuiGraphicsExtractor g, ItemStack s, int x, int y) { g.item(s, x, y); }
    *///?}

    // ========== Initialization ==========

    @Override
    protected void init() {
        super.init();
        bookX = (width - BW) / 2;
        bookY = (height - BH) / 2;
        headEntries = new ArrayList<>(ConfigSettings.getBlockToEntityMappings().entrySet());

        int tocX = bookX + RP_X + 10;
        int tocW = PW - 20;
        int tocBaseY = bookY + CT + 24;

        int footerY = bookY + BH - 20;

        backButton = addRenderableWidget(styledBtn(
            Component.translatable("effigies.guide.back"),
            bookX + 9, bookY + 8, 45, 12, this::goHome));
        prevButton = addRenderableWidget(styledBtn(
            Component.literal("<<"),
            bookX + 9, footerY, 22, 12, this::prevPage));
        nextButton = addRenderableWidget(styledBtn(
            Component.literal(">>"),
            bookX + BW - 9 - 22, footerY, 22, 12, this::nextPage));
        tocPikesButton = addRenderableWidget(styledBtn(
            Component.translatable("effigies.guide.toc.pikes"),
            tocX, tocBaseY, tocW, 20, () -> goToSection(Section.PIKES)));
        tocHeadsButton = addRenderableWidget(styledBtn(
            Component.translatable("effigies.guide.toc.heads"),
            tocX, tocBaseY + 24, tocW, 20, () -> goToSection(Section.HEADS)));
        tocAntiButton = addRenderableWidget(styledBtn(
            Component.translatable("effigies.guide.toc.anti"),
            tocX, tocBaseY + 48, tocW, 20, () -> goToSection(Section.ANTI)));
        tocGrimoireButton = addRenderableWidget(styledBtn(
            Component.translatable("effigies.guide.toc.grimoire"),
            tocX, tocBaseY + 72, tocW, 20, () -> goToSection(Section.GRIMOIRE)));

        updateButtonVisibility();
    }

    // ========== Navigation ==========

    private void goHome() {
        currentSection = Section.HOME;
        currentPage = 0;
        updateButtonVisibility();
    }

    private void goToSection(Section section) {
        currentSection = section;
        currentPage = 0;
        updateButtonVisibility();
    }

    private void prevPage() {
        if (currentPage > 0) currentPage--;
        updateButtonVisibility();
    }

    private void nextPage() {
        if (currentPage < getMaxPage()) currentPage++;
        updateButtonVisibility();
    }

    private int getMaxPage() {
        return switch (currentSection) {
            case HOME -> 0;
            case PIKES -> 4;
            case HEADS -> headEntries.size() <= 3 ? 0 : (int) Math.ceil((headEntries.size() - 3) / 6.0);
            case ANTI -> 1;
            case GRIMOIRE -> 0;
        };
    }

    private void updateButtonVisibility() {
        boolean isHome = currentSection == Section.HOME;
        backButton.visible = !isHome;
        prevButton.visible = !isHome && currentPage > 0;
        nextButton.visible = !isHome && currentPage < getMaxPage();
        tocPikesButton.visible = isHome;
        tocHeadsButton.visible = isHome;
        tocAntiButton.visible = isHome;
        tocGrimoireButton.visible = isHome;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    // ========== Item Utilities ==========

    private ItemStack getItemStack(String itemId) {
        return itemStackCache.computeIfAbsent(itemId, id -> {
            Identifier rl = Identifier.tryParse(id);
            if (rl != null && BuiltInRegistries.ITEM.containsKey(rl))
                return new ItemStack(BuiltInRegistries.ITEM.getValue(rl));
            return ItemStack.EMPTY;
        });
    }

    private ItemStack getBlockItemStack(String blockId) {
        Identifier rl = Identifier.tryParse(blockId);
        if (rl != null && BuiltInRegistries.BLOCK.containsKey(rl))
            return new ItemStack(BuiltInRegistries.BLOCK.getValue(rl).asItem());
        return ItemStack.EMPTY;
    }

    // ================================================================
    //                         RENDERING
    // ================================================================

    private AbstractWidget styledBtn(Component text, int x, int y, int w, int h, Runnable action) {
        return new AbstractWidget(x, y, w, h, text) {
            @Override
            public void onClick(MouseButtonEvent event, boolean pressed) { action.run(); }
            //? if mc12011 {
            @Override
            protected void renderWidget(GuiGraphics gfx, int mx, int my, float delta) {
            //?} else {
            /*@Override
            protected void extractWidgetRenderState(GuiGraphicsExtractor gfx, int mx, int my, float delta) {
            *///?}
                boolean hov = isHovered();
                gfx.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                    hov ? C_GOLD : C_GOLD_DK);
                gfx.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1,
                    hov ? 0xFF2E2840 : 0xFF222030);
                int tc = hov ? 0xFFE0C8F8 : 0xFFCEB0E8;
                int tw = font.width(getMessage());
                txt(gfx, getMessage(),
                    getX() + (getWidth() - tw) / 2,
                    getY() + (getHeight() - font.lineHeight + 1) / 2,
                    tc);
            }
            @Override
            protected void updateWidgetNarration(NarrationElementOutput output) {
                defaultButtonNarrationText(output);
            }
        };
    }

    //? if mc12011 {
    @Override
    public void render(GuiGraphics gfx, int mx, int my, float delta) {
    //?} else {
    /*@Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mx, int my, float delta) {
    *///?}
        hoveredStack = ItemStack.EMPTY;
        hoveredEntityId = null;
        //? if mc12011 {
        super.render(gfx, mx, my, delta);
        //?} else {
        /*super.extractRenderState(gfx, mx, my, delta);
        *///?}
    }

    //? if mc12011 {
    @Override
    public void renderBackground(GuiGraphics gfx, int mx, int my, float delta) {
    //?} else {
    /*@Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mx, int my, float delta) {
    *///?}
        //? if mc12011 {
        super.renderBackground(gfx, mx, my, delta);
        //?} else {
        /*super.extractBackground(gfx, mx, my, delta);
        *///?}
        drawBookBg(gfx);
        switch (currentSection) {
            case HOME -> renderHome(gfx, mx, my);
            case PIKES -> renderPikes(gfx, mx, my);
            case HEADS -> renderHeads(gfx, mx, my);
            case ANTI -> renderAnti(gfx, mx, my);
            case GRIMOIRE -> renderGrimoire(gfx, mx, my);
        }
        drawPageNumbers(gfx);
        if (!hoveredStack.isEmpty()) {
            gfx.setTooltipForNextFrame(font, hoveredStack, mx, my);
        } else if (hoveredEntityId != null) {
            Identifier id = Identifier.tryParse(hoveredEntityId);
            if (id != null && BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
                Component name = BuiltInRegistries.ENTITY_TYPE.getValue(id).getDescription();
                gfx.setComponentTooltipForNextFrame(font, List.of(name), mx, my);
            }
        }
    }

    //? if mc12011 {
    private void drawBookBg(GuiGraphics gfx) {
    //?} else {
    /*private void drawBookBg(GuiGraphicsExtractor gfx) {
    *///?}
        int x = bookX, y = bookY;

        gfx.fill(x, y, x + BW, y + BH, C_CVR_DK);
        gfx.fill(x + 2, y + 2, x + BW - 2, y + BH - 2, C_CVR);

        gfx.fill(x + 3, y + 3, x + BW - 3, y + 4, C_GOLD);
        gfx.fill(x + 3, y + BH - 4, x + BW - 3, y + BH - 3, C_GOLD);
        gfx.fill(x + 3, y + 3, x + 4, y + BH - 3, C_GOLD);
        gfx.fill(x + BW - 4, y + 3, x + BW - 3, y + BH - 3, C_GOLD);

        gfx.fill(x + 4, y + 4, x + BW - 4, y + BH - 4, C_CVR_LT);

        int cLen = 8, cTh = 2;
        gfx.fill(x + 4, y + 4, x + 4 + cLen, y + 4 + cTh, C_GOLD);
        gfx.fill(x + 4, y + 4, x + 4 + cTh, y + 4 + cLen, C_GOLD);
        gfx.fill(x + BW - 4 - cLen, y + 4, x + BW - 4, y + 4 + cTh, C_GOLD);
        gfx.fill(x + BW - 4 - cTh, y + 4, x + BW - 4, y + 4 + cLen, C_GOLD);
        gfx.fill(x + 4, y + BH - 4 - cTh, x + 4 + cLen, y + BH - 4, C_GOLD);
        gfx.fill(x + 4, y + BH - 4 - cLen, x + 4 + cTh, y + BH - 4, C_GOLD);
        gfx.fill(x + BW - 4 - cLen, y + BH - 4 - cTh, x + BW - 4, y + BH - 4, C_GOLD);
        gfx.fill(x + BW - 4 - cTh, y + BH - 4 - cLen, x + BW - 4, y + BH - 4, C_GOLD);

        gfx.fill(x + 7, y + 7, x + BW - 7, y + BH - 7, C_PAGE);

        gfx.fill(x + 7, y + 7, x + 9, y + BH - 7, C_PG_STAIN);
        gfx.fill(x + BW - 9, y + 7, x + BW - 7, y + BH - 7, C_PG_STAIN);
        gfx.fill(x + 7, y + 7, x + BW - 7, y + 9, C_PG_STAIN);
        gfx.fill(x + 7, y + BH - 9, x + BW - 7, y + BH - 7, C_PG_STAIN);

        gfx.fill(x + 7, y + 7, x + 8, y + BH - 7, C_PG_EDGE);
        gfx.fill(x + BW - 8, y + 7, x + BW - 7, y + BH - 7, C_PG_EDGE);

        int sx = x + BW / 2;
        gfx.fill(sx - 4, y + 4, sx + 4, y + BH - 4, C_SPINE);
        gfx.fill(sx - 4, y + 4, sx - 3, y + BH - 4, C_GOLD_DK);
        gfx.fill(sx + 3, y + 4, sx + 4, y + BH - 4, C_GOLD_DK);
        gfx.fill(sx, y + 7, sx + 1, y + BH - 7, C_PG_EDGE);

        for (int sy = y + 12; sy < y + BH - 12; sy += 8) {
            gfx.fill(sx - 5, sy, sx - 4, sy + 4, C_GOLD);
            gfx.fill(sx + 4, sy, sx + 5, sy + 4, C_GOLD);
        }

        int cy = y + BH / 2 - 3;
        gfx.fill(sx - 6, cy, sx + 6, cy + 6, C_GOLD);
        gfx.fill(sx - 5, cy + 1, sx + 5, cy + 5, C_GOLD_DK);
    }

    // ----- HOME -----

    //? if mc12011 {
    private void renderHome(GuiGraphics gfx, int mx, int my) {
    //?} else {
    /*private void renderHome(GuiGraphicsExtractor gfx, int mx, int my) {
    *///?}
        int lx = bookX + LP_X, rx = bookX + RP_X, ty = bookY + CT;
        txt(gfx, Component.translatable("effigies.guide.welcome.title"),
            lx, ty, C_TITLE);
        drawWrap(gfx, Component.translatable("effigies.guide.welcome.body"),
            lx, ty + 14, PW, C_BODY);
        txt(gfx, Component.translatable("effigies.guide.toc"),
            rx + 10, ty, C_TITLE);
    }

    // ----- PIKES -----

    //? if mc12011 {
    private void renderPikes(GuiGraphics gfx, int mx, int my) {
    //?} else {
    /*private void renderPikes(GuiGraphicsExtractor gfx, int mx, int my) {
    *///?}
        int ty = bookY + 22;

        if (currentPage == 0) {
            int lx = bookX + LP_X, rx = bookX + RP_X;
            txt(gfx, Component.translatable("effigies.guide.pikes.title"),
                lx, ty, C_TITLE);
            drawWrap(gfx, Component.translatable("effigies.guide.pikes.body1"),
                lx, ty + 14, PW, C_BODY);
            drawPikeRadii(gfx, rx, ty);
            return;
        }

        int leftTierIdx = (currentPage - 1) * 2;
        int rightTierIdx = leftTierIdx + 1;

        if (leftTierIdx < PIKE_NORMAL.length) {
            renderSinglePike(gfx, leftTierIdx, bookX + LP_X, bookX + LP_X + PW / 2, ty, mx, my);
        }
        if (rightTierIdx < PIKE_NORMAL.length) {
            renderSinglePike(gfx, rightTierIdx, bookX + RP_X, bookX + RP_X + PW / 2, ty, mx, my);
        }
    }

    //? if mc12011 {
    private void renderSinglePike(GuiGraphics gfx, int tierIndex, int pageX, int pageCX, int startY, int mx, int my) {
    //?} else {
    /*private void renderSinglePike(GuiGraphicsExtractor gfx, int tierIndex, int pageX, int pageCX, int startY, int mx, int my) {
    *///?}
        String tierKey = PIKE_TIER_KEYS[tierIndex];
        String[] normal = PIKE_NORMAL[tierIndex];

        Component title = Component.translatable("effigies.guide.pike." + tierKey);
        int titleW = font.width(title);
        txt(gfx, title, pageCX - titleW / 2, startY, C_TITLE);

        int y = startY + 14;
        txt(gfx, Component.translatable("effigies.guide.recipe.crafting"),
            pageX, y, C_SUB);
        y += 12;

        String[][] normalGrid = {
            {normal[0], null, null},
            {"minecraft:stick", null, null},
            {"minecraft:stick", null, null}
        };
        drawRecipe(gfx, normalGrid, normal[1], pageCX, y, mx, my);
        y += GRID + 8;

        String[] upgrade = PIKE_UPGRADE[tierIndex];
        if (upgrade != null) {
            txt(gfx, Component.translatable("effigies.guide.recipe.upgrade"),
                pageX, y, C_SUB);
            y += 12;
            String[][] upgradeGrid = {
                {upgrade[0], null, null},
                {upgrade[1], null, null},
                {"minecraft:stick", null, null}
            };
            drawRecipe(gfx, upgradeGrid, upgrade[2], pageCX, y, mx, my);
        } else if (tierIndex == 6) {
            txt(gfx, Component.translatable("effigies.guide.recipe.smithing"),
                pageX, y, C_SUB);
            y += 14;
            drawSmithing(gfx, pageCX, y, mx, my);
        } else {
            drawWrap(gfx, Component.translatable("effigies.guide.recipe.no_upgrade"),
                pageX, y, PW, C_SUB);
        }
    }

    //? if mc12011 {
    private void drawPikeRadii(GuiGraphics gfx, int x, int y) {
    //?} else {
    /*private void drawPikeRadii(GuiGraphicsExtractor gfx, int x, int y) {
    *///?}
        txt(gfx, Component.translatable("effigies.guide.pikes.radii"),
            x, y, C_TITLE);
        int ry = y + 14;
        PikeTier[] tiers = PikeTier.values();
        for (int i = 0; i < tiers.length; i++) {
            int radius = ConfigSettings.getPikeRadius(tiers[i]);
            String tierName = PIKE_TIER_KEYS[i].substring(0, 1).toUpperCase()
                + PIKE_TIER_KEYS[i].substring(1);
            Component line;
            if (radius < 0) {
                line = Component.translatable("effigies.guide.pikes.radius.disabled", tierName);
            } else if (radius == 0) {
                line = Component.translatable("effigies.guide.pikes.radius.own_chunk", tierName);
            } else if (radius == 1) {
                line = Component.translatable("effigies.guide.pikes.radius.chunk", tierName, radius);
            } else {
                line = Component.translatable("effigies.guide.pikes.radius.chunks", tierName, radius);
            }
            txt(gfx, line, x, ry, C_BODY);
            ry += font.lineHeight + 2;
        }
        ry += 4;
        drawWrap(gfx, Component.translatable("effigies.guide.pikes.activate"),
            x, ry, PW, C_BODY);
    }

    // ----- HEADS -----

    //? if mc12011 {
    private void renderHeads(GuiGraphics gfx, int mx, int my) {
    //?} else {
    /*private void renderHeads(GuiGraphicsExtractor gfx, int mx, int my) {
    *///?}
        int lx = bookX + LP_X, rx = bookX + RP_X, ty = bookY + 22;
        int perSide = 3;
        int entryH = 50;

        if (currentPage == 0) {
            txt(gfx, Component.translatable("effigies.guide.heads.title"),
                lx, ty, C_TITLE);
            drawWrap(gfx, Component.translatable("effigies.guide.heads.body"),
                lx, ty + 14, PW, C_BODY);
            if (headEntries.isEmpty()) {
                drawWrap(gfx, Component.translatable("effigies.guide.heads.empty"),
                    rx, ty, PW, C_SUB);
                return;
            }
        }

        int startIdx;
        int sideStart;
        if (currentPage == 0) {
            startIdx = 0;
            sideStart = 1;
        } else {
            startIdx = 3 + (currentPage - 1) * 6;
            sideStart = 0;
        }

        for (int side = sideStart; side < 2; side++) {
            int px = side == 0 ? lx : rx;
            for (int i = 0; i < perSide; i++) {
                int idx = startIdx + (side - sideStart) * perSide + i;
                if (idx >= headEntries.size()) break;

                Map.Entry<String, String> entry = headEntries.get(idx);
                int ey = ty + 4 + i * entryH;

                ItemStack headStack = getBlockItemStack(entry.getKey());
                drawSlot(gfx, px + 2, ey + 10, headStack, mx, my);

                txt(gfx, "\u2192", px + 24, ey + 14, C_ARROW);

                int ecx = px + 56, ecy = ey + 24, eSize = 34;
                //? if mc12011 {
                MobRenderHelper.renderEntity(gfx, entry.getValue(), ecx, ecy, eSize);
                //?} else {
                /*MobRenderHelper.renderEntity((GuiGraphicsExtractor) gfx, entry.getValue(), ecx, ecy, eSize);
                *///?}

                int half = eSize / 2;
                if (mx >= ecx - half && mx < ecx + half && my >= ecy - half && my < ecy + half) {
                    hoveredEntityId = entry.getValue();
                }
            }
        }
    }

    // ----- ANTI -----

    //? if mc12011 {
    private void renderAnti(GuiGraphics gfx, int mx, int my) {
    //?} else {
    /*private void renderAnti(GuiGraphicsExtractor gfx, int mx, int my) {
    *///?}
        int lx = bookX + LP_X, rx = bookX + RP_X, ty = bookY + 22;
        int pageCX = bookX + RP_X + PW / 2;

        if (currentPage == 0) {
            txt(gfx, Component.translatable("effigies.guide.anti.spear.title"),
                lx, ty, C_TITLE);
            drawWrap(gfx, Component.translatable("effigies.guide.anti.spear.body"),
                lx, ty + 14, PW, C_BODY);

            txt(gfx, Component.translatable("effigies.guide.recipe.crafting"),
                rx, ty, C_SUB);
            drawRecipe(gfx, ANTI_SPEAR_GRID, "effigies:anti_spear", pageCX, ty + 14, mx, my);
        } else {
            txt(gfx, Component.translatable("effigies.guide.anti.pike.title"),
                lx, ty, C_TITLE);
            drawWrap(gfx, Component.translatable("effigies.guide.anti.pike.body"),
                lx, ty + 14, PW, C_BODY);

            txt(gfx, Component.translatable("effigies.guide.recipe.direct"),
                rx, ty, C_SUB);
            drawRecipe(gfx, ANTI_PIKE_GRID, "effigies:anti_pike", pageCX, ty + 14, mx, my);

            int r2Y = ty + 14 + GRID + 10;
            txt(gfx, Component.translatable("effigies.guide.recipe.upgrade"),
                rx, r2Y, C_SUB);
            drawRecipe(gfx, ANTI_PIKE_UPGRADE_GRID, "effigies:anti_pike", pageCX, r2Y + 12, mx, my);
        }
    }

    // ----- GRIMOIRE -----

    //? if mc12011 {
    private void renderGrimoire(GuiGraphics gfx, int mx, int my) {
    //?} else {
    /*private void renderGrimoire(GuiGraphicsExtractor gfx, int mx, int my) {
    *///?}
        int lx = bookX + LP_X, rx = bookX + RP_X, ty = bookY + 22;
        int pageCX = bookX + RP_X + PW / 2;

        txt(gfx, Component.translatable("effigies.guide.grimoire.title"),
            lx, ty, C_TITLE);
        drawWrap(gfx, Component.translatable("effigies.guide.grimoire.body"),
            lx, ty + 14, PW, C_BODY);

        txt(gfx, Component.translatable("effigies.guide.recipe.crafting"),
            rx, ty, C_SUB);
        drawRecipe(gfx, GRIMOIRE_GRID, "effigies:grimoire", pageCX, ty + 14, mx, my);
    }

    // ----- Page Numbers -----

    //? if mc12011 {
    private void drawPageNumbers(GuiGraphics gfx) {
    //?} else {
    /*private void drawPageNumbers(GuiGraphicsExtractor gfx) {
    *///?}
        if (currentSection == Section.HOME) return;

        int totalPages, leftPage, rightPage;

        switch (currentSection) {
            case PIKES:
                totalPages = 8;
                if (currentPage == 0) {
                    leftPage = 1; rightPage = 0;
                } else {
                    leftPage = (currentPage - 1) * 2 + 2;
                    rightPage = leftPage + 1;
                    if (rightPage > totalPages) rightPage = 0;
                }
                break;
            case HEADS:
                totalPages = headEntries.isEmpty() ? 2 : 1 + (int) Math.ceil(headEntries.size() / 3.0);
                leftPage = currentPage * 2 + 1;
                rightPage = currentPage * 2 + 2;
                if (leftPage > totalPages) leftPage = 0;
                if (rightPage > totalPages) rightPage = 0;
                break;
            case ANTI:
                totalPages = 4;
                leftPage = currentPage * 2 + 1;
                rightPage = currentPage * 2 + 2;
                break;
            case GRIMOIRE:
                totalPages = 2;
                leftPage = 1;
                rightPage = 2;
                break;
            default:
                return;
        }

        int footerY = bookY + BH - 18;
        int leftCX = bookX + LP_X + PW / 2;
        int rightCX = bookX + RP_X + PW / 2;

        if (leftPage > 0) {
            String lpg = leftPage + " / " + totalPages;
            int lw = font.width(lpg);
            txt(gfx, lpg, leftCX - lw / 2, footerY, C_PGNUM);
        }
        if (rightPage > 0) {
            String rpg = rightPage + " / " + totalPages;
            int rw = font.width(rpg);
            txt(gfx, rpg, rightCX - rw / 2, footerY, C_PGNUM);
        }
    }

    // ----- Drawing Helpers -----

    //? if mc12011 {
    private void drawWrap(GuiGraphics gfx, Component text, int x, int y, int w, int color) {
    //?} else {
    /*private void drawWrap(GuiGraphicsExtractor gfx, Component text, int x, int y, int w, int color) {
    *///?}
        List<FormattedCharSequence> lines = font.split(text, w);
        for (int i = 0; i < lines.size(); i++) {
            txt(gfx, lines.get(i), x, y + i * (font.lineHeight + 1), color);
        }
    }

    //? if mc12011 {
    private void drawSlot(GuiGraphics gfx, int x, int y, ItemStack stack, int mx, int my) {
    //?} else {
    /*private void drawSlot(GuiGraphicsExtractor gfx, int x, int y, ItemStack stack, int mx, int my) {
    *///?}
        gfx.fill(x, y, x + SLOT, y + SLOT, C_SL_BD);
        gfx.fill(x + 1, y + 1, x + SLOT - 1, y + SLOT - 1, C_SL_BG);
        if (!stack.isEmpty()) {
            itm(gfx, stack, x + 1, y + 1);
            if (mx >= x && mx < x + SLOT && my >= y && my < y + SLOT) {
                gfx.fill(x + 1, y + 1, x + SLOT - 1, y + SLOT - 1, C_SL_HI);
                hoveredStack = stack;
            }
        }
    }

    //? if mc12011 {
    private void drawCraftingGrid(GuiGraphics gfx, String[][] grid, int gx, int gy, int mx, int my) {
    //?} else {
    /*private void drawCraftingGrid(GuiGraphicsExtractor gfx, String[][] grid, int gx, int gy, int mx, int my) {
    *///?}
        gfx.fill(gx - 2, gy - 2, gx + GRID + 2, gy + GRID + 2, C_GRID_BG);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                ItemStack stack = ItemStack.EMPTY;
                if (grid[r] != null && c < grid[r].length && grid[r][c] != null)
                    stack = getItemStack(grid[r][c]);
                drawSlot(gfx, gx + c * SLOT, gy + r * SLOT, stack, mx, my);
            }
        }
    }

    //? if mc12011 {
    private void drawRecipe(GuiGraphics gfx, String[][] grid, String resultId, int cx, int y, int mx, int my) {
    //?} else {
    /*private void drawRecipe(GuiGraphicsExtractor gfx, String[][] grid, String resultId, int cx, int y, int mx, int my) {
    *///?}
        int arrowW = font.width("\u2192");
        int totalW = GRID + 10 + arrowW + 10 + SLOT;
        int gx = cx - totalW / 2;

        drawCraftingGrid(gfx, grid, gx, y, mx, my);

        int ax = gx + GRID + 10;
        int ay = y + GRID / 2 - font.lineHeight / 2;
        txt(gfx, "\u2192", ax, ay, C_ARROW);

        int rx = ax + arrowW + 10;
        int ry = y + GRID / 2 - SLOT / 2;
        drawSlot(gfx, rx, ry, getItemStack(resultId), mx, my);
    }

    //? if mc12011 {
    private void drawSmithing(GuiGraphics gfx, int cx, int y, int mx, int my) {
    //?} else {
    /*private void drawSmithing(GuiGraphicsExtractor gfx, int cx, int y, int mx, int my) {
    *///?}
        int plusW = font.width("+");
        int arrowW = font.width("\u2192");
        int g = 4, bg = 6;
        int totalW = 4 * SLOT + 2 * (g + plusW + g) + bg + arrowW + bg;
        int sx = cx - totalW / 2;
        int ty = y + SLOT / 2 - font.lineHeight / 2;

        drawSlot(gfx, sx, y, getItemStack(NETHERITE_SMITHING[0]), mx, my);
        sx += SLOT + g;
        txt(gfx, "+", sx, ty, C_ARROW);
        sx += plusW + g;
        drawSlot(gfx, sx, y, getItemStack(NETHERITE_SMITHING[1]), mx, my);
        sx += SLOT + g;
        txt(gfx, "+", sx, ty, C_ARROW);
        sx += plusW + g;
        drawSlot(gfx, sx, y, getItemStack(NETHERITE_SMITHING[2]), mx, my);
        sx += SLOT + bg;
        txt(gfx, "\u2192", sx, ty, C_ARROW);
        sx += arrowW + bg;
        drawSlot(gfx, sx, y, getItemStack(NETHERITE_SMITHING[3]), mx, my);
    }
}
