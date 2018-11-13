FROM python:2

ENV MSB_ADDR "127.0.0.1"
ENV MSB_PORT "80"
ENV AAI_ADDR "aai.api.simpledemo.openecomp.org"
ENV AAI_PORT "8443"
ENV AAI_SCHEMA_VERSION "v11"
ENV AAI_USERNAME "AAI"
ENV AAI_PASSWORD "AAI"

EXPOSE 9001

# COPY ./ /opt/multivimbroker/
RUN apt-get update && \
    apt-get install -y unzip && \
    cd /opt/ && \
    wget -O multicloud-framework.zip "https://nexus.onap.org/service/local/artifact/maven/redirect?r=snapshots&g=org.onap.multicloud.framework.broker&a=multicloud-framework-broker&e=zip&v=1.2.2-SNAPSHOT" && \
    unzip -q -o -B multicloud-framework.zip && \
    rm -f multicloud-framework.zip && \
    pip install -r /opt/multivimbroker/requirements.txt

WORKDIR /opt/multivimbroker
CMD /bin/sh -c /opt/multivimbroker/run.sh