from app.domain.solution import Solution
from app.domain.description import Description
from app.domain.credit import Credit
from app.service import token_service, nexus_client, credit_service
from app.database import solution_db, description_db, document_db, artifact_db, star_db, comment_db
from app.utils import mytime


def create_solution(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')
    user_login = token.username

    solution = Solution()
    solution.__dict__ = args.get('solution')
    if user_login != solution.authorLogin and user_login != 'internal':
        raise Exception('403 Forbidden')

    solution.active = True
    solution.company = ''
    solution.starCount = 0
    solution.viewCount = 0
    solution.downloadCount = 0
    solution.commentCount = 0
    solution.displayOrder = 0
    solution.createdDate = mytime.now()
    solution.modifiedDate = mytime.now()

    solution_id =solution_db.create_solution(solution)

    # 为solution创建描述，其uuid设为与solution的uuid一致。 ----huolongshe
    # description表只能在这里创建
    description = Description()
    description.solutionUuid = solution.uuid
    description.authorLogin = solution.authorLogin
    description.content = r'<p>无内容</p>'
    description_db.create_description(description)

    credit = Credit()
    credit.__dict__ = credit_service.find_user_credit(solution.authorLogin)
    credit_service.do_update_credit(credit, 3, '创建新模型<{}>'.format(solution.name))

    return solution_id


def get_solutions(**args):
    http_request = args.get('http_request')
    active = args.get('active')
    uuid = args.get('uuid')
    name = args.get('name')
    authorLogin = args.get('authorLogin')
    company = args.get('company')
    modelType = args.get('modelType')
    toolkitType = args.get('toolkitType')
    subject1 = args.get('subject1')
    subject2 = args.get('subject2')
    subject3 = args.get('subject3')
    tag = args.get('tag')
    filter = args.get('filter')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    if uuid is not None:
        results = solution_db.get_solutions_by_uuid(uuid)
        return {
            'total': len(results),
            'results': results,
        }

    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None and not active:
        # 非登录用户不能查询私有模型
        raise Exception('403 Forbidden')

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
    total, results = solution_db.get_solutions(where, pageable)

    return {
        'total': total,
        'results': results,
    }


def update_solution_baseinfo(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    if user_login != solution.authorLogin and not has_role:
        raise Exception('403 Forbidden')

    solution.name = args.get('name') if args.get('name') else ''
    solution.company = args.get('company') if args.get('company') else ''
    solution.version = args.get('version') if args.get('version') else ''
    solution.summary = args.get('summary') if args.get('summary') else ''
    solution.tag1 = args.get('tag1') if args.get('tag1') else ''
    solution.tag2 = args.get('tag2') if args.get('tag2') else ''
    solution.tag3 = args.get('tag3') if args.get('tag3') else ''
    solution.modelType = args.get('modelType') if args.get('modelType') else ''
    solution.toolkitType = args.get('toolkitType') if args.get('toolkitType') else ''
    solution.modifiedDate = mytime.now()

    solution_db.update_solution_baseinfo(solution)
    return 0


def update_solution_admininfo(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_MANAGER')

    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    if not has_role:
        raise Exception('403 Forbidden')

    solution.subject1 = args.get('subject1') if args.get('subject1') else ''
    solution.subject2 = args.get('subject2') if args.get('subject2') else ''
    solution.subject3 = args.get('subject3') if args.get('subject3') else ''
    solution.displayOrder = args.get('displayOrder') if args.get('displayOrder') else 0
    solution.modifiedDate = mytime.now()
    
    solution_db.update_solution_admininfo(solution)
    return 0


def update_solution_name(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username

    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    if user_login != solution.authorLogin:
        raise Exception('403 Forbidden')

    solution.name = args.get('name')
    solution.modifiedDate = mytime.now()

    solution_db.update_solution_name(solution)
    return 0


def update_solution_active(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    if user_login != solution.authorLogin and not has_role:
        raise Exception('403 Forbidden')

    if solution.active and not args.get('active'):
        credit = Credit()
        credit.__dict__ = credit_service.find_user_credit(user_login)
        if credit.credit < 20:
            raise Exception('400 Bad request')
        else:
            solution.active = args.get('active')
            solution_db.update_solution_active(solution)
            credit_service.do_update_credit(credit, -20, '将AI模型<{}>设为私有'.format(solution.name))
            return 0
    else:
        solution.active = args.get('active')
        solution_db.update_solution_active(solution)
        return 0
    

def update_solution_picture_url(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    if user_login != solution.authorLogin and not has_role:
        raise Exception('403 Forbidden')

    solution.pictureUrl = args.get('pictureUrl')
    solution.modifiedDate = mytime.now()

    solution_db.update_solution_picture_url(solution)
    return 0


def update_solution_star_count(**args):
    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))
    solution.starCount = star_db.get_stared_count(solution.uuid)

    solution_db.update_solution_star_count(solution)
    return 0


def update_solution_comment_count(**args):
    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))
    solution.commentCount = comment_db.get_comment_count(solution.uuid)

    solution_db.update_solution_comment_count(solution)
    return 0


def update_solution_view_count(**args):
    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    solution.viewCount += 1

    solution_db.update_solution_view_count(solution)
    return 0


def update_solution_download_count(**args):
    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    solution.downloadCount += 1

    solution_db.update_solution_download_count(solution)
    return 0


def delete_solution(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    solution = Solution()
    solution.__dict__ = solution_db.get_solution_by_id(args.get('solutionId'))

    if user_login != solution.authorLogin and user_login != 'internal' and not has_role:
        raise Exception('403 Forbidden')

    where = 'WHERE solution_uuid = "{}"'.format(solution.uuid)
    document_list = document_db.get_documents(where)
    for document in document_list:
        nexus_client.delete_artifact(document.get('url'))
        document_db.delete_document(document.get('id'))

    artifact_list = artifact_db.get_artifacts(where)
    for artifact in artifact_list:
        if artifact.get('type') == 'DOCKER镜像':
            nexus_client.delete_docker_image(artifact.get('url'))
        else:
            nexus_client.delete_artifact(artifact.get('url'))

        artifact_db.delete_artifact(artifact.get('id'))

    description = description_db.get_descriptions(solution.uuid)[0]
    description_db.delete_description(description.get('id'))

    where = 'WHERE target_uuid = "{}"'.format(solution.uuid)
    _, star_list = star_db.get_stars(where, None)
    for star in star_list:
        star_db.delete_star(star.get('id'))

    solution_db.delete_solution(solution.id)
    return 0

