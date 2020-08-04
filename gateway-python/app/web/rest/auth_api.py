import json
import base64
import tornado.web
from tornado.httpclient import AsyncHTTPClient, HTTPRequest
from app.service import uaa_client
from app.globals.globals import g


class AuthApi(tornado.web.RequestHandler):

    async def post(self, action, *args, **kwargs):

        if action == 'login':
            body = json.loads(str(self.request.body, encoding='utf-8'))
            username = body.get('username')
            password = body.get('password')

            verify_result = await uaa_client.validate_verify_code(self.request.body)

            if verify_result != 1:
                self.send_error(400)
                return

            headers = {
                'Accept': 'application/json, application/*+json',
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': 'Basic {}'.format(str(base64.b64encode(b'web_app:changeit'), encoding='utf-8'))
            }
            url = await g.oauth_client.async_get_jwt_url()
            params = '?grant_type=password&username={}&password={}'.format(username, password)
            request = HTTPRequest(url=url + params,
                                  method='POST',
                                  body='{}',
                                  headers=headers)

            http = AsyncHTTPClient()
            try:
                res = await http.fetch(request)
            except Exception as e:
                self.send_error(403)
                return

            if res.code != 200:
                self.send_error(403)
                return

            res_body = json.loads(str(res.body, encoding='utf-8'))
            access_token = res_body.get('access_token')
            refresh_token = res_body.get('refresh_token')

            self.set_cookie('access_token', access_token)
            self.set_cookie('session_token', refresh_token)

        elif action == 'logout':
            self.set_cookie('access_token', '')
            self.set_cookie('session_token', '')
            self.set_cookie('refresh_token', '')

        else:
            self.send_error(400)
