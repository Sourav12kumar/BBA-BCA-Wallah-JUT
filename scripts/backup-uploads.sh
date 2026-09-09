#!/bin/sh
set -eu

UPLOADS_DIR="${UPLOADS_DIR:-/uploads}"
BACKUP_DIR="${BACKUP_DIR:-/backups}"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-14}"
TIMESTAMP="${BACKUP_TIMESTAMP:-$(date '+%Y-%m-%d_%H-%M-%S')}"
OUTPUT="${BACKUP_DIR}/uploads_${TIMESTAMP}.tar.gz"

mkdir -p "$BACKUP_DIR"

if [ ! -d "$UPLOADS_DIR" ]; then
  echo "Uploads directory not found: $UPLOADS_DIR" >&2
  exit 1
fi

echo "Creating uploaded-files backup: $OUTPUT"
tar -C "$UPLOADS_DIR" -czf "$OUTPUT" .

if [ ! -s "$OUTPUT" ]; then
  echo "Uploads backup is empty; removing failed backup" >&2
  rm -f "$OUTPUT"
  exit 1
fi

echo "Uploaded-files backup completed: $OUTPUT"

if [ "$RETENTION_DAYS" -gt 0 ] 2>/dev/null; then
  find "$BACKUP_DIR" -type f -name "uploads_*.tar.gz" -mtime "+$RETENTION_DAYS" -delete
  echo "Deleted uploaded-file backups older than ${RETENTION_DAYS} day(s)"
fi

printf '%s\n' "$OUTPUT"
