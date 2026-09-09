#!/bin/sh
set -eu

UPLOADS_DIR="${UPLOADS_DIR:-/uploads}"
BACKUP_DIR="${BACKUP_DIR:-/backups}"
BACKUP_FILE="${1:-}"

if [ -z "$BACKUP_FILE" ]; then
  echo "Usage: restore-uploads.sh <uploads-backup.tar.gz>" >&2
  exit 1
fi

case "$BACKUP_FILE" in
  /*) RESTORE_PATH="$BACKUP_FILE" ;;
  *) RESTORE_PATH="${BACKUP_DIR}/${BACKUP_FILE}" ;;
esac

if [ ! -f "$RESTORE_PATH" ]; then
  echo "Uploads backup not found: $RESTORE_PATH" >&2
  exit 1
fi

if [ "${CONFIRM_RESTORE:-}" != "YES" ]; then
  echo "Restore cancelled. Set CONFIRM_RESTORE=YES to replace current uploaded files." >&2
  exit 1
fi

mkdir -p "$UPLOADS_DIR"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

echo "Validating uploaded-files archive: $RESTORE_PATH"
tar -tzf "$RESTORE_PATH" >/dev/null

echo "Extracting archive to temporary directory"
tar -xzf "$RESTORE_PATH" -C "$TMP_DIR"

echo "WARNING: replacing current uploaded files in $UPLOADS_DIR"
find "$UPLOADS_DIR" -mindepth 1 -maxdepth 1 -exec rm -rf {} +
cp -a "$TMP_DIR"/. "$UPLOADS_DIR"/

echo "Uploaded-files restore completed from: $RESTORE_PATH"
