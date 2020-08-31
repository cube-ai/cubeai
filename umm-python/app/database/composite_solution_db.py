from app.global_data.global_data import g
from app.domain.composite_solution import CompositeSolution
from app.utils.pageable import gen_pageable


def get_solutions(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM composite_solution {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM composite_solution {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        solution_list = []
        for record in records:
            solution = CompositeSolution()
            solution.from_record(record)
            solution_list.append(solution.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], solution_list


def get_solutions_by_uuid(uuid):
    sql = 'SELECT * FROM composite_solution WHERE uuid = "{}" limit 1'.format(uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    solution_list = []
    for record in records:
        solution = CompositeSolution()
        solution.from_record(record)
        solution_list.append(solution.__dict__)

    return solution_list


def get_solution_by_id(id):
    sql = 'SELECT * FROM composite_solution WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    solution_list = []
    for record in records:
        solution = CompositeSolution()
        solution.from_record(record)
        solution_list.append(solution.__dict__)

    return solution_list[0]


def create_solution(solution):
    sql = '''
        INSERT INTO composite_solution (
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
            download_count,
            comment_count,
            star_count,
            toolkit_type
        ) VALUES ( "{}", "{}", "{}", "{}", "{}", "{}", 
                    {}, 
                    "{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}")
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
        solution.downloadCount,
        solution.commentCount,
        solution.starCount,
        solution.toolkitType
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def update_solution_baseinfo(solution):
    sql = '''
        UPDATE composite_solution SET 
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
        UPDATE composite_solution SET 
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
        UPDATE composite_solution SET 
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
        UPDATE composite_solution SET 
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
        UPDATE composite_solution SET 
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
        UPDATE composite_solution SET 
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


def update_solution_comment_count(solution):
    sql = '''
        UPDATE composite_solution SET 
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


def update_solution_view_count(solution):
    sql = '''
        UPDATE composite_solution SET 
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


def update_solution_download_count(solution):
    sql = '''
        UPDATE composite_solution SET 
            download_count = "{}"
        WHERE id = {}
    '''.format(
        solution.downloadCount,
        solution.id
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_solution(id):
    sql = 'DELETE FROM composite_solution WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
