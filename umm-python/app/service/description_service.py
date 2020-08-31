from app.domain.description import Description
from app.service import token_service
from app.database import description_db


def find_description(**args):
    results = description_db.get_descriptions(args.get('solutionUuid'))

    if len(results) < 1:
        raise Exception('description not found!')

    return results[0]


def update_description(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    description = Description()
    description.__dict__ = description_db.get_description(args.get('descriptionId'))

    if user_login != description.authorLogin and not has_role:
        raise Exception('403 Forbidden')

    description.content = args.get('content')
    description_db.update_description_content(description)
    return 0


