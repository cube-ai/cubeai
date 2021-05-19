from app.service import token_service
from app.domain.article import Article
from app.database import article_db
from app.utils import mytime


def create_article(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_CONTENT')
    if not has_role:
        raise Exception('403 Forbidden')

    article = Article()
    article.__dict__ = args.get('article')
    article.complete_attrs()
    article.createdDate = mytime.now()
    article.modifiedDate = mytime.now()

    id = article_db.create_article(article)
    return id


def update_article(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_CONTENT')
    if not has_role:
        raise Exception('403 Forbidden')

    article = Article()
    article.__dict__ = args.get('article')
    article.modifiedDate = mytime.now()

    article_db.update_article(article)
    return 0


def get_articles(**args):
    uuid = args.get('uuid')
    authorLogin = args.get('authorLogin')
    subject1 = args.get('subject1')
    subject2 = args.get('subject2')
    subject3 = args.get('subject3')
    title = args.get('title')
    tag1 = args.get('tag1')
    tag2 = args.get('tag2')
    tag3 = args.get('tag3')
    filter = args.get('filter')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    if uuid is not None:
        result = article_db.get_articles_by_uuid(uuid)
        return {
            'articles': result,
        }

    where1 = ''
    if authorLogin is not None:
        where1 += 'and author_login = "{}" '.format(authorLogin)
    if subject1 is not None:
        where1 += 'and subject_1 = "{}" '.format(subject1)
    if subject2 is not None:
        where1 += 'and subject_2 = "{}" '.format(subject2)
    if subject3 is not None:
        where1 += 'and subject_3 = "{}" '.format(subject3)
    if title is not None:
        where1 += 'and title = "{}" '.format(title)
    if tag1 is not None:
        where1 += 'and tag_1 = "{}" '.format(tag1)
    if tag2 is not None:
        where1 += 'and tag_2 = "{}" '.format(tag2)
    if tag3 is not None:
        where1 += 'and tag_3 = "{}" '.format(tag3)

    where1 = where1[4:]

    where2 = ''
    if filter is not None:
        where2 += 'author_login like "%{}%"'.format(filter)
        where2 += ' or author_name like "%{}%"'.format(filter)
        where2 += ' or subject_1 like "%{}%"'.format(filter)
        where2 += ' or subject_2 like "%{}%"'.format(filter)
        where2 += ' or subject_3 like "%{}%"'.format(filter)
        where2 += ' or title like "%{}%"'.format(filter)
        where2 += ' or tag_1 like "%{}%"'.format(filter)
        where2 += ' or tag_2 like "%{}%"'.format(filter)
        where2 += ' or tag_3 like "%{}%"'.format(filter)

    where = ''
    if where1:
        where += 'and ({})'.format(where1)
    if where2:
        where += 'and ({})'.format(where2)
    if where:
        where = where[4:]

    if where != '':
        where = 'WHERE ' + where
    total, results = article_db.get_articles(where, pageable)

    return {
        'total': total,
        'results': results,
    }


def find_article(id, http_request):
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_CONTENT')
    if not has_role:
        raise Exception('403 Forbidden')

    return article_db.get_article(id)


def delete_article(id, http_request):
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_CONTENT')
    if not has_role:
        raise Exception('403 Forbidden')

    article_db.delete_article(id)
    return 0
