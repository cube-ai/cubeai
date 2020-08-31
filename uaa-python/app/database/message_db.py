from app.global_data.global_data import g
from app.domain.message import Message
from app.utils.pageable import gen_pageable


def create_message(message):
    sql = '''
        INSERT INTO message (
                sender,
                receiver,
                subject,
                content,
                url,
                urgent,
                viewed,
                deleted,
                created_date,
                modified_date
        ) VALUES ("{}", "{}", "{}", "{}", "{}", {}, {}, {}, "{}", "{}")
    '''.format(
        message.sender,
        message.receiver,
        message.subject,
        message.content,
        message.url if hasattr(message, 'url') else '',
        1 if hasattr(message, 'urgent') and message.urgent else 0,
        1 if message.viewed else 0,
        1 if message.deleted else 0,
        message.createdDate,
        message.modifiedDate
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM message limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def get_messages(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM message {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM message {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        message_list = []
        for record in records:
            message = Message()
            message.from_record(record)
            message_list.append(message.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], message_list


def find_one_by_id(id):
    sql = 'SELECT * FROM message WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    message_list = []
    for record in records:
        message = Message()
        message.from_record(record)
        message_list.append(message)

    return message_list[0] if len(message_list) > 0 else None


def delete_message(id):
    sql = 'DELETE FROM message WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_message_viewed(id, viewed):
    sql = '''
        UPDATE message SET 
            viewed = {}
        WHERE id = {}
    '''.format(
        1 if viewed else 0,
        id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_message_deleted(id, deleted):
    sql = '''
        UPDATE message SET 
            deleted = {}
        WHERE id = {}
    '''.format(
        1 if deleted else 0,
        id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        conn.close()


def get_unread_count(receiver, deleted):
    sql = 'SELECT COUNT(*)  FROM message WHERE receiver = "{}" and deleted = {} and viewed = 0'.format(
        receiver,
        1 if deleted else 0
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        count = cursor.fetchone()
    conn.close()

    return count[0]
