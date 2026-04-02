import re
import sys


def camel_to_snake(name):
    """
    将 CamelCase 名称转换为 snake_case
    """
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    snake = re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()
    return snake

def read_inner_lines_count(mapping_file):
    """
    从映射文件中读取结构体高度（不含大括号）
    返回整数，读取失败返回 None
    """
    try:
        with open(mapping_file, 'r', encoding='utf-8') as f:
            for line in f:
                match = re.search(r'结构体高度.*?(\d+)', line)
                if match:
                    return int(match.group(1))
    except Exception as e:
        print(f"读取映射文件失败: {e}")
    return None

def merge_lines(file_path, output_path, n):
    with open(file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()

    merged_lines = ['' for _ in range(n)]
    for i in range(len(lines)):
        merged_lines[i % n] += lines[i].rstrip('\n') + ','

    result_lines = [line.rstrip(',') for line in merged_lines]

    with open(output_path, 'w', encoding='utf-8') as new_file:
        for i, line in enumerate(result_lines):
            if i < len(result_lines) - 1:
                new_file.write(line + '\n')
            else:
                new_file.write(line)  # 最后一行不加换行

def calc_dimensions_and_append(file_path, append_file):
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    height = len(lines)
    max_width = 0
    length = 0  # 每行字段数，按第一行计算

    if height > 0:
        length = lines[0].count(',') + 1

    for line in lines:
        fields = line.split(',')
        for field in fields:
            field_len = len(field)
            if field_len > max_width:
                max_width = field_len - 1  # 减1可选

    with open(append_file, 'a', encoding='utf-8') as f:
        f.write(f"\n宽: {max_width}\n")
        f.write(f"高: {height}\n")
        f.write(f"长: {length}\n")

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("用法: python input3.py 输出文件名（不带扩展名）")
        sys.exit(1)

    output_basename = sys.argv[1]
    snake_name = camel_to_snake(output_basename)
    mapping_filename = "结构方块映射表.txt"
    cleaned_structure_filename = "cleaned_structure.txt"
    output_filename = f"{snake_name}.mb"

    n = read_inner_lines_count(mapping_filename)
    if n is None:
        print(f"无法从 {mapping_filename} 中读取结构体高度，程序退出。")
        sys.exit(1)

    print(f"读取到结构体高度 n = {n}")
    print(f"生成的输出文件名为：{output_filename}")

    merge_lines(cleaned_structure_filename, output_filename, n)
    calc_dimensions_and_append(output_filename, mapping_filename)

    print(f"处理完成，输出文件：{output_filename}，统计已追加到 {mapping_filename}")
