#!/usr/bin/env bash

set -euo pipefail

TARGET_DIR="$(dirname "$0")"
cd "$TARGET_DIR"

# Generate EC private key
openssl ecparam -name prime256v1 -genkey -noout -out private_key_pkcs1.pem

# Convert to PKCS#8 format (Java/BouncyCastle compatible)
openssl pkcs8 -topk8 -nocrypt -in private_key_pkcs1.pem -out private.pem

# Extract public key in X.509 format
openssl ec -in private.pem -pubout -out public.pem

# Clean up
rm -f private_key_pkcs1.pem

chmod 600 private.pem
chmod 644 public.pem