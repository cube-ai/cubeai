import json
import tornado.web
from app.domain.application import Application
from app.service import token_service
from app.database import application_db
from app.utils import mytime


class ApplicationApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_APPLICATION')
        if not has_role:
            self.send_error(403)
            return

        application = Application()
        application.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        application.complete_attrs()
        application.createdBy = user_login
        application.modifiedBy = user_login
        application.createdDate = mytime.now()
        application.modifiedDate = mytime.now()

        await application_db.create_application(application)
        self.set_status(201)
        self.finish()

    async def put(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_APPLICATION')
        if not has_role:
            self.send_error(403)
            return

        application = Application()
        application.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        application.modifiedBy= user_login
        application.modifiedDate = mytime.now()

        await application_db.update_application(application)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        uuid = self.get_argument('uuid', None)
        name = self.get_argument('name', None)
        owner = self.get_argument('owner', None)
        subject1 = self.get_argument('subject1', None)
        subject2 = self.get_argument('subject2', None)
        subject3 = self.get_argument('subject3', None)
        filter = self.get_argument('filter', None)
        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        if uuid is not None:
            result = await application_db.get_applications_by_uuid(uuid)
            if len(result) > 0:
                result[0]['pictureUrl'] = ''
            self.write(json.dumps(result))
            return

        where1 = ''
        if name is not None:
            where1 += 'and name = "{}" '.format(name)
        if owner is not None:
            where1 += 'and owner = "{}" '.format(owner)
        if subject1 is not None:
            where1 += 'and subject_1 = "{}" '.format(subject1)
        if subject2 is not None:
            where1 += 'and subject_2 = "{}" '.format(subject2)
        if subject3 is not None:
            where1 += 'and subject_3 = "{}" '.format(subject3)

        where1 = where1[4:]

        where2 = ''
        if filter is not None:
            where2 += 'name like "%{}%"'.format(filter)
            where2 += ' or owner like "%{}%"'.format(filter)
            where2 += ' or subject_1 like "%{}%"'.format(filter)
            where2 += ' or subject_2 like "%{}%"'.format(filter)
            where2 += ' or subject_3 like "%{}%"'.format(filter)
            where2 += ' or summary like "%{}%"'.format(filter)

        where = ''
        if where1:
            where += 'and {}'.format(where1)
        if where2:
            where += 'and {}'.format(where2)
        if where:
            where = where[4:]

        if where != '':
            where = 'WHERE ' + where
        total_count, result = await application_db.get_applications(where, pageable)
        self.set_header('X-Total-Count', total_count)

        for application in result:
            application['pictureUrl'] = ''

        self.write(json.dumps(result))


class ApplicationApiB(tornado.web.RequestHandler):

    async def get(self, id, *args, **kwargs):
        result = await application_db.get_application(id)
        self.write(result)

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_APPLICATION')
        if not has_role:
            self.send_error(403)
            return

        await application_db.delete_application(id)
        self.set_status(200)
        self.finish()


class ApplicationApiC(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        uuid = self.get_argument('uuid', None)
        name = self.get_argument('name', None)
        owner = self.get_argument('owner', None)
        subject1 = self.get_argument('subject1', None)
        subject2 = self.get_argument('subject2', None)
        subject3 = self.get_argument('subject3', None)
        filter = self.get_argument('filter', None)
        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        if uuid is not None:
            result = await application_db.get_applications_by_uuid(uuid)
            if len(result) > 0:
                result[0]['pictureUrl'] = ''
            self.write(json.dumps(result))
            return

        where1 = ''
        if name is not None:
            where1 += 'and name = "{}" '.format(name)
        if owner is not None:
            where1 += 'and owner = "{}" '.format(owner)
        if subject1 is not None:
            where1 += 'and subject_1 = "{}" '.format(subject1)
        if subject2 is not None:
            where1 += 'and subject_2 = "{}" '.format(subject2)
        if subject3 is not None:
            where1 += 'and subject_3 = "{}" '.format(subject3)

        where1 = where1[4:]

        where2 = ''
        if filter is not None:
            where2 += 'name like "%{}%"'.format(filter)
            where2 += ' or owner like "%{}%"'.format(filter)
            where2 += ' or subject_1 like "%{}%"'.format(filter)
            where2 += ' or subject_2 like "%{}%"'.format(filter)
            where2 += ' or subject_3 like "%{}%"'.format(filter)
            where2 += ' or summary like "%{}%"'.format(filter)

        where = ''
        if where1:
            where += 'and {}'.format(where1)
        if where2:
            where += 'and {}'.format(where2)
        if where:
            where = where[4:]

        if where != '':
            where = 'WHERE ' + where
        total_count, result = await application_db.get_applications(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))
