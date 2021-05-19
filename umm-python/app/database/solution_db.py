from app.global_data.global_data import g
from app.domain.solution import Solution
from app.utils.pageable import gen_pageable


def get_solutions(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM solution {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM solution {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        solution_list = []
        for record in records:
            solution = Solution()
            solution.from_record(record)
            solution_list.append(solution.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], solution_list


def get_solutions_by_uuid(uuid):
    sql = 'SELECT * FROM solution WHERE uuid = "{}" limit 1'.format(uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    solution_list = []
    for record in records:
        solution = Solution()
        solution.from_record(record)
        solution_list.append(solution.__dict__)

    return solution_list


def get_solution_by_id(id):
    sql = 'SELECT * FROM solution WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    solution_list = []
    for record in records:
        solution = Solution()
        solution.from_record(record)
        solution_list.append(solution.__dict__)

    return solution_list[0]


def create_solution(solution):
    sql = '''
        INSERT INTO solution (
            uuid,
            author_login,
            author_name,
            name,
            version,
            display_order,
            active,
            company,
            created_date,
            modified_date,
            view_count,
            star_count,
            comment_count,
            call_count,
            deploy_status,
            deploy_date,
            has_web
        ) VALUES ( "{}", "{}", "{}", "{}", "{}", "{}", 
                    {}, 
                    "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}", 
                    {})
    '''.format(
        solution.uuid,
        solution.authorLogin,
        solution.authorName,
        solution.name,
        solution.version,
        solution.displayOrder,
        1 if solution.active else 0,
        solution.company,
        solution.createdDate,
        solution.modifiedDate,
        solution.viewCount,
        solution.starCount,
        solution.commentCount,
        solution.callCount,
        solution.deployStatus,
        solution.deployDate,
        1 if solution.hasWeb else 0
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM solution limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id

def update_solution_baseinfo(solution):
    sql = '''
        UPDATE solution SET 
            name = "{}",
            company = "{}",
            version = "{}",
            summary = "{}",
            tag_1 = "{}",
            tag_2 = "{}",
            tag_3 = "{}",
            model_type = "{}",
            toolkit_type = "{}",
            modified_date = "{}"
        WHERE id = {}
    '''.format(
        solution.name,
        solution.company,
        solution.version,
        solution.summary,
        solution.tag1,
        solution.tag2,
        solution.tag3,
        solution.modelType,
        solution.toolkitType,
        solution.modifiedDate,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_name(solution):
    sql = '''
        UPDATE solution SET 
            name = "{}",
            modified_date = "{}"
        WHERE id = {}
    '''.format(
        solution.name,
        solution.modifiedDate,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_picture_url(solution):
    sql = '''
        UPDATE solution SET 
            picture_url = "{}",
            modified_date = "{}"
        WHERE id = {}
    '''.format(
        solution.pictureUrl,
        solution.modifiedDate,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_active(solution):
    sql = '''
        UPDATE solution SET 
            active = {},
            modified_date = "{}"
        WHERE id = {}
    '''.format(
        1 if solution.active else 0,
        solution.modifiedDate,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_admininfo(solution):
    sql = '''
        UPDATE solution SET 
            subject_1 = "{}",
            subject_2 = "{}",
            subject_3 = "{}",
            display_order = "{}",
            modified_date = "{}"
        WHERE id = {}
    '''.format(
        solution.subject1,
        solution.subject2,
        solution.subject3,
        solution.displayOrder,
        solution.modifiedDate,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_star_count(solution):
    sql = '''
        UPDATE solution SET 
            star_count = "{}"
        WHERE id = {}
    '''.format(
        solution.starCount,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_view_count(solution):
    sql = '''
        UPDATE solution SET 
            view_count = "{}"
        WHERE id = {}
    '''.format(
        solution.viewCount,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_comment_count(solution):
    sql = '''
        UPDATE solution SET 
            comment_count = "{}"
        WHERE id = {}
    '''.format(
        solution.commentCount,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_deploy_info(solution):
    sql = '''
        UPDATE solution SET 
            deployer = "{}",
            deploy_status = "{}",
            k_8_s_port = "{}",
            deploy_date = "{}"
        WHERE id = {}
    '''.format(
        solution.deployer,
        solution.deployStatus,
        solution.k8sPort,
        solution.deployDate,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_deploy_status(solution):
    sql = '''
        UPDATE solution SET 
            deploy_status = "{}"
        WHERE id = {}
    '''.format(
        solution.deployStatus,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_call_count(solution):
    sql = '''
        UPDATE solution SET 
            call_count = "{}"
        WHERE id = {}
    '''.format(
        solution.callCount,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_solution(id):
    sql = 'DELETE FROM solution WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
