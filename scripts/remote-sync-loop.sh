#!/bin/sh
set -eu

INTERVAL_SECONDS="${REMOTE_SYNC_INTERVAL_SECONDS:-3600}"

case "$INTERVAL_SECONDS" in
  ''|*[!0-9]*)
    echo "REMOTE_SYNC_INTERVAL_SECONDS must be a positive integer" >&2
    exit 1
    ;;
esac

if [ "$INTERVAL_SECONDS" -le 0 ]; then
  echo "REMOTE_SYNC_INTERVAL_SECONDS must be greater than zero" >&2
  exit 1
fi

if [ "${REMOTE_SYNC_ON_START:-true}" = "true" ]; then
  /scripts/sync-backups-remote.sh
fi

while true; do
  echo "Next remote backup sync in ${INTERVAL_SECONDS} second(s)"
  sleep "$INTERVAL_SECONDS"
  /scripts/sync-backups-remote.sh
done
