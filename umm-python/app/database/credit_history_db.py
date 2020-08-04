from app.globals.globals import g
from app.domain.credit_history import CreditHistory
from app.utils.pageable import gen_pageable


async def create_credit_history(credit_history):
    sql = '''
        INSERT INTO credit_history (
            user_login,
            credit_plus,
            current_credit,
            jhi_comment,
            modify_date
        ) VALUES ("{}", "{}", "{}", "{}", "{}")
    '''.format(
        credit_history.userLogin,
        credit_history.creditPlus,
        credit_history.currentCredit,
        credit_history.comment,
        credit_history.modifyDate
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_credit_historys(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM credit_history {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM credit_history {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

            credit_history_list = []
            for record in records:
                credit_history = CreditHistory()
                credit_history.from_record(record)
                credit_history_list.append(credit_history.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], credit_history_list


async def get_credit_history(id):
    sql = 'SELECT * FROM credit_history WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    credit_list = []
    for record in records:
        credit_history = CreditHistory()
        credit_history.from_record(record)
        credit_list.append(credit_history.__dict__)

    return credit_list[0]

