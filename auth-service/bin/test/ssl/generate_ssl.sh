set -euo pipefail

TARGET_DIR="$(dirname "$0")"
cd "$TARGET_DIR"

POSTGRES_DOMAIN=localhost

echo "Cleaning up old certificates..."
rm -f *.crt *.key *.csr *.srl *.pk8

echo "Generating Root CA..."
openssl genrsa -out rootCA.key 2048
openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 365 \
    -out rootCA.crt -subj "/CN=TestRootCA"

echo "Generating Server Certificate..."
openssl genrsa -out server.key 2048
openssl req -new -key server.key -out server.csr -subj "/CN=$POSTGRES_DOMAIN"
openssl x509 -req -in server.csr -CA rootCA.crt -CAkey rootCA.key \
    -CAcreateserial -out server.crt -days 365 -sha256

echo "Generating Client Certificate..."
openssl genrsa -out client.key 2048
openssl req -new -key client.key -out client.csr -subj "/CN=postgres"
openssl x509 -req -in client.csr -CA rootCA.crt -CAkey rootCA.key \
    -CAcreateserial -out client.crt -days 365 -sha256


openssl pkcs8 -topk8 -inform PEM -outform PEM -in client.key -out client.key.pk8 -nocrypt -passout pass:
mv client.key.pk8 client.key

openssl pkcs8 -topk8 -inform PEM -outform PEM -in server.key -out server.key.pk8 -nocrypt
mv server.key.pk8 server.key

rm rootCA.srl client.csr server.csr

ls -l *.crt *.key