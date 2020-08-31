FROM unicom.gq:8801/python35-slim:0.0.2
ENV LANG=C.UTF-8 APP_PROFILE=prod START_SLEEP=0
WORKDIR /serviceboot
ADD . /serviceboot
# RUN pip3 install -i https://pypi.douban.com/simple/ -r requirements.txt
RUN pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt
CMD echo "10.100.26.126 cubeai.dimpt.com" >> /etc/hosts && \
    echo "The application will start in ${START_SLEEP}s..." && \
    sleep ${START_SLEEP} && \
    serviceboot
