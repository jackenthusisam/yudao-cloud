#!/usr/bin/env bash
set -euo pipefail

NACOS_ADDR="${NACOS_ADDR:-http://127.0.0.1:8848}"
NACOS_USERNAME="${NACOS_USERNAME:-nacos}"
NACOS_PASSWORD="${NACOS_PASSWORD:-nacos}"
NACOS_NAMESPACE="${NACOS_NAMESPACE:-}"

if [ -z "${NACOS_NAMESPACE}" ]; then
  echo "Using Nacos public namespace."
  exit 0
fi

token="$(
  curl -fsS -X POST "${NACOS_ADDR}/nacos/v1/auth/users/login" \
    -d "username=${NACOS_USERNAME}" \
    -d "password=${NACOS_PASSWORD}" |
  sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p'
)"

if [ -z "${token}" ]; then
  echo "Failed to login to Nacos at ${NACOS_ADDR}" >&2
  exit 1
fi

curl -fsS -X POST "${NACOS_ADDR}/nacos/v1/console/namespaces?accessToken=${token}" \
  -d "customNamespaceId=${NACOS_NAMESPACE}" \
  -d "namespaceName=${NACOS_NAMESPACE}" \
  -d "namespaceDesc=yudao remote development namespace" >/dev/null || true

echo "Nacos namespace '${NACOS_NAMESPACE}' is ready."
