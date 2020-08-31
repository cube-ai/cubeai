from app.service import token_service
from app.service import umm_client
from app.service import nexus_client
from app.domain.solution import Solution
from app.domain.document import Document


def upload_document(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    if not token.is_valid:
        raise Exception('403 Forbidden')
    user_login = token.username

    file_body = args.get('stream')
    solution_uuid = args.get('path_arg')
    res = umm_client.get_solutions(solution_uuid, token.jwt)
    if res['status'] != 'ok' or res['value']['total'] < 1:
        raise Exception('文档模型不存在')

    solution = Solution()
    solution.__dict__ = res['value']['results'][0]

    if solution.authorLogin != user_login:
        raise Exception('403 Forbidden')

    filename = http_request.files.get('upload_document')[0].filename
    short_url = solution.authorLogin + '/' + solution_uuid + '/document/' + filename
    long_url = nexus_client.upload_artifact_data(short_url, file_body)

    if long_url is None:
        raise Exception('向Nexus上传文件出错！')

    document = Document()
    document.solutionUuid = solution.uuid
    document.name = filename
    document.url = long_url
    document.fileSize = len(file_body)
    umm_client.create_document(document, token.jwt)

    return 0


def delete_document(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    user_login = token.username
    has_role = token.has_role('ROLE_MANAGER')

    document = Document()
    document.__dict__ = umm_client.find_document(args.get('documentId'))['value']

    if user_login != document.authorLogin and not has_role:
        raise Exception('403 Forbidden')

    nexus_client.delete_artifact(document.url)
    umm_client.delete_document(args.get('documentId'), jwt=token.jwt)

    return 0


def download_document(**args):
    result =  nexus_client.get_artifact(args.get('url'))
    return result
