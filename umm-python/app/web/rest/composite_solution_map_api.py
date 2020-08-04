import tornado.web
from app.domain.composite_solution_map import CompositeSolutionMap
from app.service import token_service
from app.database import composite_solution_map_db
import json


class CompositeSolutionMapApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        composite_solution_map = CompositeSolutionMap()
        composite_solution_map.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        await composite_solution_map_db.create_composite_solution_map(composite_solution_map)
        self.set_status(201)
        self.finish()

    async def put(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        composite_solution_map = CompositeSolutionMap()
        composite_solution_map.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        await composite_solution_map_db.update_composite_solution_map(composite_solution_map)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        parentUuid = self.get_argument('parentUuid', None)
        result = await composite_solution_map_db.get_composite_solution_maps(parentUuid)
        self.write(json.dumps(result))


class CompositeSolutionMapApiB(tornado.web.RequestHandler):

    async def get(self, id, *args, **kwargs):
        result = await composite_solution_map_db.get_composite_solution_map(id)
        self.write(result)

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        await composite_solution_map_db.delete_composite_solution_map(id)
        self.set_status(200)
        self.finish()
