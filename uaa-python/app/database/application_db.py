from app.global_data.global_data import g
from app.domain.application import Application
from app.utils.pageable import gen_pageable


def get_applications(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM application {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM application {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        application_list = []
        for record in records:
            application = Application()
            application.from_record(record)
            application_list.append(application.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], application_list


def get_applications_by_uuid(uuid):
    sql = 'SELECT * FROM application WHERE uuid = "{}" limit 1'.format(uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    application_list = []
    for record in records:
        application = Application()
        application.from_record(record)
        application_list.append(application.__dict__)

    return application_list


def get_application(id):
    sql = 'SELECT * FROM application WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    application_list = []
    for record in records:
        application = Application()
        application.from_record(record)
        application_list.append(application.__dict__)

    return application_list[0]


def create_application(application):
    sql = '''
        INSERT INTO application (
            uuid,
            name,
            summary,
            url,
            picture_url,
            subject_1,
            subject_2,
            subject_3,
            owner,
            need_roles,
            display_order,
            created_by,
            modified_by,
            created_date,
            modified_date
        ) VALUES ( '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}')
    '''.format(
        application.uuid,
        application.name,
        application.summary,
        application.url,
        application.pictureUrl,
        application.subject1,
        application.subject2,
        application.subject3,
        application.owner,
        application.needRoles,
        application.displayOrder,
        application.createdBy,
        application.modifiedBy,
        application.createdDate,
        application.modifiedDate
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM application limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def update_application(application):
    sql = '''
        UPDATE application SET 
            uuid = '{}',
            name = '{}',
            summary = '{}',
            url = '{}',
            picture_url = '{}',
            subject_1 = '{}',
            subject_2 = '{}',
            subject_3 = '{}',
            owner = '{}',
            need_roles = '{}',
            display_order = '{}',
            created_by = '{}',
            modified_by = '{}',
            created_date = '{}',
            modified_date = '{}'
        WHERE id = {}
    '''.format(
        application.uuid,
        application.name,
        application.summary,
        application.url,
        application.pictureUrl,
        application.subject1,
        application.subject2,
        application.subject3,
        application.owner,
        application.needRoles,
        application.displayOrder,
        application.createdBy,
        application.modifiedBy,
        application.createdDate,
        application.modifiedDate,
        application.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_application(id):
    sql = 'DELETE FROM application WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
