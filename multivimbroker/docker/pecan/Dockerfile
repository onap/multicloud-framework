FROM python:2-slim

ENV MSB_ADDR "127.0.0.1"
ENV MSB_PORT "80"
ENV AAI_ADDR "aai.api.simpledemo.openecomp.org"
ENV AAI_PORT "8443"
ENV AAI_SCHEMA_VERSION "v11"
ENV AAI_USERNAME "AAI"
ENV AAI_PASSWORD "AAI"

EXPOSE 9001

RUN groupadd -r onap && useradd -r -g onap onap

# COPY ./ /opt/multivimbroker/
RUN apt-get update && \
    apt-get install -y wget unzip gcc && \
    cd /opt/ && \
    wget -O multicloud-framework.zip "https://nexus.onap.org/service/local/artifact/maven/redirect?r=snapshots&g=org.onap.multicloud.framework.broker&a=multicloud-framework-broker&e=zip&v=1.3.2-SNAPSHOT" && \
    unzip -q -o -B multicloud-framework.zip && \
    rm -f multicloud-framework.zip && \
    pip install -r /opt/multivimbroker/requirements.txt && \
    pip install -r /opt/multivimbroker/pecan-requirements.txt && \
    apt-get --purge remove -y wget unzip gcc && \
    apt-get -y autoremove && \
    chown onap:onap /opt/multivimbroker -R

USER onap

WORKDIR /opt/multivimbroker
CMD /bin/sh -c /opt/multivimbroker/run.sh
