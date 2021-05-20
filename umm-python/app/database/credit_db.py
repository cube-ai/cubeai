from app.global_data.global_data import g
from app.domain.credit import Credit


def create_credit(credit):
    sql = '''
        INSERT INTO credit (
            user_login,
            credit
        ) VALUES ("{}", "{}")
    '''.format(
        credit.userLogin,
        credit.credit
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_credit(credit):
    sql = '''
        UPDATE credit SET 
            credit = "{}"
        WHERE id = {}
    '''.format(
        credit.credit,
        credit.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_credits(user_login=None):
    where = '' if user_login is None else 'WHERE user_login = "{}"'.format(user_login)
    sql = 'SELECT * FROM credit {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    credit_list = []
    for record in records:
        credit = Credit()
        credit.from_record(record)
        credit_list.append(credit.__dict__)

    return credit_list


def get_credit(id):
    sql = 'SELECT * FROM credit WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    credit_list = []
    for record in records:
        credit = Credit()
        credit.from_record(record)
        credit_list.append(credit.__dict__)

    return credit_list[0]


def get_user_credit(user_login):
    sql = 'SELECT * FROM credit WHERE user_login = "{}" limit 1'.format(user_login)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    credit_list = []
    for record in records:
        credit = Credit()
        credit.from_record(record)
        credit_list.append(credit.__dict__)

    return credit_list[0] if len(credit_list) > 0 else None
