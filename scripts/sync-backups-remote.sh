#!/bin/sh
set -eu

BACKUP_DIR="${BACKUP_DIR:-/backups}"
S3_ENDPOINT="${S3_ENDPOINT:-}"
S3_BUCKET="${S3_BUCKET:-}"
S3_PREFIX="${S3_PREFIX:-bba-bca-wallah}"
S3_ACCESS_KEY="${S3_ACCESS_KEY:-}"
S3_SECRET_KEY="${S3_SECRET_KEY:-}"
REMOTE_RETENTION_DAYS="${REMOTE_RETENTION_DAYS:-30}"

for var in S3_ENDPOINT S3_BUCKET S3_ACCESS_KEY S3_SECRET_KEY; do
  eval "value=\${$var:-}"
  if [ -z "$value" ]; then
    echo "$var is required for remote backup sync" >&2
    exit 1
  fi
done

mc alias set remote "$S3_ENDPOINT" "$S3_ACCESS_KEY" "$S3_SECRET_KEY" --api S3v4 >/dev/null

echo "Syncing local backups from $BACKUP_DIR to remote/$S3_BUCKET/$S3_PREFIX"
mc mirror --overwrite "$BACKUP_DIR" "remote/$S3_BUCKET/$S3_PREFIX"

case "$REMOTE_RETENTION_DAYS" in
  ''|*[!0-9]*)
    echo "REMOTE_RETENTION_DAYS must be a non-negative integer" >&2
    exit 1
    ;;
esac

if [ "$REMOTE_RETENTION_DAYS" -gt 0 ]; then
  echo "Removing remote backup objects older than ${REMOTE_RETENTION_DAYS} day(s)"
  mc rm --recursive --force --older-than "${REMOTE_RETENTION_DAYS}d" "remote/$S3_BUCKET/$S3_PREFIX" || true
fi

echo "Remote backup sync completed"
