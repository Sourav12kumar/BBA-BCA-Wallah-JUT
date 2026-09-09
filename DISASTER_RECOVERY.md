# Full Disaster Recovery

Production data has two parts:

1. MySQL data (subjects, resources, notices, opportunities, download counters, etc.)
2. Uploaded files stored in the `resource_uploads` Docker volume

The backup service now protects both.

## Automatic full backup

The production `backup` service runs `/scripts/full-backup.sh`.

Each run creates synchronized files with the same timestamp:

```text
bba_bca_wallah_YYYY-MM-DD_HH-MM-SS.sql.gz
uploads_YYYY-MM-DD_HH-MM-SS.tar.gz
full_YYYY-MM-DD_HH-MM-SS.tar.gz
manifest_YYYY-MM-DD_HH-MM-SS.txt
```

The `full_*.tar.gz` bundle contains both the database dump and uploaded-files archive.

Default schedule:

```text
BACKUP_INTERVAL_SECONDS=86400
BACKUP_RETENTION_DAYS=14
BACKUP_ON_START=true
```

## Manual full backup

```bash
docker compose -f docker-compose.prod.yml run --rm \
  --entrypoint /scripts/full-backup.sh backup
```

## Manual database-only backup

```bash
docker compose -f docker-compose.prod.yml run --rm \
  --entrypoint /scripts/backup-db.sh backup
```

## Manual uploaded-files-only backup

```bash
docker compose -f docker-compose.prod.yml run --rm \
  --entrypoint /scripts/backup-uploads.sh backup
```

## Full restore

A full restore replaces both the database and uploaded files. Stop the application first:

```bash
docker compose -f docker-compose.prod.yml stop app backup
```

Then restore an exact full bundle:

```bash
docker compose -f docker-compose.prod.yml run --rm \
  -e CONFIRM_RESTORE=YES \
  --entrypoint /scripts/restore-full.sh \
  backup full_YYYY-MM-DD_HH-MM-SS.tar.gz
```

Restart services:

```bash
docker compose -f docker-compose.prod.yml up -d
```

Verify:

```bash
curl -f http://localhost:8080/actuator/health
```

Also manually verify several uploaded PDFs/documents and a few database records from the admin panel.

## Uploaded-files-only restore

```bash
docker compose -f docker-compose.prod.yml stop app backup

docker compose -f docker-compose.prod.yml run --rm \
  -e CONFIRM_RESTORE=YES \
  --entrypoint /scripts/restore-uploads.sh \
  backup uploads_YYYY-MM-DD_HH-MM-SS.tar.gz

docker compose -f docker-compose.prod.yml up -d
```

## Important production recommendation

The Docker `db_backups` volume protects against application/database failure on the same server, but it does not protect against total server or disk loss.

For stronger disaster recovery, periodically copy full backup bundles to an independent location such as object storage, another server, or encrypted offline storage.
