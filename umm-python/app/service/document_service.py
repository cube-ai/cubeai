from app.domain.document import Document
from app.service import token_service
from app.database import document_db, solution_db
from app.utils import mytime


def create_document(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    document = Document()
    document.__dict__ = args.get('document')
    solutions = solution_db.get_solutions_by_uuid(document.solutionUuid)
    solution = solutions[0]

    user_login = token.username
    if user_login != solution.get('authorLogin'):
        raise Exception('403 Forbidden')

    document.authorLogin = user_login
    document.createdDate = mytime.now()
    document.modifiedDate = mytime.now()
    id = document_db.create_document(document)
    return id


def get_documents(**args):
    solution_uuid = args.get('solutionUuid')
    name = args.get('name')

    where = 'WHERE solution_uuid = "{}"'.format(solution_uuid)
    if name is not None:
        where += ' and name = "{}"'.format(name)

    results = document_db.get_documents(where)
    return results


def find_document(**args):
    result = document_db.get_document(args.get('documentId'))
    return result


def delete_document(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    document = document_db.get_document(args.get('documentId'))

    if user_login != document.get('authorLogin') and not has_role:
        raise Exception('403 Forbidden')

    document_db.delete_document(args.get('documentId'))
    return 0
