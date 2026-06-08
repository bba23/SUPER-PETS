# SUPER PETS

SUPER PETS is a Fabric Minecraft Java Edition mod built from the existing MCreator workspace in this folder. The base jar runs without AI tooling, and an optional AI add-on jar adds Creator's Stone features without making Python, Hugging Face, model downloads, or cloud APIs required for normal play.

## Chosen Versions

- Minecraft Java Edition: `26.1.2`
- Fabric Loader: `0.19.2` for Lunar Client compatibility
- Fabric API: `0.150.0+26.1.2`
- Java: `25`
- MCreator target: `2026.1+` with the Fabric generator, preserving the generated workspace structure

These match the existing Gradle/MCreator project and current compatible Fabric setup. Reference checks used the MCreator 2026.1 release notes/news, Fabric Loader/Fabric API Maven listings, and Minecraft release/version listings.

## Implemented Base Systems

- Preserved the existing MCreator mod elements, registry IDs, assets, and Friendship Shrine structure files.
- Relabeled the old decorative resin block concept as Ancient Resin while preserving the existing block IDs:
  - `ancientresienred`
  - `ancientresiengreen`
  - `ancientresienpurple`
  - `ancientresienpink`
- Normal Ancient Resin blocks are slippery, speed-boosting shrine blocks with a strong low-jump effect.
- Added Ancient Resin Slab as a custom half-height 2x2 multiblock with red, green, purple, and pink variants.
- Added recipes for Ancient Resin Slab, Linked Friendship Stones, Friendship Necklace, Giant Soul Stone, and Giant Linked Friendship Stone.
- Friendship Necklace can be crafted with gold nuggets plus a shrine core material, or with a vanilla chain in the top slot.
- Friendship Necklace recipes include recipe-book unlock advancements.
- Added Stone of Friendship naming on the existing `stonebutwird` item to avoid duplicating the existing MCreator item.
- Added Bonding Soul, Linked Friendship Stone, Giant Soul Stone, Giant Linked Friendship Stone, Master Stone, and Friendship Necklace.
- Added persistent player necklace-slot storage through custom player save data.
- Friendship Necklace leaves inventory while equipped, can be unequipped with an empty-hand sneak-right-click, and auto-unequips when the bonded pet dies.
- Added bonding for parrots and axolotls using matching linked stone Bond IDs.
- Bonded pets store Bond ID/status in persistent entity tags and receive permanent scale/attribute updates.
- Master Stones store Bond ID, pet type, variant, pet name, world seed, and Legendary Giant status as item custom data.
- Master Stone ownership transfers by possession of the Master Stone.
- Master Stone remains with the player after death only when it is in the necklace slot.
- Bonded parrots and axolotls can be mounted by a player carrying the matching Master Stone.
- Bonded axolotl riders receive Water Breathing while mounted.
- Bonded parrot and axolotl mounts use synced client WASD/jump/sneak/sprint input for controllable flight/swimming.
- Basic non-AI relic data hooks are stored on the Master Stone and pet tags for a later visual generation phase.
- Existing shrine variants were moved to underground Overworld worldgen with Y -50 to -10 targeting and more survival-oriented spacing.
- Added a Friendship Shrine chest loot table for Stone of Friendship, Bonding Soul, and Ancient Resin rewards.

## Optional AI Add-On

Creator's Stone is registered by the optional add-on as `super_pets_ai:creator_stone`. Install `modid-1.0.12.jar` for the base mod only, add `super-pets-ai-1.0.12.jar` when you want the AI extension, or use `super-pets-full-1.0.12.jar` when you want one jar that bundles both.

## Necklace Slot Controls

The necklace slot is implemented as a persistent player-data slot, not a full custom GUI.

- Hold a Linked Friendship Stone or Master Stone in one hand.
- Right-click with the Friendship Necklace in the other hand to equip the necklace and stone. The necklace item leaves your inventory while equipped.
- Sneak-right-click with an empty hand to unequip the necklace and return the stored items to inventory.
- If the bonded pet dies while its Master Stone is equipped, the owner is notified and the Friendship Necklace is automatically unequipped.

This keeps the feature stable in singleplayer and LAN while avoiding a fragile custom MCreator inventory screen.

## Ancient Resin Slab

The slab is implemented as a 2x2 half-height multiblock. Each quarter uses a different UV section so one resin texture stretches across the full 2x2 surface instead of repeating four times. Slab parts keep the Ancient Resin slippery movement feel even at half-block height. Placement checks all four target positions before placing. Breaking any part removes the full 2x2 and drops one matching-color slab item in survival, preventing the common child-block duplication bug.

## Known Limitations

- The target folder named in the original request, `C:\Users\Itay\Documents\MCreatorEAP2026222416\SUPER`, was not present. This workspace was used because it is the active MCreator/Fabric project containing the existing Shrine and Ancient Resin base.
- The shrine loot table is provided as a data hook, but existing `.nbt` shrine structures were preserved and not rewritten. Existing chests must reference `super_pets:chests/friendship_shrine` inside the structure for in-structure loot to appear.
- Pet mounting uses stable server tick movement based on rider input and look direction. It is a base implementation, not a custom animated mount renderer.
- Multi-passenger giant pets attempt to allow more riders through forced mounting, but vanilla passenger constraints may still limit behavior depending on entity internals.
- AI visuals and generated item/tool texture hooks are isolated in the optional add-on jar.
- The Stone of Friendship item model is a vanilla-compatible adaptation of the supplied `IDEL_E` Blockbench Java model. The Friendship Necklace item model uses a vanilla-compatible gold chain shape plus the supplied glTF gold texture; the original glTF source is preserved under `assets/super_pets/source_models/necklace/` for later replacement with a final necklace model.

## Build

```powershell
.\gradlew.bat build
```

The release jars are written to `build/libs/`:

- `modid-1.0.12.jar`
- `super-pets-ai-1.0.12.jar`
- `super-pets-full-1.0.12.jar`
