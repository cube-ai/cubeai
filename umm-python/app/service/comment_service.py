from app.domain.comment import Comment
from app.service import token_service
from app.database import comment_db
from app.utils import mytime


def create_comment(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')
    user_login = token.username

    comment = Comment()
    comment.__dict__ = args.get('comment')
    comment.userLogin = user_login
    comment.createdDate = mytime.now()
    comment.modifiedDate = mytime.now()

    id = comment_db.create_comment(comment)
    return id


def get_comments(**args):
    solutionUuid = args.get('solutionUuid')
    parentUuid = args.get('parentUuid')

    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    where = 'WHERE solution_uuid = "{}" and parent_uuid = "{}"'.format(solutionUuid, parentUuid)
    total, results = comment_db.get_comments(where, pageable)

    return {
        'total': total,
        'results': results,
    }


def delete_comment(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    comment = comment_db.get_comment(args.get('commentId'))
    if user_login != comment.get('userLogin') and not has_role:
        raise Exception('403 Forbidden')

    comment_db.delete_comment(args.get('commentId'))
    return 0
