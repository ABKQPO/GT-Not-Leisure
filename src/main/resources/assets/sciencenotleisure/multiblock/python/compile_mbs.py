from __future__ import annotations

import struct
from pathlib import Path


MAGIC = b"MBS1"
PYTHON_DIR = Path(__file__).resolve().parent
RESOURCE_MULTIBLOCK_ROOT = PYTHON_DIR.parent


def parse_txt_text(text: str) -> list[list[str]]:
    """将 decompile_mbs.py 输出的文本解析为二维结构数组。"""
    rows: list[list[str]] = []
    for line in text.splitlines():
        if line:
            rows.append(line.split(","))
    return rows


def write_mbs(path: Path, structure: list[list[str]]) -> None:
    """将二维结构数组写入 .mbs 二进制文件。"""
    string_table: list[str] = []
    string_indexes: dict[str, int] = {}
    for row in structure:
        for cell in row:
            if cell not in string_indexes:
                string_indexes[cell] = len(string_table)
                string_table.append(cell)

    with path.open("wb") as handle:
        handle.write(MAGIC)
        handle.write(struct.pack(">i", len(string_table)))
        for cell in string_table:
            encoded = cell.encode("utf-8")
            handle.write(struct.pack(">i", len(encoded)))
            handle.write(encoded)
        handle.write(struct.pack(">i", len(structure)))
        for row in structure:
            handle.write(struct.pack(">i", len(row)))
            for cell in row:
                handle.write(struct.pack(">i", string_indexes[cell]))


def compile_file(path: Path) -> Path:
    structure = parse_txt_text(path.read_text(encoding="utf-8"))
    output_path = path.with_suffix(".mbs")
    write_mbs(output_path, structure)
    return output_path


def find_txt_files(root: Path) -> list[Path]:
    python_dir = (root / "python").resolve()
    txt_files: list[Path] = []
    for txt_file in sorted(root.rglob("*.txt")):
        resolved = txt_file.resolve()
        if resolved == python_dir or python_dir in resolved.parents:
            continue
        txt_files.append(txt_file)
    return txt_files


def main() -> None:
    print("该工具会把 decompile_mbs.py 反编译出的 .txt 文本重新编译为 .mbs 二进制文件。")
    target = input("请输入相对 multiblock 资源目录的 .txt 路径，或输入 * 处理全部：").strip()
    if not target:
        raise ValueError("必须输入要处理的目标路径")

    if target == "*":
        for txt_file in find_txt_files(RESOURCE_MULTIBLOCK_ROOT):
            output = compile_file(txt_file)
            print(f"已编译：{txt_file.relative_to(RESOURCE_MULTIBLOCK_ROOT)} -> {output.relative_to(RESOURCE_MULTIBLOCK_ROOT)}")
        return

    source = (RESOURCE_MULTIBLOCK_ROOT / target).resolve()
    output = compile_file(source)
    print(f"已编译：{source.name} -> {output.name}")


if __name__ == "__main__":
    main()
