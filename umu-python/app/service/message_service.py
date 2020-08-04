from app.service import uaa_client
from app.globals.globals import g


def send_message(receiver, subject, content, url, urgent):
    message = {
        'receiver': receiver,
        'subject': subject,
        'content': content,
        'url': url,
        'urgent': urgent,
    }

    uaa_client.send_message(message, g.oauth_client.get_jwt())
