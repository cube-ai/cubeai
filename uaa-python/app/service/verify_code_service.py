import io
import base64
import random
from _datetime import datetime
from PIL import Image, ImageDraw
from app.database import verify_code_db
from app.global_data.global_data import g
from app.domain.verify_code import VerifyCode
from datetime import datetime, timedelta


def get_verify_code(**args):
    verify_code = VerifyCode()
    verify_code.code = gen_random_code()
    verify_code.expire = datetime.now() + timedelta(seconds=60)
    id = verify_code_db.create_verify_code(verify_code)

    result = {
        'verifyId': id,
        'verifyCode': gen_code_picture(verify_code.code)
    }

    return result


def validate_verify_code(**args):
    id = args.get('verifyId')
    code = args.get('verifyCode')

    verify_code = verify_code_db.get_verify_code(id)

    if verify_code is None:
        return 0

    verify_code_db.delete_verify_code(id)

    db_code = verify_code.get('code')
    expire_timestamp = datetime.strptime(verify_code.get('expire'), '%Y-%m-%dT%H:%M:%S').timestamp()

    if code.lower() == db_code.lower() and datetime.now().timestamp() < expire_timestamp:
        return 1

    return 0


def gen_random_code():
    char_set = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return '{}{}{}{}'.format(
        char_set[random.randint(0, 61)],
        char_set[random.randint(0, 61)],
        char_set[random.randint(0, 61)],
        char_set[random.randint(0, 61)]
    )


def gen_code_picture(code):
    width = 100
    height = 40
    code_length = 4  # 字符的数量
    line_num = 40  # 干扰线数量
    font_size = 25

    image = Image.new('RGB', (width, height), (239, 239, 239))

    draw = ImageDraw.Draw(image)
    font = g.verify_code_font

    # 绘制干扰线
    for i in range(line_num):
        x = random.randint(0, width - 1)
        y = random.randint(0, height - 1)
        xl = random.randint(0, 12)
        yl = random.randint(0, 15)
        color = (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))
        draw.line((x, y, x + xl, y + yl), fill=color, width=1)

    for i in range(code_length):
        color = (random.randint(0, 230), random.randint(0, 230), random.randint(0, 230))
        space = random.randint(-5, 5)
        draw.text((5 + space + i * font_size, 7), code[i], color, font=font)

    output_buffer = io.BytesIO()
    image.save(output_buffer, format='JPEG')
    return 'data:image/jpeg;base64,' + str(base64.b64encode(output_buffer.getvalue()), encoding='utf-8')


