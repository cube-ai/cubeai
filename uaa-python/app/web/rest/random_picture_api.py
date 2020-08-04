import tornado.web
from app.service import random_picture_service


class RandomPictureApi(tornado.web.RequestHandler):

    def get(self, width, height, *args, **kwargs):
        result = {
            'pictureDataUrl': random_picture_service.gen_random_picture(int(width), int(height))
        }
        self.write(result)
