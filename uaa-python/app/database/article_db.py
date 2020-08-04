from app.globals.globals import g
from app.domain.article import Article
from app.utils.pageable import gen_pageable


async def get_articles(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM article {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM article {}'.format(where)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()
            article_list = []
            for record in records:
                article = Article()
                article.from_record(record)
                article_list.append(article.__dict__)

            await cursor.execute(sql_total_count)
            total_count = cursor.fetchone()

    return total_count[0], article_list


async def get_articles_by_uuid(uuid):
    sql = 'SELECT * FROM article WHERE uuid = "{}" limit 1'.format(uuid)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    article_list = []
    for record in records:
        article = Article()
        article.from_record(record)
        article_list.append(article.__dict__)

    return article_list


async def get_article(id):
    sql = 'SELECT * FROM article WHERE id = "{}" limit 1'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            records = cursor.fetchall()

    article_list = []
    for record in records:
        article = Article()
        article.from_record(record)
        article_list.append(article.__dict__)

    return article_list[0]


async def create_article(article):
    sql = '''
        INSERT INTO article (
            uuid,
            author_login,
            author_name,
            subject_1,
            subject_2,
            subject_3,
            title,
            summary,
            tag_1,
            tag_2,
            tag_3,
            picture_url,
            content,
            display_order,
            created_date,
            modified_date
        ) VALUES ( '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}')
    '''.format(
        article.uuid,
        article.authorLogin,
        article.authorName,
        article.subject1,
        article.subject2,
        article.subject3,
        article.title,
        article.summary,
        article.tag1,
        article.tag2,
        article.tag3,
        article.pictureUrl,
        article.content,
        article.displayOrder,
        article.createdDate,
        article.modifiedDate
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def update_article(article):
    sql = '''
        UPDATE article SET 
            uuid = '{}',
            author_login = '{}',
            author_name = '{}',
            subject_1 = '{}',
            subject_2 = '{}',
            subject_3 = '{}',
            title = '{}',
            summary = '{}',
            tag_1 = '{}',
            tag_2 = '{}',
            tag_3 = '{}',
            picture_url = '{}',
            content = '{}',
            display_order = '{}',
            created_date = '{}',
            modified_date = '{}'
        WHERE id = {}
    '''.format(
        article.uuid,
        article.authorLogin,
        article.authorName,
        article.subject1,
        article.subject2,
        article.subject3,
        article.title,
        article.summary,
        article.tag1,
        article.tag2,
        article.tag3,
        article.pictureUrl,
        article.content,
        article.displayOrder,
        article.createdDate,
        article.modifiedDate,
        article.id
    )

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()


async def delete_article(id):
    sql = 'DELETE FROM article WHERE id = "{}"'.format(id)

    async with await g.db.pool.Connection() as conn:
        async with conn.cursor() as cursor:
            await cursor.execute(sql)
            await conn.commit()
