from app.globals.globals import g
from app.domain.description import Description


async def create_description(description):
    sql = '''
        INSERT INTO description (
            solution_uuid,
            author_login,
            content
        ) VALUES ("{}", "{}", "{}")
    '''.format(
        description.solutionUuid,
        description.authorLogin,
        description.content
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_descriptions(solution_uuid):
    sql = 'SELECT * FROM description WHERE solution_uuid = "{}"'.format(solution_uuid)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    description_list = []
    for record in records:
        description = Description()
        description.from_record(record)
        description_list.append(description.__dict__)

    return description_list


async def get_description(id):
    sql = 'SELECT * FROM description WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    description_list = []
    for record in records:
        description = Description()
        description.from_record(record)
        description_list.append(description.__dict__)

    return description_list[0]


async def update_description_content(description):
    sql = r'''
        UPDATE description SET 
            content = '{}'
        WHERE id = {}
    '''.format(
        description.content,
        description.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_description(id):
    sql = 'DELETE FROM description WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


