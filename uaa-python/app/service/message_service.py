import json
from app.global_data.global_data import g
from app.service import token_service
from app.domain.message import Message
from app.database import message_db
from app.utils import mytime


def get_messages(**args):
    http_request = args.get('http_request')
    receiver = args.get('receiver')
    sender = args.get('sender')
    deleted = args.get('deleted')
    filter = args.get('filter')
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    if receiver is None and sender is None:
        raise Exception('403 Forbidden')

    if not (receiver is not None and receiver == user_login or sender is not None and sender == user_login):
        raise Exception('403 Forbidden')

    where1 = ''
    if receiver is not None:
        where1 += 'and receiver = "{}" '.format(receiver)
    if sender is not None:
        where1 += 'and sender = "{}" '.format(sender)
    if deleted is not None:
        where1 += 'and deleted = {} '.format(deleted)
    where1 = where1[4:]

    where2 = ''
    if filter is not None:
        where2 += 'receiver like "%{}%"'.format(filter)
        where2 += ' or sender like "%{}%"'.format(filter)
        where2 += ' or subject like "%{}%"'.format(filter)
        where2 += ' or content like "%{}%"'.format(filter)

    where = ''
    if where1:
        where += 'and ({})'.format(where1)
    if where2:
        where += 'and ({})'.format(where2)
    if where:
        where = where[4:]

    if where != '':
        where = 'WHERE ' + where

    total, results = message_db.get_messages(where, pageable)

    return {
        'total': total,
        'results': results,
    }


def get_message(id, http_request):
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    message = message_db.find_one_by_id(id)
    if message is None or (message.sender != user_login and message.receiver != user_login):
        raise Exception('403 Forbidden')

    return message.__dict__


def delete_message(id, http_request):
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    message = message_db.find_one_by_id(id)
    if message is None or message.receiver != user_login:
        raise Exception('403 Forbidden')

    message_db.delete_message(id)
    return 0


def send_message(message, http_request):
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    msg_dict = message
    message = Message()
    message.__dict__ = msg_dict

    message.sender = user_login
    message.viewed = False
    message.deleted = False
    message.createdDate = mytime.now()
    message.modifiedDate = mytime.now()
    id = message_db.create_message(message)
    send_unread_message_count(message.receiver)

    return id


def send_multicast_message(draft, http_request):
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    message = Message()
    message.__dict__ = draft.get('message')
    receivers = draft.get('receivers')

    message.sender = user_login
    message.viewed = False
    message.deleted = False
    message.createdDate = mytime.now()
    message.modifiedDate = mytime.now()

    for receiver in receivers:
        message.receiver = receiver
        message_db.create_message(message)
        send_unread_message_count(receiver)

    return 0


def mark_message_viewed(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    id = args.get('id')
    viewed = args.get('viewed')

    message = message_db.find_one_by_id(id)
    if message.receiver != user_login:
        raise Exception('403 Forbidden')

    message_db.update_message_viewed(id, viewed)
    send_unread_message_count(message.receiver)
    return 0


def mark_message_deleted(**args):
    http_request = args.get('http_request')
    token = token_service.get_token(http_request)
    user_login = token.username
    if user_login is None:
        raise Exception('403 Forbidden')

    id = args.get('id')

    deleted = args.get('deleted')

    message = message_db.find_one_by_id(id)
    if message.receiver != user_login:
        raise Exception('403 Forbidden')

    message_db.update_message_deleted(id, deleted)
    send_unread_message_count(message.receiver)
    return 0


def get_unread_message_count(**args):
    receiver = args.get('receiver')
    deleted = args.get('deleted')

    count = message_db.get_unread_count(receiver, deleted)
    return count


def send_unread_message_count(receiver):
    count = message_db.get_unread_count(receiver, deleted=False)

    msg = {
        'type': 'data',
        'topic': 'message_' + receiver,
        'content': {
            'unread_msgs': count,
        },
    }

    g.websocket.send(json.dumps(msg))
