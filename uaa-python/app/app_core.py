# -*- coding: utf-8 -*-
from apscheduler.schedulers.tornado import TornadoScheduler
from app.global_data.global_data import g
from app.service import scheduler_service, oauth_service, verify_code_service, message_service, special_service
from app.service import account_service, user_service, authority_service
from app.service import article_service, application_service, random_picture_service, random_avatar_service, attachment_service


class AppCore(object):
    
    # 声明对外公开可通过API接口访问的方法。如public_actions未声明或值为None，则默认本class中定义的所有方法都对外公开。
    public_actions = None

    def __init__(self):
        g.load_global_data()
        if not g.init_success:
            raise Exception('初始化加载 global_data 失败！')

        scheduler = TornadoScheduler()
        scheduler.add_job(scheduler_service.do_every_day, 'cron', day_of_week='0-6', hour=0, minute=30)
        scheduler.start()

    def hello(self, **args):
        return 'Hello world!'

    def special_api(self, request):
        return special_service.special_api(request)

    def get_public_key(self, **args):
        return oauth_service.get_public_key()

    def get_token(self, **args):
        return oauth_service.get_token(**args)

    def get_verify_code(self, **args):
        return verify_code_service.get_verify_code()

    def validate_verify_code(self, **args):
        return verify_code_service.validate_verify_code(**args)

    def get_current_account(self, **args):
        return account_service.get_current_account(**args)

    def update_current_account(self, **args):
        return account_service.update_current_account(**args)

    def register_user(self, **args):
        return account_service.register_user(**args)

    def activate_user(self, **args):
        return account_service.activate_user(**args)

    def change_password(self, **args):
        return account_service.change_password(**args)

    def password_reset_init(self, **args):
        return account_service.password_reset_init(**args)

    def password_reset_finish(self, **args):
        return account_service.password_reset_finish(**args)

    def create_user(self, **args):
        return user_service.create_user(**args)

    def update_user(self, **args):
        return user_service.update_user(**args)

    def get_users(self, **args):
        return user_service.get_users(**args)

    def find_user(self, **args):
        return user_service.find_user(**args)

    def delete_user(self, **args):
        return user_service.delete_user(**args)

    def get_login_exist(self, **args):
        return user_service.get_login_exist(**args)

    def get_email_exist(self, **args):
        return user_service.get_email_exist(**args)

    def get_phone_exist(self, **args):
        return user_service.get_phone_exist(**args)

    def create_authority(self, **args):
        return authority_service.create_authority(**args)

    def get_authorities(self, **args):
        return authority_service.get_authorities(**args)

    def delete_authority(self, **args):
        return authority_service.delete_authority(**args)

    def get_random_picture(self, **args):
        return random_picture_service.get_random_picture(**args)

    def get_random_avatar(self, **args):
        return random_avatar_service.get_random_avatar(**args)
    
    def create_application(self, **args):
        return application_service.create_application(**args)

    def update_application(self, **args):
        return application_service.update_application(**args)

    def get_applications(self, **args):
        return application_service.get_applications(**args)

    def find_application(self, **args):
        return application_service.find_application(**args)

    def delete_application(self, **args):
        return application_service.delete_application(**args)

    def create_article(self, **args):
        return article_service.create_article(**args)

    def update_article(self, **args):
        return article_service.update_article(**args)

    def get_articles(self, **args):
        return article_service.get_articles(**args)

    def find_article(self, **args):
        return article_service.find_article(**args)

    def delete_article(self, **args):
        return article_service.delete_article(**args)

    def get_attachments(self, **args):
        return attachment_service.get_attachments(**args)

    def delete_attachment(self, **args):
        return attachment_service.delete_attachment(**args)

    def upload_attachment(self, **args):
        return attachment_service.upload_attachment(**args)

    def get_messages(self, **args):
        return message_service.get_messages(**args)

    def get_message(self, **args):
        return message_service.get_message(**args)

    def delete_message(self, **args):
        return message_service.delete_message(**args)

    def send_message(self, **args):
        return message_service.send_message(**args)

    def send_multicast_message(self, **args):
        return message_service.send_multicast_message(**args)

    def mark_message_viewed(self, **args):
        return message_service.mark_message_viewed(**args)

    def mark_message_deleted(self, **args):
        return message_service.mark_message_deleted(**args)

    def get_unread_message_count(self, **args):
        return message_service.get_unread_message_count(**args)
