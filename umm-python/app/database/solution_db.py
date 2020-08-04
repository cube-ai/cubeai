from app.globals.globals import g
from app.domain.solution import Solution
from app.utils.pageable import gen_pageable


async def get_solutions(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM solution {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM solution {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            solution_list = []
            for record in records:
                solution = Solution()
                solution.from_record(record)
                solution_list.append(solution.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], solution_list


async def get_solutions_by_uuid(uuid):
    sql = 'SELECT * FROM solution WHERE uuid = "{}" limit 1'.format(uuid)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    solution_list = []
    for record in records:
        solution = Solution()
        solution.from_record(record)
        solution_list.append(solution.__dict__)

    return solution_list


async def get_solution_by_id(id):
    sql = 'SELECT * FROM solution WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    solution_list = []
    for record in records:
        solution = Solution()
        solution.from_record(record)
        solution_list.append(solution.__dict__)

    return solution_list[0]


async def create_solution(solution):
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
            download_count,
            comment_count,
            star_count
        ) VALUES ( "{}", "{}", "{}", "{}", "{}", "{}", 
                    {}, 
                    "{}", "{}", "{}", "{}", "{}", "{}", "{}")
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
        solution.starCount
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_baseinfo(solution):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_name(solution):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_picture_url(solution):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_active(solution):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_admininfo(solution):
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

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_star_count(solution):
    sql = '''
        UPDATE solution SET 
            star_count = "{}"
        WHERE id = {}
    '''.format(
        solution.starCount,
        solution.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_comment_count(solution):
    sql = '''
        UPDATE solution SET 
            comment_count = "{}"
        WHERE id = {}
    '''.format(
        solution.commentCount,
        solution.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_view_count(solution):
    sql = '''
        UPDATE solution SET 
            view_count = "{}"
        WHERE id = {}
    '''.format(
        solution.viewCount,
        solution.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_solution_download_count(solution):
    sql = '''
        UPDATE solution SET 
            download_count = "{}"
        WHERE id = {}
    '''.format(
        solution.downloadCount,
        solution.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_solution(id):
    sql = 'DELETE FROM solution WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
