# Database Migrations

The application now uses Flyway for production schema changes.

## Runtime policy

- Hibernate runs with `ddl-auto=validate`.
- Flyway owns schema creation and future schema upgrades.
- Migrations live in `src/main/resources/db/migration`.
- Existing non-empty databases without Flyway history are baselined at version 1 instead of being recreated.
- Fresh databases execute `V1__initial_schema.sql`.

## Existing database adoption

Before the first Flyway-enabled deployment:

1. Create a verified database backup.
2. Deploy the new application version.
3. Flyway detects the existing non-empty schema and creates its schema-history table with baseline version 1.
4. Hibernate validates that the existing schema still matches the application entities.

No application tables should be dropped during this adoption flow.

## Fresh database

For an empty database, Flyway runs the V1 migration and creates:

- `subject`
- `academic_resource`
- `notice`
- `opportunity`
- `download_events`

including the subject/resource foreign key and useful indexes.

## Future schema changes

Never edit an already-applied migration in production. Add a new migration instead, for example:

```text
V2__add_resource_original_filename.sql
V3__add_admin_account.sql
```

Flyway applies pending migrations in version order and records checksums/history.

## Subject deletion safety

`academic_resource.subject_id` has a foreign-key constraint to `subject.id` without cascade deletion. A subject that is still referenced by resources cannot be removed at the database layer. Reassign or delete dependent resources first.

## Deployment rule

Always back up production data before applying a new migration. The existing backup and disaster-recovery procedures remain the rollback safety net for destructive or data-transforming migrations.
