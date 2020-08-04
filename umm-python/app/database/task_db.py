from app.globals.globals import g
from app.domain.task import Task
from app.utils.pageable import gen_pageable


async def create_task(task):
    sql = '''
        INSERT INTO task (
            uuid,
            task_name,
            task_type,
            task_status,
            task_progress,
            description,
            target_uuid,
            user_login,
            start_date,
            end_date
        ) VALUES ("{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}")
    '''.format(
        task.uuid,
        task.taskName,
        task.taskType,
        task.taskStatus,
        task.taskProgress,
        task.description,
        task.targetUuid,
        task.userLogin,
        task.startDate,
        task.startDate
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_task(task):
    sql = '''
        UPDATE task SET 
            task_status = "{}",
            task_progress = "{}",
            description = "{}",
            end_date = "{}"
        WHERE id = {}
    '''.format(
        task.taskStatus,
        task.taskProgress,
        task.description,
        task.endDate,
        task.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_tasks(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM task {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM task {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            task_list = []
            for record in records:
                task = Task()
                task.from_record(record)
                task_list.append(task.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], task_list


async def get_task(id):
    sql = 'SELECT * FROM task WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    task_list = []
    for record in records:
        task = Task()
        task.from_record(record)
        task_list.append(task.__dict__)

    return task_list[0]


async def delete_task(id):
    sql = 'DELETE FROM task WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
