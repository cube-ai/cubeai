from app.global_data.global_data import g
from app.domain.article import Article
from app.utils.pageable import gen_pageable


def get_articles(where, pageable):
    pageable = gen_pageable(pageable)
    sql = 'SELECT * FROM article {} {}'.format(where, pageable)
    sql_total_count = 'SELECT COUNT(*) FROM article {}'.format(where)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
        article_list = []
        for record in records:
            article = Article()
            article.from_record(record)
            article_list.append(article.__dict__)

        cursor.execute(sql_total_count)
        total_count = cursor.fetchone()
    conn.close()

    return total_count[0], article_list


def get_articles_by_uuid(uuid):
    sql = 'SELECT * FROM article WHERE uuid = "{}" limit 1'.format(uuid)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    article_list = []
    for record in records:
        article = Article()
        article.from_record(record)
        article_list.append(article.__dict__)

    return article_list


def get_article(id):
    sql = 'SELECT * FROM article WHERE id = "{}" limit 1'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        records = cursor.fetchall()
    conn.close()

    article_list = []
    for record in records:
        article = Article()
        article.from_record(record)
        article_list.append(article.__dict__)

    return article_list[0]


def create_article(article):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
        cursor.execute('SELECT last_insert_id() FROM article limit 1')
        id = cursor.fetchone()[0]
    conn.close()

    return id


def update_article(article):
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

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()


def delete_article(id):
    sql = 'DELETE FROM article WHERE id = "{}"'.format(id)

    conn = g.db.pool.connection()
    with conn.cursor() as cursor:
        cursor.execute(sql)
        conn.commit()
    conn.close()
