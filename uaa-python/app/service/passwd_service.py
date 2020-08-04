import hashlib
import random
import uuid


salt1 = '12345678'


def encode(password):

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
