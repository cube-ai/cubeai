from app.globals.globals import g
from app.domain.attachment import Attachment
from app.utils.pageable import gen_pageable


async def get_attachments(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM attachment {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM attachment {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            attachment_list = []
            for record in records:
                attachment = Attachment()
                attachment.from_record(record)
                attachment_list.append(attachment.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], attachment_list


async def get_attachment(id):
    sql = 'SELECT * FROM attachment WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    attachment_list = []
    for record in records:
        attachment = Attachment()
        attachment.from_record(record)
        attachment_list.append(attachment)

    return attachment_list[0]


async def create_attachment(attachment):
    sql = '''
        INSERT INTO attachment (
            author_login,
            name,
            url,
            file_size,
            created_date,
            modified_date
        ) VALUES ( '{}', '{}', '{}', '{}', '{}', '{}')
    '''.format(
        attachment.authorLogin,
        attachment.name,
        attachment.url,
        attachment.fileSize,
        attachment.createdDate,
        attachment.modifiedDate
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_attachment(id):
    sql = 'DELETE FROM attachment WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
