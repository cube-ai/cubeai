from app.domain.star import Star
from app.service import token_service
from app.database import star_db
from app.utils import mytime


def create_star(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')
    user_login = token.username

    star = Star()
    star.__dict__ = args.get('star')
    star.userLogin = user_login
    star.starDate = mytime.now()

    id = star_db.create_star(star)
    return id


def get_stars(**args):
    userLogin = args.get('userLogin')
    targetUuid = args.get('targetUuid')
    targetType = args.get('targetType')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    where = ''
    if userLogin is not None:
        where += 'and user_login = "{}" '.format(userLogin)
    if targetUuid is not None:
        where += 'and target_uuid = "{}" '.format(targetUuid)
    if targetType is not None:
        where += 'and target_type = "{}" '.format(targetType)
    where = where[4:]

    if where != '':
        where = 'WHERE ' + where
    total, results = star_db.get_stars(where, pageable)
    return {
        'total': total,
        'results': results,
    }


def delete_star(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')
    user_login = token.username

    star_user = star_db.get_star_user_login(args.get('starId'))
    if user_login != star_user:
        raise Exception('403 Forbidden')

    star_db.delete_star(args.get('starId'))
    return 0


def delete_star_by_target_uuid(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')
    user_login = token.username

    where = 'WHERE user_login = "{}" and target_uuid = "{}"'.format(user_login, args.get('targetUuid'))
    _, stars = star_db.get_stars(where, None)

    star_db.delete_star(stars[0].get('id'))

    return 0


def get_user_stared_uuid_list(**args):
    results = star_db.get_stared_uuid_list(args.get('userLogin'))
    return results
