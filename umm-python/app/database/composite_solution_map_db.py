from app.globals.globals import g
from app.domain.composite_solution_map import CompositeSolutionMap


async def create_composite_solution_map(composite_solution_map):
    sql = '''
        INSERT INTO composite_solution_map (
            parent_uuid,
            child_uuid
        ) VALUES ("{}", "{}")
    '''.format(
        composite_solution_map.parentUuid,
        composite_solution_map.childUuid
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_composite_solution_map(composite_solution_map):
    sql = '''
        UPDATE composite_solution_map SET 
            parent_uuid = "{}",
            child_uuid = "{}"
        WHERE id = {}
    '''.format(
        composite_solution_map.parentUuid,
        composite_solution_map.childUuid,
        composite_solution_map.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_composite_solution_maps(parent_uuid=None):
    where = '' if parent_uuid is None else 'WHERE parent_uuid = "{}"'.format(parent_uuid)
    sql = 'SELECT * FROM composite_solution_map {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    composite_solution_map_list = []
    for record in records:
        composite_solution_map = CompositeSolutionMap()
        composite_solution_map.from_record(record)
        composite_solution_map_list.append(composite_solution_map.__dict__)

    return composite_solution_map_list


async def get_composite_solution_map(id):
    sql = 'SELECT * FROM composite_solution_map WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    composite_solution_map_list = []
    for record in records:
        composite_solution_map = CompositeSolutionMap()
        composite_solution_map.from_record(record)
        composite_solution_map_list.append(composite_solution_map.__dict__)

    return composite_solution_map_list[0]


async def delete_composite_solution_map(id):
    sql = 'DELETE FROM composite_solution_map WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()

