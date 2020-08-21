import json
import tornado.web
from tornado.httpclient import AsyncHTTPClient, HTTPRequest
from app.service import umm_client_async
from app.globals.globals import g


class StreamApi(tornado.web.RequestHandler):

    async def post(self, deployment_uuid, method, *args, **kwargs):

        res = await umm_client_async.get_deployment(deployment_uuid)
        if len(res) < 1:
            self.set_status(400)
            self.write(r'{"error": "未找到部署实例"}')
            return
        deployment = res[0]

        k8s_port = deployment.get('k8sPort')
        if k8s_port is None:
            self.set_status(400)
            self.write(r'{"error": "k8s端口停止服务"}')
            return

        internal_ip = g.get_central_config()['kubernetes']['ability']['internalIP']
        url = 'http://{}:{}/api/stream/{}'.format(internal_ip, k8s_port, method)

        http = AsyncHTTPClient()
        try:
            res = await http.fetch(HTTPRequest(url=url, method='POST', body=self.request.body, headers=self.request.headers))
        except Exception as e:
            if e.response is None:
                self.set_status(e.code)
                error = r'{"error": "' + e.message + r'"}'
                self.write(error)
                return
            self.set_status(e.response.code)
            try:
                json.loads(str(e.response.body, encoding='utf-8'))
                self.write(e.response.body)
            except:
                error = r'{"error": "' \
                        + str(e.response.body, encoding='utf-8').replace(r'"', '').replace('\n', ' ') \
                        + r'"}'
                self.write(error)
            return

        self.set_status(res.code)
        self.set_header("Access-Control-Allow-Origin", "*")
        self.write(res.body)
