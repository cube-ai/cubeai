import tornado.web
from app.domain.solution import Solution
from app.domain.description import Description
from app.domain.credit import Credit
from app.service import token_service
from app.database import composite_solution_db, description_db, document_db, artifact_db, star_db, comment_db
from app.service import nexus_client_async, credit_service
from app.utils import mytime
import json


class CompositeSolutionApiA(tornado.web.RequestHandler):

    async def post(self, *args, **kwargs):
        token = token_service.get_token(self.request)
        if not token.is_valid:
            self.send_error(403)
            return

        solution = Solution()
        solution.__dict__ = json.loads(str(self.request.body, encoding='utf-8'))

        user_login = token.username
        if user_login != solution.authorLogin and user_login != 'internal':
            self.send_error(403)
            return

        solution.active = True
        solution.company = ''
        solution.starCount = 0
        solution.viewCount = 0
        solution.downloadCount = 0
        solution.commentCount = 0
        solution.displayOrder = 0
        solution.createdDate = mytime.now()
        solution.modifiedDate = mytime.now()
        solution.toolkitType = '模型组合'
        await composite_solution_db.create_solution(solution)

        # 为solution创建描述，其uuid设为与solution的uuid一致。 ----huolongshe
        # description表只能在这里创建
        description = Description()
        description.solutionUuid = solution.uuid
        description.authorLogin = solution.authorLogin
        description.content = r'<p>无内容</p>'
        await description_db.create_description(description)

        credit = Credit()
        credit.__dict__ = await credit_service.find_user_credit(solution.authorLogin)
        await credit_service.update_credit(credit, 3, '创建新模型<{}>'.format(solution.name))

        self.set_status(201)
        self.finish()

    async def get(self, *args, **kwargs):
        active = self.get_argument('active', None)
        if active is not None:
            active = active.lower() == 'true'
        uuid = self.get_argument('uuid', None)
        name = self.get_argument('name', None)
        authorLogin = self.get_argument('authorLogin', None)
        company = self.get_argument('company', None)
        modelType = self.get_argument('modelType', None)
        toolkitType = self.get_argument('toolkitType', None)
        subject1 = self.get_argument('subject1', None)
        subject2 = self.get_argument('subject2', None)
        subject3 = self.get_argument('subject3', None)
        tag = self.get_argument('tag', None)
        filter = self.get_argument('filter', None)
        pageable = {
            'page': self.get_argument('page', None),
            'size': self.get_argument('size', None),
            'sort': self.get_arguments('sort'),
        }

        if uuid is not None:
            result = await composite_solution_db.get_solutions_by_uuid(uuid)
            self.write(json.dumps(result))
            return

        token = token_service.get_token(self.request)
        user_login = token.username
        if user_login is None and not active:
            # 非登录用户不能查询私有模型
            self.send_error(403)
            return

        where1 = ''
        if active is not None:
            where1 += 'and active = {} '.format(active)
            if not active:
                where1 += 'and author_login = "{}" '.format(user_login)
        if authorLogin is not None and (active is None or active):
            where1 += 'and author_login = "{}" '.format(authorLogin)
        if name is not None:
            where1 += 'and name = "{}" '.format(name)
        if company is not None:
            where1 += 'and company = "{}" '.format(company)
        if modelType is not None:
            where1 += 'and model_type = "{}" '.format(modelType)
        if toolkitType is not None:
            where1 += 'and toolkit_type = "{}" '.format(toolkitType)
        if subject1 is not None:
            where1 += 'and subject_1 = "{}" '.format(subject1)
        if subject2 is not None:
            where1 += 'and subject_2 = "{}" '.format(subject2)
        if subject3 is not None:
            where1 += 'and subject_3 = "{}" '.format(subject3)
        where1 = where1[4:]

        where2 = ''
        if tag is not None:
            where2 = 'tag_1 like "%{}%" or tag_2 like "%{}%" or tag_3 like "%{}%"'.format(tag, tag, tag)

        where3 = ''
        if filter is not None:
            where3 += 'name like "%{}%"'.format(filter)
            where3 += ' or author_login like "%{}%"'.format(filter)
            where3 += ' or author_name like "%{}%"'.format(filter)
            where3 += ' or model_type like "%{}%"'.format(filter)
            where3 += ' or toolkit_type like "%{}%"'.format(filter)
            where3 += ' or summary like "%{}%"'.format(filter)
            where3 += ' or tag_1 like "%{}%"'.format(filter)
            where3 += ' or tag_2 like "%{}%"'.format(filter)
            where3 += ' or tag_3 like "%{}%"'.format(filter)
            where3 += ' or company like "%{}%"'.format(filter)

        where = ''
        if where1:
            where += 'and {}'.format(where1)
        if where2:
            where += 'and {}'.format(where2)
        if where3:
            where += 'and {}'.format(where3)
        if where:
            where = where[4:]

        if where != '':
            where = 'WHERE ' + where
        total_count, result = await composite_solution_db.get_solutions(where, pageable)
        self.set_header('X-Total-Count', total_count)

        self.write(json.dumps(result))


class CompositeSolutionApiB(tornado.web.RequestHandler):

    async def put(self, attr, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_MANAGER')

        body = json.loads(str(self.request.body, encoding='utf-8'))
        solution = Solution()
        solution.__dict__ = await composite_solution_db.get_solution_by_id(body.get('id'))

        if attr == 'baseinfo':
            if user_login != solution.authorLogin and not has_role:
                self.send_error(403)
                return

            solution.name = body.get('name') if body.get('name') else ''
            solution.company = body.get('company') if body.get('company') else ''
            solution.version = body.get('version') if body.get('version') else ''
            solution.summary = body.get('summary') if body.get('summary') else ''
            solution.tag1 = body.get('tag1') if body.get('tag1') else ''
            solution.tag2 = body.get('tag2') if body.get('tag2') else ''
            solution.tag3 = body.get('tag3') if body.get('tag3') else ''
            solution.modelType = body.get('modelType') if body.get('modelType') else ''
            solution.toolkitType = body.get('toolkitType') if body.get('toolkitType') else ''
            solution.modifiedDate = mytime.now()

            await composite_solution_db.update_solution_baseinfo(solution)
            self.set_status(201)
            self.finish()

        elif attr == 'name':
            if user_login != solution.authorLogin:
                self.send_error(403)
                return

            solution.name = body.get('name')
            solution.modifiedDate = mytime.now()

            await composite_solution_db.update_solution_name(solution)
            self.set_status(201)
            self.finish()

        elif attr == 'picture-url':
            if user_login != solution.authorLogin and not has_role:
                self.send_error(403)
                return

            solution.pictureUrl = body.get('pictureUrl')
            solution.modifiedDate = mytime.now()

            await composite_solution_db.update_solution_picture_url(solution)
            self.set_status(201)
            self.finish()

        elif attr == 'active':
            if user_login != solution.authorLogin and not has_role:
                self.send_error(403)
                return

            if solution.active and not body.get('active'):
                credit = Credit()
                credit.__dict__ = await credit_service.find_user_credit(user_login)
                if credit.credit < 20:
                    self.send_error(400)
                    return
                else:
                    solution.active = body.get('active')
                    await composite_solution_db.update_solution_active(solution)
                    await credit_service.update_credit(credit, -20, '将AI模型<{}>设为私有'.format(solution.name))
                    self.set_status(201)
                    self.finish()
            else:
                solution.active = body.get('active')
                await composite_solution_db.update_solution_active(solution)
                self.set_status(201)
                self.finish()

        elif attr == 'admininfo':
            if not has_role:
                self.send_error(403)
                return

            solution.subject1 = body.get('subject1') if body.get('subject1') else ''
            solution.subject2 = body.get('subject2') if body.get('subject2') else ''
            solution.subject3 = body.get('subject3') if body.get('subject3') else ''
            solution.displayOrder = body.get('displayOrder') if body.get('displayOrder') else 0
            solution.modifiedDate = mytime.now()

            await composite_solution_db.update_solution_admininfo(solution)
            self.set_status(201)
            self.finish()

        elif attr == 'star-count':
            solution.starCount = await star_db.get_stared_count(solution.uuid)

            await composite_solution_db.update_solution_star_count(solution)
            self.set_status(201)
            self.finish()

        elif attr == 'comment-count':
            solution.commentCount = await comment_db.get_comment_count(solution.uuid)

            await composite_solution_db.update_solution_comment_count(solution)
            self.set_status(201)
            self.finish()

        elif attr == 'view-count':
            solution.viewCount += 1

            await composite_solution_db.update_solution_view_count(solution)
            self.set_status(201)
            self.finish()

        elif attr == 'download-count':
            solution.downloadCount += 1

            await composite_solution_db.update_solution_download_count(solution)
            self.set_status(201)
            self.finish()

    async def delete(self, id, *args, **kwargs):
        token = token_service.get_token(self.request)
        user_login = token.username
        has_role = token.has_role('ROLE_MANAGER')

        solution = Solution()
        solution.__dict__ = await composite_solution_db.get_solution_by_id(id)

        if user_login != solution.authorLogin and user_login != 'internal' and not has_role:
            self.send_error(403)
            return

        where = 'WHERE solution_uuid = "{}"'.format(solution.uuid)
        document_list = await document_db.get_documents(where)
        for document in document_list:
            await nexus_client_async.delete_artifact(document.get('url'))
            await document_db.delete_document(document.get('id'))

        artifact_list = await artifact_db.get_artifacts(where)
        for artifact in artifact_list:
            if artifact.get('type') == 'DOCKER镜像':
                await nexus_client_async.delete_docker_image(artifact.get('url'))
            else:
                await nexus_client_async.delete_artifact(artifact.get('url'))
            await artifact_db.delete_artifact(artifact.get('id'))

        where = 'WHERE target_uuid = "{}"'.format(solution.uuid)
        _, star_list = await star_db.get_stars(where, None)
        for star in star_list:
            await star_db.delete_star(star.get('id'))

        await composite_solution_db.delete_solution(solution.id)
