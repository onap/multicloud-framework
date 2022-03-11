FROM nexus3.onap.org:10001/onap/integration-java11:9.0.0

ARG HTTP_PROXY=${HTTP_PROXY}
ARG HTTPS_PROXY=${HTTPS_PROXY}
ARG BUILD_VERSION=${BUILD_VERSION}
ARG ARTIFACT_LOGS=/var/log/onap

ENV http_proxy $HTTP_PROXY
ENV https_proxy $HTTPS_PROXY
ENV BUILD_VERSION ${BUILD_VERSION}
ENV ARTIFACT_LOGS ${ARTIFACT_LOGS}

ENV ARTIFACT_HOME=/opt/app/
ENV ARTIFACT_DISTRIBUTION_HOME=${ARTIFACT_HOME}/distribution

USER root

EXPOSE 9014

RUN apk add --no-cache mlocate zip unzip jq procps \
                       curl openssh

RUN mkdir -p ${ARTIFACT_DISTRIBUTION_HOME} ${ARTIFACT_LOGS} ${ARTIFACT_HOME}/etc/ssl && \
    chown  -R onap:onap ${ARTIFACT_HOME} ${ARTIFACT_DISTRIBUTION_HOME} ${ARTIFACT_LOGS}

WORKDIR ${ARTIFACT_DISTRIBUTION_HOME}
COPY ./multicloud-framework-artifactbroker-*.zip multicloud-framework-artifactbroker.zip
RUN unzip -q -o -B multicloud-framework-artifactbroker.zip && \
    rm -f multicloud-framework-artifactbroker.zip
COPY artifact-dist.sh  bin/.

RUN  chmod +x bin/*.sh && \
     cp ${ARTIFACT_DISTRIBUTION_HOME}/etc/ssl/* ${ARTIFACT_HOME}/etc/ssl && \
     chown onap:onap ${ARTIFACT_HOME}/etc/ssl/*

RUN updatedb

USER onap

WORKDIR ${ARTIFACT_DISTRIBUTION_HOME}/bin
ENTRYPOINT [ "sh", "./artifact-dist.sh" ]
