#!/bin/sh
set -eu

BACKUP_DIR="${BACKUP_DIR:-/backups}"
DB_NAME="${DB_NAME:-bba_bca_wallah}"
DB_HOST="${DB_HOST:-mysql}"
DB_USER="${DB_USER:-root}"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-14}"
TIMESTAMP="$(date '+%Y-%m-%d_%H-%M-%S')"
OUTPUT="${BACKUP_DIR}/${DB_NAME}_${TIMESTAMP}.sql.gz"

mkdir -p "$BACKUP_DIR"

if [ -z "${DB_PASSWORD:-}" ]; then
  echo "DB_PASSWORD is required" >&2
  exit 1
fi

echo "Creating database backup: $OUTPUT"
MYSQL_PWD="$DB_PASSWORD" mysqldump \
  --host="$DB_HOST" \
  --user="$DB_USER" \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --set-gtid-purged=OFF \
  "$DB_NAME" | gzip > "$OUTPUT"

if [ ! -s "$OUTPUT" ]; then
  echo "Backup file is empty; removing failed backup" >&2
  rm -f "$OUTPUT"
  exit 1
fi

echo "Backup completed: $OUTPUT"

if [ "$RETENTION_DAYS" -gt 0 ] 2>/dev/null; then
  find "$BACKUP_DIR" -type f -name "${DB_NAME}_*.sql.gz" -mtime "+$RETENTION_DAYS" -delete
  echo "Deleted backups older than ${RETENTION_DAYS} day(s)"
fi
