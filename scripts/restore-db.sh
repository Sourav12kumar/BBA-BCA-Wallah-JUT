#!/bin/sh
set -eu

BACKUP_DIR="${BACKUP_DIR:-/backups}"
DB_NAME="${DB_NAME:-bba_bca_wallah}"
DB_HOST="${DB_HOST:-mysql}"
DB_USER="${DB_USER:-root}"
BACKUP_FILE="${1:-}"

if [ -z "$BACKUP_FILE" ]; then
  echo "Usage: restore-db.sh <backup-file.sql.gz>" >&2
  exit 1
fi

case "$BACKUP_FILE" in
  /*) RESTORE_PATH="$BACKUP_FILE" ;;
  *) RESTORE_PATH="${BACKUP_DIR}/${BACKUP_FILE}" ;;
esac

if [ ! -f "$RESTORE_PATH" ]; then
  echo "Backup file not found: $RESTORE_PATH" >&2
  exit 1
fi

if [ -z "${DB_PASSWORD:-}" ]; then
  echo "DB_PASSWORD is required" >&2
  exit 1
fi

echo "WARNING: restoring '$RESTORE_PATH' into database '$DB_NAME' will overwrite current data."
echo "Set CONFIRM_RESTORE=YES to continue."

if [ "${CONFIRM_RESTORE:-}" != "YES" ]; then
  echo "Restore cancelled because CONFIRM_RESTORE is not YES." >&2
  exit 1
fi

MYSQL_PWD="$DB_PASSWORD" mysql --host="$DB_HOST" --user="$DB_USER" -e "CREATE DATABASE IF NOT EXISTS \`$DB_NAME\`;"

gzip -dc "$RESTORE_PATH" | MYSQL_PWD="$DB_PASSWORD" mysql --host="$DB_HOST" --user="$DB_USER" "$DB_NAME"

echo "Restore completed from: $RESTORE_PATH"
