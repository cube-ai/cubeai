import tornado.web
from app.domain.artifact import Artifact
from app.service import token_service
from app.database import artifact_db, solution_db
from app.utils import mytime
import json


class ArtifactApiA(tornado.web.RequestHandler):

    async def get(self, *args, **kwargs):
        solution_uuid = self.get_argument('solutionUuid', None)
        artifact_type = self.get_argument('type', None)

        where = 'WHERE solution_uuid = "{}"'.format(solution_uuid)
        if artifact_type is not None:
            where += ' and jhi_type = "{}"'.format(artifact_type)

        result = await artifact_db.get_artifacts(where)
        self.write(json.dumps(result))

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        artifact = Artifact()
        artifact.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        user_login = token.username
        if user_login != 'internal':
            solutions = await solution_db.get_solutions_by_uuid(artifact.solutionUuid)
            if len(solutions) > 0:
                # solution已经存在， 只允许solution作者自己添加artifact
                solution = solutions[0]
                if user_login != solution.get('authorLogin'):
                    self.send_error(403)
                    return
            else:
                # solution不存在， 只允许internal添加artifact
                self.send_error(403)
                return

        artifact.createdDate = mytime.now()
        artifact.modifiedDate = mytime.now()
        await artifact_db.create_artifact(artifact)
        self.set_status(201)
        self.finish()


class ArtifactApiB(tornado.web.RequestHandler):

    async def delete(self, id, *args, **kwargs):
        await artifact_db.delete_artifact(id)
        self.set_status(200)
        self.finish()
