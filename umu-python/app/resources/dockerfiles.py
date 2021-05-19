python35 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.5:1.0.5
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
# RUN pip3 install -r requirements.txt -i http://cubeai.org:7979/pypi/simple/ --trusted-host cubeai.org --timeout 600 && rm -rf ~/.cache/pip/*
RUN pip3 install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
CMD iboot start
'''


python36 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.6:1.0.5
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
# RUN pip3 install -r requirements.txt -i http://cubeai.org:7979/pypi/simple/ --trusted-host cubeai.org --timeout 600 && rm -rf ~/.cache/pip/*
RUN pip3 install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
CMD iboot start
'''


python37 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.7:1.0.5
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
# RUN pip3 install -r requirements.txt -i http://cubeai.org:7979/pypi/simple/ --trusted-host cubeai.org --timeout 600 && rm -rf ~/.cache/pip/*
RUN pip3 install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
CMD iboot start
'''


python38 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.8:1.0.0
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
# RUN pip3 install -r requirements.txt -i http://cubeai.org:7979/pypi/simple/ --trusted-host cubeai.org --timeout 600 && rm -rf ~/.cache/pip/*
RUN pip3 install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
CMD iboot start
'''


python39 = '''
FROM {DOCKER-SERVER}/ubuntu/python3.9:1.0.0
ENV LANG=C.UTF-8 APP_PROFILE=prod
WORKDIR /app
ADD . /app
# RUN pip3 install -r requirements.txt -i http://cubeai.org:7979/pypi/simple/ --trusted-host cubeai.org --timeout 600 && rm -rf ~/.cache/pip/*
RUN pip3 install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple
CMD iboot start
'''
