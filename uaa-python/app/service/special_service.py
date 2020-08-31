from app.service import ueditor_service


def special_api(request):
    if request.path.startswith('/special/ueditor') :
        return ueditor_service.ueditor(request)
