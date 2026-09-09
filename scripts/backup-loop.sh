#!/bin/sh
set -eu

INTERVAL_SECONDS="${BACKUP_INTERVAL_SECONDS:-86400}"

case "$INTERVAL_SECONDS" in
  ''|*[!0-9]*)
    echo "BACKUP_INTERVAL_SECONDS must be a positive integer" >&2
    exit 1
    ;;
esac

if [ "$INTERVAL_SECONDS" -le 0 ]; then
  echo "BACKUP_INTERVAL_SECONDS must be greater than zero" >&2
  exit 1
fi

if [ "${BACKUP_ON_START:-true}" = "true" ]; then
  /scripts/backup-db.sh
fi

while true; do
  echo "Next database backup in ${INTERVAL_SECONDS} second(s)"
  sleep "$INTERVAL_SECONDS"
  /scripts/backup-db.sh
done
