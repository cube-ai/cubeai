from .health_check import HealthChecker
from .hello import Hello
from .token_key_api import TokenKeyApi
from .token_api import TokenApi
from .verify_code_api import VerifyCodeApi
from .account_api import AccountApi, ChangePasswordApi, RegisterApi, ActivateApi, ResetPasswordApi
from .user_api import UserApiA, UserApiB, UserExistLoginApi, UserExistEmailApi, UserExistPhoneApi
from .authority_api import AuthorityApiA, AuthorityApiB
from .message_api import MessageApiA, MessageApiB, MessageUnreadApi
from .article_api import ArticleApiA, ArticleApiB
from .attachment_api import AttachmentGetApi, AttachmentUploadApi, AttachmentDelApi
from .application_api import ApplicationApiA, ApplicationApiB, ApplicationApiC
from .random_picture_api import RandomPictureApi


API_ROUTES = [
    (r'/api/hello', Hello),
    (r'/management/health', HealthChecker),
    (r'/oauth/token_key', TokenKeyApi),
    (r'/oauth/token', TokenApi),
    (r'/api/verify-codes', VerifyCodeApi),
    (r'/api/account', AccountApi),
    (r'/api/account/change-password', ChangePasswordApi),
    (r'/api/account/reset-password/(\w+)', ResetPasswordApi),
    (r'/api/register', RegisterApi),
    (r'/api/activate', ActivateApi),
    (r'/api/users/exist/login/(.*)', UserExistLoginApi),
    (r'/api/users/exist/email/(.*)', UserExistEmailApi),
    (r'/api/users/exist/phone/(\w+)', UserExistPhoneApi),
    (r'/api/users/authorities', AuthorityApiA),
    (r'/api/users/authorities/(\w+)', AuthorityApiB),
    (r'/api/users', UserApiA),
    (r'/api/users/(.*)', UserApiB),
    (r'/api/messages', MessageApiA),
    (r'/api/messages/unread-count', MessageUnreadApi),
    (r'/api/messages/(\w+)', MessageApiB),
    (r'/api/articles', ArticleApiA),
    (r'/api/articles/(\w+)', ArticleApiB),
    (r'/api/attachments', AttachmentGetApi),
    (r'/api/attachments/upload', AttachmentUploadApi),
    (r'/api/attachments/(\w+)', AttachmentDelApi),
    (r'/api/randompicture/(\w+)/(\w+)', RandomPictureApi),
    (r'/api/applications', ApplicationApiA),
    (r'/api/applications/(\w+)', ApplicationApiB),
    (r'/api/applicationsp', ApplicationApiC),

]
