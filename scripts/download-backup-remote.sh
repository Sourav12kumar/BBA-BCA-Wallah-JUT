#!/bin/sh
set -eu

BACKUP_DIR="${BACKUP_DIR:-/backups}"
S3_ENDPOINT="${S3_ENDPOINT:-}"
S3_BUCKET="${S3_BUCKET:-}"
S3_PREFIX="${S3_PREFIX:-bba-bca-wallah}"
S3_ACCESS_KEY="${S3_ACCESS_KEY:-}"
S3_SECRET_KEY="${S3_SECRET_KEY:-}"
BACKUP_FILE="${1:-}"

if [ -z "$BACKUP_FILE" ]; then
  echo "Usage: download-backup-remote.sh <backup-file>" >&2
  exit 1
fi

for var in S3_ENDPOINT S3_BUCKET S3_ACCESS_KEY S3_SECRET_KEY; do
  eval "value=\${$var:-}"
  if [ -z "$value" ]; then
    echo "$var is required" >&2
    exit 1
  fi
done

mkdir -p "$BACKUP_DIR"
mc alias set remote "$S3_ENDPOINT" "$S3_ACCESS_KEY" "$S3_SECRET_KEY" --api S3v4 >/dev/null
mc cp "remote/$S3_BUCKET/$S3_PREFIX/$BACKUP_FILE" "$BACKUP_DIR/$BACKUP_FILE"

echo "Downloaded remote backup to $BACKUP_DIR/$BACKUP_FILE"
