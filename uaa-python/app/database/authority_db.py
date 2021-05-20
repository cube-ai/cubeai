from app.global_data.global_data import g


def create_authority(name):
    sql = '''
        INSERT INTO authority (
                name
        ) VALUES ("{}")
    '''.format(name)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_authority(name):
    sql = 'DELETE FROM authority WHERE name = "{}"'.format(name)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_authorities():
    sql = 'SELECT name FROM authority'

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        authorities = []
        for record in records:
            authorities.append(record[0])
    conn.close()

    return authorities
