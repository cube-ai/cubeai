import uuid
from app.service import token_service, nexus_client


def editormd(request):
    if request.method == 'POST':
        return editormd_post(request)
    else:
        raise Exception('Unsupported HTTP method.')


def editormd_post(request):
    token = token_service.get_token(request)
    if not token.is_valid:
        raise Exception('403 Forbidden')

    try:
        file_obj = request.files.get('editormd-image-file')[0]
        filename = file_obj.filename
        filebody = file_obj.body
    except:
        return {
            'success': 0,
            'message': '上传失败',
        }

    name = str(uuid.uuid4()).replace('-', '')
    ext = filename[filename.rfind('.'):]
    filename = name + ext

    short_url = "editormd/picture/" + filename
    long_url = nexus_client.upload_artifact_data(short_url, filebody)

    if long_url:
        return {
            'success': 1,
            'message': '上传成功',
            'url': long_url,
        }
    else:
        return {
            'success': 0,
            'message': '上传失败',
        }
