import json
import base64
import random
import requests
import datetime
import python_jwt
from jwcrypto import jwk
from app.global_data.global_data import g
from app.database import user_db, gitee_user_db, github_user_db, qq_user_db
from app.service import passwd_service, random_avatar_service
from app.domain.user import User
from app.domain.gitee_user import GiteeUser
from app.domain.github_user import GithubUser
from app.domain.qq_user import QqUser
from app.utils import mytime


def get_public_key(**args):
    return {
        'alg': 'SHA256withRSA',
        'value': str(g.config.public_key, encoding='utf-8')
    }


def get_token(**args):
    http_request = args.get('http_request')
    grant_type = args.get('grant_type')

    if grant_type == 'client_credentials':
        if not verify_basic_authorization(http_request.headers.get('Authorization'), 'internal'):
            raise Exception('403 Forbidden')

        result = gen_client_token()
        if result is None:
            raise Exception('400 bad request')

        return result

    if grant_type == 'password':
        if not verify_basic_authorization(http_request.headers.get('Authorization'), 'web_app'):
            raise Exception('403 Forbidden')

        username = args.get('username')
        password = args.get('password')

        user = verify_user_password(username, password)
        if user is None:
            raise Exception('403 Forbidden')

        if not user.activated:
            raise Exception('403 Forbidden, 用户未激活')

        result = gen_user_token(user)
        if result is None:
            raise Exception('400 bad request')

        return result

    if grant_type == 'refresh_token':
        if not verify_basic_authorization(http_request.headers.get('Authorization'), 'web_app'):
            raise Exception('403 Forbidden')

        refresh_token = args.get('refresh_token')
        if not refresh_token:
            raise Exception('400 bad request')

        result = gen_from_refresh_token(refresh_token)
        if result is None:
            raise Exception('400 bad request')

        return result

    if grant_type == 'third_party_oauth':
        if not verify_basic_authorization(http_request.headers.get('Authorization'), 'internal'):
            raise Exception('403 Forbidden')

        third_party = args.get('third_party')
        code = args.get('code')

        if third_party == 'gitee':
            user = oauth_gitee_user(code)
        elif third_party == 'github':
            user = oauth_github_user(code)
        elif third_party == 'qq':
            user = oauth_qq_user(code)
        else:
            raise Exception('Invalid third party')

        if user is None:
            raise Exception('第三方认证失败')

        if not user.activated:
            raise Exception('用户未激活')

        result = gen_user_token(user)
        if result is None:
            raise Exception('生成用户token失败')

        return result

    raise Exception('400 bad request')


def verify_basic_authorization(authorization, client_id):
    if authorization is None or authorization.find('Basic') != 0:
        return False

    basic = authorization[6:] # 去除前缀“Basic ”
    basic = str(base64.b64decode(basic.encode()), encoding='utf-8')

    if client_id == 'internal':
        return basic == 'internal:internal'

    if client_id == 'web_app':
        return basic == 'web_app:changeit'

    return False


def gen_client_token():
    claims = {
        'scope': [
            'web-app'
        ],
        'client_id': 'internal'
    }
    try:
        jwt = python_jwt.generate_jwt(claims,
                                      priv_key=jwk.JWK.from_pem(g.config.private_key),
                                      algorithm='RS256',
                                      lifetime=datetime.timedelta(seconds=300),
                                      jti_size=16)
    except:
        return None

    try:
        jwt_decoded = python_jwt.verify_jwt(
            jwt,
            pub_key=jwk.JWK.from_pem(g.config.public_key),
            allowed_algs=['RS256'],
            checks_optional=True,
            ignore_not_implemented=True
        )
    except:
        return None

    result = {
        'access_token': jwt,
        'token_type': 'bearer',
        'scope': 'web-app',
        "iat": jwt_decoded[1].get('iat'),
        'expires_in': jwt_decoded[1].get('exp') - jwt_decoded[1].get('iat') - 1,
        "jti": jwt_decoded[1].get('jti')
    }
    return result


def verify_user_password(username, password):

    user = user_db.find_one_by_kv('login', username)
    if user is not None and passwd_service.verify_passwd(user.password, password):
        return user

    user = user_db.find_one_by_kv('email', username)
    if user is not None and passwd_service.verify_passwd(user.password, password):
        return user

    user = user_db.find_one_by_kv('phone', username)
    if user is not None and passwd_service.verify_passwd(user.password, password):
        return user

    return None


def gen_user_token(user):

    claims = {
        'user_name': user.login,
        'authorities': user.authorities,
        'scope': [
            'openid'
        ],
        'client_id': 'web_app'
    }
    try:
        access_token = python_jwt.generate_jwt(
            claims,
            priv_key=jwk.JWK.from_pem(g.config.private_key),
            algorithm='RS256',
            lifetime=datetime.timedelta(seconds=300),  # 5 min
            jti_size=16
        )
    except:
        return None

    try:
        jwt_decoded = python_jwt.verify_jwt(
            access_token,
            pub_key=jwk.JWK.from_pem(g.config.public_key),
            allowed_algs=['RS256'],
            checks_optional=True,
            ignore_not_implemented=True
        )
    except:
        return None

    try:
        refresh_token = python_jwt.generate_jwt(
            claims,
            priv_key=jwk.JWK.from_pem(g.config.private_key),
            algorithm='RS256',
            lifetime=datetime.timedelta(seconds=604800),  # 7 days
            jti_size=16
        )
    except:
        return None

    result = {
        'access_token': access_token,
        'refresh_token': refresh_token,
        'token_type': 'bearer',
        'scope': 'web-app',
        'iat': jwt_decoded[1].get('iat'),
        'exp': jwt_decoded[1].get('exp'),
        'expires_in': jwt_decoded[1].get('exp') - jwt_decoded[1].get('iat') - 1,
        'jti': jwt_decoded[1].get('jti')
    }

    return result


def gen_from_refresh_token(refresh_token):

    try:
        jwt_decoded = python_jwt.verify_jwt(
            refresh_token,
            pub_key=jwk.JWK.from_pem(g.config.public_key),
            allowed_algs=['RS256'],
            checks_optional=True,
            ignore_not_implemented=True
        )
    except:
        return None

    user_name = jwt_decoded[1].get('user_name')
    # 用户的ROLE可能改变，所以refresh token时应该重新查询用户数据库，而不能直接用上次token中信息
    user = user_db.find_one_by_kv('login', user_name)
    if user is None:
        return None

    claims = {
        'user_name': user.login,
        'authorities': user.authorities,
        'scope': [
            'openid'
        ],
        'client_id': 'web_app'
    }
    try:
        access_token = python_jwt.generate_jwt(
            claims,
            priv_key=jwk.JWK.from_pem(g.config.private_key),
            algorithm='RS256',
            lifetime=datetime.timedelta(seconds=300),  # 5 min
            jti_size=16
        )
    except:
        return None

    try:
        jwt_decoded = python_jwt.verify_jwt(
            access_token,
            pub_key=jwk.JWK.from_pem(g.config.public_key),
            allowed_algs=['RS256'],
            checks_optional=True,
            ignore_not_implemented=True
        )
    except:
        return None

    try:
        refresh_token = python_jwt.generate_jwt(
            claims,
            priv_key=jwk.JWK.from_pem(g.config.private_key),
            algorithm='RS256',
            lifetime=datetime.timedelta(seconds=604800),  # 7 days
            jti_size=16
        )
    except:
        return None

    result = {
        'access_token': access_token,
        'refresh_token': refresh_token,
        'token_type': 'bearer',
        'scope': 'web-app',
        'iat': jwt_decoded[1].get('iat'),
        'exp': jwt_decoded[1].get('exp'),
        'expires_in': jwt_decoded[1].get('exp') - jwt_decoded[1].get('iat') - 1,
        'jti': jwt_decoded[1].get('jti')
    }

    return result


def oauth_gitee_user(code):

    headers = {
        'Accept': 'application/json',
    }

    client_id = 'a0cb7df5a3489e7384bc2d9eab3f9338cf3215f0944567cd6f5b849530b6fd7d'
    client_secret = 'fc808faff01beef6b01c547ca32f1fc216f310d55bbae47ea9d77945270f804b'
    redirect_uri = 'http://127.0.0.1:8080/oauth/gitee'
    url = 'https://gitee.com/oauth/token?grant_type=authorization_code&code={}&client_id={}&redirect_uri={}'.format(code, client_id, redirect_uri)
    body = {
        'client_secret': client_secret,
    }

    try:
        res = requests.post(url=url, json=body, headers=headers)
    except:
        return None

    if res.status_code != 200:
        return None

    access_token = json.loads(res.text, encoding='utf-8')['access_token']

    url = 'https://gitee.com/api/v5/user?access_token={}'.format(access_token)
    try:
        res = requests.get(url=url)
    except:
        return None

    if res.status_code != 200:
        return None

    user_info = json.loads(res.text, encoding='utf-8')

    return get_user_by_gitee(user_info['login'])


def get_user_by_gitee(gitee_login):
    gitee_user = gitee_user_db.find_gitee_user(gitee_login)

    if gitee_user is None:
        login = 'gitee_' + gitee_login

        old = user_db.find_one_by_kv('login', login)
        if old is not None:
            login = login + '_' + str(random.randint(1, 999))

        user = User()
        user.login = login
        user.email = login + '@random.com'
        user.phone = str(random.random())[2: 15]
        user.fullName = gitee_login
        user.password = passwd_service.encode('Z' + str(random.random())[2: 12] + 'a')
        user.activated = True
        user.imageUrl = random_avatar_service.get_random_avatar(200)
        user.langKey = 'en'
        user.createdBy = 'gitee'
        user.createdDate = mytime.now()
        user.lastModifiedBy = user.createdBy
        user.lastModifiedDate = mytime.now()
        user_db.create_user(user)

        gitee_user = GiteeUser()
        gitee_user.giteeLogin = gitee_login
        gitee_user.userLogin = login
        gitee_user_db.create_gitee_user(gitee_user)
    else:
        login = gitee_user.userLogin

    user = user_db.find_one_by_kv('login', login)
    if user is not None:
        return user

    return None


def oauth_github_user(code):
    headers = {
        'Accept': 'application/json',
    }
    client_id = '00aabc78b8fe07c50e9f'
    client_secret = 'bd13211f9e338fc958145f2987a69e4619c364a1'
    url = 'https://github.com/login/oauth/access_token?code={}&client_id={}&client_secret={}'.format(code, client_id, client_secret)

    try:
        res = requests.post(url=url, headers=headers)
    except:
        return None

    if res.status_code != 200:
        return None

    access_token = json.loads(res.text, encoding='utf-8')['access_token']

    headers = {
        'Accept': 'application/json',
        'Authorization': 'token {}'.format(access_token)
    }
    url = 'https://api.github.com/user'
    try:
        res = requests.get(url=url, headers=headers)
    except:
        return None

    if res.status_code != 200:
        return None

    user_info = json.loads(res.text, encoding='utf-8')

    return get_user_by_github(user_info['login'])


def get_user_by_github(github_login):
    github_user = github_user_db.find_github_user(github_login)

    if github_user is None:
        login = 'github_' + github_login

        old = user_db.find_one_by_kv('login', login)
        if old is not None:
            login = login + '_' + str(random.randint(1, 999))

        user = User()
        user.login = login
        user.email = login + '@random.com'
        user.phone = str(random.random())[2: 15]
        user.fullName = github_login
        user.password = passwd_service.encode('A' + str(random.random())[2: 12] + 'z')
        user.activated = True
        user.imageUrl = random_avatar_service.get_random_avatar(200)
        user.langKey = 'en'
        user.createdBy = 'github'
        user.createdDate = mytime.now()
        user.lastModifiedBy = user.createdBy
        user.lastModifiedDate = mytime.now()
        user_db.create_user(user)

        github_user = GithubUser()
        github_user.githubLogin = github_login
        github_user.userLogin = login
        github_user_db.create_github_user(github_user)
    else:
        login = github_user.userLogin

    user = user_db.find_one_by_kv('login', login)
    if user is not None:
        return user

    return None


def oauth_qq_user(code):
    headers = {
        'Accept': 'application/json',
    }
    client_id = '101939187'
    client_secret = 'a6d9f8c2c7d4cb7bfb32822d6c1312b4'
    redirect_uri = 'http://127.0.0.1:8080/oauth/qq'
    url = 'https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id={}&client_secret={}&code={}&redirect_uri={}'.format(client_id,client_secret, code, redirect_uri)

    try:
        res = requests.post(url=url, headers=headers)
    except:
        return None

    if res.status_code != 200:
        return None

    access_token = json.loads(res.text, encoding='utf-8')['access_token']

    url = 'https://graph.qq.com/oauth2.0/me?access_token={}'.format(access_token)
    try:
        res = requests.post(url=url, headers=headers)
    except:
        return None

    if res.status_code != 200:
        return {
            'status': 'err',
            'value': res.text
        }

    open_id = json.loads(res.text, encoding='utf-8')['openid']

    url = 'https://graph.qq.com/user/get_user_info?access_token={}&oauth_consumer_key={}&openid={}'.format(access_token, client_id, open_id)
    try:
        res = requests.get(url=url)
    except:
        return None

    if res.status_code != 200:
        return None

    user_info = json.loads(res.text, encoding='utf-8')

    return get_user_by_qq(user_info['login'])


def get_user_by_qq(qq_login):
    qq_user = qq_user_db.find_qq_user(qq_login)

    if qq_user is None:
        login = 'qq_' + qq_login

        old = user_db.find_one_by_kv('login', login)
        if old is not None:
            login = login + '_' + str(random.randint(1, 999))

        user = User()
        user.login = login
        user.email = login + '@random.com'
        user.phone = str(random.random())[2: 15]
        user.fullName = qq_login
        user.password = passwd_service.encode('A' + str(random.random())[2: 12] + 'z')
        user.activated = True
        user.imageUrl = random_avatar_service.get_random_avatar(200)
        user.langKey = 'en'
        user.createdBy = 'qq'
        user.createdDate = mytime.now()
        user.lastModifiedBy = user.createdBy
        user.lastModifiedDate = mytime.now()
        user_db.create_user(user)

        qq_user = QqUser()
        qq_user.qqLogin = qq_login
        qq_user.userLogin = login
        qq_user_db.create_qq_user(qq_user)
    else:
        login = qq_user.userLogin

    user = user_db.find_one_by_kv('login', login)
    if user is not None:
        return user

    return None
