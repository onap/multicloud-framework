FROM alpine:3.9

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

EXPOSE 9014

RUN apk add --no-cache --update busybox-extras bash nss procps coreutils findutils grep zip unzip \
                                curl wget openssh openjdk8 maven jq httpie py-pip

RUN addgroup -S onap && \
    adduser -S --shell /bin/bash -G onap onap

RUN mkdir -p ${ARTIFACT_DISTRIBUTION_HOME} ${ARTIFACT_LOGS} ${ARTIFACT_HOME}/etc/ssl && \
    chown  -R onap:onap ${ARTIFACT_HOME} ${ARTIFACT_DISTRIBUTION_HOME} ${ARTIFACT_LOGS}

WORKDIR ${ARTIFACT_DISTRIBUTION_HOME}
COPY ./multicloud-framework-artifactbroker-*.zip /opt/multicloud-framework-artifactbroker.zip
    unzip -q -o -B multicloud-framework-artifactbroker-package-1.4.0-SNAPSHOT.zip && \
    rm -f multicloud-framework-artifactbroker-package-1.4.0-SNAPSHOT.zip
COPY artifact-dist.sh  bin/.
#RUN tar xvfz /packages/policy-distribution.tar.gz --directory ${ARTIFACT_DISTRIBUTION_HOME}
#RUN rm /packages/policy-distribution.tar.gz

RUN  chmod +x bin/*.sh && \
     cp ${ARTIFACT_DISTRIBUTION_HOME}/etc/ssl/* ${ARTIFACT_HOME}/etc/ssl && \
     chown onap:onap ${ARTIFACT_HOME}/etc/ssl/*

USER onap
WORKDIR ${ARTIFACT_DISTRIBUTION_HOME}/bin
ENTRYPOINT [ "bash", "./artifact-dist.sh" ]
