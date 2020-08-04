from app.globals.globals import g
from app.domain.application import Application
from app.utils.pageable import gen_pageable


async def get_applications(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM application {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM application {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            application_list = []
            for record in records:
                application = Application()
                application.from_record(record)
                application_list.append(application.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], application_list


async def get_applications_by_uuid(uuid):
    sql = 'SELECT * FROM application WHERE uuid = "{}" limit 1'.format(uuid)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    application_list = []
    for record in records:
        application = Application()
        application.from_record(record)
        application_list.append(application.__dict__)

    return application_list


async def get_application(id):
    sql = 'SELECT * FROM application WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    application_list = []
    for record in records:
        application = Application()
        application.from_record(record)
        application_list.append(application.__dict__)

    return application_list[0]


async def create_application(application):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_application(application):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_application(id):
    sql = 'DELETE FROM application WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
