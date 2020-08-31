from app.domain.composite_deployment import CompositeDeployment
from app.global_data.global_data import g
from app.utils.pageable import gen_pageable


def get_deployments(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM composite_deployment {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM composite_deployment {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        deployment_list = []
        for record in records:
            deployment = CompositeDeployment()
            deployment.from_record(record)
            deployment_list.append(deployment.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], deployment_list


def get_deployments_by_uuid(uuid):
    sql = 'SELECT * FROM composite_deployment WHERE uuid = "{}" limit 1'.format(uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    deployment_list = []
    for record in records:
        deployment = CompositeDeployment()
        deployment.from_record(record)
        deployment_list.append(deployment.__dict__)

    return deployment_list


def get_deployment_by_id(id):
    sql = 'SELECT * FROM composite_deployment WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    deployment_list = []
    for record in records:
        deployment = CompositeDeployment()
        deployment.from_record(record)
        deployment_list.append(deployment.__dict__)

    return deployment_list[0]


def create_deployment(deployment):
    sql = '''
        INSERT INTO composite_deployment (
            uuid,
            deployer,
            solution_uuid,
            solution_name,
            solution_author,
            k_8_s_port,
            is_public,
            status,
            created_date,
            modified_date,
            picture_url,
            star_count,
            call_count,
            display_order
        ) VALUES ( "{}", "{}", "{}", "{}", "{}", "{}", 
                    {}, 
                    "{}", "{}", "{}", "{}", "{}", "{}", "{}")
    '''.format(
        deployment.uuid,
        deployment.deployer,
        deployment.solutionUuid,
        deployment.solutionName,
        deployment.solutionAuthor,
        deployment.k8sPort,
        1 if deployment.isPublic else 0,
        deployment.status,
        deployment.createdDate,
        deployment.modifiedDate,
        deployment.pictureUrl,
        deployment.starCount,
        deployment.callCount,
        deployment.displayOrder
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_deployment_solutioninfo(deployment):
    sql = '''
        UPDATE composite_deployment SET 
            solution_name = "{}",
            picture_url = "{}"
        WHERE id = {}
    '''.format(
        deployment.solutionName,
        deployment.pictureUrl,
        deployment.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
    conn.close()


def update_deployment_admininfo(deployment):
    sql = '''
        UPDATE composite_deployment SET 
            subject_1 = "{}",
            subject_2 = "{}",
            subject_3 = "{}",
            display_order = "{}",
            modified_date = "{}"
        WHERE id = {}
    '''.format(
        deployment.subject1,
        deployment.subject2,
        deployment.subject3,
        deployment.displayOrder,
        deployment.modifiedDate,
        deployment.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_deployment_demourl(deployment):
    sql = '''
        UPDATE composite_deployment SET 
            demo_url = "{}"
        WHERE id = {}
    '''.format(
        deployment.demoUrl,
        deployment.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_deployment_status(deployment):
    sql = '''
        UPDATE composite_deployment SET 
            status = "{}"
        WHERE id = {}
    '''.format(
        deployment.status,
        deployment.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_deployment_star_count(deployment):
    sql = '''
        UPDATE composite_deployment SET 
            star_count = "{}"
        WHERE id = {}
    '''.format(
        deployment.starCount,
        deployment.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_deployment_call_count(deployment):
    sql = '''
        UPDATE composite_deployment SET 
            call_count = "{}"
        WHERE id = {}
    '''.format(
        deployment.callCount,
        deployment.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_deployment(id):
    sql = 'DELETE FROM composite_deployment WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
