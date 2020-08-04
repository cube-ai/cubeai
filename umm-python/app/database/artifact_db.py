from app.globals.globals import g
from app.domain.artifact import Artifact


async def create_artifact(artifact):
    sql = '''
        INSERT INTO artifact (
            solution_uuid,
            name,
            jhi_type,
            url,
            file_size,
            created_date,
            modified_date
        ) VALUES ("{}", "{}", "{}", "{}", "{}", "{}", "{}")
    '''.format(
        artifact.solutionUuid,
        artifact.name,
        artifact.type,
        artifact.url,
        artifact.fileSize,
        artifact.createdDate,
        artifact.modifiedDate,
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_artifacts(where):
    sql = 'SELECT * FROM artifact {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    artifact_list = []
    for record in records:
        artifact = Artifact()
        artifact.from_record(record)
        artifact_list.append(artifact.__dict__)

    return artifact_list


async def delete_artifact(id):
    sql = 'DELETE FROM artifact WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
