#!/bin/bash

IMAGE="multicloud-framework"
VERSION="latest"

docker build -t ${IMAGE}:${VERSION} .