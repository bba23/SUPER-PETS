# SUPER PETS Creator's Stone Local AI Notes

SUPER PETS keeps AI optional as a separate add-on jar. The normal `super_pets` mod runs without the add-on, Python, CUDA, Hugging Face, NVIDIA packages, model downloads, or cloud APIs. When `super-pets-ai-1.0.13.jar` is installed beside `modid-1.0.13.jar`, or when `super-pets-full-1.0.13.jar` is used by itself, the add-on uses deterministic fallback visuals plus generated item data by default.

## Runtime Config

Edit `config/super_pets_ai.properties`:

```properties
creator_stone_ai_enabled=false
creator_stone_ai_helper_command=
creator_stone_generated_asset_root=config/super_pets_ai/generated_ai
creator_stone_anvil_cost=4
```

`creator_stone_ai_enabled=false` means no helper is used. `creator_stone_ai_helper_command` is intentionally blank until a local Python helper is wired in. `creator_stone_generated_asset_root` is where generated PNGs should be written if a helper is later enabled.

## Optional Local Dependencies

No dependency below is required for Minecraft to launch.

```powershell
py -3.12 -m venv .venv-ai
.\.venv-ai\Scripts\python.exe -m pip install -U pip uv
.\.venv-ai\Scripts\python.exe -m uv pip install "diffusers[torch]" transformers accelerate safetensors pillow
```

For NVIDIA acceleration, install PyTorch using the official selector for the local Windows, Python, and CUDA combination. Example shape:

```powershell
.\.venv-ai\Scripts\python.exe -m pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu128
```

Verify CUDA before enabling any helper:

```powershell
.\.venv-ai\Scripts\python.exe -c "import torch; print(torch.__version__); print(torch.cuda.is_available()); print(torch.cuda.get_device_name(0) if torch.cuda.is_available() else 'cpu')"
```

## Model And Paths

- Preferred starter model ID: `stabilityai/sd-turbo`
- Hugging Face cache root: `%USERPROFILE%\.cache\huggingface\hub`
- Generated Creator's Stone textures: `config/super_pets_ai/generated_ai/creator_stones/<cache_key>.png`
- Generated tool textures: `config/super_pets_ai/generated_ai/tools/<cache_key>_<tool>.png`
- Offline mode after caching: set `HF_HUB_OFFLINE=1`

## Fallback Behavior

The add-on stores generation metadata immediately and uses bundled deterministic textures when AI is disabled, unavailable, slow, invalid, or missing dependencies. Fallback assets are under `src/aiAddon/resources/assets/super_pets_ai/textures/item/` and cover Creator's Stone plus sword, axe, pickaxe, shovel, and hoe skins for parrot and axolotl identities.
