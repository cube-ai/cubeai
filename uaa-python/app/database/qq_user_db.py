from app.global_data.global_data import g
from app.domain.qq_user import QqUser


def create_qq_user(qq_user):
    sql = '''
        INSERT INTO qq_user (
                qq_login,
                user_login
        ) VALUES ("{}", "{}")
    '''.format(
        qq_user.qqLogin,
        qq_user.userLogin
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM qq_user limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def find_qq_user(qq_login):
    sql = 'SELECT * FROM qq_user WHERE qq_login = "{}" limit 1'.format(qq_login)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    qq_user_list = []
    for record in records:
        qq_user = QqUser()
        qq_user.from_record(record)
        qq_user_list.append(qq_user)

    return qq_user_list[0] if len(qq_user_list) > 0 else None


def update_qq_user(qq_user):
    sql = '''
        UPDATE qq_user SET 
            user_login = "{}"
        WHERE id = {}
    '''.format(
        qq_user.user_login,
        qq_user.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
