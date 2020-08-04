import json
import tornado.web
from app.service import token_service
from app.domain.message import Message
from app.database import message_db
from app.utils import mytime


class MessageApiA(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        receiver = self.get_argument('receiver', None)
        sender = self.get_argument('sender', None)
        deleted = self.get_argument('deleted', None)
        filter = self.get_argument('filter', None)
        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(403)
            return

        if receiver is None and sender is None:
            self.send_error(403)
            return

        if not (receiver is not None and receiver == user_login or sender is not None and sender == user_login):
            self.send_error(403)
            return

        where1 = ''
        if receiver is not None:
            where1 += 'and receiver = "{}" '.format(receiver)
        if sender is not None:
            where1 += 'and sender = "{}" '.format(sender)
        if deleted is not None:
            where1 += 'and deleted = {} '.format(deleted)
        where1 = where1[4:]

        where2 = ''
        if filter is not None:
            where2 += 'receiver like "%{}%"'.format(filter)
            where2 += ' or sender like "%{}%"'.format(filter)
            where2 += ' or subject like "%{}%"'.format(filter)
            where2 += ' or content like "%{}%"'.format(filter)

        where = ''
        if where1:
            where += 'and {}'.format(where1)
        if where2:
            where += 'and {}'.format(where2)
        if where:
            where = where[4:]

        if where != '':
            where = 'WHERE ' + where

        total_count, result = await message_db.get_messages(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class MessageApiB(tornado.web.RequestHandler):

    async def get(self, id,  *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(403)
            return

        message = await message_db.find_one_by_id(id)
        if message is None or (message.sender != user_login and message.receiver != user_login):
            self.send_error(403)
            return

        self.write(message.__dict__)

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(403)
            return

        message = await message_db.find_one_by_id(id)
        if message is None or message.receiver != user_login:
            self.send_error(403)
            return

        await message_db.delete_message(id)
        self.finish()

    async def post(self, action, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(403)
            return

        if action == 'send':
            message = Message()
            message.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

            message.sender = user_login
            message.viewed = False
            message.deleted = False
            message.createdDate = mytime.now()
            message.modifiedDate = mytime.now()
            await message_db.create_message(message)
            self.finish()
            return

        if action == 'multicast':
            draft = json.loads(str(self.request.body, encoding='utf-8'))
            message = Message()
            message.__dict__ = draft.get('message')
            receivers = draft.get('receivers')

            message.sender = user_login
            message.viewed = False
            message.deleted = False
            message.createdDate = mytime.now()
            message.modifiedDate = mytime.now()

            for receiver in receivers:
                message.receiver = receiver
                await message_db.create_message(message)

            self.finish()
            return

        self.send_error(400)

    async def put(self, action, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None:
            self.send_error(403)
            return

        if action == 'viewed':
            id = self.get_argument('id')
            viewed = self.get_argument('viewed').lower() == 'true'

            message = await message_db.find_one_by_id(id)
            if message.receiver != user_login:
                self.send_error(403)
                return

            await message_db.update_message_viewed(id, viewed)
            self.finish()

        if action == 'deleted':
            id = self.get_argument('id')
            deleted = self.get_argument('deleted').lower() == 'true'

            message = await message_db.find_one_by_id(id)
            if message.receiver != user_login:
                self.send_error(403)
                return

            await message_db.update_message_deleted(id, deleted)
            self.finish()


class MessageUnreadApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        receiver = self.get_argument('receiver')
        deleted = self.get_argument('deleted').lower() == 'true'

        count = await message_db.get_unreaded_count(receiver, deleted)
        self.write(str(count))
