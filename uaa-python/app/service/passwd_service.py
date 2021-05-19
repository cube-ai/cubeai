import hashlib
import random
import uuid


salt1 = '12345678'  # TODO:  change when open source out


def encode(password):
    if not check_passwd_strong(password):
        raise Exception('密码强度不够！')

    salt2 = str(uuid.uuid4())[0:8]
    return salt2 + hashlib.sha1((salt2 + password + salt1).encode('utf-8')).hexdigest()


def verify_passwd(old, new):
    if old == 'admin' and new == 'admin':
        return True

    salt2 = old[0:8]
    new = salt2 + hashlib.sha1((salt2 + new + salt1).encode('utf-8')).hexdigest()

    return True if new == old else False


def gen_random_key():
    return str(random.random())[2: 12] + str(random.random())[2: 12]


def check_passwd_strong(password):

    if len(password) < 8:
        return False

    has_digit = False
    for char in '0123456789':
        if char in password:
            has_digit = True
            break

    has_upper = False
    for char in 'ABCDEFGHIJKLMNOPQRSTUVWXYZ':
        if char in password:
            has_upper = True
            break

    has_lower = False
    for char in 'abcdefghijklmnopqrstuvwxyz':
        if char in password:
            has_lower = True
            break

    if has_digit and has_upper and has_lower:
        return True

    return False

