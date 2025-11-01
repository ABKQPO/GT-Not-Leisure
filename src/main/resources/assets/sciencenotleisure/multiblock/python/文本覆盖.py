import os
import re

def replace_public_file_paths(root_folder):
    # re.DOTALL 允许 . 匹配换行
    pattern = re.compile(r'\bpublic\b(.*\b_FILE_PATH\b.*?;)', re.DOTALL)

    for dirpath, dirnames, filenames in os.walk(root_folder):
        for filename in filenames:
            if filename.endswith(".java"):
                file_path = os.path.join(dirpath, filename)
                with open(file_path, 'r', encoding='utf-8') as file:
                    content = file.read()

                # 替换 public 为 private
                new_content, count = pattern.subn(r'private\1', content)
                if count > 0:
                    with open(file_path, 'w', encoding='utf-8') as file:
                        file.write(new_content)
                    print(f"Replaced {count} occurrence(s) in: {file_path}")

if __name__ == "__main__":
    target_folder = "E:/Github/GT-Not-Leisure/src/main/java/com"
    replace_public_file_paths(target_folder)
