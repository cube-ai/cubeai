from app.globals.globals import g
from app.domain.task_step import TaskStep


async def create_task_step(task_step):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_task_steps(where):
    sql = 'SELECT * FROM task_step {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    task_step_list = []
    for record in records:
        task_step = TaskStep()
        task_step.from_record(record)
        task_step_list.append(task_step.__dict__)

    return task_step_list

