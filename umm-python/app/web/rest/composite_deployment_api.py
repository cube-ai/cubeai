import tornado.web

from app.domain.composite_deployment import CompositeDeployment
from app.service import token_service
from app.database import composite_solution_db, star_db, composite_deployment_db
from app.utils import mytime
import json


class CompositeDeploymentApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        deployment = CompositeDeployment()
        deployment.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        user_login = token.username
        if user_login != deployment.deployer and user_login != 'internal':
            self.send_error(403)
            return

        deployment.starCount = 0
        deployment.callCount = 0
        deployment.displayOrder = 0
        deployment.createdDate = mytime.now()
        deployment.modifiedDate = mytime.now()

        await composite_deployment_db.create_deployment(deployment)
        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        isPublic = self.get_argument('isPublic', None)
        if isPublic is not None:
            isPublic = isPublic.lower() == 'true'
        deployer = self.get_argument('deployer', None)
        status = self.get_argument('status', None)
        uuid = self.get_argument('uuid', None)
        solutionUuid = self.get_argument('solutionUuid', None)
        subject1 = self.get_argument('subject1', None)
        subject2 = self.get_argument('subject2', None)
        subject3 = self.get_argument('subject3', None)
        filter = self.get_argument('filter', None)
        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        if uuid is not None:
            result = await composite_deployment_db.get_deployments_by_uuid(uuid)
            self.write(json.dumps(result))
            return

        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_OPERATOR')

        where1 = ''
        if isPublic is not None:
            where1 += 'and is_public = {} '.format(isPublic)
            if deployer is not None:
                where1 += 'and deployer = "{}" '.format(deployer)
            elif not isPublic and not has_role:
                where1 += 'and deployer = "{}" '.format(user_login)
        elif deployer is not None:
            where1 += 'and deployer = "{}" '.format(deployer)

        if status is not None:
            where1 += 'and status = "{}" '.format(status)
        if solutionUuid is not None:
            where1 += 'and solution_uuid = "{}" '.format(solutionUuid)
        if subject1 is not None:
            where1 += 'and subject_1 = "{}" '.format(subject1)
        if subject2 is not None:
            where1 += 'and subject_2 = "{}" '.format(subject2)
        if subject3 is not None:
            where1 += 'and subject_3 = "{}" '.format(subject3)
        where1 = where1[4:]

        where2 = ''
        if filter is not None:
            where2 += 'name solution_name "%{}%"'.format(filter)
            where2 += ' or solution_author like "%{}%"'.format(filter)
            where2 += ' or deployer like "%{}%"'.format(filter)
            where2 += ' or status like "%{}%"'.format(filter)

        where = ''
        if where1:
            where += 'and {}'.format(where1)
        if where2:
            where += 'and {}'.format(where2)
        if where:
            where = where[4:]

        if where != '':
            where = 'WHERE ' + where
        total_count, result = await composite_deployment_db.get_deployments(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class CompositeDeploymentApiB(tornado.web.RequestHandler):

    async def put(self, attr, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_OPERATOR')

        body = json.loads(str(self.request.body, encoding='utf-8'))
        deployment = CompositeDeployment()
        deployment.__dict__ = await composite_deployment_db.get_deployment_by_id(body.get('id'))

        if attr == 'solutioninfo':
            solution_uuld = deployment.solutionUuid
            solutions = await composite_solution_db.get_solutions_by_uuid(solution_uuld)
            solution = solutions[0]

            deployment.solutionName = solution.get('name')
            deployment.pictureUrl = solution.get('pictureUrl')

            await composite_deployment_db.update_deployment_solutioninfo(deployment)
            self.set_status(201)
            self.finish()

        elif attr == 'admininfo':
            if not has_role:
                self.send_error(403)
                return

            deployment.subject1 = body.get('subject1') if body.get('subject1') else ''
            deployment.subject2 = body.get('subject2') if body.get('subject2') else ''
            deployment.subject3 = body.get('subject3') if body.get('subject3') else ''
            deployment.displayOrder = body.get('displayOrder') if body.get('displayOrder') else 0
            deployment.modifiedDate = mytime.now()

            await composite_deployment_db.update_deployment_admininfo(deployment)
            self.set_status(201)
            self.finish()

        elif attr == 'demourl':
            if not has_role:
                self.send_error(403)
                return

            deployment.demoUrl = body.get('demoUrl') if body.get('demoUrl') else ''
            await composite_deployment_db.update_deployment_demourl(deployment)
            self.set_status(201)
            self.finish()

        elif attr == 'status':
            if user_login != 'internal':
                self.send_error(403)
                return

            deployment.status = body.get('status')
            await composite_deployment_db.update_deployment_status(deployment)
            self.set_status(201)
            self.finish()

        elif attr == 'star-count':
            deployment.starCount = await star_db.get_stared_count(deployment.uuid)

            await composite_deployment_db.update_deployment_star_count(deployment)
            self.set_status(201)
            self.finish()

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        has_role = token.has_role('ROLE_OPERATOR')
        if not has_role:
            self.send_error(403)
            return

        await composite_deployment_db.delete_deployment(id)
