from app.service import token_service
from app.domain.application import Application
from app.database import application_db
from app.utils import mytime


def create_application(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_APPLICATION')
    if not has_role:
        raise Exception('403 Forbidden')

    application = Application()
    application.__dict__ = args.get('application')
    application.complete_attrs()
    application.createdBy = user_login
    application.modifiedBy = user_login
    application.createdDate = mytime.now()
    application.modifiedDate = mytime.now()

    id = application_db.create_application(application)
    return id


def update_application(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_APPLICATION')
    if not has_role:
        raise Exception('403 Forbidden')

    application = Application()
    application.__dict__ = args.get('application')
    application.modifiedBy = user_login
    application.modifiedDate = mytime.now()

    application_db.update_application(application)
    return 0


def get_applications(**args):
    uuid = args.get('uuid')
    name = args.get('name')
    owner = args.get('owner')
    subject1 = args.get('subject1')
    subject2 = args.get('subject2')
    subject3 = args.get('subject3')
    filter = args.get('filter')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }
    with_picture = args.get('with_picture')

    if uuid is not None:
        result = application_db.get_applications_by_uuid(uuid)
        if len(result) > 0:
            result[0]['pictureUrl'] = ''
        return {
            'applicatons': result,
        }

    where1 = ''
    if name is not None:
        where1 += 'and name = "{}" '.format(name)
    if owner is not None:
        where1 += 'and owner = "{}" '.format(owner)
    if subject1 is not None:
        where1 += 'and subject_1 = "{}" '.format(subject1)
    if subject2 is not None:
        where1 += 'and subject_2 = "{}" '.format(subject2)
    if subject3 is not None:
        where1 += 'and subject_3 = "{}" '.format(subject3)

    where1 = where1[4:]

    where2 = ''
    if filter is not None:
        where2 += 'name like "%{}%"'.format(filter)
        where2 += ' or owner like "%{}%"'.format(filter)
        where2 += ' or subject_1 like "%{}%"'.format(filter)
        where2 += ' or subject_2 like "%{}%"'.format(filter)
        where2 += ' or subject_3 like "%{}%"'.format(filter)
        where2 += ' or summary like "%{}%"'.format(filter)

    where = ''
    if where1:
        where += 'and ({})'.format(where1)
    if where2:
        where += 'and ({})'.format(where2)
    if where:
        where = where[4:]

    if where != '':
        where = 'WHERE ' + where
    total, results = application_db.get_applications(where, pageable)

    if not with_picture:
        for application in results:
            application['pictureUrl'] = ''

    return {
        'total': total,
        'results': results,
    }


def find_application(id, http_request):
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_APPLICATION')
    if not has_role:
        raise Exception('403 Forbidden')

    return application_db.get_application(id)


def delete_application(id, http_request):
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_APPLICATION')
    if not has_role:
        raise Exception('403 Forbidden')

    application_db.delete_application(id)
    return 0
