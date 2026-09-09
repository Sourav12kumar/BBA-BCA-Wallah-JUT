#!/bin/sh
set -eu

BACKUP_DIR="${BACKUP_DIR:-/backups}"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-14}"
TIMESTAMP="$(date '+%Y-%m-%d_%H-%M-%S')"
export BACKUP_TIMESTAMP="$TIMESTAMP"

DB_FILE="${DB_NAME:-bba_bca_wallah}_${TIMESTAMP}.sql.gz"
UPLOADS_FILE="uploads_${TIMESTAMP}.tar.gz"
BUNDLE="${BACKUP_DIR}/full_${TIMESTAMP}.tar.gz"
MANIFEST="${BACKUP_DIR}/manifest_${TIMESTAMP}.txt"

mkdir -p "$BACKUP_DIR"

/scripts/backup-db.sh >/dev/null
/scripts/backup-uploads.sh >/dev/null

cat > "$MANIFEST" <<EOF
created_at=$TIMESTAMP
database_backup=$DB_FILE
uploads_backup=$UPLOADS_FILE
EOF

tar -C "$BACKUP_DIR" -czf "$BUNDLE" "$DB_FILE" "$UPLOADS_FILE" "$(basename "$MANIFEST")"

if [ ! -s "$BUNDLE" ]; then
  echo "Full backup bundle is empty" >&2
  rm -f "$BUNDLE"
  exit 1
fi

if [ "$RETENTION_DAYS" -gt 0 ] 2>/dev/null; then
  find "$BACKUP_DIR" -type f -name 'full_*.tar.gz' -mtime "+$RETENTION_DAYS" -delete
  find "$BACKUP_DIR" -type f -name 'manifest_*.txt' -mtime "+$RETENTION_DAYS" -delete
fi

echo "Full-site backup completed: $BUNDLE"
