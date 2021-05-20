from app.global_data.global_data import g
from app.domain.task import Task
from app.utils.pageable import gen_pageable


def create_task(task):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM task limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def update_task(task):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_tasks(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM task {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM task {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        task_list = []
        for record in records:
            task = Task()
            task.from_record(record)
            task_list.append(task.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], task_list


def get_task(id):
    sql = 'SELECT * FROM task WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    task_list = []
    for record in records:
        task = Task()
        task.from_record(record)
        task_list.append(task.__dict__)

    return task_list[0]


def delete_task(id):
    sql = 'DELETE FROM task WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
