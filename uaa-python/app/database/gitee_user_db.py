from app.global_data.global_data import g
from app.domain.gitee_user import GiteeUser


def create_gitee_user(gitee_user):
    sql = '''
        INSERT INTO gitee_user (
                gitee_login,
                user_login
        ) VALUES ("{}", "{}")
    '''.format(
        gitee_user.giteeLogin,
        gitee_user.userLogin
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM gitee_user limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def find_gitee_user(gitee_login):
    sql = 'SELECT * FROM gitee_user WHERE gitee_login = "{}" limit 1'.format(gitee_login)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    gitee_user_list = []
    for record in records:
        gitee_user = GiteeUser()
        gitee_user.from_record(record)
        gitee_user_list.append(gitee_user)

    return gitee_user_list[0] if len(gitee_user_list) > 0 else None


def update_gitee_user(gitee_user):
    sql = '''
        UPDATE gitee_user SET 
            user_login = "{}"
        WHERE id = {}
    '''.format(
        gitee_user.user_login,
        gitee_user.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
