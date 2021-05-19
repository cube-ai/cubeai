from app.global_data.global_data import g
from app.domain.user import User
from app.utils.pageable import gen_pageable


def create_user(user):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM user limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def find_one_by_kv(key, value):
    sql = 'SELECT * FROM user WHERE {} = "{}" limit 1'.format(key, value)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    user_list = []
    for record in records:
        user = User()
        user.from_record(record)
        user_list.append(user)

    return user_list[0] if len(user_list) > 0 else None


def update_user_activation(user):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_user_password_reset(user):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def change_password(login, password):
    sql = '''
        UPDATE user SET 
            password_hash = "{}"
        WHERE login = "{}"
    '''.format(
        password,
        login
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_user_base_info(user):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_user(user):
    sql = '''
        UPDATE user SET 
            full_name = "{}",
            phone = "{}",
            email = "{}",
            activated = {},
            last_modified_by = "{}",
            last_modified_date = "{}",
            authorities = "{}",
            image_url = "{}"
        WHERE id = {}
    '''.format(
        user.fullName,
        user.phone,
        user.email,
        user.activated,
        user.lastModifiedBy,
        user.lastModifiedDate,
        ','.join(user.authorities),
        user.imageUrl,
        user.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_user_by_id(id):
    sql = 'DELETE FROM user WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_user_by_login(login):
    sql = 'DELETE FROM user WHERE login = "{}"'.format(login)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_users(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM user {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM user {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        user_list = []
        for record in records:
            user = User()
            user.from_record(record)
            user.remove_internal_values()
            user_list.append(user.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], user_list


def find_any_users(where):
    sql = 'SELECT * FROM user {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        user_list = []
        for record in records:
            user = User()
            user.from_record(record)
            user.remove_internal_values()
            user_list.append(user)
    conn.close()

    return user_list
