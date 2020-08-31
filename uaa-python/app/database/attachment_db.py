from app.global_data.global_data import g
from app.domain.attachment import Attachment
from app.utils.pageable import gen_pageable


def get_attachments(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM attachment {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM attachment {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        attachment_list = []
        for record in records:
            attachment = Attachment()
            attachment.from_record(record)
            attachment_list.append(attachment.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], attachment_list


def get_attachment(id):
    sql = 'SELECT * FROM attachment WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    attachment_list = []
    for record in records:
        attachment = Attachment()
        attachment.from_record(record)
        attachment_list.append(attachment)

    return attachment_list[0]


def create_attachment(attachment):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM attachment limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def delete_attachment(id):
    sql = 'DELETE FROM attachment WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
