import json
import tornado.web
from app.domain.article import Article
from app.service import token_service
from app.database import article_db
from app.utils import mytime


class ArticleApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_CONTENT')
        if not has_role:
            self.send_error(403)
            return

        article = Article()
        article.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        article.complete_attrs()
        article.createdDate = mytime.now()
        article.modifiedDate = mytime.now()

        await article_db.create_article(article)
        self.set_status(201)
        self.finish()

    async def put(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_CONTENT')
        if not has_role:
            self.send_error(403)
            return

        article = Article()
        article.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        article.modifiedDate = mytime.now()

        await article_db.update_article(article)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        uuid = self.get_argument('uuid', None)
        authorLogin = self.get_argument('authorLogin', None)
        subject1 = self.get_argument('subject1', None)
        subject2 = self.get_argument('subject2', None)
        subject3 = self.get_argument('subject3', None)
        title = self.get_argument('title', None)
        tag1 = self.get_argument('tag1', None)
        tag2 = self.get_argument('tag2', None)
        tag3 = self.get_argument('tag3', None)
        filter = self.get_argument('filter', None)
        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        if uuid is not None:
            result = await article_db.get_articles_by_uuid(uuid)
            self.write(json.dumps(result))
            return

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
            where += 'and {}'.format(where1)
        if where2:
            where += 'and {}'.format(where2)
        if where:
            where = where[4:]

        if where != '':
            where = 'WHERE ' + where
        total_count, result = await article_db.get_articles(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class ArticleApiB(tornado.web.RequestHandler):

    async def get(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_CONTENT')
        if not has_role:
            self.send_error(403)
            return

        result = await article_db.get_article(id)
        self.write(result)

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_CONTENT')
        if not has_role:
            self.send_error(403)
            return

        await article_db.delete_article(id)
        self.set_status(200)
        self.finish()
