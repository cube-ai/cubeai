# -*- coding: utf-8 -*-
from app.global_data.global_data import g
from app.service import solution_service, star_service, description_service, artifact_service, document_service
from app.service import comment_service, credit_service, deployment_service, task_service, task_step_service


class AppCore(object):
    
    # 声明对外公开可通过API接口访问的方法。如public_actions未声明或值为None，则默认本class中定义的所有方法都对外公开。
    public_actions = None

    def __init__(self):
        g.load_global_data()
        if not g.init_success:
            raise Exception('初始化加载 global_data 失败！')

    def hello(self, **args):
        return 'Hello world!'

    def create_solution(self, **args):
        return solution_service.create_solution(**args)

    def get_solutions(self, **args):
        return solution_service.get_solutions(**args)

    def update_solution_baseinfo(self, **args):
        return solution_service.update_solution_baseinfo(**args)

    def update_solution_admininfo(self, **args):
        return solution_service.update_solution_admininfo(**args)

    def update_solution_name(self, **args):
        return solution_service.update_solution_name(**args)

    def update_solution_active(self, **args):
        return solution_service.update_solution_active(**args)

    def update_solution_picture_url(self, **args):
        return solution_service.update_solution_picture_url(**args)

    def update_solution_star_count(self, **args):
        return solution_service.update_solution_star_count(**args)

    def update_solution_comment_count(self, **args):
        return solution_service.update_solution_comment_count(**args)

    def update_solution_view_count(self, **args):
        return solution_service.update_solution_view_count(**args)

    def update_solution_download_count(self, **args):
        return solution_service.update_solution_download_count(**args)

    def delete_solution(self, **args):
        return solution_service.delete_solution(**args)

    def create_star(self, **args):
        return star_service.create_star(**args)

    def get_stars(self, **args):
        return star_service.get_stars(**args)

    def delete_star(self, **args):
        return star_service.delete_star(**args)

    def delete_star_by_target_uuid(self, **args):
        return star_service.delete_star_by_target_uuid(**args)

    def get_user_stared_uuid_list(self, **args):
        return star_service.get_user_stared_uuid_list(**args)

    def find_description(self, **args):
        return description_service.find_description(**args)

    def update_description(self, **args):
        return description_service.update_description(**args)

    def create_artifact(self, **args):
        return artifact_service.create_artifact(**args)

    def get_artifacts(self, **args):
        return artifact_service.get_artifacts(**args)

    def delete_artifact(self, **args):
        return artifact_service.delete_artifact(**args)

    def create_document(self, **args):
        return document_service.create_document(**args)

    def get_documents(self, **args):
        return document_service.get_documents(**args)

    def find_document(self, **args):
        return document_service.find_document(**args)

    def delete_document(self, **args):
        return document_service.delete_document(**args)

    def create_comment(self, **args):
        return comment_service.create_comment(**args)

    def get_comments(self, **args):
        return comment_service.get_comments(**args)

    def delete_comment(self, **args):
        return comment_service.delete_comment(**args)

    def get_my_credit(self, **args):
        return credit_service.get_my_credit(**args)

    def get_credits(self, **args):
        return credit_service.get_credits(**args)

    def update_credit(self, **args):
        return credit_service.update_credit(**args)

    def get_credit_history(self, **args):
        return credit_service.get_credit_history(**args)

    def create_deployment(self, **args):
        return deployment_service.create_deployment(**args)

    def get_deployments(self, **args):
        return deployment_service.get_deployments(**args)

    def update_deployment_solution_info(self, **args):
        return deployment_service.update_deployment_solution_info(**args)

    def update_deployment_admin_info(self, **args):
        return deployment_service.update_deployment_admin_info(**args)

    def update_deployment_status(self, **args):
        return deployment_service.update_deployment_status(**args)

    def update_deployment_star_count(self, **args):
        return deployment_service.update_deployment_star_count(**args)

    def delete_deployment(self, **args):
        return deployment_service.delete_deployment(**args)

    def find_and_call_ability(self, **args):
        return deployment_service.find_and_call_ability(**args)

    def create_task(self, **args):
        return task_service.create_task(**args)

    def update_task(self, **args):
        return task_service.update_task(**args)

    def get_tasks(self, **args):
        return task_service.get_tasks(**args)

    def find_task(self, **args):
        return task_service.find_task(**args)

    def delete_task(self, **args):
        return task_service.delete_task(**args)

    def create_task_step(self, **args):
        return task_step_service.create_task_step(**args)

    def get_task_steps(self, **args):
        return task_step_service.get_task_steps(**args)

    def deletes_task_steps(self, **args):
        return task_step_service.deletes_task_steps(**args)




