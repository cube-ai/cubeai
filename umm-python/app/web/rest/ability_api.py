import tornado.web
from app.domain.deployment import Deployment
from app.database import deployment_db
import json


class AbilityApi(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        uuid = self.get_argument('uuid', None)
        result = await deployment_db.get_deployments_by_uuid(uuid)

        if len(result) > 0:
            deployment = Deployment()
            deployment.__dict__ = result[0]
            deployment.callCount += 1
            await deployment_db.update_deployment_call_count(deployment)

        self.write(json.dumps(result))
