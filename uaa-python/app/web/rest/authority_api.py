import json
import tornado.web
from app.service import token_service
from app.database import authority_db


class AuthorityApiA(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        result = await authority_db.get_authorities()
        self.write(json.dumps(result))


class AuthorityApiB(tornado.web.RequestHandler):

    async def post(self, authority, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        authority = authority.upper()
        authorities = await authority_db.get_authorities()
        if not authority in authorities:
            await authority_db.create_authority(authority)

        self.finish()

    async def delete(self, authority, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_ADMIN')
        if not has_role:
            self.send_error(403)
            return

        authorities = await authority_db.get_authorities()
        if authority in authorities:
            await authority_db.delete_authority(authority)

        self.finish()
