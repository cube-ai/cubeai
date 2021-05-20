from app.service import oauth_service, ueditor_service, editormd_service


def special_api(request):
    if request.path.startswith('/special/public_key'):
        return oauth_service.get_public_key()

    if request.path.startswith('/special/token'):
        args = {
            'http_request': request,
            'grant_type': str(request.arguments.get('grant_type')[0], encoding='utf-8')
        }
        return oauth_service.get_token(**args)

    if request.path.startswith('/special/ueditor'):
        return ueditor_service.ueditor(request)

    if request.path.startswith('/special/editormd'):
        return editormd_service.editormd(request)
