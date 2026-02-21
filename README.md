![Effigies](https://cdn.modrinth.com/data/cached_images/457527a06967b93028dab2ea52012640e90bd7f3.png)

**Effigies** is a mod that adds powerful mob spawning prevention options to the game.  

## Version Support

Effigies supports multiple Minecraft versions and mod loaders:

| Loader | Minecraft Versions |
|--------|-------------------|
| **Fabric** | 1.21.11+ |
| **NeoForge** | 1.21.11+ |

## Features  
  
- Adds 7 new Pikes (one for each spear variant) that prevent specific mob types from spawning within a radius based on their tier
    - Refer to the **Pike Table** below for specifics about the radius and recipes
    - Pikes also have item tooltips in-game describing their range
    - Radius' can now also be customized via the `config/effigies-config.toml` file
        - You can disable Pikes from preventing spawns by setting the radius to `-1`
        - Any config options will also update the in-game tooltips
- Place a Pike block in the world, then right-click it with a mob head (or place the mob head on top of it) to activate it
    - When active, the Pike will prevent that mob type from spawning within its area of effect
        - Fire particles spawn when it becomes active!
        - Fire particles will also spawn whenever the Pike successfully prevents a mob spawn
        - Smoke particles spawn when it becomes inactive (the head is removed)
    - Supported mob heads: Skeleton, Wither Skeleton, Zombie, Creeper, and Piglin
        - More to come soon!
- Shift+Right-Click an activated Pike to remove the head (the head will drop as an item)
- Breaking a Pike will drop both the Pike block and the head (if activated)
- Pikes work in all dimensions (Overworld, Nether, End, and modded dimensions)

## In-Game Example
Inactive (left) and Active (right) Diamond Pikes
![Inactive and Active Example](https://cdn.modrinth.com/data/cached_images/f3d95544b107234a0887d37170aa2ee0321cbe91.png)

## Pike Table

| Tier | Recipe | Area of Effect |
| ---- | ------ | -------------- | 
| Wooden | 1 Wooden Spear + 2 Sticks | 1 Chunk (the chunk it's in) |
| Stone | 1 Stone Spear + 2 Sticks | 1 Chunk + 1 Radius (the chunk it's in + all 8 surrounding chunks) |
| Copper | 1 Copper Spear + 2 Sticks | 1 Chunk + 2 Radius |
| Iron | 1 Iron Spear + 2 Sticks | 1 Chunk + 4 Radius |
| Golden | 1 Golden Spear + 2 Sticks | 1 Chunk + 6 Radius |
| Diamond | 1 Diamond Spear + 2 Sticks | 1 Chunk + 8 Radius |
| Netherite | 1 Netherite Spear + 2 Sticks | 1 Chunk + 16 Radius |

## Notes

- Each Pike only prevents the spawn of the mob type matching the head placed on it
- Multiple Pikes with different heads can be used to prevent multiple mob types from spawning within the radius
- Pikes are efficient even in large numbers - they use an optimized chunk-based lookup system
- When connecting to a server, the server will send it's config to the player, meaning the tooltips will update accurately and the player's own config file won't be modified or used.

## Upcoming Features
- Config options for defining heads (even from other mods!) and what kinds of mobs they prevent from spawning