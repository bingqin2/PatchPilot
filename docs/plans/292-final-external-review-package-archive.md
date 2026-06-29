# 292 Final External Review Package Archive

## Goal

Freeze the final external-review evidence package as durable PatchPilot-local evidence. Operators should be able to archive the READY reviewer-facing package, reopen/download recent archived packages, and see the latest frozen package from the top-level evidence bundle and copied runbook.

## Scope

- Add a final external-review package archive VO, entity, converter, mapper, repository, service, and Flyway migration.
- Allow archive creation only when the current package is `READY` and `readyForExternalReview`.
- Add API endpoints to create/list/download archived final external-review packages and record protected admin audit evidence on creation.
- Promote the latest package archive into `DemoEvidenceBundleVo` and copied runbooks with compatibility defaults.
- Add dashboard API/App/panel support for archive creation, recent archive history, archive downloads, and top-level bundle display.
- Update README, product spec, frontend design, and execution log with validation evidence.

## Validation

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageArchiveServiceTests,InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests,DemoFinalExternalReviewEvidencePackageArchiveConvertTests,MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests,DemoFinalExternalReviewEvidencePackageArchiveMigrationTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx`
- Full backend/frontend validation before merge.
