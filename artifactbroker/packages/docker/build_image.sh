#!/bin/bash
# Copyright (c) 2017-2018 VMware, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

DIRNAME=`dirname $0`
DOCKER_BUILD_DIR=`cd $DIRNAME/; pwd`
echo "DOCKER_BUILD_DIR=${DOCKER_BUILD_DIR}"
cd ${DOCKER_BUILD_DIR}

BUILD_ARGS="--no-cache"
ORG="onap"
VERSION="1.9.2-SNAPSHOT"
STAGING_VERSION="1.9.2-STAGING"
PROJECT="multicloud"
IMAGE="framework-artifactbroker"
DOCKER_REPOSITORY="nexus3.onap.org:10003"
IMAGE_NAME="${DOCKER_REPOSITORY}/${ORG}/${PROJECT}/${IMAGE}"

if [ $HTTP_PROXY ]; then
    BUILD_ARGS+=" --build-arg HTTP_PROXY=${HTTP_PROXY}"
fi
if [ $HTTPS_PROXY ]; then
    BUILD_ARGS+=" --build-arg HTTPS_PROXY=${HTTPS_PROXY}"
fi

function build_image {
    docker build ${BUILD_ARGS} -t ${IMAGE_NAME}:${VERSION} -t ${IMAGE_NAME}:latest -t ${IMAGE_NAME}:${STAGING_VERSION} .
}

function push_image {
    docker push ${IMAGE_NAME}:${VERSION}
    docker push ${IMAGE_NAME}:${STAGING_VERSION}
    docker push ${IMAGE_NAME}:latest
}

build_image
push_image
