FROM unicom.gq:8801/python35-slim:0.0.2
ENV LANG=C.UTF-8 \
    APP_PROFILE=prod \
    START_SLEEP=0
WORKDIR /app
ADD . /app
RUN pip3 install -r requirements.txt -i http://unicom.gq:7979/pypi/simple/ --trusted-host unicom.gq
CMD echo "10.100.26.126 cubeai.dimpt.com" >> /etc/hosts && \
    echo "The application will start in ${START_SLEEP}s..." && \
    sleep ${START_SLEEP} && \
    python3 ./start.py
