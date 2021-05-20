from app.global_data.global_data import g
from app.domain.artifact import Artifact


def create_artifact(artifact):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM artifact limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def get_artifacts(where):
    sql = 'SELECT * FROM artifact {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    artifact_list = []
    for record in records:
        artifact = Artifact()
        artifact.from_record(record)
        artifact_list.append(artifact.__dict__)

    return artifact_list


def delete_artifact(id):
    sql = 'DELETE FROM artifact WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
