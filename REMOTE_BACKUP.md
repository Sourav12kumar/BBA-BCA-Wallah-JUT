# Off-Server Backup Synchronization

The project can copy full-site recovery bundles from the local `db_backups` Docker volume to private S3-compatible object storage.

Supported examples include:

- AWS S3
- Cloudflare R2
- Backblaze B2 S3-compatible API
- MinIO
- Other S3-compatible providers

The application itself never receives these credentials. A separate `remote-backup` container performs the synchronization.

## 1. Configure a private bucket

Create a private bucket and credentials that can read, write, list and delete objects only inside the backup bucket/prefix you plan to use.

Add these values to the server `.env` file:

```text
S3_ENDPOINT=https://s3.amazonaws.com
S3_BUCKET=your-private-backup-bucket
S3_PREFIX=bba-bca-wallah
S3_ACCESS_KEY=your-access-key
S3_SECRET_KEY=your-secret-key
REMOTE_SYNC_INTERVAL_SECONDS=3600
REMOTE_SYNC_ON_START=true
REMOTE_RETENTION_DAYS=30
```

Use a dedicated prefix because remote retention cleanup operates inside that prefix.

## 2. Start production with remote backup enabled

Remote backup is an opt-in Compose profile.

```bash
docker compose -f docker-compose.prod.yml --profile remote-backup up -d --build
```

Normal production startup without the profile remains valid and does not require cloud credentials.

## 3. What gets uploaded

The remote service mirrors the local backup directory, including synchronized recovery artifacts such as:

```text
bba_bca_wallah_YYYY-MM-DD_HH-MM-SS.sql.gz
uploads_YYYY-MM-DD_HH-MM-SS.tar.gz
manifest_YYYY-MM-DD_HH-MM-SS.txt
full_YYYY-MM-DD_HH-MM-SS.tar.gz
```

The `full_*.tar.gz` bundle is the recommended disaster-recovery artifact because it contains both database and uploaded-file backups.

## 4. Manual remote sync

```bash
docker compose -f docker-compose.prod.yml --profile remote-backup run --rm \
  --entrypoint /scripts/sync-backups-remote.sh remote-backup
```

## 5. Download a remote backup to the server

```bash
docker compose -f docker-compose.prod.yml --profile remote-backup run --rm \
  --entrypoint /scripts/download-backup-remote.sh \
  remote-backup full_2026-09-09_20-30-00.tar.gz
```

The file is downloaded into the shared local backup volume. After that, restore it using the normal full restore procedure.

## 6. Restore a downloaded full bundle

Stop the app and backup scheduler first:

```bash
docker compose -f docker-compose.prod.yml stop app backup
```

Then restore:

```bash
docker compose -f docker-compose.prod.yml run --rm \
  -e CONFIRM_RESTORE=YES \
  --entrypoint /scripts/restore-full.sh \
  backup full_2026-09-09_20-30-00.tar.gz
```

Restart services:

```bash
docker compose -f docker-compose.prod.yml --profile remote-backup up -d
```

Verify health:

```bash
curl -f http://localhost:8080/actuator/health
```

## 7. Remote retention

`REMOTE_RETENTION_DAYS=30` removes remote objects under the configured prefix when they are older than the retention period.

Set it to `0` to disable remote cleanup and rely on provider lifecycle rules instead.

For production, provider-side bucket lifecycle rules are recommended as a second layer of protection.

## Security recommendations

- Keep the bucket private.
- Never commit `.env` or object-storage credentials.
- Use credentials restricted to the backup bucket/prefix only.
- Prefer bucket versioning when supported.
- Consider object lock/immutability for ransomware protection when your provider supports it.
- Periodically test an actual restore; an untested backup is not a proven recovery plan.
