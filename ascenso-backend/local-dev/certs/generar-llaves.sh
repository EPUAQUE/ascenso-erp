#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

if [[ -f dev-private.pem && -f dev-public.pem ]]; then
  echo "Las llaves de desarrollo ya existen — no se genera nada."
  exit 0
fi

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out dev-private.pem
openssl rsa -pubout -in dev-private.pem -out dev-public.pem
echo "Llaves de desarrollo generadas: dev-private.pem / dev-public.pem"
