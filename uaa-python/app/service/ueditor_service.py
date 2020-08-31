import uuid
from app.service import token_service, nexus_client


def ueditor(request):
    if request.method == 'GET':
        return ueditor_get(request)
    elif request.method == 'POST':
        return ueditor_post(request)
    else:
        raise Exception('Unsupported HTTP method.')


def ueditor_get(request):
    action = str(request.arguments.get('action')[0], encoding='utf-8')

    if action == 'config':
        return ueditor_config
    else:
        return {'state': '不支持操作！'}


def ueditor_post(request):
    token = token_service.get_token(request)
    if not token.is_valid:
        raise Exception('403 Forbidden')

    action = str(request.arguments.get('action')[0], encoding='utf-8')
    if action != 'uploadimage' and action != 'uploadscrawl':
        return {'state': '不支持操作！'}

    try:
        file_obj = request.files.get('upfile')[0]
        filename = file_obj.filename
        filebody = file_obj.body
    except:
        return {'state': '文件为空！'}

    name = str(uuid.uuid4()).replace('-', '')
    ext = filename[filename.rfind('.'):]
    filename = name + ext

    short_url = "ueditor/picture/" + filename
    long_url = nexus_client.upload_artifact_data(short_url, filebody)

    if long_url:
        return {
            'state': 'SUCCESS',
            'url': long_url,
        }
    else:
        return {'state': '上传文件出错'}


ueditor_config = {
    'imageActionName': 'uploadimage',
    'imageFieldName': 'upfile',
    'imageMaxSize': 2048000,
    'imageAllowFiles': ['.png', '.jpg', '.jpeg', '.gif', '.bmp'],
    'imageCompressEnable': True,
    'imageCompressBorder': 1600,
    'imageInsertAlign': 'none',
    'imageUrlPrefix': '',
    'imagePathFormat': '/contest/api/ueditor',
    'scrawlActionName': 'uploadscrawl',
    'scrawlFieldName': 'upfile',
    'scrawlPathFormat': '/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}',
    'scrawlMaxSize': 2048000,
    'scrawlUrlPrefix': '',
    'scrawlInsertAlign': 'none',
    'snapscreenActionName': 'uploadimage',
    'snapscreenPathFormat': '/contest/api/ueditor',
    'snapscreenUrlPrefix': '',
    'snapscreenInsertAlign': 'none',
    'catcherLocalDomain': ['127.0.0.1', 'localhost', 'img.baidu.com'],
    'catcherActionName': 'catchimage',
    'catcherFieldName': 'source',
    'catcherPathFormat': '/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}',
    'catcherUrlPrefix': '',
    'catcherMaxSize': 2048000,
    'catcherAllowFiles': ['.png', '.jpg', '.jpeg', '.gif', '.bmp'],
    'videoActionName': 'uploadvideo',
    'videoFieldName': 'upfile',
    'videoPathFormat': '/ueditor/jsp/upload/video/{yyyy}{mm}{dd}/{time}{rand:6}',
    'videoUrlPrefix': '',
    'videoMaxSize': 102400000,
    'videoAllowFiles': [
        '.flv', '.swf', '.mkv', '.avi', '.rm', '.rmvb', '.mpeg', '.mpg',
        '.ogg', '.ogv', '.mov', '.wmv', '.mp4', '.webm', '.mp3', '.wav', '.mid'],
    'fileActionName': 'uploadfile',
    'fileFieldName': 'upfile',
    'filePathFormat': '/ueditor/jsp/upload/file/{yyyy}{mm}{dd}/{time}{rand:6}',
    'fileUrlPrefix': '',
    'fileMaxSize': 51200000,
    'fileAllowFiles': [
        '.png', '.jpg', '.jpeg', '.gif', '.bmp',
        '.flv', '.swf', '.mkv', '.avi', '.rm', '.rmvb', '.mpeg', '.mpg',
        '.ogg', '.ogv', '.mov', '.wmv', '.mp4', '.webm', '.mp3', '.wav', '.mid',
        '.rar', '.zip', '.tar', '.gz', '.7z', '.bz2', '.cab', '.iso',
        '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.pdf', '.txt', '.md', '.xml'
    ],
    'imageManagerActionName': 'listimage',
    'imageManagerListPath': '/ueditor/jsp/upload/image/',
    'imageManagerListSize': 20,
    'imageManagerUrlPrefix': '',
    'imageManagerInsertAlign': 'none',
    'imageManagerAllowFiles': ['.png', '.jpg', '.jpeg', '.gif', '.bmp'],
    'fileManagerActionName': 'listfile',
    'fileManagerListPath': '/ueditor/jsp/upload/file/',
    'fileManagerUrlPrefix': '',
    'fileManagerListSize': 20,
    'fileManagerAllowFiles': [
        '.png', '.jpg', '.jpeg', '.gif', '.bmp',
        '.flv', '.swf', '.mkv', '.avi', '.rm', '.rmvb', '.mpeg', '.mpg',
        '.ogg', '.ogv', '.mov', '.wmv', '.mp4', '.webm', '.mp3', '.wav', '.mid',
        '.rar', '.zip', '.tar', '.gz', '.7z', '.bz2', '.cab', '.iso',
        '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.pdf', '.txt', '.md', '.xml'
    ]
}

