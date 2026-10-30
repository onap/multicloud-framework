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

Tracing (OpenTelemetry, OTLP over HTTP) is off by default:

- `TRACING_ENABLED` — `true` exports spans, propagated as W3C `traceparent` (default `false`)
- `TRACING_SAMPLING_PROBABILITY` — share of distributions traced (default `1.0`)
- `COLLECTOR_HOST`, `COLLECTOR_PORT` — collector the spans go to as
  `${COLLECTOR_HOST}:${COLLECTOR_PORT}/v1/traces` (default `http://jaeger-collector.istio-system`, `4318`)

Each SDC distribution is one trace (`artifactbroker.distribution`), with a span per artifact download
(`artifactbroker.download`) and per k8s plugin request.

## Endpoints (port 9014)

- `/actuator/health/liveness`, `/actuator/health/readiness` — probes; readiness does not depend on SDC
- `/actuator/health` — includes `sdcConnection`
- `/actuator/prometheus` — `artifactbroker_distributions_total`, `artifactbroker_downloads_total` and the
  `artifactbroker_distribution_seconds`, `artifactbroker_download_seconds` timers
