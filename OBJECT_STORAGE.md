# Primary Resource Object Storage

The application supports two storage modes for admin-uploaded academic files:

- `local` — files are stored on the application host under `UPLOAD_DIR`.
- `s3` — files are stored in a private S3-compatible bucket and served through short-lived presigned URLs.

Existing resource records continue to use `/files/{uuid.ext}`. This means switching storage providers does not change the public resource URL shape or the tracked `/resource/{id}/download` flow.

## Recommended production mode

Use a private S3-compatible bucket and set:

```env
STORAGE_PROVIDER=s3
STORAGE_S3_BUCKET=your-private-resource-bucket
STORAGE_S3_REGION=us-east-1
STORAGE_S3_ENDPOINT=
STORAGE_S3_ACCESS_KEY=change_me
STORAGE_S3_SECRET_KEY=change_me
STORAGE_S3_PATH_STYLE=false
STORAGE_S3_KEY_PREFIX=resources
STORAGE_S3_PRESIGN_MINUTES=15
```

For AWS S3, `STORAGE_S3_ENDPOINT` can remain blank.

For Cloudflare R2, Backblaze B2 S3 API, MinIO, or another compatible provider, set the provider endpoint. Some providers may require `STORAGE_S3_PATH_STYLE=true`.

## Private-file delivery flow

```text
Student clicks Download
        ↓
/resource/{id}/download
        ↓
Lifetime counter + timestamped analytics event
        ↓
/files/{uuid.ext}
        ↓
Application generates short-lived signed object URL
        ↓
Browser downloads directly from object storage
```

The bucket does not need to be public.

## Local development

No cloud credentials are required:

```env
STORAGE_PROVIDER=local
```

Files continue to use `UPLOAD_DIR` and the local Docker volume.

## Permissions

The primary-storage credential should have access only to the configured resource bucket/prefix and should be able to:

- upload objects,
- read objects,
- delete objects.

Do not reuse administrator/root cloud credentials.

## Migration note

Enabling `STORAGE_PROVIDER=s3` affects newly uploaded files. Existing files currently stored in the local volume are not automatically copied to the bucket.

Before switching an existing production installation completely to S3, migrate existing `/files/...` objects into the configured bucket using the same object filenames under `STORAGE_S3_KEY_PREFIX`.

Keep a backup until all existing resources have been verified.

## Backup note

The existing MySQL/full-site backup system remains useful for database state and legacy/local files. When primary files live in object storage, configure provider-side object versioning or lifecycle protection where available and keep the existing off-server recovery process for the rest of the application data.

## Security

- Keep the bucket private.
- Do not commit storage credentials.
- Use a dedicated least-privilege key or workload identity.
- Keep presigned URL duration short.
- Rotate credentials if they are exposed.
- Use HTTPS for production object-storage endpoints.
