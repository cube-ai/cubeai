import tornado.web
from app.domain.star import Star
from app.service import token_service
from app.database import star_db
from app.utils import mytime
import json


class StarApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username

        star = Star()
        star.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        star.userLogin = user_login
        star.starDate = mytime.now()

        await star_db.create_star(star)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        userLogin = self.get_argument('userLogin', None)
        targetUuid = self.get_argument('targetUuid', None)
        targetType = self.get_argument('targetType', None)

        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        where = ''
        if userLogin is not None:
            where += 'and user_login = "{}" '.format(userLogin)
        if targetUuid is not None:
            where += 'and target_uuid = "{}" '.format(targetUuid)
        if targetType is not None:
            where += 'and target_type = "{}" '.format(targetType)
        where = where[4:]

        if where != '':
            where = 'WHERE ' + where
        total_count, result = await star_db.get_stars(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class StarApiB(tornado.web.RequestHandler):

    async def get(self, uuids, *args, **kwargs):
        if uuids == 'uuids':
            user_login = self.get_argument('userLogin')
            result = await star_db.get_stared_uuid_list(user_login)
            self.write(json.dumps(result))
        else:
            self.send_error(400)


    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username
        star_user = await star_db.get_star_user_login(id)
        if user_login != star_user:
            self.send_error(403)
            return

        await star_db.delete_star(id)
        self.set_status(200)
        self.finish()


class StarApiC(tornado.web.RequestHandler):

    async def get(self, count, target_uuid, *args, **kwargs):
        if count != 'count':
            self.send_error(400)
            return

        star_count = await star_db.get_stared_count(target_uuid)

        self.write({'starCount': star_count})

    async def delete(self, uuid, target_uuid, *args, **kwargs):
        if uuid != 'uuid':
            self.send_error(400)
            return

        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return
        user_login = token.username

        where = 'WHERE user_login = "{}" and target_uuid = "{}"'.format(user_login, target_uuid)
        _, stars = await star_db.get_stars(where, None)

        await star_db.delete_star(stars[0].get('id'))

        self.finish()

