from app.global_data.global_data import g
from app.domain.comment import Comment
from app.utils.pageable import gen_pageable


def create_comment(comment):
    sql = '''
        INSERT INTO comment (
            uuid,
            user_login,
            solution_uuid,
            parent_uuid,
            comment_text,
            jhi_level,
            created_date,
            modified_date
        ) VALUES ("{}", "{}", "{}", "{}", "{}", "{}", "{}", "{}")
    '''.format(
        comment.uuid,
        comment.userLogin,
        comment.solutionUuid,
        comment.parentUuid,
        comment.commentText,
        comment.level,
        comment.createdDate,
        comment.modifiedDate
    )

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM star limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def get_comments(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM comment {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM comment {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        comment_list = []
        for record in records:
            comment = Comment()
            comment.from_record(record)
            comment_list.append(comment.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], comment_list


def get_comment(id):
    sql = 'SELECT * FROM comment WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    comment_list = []
    for record in records:
        comment = Comment()
        comment.from_record(record)
        comment_list.append(comment.__dict__)

    return comment_list[0]


def delete_comment(id):
    sql = 'DELETE FROM comment WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def get_comment_count(solution_uuid):

    sql = 'SELECT COUNT(*) FROM comment WHERE solution_uuid = "{}"'.format(solution_uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        comment_count = cursor.fetchone()
    conn.close()

    return comment_count[0]
