import base64
import datetime
import python_jwt
from jwcrypto import jwk
from app.global_data.global_data import g
from app.database import user_db
from app.service import passwd_service


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
