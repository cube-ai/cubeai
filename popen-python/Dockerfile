FROM unicom.gq:8801/python35-slim:0.0.2
ENV LANG=C.UTF-8 \
    APP_PROFILE=prod \
    START_SLEEP=0
WORKDIR /app
ADD . /app
RUN pip3 install -r requirements.txt -i http://mirrors.aliyun.com/pypi/simple/ --trusted-host mirrors.aliyun.com
CMD echo "The application will start in ${START_SLEEP}s..." && \
    sleep ${START_SLEEP} && \
    python3 ./start.py
