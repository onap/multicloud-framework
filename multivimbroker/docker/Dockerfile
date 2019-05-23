FROM python:2-alpine

ENV MSB_ADDR "127.0.0.1"
ENV MSB_PORT "80"
ENV AAI_ADDR "aai.api.simpledemo.openecomp.org"
ENV AAI_PORT "8443"
ENV AAI_SCHEMA_VERSION "v11"
ENV AAI_USERNAME "AAI"
ENV AAI_PASSWORD "AAI"

EXPOSE 9001

RUN addgroup -S onap && adduser -S -G onap onap

# COPY ./ /opt/multivimbroker/
RUN apk update && \
    apk add uwsgi wget unzip gcc make libc-dev libffi-dev openssl-dev && \
    cd /opt/ && \
    wget -O multicloud-framework.zip "https://nexus.onap.org/service/local/artifact/maven/redirect?r=snapshots&g=org.onap.multicloud.framework&a=multicloud-framework-broker&e=zip&v=1.3.3-SNAPSHOT" && \
    unzip -q -o -B multicloud-framework.zip && \
    rm -f multicloud-framework.zip && \
    pip install -r /opt/multivimbroker/requirements.txt && \
    pip install -r /opt/multivimbroker/uwsgi-requirements.txt && \
    apk del wget unzip gcc make libc-dev libffi-dev openssl-dev && \
    mkdir -p /var/log/onap/multicloud/multivimbroker && \
    chown onap:onap /var/log/onap -R && \
    chown onap:onap /opt/multivimbroker -R

USER onap

WORKDIR /opt/multivimbroker
CMD /bin/sh -c /opt/multivimbroker/run.sh
