import io
import base64
import random
from PIL import Image


def gen_random_picture(width, height):
    color = (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))
    image = Image.new('RGB', (width, height), color)

    output_buffer = io.BytesIO()
    image.save(output_buffer, format='JPEG')
    return 'data:image/jpeg;base64,' + str(base64.b64encode(output_buffer.getvalue()), encoding='utf-8')
