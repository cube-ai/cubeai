from .health_check import HealthChecker
from .hello import Hello
from .solution_api import SolutionApiA, SolutionApiB
from .artifact_api import ArtifactApiA, ArtifactApiB
from .document_api import DocumentApiA, DocumentApiB
from .description_api import DescriptionApiA, DescriptionApiB
from .task_api import TaskApiA, TaskApiB
from .task_step_api import TaskStepApi
from .star_api import StarApiA, StarApiB, StarApiC
from .comment_api import CommentApiA, CommentApiB
from .credit_api import CreditApiA, CreditApiB, CreditApiC
from .credit_history_api import CreditHistoryApi
from .composite_solution_map_api import CompositeSolutionMapApiA, CompositeSolutionMapApiB
from .deployment_api import DeploymentApiA, DeploymentApiB
from .ability_api import AbilityApi


API_ROUTES = [
    (r'/api/hello', Hello),
    (r'/management/health', HealthChecker),
    (r'/api/solutions', SolutionApiA),
    (r'/api/solutions/(.*)', SolutionApiB),
    (r'/api/artifacts', ArtifactApiA),
    (r'/api/artifacts/(\w+)', ArtifactApiB),
    (r'/api/documents', DocumentApiA),
    (r'/api/documents/(\w+)', DocumentApiB),
    (r'/api/descriptions', DescriptionApiA),
    (r'/api/descriptions/(\w+)', DescriptionApiB),
    (r'/api/tasks', TaskApiA),
    (r'/api/tasks/(\w+)', TaskApiB),
    (r'/api/task-steps', TaskStepApi),
    (r'/api/stars', StarApiA),
    (r'/api/stars/(\w+)', StarApiB),
    (r'/api/stars/(\w+)/(\w+)',StarApiC),
    (r'/api/comments', CommentApiA),
    (r'/api/comments/(\w+)', CommentApiB),
    (r'/api/credits', CreditApiA),
    (r'/api/credits/(\w+)', CreditApiB),
    (r'/api/credits/(\w+)/(.*)', CreditApiC),
    (r'/api/credit-histories', CreditHistoryApi),
    (r'/api/composite-solution-maps', CompositeSolutionMapApiA),
    (r'/api/composite-solution-maps/(\w+)', CompositeSolutionMapApiB),
    (r'/api/deployments', DeploymentApiA),
    (r'/api/deployments/(.*)', DeploymentApiB),
    (r'/model/ability', AbilityApi),

]
