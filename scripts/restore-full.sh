#!/bin/sh
set -eu

BACKUP_DIR="${BACKUP_DIR:-/backups}"
BUNDLE_FILE="${1:-}"

if [ -z "$BUNDLE_FILE" ]; then
  echo "Usage: restore-full.sh <full-backup.tar.gz>" >&2
  exit 1
fi

case "$BUNDLE_FILE" in
  /*) BUNDLE_PATH="$BUNDLE_FILE" ;;
  *) BUNDLE_PATH="${BACKUP_DIR}/${BUNDLE_FILE}" ;;
esac

if [ ! -f "$BUNDLE_PATH" ]; then
  echo "Full backup bundle not found: $BUNDLE_PATH" >&2
  exit 1
fi

if [ "${CONFIRM_RESTORE:-}" != "YES" ]; then
  echo "Full restore cancelled. Set CONFIRM_RESTORE=YES to continue." >&2
  exit 1
fi

TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

echo "Validating full backup bundle"
tar -tzf "$BUNDLE_PATH" >/dev/null

tar -xzf "$BUNDLE_PATH" -C "$TMP_DIR"
DB_FILE="$(find "$TMP_DIR" -maxdepth 1 -type f -name '*.sql.gz' | head -n 1)"
UPLOADS_FILE="$(find "$TMP_DIR" -maxdepth 1 -type f -name 'uploads_*.tar.gz' | head -n 1)"

if [ -z "$DB_FILE" ] || [ -z "$UPLOADS_FILE" ]; then
  echo "Bundle is missing database or uploaded-files backup" >&2
  exit 1
fi

/scripts/restore-db.sh "$DB_FILE"
/scripts/restore-uploads.sh "$UPLOADS_FILE"

echo "Full-site restore completed from: $BUNDLE_PATH"
