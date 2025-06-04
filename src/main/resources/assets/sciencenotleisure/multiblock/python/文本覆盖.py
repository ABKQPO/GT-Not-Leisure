import os

def replace_text_in_java_files(root_folder, old_text, new_text):
    for dirpath, dirnames, filenames in os.walk(root_folder):
        for filename in filenames:
            if filename.endswith(".java"):
                file_path = os.path.join(dirpath, filename)
                with open(file_path, 'r', encoding='utf-8') as file:
                    content = file.read()

                if old_text in content:
                    new_content = content.replace(old_text, new_text)
                    with open(file_path, 'w', encoding='utf-8') as file:
                        file.write(new_content)
                    print(f"Replaced text in: {file_path}")

if __name__ == "__main__":
    target_folder = "E:/Github/GT-Not-Leisure/src/main/java/com"

    text_to_replace = """.setMaxParallelSupplier(this::getLimitedMaxParallel);"""

    replacement_text = """.setMaxParallelSupplier(this::getTrueParallel);"""

    replace_text_in_java_files(target_folder, text_to_replace, replacement_text)
