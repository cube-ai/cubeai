from app.global_data.global_data import g
from app.domain.task_step import TaskStep


def create_task_step(task_step):
    sql = '''
        INSERT INTO task_step (
            task_uuid,
            step_name,
            step_status,
            step_progress,
            description,
            step_date
        ) VALUES ("{}", "{}", "{}", "{}", "{}", "{}")
    '''.format(
        task_step.taskUuid,
        task_step.stepName,
        task_step.stepStatus,
        task_step.stepProgress,
        task_step.description,
        task_step.stepDate,
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM task_step limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def get_task_steps(where):
    sql = 'SELECT * FROM task_step {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    task_step_list = []
    for record in records:
        task_step = TaskStep()
        task_step.from_record(record)
        task_step_list.append(task_step.__dict__)

    return task_step_list


def delete_task_steps(task_uuid, start_progress, end_progress):
    sql = 'DELETE FROM task_step WHERE task_uuid = "{}" and step_progress > "{}" and step_progress < "{}"'.format(task_uuid, start_progress, end_progress)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
