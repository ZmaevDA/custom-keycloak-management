#!/bin/bash

set -e

cd /opt/keycloak/providers

/opt/keycloak/bin/kc.sh build

exec /opt/keycloak/bin/kc.sh start --auto-build --db postgres --hostname-strict-https false --hostname-strict false --proxy edge --http-enabled true --log-level=DEBUG --import-realm --spi-user-profile-legacy-user-profile-read-only-attributes *_RES_ACCESS_MODE
