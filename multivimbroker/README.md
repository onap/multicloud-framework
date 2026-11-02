# Copyright (c) 2017 Wind River Systems, Inc.
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

# Micro service of broker for multiple VIM instance drivers.

Changelog: 

2019-12-11: Depreciate api_v2 module since it is not maintained any more

## Optional API Authentication

The Django WSGI broker can enforce deployment-managed HTTP Basic credentials.
It does not use Django database users. Defaults preserve anonymous access.

| Environment variable | Contract |
| --- | --- |
| `MULTICLOUD_AUTH_ENABLED` | Defaults to `false`. Accepts `true` or `false`, case-insensitive, ignoring surrounding whitespace. Any other value fails startup. |
| `MULTICLOUD_AUTH_USERS_FILE` | Required when enabled: path to a readable UTF-8 JSON users array. Ignored when disabled. |

Mount the credential file as a secret, restrict its filesystem permissions, and
never interpolate credentials into Python source. Its schema is:

```json
[
	{"username": "operator", "password": "replace-at-deployment"},
	{"username": "rotation-user", "password": "another-deployment-secret"}
]
```

Enabled startup rejects missing/unreadable/malformed files, non-array or empty
lists, missing/non-string/blank usernames or passwords, duplicate usernames, and
usernames containing `:`. Credentials are compared exactly as supplied, using
constant-time digest comparisons. Basic credentials are UTF-8, split at the first
colon: passwords may contain colons, usernames may not. Configuration errors and
authentication challenges do not echo credentials.

All broker APIs, including non-DRF views, require authentication when enabled.
The sole anonymous exemption is `GET /api/multicloud/v0/swagger.json`, for the
existing probe; other methods and trailing/child paths are protected. Loopback
requests have no exemption. Missing, malformed, or invalid credentials return
`401` with `WWW-Authenticate: Basic realm="multicloud", charset="UTF-8"`.
Auth-enabled use of the deprecated Pecan launcher is rejected rather than
starting an unprotected application.

The incoming `Authorization` header is preserved through normal proxy requests,
including multipart bodies, and the multipart transport helper. The v1
`infra_workload` endpoint still rejects multipart as unsupported. Outbound A&AI
requests continue to use only `AAI_USERNAME` / `AAI_PASSWORD`; do not reuse those
credentials for the MultiCloud API. Plugins need their own enforcement and must
accept the forwarded deployment credentials.

HTTP Basic is encoding, not encryption. Require HTTPS or mesh mTLS on every hop,
including broker-to-plugin traffic, before enabling authentication. This feature
does not protect separately exposed provider/plugin services or configure TLS.

Credentials are loaded once at startup. For rotation, add the new user alongside
the old user and restart brokers, switch callers to the new pair and restart
them, then remove the old user and restart brokers again. Changing a mounted
file alone does not reload credentials or restart the application.
