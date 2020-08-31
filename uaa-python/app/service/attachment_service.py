import uuid
from app.domain.attachment import Attachment
from app.service import token_service
from app.service import nexus_client
from app.database import attachment_db
from app.utils import mytime


def get_attachments(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    has_role = token.has_role('ROLE_CONTENT')
    if not has_role:
        raise Exception('403 Forbidden')

    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    total, results = attachment_db.get_attachments('', pageable)
    return {
        'total': total,
        'results': results,
    }


def delete_attachment(id, http_request):
    token = token_service.get_token(http_request)
    user_login = token.username
    has_role = token.has_role('ROLE_ADMIN')

    attachment = attachment_db.get_attachment(id)

    if not has_role and attachment.authorLogin != user_login:
        raise Exception('403 Forbidden')

    nexus_client.delete_artifact(attachment.url)
    attachment_db.delete_attachment(id)
    return 0


def upload_attachment(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    user_login = token.username
    has_role = token.has_role('ROLE_CONTENT')

    if not has_role:
        raise Exception('403 Forbidden')

    file_body = args.get('stream')
    file_obj = http_request.files.get('upload_attachment')[0]
    filename = file_obj.filename

    short_url = 'attachment/{}/{}'.format(str(uuid.uuid4()).replace('-', ''), filename)
    long_url = nexus_client.upload_artifact_data(short_url, file_body)

    if long_url is None:
        raise Exception('400 Upload to Nexus fail')

    attachment = Attachment()
    attachment.authorLogin = user_login
    attachment.name = filename
    attachment.url = long_url
    attachment.fileSize = len(file_body)
    attachment.createdDate = mytime.now()
    attachment.modifiedDate = mytime.now()

    id = attachment_db.create_attachment(attachment)
    return id
