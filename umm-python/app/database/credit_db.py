from app.globals.globals import g
from app.domain.credit import Credit


async def create_credit(credit):
    sql = '''
        INSERT INTO credit (
            user_login,
            credit
        ) VALUES ("{}", "{}")
    '''.format(
        credit.userLogin,
        credit.credit
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_credit(credit):
    sql = '''
        UPDATE credit SET 
            credit = "{}"
        WHERE id = {}
    '''.format(
        credit.credit,
        credit.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_credits(user_login=None):
    where = '' if user_login is None else 'WHERE user_login = "{}"'.format(user_login)
    sql = 'SELECT * FROM credit {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    credit_list = []
    for record in records:
        credit = Credit()
        credit.from_record(record)
        credit_list.append(credit.__dict__)

    return credit_list


async def get_credit(id):
    sql = 'SELECT * FROM credit WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    credit_list = []
    for record in records:
        credit = Credit()
        credit.from_record(record)
        credit_list.append(credit.__dict__)

    return credit_list[0]


async def get_user_credit(user_login):
    sql = 'SELECT * FROM credit WHERE user_login = "{}" limit 1'.format(user_login)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    credit_list = []
    for record in records:
        credit = Credit()
        credit.from_record(record)
        credit_list.append(credit.__dict__)

    return credit_list[0] if len(credit_list) > 0 else None
