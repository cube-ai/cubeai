

def gen_pageable(pageable):
    if pageable is None:
        return ''

    p = ''
    sort_list = pageable.get('sort')
    if sort_list and len(sort_list) > 0:
        p += 'order by'
        for sort in sort_list:
            sort = camel_to_lowercase(sort).replace(',', ' ')
            p += ' ' + sort + ','
        p = p[:-1]
    if pageable.get('page') is not None and pageable.get('size') is not None:
        p += ' limit {}, {}'.format(int(pageable.get('page')) * int(pageable.get('size')), pageable.get('size'))


    return p


def camel_to_lowercase(text):
    lst = []
    for index, char in enumerate(text):
        if (char.isupper() or '0' <= char <= '9') and index != 0:
            lst.append("_")
        lst.append(char)
        if '0' <= char <= '9' and (index != len(text)-1 and text[index + 1] != ','):
            lst.append("_")

    return "".join(lst).lower()


if __name__ == '__main__':
    print(camel_to_lowercase('isAdmin'))

