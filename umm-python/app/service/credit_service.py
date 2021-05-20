from app.domain.credit import Credit
from app.domain.credit_history import CreditHistory
from app.service import token_service
from app.database import credit_db, credit_history_db
from app.utils import mytime


def get_my_credit(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    return find_user_credit(token.username)


def get_credits(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    user_login = args.get('userLogin')
    return credit_db.get_credits(user_login)


def update_credit(**args):
    token = token_service.get_token(args.get('http_request'))
    has_role = token.has_role('ROLE_ADMIN')
    if not has_role:
        raise Exception('403 Forbidden')

    credit = Credit()
    credit.__dict__ = credit_db.get_credit(args.get('creditId'))
    do_update_credit(credit, args.get('creditPlus'), '管理员后台配置')

    return 0


def get_credit_history(**args):
    target_login = args.get('targetLogin')  # 带 targetLogin 表示是管理员在查看
    pageable = {
        'page': args.get('page'),
        'size': args.get('size'),
        'sort': args.get('sort'),
    }

    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    has_role = token.has_role('ROLE_ADMIN')

    if user_login is None or (target_login is not None and not has_role):
        raise Exception('403 Forbidden')

    if target_login is not None:
        user_login = target_login

    where = 'WHERE user_login = "{}"'.format(user_login)

    total, results = credit_history_db.get_credit_historys(where, pageable)
    return {
        'total': total,
        'results': results,
    }


def find_user_credit(user_login):
    credit_dict = credit_db.get_user_credit(user_login)

    if credit_dict is None:
        credit = Credit()
        credit.userLogin = user_login
        credit.credit = 50
        credit_db.create_credit(credit)
        credit_dict = credit.__dict__

        credit_history = CreditHistory()
        credit_history.userLogin = user_login
        credit_history.creditPlus = 50
        credit_history.currentCredit = 50
        credit_history.comment = '初始化赋值50积分'
        credit_history.modifyDate = mytime.now()
        credit_history_db.create_credit_history(credit_history)

    return credit_dict


def do_update_credit(credit, credit_plus, comment):
    credit.credit += int(credit_plus)
    credit_db.update_credit(credit)

    credit_history = CreditHistory()
    credit_history.userLogin = credit.userLogin
    credit_history.creditPlus = credit_plus
    credit_history.currentCredit = credit.credit
    credit_history.comment = comment
    credit_history.modifyDate = mytime.now()
    credit_history_db.create_credit_history(credit_history)

