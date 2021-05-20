import python_jwt
from jwcrypto import jwk
from app.global_data.global_data import g


class Token:
    def __init__(self):
        self.is_valid = False
        self.jwt = None
        self.username = None
        self.rules = []

    def has_role(self, rule):
        return rule in self.rules


def get_token(request):
    token = Token()

    authorization = request.headers.get('Authorization')
    if authorization is None:
        return token

    if authorization[0:6].lower() != 'bearer':
        return token

    token.jwt = authorization[7:]  # 去除前缀“Bearer ”

    try:
        jwt_decoded = python_jwt.verify_jwt(
            token.jwt,
            jwk.JWK.from_pem(g.config.public_key),
            ['RS256'],
            checks_optional=True,
            ignore_not_implemented=True
        )
        claims = jwt_decoded[1]
        token.username = claims.get('user_name') or claims.get('client_id')
        token.rules = claims.get('authorities') or []
        token.is_valid = True
    except Exception as e:
        token.is_valid = False
        token.jwt = None
        token.username = None
        token.rules = []

    return token
