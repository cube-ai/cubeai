from app.domain.deployment import Deployment
from app.service import token_service
from app.database import deployment_db, solution_db, star_db
from app.utils import mytime


def create_deployment(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    deployment = Deployment()
    deployment.__dict__ = args.get('deployment')

    user_login = token.username
    if user_login != deployment.deployer and user_login != 'internal':
        raise Exception('403 Forbidden')

    deployment.starCount = 0
    deployment.callCount = 0
    deployment.displayOrder = 0
    deployment.createdDate = mytime.now()
    deployment.modifiedDate = mytime.now()

    id = deployment_db.create_deployment(deployment)
    return id


def get_deployments(**args):
    http_request = args.get('http_request')
    isPublic = args.get('isPublic')
    deployer = args.get('deployer')
    status = args.get('status')
    uuid = args.get('uuid')
    solutionUuid = args.get('solutionUuid')
    subject1 = args.get('subject1')
    subject2 = args.get('subject2')
    subject3 = args.get('subject3')
    filter = args.get('filter')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    if uuid is not None:
        results = deployment_db.get_deployments_by_uuid(uuid)
        return {
            'total': len(results),
            'results': results,
        }

    token = token_service.get_token(http_request)
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
        where2 += 'solution_name like "%{}%"'.format(filter)
        where2 += ' or solution_author like "%{}%"'.format(filter)
        where2 += ' or deployer like "%{}%"'.format(filter)
        where2 += ' or status like "%{}%"'.format(filter)

    where = ''
    if where1:
        where += 'and ({})'.format(where1)
    if where2:
        where += 'and ({})'.format(where2)
    if where:
        where = where[4:]

    if where != '':
        where = 'WHERE ' + where
    total, results = deployment_db.get_deployments(where, pageable)

    return {
        'total': total,
        'results': results,
    }


def update_deployment_solution_info(**args):
    deployment = Deployment()
    deployment.__dict__ = deployment_db.get_deployment_by_id(args.get('deploymentId'))

    solution_uuld = deployment.solutionUuid
    solutions = solution_db.get_solutions_by_uuid(solution_uuld)
    solution = solutions[0]

    deployment.solutionName = solution.get('name')
    deployment.pictureUrl = solution.get('pictureUrl')

    deployment_db.update_deployment_solutioninfo(deployment)
    return 0


def update_deployment_admin_info(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    if not has_role:
        raise Exception('403 Forbidden')

    deployment = Deployment()
    deployment.__dict__ = deployment_db.get_deployment_by_id(args.get('deploymentId'))

    deployment.subject1 = args.get('subject1') if args.get('subject1') else ''
    deployment.subject2 = args.get('subject2') if args.get('subject2') else ''
    deployment.subject3 = args.get('subject3') if args.get('subject3') else ''
    deployment.displayOrder = args.get('displayOrder') if args.get('displayOrder') else 0
    deployment.modifiedDate = mytime.now()

    deployment_db.update_deployment_admininfo(deployment)
    return 0


def update_deployment_status(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    if user_login != 'internal':
        raise Exception('403 Forbidden')

    deployment = Deployment()
    deployment.__dict__ = deployment_db.get_deployment_by_id(args.get('deploymentId'))
    deployment.status = args.get('status')
    deployment_db.update_deployment_status(deployment)
    return 0


def update_deployment_star_count(**args):
    deployment = Deployment()
    deployment.__dict__ = deployment_db.get_deployment_by_id(args.get('deploymentId'))
    deployment.starCount = star_db.get_stared_count(deployment.uuid)

    deployment_db.update_deployment_star_count(deployment)
    return 0


def delete_deployment(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_OPERATOR')
    if not has_role:
        raise Exception('403 Forbidden')

    deployment_db.delete_deployment(args.get('deploymentId'))
    return 0


def find_and_call_ability(**args):
    results = deployment_db.get_deployments_by_uuid(args.get('uuid'))

    if len(results) > 0:
        deployment = Deployment()
        deployment.__dict__ = results[0]
        deployment.callCount += 1
        deployment_db.update_deployment_call_count(deployment)
        return results[0]
    else:
        raise Exception('404 ability not found')
