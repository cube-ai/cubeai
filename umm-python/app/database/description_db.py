from app.global_data.global_data import g
from app.domain.description import Description


def create_description(description):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_descriptions(solution_uuid):
    sql = 'SELECT * FROM description WHERE solution_uuid = "{}"'.format(solution_uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    description_list = []
    for record in records:
        description = Description()
        description.from_record(record)
        description_list.append(description.__dict__)

    return description_list


def get_description(id):
    sql = 'SELECT * FROM description WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    description_list = []
    for record in records:
        description = Description()
        description.from_record(record)
        description_list.append(description.__dict__)

    return description_list[0]


def update_description_content(description):
    sql = r'''
        UPDATE description SET 
            content = '{}'
        WHERE id = {}
    '''.format(
        description.content,
        description.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_description(id):
    sql = 'DELETE FROM description WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
