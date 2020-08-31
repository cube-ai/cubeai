from app.global_data.global_data import g
from app.domain.composite_solution_map import CompositeSolutionMap


def create_composite_solution_map(composite_solution_map):
    sql = '''
        INSERT INTO composite_solution_map (
            parent_uuid,
            child_uuid
        ) VALUES ("{}", "{}")
    '''.format(
        composite_solution_map.parentUuid,
        composite_solution_map.childUuid
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_composite_solution_map(composite_solution_map):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_composite_solution_maps(parent_uuid=None):
    where = '' if parent_uuid is None else 'WHERE parent_uuid = "{}"'.format(parent_uuid)
    sql = 'SELECT * FROM composite_solution_map {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    composite_solution_map_list = []
    for record in records:
        composite_solution_map = CompositeSolutionMap()
        composite_solution_map.from_record(record)
        composite_solution_map_list.append(composite_solution_map.__dict__)

    return composite_solution_map_list


def get_composite_solution_map(id):
    sql = 'SELECT * FROM composite_solution_map WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    composite_solution_map_list = []
    for record in records:
        composite_solution_map = CompositeSolutionMap()
        composite_solution_map.from_record(record)
        composite_solution_map_list.append(composite_solution_map.__dict__)

    return composite_solution_map_list[0]


def delete_composite_solution_map(id):
    sql = 'DELETE FROM composite_solution_map WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
