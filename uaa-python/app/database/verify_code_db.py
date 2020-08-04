from app.globals.globals import g
from app.domain.verify_code import VerifyCode


async def create_verify_code(verify_code):
    sql = '''
        INSERT INTO verify_code (
            code,
            expire
        ) VALUES ("{}", "{}")
    '''.format(
        verify_code.code,
        verify_code.expire
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
            await cursor.execute('SELECT last_insert_id() FROM verify_code limit 1')

    return cursor.fetchone()[0]


async def update_verify_code(verify_code):
    sql = '''
        UPDATE verify_code SET 
            verify_code_status = "{}",
            verify_code_progress = "{}",
            description = "{}",
            end_date = "{}"
        WHERE id = {}
    '''.format(
        verify_code.verify_codeStatus,
        verify_code.verify_codeProgress,
        verify_code.description,
        verify_code.endDate,
        verify_code.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_verify_code(id):
    sql = 'SELECT * FROM verify_code WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    verify_code_list = []
    for record in records:
        verify_code = VerifyCode()
        verify_code.from_record(record)
        verify_code_list.append(verify_code.__dict__)

    return verify_code_list[0] if len(verify_code_list) > 0 else None


async def delete_verify_code(id):
    sql = 'DELETE FROM verify_code WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def cleanup_all():
    sql = 'DELETE FROM verify_code'

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()

