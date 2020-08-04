from app.globals.globals import g
from app.domain.comment import Comment
from app.utils.pageable import gen_pageable


async def create_comment(comment):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_comments(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM comment {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*)  FROM comment {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            comment_list = []
            for record in records:
                comment = Comment()
                comment.from_record(record)
                comment_list.append(comment.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], comment_list


async def get_comment(id):
    sql = 'SELECT * FROM comment WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    comment_list = []
    for record in records:
        comment = Comment()
        comment.from_record(record)
        comment_list.append(comment.__dict__)

    return comment_list[0]


async def delete_comment(id):
    sql = 'DELETE FROM comment WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def get_comment_count(solution_uuid):

    sql = 'SELECT COUNT(*) FROM comment WHERE solution_uuid = "{}"'.format(solution_uuid)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            comment_count = cursor.fetchone()

    return comment_count[0]
