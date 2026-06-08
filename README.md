# Friendship Relics

Friendship Relics is a Fabric Minecraft Java Edition mod built from the existing MCreator workspace in this folder. This branch builds the normal mod plus an optional `super_pets_ai` add-on jar for the Creator's Stone AI extension.

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
- Added bonding for parrots and axolotls using matching linked stone Bond IDs.
- Bonded pets store Bond ID/status in persistent entity tags and receive permanent scale/attribute updates.
- Master Stones store Bond ID, pet type, variant, pet name, world seed, and Legendary Giant status as item custom data.
- Master Stone ownership transfers by possession of the Master Stone.
- Master Stone remains with the player after death only when it is in the necklace slot.
- Bonded parrots and axolotls can be mounted by a player carrying the matching Master Stone.
- Bonded axolotl riders receive Water Breathing while mounted.
- Bonded parrot and axolotl mounts use synced client WASD/jump/sneak/sprint input for controllable flight/swimming.
- Creator's Stone lives in the optional `super_pets_ai` add-on jar, so the normal `super_pets` mod can be installed without it.
- The base mod manifest suggests `super_pets_ai`, but does not require it.
- Existing shrine variants were moved to underground Overworld worldgen with Y -50 to -10 targeting and more survival-oriented spacing.
- Added a Friendship Shrine chest loot table for Stone of Friendship, Bonding Soul, and Ancient Resin rewards. The optional AI add-on injects Creator's Stone into that table when installed.

## Necklace Slot Controls

The necklace slot is implemented as a persistent player-data slot, not a full custom GUI.

- Keep a Friendship Necklace in inventory.
- Hold a Linked Friendship Stone or Master Stone in one hand.
- Right-click with the Friendship Necklace in the other hand to equip that stone into the necklace slot.
- Right-click the Friendship Necklace again with a filled slot to remove the stored stone back to inventory.

This keeps the feature stable in singleplayer and LAN while avoiding a fragile custom MCreator inventory screen.

## Ancient Resin Slab

The slab is implemented as a 2x2 half-height multiblock. Each quarter uses a different UV section so one resin texture stretches across the full 2x2 surface instead of repeating four times. Slab parts keep the Ancient Resin slippery movement feel even at half-block height. Placement checks all four target positions before placing. Breaking any part removes the full 2x2 and drops one matching-color slab item in survival, preventing the common child-block duplication bug.

## Creator's Stone AI Extension

Creator's Stone is registered by the optional add-on as `super_pets_ai:creator_stone`. Install `modid-1.0.11.jar` for the normal mod, then add `super-pets-ai-1.0.11.jar` only when you want the AI extension. The add-on depends on `super_pets` and adds its item, loot injection, creative-tab entry, and anvil mixin from its own jar.

A shrine chest can rarely contain Creator's Stone when the add-on is installed. Use it on a bonded large Parrot or Axolotl, including Legendary Giant pets, while carrying the matching Master Stone. Unbonded pets, unsupported entities, normal unrelated mobs, players, and hostile mobs are rejected.

Generation stores data on the same Creator's Stone stack instead of creating a separate artifact item. The stored fields include pet type, pet variant, pet name, Bond ID, Legendary Giant status, world/player/pet-derived generation seed, cache key, generated texture path, generated description, and fallback reason.

The anvil integration accepts a generated Creator's Stone in the second slot and one valid sword, axe, pickaxe, shovel, or hoe in the first slot. The output keeps the original tool item and adds a generated tool skin state through item data, lore, name, and bundled fallback item model.

Local AI is currently an optional integration boundary, not a required runtime dependency. The Java mod never calls cloud APIs and does not require Python, CUDA, Hugging Face, or NVIDIA libraries to run. Configure these keys in `config/super_pets_ai.properties` only when a local helper is added:

- `creator_stone_ai_enabled=false`
- `creator_stone_ai_helper_command=`
- `creator_stone_generated_asset_root=config/super_pets_ai/generated_ai`
- `creator_stone_anvil_cost=4`

Recommended local helper assumptions:

- Python: use a virtual environment on Python 3.10-3.12 for best Windows PyTorch compatibility.
- NVIDIA/CUDA: install the PyTorch CUDA wheel that matches the local GPU and driver using the official PyTorch selector.
- Hugging Face libraries: `uv pip install "diffusers[torch]" transformers accelerate safetensors pillow`
- Preferred starter model ID: `stabilityai/sd-turbo`
- Suggested local cache root: `%USERPROFILE%\.cache\huggingface\hub`
- Suggested generated asset root: `config/super_pets_ai/generated_ai`
- Offline behavior: cache models first, then set `HF_HUB_OFFLINE=1` if needed.

If any helper step fails or the helper command is blank, the mod keeps working with bundled fallback textures:

- `creator_stone.png`
- `creator_stone_parrot.png`
- `creator_stone_axolotl.png`
- `generated_tool_<tool>_<pet>.png`

## Known Limitations

- The target folder named in the original request, `C:\Users\Itay\Documents\MCreatorEAP2026222416\SUPER`, was not present. This workspace was used because it is the active MCreator/Fabric project containing the existing Shrine and Ancient Resin base.
- The shrine loot table is provided as a data hook, but existing `.nbt` shrine structures were preserved and not rewritten. Existing chests must reference `super_pets:chests/friendship_shrine` inside the structure for in-structure loot to appear.
- Pet mounting uses stable server tick movement based on rider input and look direction. It is a base implementation, not a custom animated mount renderer.
- Multi-passenger giant pets attempt to allow more riders through forced mounting, but vanilla passenger constraints may still limit behavior depending on entity internals.
- Local AI generation is represented by add-on item data, add-on config, cache paths, and fallback models, but no model weights are bundled.
- The current Java path does not launch a Python helper process yet; it records the configured helper boundary and always keeps deterministic fallback visuals available.
- The Stone of Friendship item model is a vanilla-compatible adaptation of the supplied `IDEL_E` Blockbench Java model. The Friendship Necklace item model uses a vanilla-compatible gold chain shape plus the supplied glTF gold texture; the original glTF source is preserved under `assets/super_pets/source_models/necklace/` for later replacement with a final necklace model.

## Build

```powershell
.\gradlew.bat build
```

The built jar is written to `build/libs/`.
The normal mod jar is `modid-1.0.11.jar`; the optional AI add-on jar is `super-pets-ai-1.0.11.jar`.
