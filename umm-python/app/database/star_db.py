from app.globals.globals import g
from app.domain.star import Star
from app.utils.pageable import gen_pageable


async def create_star(star):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_stars(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM star {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM star {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            star_list = []
            for record in records:
                star = Star()
                star.from_record(record)
                star_list.append(star.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], star_list


async def get_star_user_login(id):
    sql = 'SELECT user_login FROM star WHERE id = {} limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchone()

    return records[0]


async def delete_star(id):
    sql = 'DELETE FROM star WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_stared_uuid_list(user_login):

    sql = 'SELECT target_uuid FROM star WHERE user_login = "{}"'.format(user_login)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            uuids_list = cursor.fetchall()

    uuid_list = []
    for uuids in uuids_list:
        uuid_list.append(uuids[0])

    return uuid_list


async def get_stared_count(target_uuid):

    sql = 'SELECT COUNT(*) FROM star WHERE target_uuid = "{}"'.format(target_uuid)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            stared_count = cursor.fetchone()

    return stared_count[0]
