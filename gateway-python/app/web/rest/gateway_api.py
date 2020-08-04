import json
import tornado.web
from tornado.httpclient import AsyncHTTPClient
from app.service.gateway_service import gen_next_request, check_allow_forward


class GatewayApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        if not check_allow_forward(self.request):
            self.send_error(403)
            return

        next_request, new_access_token, new_refresh_token = await gen_next_request(self.request)
        if next_request is None:
            self.set_status(500)
            self.write('服务维护中...')
            return

        if new_access_token is not None and new_refresh_token is not None:
            self.set_cookie('access_token', new_access_token)
            self.set_cookie('session_token', new_refresh_token)

        http = AsyncHTTPClient()
        try:
            res = await http.fetch(next_request)
        except Exception as e:
            res = e.response

        self.set_status(res.code)
        try:
            json.loads(str(res.body, encoding='utf-8'))
            count = res.headers.get('X-Total-Count')
            if count is not None:
                self.set_header('X-Total-Count', count)
        except:
            self._headers = res.headers

        self.write(res.body)

    async def post(self, *args, **kwargs):
        if not check_allow_forward(self.request):
            self.send_error(403)
            return

        next_request, new_access_token, new_refresh_token = await gen_next_request(self.request)
        if next_request is None:
            self.set_status(500)
            self.write('服务维护中...')
            return

        if new_access_token is not None and new_refresh_token is not None:
            self.set_cookie('access_token', new_access_token)
            self.set_cookie('session_token', new_refresh_token)

        http = AsyncHTTPClient()
        try:
            res = await http.fetch(next_request)
        except Exception as e:
            res = e.response

        self.set_status(res.code)
        try:
            json.loads(str(res.body, encoding='utf-8'))
        except:
            self._headers = res.headers

        self.write(res.body)

    async def put(self, *args, **kwargs):
        if not check_allow_forward(self.request):
            self.send_error(403)
            return

        next_request, new_access_token, new_refresh_token = await gen_next_request(self.request)
        if next_request is None:
            self.set_status(500)
            self.write('服务维护中...')
            return

        if new_access_token is not None and new_refresh_token is not None:
            self.set_cookie('access_token', new_access_token)
            self.set_cookie('session_token', new_refresh_token)

        http = AsyncHTTPClient()
        try:
            res = await http.fetch(next_request)
        except Exception as e:
            res = e.response

        self.set_status(res.code)
        try:
            json.loads(str(res.body, encoding='utf-8'))
        except:
            self._headers = res.headers

        self.write(res.body)

    async def delete(self, *args, **kwargs):
        if not check_allow_forward(self.request):
            self.send_error(403)
            return

        next_request, new_access_token, new_refresh_token = await gen_next_request(self.request)
        if next_request is None:
            self.set_status(500)
            self.write('服务维护中...')
            return

        if new_access_token is not None and new_refresh_token is not None:
            self.set_cookie('access_token', new_access_token)
            self.set_cookie('session_token', new_refresh_token)

        http = AsyncHTTPClient()
        try:
            res = await http.fetch(next_request)
        except Exception as e:
            res = e.response

        self.set_status(res.code)
        try:
            json.loads(str(res.body, encoding='utf-8'))
        except:
            self._headers = res.headers

        self.write(res.body)
