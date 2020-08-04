from app.domain.credit import Credit
from app.domain.credit_history import CreditHistory
from app.database import credit_db, credit_history_db
from app.utils import mytime


async def find_user_credit(user_login):
    credit = await credit_db.get_user_credit(user_login)

    if credit is None:
        credit = Credit()
        credit.userLogin = user_login
        credit.credit = 50
        await credit_db.create_credit(credit)

        credit_history = CreditHistory()
        credit_history.userLogin = user_login
        credit_history.creditPlus = 50
        credit_history.currentCredit = 50
        credit_history.comment = '初始化赋值50积分'
        credit_history.modifyDate = mytime.now()
        await credit_history_db.create_credit_history(credit_history)

    return credit


async def update_credit(credit, credit_plus, comment):
    credit.credit += int(credit_plus)
    await credit_db.update_credit(credit)

    credit_history = CreditHistory()
    credit_history.userLogin = credit.userLogin
    credit_history.creditPlus = credit_plus
    credit_history.currentCredit = credit.credit
    credit_history.comment = comment
    credit_history.modifyDate = mytime.now()
    await credit_history_db.create_credit_history(credit_history)
