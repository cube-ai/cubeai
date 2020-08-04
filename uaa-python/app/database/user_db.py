from app.globals.globals import g
from app.domain.user import User
from app.utils.pageable import gen_pageable


async def create_user(user):
    sql = '''
        INSERT INTO user (
                login,
                password_hash,
                full_name,
                phone,
                email,
                image_url,
                activated,
                lang_key,
                activation_key,
                created_by,
                created_date,
                last_modified_by,
                last_modified_date,
                authorities
        ) VALUES ("{}", "{}", "{}", "{}", "{}", "{}", {}, "{}", "{}", "{}", "{}", "{}", "{}", "{}")
    '''.format(
        user.login,
        user.password,
        user.fullName,
        user.phone,
        user.email,
        user.imageUrl,
        1 if user.activated else 0,
        user.langKey,
        user.activationKey if hasattr(user, 'activationKey') else '',
        user.createdBy,
        user.createdDate,
        user.lastModifiedBy,
        user.lastModifiedDate,
        ','.join(user.authorities)
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def find_one_by_kv(key, value):
    sql = 'SELECT * FROM user WHERE {} = "{}" limit 1'.format(key, value)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    user_list = []
    for record in records:
        user = User()
        user.from_record(record)
        user_list.append(user)

    return user_list[0] if len(user_list) > 0 else None


async def update_user_activation(user):
    sql = '''
        UPDATE user SET 
            activated = {},
            activation_key = "{}"
        WHERE id = {}
    '''.format(
        1 if user.activated else 0,
        user.activationKey,
        user.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_user_password_reset(user):
    sql = '''
        UPDATE user SET 
            password_hash = "{}",
            reset_key = "{}",
            reset_date = "{}"
        WHERE id = {}
    '''.format(
        user.password,
        user.resetKey,
        user.resetDate,
        user.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def change_password(login, password):
    sql = '''
        UPDATE user SET 
            password_hash = "{}"
        WHERE login = "{}"
    '''.format(
        password,
        login
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_user_base_info(user):
    sql = '''
        UPDATE user SET 
            full_name = "{}",
            phone = "{}",
            email = "{}",
            image_url = "{}"
        WHERE login = "{}"
    '''.format(
        user.fullName,
        user.phone,
        user.email,
        user.imageUrl,
        user.login
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_user(user):
    sql = '''
        UPDATE user SET 
            full_name = "{}",
            phone = "{}",
            email = "{}",
            activated = {},
            last_modified_by = "{}",
            last_modified_date = "{}",
            authorities = "{}"
        WHERE id = {}
    '''.format(
        user.fullName,
        user.phone,
        user.email,
        user.activated,
        user.lastModifiedBy,
        user.lastModifiedDate,
        ','.join(user.authorities),
        user.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_user_by_id(id):
    sql = 'DELETE FROM user WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_user_by_login(login):
    sql = 'DELETE FROM user WHERE login = "{}"'.format(login)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_users(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM user {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM user {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            user_list = []
            for record in records:
                user = User()
                user.from_record(record)
                user.remove_internal_values()
                user_list.append(user.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], user_list


async def find_any_users(where):
    sql = 'SELECT * FROM user {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            user_list = []
            for record in records:
                user = User()
                user.from_record(record)
                user.remove_internal_values()
                user_list.append(user)

    return user_list
