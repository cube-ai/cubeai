python35 = '''
FROM {DOCKER-SERVER}/ubuntu/python:3.0.3
WORKDIR /app
ENV LANG=C.UTF-8
ADD . /app
RUN pip3 install -r requirements.txt -i http://unicom.gq:7979/pypi/simple/ --trusted-host unicom.gq --timeout 600 && rm -rf ~/.cache/pip/*
CMD ["cubeai_model_runner", "model/"]

'''


python36 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.6:0.0.9
WORKDIR /app
ENV LANG C.UTF-8
ADD . /app
RUN pip3 install -r requirements.txt -i http://unicom.gq:7979/pypi/simple/ --trusted-host unicom.gq --timeout 600 && rm -rf ~/.cache/pip/*
CMD ["/usr/python3/bin/cubeai_model_runner", "model/"]
'''


python37 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.7:0.0.1
WORKDIR /app
ENV LANG C.UTF-8
ADD . /app
RUN pip3 install -r requirements.txt -i http://unicom.gq:7979/pypi/simple/ --trusted-host unicom.gq --timeout 600 && rm -rf ~/.cache/pip/*
CMD ["cubeai_model_runner", "model/"]
'''
