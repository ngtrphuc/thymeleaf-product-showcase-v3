from pathlib import Path

from PIL import Image
from rembg import remove


SOURCE_DIR = Path("src/main/resources/static/customer/images")
OUTPUT_DIR = SOURCE_DIR / "cutouts"
SUPPORTED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp"}


def process_image(source_path: Path, output_path: Path) -> None:
    with source_path.open("rb") as input_file:
        input_bytes = input_file.read()

    output_bytes = remove(input_bytes)
    output_path.write_bytes(output_bytes)

    # Normalize final files as PNG with transparency.
    with Image.open(output_path) as image:
        image.convert("RGBA").save(output_path, "PNG")


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    source_files = sorted(
        path for path in SOURCE_DIR.iterdir()
        if path.is_file() and path.suffix.lower() in SUPPORTED_EXTENSIONS
    )

    if not source_files:
        print("No source images found.")
        return

    processed = 0
    skipped = 0

    for source_path in source_files:
        output_path = OUTPUT_DIR / f"{source_path.stem}.png"
        if output_path.exists():
            skipped += 1
            print(f"Skipped existing: {output_path.name}")
            continue

        print(f"Processing: {source_path.name}")
        process_image(source_path, output_path)
        processed += 1

    print(f"Done. Processed={processed}, Skipped={skipped}, OutputDir={OUTPUT_DIR}")


if __name__ == "__main__":
    main()
