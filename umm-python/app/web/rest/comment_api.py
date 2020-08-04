import tornado.web
from app.domain.comment import Comment
from app.service import token_service
from app.database import comment_db
from app.utils import mytime
import json


class CommentApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username

        comment = Comment()
        comment.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))
        comment.userLogin = user_login
        comment.createdDate = mytime.now()
        comment.modifiedDate = mytime.now()

        await comment_db.create_comment(comment)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        solutionUuid = self.get_argument('solutionUuid', None)
        parentUuid = self.get_argument('parentUuid', None)

        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        where = 'WHERE solution_uuid = "{}" and parent_uuid = "{}"'.format(solutionUuid, parentUuid)
        total_count, result = await comment_db.get_comments(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class CommentApiB(tornado.web.RequestHandler):

    async def get(self, id, *args, **kwargs):
        result = await comment_db.get_comment(id)
        self.write(result)

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        user_login = token.username
        has_role = token.has_role('ROLE_MANAGER')

        comment = await comment_db.get_comment(id)
        if user_login != comment.get('userLogin') and not has_role:
            self.send_error(403)
            return

        await comment_db.delete_comment(id)
        self.set_status(200)
        self.finish()
