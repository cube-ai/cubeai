from app.global_data.global_data import g
from app.domain.document import Document


def create_document(document):
    sql = '''
        INSERT INTO document (
            solution_uuid,
            author_login,
            name,
            url,
            file_size,
            created_date,
            modified_date
        ) VALUES ("{}", "{}", "{}", "{}", "{}", "{}", "{}")
    '''.format(
        document.solutionUuid,
        document.authorLogin,
        document.name,
        document.url,
        document.fileSize,
        document.createdDate,
        document.modifiedDate,
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM document limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def get_documents(where):
    sql = 'SELECT * FROM document {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    document_list = []
    for record in records:
        document = Document()
        document.from_record(record)
        document_list.append(document.__dict__)

    return document_list


def get_document(id):
    sql = 'SELECT * FROM document WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    document_list = []
    for record in records:
        document = Document()
        document.from_record(record)
        document_list.append(document.__dict__)

    return document_list[0]


def delete_document(id):
    sql = 'DELETE FROM document WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
