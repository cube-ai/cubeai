from app.globals.globals import g


async def create_authority(name):
    sql = '''
        INSERT INTO authority (
                name
        ) VALUES ("{}")
    '''.format(name)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_authority(name):
    sql = 'DELETE FROM authority WHERE name = "{}"'.format(name)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_authorities():
    sql = 'SELECT name FROM authority'

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            authorities = []
            for record in records:
                 authorities.append(record[0])

    return authorities
