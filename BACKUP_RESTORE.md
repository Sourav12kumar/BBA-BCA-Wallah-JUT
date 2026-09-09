# Database Backup & Restore

Production uses a dedicated backup container that creates compressed MySQL dumps and stores them in the persistent `db_backups` Docker volume.

## Default schedule

The defaults are:

```text
BACKUP_INTERVAL_SECONDS=86400
BACKUP_RETENTION_DAYS=14
BACKUP_ON_START=true
```

That means one backup every 24 hours, automatic cleanup of backups older than 14 days, and one backup immediately when the backup service starts.

## Start production with backups

```bash
docker compose -f docker-compose.prod.yml pull app
docker compose -f docker-compose.prod.yml up -d --build
```

Check the backup service:

```bash
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs --tail=100 backup
```

## Create a manual backup

```bash
docker compose -f docker-compose.prod.yml run --rm --entrypoint /scripts/backup-db.sh backup
```

Backups are named like:

```text
bba_bca_wallah_2026-09-09_20-15-00.sql.gz
```

## List available backups

```bash
docker compose -f docker-compose.prod.yml run --rm --entrypoint sh backup -c 'ls -lh /backups'
```

## Restore a backup

Restore is intentionally guarded. You must set `CONFIRM_RESTORE=YES` and explicitly provide a backup filename.

```bash
docker compose -f docker-compose.prod.yml run --rm \
  -e CONFIRM_RESTORE=YES \
  --entrypoint /scripts/restore-db.sh \
  backup bba_bca_wallah_2026-09-09_20-15-00.sql.gz
```

Before restoring production data, stop the application container to avoid writes during the restore:

```bash
docker compose -f docker-compose.prod.yml stop app
```

After the restore finishes:

```bash
docker compose -f docker-compose.prod.yml start app
```

Then verify health:

```bash
curl --fail http://localhost:${APP_PORT:-8080}/actuator/health
```

## Change backup frequency

Examples:

Every 12 hours:

```text
BACKUP_INTERVAL_SECONDS=43200
```

Every 6 hours:

```text
BACKUP_INTERVAL_SECONDS=21600
```

Keep backups for 30 days:

```text
BACKUP_RETENTION_DAYS=30
```

## Important limitation

These backups protect the **MySQL database** only. Uploaded PDFs/documents are stored separately in the `resource_uploads` Docker volume. For full disaster recovery, back up both `db_backups`/MySQL data and the uploaded-resource volume or move file storage to object storage such as S3-compatible storage.

## Recovery checklist

1. Identify the exact backup file to restore.
2. Stop the application container.
3. Run the guarded restore command with `CONFIRM_RESTORE=YES`.
4. Restart the application.
5. Check `/actuator/health`.
6. Open the admin dashboard and verify subjects, resources, notices, opportunities, and download counts.
7. Verify uploaded files separately because they are not contained in the SQL dump.
