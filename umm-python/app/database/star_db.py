from app.global_data.global_data import g
from app.domain.star import Star
from app.utils.pageable import gen_pageable


def create_star(star):
    sql = '''
        INSERT INTO star (
            user_login,
            target_type,
            target_uuid,
            star_date
        ) VALUES ("{}", "{}", "{}", "{}")
    '''.format(
        star.userLogin,
        star.targetType,
        star.targetUuid,
        star.starDate
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM star limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def get_stars(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM star {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM star {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        star_list = []
        for record in records:
            star = Star()
            star.from_record(record)
            star_list.append(star.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], star_list


def get_star_user_login(id):
    sql = 'SELECT user_login FROM star WHERE id = {} limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchone()
    conn.close()

    return records[0]


def delete_star(id):
    sql = 'DELETE FROM star WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_stared_uuid_list(user_login):

    sql = 'SELECT target_uuid FROM star WHERE user_login = "{}"'.format(user_login)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        uuids_list = cursor.fetchall()
    conn.close()

    uuid_list = []
    for uuids in uuids_list:
        uuid_list.append(uuids[0])

    return uuid_list


def get_stared_count(target_uuid):

    sql = 'SELECT COUNT(*) FROM star WHERE target_uuid = "{}"'.format(target_uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        stared_count = cursor.fetchone()
    conn.close()

    return stared_count[0]
