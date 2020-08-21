import tornado.web
from app.service import nexus_client_async


class DownloadApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        url = self.get_argument('url')
        data = await nexus_client_async.get_artifact(url)
        self.write(data)
