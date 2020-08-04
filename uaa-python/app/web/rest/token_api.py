import tornado.web
from app.service import oauth_service


class TokenApi(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        grant_type = self.get_argument('grant_type')

        if grant_type == 'client_credentials':
            if not oauth_service.verify_basic_authorization(self.request.headers.get('Authorization'), 'internal'):
                self.send_error(403)
                return

            result = oauth_service.gen_client_token()

            if result is None:
                self.send_error(400)
                return

            self.write(result)
            return

        if grant_type == 'password':
            if not oauth_service.verify_basic_authorization(self.request.headers.get('Authorization'), 'web_app'):
                self.send_error(403)
                return

            username = self.get_argument('username')
            password = self.get_argument('password')

            user = await oauth_service.verify_user_password(username, password)
            if user is None:
                self.send_error(403)
                return

            result = oauth_service.gen_user_token(user)
            if result is None:
                self.send_error(400)
                return

            self.write(result)
            return

        if grant_type == 'refresh_token':
            if not oauth_service.verify_basic_authorization(self.request.headers.get('Authorization'), 'web_app'):
                self.send_error(403)
                return

            refresh_token = self.get_argument('refresh_token', None)
            if not refresh_token:
                self.send_error(400)
                return

            result = await oauth_service.gen_from_refresh_token(refresh_token)
            if result is None:
                self.send_error(400)
                return

            self.write(result)
            return

        self.send_error(400)
        return
