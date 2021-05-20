from app.domain.artifact import Artifact
from app.service import token_service
from app.database import artifact_db, solution_db
from app.utils import mytime


def create_artifact(**args):
    token = token_service.get_token(args.get('http_request'))
    if not token.is_valid:
        raise Exception('403 Forbidden')

    artifact = Artifact()
    artifact.__dict__ = args.get('artifact')

    user_login = token.username
    if user_login != 'internal':
        solutions = solution_db.get_solutions_by_uuid(artifact.solutionUuid)
        if len(solutions) > 0:
            # solution已经存在， 只允许solution作者自己添加artifact
            solution = solutions[0]
            if user_login != solution.get('authorLogin'):
                raise Exception('403 Forbidden')
        else:
            # solution不存在， 只允许internal添加artifact
            raise Exception('403 Forbidden')

    artifact.createdDate = mytime.now()
    artifact.modifiedDate = mytime.now()
    id = artifact_db.create_artifact(artifact)
    return id


def get_artifacts(**args):
    solution_uuid = args.get('solutionUuid')
    artifact_type = args.get('type')

    where = 'WHERE solution_uuid = "{}"'.format(solution_uuid)
    if artifact_type is not None:
        where += ' and jhi_type = "{}"'.format(artifact_type)

    results = artifact_db.get_artifacts(where)
    return results


def delete_artifact(**args):
    token = token_service.get_token(args.get('http_request'))
    user_login = token.username
    if user_login != 'internal':
        # 只允许onboarding失败时由internal调用。
        raise Exception('403 Forbidden')

    artifact_db.delete_artifact(id)
    return 0

