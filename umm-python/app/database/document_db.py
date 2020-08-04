from app.globals.globals import g
from app.domain.document import Document


async def create_document(document):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_documents(where):
    sql = 'SELECT * FROM document {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    document_list = []
    for record in records:
        document = Document()
        document.from_record(record)
        document_list.append(document.__dict__)

    return document_list


async def get_document(id):
    sql = 'SELECT * FROM document WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    document_list = []
    for record in records:
        document = Document()
        document.from_record(record)
        document_list.append(document.__dict__)

    return document_list[0]


async def delete_document(id):
    sql = 'DELETE FROM document WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
