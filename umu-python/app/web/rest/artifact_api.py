import tornado.web
from app.service import umm_client_async
from app.service import nexus_client_async


class ArtifactApi(tornado.web.RequestHandler):

    async def get(self, artifact_type, solution_uuid, *args, **kwargs):
        if artifact_type == 'metadata':
            ctype = '元数据'
        elif artifact_type == 'protobuf':
            ctype = 'PROTOBUF文件'
        else:
            self.send_error(400)
            return

        artifact_list = await umm_client_async.get_all_artifacts(solution_uuid)

        if artifact_list is None or len(artifact_list) < 1:
            self.set_status(404)
            self.write('no artifact!')
            return

        url = None
        for artifact in artifact_list:
            if artifact.get('type') == ctype:
                url = artifact.get('url')
                break

        if url is None:
            self.set_status(404)
            self.write('no typed artifact!')
            return

        metadata_text = await nexus_client_async.get_artifact(url)
        result = {artifact_type: metadata_text}

        self.write(result)
