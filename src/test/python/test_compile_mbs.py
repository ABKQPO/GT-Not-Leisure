from __future__ import annotations

import importlib.util
import tempfile
import unittest
from pathlib import Path


PYTHON_DIR = (
    Path(__file__).resolve().parents[2]
    / "main"
    / "resources"
    / "assets"
    / "sciencenotleisure"
    / "multiblock"
    / "python"
)


def load_module(name: str, path: Path):
    spec = importlib.util.spec_from_file_location(name, path)
    if spec is None or spec.loader is None:
        raise RuntimeError(f"Could not load {path}")
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


class CompileMbsTest(unittest.TestCase):
    def test_compile_txt_round_trips_through_decompiler(self) -> None:
        compile_mbs = load_module("compile_mbs", PYTHON_DIR / "compile_mbs.py")
        decompile_mbs = load_module("decompile_mbs", PYTHON_DIR / "decompile_mbs.py")

        with tempfile.TemporaryDirectory() as temp_dir:
            txt_path = Path(temp_dir) / "sample.txt"
            txt_path.write_text("A,B,A\n中,~, \n\n", encoding="utf-8")

            mbs_path = compile_mbs.compile_file(txt_path)

            self.assertEqual(txt_path.with_suffix(".mbs"), mbs_path)
            self.assertEqual([["A", "B", "A"], ["中", "~", " "]], decompile_mbs.read_mbs(mbs_path))

    def test_find_txt_files_skips_python_tool_directory(self) -> None:
        compile_mbs = load_module("compile_mbs", PYTHON_DIR / "compile_mbs.py")

        with tempfile.TemporaryDirectory() as temp_dir:
            root = Path(temp_dir)
            keep = root / "machine.txt"
            skip = root / "python" / "input.txt"
            keep.write_text("A\n", encoding="utf-8")
            skip.parent.mkdir()
            skip.write_text("B\n", encoding="utf-8")

            self.assertEqual([keep], list(compile_mbs.find_txt_files(root)))


if __name__ == "__main__":
    unittest.main()
