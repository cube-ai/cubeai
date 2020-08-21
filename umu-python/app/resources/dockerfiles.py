python35 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.5:1.0.2
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
RUN pip3 install -r requirements.txt -i http://unicom.gq:7979/pypi/simple/ --trusted-host unicom.gq --timeout 600 && rm -rf ~/.cache/pip/*
CMD cubeai_model_runner
'''


python36 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.6:1.0.1
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
RUN pip3 install -r requirements.txt -i http://unicom.gq:7979/pypi/simple/ --trusted-host unicom.gq --timeout 600 && rm -rf ~/.cache/pip/*
CMD cubeai_model_runner
'''


python37 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.7:1.0.0
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
RUN pip3 install -r requirements.txt -i http://unicom.gq:7979/pypi/simple/ --trusted-host unicom.gq --timeout 600 && rm -rf ~/.cache/pip/*
CMD cubeai_model_runner
'''
