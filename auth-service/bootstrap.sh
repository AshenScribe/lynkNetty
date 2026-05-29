#!/usr/bin/env bash

set -euo pipefail

openssl ecparam -name prime256v1 -genkey -noout -out private.pem
openssl ec -in private.pem -pubout -out public.pem

chmod 600 private.pem
chmod 644 public.pem