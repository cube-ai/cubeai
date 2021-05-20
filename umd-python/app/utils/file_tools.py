import os
import shutil


def make_path(path):
    if not os.path.exists(path):
        os.makedirs(path)


def del_path(path):
    if os.path.exists(path):
        shutil.rmtree(path)


def del_path_files(path):
    del_list = os.listdir(path)
    for f in del_list:
        full = os.path.join(path, f)
        if os.path.isfile(full):
            os.remove(full)
        elif os.path.isdir(full):
            shutil.rmtree(full)


def replace_special_char(text):
    result = ''
    for ch in text:
        if '0' <= ch <= '9' or 'a' <= ch <= 'z' or 'A' <= ch <= 'Z':
            result += ch
        else:
            result += '-'

    if result[-1] == '-':
        result = result[:-1]

    return result
