from app.database import authority_db
from app.service import token_service


def get_authorities(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    return authority_db.get_authorities()


def create_authority(authority, http_request):
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    authority = authority.upper()
    authorities = authority_db.get_authorities()
    if not authority in authorities:
        authority_db.create_authority(authority)

    return 0


def delete_authority(authority, http_request):
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    authorities = authority_db.get_authorities()
    if authority in authorities:
        authority_db.delete_authority(authority)

    return 0
