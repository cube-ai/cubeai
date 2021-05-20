from app.global_data.global_data import g
from app.domain.github_user import GithubUser


def create_github_user(github_user):
    sql = '''
        INSERT INTO github_user (
                github_login,
                user_login
        ) VALUES ("{}", "{}")
    '''.format(
        github_user.githubLogin,
        github_user.userLogin
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM github_user limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def find_github_user(github_login):
    sql = 'SELECT * FROM github_user WHERE github_login = "{}" limit 1'.format(github_login)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    github_user_list = []
    for record in records:
        github_user = GithubUser()
        github_user.from_record(record)
        github_user_list.append(github_user)

    return github_user_list[0] if len(github_user_list) > 0 else None


def update_github_user(github_user):
    sql = '''
        UPDATE github_user SET 
            user_login = "{}"
        WHERE id = {}
    '''.format(
        github_user.user_login,
        github_user.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
