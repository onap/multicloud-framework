FROM python:3.7-alpine

ENV MSB_PROTO "https"
ENV MSB_ADDR "127.0.0.1"
ENV MSB_PORT "80"
ENV AAI_ADDR "aai.api.simpledemo.openecomp.org"
ENV AAI_PORT "8443"
ENV AAI_SCHEMA_VERSION "v11"
ENV AAI_USERNAME "AAI"
ENV AAI_PASSWORD "AAI"

EXPOSE 9001

RUN addgroup -S onap && adduser -S -G onap onap

COPY ./multicloud-framework-broker-*.zip /opt/multicloud-framework.zip
RUN apk update && \
    apk add uwsgi wget unzip gcc make libc-dev libffi-dev openssl-dev && \
    cd /opt/ && \
    unzip -q -o -B multicloud-framework.zip && \
    rm -f multicloud-framework.zip && \
    pip3 install -r /opt/multivimbroker/requirements.txt && \
    pip3 install -r /opt/multivimbroker/uwsgi-requirements.txt && \
    apk del wget unzip gcc make libc-dev libffi-dev openssl-dev && \
    mkdir -p /var/log/onap/multicloud/multivimbroker && \
    chown onap:onap /var/log/onap -R && \
    chown onap:onap /opt/multivimbroker -R

USER onap

WORKDIR /opt/multivimbroker
CMD /bin/sh -c /opt/multivimbroker/run.sh
