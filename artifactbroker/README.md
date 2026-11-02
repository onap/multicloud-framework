# multicloud-framework-artifactbroker

Spring Boot service that runs as a sidecar of the multicloud k8s plugin. It consumes SDC
distributions with the SDC distribution client and, for every VF module listed in a VF's
`VF_MODULES_METADATA`, registers the module's Helm chart (`HELM`, else a matching
`CLOUD_TECHNOLOGY_SPECIFIC_ARTIFACT`) with the k8s plugin as resource-bundle definition
`<vf-module invariant UUID>/<vf-module customization UUID>`.

## Build

    mvn clean verify            # tests
    mvn clean install -P docker # also builds and pushes ${CONTAINER_PUSH_REGISTRY}/onap/multicloud/framework-artifactbroker

## Logging

The console log is one JSON object per line in Spring Boot's `logstash` structured format.
`LOG_FORMAT` selects another built-in format (`ecs`, `gelf`); set it to an empty value for
plain text. Every call to SDC and to the k8s plugin is logged when it starts, succeeds and
fails, with `distribution_id`, `artifact_name`/`artifact_uuid`, `rb_name`/`rb_version`,
`status` and `duration_ms` as separate fields.

## Configuration

`artifactbroker.*` properties (see `ArtifactBrokerProperties`), typically from a mounted
`/opt/app/artifactbroker/config/application.yaml`. The Kafka credentials come from the
`SASL_JAAS_CONFIG` environment variable, read by the SDC distribution client.

### Optional k8s Plugin Authentication

The dedicated plugin client accepts `artifactbroker.k8s-plugin.username` and
`artifactbroker.k8s-plugin.password`. Supply them through a protected mounted
Spring configuration file or the standard Spring environment variables
`ARTIFACTBROKER_K8SPLUGIN_USERNAME` and `ARTIFACTBROKER_K8SPLUGIN_PASSWORD`.
`artifactbroker.k8s-plugin.base-url` (environment variable
`ARTIFACTBROKER_K8SPLUGIN_BASEURL`) still defaults to `http://localhost:9015`.

Username and password values are literal: `${...}` placeholders and `#{...}`
expressions are not evaluated, including unresolved or malformed placeholders.

Omitting both credentials preserves anonymous calls with no `Authorization`
header. Supplying only one, blank values, or a username containing `:` fails
configuration without echoing credentials. The client sends preemptive HTTP Basic
on definition lookup, JSON creation, and binary chart upload, using UTF-8 and
splitting credentials at the first colon: passwords may contain colons, usernames
may not. Configure a pair accepted by the plugin's deployment users file; this
client does not read `MULTICLOUD_AUTH_USERS_FILE` or depend on the server's
`MULTICLOUD_AUTH_ENABLED` flag. Callers can therefore be configured before server
enforcement is enabled.

Credentials apply only to the dedicated plugin client, not the shared builder,
SDC, Kafka, or callbacks. The plugin client never follows redirects, with or
without credentials. Any 3xx response fails forwarding instead of registering
the chart as successfully uploaded. Other HTTP clients retain their configured
redirect policy. This config does not change artifactbroker's
own inbound endpoints. Loopback access to an auth-enabled plugin still needs
credentials.

Require HTTPS or mesh mTLS for credential transport, including sidecar traffic;
Basic credentials are not encrypted by their encoding. The default HTTP URL does
not itself provide TLS. Keep credentials out of command-line arguments and logs.
Rotation requires a restart: first make the plugin accept old and new users and
restart it, update and restart artifactbroker with the new pair, then remove the
old server user and restart the plugin again.

### Tracing

Tracing (OpenTelemetry, OTLP over gRPC) is off by default. The variables match OOM's
`global.tracing` (`enabled`, `sampling.probability`, `collector.baseUrl`, `collector.port.otlpGrpc`):

- `TRACING_ENABLED` — `true` exports spans, propagated as W3C `traceparent` (default `false`)
- `TRACING_SAMPLING_PROBABILITY` — share of distributions traced (default `1.0`)
- `COLLECTOR_HOST`, `COLLECTOR_PORT` — OTLP gRPC collector the spans go to
  (default `http://jaeger-collector.istio-config`, `4317`)

Each SDC distribution is one trace (`artifactbroker.distribution`), with a span per artifact download
(`artifactbroker.download`) and per k8s plugin request. Requests to `/actuator/**` (probes, Prometheus scrapes) are not
traced, and therefore have no `http_server_requests` metric either.

## Endpoints (port 9014)

- `/actuator/health/liveness`, `/actuator/health/readiness` — probes; readiness does not depend on SDC
- `/actuator/health` — includes `sdcConnection`
- `/actuator/prometheus` — `artifactbroker_distributions_total`, `artifactbroker_downloads_total` and the
  `artifactbroker_distribution_seconds`, `artifactbroker_download_seconds` timers
