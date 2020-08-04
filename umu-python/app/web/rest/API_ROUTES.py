from .health_check import HealthChecker
from .hello import Hello
from .onboarding_api import OnboardingApi
from .document_api import DocumentApi
from .artifact_api import ArtifactApi
from .download_api import DownloadApi
from .ueditor_api import UeditorApi


API_ROUTES = [
    (r'/management/health', HealthChecker),
    (r'/api/hello', Hello),
    (r'/api/onboarding/(\w+)', OnboardingApi),
    (r'/api/documents/(\w+)', DocumentApi),
    (r'/api/artifact/(\w+)/(\w+)', ArtifactApi),
    (r'/api/get-file-text', DownloadApi),
    (r'/api/ueditor', UeditorApi),
]