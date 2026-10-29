# multicloud-framework-artifactbroker

Spring Boot service that runs as a sidecar of the multicloud k8s plugin. It consumes SDC
distributions with the SDC distribution client and, for every VF module listed in a VF's
`VF_MODULES_METADATA`, registers the module's Helm chart (`HELM`, else a matching
`CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT`) with the k8s plugin as resource-bundle definition
`<vf-module invariant UUID>/<vf-module customization UUID>`.

## Build

    mvn clean verify            # tests
    mvn clean install -P docker # also builds and pushes ${CONTAINER_PUSH_REGISTRY}/onap/multicloud/framework-artifactbroker

## Configuration

`artifactbroker.*` properties (see `ArtifactBrokerProperties`), typically from a mounted
`/opt/app/artifactbroker/config/application.yaml`. The Kafka credentials come from the
`SASL_JAAS_CONFIG` environment variable, read by the SDC distribution client.

## Endpoints (port 9014)

- `/actuator/health/liveness`, `/actuator/health/readiness` — probes; readiness does not depend on SDC
- `/actuator/health` — includes `sdcConnection`
- `/actuator/prometheus` — `artifactbroker_distributions_total`, `artifactbroker_downloads_total`
