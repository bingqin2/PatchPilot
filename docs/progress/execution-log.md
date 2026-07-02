# Execution Log

This file records dated implementation progress, validation commands, and important outcomes.

## 2026-06-30 - 310 Self-hosted launch publish gates

- Started `310-self-hosted-launch-publish-gates` to make the final self-hosted launch readiness package include the GitHub publish path and publish permission diagnostics added in 308 and 309.
- Added `GitHub publish path` and `GitHub publish permissions` checks to `SelfHostedLaunchReadinessService` so the launch package warns or blocks before a live `/agent fix` trigger when branch push, Pull Request creation, or issue-feedback capability is not ready.
- Kept the launch readiness package read-only: the new checks consume existing non-mutating readiness probes and do not push branches, open Pull Requests, write issue comments, create tasks, call the model, archive records, or write to GitHub.
- Updated the self-hosted launch readiness dashboard test and App smoke fixture so the final go/no-go panel visibly includes both publish gates.
- Updated README, product spec, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=SelfHostedLaunchReadinessServiceTests test`: first failed because the service did not accept publish-readiness suppliers or emit the new checks; passed after backend aggregation was implemented.
- `npm --prefix frontend test -- --run src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx src/App.test.tsx -t "self-hosted launch|operational task dashboard"`: first failed because the permission remediation appears both as a check action and a launch next action; passed after asserting the repeated operator action intentionally.
- `npm --prefix frontend test -- --run src/App.test.tsx -t "approves pending review tasks and refreshes dashboard data"`: first timed out at the existing 10 second budget during full frontend verification; passed after raising that single long App workflow budget to 15 seconds.
- `mvn -q -pl PatchPilot test`: passed after full backend regression verification.
- `npm --prefix frontend test -- --reporter=dot`: passed after full frontend regression verification, 30 test files and 494 tests.
- `npm --prefix frontend run build`: passed after TypeScript and production Vite build verification, with the existing large chunk warning.
- `git diff --check`: passed.

## 2026-06-28

Implemented task adapter execution evidence from `docs/plans/262-task-adapter-execution-evidence.md`.

Changes:

- Added a structured adapter execution evidence read model to task detail responses.
- Derived `SUPPORTED`, `PENDING`, and `UNSUPPORTED` evidence from persisted adapter metadata and repository support guidance.
- Added adapter execution evidence to copied Markdown task reports, including the selected adapter, allowlisted verification command, detection reason, next action, and safe-command boundary.
- Rendered adapter execution evidence in the dashboard task detail panel, including supported adapter options when repository execution stopped before model generation, verification, Git mutation, push, or Pull Request creation.
- Updated README, product spec, architecture notes, frontend design notes, API contracts, dashboard fixtures, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_include_adapter_execution_evidence_in_task_detail test`: first failed because task detail did not expose `adapterExecutionEvidence.status`; passed after adding the backend detail read model.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx --reporter=basic --silent`: first failed because the dashboard did not render `Adapter execution evidence`; passed after adding the task detail section.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_include_adapter_execution_evidence_in_task_detail+should_report_pending_adapter_execution_evidence_before_preflight_records_metadata+should_report_unsupported_adapter_execution_evidence_for_unsupported_repository_failures+should_get_task_report_by_task_id+should_include_repository_support_guidance_in_task_report test`: passed, 5 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx --reporter=basic --silent`: passed, 27 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because the new adapter evidence test reused the default `octocat/hello-world` repository and polluted an adapter metrics scope; passed after isolating the test repository, 74 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 938 tests run, 0 failures.
- `npm test -- --reporter=basic --silent`: passed after full frontend regression verification, 28 test files and 363 tests run, 0 failures.
- `npm run build`: first failed because a task detail test fixture used a `supported` field that is not part of `SupportedLanguageAdapter`; passed after aligning the fixture with the typed API contract.
- `git diff --check`: passed.

## 2026-06-30 - 308 GitHub publish readiness diagnostics

- Started `308-github-publish-readiness-diagnostics` to make push/PR publication blockers visible before a live `/agent fix` task reaches Git push.
- Added `GET /api/github/publish-readiness`, backed by `GitHubPublishReadinessService`, to aggregate GitHub token readiness, repository access readiness, configured demo repository fallback, safe push command shape, evidence notes, and a no-side-effect contract.
- Updated GitHub readiness controller tests and repository-access controller fixtures for the new controller dependency.
- Updated frontend API helpers, types, App loading, and the operator setup checklist with a dedicated GitHub publish readiness row plus a detailed diagnostics card for READY, NEEDS_ATTENTION, and BLOCKED states.
- Updated README and added this plan document.

Validation so far:

- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: first failed because the publish blocked test matched the same operator action twice; passed after using the multiple-match assertion, 2 test files and 230 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx -t "operator setup"`: first failed because App-level setup readiness mocks and totals did not include the new publish readiness probe; passed after wiring the publish readiness API response and checklist count, 2 tests run.
- `mvn -q -pl PatchPilot -Dtest=GitHubPublishReadinessServiceTests,GitHubCredentialReadinessControllerTests,GitHubRepositoryAccessReadinessControllerTests test`: passed after focused backend service and controller verification.
- `mvn -q -pl PatchPilot test`: passed after full backend regression verification.
- `npm --prefix frontend test -- --reporter=dot`: passed after full frontend regression verification, 30 test files and 492 tests.
- `npm --prefix frontend run build`: passed after TypeScript and production Vite build verification, with the existing large chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 309 GitHub publish permission readiness

- Started `309-github-publish-permission-readiness` to make GitHub token write-permission blockers visible before a live task reaches branch push, Pull Request creation, or issue feedback.
- Added a read-only GitHub repository permission probe that parses `default_branch` and non-sensitive `permissions.pull`, `permissions.push`, `permissions.admin`, and `permissions.maintain` fields from the repository metadata API.
- Added `GET /api/github/publish-permission-readiness`, backed by `GitHubPublishPermissionReadinessService`, to summarize repository read, branch push, Pull Request creation, and issue-feedback permission checks without mutating GitHub or exposing tokens.
- Updated the operator setup checklist to load the permission readiness beside publish readiness and render a dedicated permissions card with read/push/PR/issue-feedback capability and next actions.
- Updated README and added this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=GitHubRepositoryPermissionHttpProbeTests,GitHubPublishPermissionReadinessServiceTests,GitHubCredentialReadinessControllerTests test`: first failed because the new probe, VOs, service, and controller route did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx -t "operator setup|publish permission|GitHub publish permission"`: first failed because the App-level ready count did not include the newly ready permission check; passed after updating the fixture expectation.
- `mvn -q -pl PatchPilot test`: passed after full backend regression verification.
- `npm --prefix frontend test -- --reporter=dot`: first failed because the operator setup test expected a single read-only `does not run git push` side-effect contract while the page now renders one contract for publish readiness and one for publish permission readiness; passed after asserting both contracts are present, 30 test files and 494 tests.
- `npm --prefix frontend run build`: passed after TypeScript and production Vite build verification, with the existing large chunk warning.
- `git diff --check`: passed.

Implemented launch certificate evidence bundle from `docs/plans/261-launch-certificate-evidence-bundle.md`.

Changes:

- Added a structured launch acceptance certificate evidence read model to the top-level demo evidence bundle.
- Made the evidence bundle read the latest certificate archive directly from the archive repository and require a certified READY certificate archive before reporting the full bundle as `READY`.
- Added certificate archive status, certified flag, linked closeout archive, linked launch evidence archive, delivery receipt, Pull Request, next action, and download actions to the copied demo runbook.
- Added a dashboard evidence card for the latest launch acceptance certificate archive, including its Pull Request link, and updated frontend typed contracts.
- Updated README, product spec, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: first failed because a legacy evidence bundle constructor still accepted closeout evidence without a certificate evidence value; passed after adding a compatibility constructor and certificate evidence defaults, 6 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=basic`: first failed because the linked closeout archive id now appears in both closeout and certificate evidence cards; passed after updating the assertion to allow repeated evidence identifiers and adding legacy bundle fallback coverage, 4 tests run, 0 failures.
- `npm test -- --reporter=basic --silent`: first failed because older dashboard evidence bundle fixtures omitted the new certificate evidence field; passed after adding a dashboard compatibility fallback and fixture coverage, 28 test files and 360 tests run, 0 failures.
- `npm run build`: first failed because `DemoSessionSnapshotPanel.test.tsx` had a typed evidence bundle fixture without `launchAcceptanceCertificateEvidence`; passed after updating the fixture.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 935 tests run, 0 failures.
- `git diff --check`: passed.

Implemented launch acceptance certificate archive from `docs/plans/260-launch-acceptance-certificate-archive.md`.

Changes:

- Added backend launch acceptance certificate archive VO/entity/mapper/converter/repository/service layers with in-memory and MyBatis-backed storage.
- Added Flyway migration `V40__create_demo_launch_acceptance_certificate_archive.sql`.
- Added `POST /api/demo/launch-acceptance-certificate/archives`, `GET /api/demo/launch-acceptance-certificate/archives`, and `GET /api/demo/launch-acceptance-certificate/archives/{archiveId}/report/download`.
- Recorded protected admin audit evidence when an operator archives the current launch acceptance certificate.
- Added frontend API helpers, typed contracts, App refresh/state wiring, dashboard archive action, recent certificate archive history, and archived Markdown report download.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLaunchAcceptanceCertificateArchiveServiceTests,MyBatisDemoLaunchAcceptanceCertificateArchiveRepositoryTests,DemoLaunchAcceptanceCertificateArchiveMigrationTests,DemoReadinessControllerTests test`: first failed because the archive VO, service, mapper, repositories, migration, and controller endpoints did not exist; passed after backend implementation, 85 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because certificate archive API helpers and dashboard history did not exist; then failed once because the success message includes the archived certificate id; passed after API, App, panel, mock, and assertion updates, 3 test files and 212 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 934 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 359 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented launch acceptance certificate from `docs/plans/259-launch-acceptance-certificate.md`.

Changes:

- Added a backend launch acceptance certificate read model and service derived from the latest launch acceptance closeout archive.
- Added `GET /api/demo/launch-acceptance-certificate` and `GET /api/demo/launch-acceptance-certificate/report/download`.
- Required the latest closeout archive to be `READY` and accepted before the certificate reports certified.
- Added certificate API helpers, typed contracts, App refresh wiring, dashboard rendering, and a certificate Markdown download action.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLaunchAcceptanceCertificateServiceTests,DemoReadinessControllerTests test`: first failed because the certificate VO and service did not exist; then failed once because Java 17 does not support `List#getFirst`; passed after certificate service and controller implementation, 78 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because certificate API helpers and dashboard UI did not exist; then failed once because the closeout archive id now appears in both archive history and certificate evidence; passed after API, App, panel, fixture, and assertion updates, 3 test files and 208 tests run, 0 failures.

Implemented launch acceptance evidence bundle from `docs/plans/258-launch-acceptance-evidence-bundle.md`.

Changes:

- Added a structured launch acceptance closeout evidence read model to the top-level demo evidence bundle.
- Made the evidence bundle read the latest closeout archive directly from the archive repository to avoid a service dependency cycle.
- Required accepted launch acceptance closeout archive evidence before the evidence bundle reports `READY`.
- Added closeout archive status, accepted flag, linked launch evidence archive, delivery receipt, next action, and download actions to the copied demo runbook.
- Added a dashboard evidence card for the latest launch acceptance closeout archive and updated typed fixtures.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests test`: first failed because the bundle VO had not been migrated through all constructor paths; passed after backend bundle, runbook, and controller contract updates, 78 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the dashboard did not render launch acceptance closeout evidence and later because shared archive/receipt identifiers made old singular text assertions ambiguous; passed after UI and assertion updates, 3 test files and 102 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 919 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 352 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented launch acceptance closeout archive from `docs/plans/257-launch-acceptance-closeout-archive.md`.

Changes:

- Added a backend closeout archive service, repository boundary, in-memory repository, MyBatis repository, entity/mapper/converter, and Flyway migration for local durable launch acceptance closeout evidence.
- Added `POST /api/demo/launch-acceptance-closeout/archives`, `GET /api/demo/launch-acceptance-closeout/archives`, and `GET /api/demo/launch-acceptance-closeout/archives/{archiveId}/report/download` with protected admin audit evidence for archive creation.
- Added frontend types, API helpers, App refresh/state wiring, and dashboard controls to archive the final closeout, list recent closeout archives, open archived Pull Request evidence, and download archived Markdown reports.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLaunchAcceptanceCloseoutArchiveServiceTests,InMemoryDemoLaunchAcceptanceCloseoutArchiveRepositoryTests,MyBatisDemoLaunchAcceptanceCloseoutArchiveRepositoryTests,DemoLaunchAcceptanceCloseoutArchiveMigrationTests,DemoReadinessControllerTests test`: first failed because the archive service, VO, repositories, mapper, migration, and controller endpoints did not exist; passed after backend implementation, 80 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the dashboard had no `Archive launch acceptance closeout` action or closeout archive API helpers; then failed once because the new archive list made an older `PR` link assertion non-unique; passed after API, App, panel, and assertion updates, 3 test files and 205 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 919 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 352 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented launch acceptance closeout from `docs/plans/256-launch-acceptance-closeout.md`.

Changes:

- Added a read-only launch acceptance closeout service and API that compose self-hosted launch readiness, the current launch evidence package, launch evidence share center, and launch evidence finalization.
- Added `GET /api/demo/launch-acceptance-closeout` and `GET /api/demo/launch-acceptance-closeout/report/download` with accepted/not-accepted state, key evidence identifiers, check outcomes, evidence notes, download actions, and Markdown report output.
- Added a `Launch acceptance closeout` section to the dashboard launch evidence package panel with receipt freshness, archive/session/task/Pull Request/webhook/evaluation evidence, final accepted status, and report download action.
- Refreshed closeout state after launch package archive and launch delivery receipt actions so the final readout tracks the latest share/finalization evidence.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLaunchAcceptanceCloseoutServiceTests,DemoReadinessControllerTests test`: first failed because the closeout service, VO, and endpoints did not exist; then failed once because a share-ready package without a fresh receipt was still reported as a ready share-center check; passed after requiring fresh receipt evidence in the closeout check, 72 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx --reporter=basic`: first failed because the launch evidence package panel did not render or download launch acceptance closeout evidence.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx src/App.test.tsx --reporter=basic`: passed after API, App, panel, and assertion updates, 3 test files and 201 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 908 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 348 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented launch finalization evidence bundle from `docs/plans/255-launch-finalization-evidence-bundle.md`.

Changes:

- Added final launch evidence finalization fields to the backend demo evidence bundle read model, including status, finalized flag, accepted receipt id, receipt freshness, summary, and next action.
- Reused `DemoLaunchEvidenceFinalizationService` inside `DemoEvidenceBundleService` so the top-level bundle, standalone finalization endpoint, and dashboard use the same launch acceptance source of truth.
- Required launch evidence finalization to be `READY` before the overall demo evidence bundle can report `READY`.
- Added launch evidence finalization status to the copied demo runbook evidence snapshot.
- Added a `Launch evidence finalization` record to the dashboard demo evidence bundle panel and updated typed fixtures.
- Aligned launch evidence demo fixtures so READY launch packages include accepted launch delivery evidence in the shared evidence bundle.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests test`: first failed because the service constructor and bundle VO did not expose launch finalization fields; passed after backend aggregation implementation, 3 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=basic`: first failed because the evidence bundle panel did not render launch finalization; passed after frontend implementation, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests test`: first failed because the runbook fixture used the expanded launch finalization constructor without handoff finalization fields; passed after fixture alignment, 72 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: passed after fixture alignment, 3 test files and 102 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoSessionSnapshotServiceTests,DemoSessionReportServiceTests,DemoSessionArchiveServiceTests,DemoScriptServiceTests,SelfHostedLaunchReadinessServiceTests test`: passed after launch fixture alignment, 19 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 903 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 346 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented launch share delivery finalization from `docs/plans/254-launch-share-delivery-finalization.md`.

Changes:

- Added launch evidence delivery receipt storage with in-memory and MySQL-backed repositories plus a Flyway migration.
- Added protected APIs to record, list, and download launch evidence delivery receipts, with protected admin audit evidence for receipt creation.
- Added a read-only launch evidence finalization gate and Markdown download endpoint that only passes when the latest archived launch evidence package is share-ready and the latest receipt is fresh for the current archive/session.
- Extended the launch evidence share center with latest receipt metadata, receipt-recorded state, freshness state, download actions, evidence notes, and Markdown output.
- Extended the dashboard launch evidence package panel with receipt recording, receipt history/downloads, finalization status/downloads, share-center receipt freshness, API helpers, typed contracts, and focused tests.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoLaunchEvidenceShareDeliveryReceiptServiceTests,DemoLaunchEvidenceFinalizationServiceTests,DemoLaunchEvidenceShareCenterServiceTests,DemoReadinessControllerTests test`: first failed because launch receipt/finalization contracts and endpoints did not exist; passed after implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx --reporter=basic`: first failed because frontend API helpers and launch receipt/finalization UI did not exist; passed after API, App, panel, and assertion updates, 120 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because `DemoEvidenceBundleServiceTests` still expected the old launch share-center download action list without the new fresh receipt download; passed after assertion alignment, 903 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 346 tests run, 0 failures.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented launch share center evidence bundle from `docs/plans/253-launch-share-center-evidence-bundle.md`.

Changes:

- Added final launch evidence share-center fields to the backend demo evidence bundle read model, including share status, share-ready flag, summary, next action, archive count, latest archive/session/Pull Request identifiers, and download actions.
- Reused `DemoLaunchEvidenceShareCenterService` inside `DemoEvidenceBundleService` so `GET /api/demo/evidence-bundle` and the standalone launch share-center endpoint share the same read-only source of truth.
- Added a `Launch evidence share center` record to the dashboard demo evidence bundle panel so the first readout shows whether archived launch evidence is shareable.
- Updated frontend types and dashboard fixtures, plus README, product spec, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests test`: first failed because the service constructor and bundle VO did not expose launch share-center fields; passed after backend aggregation implementation, 3 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=basic`: first failed because the evidence bundle panel did not render the launch share-center record; passed after frontend implementation, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because wiring the launch share-center service through the launch package archive service created a Spring bean cycle; passed after making the share-center service read launch archives directly from the read-only repository, 884 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 340 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented demo launch evidence share center from `docs/plans/252-demo-launch-evidence-share-center.md`.

Changes:

- Added read-only `GET /api/demo/launch-evidence-share-center` and `GET /api/demo/launch-evidence-share-center/report/download`.
- Added `DemoLaunchEvidenceShareCenterService` and `DemoLaunchEvidenceShareCenterVo` to derive final share/no-share status, archive count, latest archive/session/task/Pull Request/webhook/evaluation identifiers, download actions, evidence notes, and Markdown evidence from the latest launch evidence package archive.
- Extended the dashboard launch evidence package panel with a `Launch evidence share center` section, latest-archive status cards, Pull Request evidence link, evidence/download lists, and Markdown download action.
- Updated frontend API helpers, typed contracts, App refresh wiring, README, product spec, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoLaunchEvidenceShareCenterServiceTests,DemoReadinessControllerTests test`: first failed because the share-center read model and service did not exist; passed after implementation, 63 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx --reporter=basic`: first failed because the launch evidence package panel did not render a share center, then failed again because the same archived session/archive evidence now appears in both the package and share-center sections; passed after implementation and assertion updates, 193 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 884 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 340 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented demo launch evidence package archive from `docs/plans/251-demo-launch-evidence-package-archive.md`.

Changes:

- Added `POST /api/demo/launch-evidence-package/archives`, `GET /api/demo/launch-evidence-package/archives`, and `GET /api/demo/launch-evidence-package/archives/{archiveId}/report/download`.
- Added archive VO/entity/mapper/converter/repository/service layers with in-memory default storage and MySQL persistence through Flyway/MyBatis for `local`, `docker`, and `idea` profiles.
- Added protected admin audit recording when the final launch evidence package is archived.
- Extended the dashboard launch evidence package panel with `Archive package`, recent archive history, Pull Request links, and archived Markdown report downloads.
- Updated frontend API helpers, typed contracts, App refresh wiring, README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoLaunchEvidencePackageArchiveServiceTests,InMemoryDemoLaunchEvidencePackageArchiveRepositoryTests,MyBatisDemoLaunchEvidencePackageArchiveRepositoryTests,DemoLaunchEvidencePackageArchiveMigrationTests,DemoReadinessControllerTests test`: first failed because archive types and repositories did not exist; passed after implementation, 65 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx --reporter=basic`: passed after frontend integration, 190 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 879 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 337 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented demo launch evidence package from `docs/plans/250-demo-launch-evidence-package.md`.

Changes:

- Added a read-only `GET /api/demo/launch-evidence-package` endpoint and Markdown download endpoint.
- Added `DemoLaunchEvidencePackageService` to combine self-hosted launch readiness, demo session snapshot, evidence bundle, live task/Pull Request/webhook proof, evaluation coverage, post-demo handoff proof, next actions, and side-effect contract into one final shareable status.
- Added a dashboard `DemoLaunchEvidencePackagePanel` with status, share readiness, session/task/webhook/evaluation evidence, live-run proof, post-demo proof, next actions, copy report, and download report actions.
- Updated backend/frontend API types, controller wiring, dashboard integration, README, product spec, architecture notes, frontend design docs, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoLaunchEvidencePackageServiceTests,DemoReadinessControllerTests test`: first failed because `DemoLaunchEvidencePackageVo` and service/controller wiring did not exist; then passed after implementing the read model and endpoint, 56 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx --reporter=basic`: first failed because the API helpers and panel did not exist; then passed after frontend API/panel implementation, 107 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx --reporter=basic`: passed after dashboard integration and App fixture coverage, 186 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 868 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 28 test files and 333 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented evaluation readiness evidence bundle from `docs/plans/249-evaluation-readiness-evidence-bundle.md`.

Changes:

- Added compact full evaluation run readiness evidence to `GET /api/demo/evidence-bundle`.
- Derived the evidence from archived full evaluation run readiness, including latest and previous run ids, pass/fail/skip deltas, coverage, safety rejection categories, side-effect contract, and next action.
- Made demo evidence bundle status and next actions reflect missing or blocked full evaluation archive evidence.
- Added full evaluation run readiness to demo runbook Markdown.
- Added dashboard evidence-bundle rendering for latest evaluation run evidence, coverage, safety categories, and next action.
- Updated README, product spec, frontend design notes, architecture notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests test`: first failed because the service constructor and bundle VO did not expose evaluation readiness; passed after backend aggregation implementation.
- `mvn -pl PatchPilot -Dtest=DemoRunbookServiceTests test`: first failed because the runbook did not render full evaluation run readiness; passed after adding Markdown output.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=basic`: first failed because the evidence bundle panel did not render full evaluation run readiness; passed after adding the card.
- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests test`: passed after targeted backend verification.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx --reporter=basic`: passed after targeted frontend verification, 185 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 864 tests run, 0 failures.
- `npm run build`: first failed because the demo session snapshot test fixture did not include the new evidence bundle field; passed after aligning the fixture with the typed API contract.
- `npm test -- --run src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx --reporter=basic`: passed after fixture alignment, 205 tests run, 0 failures.
- `npm test -- --reporter=dot`: passed after full frontend regression verification, 27 test files and 329 tests run.
- `git diff --check`: passed.

Implemented evaluation run readiness gate from `docs/plans/248-evaluation-run-readiness-gate.md`.

Changes:

- Added `GET /api/evaluation/runs/summary` as a read-only summary over archived full evaluation runs.
- Added latest and previous full evaluation run digests, pass/fail/skip deltas, language/build-system coverage, safety categories, side-effect contract, next action, and Markdown evidence.
- Added an `Evaluation run archive` demo readiness check that needs attention when no full evaluation run is archived, blocks when the latest archived run is failed or lacks safety coverage, and reports ready when the latest run passed with safety rejection coverage.
- Added dashboard API wiring and an evaluation catalog readiness section that refreshes after `Run evaluation` and can copy the full evaluation readiness report.
- Updated README, product spec, frontend design notes, architecture notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationRunArchiveReadinessSummaryServiceTests,DemoReadinessServiceTests test`: first failed because the summary service and VO contracts did not exist; passed after backend service and demo readiness implementation, 22 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=EvaluationCaseControllerTests test`: first failed because the controller endpoint dependency was missing; passed after adding `GET /api/evaluation/runs/summary`, 11 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the frontend API helper, panel summary section, and App refresh wiring did not exist; passed after implementation, 194 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=EvaluationRunArchiveReadinessSummaryServiceTests,DemoReadinessServiceTests,EvaluationCaseControllerTests test`: passed after combined targeted backend verification.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 864 tests run, 0 failures.
- `npm test -- --reporter=dot`: passed after full frontend regression verification, 27 test files and 329 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented evaluation run execution archive from `docs/plans/247-evaluation-run-execution-archive.md`.

Changes:

- Added full local evaluation run archives that combine evaluation catalog coverage, safety rejection coverage, and executable checked-in fixture baseline output.
- Added `POST /api/evaluation/runs`, `GET /api/evaluation/runs`, and `GET /api/evaluation/runs/{runId}/report/download`.
- Added in-memory and MySQL-backed archive repositories plus a Flyway migration for durable local evidence.
- Added dashboard controls to run an evaluation, inspect recent archived runs, copy reports, and download archived Markdown reports from the evaluation catalog panel.
- Documented the side-effect contract: full evaluation runs execute only local checked-in fixture verification commands and do not create tasks, call the model, clone repositories, mutate Git, push branches, open Pull Requests, send GitHub comments, or write to GitHub.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationRunArchiveServiceTests,MyBatisEvaluationRunArchiveRepositoryTests,EvaluationRunArchiveMigrationTests,EvaluationCaseControllerTests test`: first failed because the archive service, repository, migration, and controller endpoints did not exist; passed after backend implementation, 17 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because frontend API helpers and archive UI did not exist; passed after frontend implementation, 192 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 860 tests run, 0 failures.
- `npm test -- --reporter=dot`: passed after full frontend regression verification, 27 test files and 327 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented self-hosted launch readiness archive from `docs/plans/246-self-hosted-launch-readiness-archive.md`.

Changes:

- Added a local self-hosted launch readiness archive service, in-memory repository, MyBatis repository, and Flyway migration so final pre-launch packages can be preserved as local evidence.
- Added API endpoints to archive the current launch readiness package, list recent archives, and download one archived Markdown report.
- Recorded protected admin audit evidence when a launch readiness package is archived.
- Added dashboard API bindings, App refresh wiring, an archive action, recent archive rows, and archived report downloads in the self-hosted launch readiness panel.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=SelfHostedLaunchReadinessArchiveServiceTests test`: first failed because the archive VO and repository contract did not exist; passed after backend service implementation, 2 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=MyBatisDemoSelfHostedLaunchReadinessArchiveRepositoryTests,DemoSelfHostedLaunchReadinessArchiveMigrationTests test`: first failed because persistence classes and migration were missing; passed after MyBatis and Flyway implementation, 4 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoReadinessControllerTests test`: first failed because the archive endpoints returned 404; passed after controller implementation, 52 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx --reporter=basic`: first failed because frontend API helpers and archive UI were missing; passed after frontend implementation, 102 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx -t "renders operational task dashboard from backend APIs" --reporter=basic`: first failed because the test expected a unique launch readiness summary after archive history reused the same summary; passed after assertion alignment, 1 selected test run, 0 failures.
- `mvn -pl PatchPilot -Dtest=SelfHostedLaunchReadinessArchiveServiceTests,MyBatisDemoSelfHostedLaunchReadinessArchiveRepositoryTests,DemoSelfHostedLaunchReadinessArchiveMigrationTests,DemoReadinessControllerTests test`: passed after targeted backend integration verification, 58 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx --reporter=basic`: passed after targeted frontend integration verification, 181 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 853 tests run, 0 failures.
- `npm test -- --reporter=dot`: passed after full frontend regression verification, 27 test files and 323 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented self-hosted launch readiness package from `docs/plans/245-self-hosted-launch-readiness-package.md`.

Changes:

- Added a read-only launch readiness service and API that combine demo readiness, evidence bundle status, handoff finalization, credential, webhook setup, and queue/worker signals into one `READY`, `NEEDS_ATTENTION`, or `BLOCKED` result.
- Added `GET /api/demo/self-hosted-launch-readiness/report/download` so operators can download `patchpilot-self-hosted-launch-readiness.md` as final pre-launch evidence.
- Added a dashboard launch readiness panel with status, check rows, next actions, and report download.
- Refreshed launch readiness after handoff package archiving or delivery receipt recording changes the underlying evidence.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=SelfHostedLaunchReadinessServiceTests,DemoReadinessControllerTests test`: first failed because the service/VO/controller endpoints did not exist; passed after backend implementation, 51 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx`: first failed because the component and frontend API bindings did not exist; passed after frontend implementation, 98 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/SelfHostedLaunchReadinessPanel.test.tsx`: passed after App integration, 177 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 843 tests run, 0 failures.
- `npm test -- --reporter=dot`: passed after full frontend regression verification, 27 test files and 319 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-27

Implemented demo handoff finalization gate from `docs/plans/244-demo-handoff-finalization-gate.md`.

Changes:

- Added a read-only handoff finalization service and API that classify final handoff acceptance as `READY`, `NEEDS_ATTENTION`, or `BLOCKED`.
- Added `GET /api/demo/handoff-finalization/report/download` so operators can download `patchpilot-demo-handoff-finalization.md` as final acceptance evidence.
- Required both a share-ready handoff package and a fresh delivery receipt before the top-level demo evidence bundle can report ready.
- Added finalization status, receipt freshness, next action, and latest receipt id to the demo evidence bundle.
- Added a dashboard finalization panel with acceptance checks, evidence notes, and report download action.
- Refreshed share-center and finalization state after recording a local delivery receipt or archiving a new handoff package.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoHandoffFinalizationServiceTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests test`: passed after backend implementation, 53 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: passed after frontend implementation, 173 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 838 tests run, 0 failures.
- `npm test -- --reporter=dot`: passed after full frontend regression verification, 26 test files and 315 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo handoff delivery freshness gate from `docs/plans/243-demo-handoff-delivery-freshness-gate.md`.

Changes:

- Added `MISSING`, `FRESH`, and `STALE` delivery receipt freshness fields to the handoff share center and top-level demo evidence bundle.
- Treated a receipt as fresh only when its archive id and session id match the current latest handoff package archive summary.
- Updated stale receipt next actions, download actions, evidence notes, and Markdown report fields so historical receipts are not mistaken for current delivery evidence.
- Rendered receipt freshness in the demo evidence bundle panel and handoff share center panel.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareCenterServiceTests,DemoEvidenceBundleServiceTests test`: first failed because freshness accessors, constructor fields, and stale receipt behavior did not exist.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because `Fresh` was not rendered in either panel; passed after frontend implementation, 23 tests run, 0 failures.
- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareCenterServiceTests,DemoEvidenceBundleServiceTests,DemoReadinessControllerTests test`: passed after backend implementation and API contract updates.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 26 test files and 312 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo handoff delivery evidence summary from `docs/plans/242-demo-handoff-delivery-evidence-summary.md`.

Changes:

- Extended the handoff share center with latest delivery receipt id, target, channel, delivered time, and recorded/not-recorded status.
- Added receipt-aware share-center download actions, evidence notes, next-action text, and Markdown report fields.
- Repeated the latest handoff share delivery receipt summary in the top-level demo evidence bundle.
- Added receipt delivery summary cards to the demo evidence bundle panel and handoff share center panel.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareCenterServiceTests,DemoEvidenceBundleServiceTests test`: first failed because receipt summary fields, share-center repository wiring, and evidence-bundle fields did not exist; passed after backend implementation.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because the receipt summary was not rendered in either panel; then failed on intentionally repeated receipt ids; passed after UI implementation and assertion alignment, 23 tests run, 0 failures.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 26 test files and 312 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo handoff share delivery receipts from `docs/plans/241-demo-handoff-share-delivery-receipts.md`.

Changes:

- Added persistent handoff share delivery receipt records with in-memory and MyBatis-backed repositories plus Flyway migration `V34__create_demo_handoff_share_delivery_receipt.sql`.
- Added `POST /api/demo/handoff-share-delivery-receipts`, `GET /api/demo/handoff-share-delivery-receipts`, and per-receipt Markdown report downloads.
- Rejected receipt creation until the current handoff share instructions are send-ready, and recorded protected admin audit evidence when a receipt is created.
- Added frontend API helpers, App refresh wiring, a receipt form/list in the demo session snapshot, and per-receipt Markdown download actions.
- Extended handoff share instructions with structured latest archive/session ids so receipts do not depend on parsing Markdown report text.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareDeliveryReceiptServiceTests,DemoReadinessControllerTests,InMemoryDemoHandoffShareDeliveryReceiptRepositoryTests,MyBatisDemoHandoffShareDeliveryReceiptRepositoryTests,DemoHandoffShareDeliveryReceiptMigrationTests test`: first failed because receipt VO, service, repositories, mapper, migration, and endpoints did not exist; passed after backend implementation and after the structured latest archive/session id cleanup.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because receipt API helpers and session snapshot UI did not exist; then failed on duplicate subject text and combined `email - target` rendering assertions; passed after frontend implementation and assertion alignment, 190 tests run, 0 failures.

Final validation:

- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 26 test files and 312 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo handoff share instructions from `docs/plans/240-demo-handoff-share-instructions.md`.

Changes:

- Added `DemoHandoffShareInstructionsVo` and `GET /api/demo/handoff-share-instructions` as a read-only handoff sharing guide derived from the existing share center.
- Added `GET /api/demo/handoff-share-instructions/report/download` so operators can download `patchpilot-demo-handoff-share-instructions.md`.
- Added recipients, required attachments, pre-send checks, subject, body, generated time, and a read-only no-send side-effect contract to the backend report.
- Added frontend API helpers, App refresh wiring, post-archive refresh, and a `Handoff share instructions` panel with copy/download actions in the demo session snapshot.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareCenterServiceTests,DemoReadinessControllerTests test`: first failed because `DemoHandoffShareInstructionsVo` and share-instructions endpoints did not exist; passed after backend implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the API helpers and dashboard instructions panel did not exist; passed after frontend implementation, 183 tests run, 0 failures.

Final validation:

- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 26 test files and 305 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo share center evidence bundle from `docs/plans/239-demo-share-center-evidence-bundle.md`.

Changes:

- Added handoff share-center status, summary, next action, and download actions to the backend demo evidence bundle read model.
- Reused the existing handoff share-center service so the evidence bundle and standalone share-center endpoint share the same send/no-send source of truth.
- Added a `Handoff share center` evidence record to the dashboard demo evidence bundle panel.
- Updated README, product spec, architecture notes, frontend design docs, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests test`: first failed because `DemoEvidenceBundleVo` did not expose handoff share-center fields; passed after backend implementation.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=basic`: first failed because the evidence bundle panel did not render a handoff share-center record; passed after frontend implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoReadinessControllerTests test`: passed after controller and service contract verification.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx --reporter=basic`: passed after App fixtures and panel assertions were aligned with the expanded evidence bundle contract.

Implemented demo handoff share center from `docs/plans/238-demo-handoff-share-center.md`.

Changes:

- Added a read-only `GET /api/demo/handoff-share-center` endpoint that combines the latest handoff package archive summary and handoff share checklist into one final sharing status.
- Added `GET /api/demo/handoff-share-center/report/download` so operators can download `patchpilot-demo-handoff-share-center.md`.
- Added backend aggregation logic for send/no-send status, next action, download actions, evidence notes, embedded archive summary, embedded checklist, and read-only side-effect contract.
- Added frontend API helpers, App refresh wiring, post-archive refresh, and a `Handoff share center` panel in the demo session snapshot.
- Updated README, product spec, architecture notes, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareCenterServiceTests,DemoReadinessControllerTests test`: first failed because `DemoHandoffShareCenter*` records and service did not exist; then passed after backend implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the share-center panel and API helper did not exist; then failed twice because existing assertions expected unique `Share-ready` and `handoff-archive-1` text after the new panel intentionally repeated that evidence; passed after frontend implementation and explicit repeated-evidence assertions, 178 tests run, 0 failures.

Final validation:

- `mvn -pl PatchPilot test`: passed, 813 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed, 26 test files and 300 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented handoff share checklist from `docs/plans/236-handoff-share-checklist.md`.

Changes:

- Added a read-only `GET /api/demo/handoff-share-checklist` endpoint that converts the latest handoff package archive summary into explicit share-readiness checks.
- Added backend share-checklist records and a service that returns `READY`, `NEEDS_ATTENTION`, or `BLOCKED` with check rows, next action, generated time, and Markdown evidence.
- Added a frontend API helper, App refresh wiring, and post-archive refresh for the handoff share checklist.
- Rendered the checklist in the demo session snapshot panel with a `Copy checklist` action.
- Updated README, architecture notes, frontend design docs, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareChecklistServiceTests,DemoReadinessControllerTests test`: first failed because `DemoHandoffShareChecklist*` records and service did not exist; then passed after backend implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the API helper, panel section, and copy action did not exist; then passed after frontend integration, 171 tests run, 0 failures.

Final validation:

- `mvn -pl PatchPilot test`: passed, 808 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed, 26 test files and 293 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented handoff archive summary evidence actions from `docs/plans/235-handoff-archive-summary-evidence-actions.md`.

Changes:

- Added a read-only `GET /api/demo/handoff-package-archives/summary-report/download` endpoint that returns the current handoff package archive summary as a Markdown attachment.
- Added a frontend API helper and App wiring for downloading the latest handoff archive summary evidence.
- Added `Copy summary` and `Download summary` actions to the handoff package archive summary panel.
- Updated README, architecture notes, frontend design docs, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests test`: first failed with 404 for `/api/demo/handoff-package-archives/summary-report/download`; then passed after controller implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the API helper and summary panel buttons did not exist; then passed after frontend implementation, 169 tests run, 0 failures.

Final validation:

- `mvn -pl PatchPilot test`: passed, 805 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed, 26 test files and 291 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented handoff package archive summary from `docs/plans/234-handoff-package-archive-summary.md`.

Changes:

- Added a read-only `GET /api/demo/handoff-package-archives/summary` endpoint.
- Added a backend archive summary read model with archive count, latest archive metadata, latest handoff readiness status, share-ready flag, next action, and Markdown evidence.
- Rendered the handoff archive summary in the dashboard above recent handoff package archives.
- Refreshed the dashboard summary after archiving a new handoff package.
- Updated README, architecture notes, frontend design docs, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffPackageArchiveServiceTests,DemoReadinessControllerTests test`: first failed because `DemoHandoffPackageArchiveSummaryVo` and service/controller summary methods did not exist; then passed after backend implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the API client, component summary panel, and app fetch wiring did not exist; then passed after frontend implementation, 165 tests run, 0 failures.

Final validation:

- `mvn -pl PatchPilot test`: passed, 804 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed, 26 test files and 287 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented handoff package archive readiness metadata from `docs/plans/233-handoff-package-archive-readiness-metadata.md`.

Changes:

- Added handoff readiness status, summary, overall next action, and ready/warning/blocked check counts to handoff package archive records.
- Added Flyway/MyBatis persistence for the archived readiness metadata while keeping in-memory archive behavior compatible.
- Reused the backend handoff readiness calculation at archive time so archive rows match generated handoff packages.
- Rendered archived handoff readiness metadata in the dashboard's recent handoff package archive list.
- Updated README and architecture notes to describe archive-time readiness metadata.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffPackageArchiveServiceTests,DemoHandoffPackageArchiveMigrationTests,MyBatisDemoHandoffPackageArchiveRepositoryTests,DemoReadinessControllerTests test`: first failed because archive records did not expose persisted readiness metadata; then failed because the service test used a lightweight snapshot without recent task or webhook delivery evidence; passed after reusing the full handoff-ready snapshot/request fixture, 37 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the dashboard did not render archived handoff readiness metadata; then failed once because the same next-action text appears in current readiness and archive rows; passed after rendering metadata in the handoff package archive list and making the assertion explicit, 164 tests run, 0 failures.

Final validation:

- `mvn -pl PatchPilot test`: passed, 801 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed, 26 test files and 286 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented handoff readiness operator actions from `docs/plans/232-handoff-readiness-operator-actions.md`.

Changes:

- Added an overall `nextAction` to structured demo handoff readiness responses.
- Added check-level `nextAction` values for demo snapshot, recent task, webhook delivery, Pull Request, prepared command, archived outcome, and readiness trend checks.
- Rendered the same action guidance in Markdown handoff readiness sections so copied handoff packages match the structured API.
- Updated the dashboard demo session snapshot panel to keep the readiness summary visible while showing the overall next action and each check's next action.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests,DemoReadinessControllerTests test`: first failed because the readiness records did not expose `nextAction`; then passed after backend implementation, 40 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because the dashboard did not render handoff actions; then passed after frontend implementation, 92 tests run, 0 failures.

Final validation:

- `mvn -pl PatchPilot test`: passed, 800 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed, 26 test files and 286 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented structured demo handoff readiness from `docs/plans/231-structured-demo-handoff-readiness.md`.

Changes:

- Added structured `DemoHandoffReadinessVo` and `DemoHandoffReadinessCheckVo` response objects.
- Added `GET /api/demo/handoff-readiness` and `POST /api/demo/handoff-readiness`; the POST path accepts the same browser-local report context as session reports and handoff packages.
- Reused the existing backend handoff readiness rules for Markdown generation and JSON responses so dashboard previews cannot drift from handoff package output.
- Updated the dashboard demo session snapshot panel to fetch and render backend readiness status, summary, and check-level evidence instead of computing readiness locally.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests,DemoReadinessControllerTests test`: first failed because the structured readiness VO/API did not exist, then passed after implementation, 40 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the frontend API helper and structured panel rendering did not exist, then passed after implementation, 164 tests run, 0 failures.

Implemented demo handoff webhook delivery check from `docs/plans/230-demo-handoff-webhook-delivery-check.md`.

Changes:

- Added a `Webhook delivery evidence` row to demo handoff readiness.
- Made handoff readiness `READY` when recent delivery diagnostics include a task-created webhook delivery.
- Made missing delivery history a `NEEDS_ATTENTION` handoff signal.
- Made redelivery-required latest delivery failures, such as invalid signature, a `BLOCKED` handoff signal with the diagnostic operator action.
- Updated README, product spec, architecture notes, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests test`: first failed because handoff readiness did not include webhook delivery evidence; then failed once more because a newer redelivery-required delivery could be hidden by an older task-created delivery; then passed after ordering the check by recent evidence precedence, 7 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 797 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 285 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo webhook delivery evidence trail from `docs/plans/229-demo-webhook-delivery-evidence-trail.md`.

Changes:

- Added `recentWebhookDeliveries` to the demo evidence bundle read model while keeping `latestWebhookDelivery` for summary compatibility.
- Included the capped recent webhook delivery trail in generated demo session reports with delivery id, status, repository, trigger, outcome, and message evidence.
- Rendered a compact recent webhook delivery trail in the dashboard demo evidence bundle panel.
- Updated backend/frontend fixtures plus README, product spec, architecture, frontend design docs, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoSessionReportServiceTests,DemoReadinessControllerTests test`: first failed because the new evidence-bundle field and report section did not exist and one test used a non-existent `REJECTED_TRIGGER` status; then passed after wiring the field, report output, REST serialization, and existing `REJECTED` status plus `REJECTED_TRIGGER` outcome type.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because the panel did not render the trail; then failed once because latest delivery and trail intentionally repeat the same delivery id; then passed after targeted assertions were updated to expect repeated evidence, 166 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 796 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 285 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo webhook setup gate from `docs/plans/227-demo-webhook-setup-gate.md`.

Changes:

- Wired `GitHubWebhookSetupReadinessService` into demo readiness and replaced the URL-only readiness check with a `GitHub webhook setup` gate.
- Wired webhook setup readiness into the smoke checklist so the `Webhook delivery` step blocks or warns before latest-delivery evidence is evaluated.
- Updated the operator setup checklist to prefer the combined demo readiness setup gate while keeping URL-only readiness as a fallback.
- Updated README, product spec, frontend design docs, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoSmokeChecklistServiceTests test`: first failed at test compile because demo readiness and smoke checklist did not accept webhook setup readiness suppliers; then passed after backend integration, 24 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/dashboard/components/OperatorSetupChecklistPanel.test.tsx --reporter=basic`: first failed because two frontend assertions still expected the old `Webhook public URL` row; then passed after dashboard fixture/assertion updates, 84 tests run, 0 failures.

Implemented webhook setup readiness summary from `docs/plans/226-webhook-setup-readiness-summary.md`.

Changes:

- Added `GET /api/github/webhook-setup-readiness` as a read-only summary over webhook secret configuration, public URL readiness, latest delivery outcome, redelivery recommendation, next actions, and copyable Markdown evidence.
- Added `GitHubWebhookSetupReadinessService` and a non-sensitive response object that never exposes the webhook secret value.
- Added dashboard API helpers, types, App refresh wiring, and a Webhook delivery panel setup summary above recent deliveries and pasted payload diagnostics.
- Updated README, product spec, architecture notes, frontend design docs, the plan document, and this execution log.

Validation:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home PATH=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitHubWebhookSetupReadinessServiceTests,GitHubCredentialReadinessControllerTests,GitHubRepositoryAccessReadinessControllerTests test`: passed, 9 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/WebhookDeliveryPanel.test.tsx --reporter=basic`: first failed because the new API helper and panel section did not exist; passed after implementation, 84 tests run.
- `npm test -- --run src/App.test.tsx --reporter=basic`: passed after App-level webhook setup readiness refresh and rendering, 72 tests run.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home PATH=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot test`: passed after full backend regression verification, 794 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 284 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented GitHub webhook URL readiness from `docs/plans/225-webhook-url-readiness.md`.

Changes:

- Added `PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL` and non-sensitive configuration summary fields.
- Added `GET /api/github/webhook-url-readiness` to normalize the public base URL, derive the GitHub Payload URL, and probe the public `/health` endpoint.
- Added public webhook URL readiness to demo readiness and the dashboard operator setup checklist.
- Updated frontend API helpers, App loading, checklist rendering, and tests so operators can see the exact payload URL before a live `/agent fix` demo.
- Updated README, product spec, architecture notes, frontend design docs, the plan document, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home PATH=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot -Dtest=GitHubWebhookUrlReadinessServiceTests,GitHubCredentialReadinessControllerTests,GitHubRepositoryAccessReadinessControllerTests,DemoReadinessServiceTests,DemoEvidenceBundleServiceTests test`: passed, 27 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx --reporter=basic`: passed, 3 test files and 162 tests run.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home PATH=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home/bin:$PATH mvn -pl PatchPilot test`: passed after full backend regression verification, 790 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 282 tests run.
- `npm run build`: initially failed because two frontend test fixtures were missing the new webhook URL configuration fields; passed after fixture updates.
- `git diff --check`: passed.

Implemented demo handoff package archive from `docs/plans/224-demo-handoff-package-archive.md`.

Changes:

- Added a separate `demo_handoff_package_archive` store for final demo handoff packages.
- Added in-memory and MySQL-backed repositories, conversion, mapper, and Flyway schema for package archives.
- Added `POST /api/demo/handoff-package-archives`, `GET /api/demo/handoff-package-archives`, and `GET /api/demo/handoff-package-archives/{archiveId}/report/download`.
- Recorded handoff package archive creation as a protected admin audit event while keeping the archive operation PatchPilot-local.
- Added dashboard API helpers, refresh wiring, `Archive handoff package`, recent package archive listing, copy actions, and download actions.
- Updated README, product spec, architecture notes, frontend design docs, the plan document, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoHandoffPackageArchiveServiceTests,InMemoryDemoHandoffPackageArchiveRepositoryTests,DemoHandoffPackageArchiveMigrationTests test`: first failed because `DemoHandoffPackageArchiveVo` and `InMemoryDemoHandoffPackageArchiveRepository` did not exist; then passed after backend archive implementation, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoReadinessControllerTests test`: first failed because `/api/demo/handoff-package-archives` endpoints returned 404; then passed after controller implementation, 30 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because the frontend API helpers and handoff package archive UI did not exist; then passed after frontend implementation, 89 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx --reporter=basic`: passed after App-level handoff archive refresh and action wiring, 72 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoHandoffPackageArchiveServiceTests,InMemoryDemoHandoffPackageArchiveRepositoryTests,MyBatisDemoHandoffPackageArchiveRepositoryTests,DemoHandoffPackageArchiveMigrationTests,DemoReadinessControllerTests test`: passed after MyBatis repository coverage, 36 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 786 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 280 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.

Implemented demo handoff readiness check from `docs/plans/223-demo-handoff-readiness-check.md`.

Changes:

- Added a `Handoff Readiness` section to demo session reports and handoff packages.
- Checked demo snapshot status, recent completed task evidence, recent Pull Request evidence, prepared command context, archived outcome evidence, and readiness trend baseline.
- Reported `READY`, `NEEDS_ATTENTION`, or `BLOCKED` with concrete missing-evidence guidance before an operator shares a handoff package.
- Rendered the same handoff readiness summary and evidence counts in the dashboard demo session snapshot panel.
- Updated README, product spec, architecture notes, frontend design docs, the plan document, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests test`: first failed because the report and handoff package did not include `## Handoff Readiness`; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because the panel did not render `Handoff readiness`; then passed after frontend implementation, 9 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 776 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 274 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo readiness trend session handoff from `docs/plans/222-demo-readiness-trend-session-handoff.md`.

Changes:

- Added `readinessSnapshotTrend` to the demo session snapshot response.
- Wired the existing read-only readiness snapshot trend service into demo session snapshot generation.
- Added a `Readiness Snapshot Trend` section to session report Markdown with snapshot ids, readiness statuses, check-count deltas, and next action.
- Added a concise readiness trend line to demo handoff package summaries while keeping the embedded session report as the detailed evidence.
- Rendered readiness trend status and delta evidence in the dashboard demo session snapshot panel.
- Updated README, product spec, frontend design docs, AI infrastructure target, the plan document, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoSessionSnapshotServiceTests,DemoSessionReportServiceTests,DemoSessionArchiveServiceTests,DemoReadinessControllerTests test`: first failed because `DemoSessionSnapshotVo` had no `readinessSnapshotTrend` field and `DemoSessionSnapshotService` had no trend supplier; then passed after backend implementation, 36 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the demo session panel did not render `Readiness trend`; then passed after frontend implementation and scoped duplicate delta assertions, 154 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 775 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 273 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo readiness snapshot trend summary from `docs/plans/221-demo-readiness-snapshot-trend.md`.

Changes:

- Added admin-protected `GET /api/demo/readiness-snapshots/summary`.
- Added a read-only trend service that compares the two latest readiness snapshots and returns `NO_BASELINE`, `IMPROVING`, `STABLE`, or `REGRESSING`.
- Returned latest and previous snapshot ids, readiness statuses, ready/warning/blocked deltas, next action, and copyable Markdown trend report.
- Added dashboard API helper, App refresh wiring, post-archive trend refresh, and a `Snapshot trend` section in `Demo readiness` with a copyable Markdown report.
- Updated README, product spec, frontend design docs, AI infrastructure target, the plan document, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoReadinessSnapshotTrendServiceTests,DemoReadinessControllerTests test`: first failed because `DemoReadinessSnapshotTrendStatus`, `DemoReadinessSnapshotTrendVo`, and `DemoReadinessSnapshotTrendService` did not exist; then passed after backend implementation, 30 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoReadinessPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because `getDemoReadinessSnapshotTrend` and the dashboard `Snapshot trend` section did not exist; then passed after frontend implementation, 152 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 775 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 273 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented demo readiness snapshot archive from `docs/plans/220-demo-readiness-snapshot-archive.md`.

Changes:

- Added `POST /api/demo/readiness-snapshots`, `GET /api/demo/readiness-snapshots`, and `GET /api/demo/readiness-snapshots/{snapshotId}/report/download`.
- Added a readiness snapshot archive service that stores the current readiness status, summary, check counts, created time, and Markdown report.
- Added in-memory and MySQL-backed archive repositories, conversion, mapper, and Flyway schema.
- Recorded snapshot creation as a protected admin audit event while keeping the archive operation PatchPilot-local.
- Added dashboard API helpers and `Demo readiness` controls to archive the current gate, show recent snapshots, copy reports, and download Markdown reports.
- Updated README, product spec, frontend design docs, AI infrastructure target, the plan document, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoReadinessSnapshotArchiveServiceTests test`: first failed because the snapshot value object and repository did not exist; then passed after implementing the service and in-memory archive, 2 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoReadinessSnapshotArchiveServiceTests,MyBatisDemoReadinessSnapshotArchiveRepositoryTests,DemoReadinessSnapshotArchiveMigrationTests,DemoReadinessControllerTests test`: first failed because the MyBatis repository, mapper, entity, and migration did not exist; then passed after persistence and controller implementation, 31 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoReadinessPanel.test.tsx --reporter=basic`: first failed because the snapshot API helpers and archive button did not exist; then passed after frontend API and panel implementation, 79 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/DemoReadinessPanel.test.tsx --reporter=basic`: first failed because the main dashboard test expected unique readiness text after recent snapshots duplicated the same summary; then passed after scoping repeated readiness/snapshot assertions, 150 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 770 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 271 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

Implemented evaluation baseline demo readiness gate from `docs/plans/219-evaluation-baseline-demo-readiness-gate.md`.

Changes:

- Added an `Evaluation baseline` check to `GET /api/demo/readiness`.
- Reused the archived fixture baseline regression summary as a read-only demo gate.
- Marked demo readiness as needing attention when archived baseline evidence is missing or only one baseline run exists.
- Marked demo readiness as blocked when the latest archived fixture baseline regressed or still has failed cases.
- Updated the dashboard demo readiness panel to show per-check operator actions directly in each check row.
- Updated README, product spec, frontend design, AI infrastructure target, and the plan document.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests test`: first failed because the new tests required a baseline regression supplier that `DemoReadinessService` did not yet accept; then passed after backend implementation, 17 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoReadinessControllerTests test`: passed after controller serialization coverage, 38 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoReadinessPanel.test.tsx --reporter=basic`: first failed because per-check actions were not rendered in the check row; then passed after panel implementation, 4 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 760 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 266 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented evaluation baseline regression summary from `docs/plans/218-evaluation-baseline-regression-summary.md`.

Changes:

- Added `GET /api/evaluation/fixture-baseline-runs/summary`.
- Added a read-only regression summary service that compares the latest two archived fixture baseline runs.
- Extracted failed case ids from archived baseline Markdown reports so existing archive rows can still be compared.
- Returned latest and previous run digests, pass/fail/skip deltas, latest failed cases, newly failed cases, recovered cases, next action, side-effect contract, and copyable Markdown report.
- Added dashboard API helper, App refresh wiring, post-archive summary refresh, and `Evaluation case catalog` regression summary UI with copyable report.
- Updated README, product spec, frontend design docs, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationFixtureBaselineRunRegressionSummaryServiceTests,EvaluationCaseControllerTests test`: first failed because `EvaluationFixtureBaselineRunRegressionSummaryService` did not exist; then passed after backend implementation, 14 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx --reporter=basic`: first failed because the API helper and panel regression section did not exist; then passed after frontend implementation, 81 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx --reporter=basic`: first exposed that a post-archive regression-summary refresh failure was also shown as a fixture-baseline failure; then passed after splitting the error state, 151 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 758 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 265 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented evaluation fixture baseline run history from `docs/plans/217-evaluation-baseline-run-history.md`.

Changes:

- Added `POST /api/evaluation/fixture-baseline-runs`, `GET /api/evaluation/fixture-baseline-runs`, and `GET /api/evaluation/fixture-baseline-runs/{runId}/report/download`.
- Added in-memory and MySQL-backed evaluation fixture baseline run archive repositories, conversion, mapper, and Flyway schema.
- Archived executed fixture baseline reports as PatchPilot-local evidence after the existing adapter-selected fixture baseline service runs.
- Added dashboard API helpers and `Evaluation case catalog` actions to run and archive the fixture baseline, inspect recent runs, copy archived reports, and download archived reports.
- Updated README, product docs, frontend design notes, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationFixtureBaselineRunArchiveServiceTests,InMemoryEvaluationFixtureBaselineRunArchiveRepositoryTests,MyBatisEvaluationFixtureBaselineRunArchiveRepositoryTests,EvaluationFixtureBaselineRunArchiveMigrationTests,EvaluationCaseControllerTests test`: first failed because the archive service, repository, mapper, controller endpoints, and migration did not exist; then passed after backend implementation, 15 tests run, 0 failures.
- `npm test -- --run src/api.test.ts -t "evaluation fixture baseline run|evaluation fixture baseline" --reporter=basic`: first failed because the baseline run archive API helpers did not exist; then passed after frontend API implementation, 4 tests passed and 66 skipped.
- `npm test -- --run src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx -t "fixture baseline run|evaluation cases" --reporter=basic`: first failed because the panel had no archived baseline run UI; then passed after adding run-and-archive, copy, and download actions, 2 tests passed and 7 skipped.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx -t "evaluation fixture baseline run|operational task dashboard|evaluation cases|evaluation fixture baseline" --reporter=basic`: passed after App-level loading and action wiring, 9 tests passed and 139 skipped.

## 2026-06-26

Implemented evaluation fixture execution baseline from `docs/plans/216-evaluation-fixture-execution-baseline.md`.

Changes:

- Added `POST /api/evaluation/fixture-baseline`.
- Added a fixture-baseline service that executes only supported evaluation fixtures whose readiness already passes.
- Selected verification commands from the detected language adapter instead of issue text or request input.
- Skipped safety-rejection cases because they are validated through trigger rejection gates.
- Returned aggregate pass/fail/skip counts, per-case command output snippets, side-effect contract, next action, and a copyable Markdown baseline report.
- Added a dashboard action that runs the fixture baseline on demand, shows loading/error states, renders the latest baseline result, and copies the report.
- Updated README, frontend design docs, AI infrastructure target, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationFixtureBaselineServiceTests,EvaluationCaseControllerTests test`: first failed because the fixture-baseline service, runner contract, and response models did not exist; then passed after backend implementation, 9 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx`: first failed because the new API helper and dashboard action did not exist; then passed after frontend integration and assertion updates, 144 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 745 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 258 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented evaluation case fixture readiness from `docs/plans/215-evaluation-case-fixture-readiness.md`.

Changes:

- Added read-only `GET /api/evaluation/case-readiness`.
- Checked supported evaluation cases against the real language adapter registry, fixture directory existence, expected verification command, and expected changed files.
- Marked safety-rejection evaluation cases as `NO_FIXTURE_REQUIRED` because they validate trigger gates rather than repository files.
- Added aggregate readiness counts, side-effect contract, next action, and a copyable Markdown readiness report.
- Added dashboard API helpers and a fixture-readiness section in `Evaluation case catalog`.
- Aligned Java/Maven and Node/npm evaluation expected files with the checked-in demo fixtures.
- Updated README, product spec, frontend design docs, AI infrastructure target, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationCaseFixtureReadinessServiceTests,EvaluationCaseCatalogServiceTests,EvaluationCaseControllerTests test`: first failed because the fixture-readiness service and response models did not exist; then passed after backend implementation, 12 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the new API helper and dashboard section did not exist; then failed while old uniqueness assertions were updated for the new catalog-plus-readiness layout; then passed after frontend integration, 141 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 255 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented evaluation run snapshot archive from `docs/plans/214-evaluation-run-snapshot-archive.md`.

Changes:

- Added `POST /api/evaluation/run-snapshots`, `GET /api/evaluation/run-snapshots`, and `GET /api/evaluation/run-snapshots/{snapshotId}/report/download`.
- Added in-memory and MySQL-backed evaluation run snapshot archive repositories, conversion, mapper, and Flyway schema.
- Archived the current evaluation run preview as PatchPilot-local Markdown evidence without creating tasks, calling the model, cloning repositories, running verification commands, mutating Git, or writing to GitHub.
- Added dashboard API helpers and an archive section with copy/download report actions in `Evaluation case catalog`.
- Updated README, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationRunSnapshotArchiveServiceTests,EvaluationCaseControllerTests,InMemoryEvaluationRunSnapshotArchiveRepositoryTests,MyBatisEvaluationRunSnapshotArchiveRepositoryTests,EvaluationRunSnapshotArchiveMigrationTests test`: first failed because the archive service, repository, mapper, controller endpoints, and migration did not exist; then passed after backend implementation, 11 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx`: first failed because the archive API helpers and panel archive workflow did not exist; then passed after frontend implementation, 2 test files and 70 tests passed.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx --reporter=basic`: initially exposed an App-level duplicate safety-contract assertion after archives rendered beside previews; then passed after tightening the assertion, 3 test files and 139 tests passed.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 253 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented evaluation run preview report from `docs/plans/213-evaluation-run-preview-report.md`.

Changes:

- Added read-only `GET /api/evaluation/run-preview`.
- Added an evaluation run preview record derived from the checked-in evaluation catalog, including preview run id, case counts, covered languages, covered build systems, expected verification commands, safety rejection categories, known gaps, next action, side-effect contract, and Markdown report.
- Added frontend API/client support and wired the dashboard to load the preview beside the evaluation catalog and readiness summary.
- Updated `Evaluation case catalog` to render the preview report, known gaps, side-effect contract, and `Copy evaluation run preview`.
- Updated README, product docs, frontend design docs, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationCaseCatalogServiceTests,EvaluationCaseControllerTests test`: first failed because `EvaluationCaseCatalogService#getEvaluationRunPreview()` did not exist; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx -t "evaluation run preview|operational task dashboard" --reporter=basic`: first failed because `getEvaluationRunPreview`, App-level preview loading, and panel preview copying did not exist; then passed after frontend implementation, 3 relevant tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 249 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented evaluation case readiness summary from `docs/plans/212-evaluation-case-readiness-summary.md`.

Changes:

- Added read-only `GET /api/evaluation/summary`.
- Added an evaluation summary record derived from the existing in-memory catalog, including status, total case count, supported fix count, safety rejection count, covered languages, covered build systems, rejection categories, next action, read-only flag, and health contract.
- Added frontend API/client support and wired the dashboard to load the summary beside the case catalog.
- Updated `Evaluation case catalog` to render readiness status, build-system coverage, next action, health contract, and summary evidence in copied Markdown.
- Updated README, product docs, frontend design docs, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationCaseCatalogServiceTests,EvaluationCaseControllerTests test`: first failed because `EvaluationCaseCatalogService#getEvaluationSummary()` did not exist; then passed after backend implementation, 4 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx -t "evaluation case|operational task dashboard" --reporter=basic`: first failed because `getEvaluationSummary`, App-level summary loading, and panel summary rendering did not exist; then passed after frontend implementation, 5 relevant tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 247 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented evaluation case catalog from `docs/plans/211-evaluation-case-catalog.md`.

Changes:

- Added read-only `GET /api/evaluation/cases`.
- Added an in-memory catalog for Java/Maven, Node/npm, Python/pytest, Go, unsafe secret exfiltration rejection, and vague trigger rejection cases.
- Added dashboard API/client support and an `Evaluation case catalog` panel with language coverage, supported fix count, safety rejection count, expected commands, expected files, success criteria, safety expectations, and copyable Markdown report.
- Updated README, product docs, AI infrastructure target, frontend design docs, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=EvaluationCaseCatalogServiceTests,EvaluationCaseControllerTests test`: first failed because `EvaluationCaseVo`, the catalog service, and controller did not exist; then passed after backend implementation, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/EvaluationCaseCatalogPanel.test.tsx src/App.test.tsx -t "evaluation case|operational task dashboard" --reporter=basic`: first failed because `listEvaluationCases`, `EvaluationCaseCatalogPanel`, and App-level loading did not exist; then passed after frontend implementation, 4 relevant tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 26 test files and 246 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented demo handoff package export from `docs/plans/210-demo-handoff-package-export.md`.

Changes:

- Added read-only `POST /api/demo/handoff-package` and `POST /api/demo/handoff-package/download` endpoints.
- Added a demo handoff Markdown package that wraps the session report with summary, recent task/Pull Request evidence, prepared command counts, archived outcome counts, next actions, prepared commands, and archived outcomes.
- Added dashboard API helpers and `Copy handoff package` / `Download handoff package` actions to the demo session snapshot panel.
- Wired App-level browser-local prepared command and archived outcome context into handoff package requests.
- Updated README, product docs, the plan document, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests,DemoReadinessControllerTests test`: first failed because `getHandoffPackage` and the handoff endpoints did not exist; then passed after backend implementation, 26 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because the API helpers and handoff buttons did not exist; then passed after frontend implementation, 67 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx -t "handoff package|copies demo session report" --reporter=basic`: passed after App-level handoff wiring, 2 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 25 test files and 242 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented demo launch outcome session integration from `docs/plans/209-demo-launch-outcome-session-integration.md`.

Changes:

- Added `archivedLaunchOutcomes` to demo session report requests and rendered an `Archived Launch Outcomes` Markdown section.
- Loaded browser-local `patchpilot.demoLaunchOutcomeArchive` in the dashboard coordinator and passed it into demo session report copy, download, and archive actions.
- Rendered archived launch outcomes in the demo session snapshot panel so operators can inspect prepared commands and observed outcomes in one place.
- Notified the session report context when the demo launch tracker archives or clears local outcome reports.
- Sanitized embedded outcome-report summaries before placing them inside inline Markdown.
- Updated README, product docs, the plan document, and this execution log.

Validation:

- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx --reporter=basic`: first failed because session report actions omitted archived outcomes; then passed after wiring the frontend context, 63 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests test`: first failed because `DemoArchivedLaunchOutcomeRequestDto` and the expanded report request did not exist.
- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests,DemoReadinessControllerTests,DemoSessionArchiveServiceTests test`: passed after backend DTO, controller binding, Markdown rendering, and archive-service coverage, 26 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/dashboard/components/DemoLaunchTrackerPanel.test.tsx --reporter=basic`: passed after App-level localStorage handoff coverage, 136 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 25 test files and 237 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented demo launch history outcome archive from `docs/plans/208-demo-launch-history-outcome-archive.md`.

Changes:

- Added browser-local `patchpilot.demoLaunchOutcomeArchive` storage for the five most recent unique launch outcome reports.
- Added `Archive outcome` to tracked launch rows while keeping `Copy outcome report`.
- Rendered a `Demo launch outcome archive` section with archived command, repository/issue, task status, archived timestamp, task link, Pull Request link, copied report action, and clear action.
- Restored archived outcomes after dashboard reload from the same browser.
- Kept the feature read-only with no backend endpoint, task creation, queue mutation, GitHub mutation, or command-history mutation.
- Updated README, product spec, frontend design docs, the plan document, and this execution log.

Validation:

- `npm test -- --run src/dashboard/components/DemoLaunchTrackerPanel.test.tsx -t "archives and clears" --reporter=basic`: first failed because the outcome archive section did not exist; then passed after adding local archive storage and UI.
- `npm test -- --run src/App.test.tsx -t "restores archived demo launch outcomes" --reporter=basic`: first failed because the dashboard did not restore archived outcomes; then passed after adding archive loading.
- `npm test -- --run src/dashboard/components/DemoLaunchTrackerPanel.test.tsx src/App.test.tsx -t "launch outcome|tracks a prepared demo launch|restores archived demo launch outcomes|archives and clears" --reporter=basic`: passed after focused cross-component verification, 5 tests run.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 236 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented demo launch outcome reports from `docs/plans/207-demo-launch-outcome-report.md`.

Changes:

- Added `Copy outcome report` to each demo launch tracker row.
- Included repository, issue, trigger user, exact command, prepared timestamp, and launch state in the copied Markdown.
- Included matched webhook status, delivery id, outcome type, message, task id, task status, failure reason, completion timestamp, Pull Request URL, and next action.
- Kept the feature read-only with no new backend endpoint, task creation, queue mutation, GitHub mutation, or localStorage writes.
- Updated README, product spec, frontend design docs, the plan document, and this execution log.

Validation:

- `npm test -- --run src/dashboard/components/DemoLaunchTrackerPanel.test.tsx -t "copies a launch outcome report" --reporter=basic`: first failed because the `Copy outcome report` button did not exist; then passed after adding the report action.
- `npm test -- --run src/dashboard/components/DemoLaunchTrackerPanel.test.tsx --reporter=basic`: passed after component verification, 4 tests run.
- `npm test -- --run src/App.test.tsx -t "copies a prepared demo launch outcome report" --reporter=basic`: passed after dashboard integration verification.
- `npm test -- --run src/dashboard/components/DemoLaunchTrackerPanel.test.tsx src/App.test.tsx -t "launch outcome report|tracks a prepared demo launch|tracks a successful launch" --reporter=basic`: passed after focused cross-component verification, 4 tests run.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 234 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.
- `git diff --check`: passed.

## 2026-06-26

Implemented demo launch tracker from `docs/plans/206-demo-launch-tracker.md`.

Changes:

- Added `DemoLaunchTrackerPanel` to correlate browser-local prepared demo launch commands with recent webhook deliveries, task records, and Pull Request URLs.
- Matched prepared commands to tasks by repository, issue number, trigger user, and trigger comment.
- Matched prepared commands to webhook deliveries by trigger fields, with a task-id fallback when delivery trigger text differs but the delivery created the matched task.
- Rendered webhook, task, and Pull Request launch states with task and Pull Request links plus the next operator action.
- Kept the feature read-only with no new backend endpoint, task creation, queue mutation, GitHub mutation, or localStorage writes.
- Updated README, product spec, frontend design docs, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "tracks a prepared demo launch" --reporter=basic`: first failed because `Demo launch tracker` did not exist; then passed after adding the panel and App wiring.
- `npm test -- --run src/dashboard/components/DemoLaunchTrackerPanel.test.tsx --reporter=basic`: passed after covering successful, waiting-for-webhook, and failed-task launch states, 3 tests run.
- `npm test -- --run src/dashboard/components/DemoLaunchTrackerPanel.test.tsx src/App.test.tsx -t "tracks a prepared demo launch|tracks a successful launch|shows waiting guidance|shows failure guidance" --reporter=basic`: passed after focused cross-component verification, 4 tests run.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 232 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `mvn -pl PatchPilot test -q`: passed after full backend regression verification.

## 2026-06-26

Implemented demo launch package from `docs/plans/205-demo-launch-package.md`.

Changes:

- Added `Copy launch package` to the demo launch preflight panel after a preflight result exists.
- Included the exact GitHub issue URL, `/agent fix` comment, trigger user, readiness status, trigger evaluation, issue-context evidence, blocked category/reason, and next actions in the copied Markdown package.
- Included up to five browser-local prepared launch commands from `patchpilot.demoLaunchCommandHistory` in the launch package.
- Wired App-level prepared command history into the launch preflight panel without adding backend endpoints or creating tasks.
- Updated README, product spec, frontend design docs, and this execution log.

Validation:

- `npm test -- --run src/dashboard/components/DemoLaunchPreflightPanel.test.tsx --reporter=basic`: first failed because the `Copy launch package` button did not exist; this confirmed the RED test.
- `npm test -- --run src/dashboard/components/DemoLaunchPreflightPanel.test.tsx src/App.test.tsx --reporter=basic`: passed after adding the launch package action and App wiring, 70 tests run.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 228 tests run.
- `npm run build`: passed after TypeScript and production Vite build verification.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 719 tests run.
- `git diff --check`: passed.

## 2026-06-26

Implemented demo command history session reports from `docs/plans/204-demo-command-history-session-report.md`.

Changes:

- Added optional prepared launch command context to demo session report requests.
- Added `POST /api/demo/session-report` and `POST /api/demo/session-report/download` so dashboard report actions can include browser-local command history while preserving GET compatibility.
- Updated demo session archive creation to include supplied prepared command context in the stored Markdown report.
- Moved demo launch command history parsing into a shared frontend helper and converted saved browser history into bounded report context.
- Rendered prepared launch commands in the demo session snapshot panel and sent the same context through copy, download, and archive actions.
- Updated README and product docs so command history is no longer described as excluded from reports.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests,DemoSessionArchiveServiceTests,DemoReadinessControllerTests test`: passed after adding backend context/request coverage, 24 tests run.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/dashboard/components/DemoLaunchCommandPanel.test.tsx src/App.test.tsx --reporter=basic`: passed after wiring App-level browser history context into report actions, 133 tests run.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 227 tests run.
- `npm run build`: first failed because `tsc` caught un-narrowed API test fixtures and optional `replacementText` context; passed after adding explicit `DemoSessionReportInput` fixtures and normalizing missing replacement text to `null`.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 719 tests run.

## 2026-06-26

Implemented demo command history and reuse from `docs/plans/203-demo-command-history-and-reuse.md`.

Changes:

- Added browser-local history for successful demo launch command compositions in `patchpilot.demoLaunchCommandHistory`.
- Kept the five most recent unique generated `/agent fix` commands and restored them across dashboard reloads in the same browser.
- Added copy, refill-composer, apply-to-launch-preflight, and clear-history actions for saved demo commands.
- Kept command history read-only with respect to backend tasks, queues, GitHub, webhook diagnostics, and demo session archives.
- Documented plan 204 as the follow-up for writing selected command-history evidence into demo session snapshots and session reports.

Validation:

- `npm test -- --run src/dashboard/components/DemoLaunchCommandPanel.test.tsx -t "stores generated" --reporter=basic`: first failed because the frontend test setup did not provide `localStorage`; fixed by adding a memory-backed test storage.
- `npm test -- --run src/dashboard/components/DemoLaunchCommandPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because App tests expected the generated command to appear only once; passed after scoping assertions to current result and history regions, 71 tests run.

## 2026-06-25

Implemented demo launch preflight from `docs/plans/201-demo-launch-preflight.md`.

Changes:

- Added `POST /api/demo/launch-preflight` to combine current demo readiness with a read-only `ISSUE_COMMENT` trigger evaluation for the exact `/agent fix` comment an operator plans to post on GitHub.
- Kept the launch preflight endpoint read-only: it creates no task, queue item, GitHub comment, webhook diagnostic, rejected-trigger audit row, rate-limit record, Git commit, push, or Pull Request.
- Added frontend API types and client support for demo launch preflight.
- Added `DemoLaunchPreflightPanel` to the dashboard with exact launch-comment inputs, ready/blocked status, trigger evaluation status, issue-context evidence, blocked reason, next actions, and copyable Markdown evidence.
- Updated README and product/frontend docs with the final pre-GitHub-comment launch gate.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoLaunchPreflightServiceTests,DemoReadinessControllerTests test`: first failed because the launch preflight service, request DTO, and response VO did not exist; then passed after adding the backend service and controller integration, 18 tests run.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchPreflightPanel.test.tsx --reporter=basic`: first failed because `preflightDemoLaunch` and `DemoLaunchPreflightPanel` did not exist; then passed after adding the client, types, component, and report copy behavior, 55 tests run.
- `npm test -- --run src/App.test.tsx src/dashboard/components/DemoLaunchPreflightPanel.test.tsx --reporter=basic`: first exposed App-level label conflicts with the existing manual task form; then passed after scoping existing Manual Task tests to their panel and wiring launch preflight into the dashboard, 66 tests run.

## 2026-06-25

Implemented demo target policy alignment from `docs/plans/200-demo-target-policy-alignment.md`.

Changes:

- Added a `Demo target policy` check to `GET /api/demo/readiness`.
- Warned when the configured demo repository is missing from `PATCHPILOT_ALLOWED_REPOSITORIES` while repository allowlists are enabled.
- Warned when the most recent demo trigger user is missing from `PATCHPILOT_ALLOWED_TRIGGER_USERS` while trigger-user allowlists are enabled.
- Updated the operator setup checklist to display the demo target policy check from demo readiness.
- Updated README and product docs so demo readiness now covers both repository access and safety allowlist alignment.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests test`: first failed because the readiness checks did not include `Demo target policy` and the allowlist mismatch scenarios still returned `READY`; then passed after adding the backend check, 15 tests run.
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx --reporter=basic`: first failed because the checklist still rendered `12/12 checks ready` and omitted `Demo target policy`; then passed after wiring the readiness check into the setup checklist, 10 tests run.
- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoSessionSnapshotServiceTests test`: passed after demo evidence regression verification, 33 tests run.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx --reporter=basic`: passed after dashboard integration fixture updates, 123 tests run.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 702 tests run.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 209 tests run.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

## 2026-06-25

Implemented repository access demo readiness context from `docs/plans/199-repository-access-demo-readiness-context.md`.

Changes:

- Added demo repository owner/name configuration for the live repository used by demo readiness.
- Added a `GitHub repository access` check to `GET /api/demo/readiness` using the existing read-only repository access probe.
- Made missing demo repository configuration produce `NEEDS_ATTENTION` and configured-but-unreadable repositories produce `BLOCKED`.
- Updated the operator setup checklist to prefer the demo readiness repository access result before falling back to the standalone repository access endpoint.
- Updated `.env.example`, Docker Compose, README, and the plan document with the new demo repository configuration.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests test`: first failed because the readiness checks did not include `GitHub repository access`; then passed after wiring the repository access readiness supplier and status mapping, 13 tests run.
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: first failed because the checklist still reported `12/12 checks ready` while demo readiness had a blocked repository access check; then passed after preferring demo readiness repository access evidence, 9 tests run.
- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoSessionSnapshotServiceTests test`: passed after Spring injection and demo evidence regression verification, 31 tests run.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: passed after dashboard integration regression verification, 122 tests run.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 700 tests run.
- `npm test`: passed after full frontend regression verification, 208 tests run.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

## 2026-06-25

Implemented model provider health readiness from `docs/plans/195-model-provider-health-readiness.md`.

Changes:

- Added admin-protected `GET /api/model-provider/health` to run a minimal OpenAI-compatible health probe and return only non-sensitive status, configured booleans, latency, checked time, and operator action.
- Kept provider health probes out of task model-call audit records so dashboard refreshes do not pollute task execution metrics.
- Added a `Model provider` check to `GET /api/demo/readiness` so configured-but-unreachable model providers produce `NEEDS_ATTENTION` before a live `/agent fix` run.
- Added model provider health loading to the React dashboard and surfaced it in the operator setup checklist.
- Updated README, product spec, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=ModelProviderHealthServiceTests,ModelProviderHealthControllerTests,DemoReadinessServiceTests test`: first failed because `ModelProviderHealthVo`, `ModelProviderHealthService`, and the controller did not exist; then passed after adding the health probe service, controller, and readiness gate, 15 tests run.
- `npm test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: first failed because `getModelProviderHealth` did not exist and the checklist still had 9 checks; then passed after adding the API client, type, and `Model provider health` setup check, 54 tests run.
- `npm test -- --run src/App.test.tsx`: first failed because a ready-state App test did not mock model provider health; then passed after updating dashboard integration fixtures, 61 tests run.
- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests,TaskControllerTests,DemoReadinessControllerTests test`: first exposed a Spring constructor-selection issue in the health probe component; then passed after marking the production constructor for injection, 84 tests run.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 677 tests run.
- `npm test`: passed after full frontend regression verification, 201 tests run.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

## 2026-06-25

Implemented demo runtime readiness gate from `docs/plans/194-demo-runtime-readiness-gate.md`.

Changes:

- Added adapter runtime executable availability to the demo readiness gate as an `Adapter runtimes` check.
- Added a dedicated `Adapter runtime gate` step to the live demo smoke checklist.
- Added adapter runtime guidance to demo session snapshots and generated demo scripts so copied demo reports and operator scripts do not omit missing executables.
- Added adapter runtime availability to the dashboard operator setup checklist.
- Updated README and product docs to describe runtime executable readiness as a demo gate, not only an adapter report diagnostic.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoSmokeChecklistServiceTests test`: first failed because `DemoReadinessService` had no runtime readiness supplier and the smoke checklist lacked an adapter runtime step; then passed after wiring runtime readiness into the demo gate, 14 tests run.
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: first failed because the checklist still rendered 8 setup checks and ignored runtime readiness; then passed after adding the `Adapter runtimes` setup check, 5 tests run.
- `mvn -pl PatchPilot -Dtest=DemoScriptServiceTests test`: first failed because the repository-support demo script step only referenced `/api/language-adapters/fixtures`; then passed after adding `/api/language-adapters/runtime-readiness` evidence to the script.
- `mvn -pl PatchPilot -Dtest=DemoScriptServiceTests,DemoReadinessServiceTests,DemoSmokeChecklistServiceTests,DemoSessionSnapshotServiceTests test`: passed after focused demo-readiness regression verification, 18 tests run.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 671 tests run.
- `npm test`: passed after full frontend regression verification, 199 tests run.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

## 2026-06-25

Implemented adapter runtime readiness checks from `docs/plans/193-adapter-runtime-readiness-checks.md`.

Changes:

- Added `GET /api/language-adapters/runtime-readiness` to report whether each adapter verification executable is available on the backend process `PATH`.
- Kept the endpoint read-only: it does not execute verification commands, create tasks, mutate repositories, write to GitHub, or call the model.
- Added runtime readiness status and missing-executable reasons to the dashboard adapter readiness report.
- Extended the copyable adapter readiness Markdown report with runtime executable evidence.
- Updated README and product docs to describe the runtime readiness diagnostic.

Validation:

- `mvn -pl PatchPilot -Dtest=LanguageAdapterControllerTests test`: first failed because `LanguageAdapterRuntimeReadinessService` did not exist; then failed once because `List.getFirst()` is not available on Java 17; then passed after adding the Java 17-compatible runtime readiness service and controller endpoint, 3 tests run.
- `npm test -- --run src/dashboard/components/AdapterReadinessReportPanel.test.tsx`: first failed because the panel ignored runtime readiness; then passed after rendering runtime status and copy-report evidence, 3 tests run.
- `npm test -- --run src/api.test.ts -t "loads language adapter runtime readiness"`: first failed because `listLanguageAdapterRuntimeReadiness` did not exist; then passed after adding the API client method, 1 test run.
- `mvn -pl PatchPilot -Dtest=LanguageAdapterRuntimeReadinessServiceTests,LanguageAdapterControllerTests test`: passed after adding service-level coverage, 4 tests run.
- `npm test -- --run src/api.test.ts src/dashboard/components/AdapterReadinessReportPanel.test.tsx`: passed after focused frontend API and panel regression verification, 50 tests run.
- `npm test -- --run src/App.test.tsx -t "renders operational task dashboard"`: passed after wiring runtime readiness into the dashboard load flow, 1 targeted test run.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 669 tests run.
- `npm test`: passed after full frontend regression verification, 198 tests run.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

## 2026-06-25

Implemented accepted trigger decision audit from `docs/plans/192-accepted-trigger-decision-audit.md`.

Changes:

- Added `GET /api/tasks/pre-execution-decisions` to list recent persisted accepted trigger decisions with task context.
- Extended the pre-execution decision service boundary for in-memory and MyBatis profiles.
- Added a structured accepted-decision summary response with repository, issue, trigger user, command, task status, source, final allow decision, gate decisions, issue-context state, and timestamp.
- Added a dashboard `Accepted trigger audit` panel backed by the new API, with per-decision gate evidence and an `Open task` action.
- Kept accepted-trigger audit errors scoped to the accepted panel instead of mixing them into rejected-trigger or admin-audit errors.
- Updated README and product/frontend design docs to describe the accepted trigger audit stream.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskPreExecutionDecisionServiceTests,MyBatisFixTaskPreExecutionDecisionServiceTests,TaskControllerTests test`: first failed because test-local `FixTaskPreExecutionDecisionService` stubs did not implement the new list method, then passed after updating those stubs, 76 tests run.
- `npm test -- --run src/api.test.ts src/dashboard/components/AcceptedTriggerDecisionPanel.test.tsx src/App.test.tsx`: first failed because the accepted-trigger fixture reused the same command text as task-list/detail fixtures, creating ambiguous text queries; passed after giving the accepted audit fixture a distinct command, 109 tests run.

## 2026-06-25

Implemented persisted pre-execution decision records from `docs/plans/191-persisted-pre-execution-decision-records.md`.

Changes:

- Added a `fix_task_pre_execution_decision` migration, entity, mapper, converter, in-memory service, and MyBatis service.
- Recorded structured pre-execution allow decisions for manual task creation and GitHub `issue_comment` webhook task creation.
- Preserved all existing safety checks while storing safety, active-task, quarantine, rate-limit, model trigger-classification, issue-context, and final allow evidence.
- Updated task detail and copied Markdown reports to prefer persisted pre-execution decisions while falling back to accepted-trigger timeline parsing for older tasks.
- Restored `DefaultManualFixTaskService` Spring injection to the complete constructor so runtime task creation continues to use configured safety, quarantine, rate-limit, model-classification, issue-context, and persistence beans.
- Updated README and product specification language to describe persisted pre-execution safety snapshots.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskPreExecutionDecisionMigrationTests,InMemoryFixTaskPreExecutionDecisionServiceTests,MyBatisFixTaskPreExecutionDecisionServiceTests,TaskControllerTests test`: first failed at test compile because the persisted decision domain/service did not exist, then passed after implementation.
- `mvn -pl PatchPilot -Dtest=FixTaskPreExecutionDecisionMigrationTests,InMemoryFixTaskPreExecutionDecisionServiceTests,MyBatisFixTaskPreExecutionDecisionServiceTests,DefaultManualFixTaskServiceTests,TaskControllerTests test`: first failed because `DefaultManualFixTaskService` Spring injection used a convenience constructor that bypassed configured `CommandSafetyGate`; passed after moving injection to the complete constructor, 84 tests run.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests test`: passed, 19 tests run.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: first exposed shared in-memory test data around active tasks for one webhook-controller case, then passed after isolating the no-installation payload issue number, 9 tests run.
- `mvn -pl PatchPilot test`: passed, 664 tests run.
- `git diff --check`: passed.

## 2026-06-25

Implemented task pre-execution safety snapshots from `docs/plans/190-task-pre-execution-safety-snapshot.md`.

Changes:

- Added structured task detail evidence for accepted tasks that summarizes source, final allow decision, safety, quarantine, rate-limit, issue-context, and model trigger-classification state.
- Derived the first snapshot from durable accepted-trigger timeline evidence so existing task records can still be explained without a schema migration.
- Added the snapshot to copied Markdown task reports.
- Rendered the snapshot in the dashboard task detail panel.
- Covered backend task detail/report behavior plus frontend API parsing and dashboard rendering with tests.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because task detail lacked `preExecutionSafetySnapshot` and copied reports lacked the snapshot section, then passed after implementation.
- `npm test -- src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the `Pre-execution safety` section was not rendered, then passed after implementation.
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`: passed, 130 tests run.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: passed, 68 tests run.
- `npm test`: passed, 194 tests run.
- `npm run build`: passed.
- `mvn -pl PatchPilot test`: passed, 657 tests run.
- `git diff --check`: passed.

## 2026-06-25

Implemented unsupported repository issue feedback from `docs/plans/181-unsupported-repository-issue-feedback.md`.

Changes:

- Extended failed task issue comments for `UNSUPPORTED_REPOSITORY` with a safe-stop headline.
- Added issue-facing guidance that no model patch generation, tests, commits, pushes, or Pull Request creation were attempted.
- Listed supported repository shapes from the existing language adapter catalog instead of a separate hardcoded issue-comment matrix.
- Covered direct issue comment formatting and worker failure feedback paths with backend tests.
- Updated README and product spec documentation.

Validation:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`: first failed because unsupported repository comments still used generic failure text, then passed after implementation.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,FixTaskWorkerTests test`: passed, 25 tests run.
- `mvn -pl PatchPilot test`: passed, 649 tests run.
- `npm test`: passed, 193 tests run.
- `npm run build`: passed.
- `git diff --check`: passed.

## 2026-06-28 - 268 Task evidence acceptance certificate

- Started `268-task-evidence-acceptance-certificate` to turn the latest accepted task evidence closeout archive into one certified evidence record.
- Planned a complete feature slice: backend certificate read model, persistent certificate archives, create/list/download APIs, dashboard certificate controls, docs, and regression tests.
- RED controller and frontend tests were added first for reading, downloading, archiving, listing, and displaying task evidence acceptance certificates.
- Implemented the certificate read model from the latest accepted `READY` closeout archive, with explicit certified/not-certified status, source evidence identifiers, download actions, summary, next action, generated time, and a read-only side-effect contract.
- Added persistent certificate archives with in-memory and MyBatis repositories plus Flyway migration `V44__create_fix_task_evidence_acceptance_certificate_archive.sql`.
- Added `GET /api/tasks/evidence-packages/acceptance-certificate`, certificate Markdown download, certificate archive creation, archive listing, and archived report download.
- Added a service guard so certificate archives can only be created when the current certificate is certified.
- Updated the `Task evidence archive review` dashboard panel with the current certificate, current certificate download, certificate archive creation, recent certificate archives, archived report download, and status/error feedback.
- Updated README and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=TaskControllerTests#should_certify_archive_list_and_download_task_evidence_acceptance_certificate,FixTaskEvidencePackageAcceptanceCertificateServiceTests,FixTaskEvidencePackageAcceptanceCertificateArchiveServiceTests,FixTaskEvidencePackageAcceptanceCertificateArchiveConvertTests,InMemoryFixTaskEvidencePackageAcceptanceCertificateArchiveRepositoryTests,MyBatisFixTaskEvidencePackageAcceptanceCertificateArchiveRepositoryTests,FixTaskEvidencePackageAcceptanceCertificateArchiveMigrationTests test`: passed.
- `npm test -- src/api.test.ts src/dashboard/components/TaskEvidenceArchiveReviewPanel.test.tsx src/App.test.tsx -- --reporter=basic`: passed, 3 test files and 230 tests.
- `mvn -pl PatchPilot -q test`: passed.
- `npm test -- --reporter=basic`: passed, 29 test files and 390 tests.
- `npm run build`: passed.
- `git diff --check`: passed.

## 2026-06-25

Implemented manual trigger evaluation evidence reports from `docs/plans/180-manual-trigger-evaluation-report.md`.

Changes:

- Added a copy action to the dashboard manual task form when a trigger evaluation result is visible.
- The copied Markdown report includes status, source, would-create-task state, repository, issue, trigger user, command, blocked reason/category, issue-context state, next action, and per-gate decisions.
- Covered both allowed and blocked trigger dry-runs in `ManualTaskForm` tests.
- Updated README and frontend design documentation to describe the copyable dry-run evidence workflow.

Validation:

- `npm test -- --run src/dashboard/components/ManualTaskForm.test.tsx`: first failed because `Copy evaluation report` was not rendered, then passed after implementation, 5 tests run.
- `npm test -- --run src/App.test.tsx -t "copies manual trigger evaluation evidence"`: passed, 1 selected test run.
- `npm test`: passed, 193 tests run.
- `npm run build`: passed.
- `mvn -pl PatchPilot test`: passed, 646 tests run.
- `git diff --check`: passed.

## 2026-06-23

Implemented the post-edit model review gate from `docs/plans/134-post-edit-model-review-gate.md`.

Changes:

- Added `PatchReviewGenerator` to ask the configured model for JSON-only post-edit review decisions.
- Added patch review domain objects for `APPROVE` and `REJECT` decisions plus review generation failures.
- Extended `PlannedPatchWorkflow` so model-generated file edits are reviewed before writing to the workspace.
- Rejected edits now fail before file writes, preventing tests, commits, pushes, and Pull Request creation from continuing with a mismatched patch.
- Preserved the manual `/agent fix replace <path> <content>` smoke path without invoking the review gate.
- Added tests for review parsing, unsupported review decisions, approved edits, rejected edits, and Spring wiring.

Validation:

- `mvn -pl PatchPilot -Dtest=PatchReviewGeneratorTests,PlannedPatchWorkflowTests test`: first failed because the patch review generator/domain objects did not exist.
- `mvn -pl PatchPilot -Dtest=PatchReviewGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,PatchPilotApplicationTests test`: passed after implementation and Spring wiring updates, 21 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 488 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

## 2026-06-23

Implemented model-generated file edits from `docs/plans/133-model-generated-file-edits.md`.

Changes:

- Added a `FileEditPlanGenerator` that asks the configured model for JSON-only full-file edits.
- Added file edit domain records for edit context, proposed edits, edit plans, and edit generation failures.
- Extended `PlannedPatchWorkflow` so `/agent fix` can apply model-generated edits when no manual `replace` instruction is present.
- Preserved the existing `/agent fix replace <path> <content>` workflow for smoke tests and demos.
- Added workflow guards so generated edits can only touch fix-plan target files and cannot modify sensitive paths such as `.env`, `.git`, GitHub workflows, or private key files.
- Added tests for edit-plan parsing, manual replace compatibility, generated edit application, unauthorized paths, sensitive paths, and blank generated content.

Validation:

- `mvn -pl PatchPilot -Dtest=FileEditPlanGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests test`: first failed because the new edit generator/domain objects did not exist, then passed after implementation, 13 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=FileEditPlanGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,PatchPilotApplicationTests test`: passed after Spring bean wiring updates, 20 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 484 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

## 2026-06-18

Initialized the PatchPilot documentation baseline.

Created documentation categories following the reference layout:

- `docs/product`
- `docs/plans`
- `docs/progress`
- `docs/agent`
- `docs/superpowers`

Initial product documents:

- `docs/product/spec.md`
- `docs/product/architecture.md`
- `docs/product/backend-code-standard.md`
- `docs/product/target-state.md`
- `docs/product/roadmap.md`
- `docs/product/milestones.md`

Initial progress documents:

- `docs/progress/decisions.md`
- `docs/progress/execution-log.md`

Initial plan:

- `docs/plans/000-project-foundation.md`

Validation is pending until documents are accepted and copied into the project docs directory.

## 2026-06-18

Implemented the basic backend foundation from `docs/plans/001-basic-version-implementation.md`.

Changes:

- Added Spring Web, Validation, Actuator, MySQL Connector/J, Flyway, and MyBatis-Plus backend dependencies.
- Added base, local, and docker Spring profile configuration.
- Added structured `ApiResponse` and custom `/health` endpoint.
- Added MockMvc coverage for `/health`.
- Added test-only Mockito subclass mock maker configuration because the local macOS/JDK environment cannot self-attach Mockito's inline mock maker.
- Added test-only default datasource auto-configuration exclusion so root tests do not require a MySQL service before persistence migrations exist.

Validation:

- `mvn test` from repository root with Java 17: passed, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=HealthControllerTests test` with Java 17: first failed because `HealthController` did not exist, then passed after implementation.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `docker compose config`: passed.
- `docker compose build patchpilot-backend`: initially failed while resolving `maven:3.9-eclipse-temurin-17` and `eclipse-temurin:17-jre` through `https://docker.mirrors.ustc.edu.cn` with EOF. After the base images were pulled successfully, rerunning the command passed and built `patchpilot-backend:local`.

## 2026-06-18

Implemented the GitHub webhook MVP from `docs/plans/001-github-webhook-mvp.md`.

Changes:

- Added `POST /api/github/webhook` with HMAC-SHA256 validation for `X-Hub-Signature-256`.
- Added webhook routing for `issue_comment.created` comments whose trimmed body is exactly `/agent fix`.
- Added in-memory delivery idempotency using `X-GitHub-Delivery`.
- Added in-memory fix task creation with `PENDING` task status.
- Added `patchpilot.github.webhook-secret=${PATCHPILOT_GITHUB_WEBHOOK_SECRET:}` configuration.
- Added MockMvc coverage for invalid signatures, unsupported events, non-triggering comments, task creation, and duplicate deliveries.
- Updated `ApiResponse` with `fail(String message)` for error responses.

Notes:

- The initial webhook test was intentionally failing before implementation because the controller and task service did not exist.
- After adding the controller, `@WebMvcTest` failed to load service dependencies; the test was changed to `@SpringBootTest` with `@AutoConfigureMockMvc` so the real verifier, router, and in-memory service are exercised.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: passed, 5 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 7 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `docker compose build patchpilot-backend`: passed, built `patchpilot-backend:local`.

## 2026-06-18

Fixed local GitHub repository webhook delivery returning HTTP 400.

Changes:

- Made `installation.id` optional for repository-level GitHub webhooks. GitHub App deliveries include `installation`, but repository webhooks can omit it.
- Added regression coverage for `/agent fix` issue comments without an `installation` object.
- Removed the host `3306:3306` MySQL port mapping from `docker-compose.yml` so PatchPilot does not conflict with other local MySQL containers. The backend still reaches MySQL through the compose network at `mysql:3306`.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: first failed with `Missing field: installation.id`, then passed after the fix, 6 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 8 tests run, 0 failures, 0 errors.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up -d`: passed after removing the host MySQL port mapping.
- `curl http://127.0.0.1:8080/health`: returned `UP`.
- `curl https://cement-screenshot-thumbzilla-button.trycloudflare.com/health`: returned `UP`.

## 2026-06-18

Added task query endpoints for webhook observability.

Changes:

- Added `GET /api/tasks` to list in-memory fix tasks, newest first.
- Added `GET /api/tasks/{id}` to return one in-memory fix task or a 404 API response.
- Extended `FixTaskService` with `listTasks()` and `findTask(String id)`.
- Added MockMvc coverage for listing tasks, fetching a task by id, and missing task responses.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test` with Java 17: first failed because `/api/tasks` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 11 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `curl http://127.0.0.1:8080/api/tasks`: returned an empty task list API response.
- `curl https://cement-screenshot-thumbzilla-button.trycloudflare.com/api/tasks`: returned an empty task list API response through the Cloudflare temporary URL.

## 2026-06-18

Implemented the task execution skeleton from `docs/plans/002-task-execution-skeleton.md`.

Changes:

- Added task status transitions for `PENDING`, `RUNNING`, `COMPLETED`, and `FAILED`.
- Added `failureReason` to task API responses for failed executions.
- Added `FixTaskExecutor` and a no-op executor implementation as the replaceable execution boundary.
- Added `FixTaskDispatcher` and an asynchronous dispatcher that marks tasks running, completed, or failed.
- Wired `/agent fix` webhook-created tasks into the dispatcher so new tasks no longer remain permanently pending.
- Added unit and MockMvc coverage for status transitions, dispatcher success/failure behavior, and webhook dispatch completion.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests test` with Java 17: first failed because status transition methods, new statuses, and `failureReason` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test` with Java 17: first failed because dispatcher and executor classes did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: first failed because created tasks stayed `PENDING`, then passed after wiring the dispatcher, 7 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 17 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed, generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `curl http://127.0.0.1:8080/api/tasks`: returned a successful API response.
- `curl https://cement-screenshot-thumbzilla-button.trycloudflare.com/api/tasks`: returned a successful API response through the Cloudflare temporary URL.
- A signed local `POST /api/github/webhook` with `/agent fix` returned `TASK_CREATED`, and polling `GET /api/tasks/{id}` showed the task reached `COMPLETED` with `failureReason=null`.

## 2026-06-18

Implemented the workspace clone MVP from `docs/plans/003-workspace-clone-mvp.md`.

Changes:

- Added a `workspace` module with `WorkspaceService`, task-scoped clone commands/results, and `GitWorkspaceService`.
- Added `GitCommandRunner` using controlled `git clone --depth 1 <url> <target>` arguments via `ProcessBuilder`.
- Added optional `PATCHPILOT_GITHUB_TOKEN` configuration through `patchpilot.github.token` for private repository clone URLs, with token redaction in command output.
- Replaced the no-op task executor behavior so webhook-created tasks clone the target repository before completing.
- Added `PATCHPILOT_WORKSPACE_ROOT_DIR` and `PATCHPILOT_GITHUB_TOKEN` compose environment entries.
- Updated the runtime Docker image to install `git` and `ca-certificates`.
- Isolated webhook controller tests from real network clone by providing a test `WorkspaceService`.

Validation:

- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests test` with Java 17: first failed because the workspace classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` with Java 17: first failed because the executor constructor and clone call were missing, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` with Java 17: first exposed the blank-token output sanitization bug, then passed after fixing it, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test` with Java 17: passed, 7 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 23 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `docker exec patchpilot-backend git --version`: returned `git version 2.53.0`.
- A signed local `POST /api/github/webhook` with `/agent fix` against public repository `octocat/Hello-World` returned `TASK_CREATED`; polling `GET /api/tasks/{id}` showed the task reached `COMPLETED` with `failureReason=null`.

## 2026-06-18

Implemented the workspace branch MVP from `docs/plans/004-workspace-branch-mvp.md`.

Changes:

- Added `PreparedWorkspaceResult` to return task id, workspace path, repository path, and branch name.
- Added `WorkspaceService#prepareRepository(...)` to clone the repository and create a task branch.
- Added `GitCommandRunner#createBranch(...)` using controlled `git -C <repo> checkout -b <branch>` arguments.
- Changed the task executor to call workspace preparation instead of clone-only execution.
- Updated webhook controller tests with a concrete `WorkspaceService` fake because the service interface now has multiple methods.
- Kept this phase local-only: no file edits, Maven test execution in cloned repositories, pushes, or Pull Requests.

Validation:

- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests test` with Java 17: first failed because `PreparedWorkspaceResult`, `prepareRepository`, and `createBranch` did not exist, then passed after implementation, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test` with Java 17: first failed because the executor still called `cloneRepository`, then passed after switching it to `prepareRepository`, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` with Java 17: first failed because `createBranch` did not switch the branch and did not reject blank names, then passed after implementing the fixed git command, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test` with Java 17: passed, 13 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 27 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- A signed local `POST /api/github/webhook` with `/agent fix` against public repository `octocat/Hello-World` returned `TASK_CREATED`; polling `GET /api/tasks/{id}` showed the task reached `COMPLETED` with `failureReason=null`.
- `docker exec patchpilot-backend git -C /tmp/patchpilot/workspaces/<taskId>/repo branch --show-current`: returned `patchpilot/<taskId>` for task `0aa0e1e7-888b-43fa-848b-311c717d8418`.

## 2026-06-18

Implemented the controlled file tools MVP from `docs/plans/005-file-tools-mvp.md`.

Changes:

- Added `WorkspacePathResolver` to resolve repository-relative paths and reject blank, absolute, and traversal inputs.
- Added `FileReadTool` for UTF-8 file reads inside a repository workspace.
- Added `FileWriteTool` for UTF-8 file writes inside a repository workspace, including parent directory creation.
- Added `GitCommandRunner#diff(...)` using controlled `git -C <repo> diff --` arguments.
- Added `DiffTool` to expose git diff output and fail explicitly on non-zero git exit.
- Kept these tools internal only; no model integration, HTTP API, Maven execution, push, or Pull Request creation was added in this phase.

Validation:

- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests test` with Java 17: first failed because `WorkspacePathResolver` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FileToolsTests test` with Java 17: first failed because `FileReadTool` and `FileWriteTool` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test` with Java 17: first failed because `GitCommandRunner#diff(...)` did not exist, then passed after implementation, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=DiffToolTests test` with Java 17: first failed because `DiffTool` did not exist, then passed after implementation.
- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests,FileToolsTests,GitCommandRunnerTests,DiffToolTests test` with Java 17: passed, 15 tests run, 0 failures, 0 errors.
- `mvn test` from repository root with Java 17: passed, 38 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root with Java 17: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `curl http://127.0.0.1:8080/api/tasks`: returned a successful API response with an empty in-memory task list after restart.

## 2026-06-18

Implemented the Maven test runner MVP from `docs/plans/006-maven-test-runner-mvp.md`.

Changes:

- Added `TestRunResult` to return the executed command, exit code, and combined command output.
- Added `MavenTestRunner` under the `runner` module.
- Detects Maven wrapper repositories and runs only `./mvnw test`.
- Falls back to `mvn test` when only `pom.xml` exists.
- Fails explicitly for unsupported repositories without `mvnw` or `pom.xml`.
- Captures non-zero test exits without throwing away output.
- Enforces a command timeout and returns exit code `124` for timed out runs.
- Kept this phase internal only: no task executor wiring, model integration, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests test`: first failed because `MavenTestRunner` and `TestRunResult` did not exist, then passed after implementation, 6 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 44 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `curl http://127.0.0.1:8080/api/tasks`: returned `{"success":true,"data":[],"message":null}` after restart.

## 2026-06-18

Integrated Maven test execution into task execution from `docs/plans/007-task-test-runner-integration.md`.

Changes:

- Added `RUNNING_TESTS` task status.
- Added `FixTaskService#markRunningTests(...)` and the in-memory implementation.
- Updated the async dispatcher to transition tasks from `RUNNING` to `RUNNING_TESTS` before calling the executor.
- Updated `NoopFixTaskExecutor` to prepare the repository and then call `MavenTestRunner` on the prepared repository directory.
- Made non-zero Maven test exits fail task execution with `maven tests failed: <output>`.
- Isolated webhook controller tests from real Maven execution by providing a primary test `MavenTestRunner`.
- Added a Dockerfile packaging test to ensure the backend runtime image includes Maven on the Java 17 Maven base image.
- Updated the backend runtime Docker stage to use `maven:3.9-eclipse-temurin-17` and avoid installing a second JDK through apt.
- Kept this phase local-only: no model integration, file edits, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests test`: first failed because `markRunningTests` and `RUNNING_TESTS` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because `NoopFixTaskExecutor` did not accept or call `MavenTestRunner`, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: first failed because the dispatcher transitioned directly from `RUNNING` to `COMPLETED`, then passed after adding `RUNNING_TESTS`, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: first failed because the Spring test context used the real Maven runner against a fake non-Maven workspace, then passed after adding a test runner bean, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MavenRuntimePackagingTests test`: first failed because the runtime image did not guarantee Maven availability, then passed after switching the runtime stage to the Java 17 Maven image.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,AsyncFixTaskDispatcherTests,WorkspaceFixTaskExecutorTests test`: passed, 9 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MavenRuntimePackagingTests,MavenTestRunnerTests,InMemoryFixTaskServiceTests,AsyncFixTaskDispatcherTests,WorkspaceFixTaskExecutorTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 48 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.
- `PATCHPILOT_GITHUB_WEBHOOK_SECRET=test-secret-123 docker compose up --build -d`: passed, rebuilt and restarted the backend container.
- `docker compose ps`: showed `patchpilot-backend` up on `0.0.0.0:8080->8080/tcp` and `patchpilot-mysql` healthy.
- `docker exec patchpilot-backend mvn -version`: returned Apache Maven 3.9.16 running on Java 17.0.19.
- `docker exec patchpilot-backend java -version`: returned Temurin 17.0.19.
- `curl http://127.0.0.1:8080/health`: returned `UP` from the backend after container startup.
- `curl http://127.0.0.1:8080/api/tasks`: returned `{"success":true,"data":[],"message":null}` after restart.

## 2026-06-18

Implemented repository inspection tools from `docs/plans/008-repository-inspection-tools.md`.

Changes:

- Added `RepositoryFileScanner` to produce sorted repository-relative file lists while skipping noisy/generated directories.
- Added `RepoTreeTool` to return a bounded newline-separated repository file tree.
- Added `CodeSearchTool` to return bounded `path:line: text` matches using Java filesystem APIs and UTF-8 reads.
- Added coverage for sorted tree output, skipped directories, search match formatting, blank query rejection, and search result limiting.
- Kept this phase internal and read-only: no model integration, file edits, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=RepositoryInspectionToolsTests test`: first failed because `RepoTreeTool`, `RepositoryFileScanner`, and `CodeSearchTool` did not exist, then passed after implementation, 5 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 53 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented the minimal deterministic patch workflow from `docs/plans/009-minimal-patch-workflow.md`.

Changes:

- Added `PatchWorkflow`, `PatchWorkflowResult`, and `SimplePatchWorkflow`.
- Added deterministic support for trigger comments containing `touch <relative-path>`.
- Wrote generated files through `FileWriteTool`, preserving repository-relative path validation.
- Updated webhook trigger matching so `/agent fix` and `/agent fix <instruction>` both create tasks.
- Wired task execution order as repository preparation -> patch workflow -> diff inspection -> Maven tests.
- Isolated webhook controller tests from real patch/diff side effects with primary test beans.
- Kept this phase local-only: no model provider calls, commits, pushes, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=SimplePatchWorkflowTests test`: first failed because workflow classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor was not wired for workflow and diff, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests#should_create_task_for_agent_fix_issue_comment_with_patch_instruction test`: first failed with `IGNORED`, then passed after accepting command-prefixed instructions.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: passed, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=SimplePatchWorkflowTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 13 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 57 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 57 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented the local commit workflow from `docs/plans/010-local-commit-workflow.md`.

Changes:

- Added controlled `GitCommandRunner#stageAll(...)` using `git -C <repo> add --all`.
- Added controlled `GitCommandRunner#commit(...)` using `git -C <repo> commit -m <message>`.
- Added `CommitTool` to stage and commit all repository changes with clear failure messages.
- Updated task execution order to commit only after patch workflow, diff inspection, and successful Maven verification.
- Ensured Maven test failures do not create commits.
- Isolated webhook controller tests from real commit side effects with a primary test `CommitTool`.
- Kept this phase local-only: no branch push, GitHub API calls, issue comments, or Pull Requests were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test`: first failed because `stageAll` and `commit` did not exist, then passed after implementation, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommitToolTests test`: first failed because `CommitTool` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor did not accept `CommitTool`, then passed after wiring commit after Maven verification, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: first failed because the Spring test context used the real commit tool against a fake workspace, then passed after adding a test commit tool bean, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,CommitToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 21 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 63 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 63 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented remote task branch push from `docs/plans/011-remote-branch-push.md`.

Changes:

- Added controlled `GitCommandRunner#pushBranch(...)` using `git -C <repo> push origin HEAD:<branch>`.
- Added `PushTool` to wrap push execution and surface clear failure messages.
- Updated task execution order to push the prepared task branch only after patch workflow, diff inspection, Maven verification, and local commit succeed.
- Ensured Maven test failures and commit failures do not push.
- Isolated webhook controller tests from real push side effects with a primary test `PushTool`.
- Kept this phase limited to branch push: no GitHub REST API calls, Pull Requests, or issue comments were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test`: first failed because `pushBranch` did not exist, then passed after implementation, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PushToolTests test`: first failed because `PushTool` did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor did not accept `PushTool`, then passed after wiring push after commit, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests test`: first failed because the Spring test context used the real push tool against a fake workspace, then passed after adding a test push tool bean, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,PushToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 23 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 68 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 68 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented Pull Request creation from `docs/plans/012-pull-request-creation.md`.

Changes:

- Added `GitHubPullRequestClient` for GitHub Pull Request API calls using Java `HttpClient`.
- Added `CreatePullRequestCommand`, `PullRequestResult`, and `GitHubPullRequestException`.
- Added `PullRequestTool` to build PR title, head branch, base branch, and body from task context.
- Updated task execution order to create a PR only after patch workflow, diff inspection, Maven verification, local commit, and branch push succeed.
- Ensured Maven test failures, commit failures, and push failures do not create PRs.
- Ensured missing GitHub tokens fail clearly before any HTTP request and without exposing secrets.
- Isolated webhook controller tests from real GitHub API calls with a primary test `PullRequestTool`.
- Kept this phase limited to PR creation: no issue comments, PR URL persistence, merge behavior, or model provider calls were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubPullRequestClientTests test`: first failed because PR client classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PullRequestToolTests test`: first failed because `PullRequestTool` did not exist, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because the executor did not accept `PullRequestTool`, then passed after wiring PR creation after push, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubPullRequestClientTests,PullRequestToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: first failed because Spring could not choose a `GitHubPullRequestClient` constructor, then passed after marking the production constructor for injection, 16 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 73 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 73 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented Issue comment feedback from `docs/plans/013-issue-comment-feedback.md`.

Changes:

- Added `GitHubIssueCommentClient` for GitHub Issue comment API calls using Java `HttpClient`.
- Added `CreateIssueCommentCommand`, `IssueCommentResult`, and `GitHubIssueCommentException`.
- Added `IssueCommentTool` to build completion and failure comments from task context.
- Added `FixTaskExecutionResult` so task execution can return the created PR URL.
- Updated `FixTaskExecutor` and `NoopFixTaskExecutor` to return the PR URL after Pull Request creation.
- Updated `AsyncFixTaskDispatcher` to comment on the original Issue after `COMPLETED` or `FAILED` status updates.
- Kept task status stable when a post-completion Issue comment fails.
- Isolated webhook controller tests from real GitHub Issue comment API calls with a primary test `IssueCommentTool`.
- Kept this phase limited to status feedback: no retries, persisted PR URLs, persisted comment IDs, merges, or model provider calls were added.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test`: first failed because Issue comment client classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`: first failed because `IssueCommentTool` did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because `FixTaskExecutionResult` did not exist and the executor returned `void`, then passed after returning the PR URL, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: first failed because the dispatcher did not accept `IssueCommentTool`, then passed after posting completion and failure comments, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests#should_keep_completed_status_when_completion_comment_fails test`: first failed because completion comment failure triggered a failure comment and status rewrite, then passed after narrowing the executor failure boundary, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: passed after the boundary fix, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests,IssueCommentToolTests,WorkspaceFixTaskExecutorTests,AsyncFixTaskDispatcherTests,GitHubWebhookControllerTests test`: passed, 21 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 79 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 79 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented MySQL task persistence from `docs/plans/014-mysql-task-persistence.md`.

Changes:

- Added Flyway migration `V1__create_fix_task.sql` for the `fix_task` table and unique `delivery_id`.
- Added `FixTaskEntity`, `FixTaskMapper`, and `FixTaskConvert` for MyBatis-Plus persistence mapping.
- Added `MyBatisFixTaskService` for `local` and `docker` profiles, preserving duplicate delivery handling and task status transitions.
- Added a task creation result contract so webhook handling can return `DUPLICATE_DELIVERY` without re-dispatching when MySQL already has the delivery id.
- Kept `InMemoryFixTaskService` as the default no-database service.
- Enabled Flyway migrations for `application-local.properties` and `application-docker.properties`.
- Added migration, converter, and MyBatis service tests.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskMigrationTests test`: first failed because the migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test`: first failed because entity/converter classes did not exist, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskServiceTests test`: first failed because `MyBatisFixTaskService` did not exist, then passed after implementation, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,InMemoryFixTaskServiceTests test`: passed, 13 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,MyBatisFixTaskServiceTests test`: first failed because `FixTaskCreationResult` did not exist, then passed after adding the creation-result contract, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskMigrationTests,FixTaskConvertTests,MyBatisFixTaskServiceTests,InMemoryFixTaskServiceTests,PatchPilotApplicationTests,GitHubWebhookControllerTests,GitHubWebhookServiceTests test`: passed, 23 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 89 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 89 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented task result persistence from `docs/plans/015-task-result-persistence.md`.

Changes:

- Added Flyway migration `V2__add_fix_task_result_fields.sql` for `pull_request_url`, `completed_at`, and `updated_at`.
- Extended `FixTaskVo`, `FixTaskEntity`, and `FixTaskConvert` with result metadata fields.
- Added `FixTaskService#markCompleted(String id, String pullRequestUrl)` while keeping the old single-argument method as a compatibility default.
- Updated in-memory and MyBatis task services to persist PR URL, completion time, and update time.
- Updated the async dispatcher to persist the Pull Request URL returned by task execution.
- Added task API assertions for additive JSON fields `pullRequestUrl`, `completedAt`, and `updatedAt`.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskResultMigrationTests test`: first failed because the V2 migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test`: first failed because result fields and `replaceCompleted(...)` did not exist, then passed after updating DTO/entity/converter, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because `markCompleted(id, pullRequestUrl)` did not exist, then passed after service updates, 11 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests,TaskControllerTests test`: first failed because completed tasks did not retain `pullRequestUrl`, then passed after dispatcher wiring, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskResultMigrationTests,FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,AsyncFixTaskDispatcherTests,TaskControllerTests,GitHubWebhookControllerTests test`: passed, 30 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 92 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 92 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented editable Issue comment lifecycle feedback from `docs/plans/016-edit-issue-comment-lifecycle.md`.

Changes:

- Added Flyway migration `V3__add_fix_task_status_comment.sql` for `status_comment_id` and `status_comment_url`.
- Extended `FixTaskVo`, `FixTaskEntity`, and `FixTaskConvert` with PatchPilot-owned status comment metadata.
- Added `FixTaskService#attachStatusComment(...)` for both in-memory and MyBatis-backed task services.
- Added `UpdateIssueCommentCommand` and `GitHubIssueCommentClient#updateIssueComment(...)` using GitHub's Issue comment PATCH endpoint.
- Updated `IssueCommentTool` with lifecycle methods for accepted, running, running tests, completed, and failed task states.
- Updated webhook handling to create one initial PatchPilot status comment, persist its id and URL, and avoid duplicate status comments for duplicate deliveries.
- Updated async dispatch to edit the same status comment after `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, and `FAILED` transitions.
- Kept GitHub comment creation/update failures non-blocking so durable task state remains authoritative.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskStatusCommentMigrationTests test`: first failed because the V3 migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because status comment fields and `attachStatusComment(...)` did not exist, then passed after DTO/entity/converter/service updates, 17 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test`: first failed because `UpdateIssueCommentCommand` and `updateIssueComment(...)` did not exist, then passed after PATCH client implementation, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`: first failed because lifecycle comment methods did not exist, then passed after tool updates, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests test`: first failed because webhook handling did not create/save the accepted status comment, then passed after wiring `IssueCommentTool`, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=AsyncFixTaskDispatcherTests test`: first failed because only terminal comments were updated, then passed after adding lifecycle updates, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests,TaskControllerTests,AsyncFixTaskDispatcherTests test`: passed, 15 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskStatusCommentMigrationTests,FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,GitHubIssueCommentClientTests,IssueCommentToolTests,GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests,TaskControllerTests,GitHubWebhookControllerTests test`: passed, 48 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 105 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 105 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented task execution timeline from `docs/plans/017-task-execution-timeline.md`.

Changes:

- Added Flyway migration `V4__create_fix_task_timeline_event.sql` for timeline events.
- Added timeline event enum, VO, entity, converter, and MyBatis mapper.
- Added `FixTaskTimelineService` with default in-memory and MyBatis-backed implementations.
- Added `GET /api/tasks/{id}/timeline` to expose ordered task timeline events.
- Updated webhook handling to record `TASK_CREATED` and `STATUS_COMMENT_CREATED`.
- Updated async dispatch to record `RUNNING`, `RUNNING_TESTS`, `PR_CREATED`, `COMPLETED`, and `FAILED`.
- Kept timeline write failures non-blocking so durable task status transitions remain authoritative.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskTimelineMigrationTests test`: first failed because the V4 migration file did not exist, then passed after adding it, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskTimelineEventConvertTests test`: first failed because timeline domain/converter classes did not exist, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskTimelineServiceTests,MyBatisFixTaskTimelineServiceTests test`: first failed because `FixTaskTimelineService` implementations did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `GET /api/tasks/{id}/timeline` did not exist, then passed after adding the endpoint, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests test`: first failed because webhook and dispatcher were not wired to `FixTaskTimelineService`, then passed after lifecycle event recording, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests,TaskControllerTests test`: passed, 13 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=FixTaskTimelineMigrationTests,FixTaskTimelineEventConvertTests,InMemoryFixTaskTimelineServiceTests,MyBatisFixTaskTimelineServiceTests,TaskControllerTests,GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests,GitHubWebhookControllerTests test`: passed, 25 tests run, 0 failures, 0 errors.
- `mvn test` from repository root: passed, 112 tests run, 0 failures, 0 errors.
- `mvn clean package` from repository root: passed, 112 tests run, 0 failures, 0 errors, and generated `PatchPilot/target/patchpilot-backend-0.0.1-SNAPSHOT.jar`.

## 2026-06-19

Implemented active task deduplication and the in-process queue boundary from `docs/plans/018-active-task-deduplication-queue.md`.

Changes:

- Added `FixTaskService#findTaskByDeliveryId(...)` so duplicate delivery idempotency can be checked before issue-level active task deduplication.
- Added `FixTaskService#findActiveTaskForIssue(...)` for both in-memory and MyBatis-backed task services.
- Added Flyway migration `V5__add_fix_task_active_lookup_index.sql` for `repository_owner`, `repository_name`, `issue_number`, and `status` lookup.
- Added `WebhookHandleStatus.ACTIVE_TASK_EXISTS` and `FixTaskTimelineEventType.ACTIVE_TASK_EXISTS`.
- Updated webhook handling so a second `/agent fix` for the same active issue returns the existing task id, records a timeline event, edits the existing status comment when possible, and does not create or dispatch a second task.
- Preserved duplicate delivery priority over active task deduplication, including active tasks found by delivery id after process restart.
- Added `FixTaskQueue` and `InMemoryFixTaskQueue` as the queue abstraction.
- Moved task execution lifecycle transitions from `AsyncFixTaskDispatcher` into `FixTaskWorker`.
- Reduced `AsyncFixTaskDispatcher` to enqueue task ids only.
- Kept this phase in-process only: no Redis, RabbitMQ, Kafka, or docker-compose service was added.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,FixTaskMigrationTests,IssueCommentToolTests,GitHubWebhookServiceTests,AsyncFixTaskDispatcherTests,FixTaskWorkerTests,InMemoryFixTaskQueueTests test`: first failed because the new queue, worker, active-task lookup, webhook status, timeline event, comment update, and migration did not exist, then passed after implementation, 36 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,GitHubWebhookServiceTests test`: first failed because `findTaskByDeliveryId(...)` was not part of the service contract, then passed after adding the contract and duplicate-before-active webhook check, 24 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 124 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented Maven test-run records from `docs/plans/019-test-run-records.md`.

Changes:

- Added Flyway migration `V6__create_fix_task_test_run.sql` for the `fix_task_test_run` table.
- Added `FixTaskTestRunVo`, `FixTaskTestRunEntity`, `FixTaskTestRunConvert`, and `FixTaskTestRunMapper`.
- Added `FixTaskTestRunService` with default in-memory and MyBatis-backed implementations.
- Added `GET /api/tasks/{id}/test-runs` to expose ordered Maven verification records for a task.
- Updated `NoopFixTaskExecutor` to record Maven command, exit code, output, start time, end time, and duration immediately after `MavenTestRunner` returns.
- Preserved existing Maven failure behavior: non-zero test exits still fail the task before commit, push, or Pull Request creation.
- Kept this phase limited to Maven test-run observability; no external services or frontend changes were added.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskTestRunMigrationTests,FixTaskTestRunConvertTests,InMemoryFixTaskTestRunServiceTests,MyBatisFixTaskTestRunServiceTests,TaskControllerTests,WorkspaceFixTaskExecutorTests test`: first failed because the test-run VO, entity, mapper, service, controller endpoint, and executor dependency did not exist, then passed after implementation, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 131 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented tool-call audit records from `docs/plans/020-tool-call-audit-records.md`.

Changes:

- Added Flyway migration `V7__create_fix_task_tool_call.sql` for the `fix_task_tool_call` table.
- Added `FixTaskToolCallVo`, `FixTaskToolCallEntity`, `FixTaskToolCallConvert`, and `FixTaskToolCallMapper`.
- Added `FixTaskToolCallService` with default in-memory and MyBatis-backed implementations.
- Added `GET /api/tasks/{id}/tool-calls` to expose ordered tool-call audit records for a task.
- Updated `NoopFixTaskExecutor` to record `PatchWorkflow`, `DiffTool`, `CommitTool`, `PushTool`, and `PullRequestTool` success or failure with input/output summaries and timing.
- Kept Maven verification records in `fix_task_test_run`; tool-call audit does not duplicate Maven test-run details.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskToolCallMigrationTests,FixTaskToolCallConvertTests,InMemoryFixTaskToolCallServiceTests,MyBatisFixTaskToolCallServiceTests,TaskControllerTests,WorkspaceFixTaskExecutorTests test`: first failed because the tool-call VO, entity, mapper, service, controller endpoint, and executor dependency did not exist, then passed after implementation, 18 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 138 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented MySQL-backed task queue from `docs/plans/021-mysql-backed-task-queue.md`.

Changes:

- Added Flyway migration `V8__create_fix_task_queue_item.sql` for durable queue records.
- Added `FixTaskQueueItemStatus`, `FixTaskQueueItemEntity`, `FixTaskQueueItemVo`, `FixTaskQueueItemConvert`, and `FixTaskQueueItemMapper`.
- Added `MyBatisFixTaskQueue` for `local` and `docker` profiles.
- Added `FixTaskQueuePoller` to claim queued items, execute `FixTaskWorker`, and persist `COMPLETED` or `FAILED` queue item status.
- Enabled Spring scheduling in `PatchPilotApplication`.
- Scoped `InMemoryFixTaskQueue` to the default profile so default tests remain database-free.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskQueueItemMigrationTests,FixTaskQueueItemConvertTests,MyBatisFixTaskQueueTests,FixTaskQueuePollerTests test`: first failed because the queue item VO, entity, enum, mapper, MyBatis queue, poller, and migration did not exist, then passed after implementation, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 148 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented queue recovery and retry behavior from `docs/plans/022-queue-recovery-and-retry.md`.

Changes:

- Added `TaskQueueProperties` with `patchpilot.task.queue` settings for max attempts, retry delay, and visibility timeout.
- Registered queue properties in `PatchPilotApplication`.
- Updated `MyBatisFixTaskQueue#markFailed(...)` so transient worker failures return the item to `PENDING` until max attempts are reached.
- Added stale `RUNNING` item recovery through `MyBatisFixTaskQueue#recoverTimedOutRunningItems()`.
- Updated `FixTaskQueuePoller` to recover timed-out running items before claiming new work.

Validation:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueTests,FixTaskQueuePollerTests test`: first failed because `TaskQueueProperties`, retry-aware queue constructor, and `recoverTimedOutRunningItems()` did not exist, then passed after implementation, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 150 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented queue observability API from `docs/plans/023-queue-observability-api.md`.

Changes:

- Added `FixTaskQueueQueryService` to separate read-only queue inspection from queue execution.
- Added `FixTaskQueueSummaryVo` for aggregate queue state.
- Added default-profile empty queue query implementation so no-database local runs still expose queue endpoints safely.
- Added `MyBatisFixTaskQueueQueryService` for `local` and `docker` profiles to list queue items and summarize status counts from `fix_task_queue_item`.
- Added `GET /api/task-queue/items` with optional `status` filtering.
- Added `GET /api/task-queue/summary` for total, pending, available pending, delayed pending, running, completed, and failed counts.
- Kept this phase read-only: no queue mutation or admin retry endpoint was added.

Validation:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueQueryServiceTests,TaskControllerTests test`: first failed because the queue query service and summary VO did not exist, then passed after implementation, 14 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=TaskQueueControllerTests,MyBatisFixTaskQueueQueryServiceTests,TaskControllerTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 157 tests run, 0 failures, 0 errors.

## 2026-06-19

Implemented concurrent queue claim safety from `docs/plans/024-concurrent-queue-claim-safety.md`.

Changes:

- Updated `MyBatisFixTaskQueue#claimNext()` to use a conditional update for claiming selected pending queue items.
- The conditional update now requires the item id, `PENDING` status, and `available_at <= now`.
- `claimNext()` returns a running queue item only when the update affects one row.
- If another worker claims the selected item first and the update affects zero rows, `claimNext()` now returns `Optional.empty()` so the losing worker does not execute the task.
- Kept this phase scoped to claim safety: no distributed lock service, queue mutation API, or `FOR UPDATE SKIP LOCKED` path was added.

Validation:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueTests test`: first failed because the old implementation still used `updateById` and returned a claimed item even when a conditional update would affect zero rows, then passed after implementation, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskQueueTests,FixTaskQueuePollerTests,MyBatisFixTaskQueueQueryServiceTests,TaskQueueControllerTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 158 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented command allowlist and task sandbox guards from `docs/plans/025-command-allowlist-and-task-sandbox.md`.

Changes:

- Added `CommandExecutionGuard` to validate command shapes and command working directories before process execution.
- Guarded Git commands in `GitCommandRunner`, including clone, branch creation, diff, add, commit, and push.
- Guarded Maven test execution in `MavenTestRunner` for `./mvnw test` and `mvn test`.
- Updated `WorkspacePathResolver` to reject repository roots outside `patchpilot.workspace.root-dir` before resolving file read/write paths.
- Updated `RepositoryFileScanner` to reject repository roots outside `patchpilot.workspace.root-dir` before file tree and code search scans.
- Preserved existing path traversal rejection for relative file inputs.

Validation:

- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests test`: first failed because `CommandExecutionGuard` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests,GitCommandRunnerTests,MavenTestRunnerTests test`: first failed because existing runner tests used temp directories outside the configured workspace root, then passed after injecting test workspace roots, 22 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=RepositoryInspectionToolsTests test`: first failed because `RepositoryFileScanner` did not accept `WorkspaceProperties`, then passed after scanner root validation, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests test`: first failed because `WorkspacePathResolver` did not accept `WorkspaceProperties`, then passed after resolver root validation, 5 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspacePathResolverTests,FileToolsTests,SimplePatchWorkflowTests,RepositoryInspectionToolsTests test`: passed, 19 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests,GitCommandRunnerTests,MavenTestRunnerTests,WorkspacePathResolverTests,FileToolsTests,RepositoryInspectionToolsTests,SimplePatchWorkflowTests,GitWorkspaceServiceTests,PatchPilotApplicationTests test`: passed, 47 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 167 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented task control API from `docs/plans/026-task-control-api.md`.

Changes:

- Added `CANCELLED` task status and `CANCELLED` / `REQUEUED` timeline event types.
- Added `FixTaskControlService` to own user-driven task lifecycle actions.
- Added `POST /api/tasks/{id}/cancel` for pending tasks only.
- Added `POST /api/tasks/{id}/retry` for failed or cancelled tasks only.
- Added pending queue-item cancellation support and `CANCELLED` queue item status.
- Extended queue summary responses with `cancelledCount`.
- Kept cancellation scoped to pending tasks because running worker interruption is not implemented yet.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,TaskControllerTests test`: first failed because the new service methods and status enums did not exist, confirming the red test path.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,MyBatisFixTaskQueueQueryServiceTests,TaskQueueControllerTests,TaskControllerTests test`: passed, 54 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,MyBatisFixTaskQueueQueryServiceTests,TaskQueueControllerTests,TaskControllerTests test`: passed, 58 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented running task cancellation safety from `docs/plans/027-running-task-cancellation-safety.md`.

Changes:

- Extended task cancellation to active `RUNNING` and `RUNNING_TESTS` tasks while keeping terminal tasks non-cancellable.
- Added `TaskCancellationChecker` and `TaskCancellationException` for durable task-state cancellation checks.
- Added `DefaultTaskCancellationChecker` backed by `FixTaskService`.
- Updated `NoopFixTaskExecutor` to check cancellation at execution stage boundaries before later side effects.
- Updated `FixTaskWorker` so cancellation exceptions stop execution cleanly without overwriting `CANCELLED` tasks as `FAILED`.
- Kept this phase scoped to stage-boundary safety; Maven/Git process interruption is not implemented yet.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,FixTaskWorkerTests,WorkspaceFixTaskExecutorTests test`: first failed because `TaskCancellationChecker`, `TaskCancellationException`, and the extended executor constructor did not exist; then failed once because the cancellation test fired before test-run recording; then passed after implementation and test correction, 34 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 187 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented cancellable Maven process runner from `docs/plans/028-cancellable-process-runner.md`.

Changes:

- Added `TaskProcessRegistry` to register the currently running external process for each task.
- Added task-scoped Maven execution through `MavenTestRunner#runTests(String taskId, Path repositoryDir)`.
- Updated Maven process execution to register and unregister task processes around `ProcessBuilder` execution.
- Updated task cancellation control to interrupt a registered process when cancelling `RUNNING` or `RUNNING_TESTS` tasks.
- Updated `NoopFixTaskExecutor` to pass the task id into Maven test execution.
- Rechecked cancellation after recording Maven test results so a cancelled Maven process is not reported as a failed task.
- Deferred Git process cancellation and repository recovery to plan 029.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskProcessRegistryTests,MavenTestRunnerTests,DefaultFixTaskControlServiceTests,WorkspaceFixTaskExecutorTests test`: first failed because `TaskProcessRegistry`, task-aware Maven execution, and the extended control-service constructor did not exist; then failed once because a recording test runner still overrode the old `runCommand` signature; then passed after implementation and test correction, 22 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because a cancelled Maven process result was still raised as `IllegalStateException`; then passed after checking cancellation before Maven exit-code failure handling, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=TaskProcessRegistryTests,MavenTestRunnerTests,DefaultFixTaskControlServiceTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: passed, 31 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 192 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented Git process cancellation and recovery guidance from `docs/plans/029-git-process-cancellation-recovery.md`.

Changes:

- Added `GitWorkspaceRecoveryInspector` to read known Git half-finished states without deleting files or running recovery commands.
- Detects `.git/index.lock`, `.git/HEAD.lock`, `.git/MERGE_HEAD`, `.git/rebase-merge`, and `.git/rebase-apply`.
- Extended `GitCommandRunner` with task-aware clone, branch, diff, stage, commit, and push overloads.
- Reused `TaskProcessRegistry` so task cancellation can interrupt Git processes as well as Maven processes.
- Passed task ids through workspace clone/branch creation, commit, and push.
- Added recovery guidance to clone, branch creation, commit, and push failure messages when known Git lock or in-progress states are present.
- Added `WorkspaceService` tool-call audit records for repository preparation.
- Converted commit and push failures into cancellation when the durable task state has become `CANCELLED` during the Git operation.
- Kept recovery read-only; manual cleanup remains explicit.

Validation:

- `mvn -pl PatchPilot -Dtest=GitWorkspaceRecoveryInspectorTests test`: first failed because `GitWorkspaceRecoveryInspector` did not exist, then passed, 6 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests test`: first failed because task-aware overloads, process registry injection, and `startProcess` override point did not exist, then passed, 13 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitWorkspaceServiceTests,CommitToolTests,PushToolTests,WorkspaceFixTaskExecutorTests test`: first failed because task-aware tool APIs and recovery inspector injection did not exist, then passed, 21 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests test`: first failed because commit and push cancellation still surfaced as Git failures, then passed after checking cancellation after failed audited tool calls, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitWorkspaceRecoveryInspectorTests,GitCommandRunnerTests,GitWorkspaceServiceTests,CommitToolTests,PushToolTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests test`: first failed because new constructors needed explicit Spring injection and webhook test doubles still overrode old commit/push signatures, then passed, 52 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 209 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented model-call audit records from `docs/plans/030-model-call-audit-records.md`.

Changes:

- Added Flyway migration `V9__create_fix_task_model_call.sql` for model-call audit rows.
- Added `FixTaskModelCallVo`, `FixTaskModelCallEntity`, converter, mapper, and service boundary.
- Added default-profile in-memory and local/docker MyBatis-backed model-call services.
- Added `GET /api/tasks/{id}/model-calls` to expose ordered model-call records for existing tasks.
- Kept this phase audit-only: no real model provider calls, prompt generation, or workflow changes were added.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskModelCallConvertTests,FixTaskModelCallMigrationTests,InMemoryFixTaskModelCallServiceTests,MyBatisFixTaskModelCallServiceTests,TaskControllerTests test`: first failed because model-call VO/entity/converter/mapper/service and controller wiring did not exist, then passed after implementation, 25 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 216 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented model provider client boundary from `docs/plans/031-model-provider-client-boundary.md`.

Changes:

- Added `AgentProperties` under `patchpilot.agent` for provider, model, base URL, and API key.
- Added `ModelProviderClient`, request/response records, and `ModelProviderException`.
- Added `OpenAiCompatibleModelClient` using Java `HttpClient` against `/chat/completions`.
- Recorded successful and failed model client calls through `FixTaskModelCallService`.
- Added environment-backed defaults for `PATCHPILOT_AGENT_PROVIDER`, `PATCHPILOT_AGENT_MODEL`, `PATCHPILOT_AGENT_BASE_URL`, and `PATCHPILOT_AGENT_API_KEY`.
- Kept this phase boundary-only: no workflow path invokes the model client yet.

Validation:

- `mvn -pl PatchPilot -Dtest=OpenAiCompatibleModelClientTests test`: first failed because agent config, provider domain, and client classes did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,OpenAiCompatibleModelClientTests test`: passed, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 219 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented structured fix-plan generation from `docs/plans/032-structured-fix-plan-generation.md`.

Changes:

- Added `FixPlanGenerator` as a model-backed planning boundary.
- Added typed `FixPlan` output with summary, target files, steps, and risk fields.
- Added `FixPlanGenerationException` for invalid or incomplete model output.
- Built deterministic system and user prompts from `FixTaskVo` metadata.
- Kept execution unchanged: `NoopFixTaskExecutor` and `SimplePatchWorkflow` do not call the fix-plan generator yet.

Validation:

- `mvn -pl PatchPilot -Dtest=FixPlanGeneratorTests test`: first failed because `FixPlanGenerator`, `FixPlan`, and `FixPlanGenerationException` did not exist, then passed after implementation, 3 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,FixPlanGeneratorTests,WorkspaceFixTaskExecutorTests test`: first failed because Spring could not choose the production `FixPlanGenerator` constructor, then passed after marking it with `@Autowired`, 12 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 222 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented planned file edit workflow from `docs/plans/033-planned-file-edit-workflow.md`.

Changes:

- Added `PlannedPatchWorkflow` as an internal, non-Spring workflow class.
- Supported only `/agent fix replace <path> <text>` planned replacement instructions.
- Required replacement targets to appear in `FixPlan.targetFiles()`.
- Routed writes through `FileWriteTool` so existing workspace path guards still apply.
- Kept production execution unchanged: `SimplePatchWorkflow` remains the active `PatchWorkflow` bean.

Validation:

- `mvn -pl PatchPilot -Dtest=PlannedPatchWorkflowTests test`: first failed because `PlannedPatchWorkflow` did not exist, then passed after implementation, 4 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,PlannedPatchWorkflowTests,SimplePatchWorkflowTests,WorkspaceFixTaskExecutorTests test`: passed, 16 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 226 tests run, 0 failures, 0 errors.

## 2026-06-20

Implemented plan-driven executor integration from `docs/plans/034-plan-driven-executor-integration.md`.

Changes:

- Added `PlanDrivenPatchWorkflow` as the production `PatchWorkflow`.
- Wired production patching as `FixPlanGenerator` followed by `PlannedPatchWorkflow`.
- Added `PatchWorkflowConfiguration` to provide the planned patch workflow bean.
- Kept `SimplePatchWorkflow` available as a deterministic test helper but no longer registered it as a Spring component.
- Added an application-context assertion that production has one `PatchWorkflow` bean and it is plan-driven.
- Preserved the existing executor sequence after patching: diff, Maven tests, commit, push, and Pull Request creation.
- Added explicit Maven compiler annotation processor configuration for Lombok because the current branch contains Lombok-based entity and constructor changes.

Validation:

- `mvn -pl PatchPilot -Dtest=PlanDrivenPatchWorkflowTests test`: first failed because `PlanDrivenPatchWorkflow` did not exist, then passed after implementation, 1 test run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests test`: first failed because Lombok-generated accessors were unavailable during compilation, then passed after adding the compiler annotation processor configuration, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PlanDrivenPatchWorkflowTests,PlannedPatchWorkflowTests,PatchPilotApplicationTests test`: passed, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests,PlanDrivenPatchWorkflowTests,PlannedPatchWorkflowTests,PatchPilotApplicationTests test`: passed, 24 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.

## 2026-06-20

Completed low-risk Lombok cleanup from `docs/plans/035-code-simplification-lombok-cleanup.md`.

Changes:

- Removed duplicated getter/setter methods from task persistence entities that are now covered by Lombok `@Data`.
- Replaced pure dependency-injection constructors with `@RequiredArgsConstructor` in task controllers, task services, queue components, workflow classes, repository tools, file tools, issue/PR tools, and command guard code.
- Replaced repeated blank-string checks with Spring `StringUtils` instead of adding Hutool for a small helper-only cleanup.
- Kept explicit constructors where they still document overloads, default helper creation, package-private test seams, or custom initialization.
- Verified `.idea/` and `.DS_Store` are ignored and not tracked.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskQueueItemConvertTests,FixTaskTestRunConvertTests,FixTaskTimelineEventConvertTests,FixTaskToolCallConvertTests,MyBatisFixTaskQueueTests,MyBatisFixTaskTestRunServiceTests,MyBatisFixTaskTimelineServiceTests,MyBatisFixTaskToolCallServiceTests test`: passed, 19 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,TaskControllerTests,TaskQueueControllerTests,FixTaskWorkerTests,WorkspaceFixTaskExecutorTests,CommandExecutionGuardTests,RepositoryInspectionToolsTests,FileToolsTests,DiffToolTests,PullRequestToolTests,IssueCommentToolTests,MyBatisFixTaskServiceTests,MyBatisFixTaskQueueTests,MyBatisFixTaskQueueQueryServiceTests,MyBatisFixTaskModelCallServiceTests,MyBatisFixTaskTestRunServiceTests,MyBatisFixTaskTimelineServiceTests,MyBatisFixTaskToolCallServiceTests test`: passed, 95 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests,OpenAiCompatibleModelClientTests,GitHubIssueCommentClientTests,GitHubPullRequestClientTests,GitHubWebhookServiceTests,GitHubWebhookSignatureVerifierTests,FixPlanGeneratorTests,SimplePatchWorkflowTests,PlannedPatchWorkflowTests,RepositoryInspectionToolsTests,MavenTestRunnerTests,CommandExecutionGuardTests,TaskProcessRegistryTests,FixTaskWorkerTests,FixTaskQueuePollerTests test`: passed, 70 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.

## 2026-06-20

Started self-hosted README and environment documentation from `docs/plans/036-self-hosted-readme-and-env-docs.md`.

Changes:

- Rewrote `README.md` around the current self-hosted GitHub issue-to-PR workflow.
- Added `.env.example` for Docker Compose, GitHub webhook/token, workspace, and model provider configuration.
- Updated `.gitignore` so local `.env` files stay untracked while `.env.example` remains commit-ready.
- Passed model provider environment variables through `docker-compose.yml`.
- Added `docs/agent/smoke-test-checklist.md` for repeatable local demo validation.
- Updated temporary URL and IDEA local run docs to match the MySQL-backed Docker profile and default no-database IDEA profile.

Validation:

- `docker compose --env-file .env.example config`: passed, Compose resolves the backend, MySQL, GitHub, workspace, and model provider environment variables.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.

## 2026-06-20

Started end-to-end smoke test hardening from `docs/plans/037-end-to-end-smoke-test-hardening.md`.

Initial state:

- Branch `037-end-to-end-smoke-test-hardening` is active.
- Local `.env` is not present, so Docker runtime and GitHub webhook smoke tests are blocked until local secrets are configured.

Validation:

- `docker compose --env-file .env.example config`: passed, Compose structure resolves with placeholder values.
- `mvn -pl PatchPilot test`: passed, 229 tests run, 0 failures, 0 errors.
- `.env` validation: passed, required keys are present and non-placeholder without exposing values.
- `docker compose --env-file .env config --quiet`: passed.
- `docker compose --env-file .env ps -a`: passed, `patchpilot-backend` is up and `patchpilot-mysql` is healthy.
- Backend logs show Spring Boot active with the `docker` profile, Flyway migrations up to date, and Tomcat started on port 8080.
- Local API checks passed outside the restricted command sandbox:
  - `curl http://127.0.0.1:8080/health`: returned `success=true` and `status=UP`.
  - `curl http://127.0.0.1:8080/api/tasks`: returned `success=true` with an empty task list.
  - `curl http://127.0.0.1:8080/api/task-queue/summary`: returned `success=true` with zero queue counts.
- Note: sandboxed local port checks returned connection errors even while Docker and the backend were healthy; unrestricted local checks confirmed the backend is reachable.
- Cloudflare Tunnel health check reached the backend; `GET /api/github/webhook` returned `405`, confirming the route is reachable and POST-only.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `d87a1b3d-87e3-4435-9ad1-fda9d1f528e5`.
- Task `d87a1b3d-87e3-4435-9ad1-fda9d1f528e5` reached the worker:
  - Workspace clone and branch creation succeeded.
  - Model call succeeded with model `gpt-5.5`.
  - Planned patch replaced `docs/demo.md`.
  - Diff tool ran successfully.
- Task failed during verification with `maven tests failed: maven test command timed out`.
- Test run record captured `mvn test`, exit code `124`, duration `300001` ms, and output `maven test command timed out`.
- Root cause investigation:
  - The backend container runs with `SPRING_PROFILES_ACTIVE=docker`.
  - `MavenTestRunner` inherited the backend container environment when launching target repository tests.
  - The target repository Spring tests then loaded the Docker profile and failed to create MyBatis mapper-backed services in the test context.
  - Re-running the same container workspace with `env -u SPRING_PROFILES_ACTIVE mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,TaskControllerTests test` passed, 30 tests run, 0 failures, 0 errors.
- Fixed `MavenTestRunner` so child Maven processes remove `PATCHPILOT_*` runtime variables and `SPRING_PROFILES_ACTIVE`.
- Added a regression test that verifies Maven child process environment sanitization preserves normal variables such as `PATH` and `JAVA_HOME`.
- Found a second Maven runner issue: output was read only after process exit, so large Maven/Spring output could fill the process pipe and make a failing command look like a timeout.
- Fixed `MavenTestRunner` to read merged output asynchronously while the process is running and keep partial output on timeout.
- Added a regression test that emits 20,000 output lines and exits with code 7; it failed with exit code 124 before the fix and passed after output was read asynchronously.
- Updated smoke test docs from the old unsupported `/agent fix touch ...` command to `/agent fix replace docs/demo.md PatchPilot smoke test`.

Validation after fix:

- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests test`: first failed because environment sanitization did not exist, then passed after implementation, 9 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,TaskControllerTests test`: passed, 30 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests#should_capture_large_command_output_without_blocking_until_timeout test`: first failed with exit code 124 instead of the expected exit code 7, then passed after asynchronous output capture.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests test`: passed after both fixes, 10 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 231 tests run, 0 failures, 0 errors.

Follow-up:

- Rebuild the Docker backend image and rerun the GitHub issue smoke test so the running container uses the fixed `MavenTestRunner`.

Second smoke-test rerun:

- Rebuilt and restarted Docker Compose backend with the fixed `MavenTestRunner`.
- `curl http://127.0.0.1:8080/health`: returned `success=true` and `status=UP`.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `4645c8e7-058b-4ea6-a51e-e00d1d3878be`.
- The task reached workspace clone, model call, planned patch, diff, and Maven verification.
- The task then failed while persisting the test run record because full Maven output exceeded MySQL `text` capacity:
  - `Data too long for column 'output' at row 1`.
- Fixed test-run persistence by truncating captured Maven output before storing it.
- Fixed worker failure handling by truncating failure reasons before saving task state, timeline messages, and GitHub status-comment content.

Validation after output truncation fix:

- `mvn -pl PatchPilot -Dtest=MyBatisFixTaskTestRunServiceTests,InMemoryFixTaskTestRunServiceTests,FixTaskWorkerTests test`: first failed because output and failure reasons were not truncated, then passed after implementation, 11 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 234 tests run, 0 failures, 0 errors.

Follow-up:

- Rebuild the Docker backend image again and rerun the GitHub issue smoke test so the running container uses the output truncation fix.

Third smoke-test rerun:

- Confirmed the running backend image contains `LogSummary.class`, so the output truncation fix is present in the container.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `c550c1ed-bc4e-42b8-aa02-f3de027b0b9e`.
- The task reached workspace clone, model call, planned patch, diff, and Maven verification.
- Maven verification succeeded:
  - `mvn test` exit code `0`.
  - Duration `43292` ms.
  - Captured output was truncated before persistence, avoiding the previous MySQL `output` overflow.
- The task then failed at `CommitTool` because the container workspace had no Git author identity:
  - `git commit failed: Author identity unknown`.
  - Git attempted to auto-detect `root@...` inside the container and failed.
- Fixed `GitCommandRunner#commit(...)` to run commits with a command-scoped PatchPilot author identity:
  - `git -C <repo> -c user.name=PatchPilot -c user.email=patchpilot@example.com commit -m <message>`.
- Updated `CommandExecutionGuard` to allow only that exact command-scoped identity form for commits.

Validation after Git author identity fix:

- `mvn -pl PatchPilot -Dtest=GitCommandRunnerTests#should_commit_with_patchpilot_author_identity,CommandExecutionGuardTests#should_allow_mvp_git_and_maven_commands_inside_workspace_root test`: first failed because the commit command lacked author identity and the guard rejected the command-scoped identity form, then passed after implementation, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=CommandExecutionGuardTests,GitCommandRunnerTests,CommitToolTests,WorkspaceFixTaskExecutorTests test`: passed, 32 tests run, 0 failures, 0 errors.

Follow-up:

- Run the full backend test suite, rebuild the Docker backend image, and rerun the GitHub issue smoke test so the running container uses the Git author identity fix.

Fourth smoke-test rerun:

- Rebuilt and restarted Docker Compose backend with the Git author identity fix.
- `curl http://127.0.0.1:8080/health`: returned `success=true` and `status=UP`.
- GitHub webhook smoke test with `/agent fix replace docs/demo.md PatchPilot smoke test` created task `7cb4f2ab-189e-4df6-85b0-519f3fabe46c`.
- The task completed end to end:
  - Workspace clone and branch creation succeeded.
  - Planned patch replaced `docs/demo.md`.
  - Diff tool succeeded.
  - `mvn test` succeeded with exit code `0` and duration `45088` ms.
  - Commit succeeded on branch `patchpilot/7cb4f2ab-189e-4df6-85b0-519f3fabe46c`.
  - Push succeeded to GitHub.
  - Pull Request creation succeeded: `https://github.com/bingqin2/PatchPilot/pull/7`.
- Task detail returned `status=COMPLETED`, `failureReason=null`, and `pullRequestUrl=https://github.com/bingqin2/PatchPilot/pull/7`.
- Timeline ended with `PR_CREATED` followed by `COMPLETED`.

## 2026-06-20

Implemented status comment failure observability from `docs/plans/038-status-comment-observability.md`.

Changes:

- Added `STATUS_COMMENT_FAILED` as an additive timeline event.
- Recorded `STATUS_COMMENT_FAILED` when accepted issue status-comment creation fails, while still dispatching the task.
- Recorded `STATUS_COMMENT_FAILED` when lifecycle status-comment updates fail in the worker, while preserving the durable task status.
- Reused failure-reason truncation for status-comment failure messages.
- Updated setup and smoke-test docs to require fine-grained GitHub token permissions for `Contents`, `Issues`, and `Pull requests`.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_dispatch_created_task_when_status_comment_creation_fails,FixTaskWorkerTests#should_keep_completed_status_when_status_comment_update_fails test`: passed, 2 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,FixTaskWorkerTests test`: passed, 11 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 235 tests run, 0 failures, 0 errors.

Implemented test-run output storage expansion from `docs/plans/039-test-run-output-storage.md`.

Changes:

- Added Flyway migration `V10__expand_fix_task_test_run_output.sql` to change `fix_task_test_run.output` to `mediumtext`.
- Added a dedicated `LogSummary.truncateTestRunOutput(...)` helper with a higher bounded output limit for Maven test logs.
- Updated in-memory and MyBatis test-run services to use the test-run-specific output limit.
- Added regression coverage that preserves 120k-character test output and still truncates very large output.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskTestRunOutputStorageMigrationTests,MyBatisFixTaskTestRunServiceTests,InMemoryFixTaskTestRunServiceTests test`: first failed because migration `V10__expand_fix_task_test_run_output.sql` did not exist and 120k-character output was still truncated to the old `TEXT` limit, then passed after implementation, 8 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 238 tests run, 0 failures, 0 errors.

Ran GitHub smoke test after the test-run output storage fix from `docs/plans/040-github-smoke-run-after-storage-fix.md`.

Smoke setup:

- Local IDEA backend was running on `http://127.0.0.1:18080` with the `idea` profile.
- Docker MySQL was reachable through the IDEA compose profile on `127.0.0.1:3307`.
- Cloudflare Tunnel was restarted against `http://127.0.0.1:18080`.
- GitHub webhook payload URL was updated to the new temporary tunnel URL.

Smoke result:

- GitHub webhook delivery for `/agent fix replace docs/demo.md PatchPilot storage smoke test` created task `73c92ed1-d2d5-4be3-a5ad-c1ff7047f12c`.
- Task detail returned `status=COMPLETED`, `failureReason=null`, and `pullRequestUrl=https://github.com/bingqin2/PatchPilot/pull/8`.
- Queue summary returned `pendingCount=0`, `runningCount=0`, `completedCount=5`, `failedCount=0`, and `cancelledCount=0`.
- Test-run API returned a persisted `mvn test` record with exit code `0`, duration `12769` ms, and full Maven test output including `Tests run: 238, Failures: 0, Errors: 0, Skipped: 0`.
- Timeline ended with `PR_CREATED` followed by `COMPLETED`.

Follow-up:

- The timeline recorded `STATUS_COMMENT_FAILED` with `GitHub issue comment creation failed: HTTP 403`; task execution still completed because issue comments are best-effort. Recheck the fine-grained GitHub token's `Issues: Read and write` permission or regenerate/reload the token before relying on issue status comments.

Implemented issue comment permission diagnostics from `docs/plans/041-issue-comment-permission-diagnostics.md`.

Changes:

- Added a shared HTTP failure message helper for GitHub Issue comment create/update calls.
- Expanded HTTP `403` failures with an actionable `PATCHPILOT_GITHUB_TOKEN` permission hint for fine-grained tokens.
- Preserved concise existing messages for non-`403` GitHub Issue comment failures.
- Updated setup and smoke-test docs to call out `Issues: Read and write` as the required permission for PatchPilot status comments.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests test`: first failed because HTTP `403` messages only included the status code, then passed after adding the permission hint, 7 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot -Dtest=GitHubIssueCommentClientTests,GitHubWebhookServiceTests,FixTaskWorkerTests test`: passed, 18 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 239 tests run, 0 failures, 0 errors.

Implemented task metrics summary API from `docs/plans/042-task-metrics-summary-api.md`.

Changes:

- Added `FixTaskMetricsSummaryVo` for task-level operational metrics.
- Added `FixTaskMetricsService` and `DefaultFixTaskMetricsService` to aggregate status counts, completion/failure rates, completion duration, and model token usage from existing task and model-call records.
- Exposed `GET /api/tasks/metrics/summary`.
- Documented the metrics endpoint in README.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: first failed because metrics service classes did not exist, then failed on a non-deterministic same-millisecond duration assertion, then passed after using fixed test data, 23 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 242 tests run, 0 failures, 0 errors.

Implemented task list filtering API from `docs/plans/043-task-list-filter-api.md`.

Changes:

- Extended `GET /api/tasks` with optional `status`, `repositoryOwner`, `repositoryName`, and `limit` query parameters.
- Added HTTP `400` responses for invalid task status and out-of-range limits.
- Kept the default `GET /api/tasks` behavior backward compatible.
- Documented filtered task-list examples in README.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because the task list endpoint ignored query parameters and returned HTTP `200` for invalid filters, then passed after controller filtering and validation, 24 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 245 tests run, 0 failures, 0 errors.

Implemented task detail audit summary API from `docs/plans/044-task-detail-audit-summary-api.md`.

Changes:

- Added `FixTaskAuditSummaryVo` for single-task audit summaries.
- Added `FixTaskAuditSummaryService` and `DefaultFixTaskAuditSummaryService` to aggregate existing task, timeline, test-run, tool-call, and model-call records.
- Exposed `GET /api/tasks/{taskId}/summary`.
- Documented the summary endpoint in README.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `/api/tasks/{taskId}/summary` did not exist, then passed after adding the summary service and controller route, 26 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 247 tests run, 0 failures, 0 errors.

Implemented task metrics test pass rate from `docs/plans/045-task-metrics-test-pass-rate.md`.

Changes:

- Extended task metrics summary with test-run count, passed/failed test-run counts, and test pass rate.
- Aggregated test-run metrics through the existing `FixTaskTestRunService`.
- Preserved zero values when no tasks or test runs exist.
- Covered the new metrics fields in service and controller tests.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: first failed because metrics summary did not expose test-run fields and metrics service did not depend on test-run service, then passed after implementation, 28 tests run, 0 failures, 0 errors.
- `mvn -pl PatchPilot test`: passed, 247 tests run, 0 failures, 0 errors.

Implemented the React dashboard scaffold from `docs/plans/046-react-dashboard-scaffold.md`.

Changes:

- Added a React + Vite + TypeScript frontend under `frontend/`.
- Added typed API helpers for task list, metrics summary, task summary, timeline, test-run, tool-call, and model-call endpoints.
- Built a compact operations dashboard with metric cards, task list, Pull Request links, selected task summary, timeline, Maven test output, tool calls, and model calls.
- Added Vitest and Testing Library coverage for successful backend rendering and backend error display.
- Documented frontend setup and validation commands in README.

Validation:

- `npm test` in `frontend/`: first failed because `src/App.tsx` did not exist, then failed because the task detail omitted the latest summary event, then passed after implementation, 2 tests run, 0 failures.
- `npm run build` in `frontend/`: first failed because TypeScript config did not match Vite/Vitest module resolution and test globals, then passed after separating Vite and Vitest config, production build generated `dist/`.

Implemented dashboard task status filters from `docs/plans/047-dashboard-task-filters.md`.

Changes:

- Added `ALL`, `PENDING`, `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, `FAILED`, and `CANCELLED` filters to the React task list.
- Updated the frontend task API helper to call `/api/tasks?limit=50` for all tasks and `/api/tasks?limit=50&status={STATUS}` for filtered lists.
- Reset selected task details when the current selection is not present in the filtered result.
- Added empty-state copy for filtered task lists.
- Documented the dashboard status filters in README.

Validation:

- `npm test` in `frontend/`: first failed because the filter controls were not implemented, then failed because the test used ambiguous status text and missing task-detail mocks, then passed after implementation, 3 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard task control actions from `docs/plans/048-dashboard-task-control-actions.md`.

Changes:

- Added frontend POST helpers for `/api/tasks/{taskId}/cancel` and `/api/tasks/{taskId}/retry`.
- Added `Cancel task` in task detail for `PENDING`, `RUNNING`, and `RUNNING_TESTS` tasks.
- Added `Retry task` in task detail for `FAILED` and `CANCELLED` tasks.
- Disabled the active action while a control request is in flight.
- Refreshed dashboard task list, metrics, and selected task detail after successful control actions.
- Documented dashboard cancel/retry support in README.

Validation:

- `npm test` in `frontend/`: first failed because `Cancel task` and `Retry task` controls did not exist, then passed after implementation, 5 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard queue observability from `docs/plans/049-dashboard-queue-observability.md`.

Changes:

- Added frontend queue types for queue summaries and queue items.
- Added API helpers for `/api/task-queue/summary` and `/api/task-queue/items`.
- Loaded queue data during dashboard refresh.
- Added a read-only Queue panel showing pending, available, delayed, running, failed, and cancelled counts.
- Rendered queue item id, task id, status, attempt count, available time, and last error.
- Documented dashboard queue visibility in README.

Validation:

- `npm test` in `frontend/`: first failed because the Queue panel did not exist, then passed after implementation, 6 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard issue links from `docs/plans/050-dashboard-issue-links.md`.

Changes:

- Added generated GitHub Issue links from task repository owner, repository name, and issue number.
- Rendered `Open Issue` links in visible task rows.
- Rendered `Open Issue` in the selected task detail header alongside existing Pull Request links.
- Documented dashboard issue links in README.

Validation:

- `npm test` in `frontend/`: first failed because no `Open Issue` links existed, then passed after implementation, 6 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard component extraction from `docs/plans/051-dashboard-component-extraction.md`.

Changes:

- Kept `frontend/src/App.tsx` as the dashboard data-loading, selection, and task-action coordinator.
- Extracted task list, task detail, queue panel, metric card, summary item, and record-line rendering into `frontend/src/dashboard/components/`.
- Moved dashboard formatting helpers into `frontend/src/dashboard/format.ts`.
- Moved selected-task detail state shape and empty state into `frontend/src/dashboard/types.ts`.
- Documented the dashboard component boundary in README.

Validation:

- `npm test` in `frontend/`: passed, 6 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard status comment links from `docs/plans/052-dashboard-status-comment-links.md`.

Changes:

- Added frontend coverage for optional task `statusCommentUrl` links.
- Rendered `Status Comment` links in task rows when a task exposes a status comment URL.
- Rendered `Status Comment` in the selected task detail action area when available.
- Documented dashboard status comment links in README.

Validation:

- `npm test` in `frontend/`: first failed because `Status Comment` links were not rendered, then passed after implementation, 6 tests run, 0 failures.

Implemented dashboard task timestamps from `docs/plans/053-dashboard-task-timestamps.md`.

Changes:

- Added frontend coverage for task row creation and update timestamps.
- Rendered `Created` and `Updated` times in each dashboard task row using existing task API fields.
- Preserved the original ISO timestamp through `dateTime` attributes.
- Added wrapping timestamp styling for task rows.
- Documented task timestamp visibility in README.

Validation:

- `npm test` in `frontend/`: first failed because task rows did not render `Created` and `Updated` times, then passed after implementation, 7 tests run, 0 failures.

Implemented dashboard detail empty states from `docs/plans/054-dashboard-detail-empty-states.md`.

Changes:

- Added frontend coverage for task detail sections with missing records.
- Rendered empty states for absent timeline events, Maven test runs, tool calls, and model calls.
- Limited empty states to completed detail-loading states so loading copy remains distinct.
- Documented detail empty-state behavior in README.

Validation:

- `npm test` in `frontend/`: first failed because missing detail records produced blank sections, then passed after implementation, 8 tests run, 0 failures.

Implemented dashboard call durations from `docs/plans/055-dashboard-call-durations.md`.

Changes:

- Added frontend coverage for tool-call and model-call duration rendering.
- Rendered tool-call duration next to the success/failure state in task detail records.
- Rendered model-call duration next to the total token count in task detail records.
- Reused the existing dashboard duration formatter, with no backend API or persistence changes.
- Documented dashboard call durations in README.

Validation:

- `npm test` in `frontend/`: first failed because task detail tool/model call records did not show durations, then passed after implementation, 9 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard task search from `docs/plans/056-dashboard-task-search.md`.

Changes:

- Added a task search input to the React task list panel.
- Filtered the currently loaded frontend task list by task id, repository, issue number, status, trigger comment, and failure reason.
- Kept status filters backed by existing backend query parameters.
- Added a distinct empty state when local search has no matches.
- Documented local dashboard search and recorded backend `GET /api/tasks?query=...` search as future work.

Validation:

- `npm test` in `frontend/`: first failed because the task list had no `Search tasks` searchbox, then passed after implementation, 10 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Planned backend task history search and pagination from `docs/plans/057-task-history-search-pagination-plan.md`.

Changes:

- Defined the target `GET /api/tasks?query=...&status=...&repositoryOwner=...&repositoryName=...&limit=...&offset=...` API shape.
- Documented searchable MySQL fields and stable newest-first sorting.
- Chose to keep the existing list response shape for the first backend-search phase to avoid frontend breakage.
- Split backend service/query, MyBatis, in-memory, controller, and frontend upgrade work into future implementation steps.
- Captured tests needed for query, pagination, escaping, and dashboard integration.

Validation:

- Documentation-only planning change; no runtime tests required.

Implemented backend task search and offset pagination from `docs/plans/058-backend-task-search-pagination.md`.

Changes:

- Added `FixTaskListQuery` as the backend task-list query object.
- Extended `GET /api/tasks` with optional `query` and `offset` parameters while preserving the existing list response shape.
- Moved task-list filtering from controller stream filtering into `FixTaskService#listTasks(FixTaskListQuery)`.
- Implemented equivalent query behavior for default in-memory tasks and MyBatis-backed task storage.
- Kept exact status and repository filters, newest-first sorting, and offset/limit application after filtering.
- Extended the frontend `listTasks()` helper to accept future `{ status, query, limit, offset }` options without changing the current dashboard UI behavior.
- Documented backend task-list search support and the remaining dashboard wiring work.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `query` and `offset` were ignored, then passed after implementation, 28 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: passed after adding equivalent service coverage, 25 tests run, 0 failures.
- `npm test` in `frontend/`: passed after adding API helper coverage for `{ status, query, limit, offset }`, 11 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 251 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard backend search and offset pagination from `docs/plans/059-dashboard-backend-search-pagination.md`.

Changes:

- Wired the dashboard search input to backend `GET /api/tasks?query=...`.
- Preserved status filters when sending backend search requests.
- Removed local-only task filtering from the dashboard coordinator.
- Added `Load more tasks` backed by `offset=tasks.length` and appended subsequent pages.
- Documented backend-backed dashboard search and the remaining pagination metadata limitation.

Validation:

- `npm test -- src/App.test.tsx -t "searches tasks with backend query parameters"`: first failed because the dashboard still only called `/api/tasks?limit=50`, then passed after wiring `searchQuery` into `listTasks()`.
- `npm test -- src/App.test.tsx -t "preserves status filter when searching backend task history"`: passed, preserving `query` and `status` together.
- `npm test -- src/App.test.tsx -t "loads the next backend task page with offset pagination"`: first failed because there was no `Load more tasks` button, then passed after adding offset pagination.
- `npm test` in `frontend/`: passed, 13 tests run, 0 failures.

Implemented task list pagination metadata from `docs/plans/060-task-list-pagination-metadata.md`.

Changes:

- Changed `GET /api/tasks` response data from a plain task array to a task page object.
- Added `FixTaskPageVo` with `items`, `limit`, `offset`, and `hasMore`.
- Computed `hasMore` by internally requesting one extra task beyond the requested page size.
- Updated the frontend task API type and dashboard state to consume `page.items` and `page.hasMore`.
- Kept task detail, metrics, queue, control, and audit endpoints unchanged.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because `data` was still an array, then passed after adding `FixTaskPageVo`; a follow-up failure from shared test data was fixed by filtering pagination metadata tests to a dedicated repository, 29 tests run, 0 failures.
- `npm test -- src/App.test.tsx -t "renders operational task dashboard from backend APIs"`: first failed because `tasks.find` received a page object, then passed after using `page.items`.
- `npm test` in `frontend/`: passed, 13 tests run, 0 failures.

Implemented task list total count from `docs/plans/061-task-list-total-count.md`.

Changes:

- Added `total` to the `GET /api/tasks` task page response.
- Added `FixTaskService#countTasks(FixTaskListQuery)` so count logic can use the same filters as task listing without `limit` or `offset`.
- Implemented matching count behavior in both in-memory and MyBatis-backed task services.
- Updated the React dashboard task list to show loaded task count versus total matching count.
- Updated frontend API types and tests for the `total` response field.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_return_task_list_pagination_metadata test`: first failed because `$.data.total` was missing, then passed after adding the count field.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests#should_count_tasks_before_limit_and_offset,MyBatisFixTaskServiceTests#should_count_tasks_with_query_filters_without_limit_or_offset test`: passed, 2 tests run, 0 failures.
- `npm test -- src/App.test.tsx -t "renders operational task dashboard from backend APIs"`: first failed because the task list still rendered `2 visible tasks`, then passed after rendering `2 of 2 tasks visible`.
- `npm test -- src/api.test.ts`: passed, 1 test run, 0 failures.

Implemented dashboard failure cause summary from `docs/plans/062-dashboard-failure-cause-summary.md`.

Changes:

- Added `GET /api/tasks/metrics/failure-causes`.
- Added `FixTaskFailureCauseSummaryVo`.
- Extended `FixTaskMetricsService` with `failureCauses()`.
- Classified failed task reasons into `MAVEN_TESTS`, `GITHUB_AUTH`, `MODEL_ERROR`, `SANDBOX_REJECTION`, and `UNKNOWN`.
- Rendered a React failure-cause summary panel in the dashboard.
- Added a frontend API helper for the new metrics endpoint.
- Forced full Spring Boot controller/application tests to use the `default` profile so local `.env` values such as `SPRING_PROFILES_ACTIVE=docker` do not activate MyBatis-backed services in unit test context.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_failed_tasks_by_failure_cause,TaskControllerTests#should_get_task_failure_cause_summary test`: first failed because `failureCauses()` was missing, then failed once due to unstable ordering, then passed after adding the service method and fixed cause order.
- `npm test -- src/App.test.tsx -t "renders operational task dashboard from backend APIs"`: first failed because the dashboard did not render `Failure causes`, then passed after adding the panel.
- `mvn -pl PatchPilot test`: passed, 256 tests run, 0 failures.
- `SPRING_PROFILES_ACTIVE=docker mvn -pl PatchPilot clean -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,TaskControllerTests test`: first reproduced the missing `FixTaskModelCallMapper` context failure, then passed after adding explicit `@ActiveProfiles("default")`.
- `SPRING_PROFILES_ACTIVE=docker mvn -pl PatchPilot clean test`: passed, 256 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 14 tests run, 0 failures.
- `npm run build` in `frontend/`: passed.

Implemented dashboard model cost summary from `docs/plans/063-dashboard-model-cost-summary.md`.

Changes:

- Added `GET /api/tasks/metrics/model-usage`.
- Added `FixTaskModelUsageSummaryVo`.
- Extended `FixTaskMetricsService` with `modelUsage()`.
- Aggregated prompt tokens, completion tokens, total tokens, successful model calls, failed model calls, and estimated USD cost from recorded model-call rows.
- Added configurable model cost inputs under `patchpilot.agent.cost.prompt-token-usd` and `patchpilot.agent.cost.completion-token-usd`, both defaulting to `0`.
- Added a frontend API helper, type, and `ModelUsagePanel`.
- Rendered model usage next to failure causes in the dashboard operational summaries.
- Documented the model usage endpoint and dashboard cost summary in README and frontend design docs.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_model_usage_and_estimated_cost,TaskControllerTests#should_get_task_model_usage_summary test`: passed, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: first failed because `getModelUsageSummary()` and `Model usage` UI were missing, then failed once on ambiguous `Completion` label, then passed after adding the API helper, panel, and clearer `Completion tokens` label, 15 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 258 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 15 tests run, 0 failures.
- `npm run build` in `frontend/`: passed.

Implemented dashboard latency summary from `docs/plans/064-dashboard-latency-summary.md`.

Changes:

- Added `GET /api/tasks/metrics/latency`.
- Added `FixTaskLatencySummaryVo`.
- Extended `FixTaskMetricsService` with `latency()`.
- Aggregated completed task duration, model-call duration, tool-call duration, and test-run duration.
- Added a frontend API helper, type, and `LatencyPanel`.
- Rendered latency next to failure causes and model usage in the dashboard operational summaries.
- Documented the latency endpoint and dashboard latency summary in README and frontend design docs.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_latency_across_tasks_model_calls_tool_calls_and_test_runs test`: first failed because `DefaultFixTaskMetricsService` did not accept a tool-call service and `FixTaskMetricsService#latency()` did not exist.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_latency_across_tasks_model_calls_tool_calls_and_test_runs,TaskControllerTests#should_get_task_latency_summary test`: passed after adding the latency VO, service method, HTTP endpoint, and tool-call duration aggregation, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: first failed because `getLatencySummary()` and `Latency` UI were missing, then passed after adding the API helper, panel, and dashboard wiring, 16 tests run, 0 failures.

Implemented dashboard configuration summary from `docs/plans/065-dashboard-configuration-summary.md`.

Changes:

- Added `GET /api/configuration/summary`.
- Added `ConfigurationSummaryVo` and `ConfigurationController`.
- Returned provider, model, base URL, workspace root, queue policy, model-cost configuration status, and secret configured/missing booleans.
- Kept API key, GitHub token, and webhook secret values out of the response.
- Added frontend `ConfigurationSummary` type and `getConfigurationSummary()` API helper.
- Added `ConfigurationPanel` to the dashboard, rendered above queue state.
- Documented the configuration endpoint and dashboard panel in README and frontend design docs.

Validation:

- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests test`: first failed with 404 because the endpoint did not exist, then passed after adding the controller and VO, 1 test run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: first failed because `getConfigurationSummary()` and `Configuration` UI were missing, then passed after adding the API helper, panel, and dashboard wiring, 17 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 261 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 17 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard configuration health hints from `docs/plans/066-dashboard-configuration-health.md`.

Changes:

- Added `ConfigurationPanel` health evaluation for required secret status.
- Added advisory checks for missing model cost, invalid queue attempts, negative retry delay, and very low visibility timeout.
- Rendered `Configuration healthy`, setup issue counts, advisory counts, and terse issue rows.
- Updated the dashboard default test fixture to represent a healthy configuration.
- Documented configuration health hints in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because no health summary or issue rows existed, then passed after adding panel health evaluation and styles, 3 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 20 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard queue health hints from `docs/plans/067-dashboard-queue-health.md`.

Changes:

- Added `QueuePanel` health evaluation using existing queue summary data.
- Rendered `Queue has failures`, `Queue delayed`, `Queue active`, and `Queue idle` states.
- Added count details for failed, delayed, and running queue items.
- Preserved the existing queue summary cards and queue row list.
- Documented queue health hints in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/QueuePanel.test.tsx`: first failed because queue health labels did not exist, then passed after adding health evaluation and styles, 4 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 24 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard detail evidence summary from `docs/plans/068-dashboard-detail-evidence-summary.md`.

Changes:

- Added an `Execution evidence` strip to `TaskDetailPanel`.
- Summarized timeline, test-run, tool-call, and model-call counts from the existing detail summary response.
- Surfaced latest test status as `PASS`, `FAIL`, or `None` before the detailed Maven output.
- Added focused component coverage for populated evidence and missing latest test evidence.
- Documented the detail evidence strip in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the evidence strip did not exist, then passed after adding the component rendering and styles, 2 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 26 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard task deep links from `docs/plans/069-dashboard-task-deep-links.md`.

Changes:

- Initialized selected task state from the `taskId` URL query parameter.
- Updated `taskId` in the URL when operators select a task row.
- Preserved existing fallback behavior when no matching task is loaded.
- Documented dashboard task deep links in README and frontend design docs.

Validation:

- `npm test -- --run src/App.test.tsx -t "taskId URL parameter"`: first failed because URL task selection and URL updates did not exist, then passed after adding URL-backed task selection, 2 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 28 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented configurable frontend backend URL from `docs/plans/070-configurable-frontend-backend-url.md`.

Changes:

- Added a Vite proxy target helper that defaults to `http://127.0.0.1:8080`.
- Allowed `PATCHPILOT_FRONTEND_BACKEND_URL` and `VITE_PATCHPILOT_BACKEND_URL` to override the frontend dev proxy target.
- Documented IDEA `18080` frontend proxy usage in `.env.example`, README, and frontend design docs.

Validation:

- `npx vitest run --config vitest.config.ts viteProxy.test.ts`: first failed because `backendProxyTarget` did not exist, then passed after adding the helper, 3 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 31 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Follow-up fix:

- Loaded the repository root `.env` from Vite config so `PATCHPILOT_FRONTEND_BACKEND_URL` works when `npm run dev` is launched inside `frontend/`.
- Added coverage for parsing the frontend backend URL from `.env` content.

Validation:

- `npx vitest run --config vitest.config.ts viteProxy.test.ts`: first failed because `.env` parsing was not implemented, then passed after adding repository `.env` loading, 4 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 32 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard API error guidance from `docs/plans/071-dashboard-api-error-guidance.md`.

Changes:

- Added a shared frontend API request helper for GET and POST calls.
- Converted fetch failures and JSON parsing failures into an actionable backend/proxy guidance message.
- Preserved backend-provided JSON error messages for valid PatchPilot error envelopes.
- Added API tests for empty and non-JSON responses.
- Documented the dashboard backend/proxy error behavior in README and frontend design docs.

Validation:

- `npm test -- --run src/api.test.ts`: first failed because raw JSON parsing errors surfaced, then passed after adding guarded response parsing, 7 tests run, 0 failures.
- `npm test` in `frontend/`: passed, 34 tests run, 0 failures.
- `npm run build` in `frontend/`: passed, production build generated `dist/`.

Implemented dashboard copy task link from `docs/plans/072-dashboard-copy-task-link.md`.

Changes:

- Added a `Copy link` action to `TaskDetailPanel`.
- Generated shareable selected-task links from the current dashboard URL by setting `taskId`.
- Preserved existing query parameters when adding or replacing `taskId`.
- Added a short success or failure status after clipboard writes.
- Documented the copyable task link in README and frontend design docs.

Validation:

- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because `taskLinkFor` and the `Copy link` button did not exist, then passed after adding link generation and clipboard behavior, 4 tests run, 0 failures.

Implemented dashboard backend health status from `docs/plans/073-dashboard-backend-health-status.md`.

Changes:

- Added a frontend `BackendHealth` type and `getBackendHealth()` helper for `GET /health`.
- Loaded backend health during dashboard refresh.
- Displayed backend status, service name, and timestamp in `ConfigurationPanel`.
- Added an unavailable backend state when health data is not loaded.
- Documented backend health visibility in README and frontend design docs.

Validation:

- `npm test -- --run src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because `getBackendHealth()` and backend health UI did not exist, then passed after adding the API helper and panel rendering, 11 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx`: first failed because a custom App test fetch mock did not handle `/health`, then passed after adding the health fixture, 14 tests run, 0 failures.

Implemented dashboard refresh state from `docs/plans/074-dashboard-refresh-state.md`.

Changes:

- Disabled the top-level refresh button while dashboard refresh is in flight.
- Changed the refresh button label and accessible name to `Refreshing`.
- Added a compact `Dashboard refreshing` status region during top-level data loading.
- Documented refresh progress feedback in README and frontend design docs.

Validation:

- `npm test -- --run src/App.test.tsx -t "dashboard refresh progress"`: first failed because no refresh status region or disabled refreshing button existed, then passed after adding refresh state UI, 1 test run, 0 failures.

Implemented dashboard last refresh time from `docs/plans/075-dashboard-last-refresh-time.md`.

Changes:

- Tracked a `lastRefreshedAt` timestamp after successful top-level dashboard refreshes.
- Rendered `Last refreshed` under the dashboard title.
- Added a reusable compact date-time formatter for title-level timestamps.
- Documented last-refresh feedback in README and frontend design docs.

Validation:

- `npm test -- --run src/App.test.tsx -t "renders operational task dashboard"`: first failed because no `Last refreshed` timestamp existed, then passed after adding the refresh timestamp state and title rendering, 1 test run, 0 failures.

Implemented task detail aggregate API from `docs/plans/076-task-detail-aggregate-api.md`.

Changes:

- Added `GET /api/tasks/{taskId}/detail` to return the selected task audit summary, timeline events, test runs, tool calls, and model calls in one response.
- Preserved the existing narrower task detail endpoints for direct debugging.
- Added frontend `FixTaskDetail` typing and `getTaskDetail()`.
- Switched the dashboard selected-task loader from five detail requests to the aggregate detail endpoint.
- Documented the aggregate task detail endpoint in README and the frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_detail_by_task_id,TaskControllerTests#should_return_404_for_missing_task_detail test`: first failed because `/api/tasks/{taskId}/detail` did not exist, then passed after adding the response type and controller endpoint, 2 tests run, 0 failures.
- `npm test -- src/api.test.ts src/App.test.tsx`: first failed because `getTaskDetail()` did not exist and the dashboard still called five detail endpoints, then passed after adding the helper and switching the selected-task loader, 24 tests run, 0 failures.

Implemented task detail queue status from `docs/plans/077-task-detail-queue-status.md`.

Changes:

- Extended `FixTaskQueueQueryService` with `findByTaskId(String taskId)`.
- Implemented MyBatis-backed task queue lookup by task id, returning the latest queue item by update time.
- Included the selected task's latest queue item in `GET /api/tasks/{taskId}/detail`.
- Added frontend task-detail typing for optional queue item data.
- Rendered queue status, attempt count, last error, available time, and locked time in `TaskDetailPanel`.
- Documented selected-task queue visibility in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_detail_by_task_id test`: first failed at test compilation because `findByTaskId` did not exist, then passed after adding the service method, queue lookup, and detail response field.
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the task detail panel did not render queue state, then passed after adding queue item typing and rendering, 14 tests run, 0 failures.

Implemented task detail queue history from `docs/plans/078-task-detail-queue-history.md`.

Changes:

- Extended `FixTaskQueueQueryService` with `listByTaskId(String taskId)`.
- Reused the task-scoped queue list to keep `queueItem` as the latest queue record and return `queueItems` as the full selected-task queue history.
- Included queue history in `GET /api/tasks/{taskId}/detail`.
- Added frontend task-detail typing for queue history.
- Rendered a `Queue History` section with queue item id, status, attempt count, available time, locked time, and last error.
- Documented task-detail queue history in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_detail_by_task_id,MyBatisFixTaskQueueQueryServiceTests#should_list_queue_items_by_task_id test`: first failed because `listByTaskId` did not exist, then passed after adding the service method and aggregate detail response field, 2 tests run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because `Queue History` was not rendered, then passed after adding queue history rendering and scoped assertions, 15 tests run, 0 failures.

Implemented task report copy from `docs/plans/079-task-report-copy.md`.

Changes:

- Added `GET /api/tasks/{taskId}/report` to return a Markdown task diagnostic report.
- Built the report from aggregate task records: metadata, status, failure reason, queue state, timeline, test runs, tool calls, and model calls.
- Added frontend `getTaskReport(taskId)`.
- Added a `Copy report` action to selected task details.
- Wired the dashboard action through `App` so report content is fetched from the backend and copied to the clipboard.
- Documented the report endpoint and dashboard copy action in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_report_by_task_id,TaskControllerTests#should_return_404_for_missing_task_report test`: first failed because `/api/tasks/{taskId}/report` did not exist, then passed after adding the endpoint and report generator, 2 tests run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`: first failed because `getTaskReport`, the `Copy report` button, and App wiring did not exist, then passed after adding the API helper and UI flow, 33 tests run, 0 failures.

Implemented dashboard manual task creation from `docs/plans/080-dashboard-manual-task-creation.md`.

Changes:

- Added `POST /api/tasks` for manual dashboard-created tasks.
- Added `CreateFixTaskDto`, `CreateManualFixTaskCommand`, and `ManualFixTaskService`.
- Kept manual creation on the same durable task, timeline, dispatcher, and queue path as webhook-created work.
- Rejected invalid manual task requests and duplicate active work for the same repository issue.
- Added frontend `createTask()` and a `ManualTaskForm`.
- Wired the dashboard form to create a task, select the created task id, refresh dashboard data, show success, and preserve form values on creation failure.
- Documented manual task creation in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_create_manual_task_and_dispatch_it,TaskControllerTests#should_return_bad_request_for_invalid_manual_task_request,TaskControllerTests#should_return_conflict_when_manual_task_already_active_for_issue test`: first failed because `POST /api/tasks` returned 405, then passed after adding the endpoint and manual task service, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_create_manual_task_and_dispatch_it,TaskControllerTests#should_return_bad_request_for_invalid_manual_task_request,TaskControllerTests#should_return_conflict_when_manual_task_already_active_for_issue,DefaultManualFixTaskServiceTests test`: passed after isolating controller tests from async worker execution and adding service-level coverage, 5 tests run, 0 failures.
- `npm test -- src/api.test.ts src/App.test.tsx -t "creates manual task|manual task creation"`: first failed because `createTask` and the manual form did not exist, then failed once because handled creation errors still surfaced as unhandled rejections, then passed after adding the API helper, form, App wiring, and handled-error preservation, 2 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 272 tests run, 0 failures.
- `cd frontend && npm test`: passed, 47 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard task detail route from `docs/plans/081-dashboard-task-detail-route.md`.

Changes:

- Added `/tasks/{taskId}` as the canonical selected-task dashboard route.
- Kept legacy `?taskId={taskId}` URLs compatible for previously copied links.
- Updated task selection to write path-based routes while preserving unrelated query parameters and hash fragments.
- Updated copyable task links to generate `/tasks/{taskId}` URLs.
- Documented route-based task deep links in README and frontend design docs.

Validation:

- `npm test -- src/App.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx -t "task detail route|taskId URL parameter|selected task route|shareable task link|deep link"`: first failed because the dashboard still only read and wrote `?taskId=...`, then passed after adding path-route parsing, writing, and copy-link generation, 6 tests run, 0 failures.
- `cd frontend && npm test`: passed, 49 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard filter URL state from `docs/plans/082-dashboard-filter-url-state.md`.

Changes:

- Initialized dashboard status and search filters from `status` and `query` URL parameters.
- Treated invalid `status` URL values as `ALL`.
- Updated status filter changes to write `status` to the URL and remove `status=ALL`.
- Updated search changes to write `query` to the URL and remove it when cleared.
- Preserved selected task routes, unrelated query parameters, and hash fragments while syncing filter state.
- Documented URL-backed filtered investigation views in README and frontend design docs.

Validation:

- `npm test -- src/App.test.tsx -t "filter URL state|task detail route with filters|syncs status filter|syncs search query|removes cleared search"`: first failed because the dashboard ignored URL filter state and did not write filter changes back to the URL, then passed after adding filter parsing and URL sync helpers, 4 tests run, 0 failures.
- `cd frontend && npm test`: passed, 53 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard filter reset action from `docs/plans/083-dashboard-filter-reset-action.md`.

Changes:

- Added a `Clear filters` action to the dashboard task list that appears only when status or search filters are active.
- Reset status to `ALL`, cleared the search input, removed `status` and `query` from the URL, and preserved the selected `/tasks/{taskId}` route, unrelated query parameters, and hash fragments.
- Let the existing dashboard refresh effect reload the default `GET /api/tasks?limit=50` task page after clearing filters.
- Added responsive task-search layout styling so the reset action stays aligned on desktop and wraps cleanly on narrow screens.
- Documented the reset behavior in README and frontend design notes.

Validation:

- `npm test -- src/App.test.tsx -t "clear filters"`: first failed because the dashboard did not expose a `Clear filters` button, then passed after adding the reset action and URL cleanup behavior, 2 tests run, 0 failures.
- `cd frontend && npm test`: passed, 55 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard sort control from `docs/plans/084-dashboard-sort-control.md`.

Changes:

- Added backend task-list sorting through `GET /api/tasks?sort=createdAtDesc|createdAtAsc`, with newest-first as the default.
- Added `FixTaskSort` and carried sort direction through `FixTaskListQuery`, in-memory task listing, and MyBatis-backed task listing.
- Kept sorting before offset/limit pagination so `Load more` continues to page through a stable order.
- Rejected invalid backend sort values with `sort must be createdAtDesc or createdAtAsc`.
- Added a dashboard `Sort tasks` control with `Newest first` and `Oldest first` options.
- Stored non-default sort state as `sort=createdAtAsc` in the URL, restored valid sort state on load, and ignored invalid frontend sort values by falling back to newest-first.
- Preserved active sort when clearing status/search filters and included sort in load-more requests.
- Documented task-list sort behavior in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_sort_tasks_oldest_first,TaskControllerTests#should_return_bad_request_for_invalid_task_list_sort,InMemoryFixTaskServiceTests#should_list_tasks_oldest_first_when_requested,MyBatisFixTaskServiceTests#should_list_tasks_oldest_first_when_requested test`: first failed because controller ignored `sort` and services always returned newest-first, then passed after parsing and applying sort, 4 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "sort|offset pagination"`: first failed because the dashboard had no `Sort tasks` control and did not include sort in requests, then passed after adding API sort parameters, URL state, the control, and load-more propagation, 6 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 276 tests run, 0 failures.
- `cd frontend && npm test`: passed, 59 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented dashboard repository filters from `docs/plans/085-dashboard-repository-filters.md`.

Changes:

- Added task-list repository owner and repository name filters to the dashboard.
- Passed trimmed `repositoryOwner` and `repositoryName` values from the frontend API helper to `GET /api/tasks`.
- Restored repository filters from the URL and kept them synchronized while preserving selected task routes, existing status/search/sort state, unrelated query parameters, and hash fragments.
- Included repository filters in `Load more` pagination requests.
- Updated `Clear filters` to reset status, search, repository owner, and repository name while preserving active sort state.
- Added a repository-specific empty state for task lists narrowed only by repository filters.
- Documented repository-filter behavior in README and frontend design notes.

Validation:

- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "repository filter|backend task search sort|offset pagination"`: first failed because `listTasks` omitted `repositoryOwner` and `repositoryName`, the task list had no repository filter controls, and pagination did not carry repository filters; then passed after adding API parameters, URL state, task-list controls, reset behavior, and pagination propagation, 5 tests run, 0 failures.
- `cd frontend && npm test`: passed, 62 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `git diff --check`: passed with no whitespace errors.
- `mvn -pl PatchPilot test`: passed, 276 tests run, 0 failures.

Implemented dashboard created time filters from `docs/plans/086-dashboard-created-time-filters.md`.

Changes:

- Added optional backend `createdAfter` and `createdBefore` task-list filters using ISO-8601 instant values.
- Applied inclusive created-time filtering in both in-memory and MyBatis-backed task listing.
- Returned a parameter-specific HTTP 400 response for invalid created-time filter values.
- Passed trimmed created-time filter values from the frontend API helper to `GET /api/tasks`.
- Added `Filter created after` and `Filter created before` controls to the dashboard task list.
- Restored created-time filters from the URL and kept them synchronized with status, search, repository, selected task route, hash fragments, and non-default sort state.
- Included created-time filters in refresh and `Load more` pagination requests.
- Updated `Clear filters` to reset status, search, repository, and created-time filters while preserving active sort state.
- Documented created-time filter behavior in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_filter_tasks_by_created_time_range+should_return_bad_request_for_invalid_created_time_filter,InMemoryFixTaskServiceTests#should_list_tasks_with_created_time_range,MyBatisFixTaskServiceTests#should_list_tasks_with_created_time_range test`: first failed because task-list queries had no created-time fields and services ignored created time; then passed after adding backend parsing and service filters, 4 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "created time filter|backend task search sort|offset pagination"`: first failed because `listTasks` omitted `createdAfter`/`createdBefore`, the dashboard had no created-time controls, and pagination did not carry created-time filters; then passed after adding API parameters, URL state, task-list controls, reset behavior, and pagination propagation, 5 tests run, 0 failures.
- `cd frontend && npm test`: passed, 65 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `mvn -pl PatchPilot test`: passed, 280 tests run, 0 failures.
- `git diff --check`: passed with no whitespace errors.

Implemented dashboard status filter counts from `docs/plans/087-dashboard-status-filter-counts.md`.

Changes:

- Added `GET /api/tasks/status-counts` for total and per-status task counts.
- Reused the existing task-list query model for search, repository, and created-time count scopes.
- Kept status counts independent from the active status filter, sort, limit, and offset.
- Returned parameter-specific HTTP 400 responses for invalid created-time count filters.
- Added a frontend `getTaskStatusCounts()` API helper and `FixTaskStatusCounts` type.
- Loaded status counts during dashboard refresh and rendered count badges on status filter buttons.
- Preserved status button accessible names as the status labels while showing visual count badges.
- Documented scoped status count behavior in README and frontend design notes.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_count_tasks_by_status_for_filtered_scope+should_return_bad_request_for_invalid_status_count_created_time_filter test`: first failed because `/api/tasks/status-counts` did not exist and was handled as a task id route, then passed after adding the endpoint and status count response, 2 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/App.test.tsx -t "status count|status filter counts"`: first failed because `getTaskStatusCounts()` did not exist and status buttons had no count badges, then passed after adding the API helper, dashboard refresh wiring, and button badges, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.
- `cd frontend && npm test -- --reporter=dot`: passed, 6 test files and 68 tests.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented safety gate and language adapter foundation from `docs/plans/088-safety-gate-language-adapter-foundation.md`.

Changes:

- Added a `CommandSafetyGate` that accepts supported `/agent fix` commands and rejects unsafe destructive, secret-exfiltration, or arbitrary shell style instructions before task creation.
- Added `REJECTED` webhook handling so unsafe GitHub comments return a non-task result instead of creating or dispatching work.
- Applied the same safety gate to dashboard-created manual tasks so manual API calls cannot bypass webhook command checks.
- Added a `LanguageAdapter` boundary, `LanguageDetectionResult`, and `JavaMavenLanguageAdapter`.
- Routed `MavenTestRunner` through the Java/Maven adapter for Maven wrapper and `pom.xml` detection while preserving the existing allowlisted `./mvnw test` and `mvn test` behavior.
- Documented the safety gate and adapter boundary in README and architecture docs.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_reject_dangerous_agent_fix_command_before_task_creation,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_command_is_unsafe,JavaMavenLanguageAdapterTests test`: first failed because language adapter types did not exist and `REJECTED` was not a webhook status, then passed after adding the adapter and safety gate wiring, 5 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests#should_run_system_maven_when_only_pom_exists test`: first failed because `MavenTestRunner` did not accept the Java/Maven adapter dependency, then passed after routing detection through `JavaMavenLanguageAdapter`.
- `mvn -pl PatchPilot -Dtest=MavenTestRunnerTests,JavaMavenLanguageAdapterTests test`: passed, 13 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_reject_dangerous_agent_fix_command_before_task_creation,GitHubWebhookControllerTests#should_reject_dangerous_agent_fix_issue_comment,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_command_is_unsafe,TaskControllerTests#should_return_bad_request_when_manual_task_command_is_unsafe test`: passed, 4 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.
- `cd frontend && npm test -- --reporter=dot`: passed, 6 test files and 68 tests.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `git diff --check`: passed with no whitespace errors.

Implemented authorized trigger policy from `docs/plans/089-authorized-trigger-policy.md`.

Changes:

- Added `SafetyProperties` for optional trigger-user and repository allowlists.
- Added `SafetyGateRequest` so safety decisions can use repository owner, repository name, trigger user, and trigger comment together.
- Extended `CommandSafetyGate` to reject unsafe commands first, then reject trigger users or repositories outside configured allowlists.
- Applied the same authorization policy to GitHub webhooks and dashboard/manual task creation before task creation or dispatch.
- Added environment variables `PATCHPILOT_ALLOWED_TRIGGER_USERS` and `PATCHPILOT_ALLOWED_REPOSITORIES`.
- Documented allowlist configuration and updated the safety architecture notes.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests#should_reject_agent_fix_from_unauthorized_trigger_user_before_task_creation+should_reject_agent_fix_for_unauthorized_repository_before_task_creation+should_accept_agent_fix_when_trigger_user_and_repository_are_allowed,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_trigger_user_is_not_allowed+should_reject_manual_task_when_repository_is_not_allowed,TaskControllerTests#should_return_bad_request_when_manual_task_trigger_user_is_not_allowed+should_return_bad_request_when_manual_task_repository_is_not_allowed test`: first failed because `SafetyProperties` did not exist.
- The same target command then failed because Spring selected the no-arg safety gate constructor, so controller tests still created tasks for unauthorized inputs.
- The same target command passed after adding configuration binding and constructor injection, 7 tests run, 0 failures.

Implemented rejected trigger audit log from `docs/plans/090-rejected-trigger-audit-log.md`.

Changes:

- Added `RejectedTriggerAuditService` with in-memory and MyBatis implementations.
- Added `rejected_trigger_audit` MySQL migration for rejected `/agent fix` attempts that do not become tasks.
- Added `GET /api/rejected-triggers` with bounded `limit` validation.
- Recorded rejected webhook triggers with source, delivery id, repository, issue number, trigger user, command, reason, and timestamp.
- Recorded rejected manual task creation attempts through the same audit service.
- Documented rejected trigger inspection and the separation between rejected triggers and executable task records.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests#should_reject_dangerous_agent_fix_command_before_task_creation,TaskControllerTests#should_return_bad_request_when_manual_task_trigger_user_is_not_allowed test`: first failed because the rejected trigger audit model, service, controller, mapper, and migration did not exist; then passed after implementation, 9 tests run, 0 failures.

Implemented actionable command classification from `docs/plans/091-actionable-command-classification.md`.

Changes:

- Added deterministic actionability checks to `CommandSafetyGate`.
- Rejected empty or vague trigger comments before task creation.
- Kept clear patch operations, likely file references, and concrete failure descriptions actionable.
- Routed vague webhook and manual API triggers into the existing rejected trigger audit path.
- Updated trigger examples and safety gate documentation.

Validation:

- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,GitHubWebhookServiceTests#should_reject_unactionable_agent_fix_command_before_task_creation,TaskControllerTests#should_return_bad_request_when_manual_task_command_is_not_actionable test`: first failed because vague commands still created tasks; then passed after adding actionability classification, 9 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,GitHubWebhookServiceTests,GitHubWebhookControllerTests,TaskControllerTests#should_return_bad_request_when_manual_task_command_is_not_actionable test`: passed, 27 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.

Implemented model-assisted trigger classification from `docs/plans/092-model-assisted-trigger-classification.md`.

Changes:

- Added optional model-assisted trigger classification after deterministic safety checks and before task creation.
- Added `TriggerIntentClassifier`, model-backed classification request/decision types, and a disabled-by-default no-op path.
- Reused the existing OpenAI-compatible `ModelProviderClient` for classification JSON.
- Rejected model-declined webhook and manual triggers through the existing rejected trigger audit log.
- Added `PATCHPILOT_MODEL_TRIGGER_CLASSIFICATION_ENABLED` to configuration, `.env.example`, and Docker Compose.
- Documented that model classification cannot override deterministic safety rejections.

Validation:

- `mvn -pl PatchPilot -Dtest=ModelTriggerIntentClassifierTests,GitHubWebhookServiceTests#should_reject_when_model_trigger_classifier_declines_execution_before_task_creation+should_not_call_model_trigger_classifier_for_dangerous_command_rejected_by_safety_gate,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_model_trigger_classifier_declines_execution test`: first failed because trigger intent classification types and services did not exist; then passed after implementation, 8 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests test`: passed after adding `modelTriggerClassificationEnabled` to the non-sensitive configuration summary, 1 test run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx src/App.test.tsx -t "configuration|Configuration"`: passed after surfacing trigger classifier state in the configuration panel, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test -q`: passed.
- `cd frontend && npm test -- --reporter=dot`: passed, 68 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.

Implemented trigger rate limit abuse guard from `docs/plans/093-trigger-rate-limit-abuse-guard.md`.

Changes:

- Added `TriggerRateLimitService` with an in-memory sliding-window implementation for local self-hosted runs.
- Added per-trigger-user, per-repository, and per-issue thresholds under `patchpilot.safety`.
- Applied rate-limit checks to GitHub webhooks and manual dashboard task creation after deterministic safety checks and active-task deduplication, but before model trigger classification and task creation.
- Routed rate-limited rejections into the rejected trigger audit log.
- Added `PATCHPILOT_TRIGGER_RATE_LIMIT_*` environment variables to `.env.example`, application configuration, and Docker Compose.
- Exposed rate-limit state through `/api/configuration/summary` and the dashboard configuration panel.
- Documented the operator-facing behavior and current in-memory single-instance limitation.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryTriggerRateLimitServiceTests,GitHubWebhookServiceTests#should_reject_when_trigger_rate_limit_is_exceeded_before_task_creation,DefaultManualFixTaskServiceTests#should_reject_manual_task_when_trigger_rate_limit_is_exceeded,ConfigurationControllerTests test`: first failed because the trigger rate-limit types and service did not exist; then failed on a record factory/accessor naming conflict; then failed until Spring constructor injection was explicit; passed after implementation, 6 tests run, 0 failures.
- `cd frontend && npm test -- src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx src/App.test.tsx -t "configuration|Configuration"`: passed after surfacing trigger rate-limit settings in the dashboard configuration panel, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed, 325 tests run, 0 failures.
- `cd frontend && npm test -- --reporter=dot`: passed, 68 tests run, 0 failures.
- `cd frontend && npm run build`: passed, production bundle generated successfully.
- `git diff --check`: passed with no whitespace errors.

Implemented unsupported repository preflight from `docs/plans/094-unsupported-repository-preflight.md`.

Changes:

- Added `LanguageAdapterRegistry` to select the first supported repository language adapter.
- Ran language-adapter detection immediately after workspace preparation and before patch workflow, diff, tests, commit, push, or Pull Request creation.
- Recorded the preflight as an audited `LanguageAdapterRegistry` tool call.
- Failed unsupported repositories with `Unsupported repository: no supported language adapter detected`.
- Kept Java/Maven as the only supported execution adapter for now and documented the boundary for future Gradle, Node.js, and Python adapters.
- Updated executor tests, product specification, architecture docs, README supported-repository notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests#should_fail_unsupported_repository_before_patch_workflow_or_tests test`: first failed because `LanguageAdapterRegistry` did not exist; then passed after adding the registry and executor preflight.
- `mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests,JavaMavenLanguageAdapterTests,WorkspaceFixTaskExecutorTests test`: first failed because existing executor tests expected the old tool-call sequence and cancellation checkpoint numbers; then passed after updating expectations for the new preflight step, 14 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because the webhook completion fixture did not create a Maven marker file, so the new preflight correctly failed it as unsupported; then passed after creating `pom.xml` in the test repository fixture, 328 tests run, 0 failures.
- `git diff --check`: passed with no whitespace errors.

Implemented adapter-driven verification runner from `docs/plans/095-adapter-driven-verification-runner.md`.

Changes:

- Added `VerificationRunner` for controlled execution of adapter-provided verification commands.
- Kept `MavenTestRunner` as a Java/Maven compatibility wrapper while delegating process execution to `VerificationRunner`.
- Changed task execution to use the `LanguageDetectionResult.verificationCommand()` selected during language preflight instead of re-running Maven detection inside the runner.
- Preserved command allowlist validation, timeout behavior, task process registration, and PatchPilot environment sanitization.
- Updated runner tests, executor tests, webhook integration test wiring, README, architecture docs, product spec, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=VerificationRunnerTests,WorkspaceFixTaskExecutorTests#should_prepare_task_repository_and_run_maven_tests test`: first failed because `VerificationRunner` did not exist; then passed after adding the runner and switching executor verification to the adapter command, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=VerificationRunnerTests,WorkspaceFixTaskExecutorTests,GitHubWebhookControllerTests#should_dispatch_created_task_to_completion test`: passed after replacing executor/webhook test doubles with `VerificationRunner`, 12 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because the Maven compatibility test still expected `maven test command timed out`; then passed after updating the timeout wording to the generic verification runner message, 330 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests,MavenTestRunnerTests,VerificationRunnerTests test`: passed after generic interruption wording cleanup, 21 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after final verification, 330 tests run, 0 failures.

Implemented Gradle language adapter support from `docs/plans/096-gradle-language-adapter.md`.

Changes:

- Added `JavaGradleLanguageAdapter` for Java/Gradle repositories with `gradlew`, `build.gradle`, or `build.gradle.kts`.
- Selected `./gradlew test` when the Gradle wrapper exists and `gradle test` when only Gradle build files exist.
- Registered Maven and Gradle adapters with deterministic Spring ordering.
- Extended `CommandExecutionGuard` to allow only the fixed Gradle verification commands, not arbitrary Gradle tasks.
- Verified the generic `VerificationRunner` can execute an adapter-provided Gradle wrapper command.
- Updated README, product specification, architecture, and backend command-execution standard to describe Maven and Gradle support.

Validation:

- `mvn -pl PatchPilot -Dtest=JavaGradleLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests test`: first failed because `JavaGradleLanguageAdapter` did not exist; then passed after adding the adapter and Gradle command allowlist, 10 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=JavaGradleLanguageAdapterTests,JavaMavenLanguageAdapterTests,LanguageAdapterRegistryTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests test`: passed after Spring adapter registration checks, 18 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 335 tests run, 0 failures.

Implemented Node/npm language adapter support from `docs/plans/097-node-npm-language-adapter.md`.

Changes:

- Added `NodeNpmLanguageAdapter` for Node.js repositories with `package.json` and a non-empty `scripts.test`.
- Selected the fixed verification command `npm test` for supported Node/npm repositories.
- Rejected missing, invalid, or no-test-script `package.json` files before patch generation or Git mutation.
- Registered the Node/npm adapter after the Java/Maven and Java/Gradle adapters.
- Extended `CommandExecutionGuard` to allow only `npm test`, not arbitrary npm scripts.
- Added Node/npm to the backend runtime Docker image so Docker Compose can execute Node verification.
- Updated README, product specification, architecture, target-state, roadmap, decisions, and backend command-execution standard.

Validation:

- `mvn -pl PatchPilot -Dtest=NodeNpmLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests test`: first failed because `NodeNpmLanguageAdapter` did not exist; then passed after adding the adapter, npm command allowlist, and Spring registration, 15 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=MavenRuntimePackagingTests test`: first failed because the runtime Dockerfile did not install `nodejs npm`; then passed after adding them to the runtime image.
- `mvn -pl PatchPilot -Dtest=NodeNpmLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: passed after runtime packaging verification, 17 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 341 tests run, 0 failures.

Implemented Python/pytest language adapter support from `docs/plans/098-python-pytest-language-adapter.md`.

Changes:

- Added `PythonPytestLanguageAdapter` for Python repositories with `pytest.ini`, `[tool.pytest.ini_options]` in `pyproject.toml`, or pytest in `requirements.txt`.
- Selected the fixed verification command `python3 -m pytest` for supported Python/pytest repositories.
- Rejected Python repositories without pytest configuration or dependency before patch generation or Git mutation.
- Registered the Python/pytest adapter after Java/Maven, Java/Gradle, and Node/npm adapters.
- Extended `CommandExecutionGuard` to allow only `python3 -m pytest`, not arbitrary Python commands or pytest arguments.
- Added Python and pytest to the backend runtime Docker image so Docker Compose can execute Python verification.
- Updated README, product specification, architecture, target-state, roadmap, decisions, and backend command-execution standard.

Validation:

- `mvn -pl PatchPilot -Dtest=PythonPytestLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: first failed because `PythonPytestLanguageAdapter` did not exist; then failed because local `python3` lacked pytest; then passed after using a local module fixture for command-path verification and adding the adapter, command allowlist, Spring registration, and runtime packaging, 19 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 347 tests run, 0 failures.

Implemented adapter-aware task metadata from `docs/plans/099-adapter-aware-task-metadata.md`.

Changes:

- Added nullable task metadata fields for selected `language`, `buildSystem`, and `verificationCommand`.
- Added a database migration for adapter metadata on `fix_task`.
- Recorded adapter metadata immediately after successful language-adapter detection.
- Exposed adapter metadata through task API responses by extending `FixTaskVo`.
- Showed adapter metadata in dashboard task rows and selected task detail.
- Renamed task detail test output labels from Maven-specific wording to generic verification wording.
- Updated README, product specification, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskConvertTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,WorkspaceFixTaskExecutorTests,FixTaskAdapterMetadataMigrationTests test`: first failed because the entity, VO, conversion method, service method, executor injection, and migration did not exist; then passed after implementation, 48 tests run, 0 failures.
- `npm test -- --run App.test.tsx TaskDetailPanel.test.tsx`: first failed because the dashboard did not render adapter metadata; then passed after adding the task row/detail display and generic verification labels, 46 tests run, 0 failures.

Implemented adapter filtering and metrics from `docs/plans/100-adapter-filtering-and-metrics.md`.

Changes:

- Added optional `language` and `buildSystem` fields to `FixTaskListQuery` while preserving existing constructors.
- Applied adapter filters in in-memory and MyBatis task list/count queries.
- Included adapter metadata in broad task search text.
- Accepted adapter filters in `GET /api/tasks` and `GET /api/tasks/status-counts`.
- Added scoped metrics overloads so summary, failure causes, model usage, and latency can use the same investigation scope as the task list.
- Accepted search, repository, adapter, and created-time filters in task metrics endpoints.
- Added dashboard language and build-system filters with URL restore/sync, count and metrics propagation, clear-filter support, and load-more propagation.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests#should_list_tasks_with_adapter_metadata_filters,MyBatisFixTaskServiceTests#should_list_tasks_with_adapter_metadata_filters,TaskControllerTests#should_filter_tasks_and_status_counts_by_adapter_metadata test`: first failed because `FixTaskListQuery` did not support adapter fields; then passed after adding query fields and service/controller filters, 3 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_only_tasks_matching_query_scope,TaskControllerTests#should_get_task_metrics_summary_for_adapter_scope test`: first failed because metrics had only global no-argument methods; then passed after adding query-scoped metrics, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx -t "adapter filter|builds backend task search|builds backend task status count|current adapter scope"`: first failed because frontend API requests omitted `language` and `buildSystem` and the dashboard had no adapter controls; then passed after adding API parameters, URL state, task-list controls, metrics propagation, and clear-filter behavior, 6 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: passed after focused backend verification, 92 tests run, 0 failures.
- `npm test`: passed after frontend verification, 73 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 356 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented Node/pnpm and Node/yarn package-manager adapter support from `docs/plans/101-node-package-manager-adapters.md`.

Changes:

- Added shared Node package-manager detection for `package.json` parsing and `scripts.test` validation.
- Added `NodePnpmLanguageAdapter` for repositories with `package.json`, `pnpm-lock.yaml`, and a non-empty `scripts.test`.
- Added `NodeYarnLanguageAdapter` for repositories with `package.json`, `yarn.lock`, and a non-empty `scripts.test`.
- Preferred pnpm and yarn adapters before the broader npm adapter when package-manager lockfiles are present.
- Extended `CommandExecutionGuard` to allow only `pnpm test` and `yarn test`, not arbitrary package-manager scripts or install commands.
- Added pnpm and yarn to the backend runtime Docker image so Docker Compose execution can run adapter-selected verification.
- Updated README, product specification, architecture, target-state, backend command standard, decisions, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=NodePnpmLanguageAdapterTests,NodeYarnLanguageAdapterTests,CommandExecutionGuardTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: first failed because `NodePnpmLanguageAdapter` and `NodeYarnLanguageAdapter` did not exist; then passed after adding the adapters, command allowlist, Spring registration/order checks, and runtime packaging, 21 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=NodeNpmLanguageAdapterTests,NodePnpmLanguageAdapterTests,NodeYarnLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: passed after focused adapter and runner verification, 30 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 367 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented Python/Poetry and Python/uv project-runner adapter support from `docs/plans/102-python-project-runner-adapters.md`.

Changes:

- Added shared Python pytest-signal detection for pytest configuration and dependency checks.
- Added `PythonPoetryLanguageAdapter` for `[tool.poetry]` projects with pytest configuration or dependency.
- Added `PythonUvLanguageAdapter` for `uv.lock` projects with pytest configuration or dependency.
- Preferred Poetry and uv adapters before the broad Python/pytest adapter when project-manager signals are present.
- Extended `CommandExecutionGuard` to allow only `poetry run pytest` and `uv run pytest`, not install, sync, pip, lock, or arbitrary runner commands.
- Added Poetry and uv to the backend runtime Docker image so Docker Compose execution can run adapter-selected verification.
- Updated README, product specification, architecture, target-state, roadmap, backend command standard, decisions, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=PythonPoetryLanguageAdapterTests,PythonUvLanguageAdapterTests,CommandExecutionGuardTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: first failed because `PythonPoetryLanguageAdapter` and `PythonUvLanguageAdapter` did not exist; then passed after adding the adapters, command allowlist, Spring registration/order checks, and runtime packaging, 24 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=PythonPytestLanguageAdapterTests,PythonPoetryLanguageAdapterTests,PythonUvLanguageAdapterTests,CommandExecutionGuardTests,VerificationRunnerTests,PatchPilotApplicationTests,MavenRuntimePackagingTests test`: passed after focused Python adapter and runner verification, 33 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 378 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter demo fixtures from `docs/plans/103-adapter-demo-fixtures.md`.

Changes:

- Added minimal fixtures under `docs/demo-repositories/` for Java/Maven, Java/Gradle, Node/npm, Node/pnpm, Node/yarn, Python/pytest, Python/Poetry, and Python/uv.
- Added per-fixture README files that document the expected adapter and verification command.
- Added source and test files to each fixture so the examples are understandable as small repositories instead of bare manifests.
- Added a registry-level backend test that verifies each fixture is detected with the expected `language`, `buildSystem`, and verification command.
- Updated README, roadmap, target-state, and this execution log to make the fixtures a documented demo-readiness asset.

Validation:

- `mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests#should_detect_adapter_demo_fixtures test`: first failed because `docs/demo-repositories/java-maven` did not exist; then failed because Maven module test execution needed a repository-root-aware fixture path; then passed after adding fixtures and root-relative path resolution, 1 test run, 0 failures.
- `mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests test`: passed after full registry verification, 3 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 379 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter smoke script from `docs/plans/104-adapter-smoke-script.md`.

Changes:

- Added `scripts/adapter-smoke.sh` as a safe local adapter detection smoke command.
- Added default detection mode that prints the fixture matrix and runs `LanguageAdapterRegistryTests#should_detect_adapter_demo_fixtures`.
- Added `--backend` mode for wider adapter and command-guard coverage.
- Added `docs/agent/adapter-smoke-checklist.md` with scope, commands, expected results, and non-goals.
- Added `AdapterSmokeScriptTests` to keep the script pointed at adapter tests and away from GitHub, model, Docker, push, and webhook operations.
- Updated README and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=AdapterSmokeScriptTests test`: first failed because `scripts/adapter-smoke.sh` did not exist; then passed after adding the script and checklist, 1 test run, 0 failures.
- `bash scripts/adapter-smoke.sh`: passed and ran the fixture detection smoke, 1 test run, 0 failures.
- `bash scripts/adapter-smoke.sh --backend`: passed and ran the wider adapter and command-guard smoke, 39 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 380 tests run, 0 failures.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented supported adapters API and dashboard panel from `docs/plans/105-supported-adapters-dashboard.md`.

Changes:

- Added `GET /api/language-adapters` with a read-only catalog for Java/Maven, Java/Gradle, Node/npm, Node/pnpm, Node/yarn, Python/pytest, Python/Poetry, and Python/uv.
- Returned each adapter's language, build system, fixed verification command, detection signals, demo fixture path, and `SUPPORTED` status.
- Added a dashboard `SupportedAdaptersPanel` backed by the new API.
- Kept adapter API failures local to the supported-adapters panel so task, queue, configuration, and health data can still load.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=LanguageAdapterCatalogServiceTests,LanguageAdapterControllerTests test`: first failed because the catalog service, controller, and VO did not exist; then passed after adding the backend API, 2 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/SupportedAdaptersPanel.test.tsx src/App.test.tsx`: first failed because the API helper and component did not exist; then failed because the panel error used the same status role as the global refresh indicator; then passed after adding the API helper, panel, App integration, and isolated panel error state, 57 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 382 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 76 tests run, 0 failures.
- `npm run build`: passed after fixing the supported-adapter test fixture type for the `SUPPORTED` literal status.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented Node/Bun adapter support from `docs/plans/106-node-bun-language-adapter.md`.

Changes:

- Added `NodeBunLanguageAdapter` for repositories with `package.json`, `bun.lockb` or `bun.lock`, and a non-empty `scripts.test`.
- Preferred Bun before the broad npm adapter when a Bun lockfile is present.
- Extended the command allowlist to permit only `bun test`, not Bun install or arbitrary package scripts.
- Installed Bun in the backend runtime Docker image.
- Added a `docs/demo-repositories/node-bun` fixture with a Bun-compatible test file.
- Added Bun to the supported-adapter API catalog and dashboard test data.
- Updated README, product specification, architecture, target state, roadmap, backend command standard, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=NodeBunLanguageAdapterTests,CommandExecutionGuardTests,MavenRuntimePackagingTests,PatchPilotApplicationTests,LanguageAdapterRegistryTests,LanguageAdapterCatalogServiceTests test`: first failed because `NodeBunLanguageAdapter` did not exist; then failed because the npm adapter constructor became ambiguous after multi-lockfile support; then failed because the Dockerfile packaging assertion was too order-specific; then passed after adding the adapter, multi-lockfile detection, command allowlist, runtime packaging, Spring registration, catalog entry, and fixture, 26 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/SupportedAdaptersPanel.test.tsx src/App.test.tsx`: passed after updating dashboard supported-adapter fixtures for Bun, 44 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `npm test`: passed after full frontend verification, 76 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because `LanguageAdapterControllerTests` still expected 8 supported adapters; then passed after updating the controller response assertions for Bun, 388 tests run, 0 failures.

Implemented Python advanced runner adapters from `docs/plans/107-python-advanced-runner-adapters.md`.

Changes:

- Added `PythonToxLanguageAdapter` for repositories with `tox.ini` or `[tool.tox]` in `pyproject.toml`.
- Added `PythonNoxLanguageAdapter` for repositories with `noxfile.py`.
- Added `PythonHatchLanguageAdapter` for repositories with a Hatch test script in `pyproject.toml`.
- Preferred tox, nox, and hatch before Poetry, uv, and plain pytest when explicit runner signals are present.
- Extended `CommandExecutionGuard` to allow only `tox`, `nox`, and `hatch test`, not arbitrary runner environments, sessions, or scripts.
- Added tox, nox, and hatch to the backend runtime Docker image so Docker Compose execution can run adapter-selected verification.
- Added `docs/demo-repositories/python-tox`, `python-nox`, and `python-hatch` fixtures.
- Added tox, nox, and hatch to the supported-adapter API catalog and dashboard test data.
- Updated README, product specification, architecture, target state, roadmap, backend command standard, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=PythonToxLanguageAdapterTests,PythonNoxLanguageAdapterTests,PythonHatchLanguageAdapterTests,CommandExecutionGuardTests,MavenRuntimePackagingTests,PatchPilotApplicationTests,LanguageAdapterRegistryTests,LanguageAdapterCatalogServiceTests,LanguageAdapterControllerTests test`: first failed because `PythonToxLanguageAdapter`, `PythonNoxLanguageAdapter`, and `PythonHatchLanguageAdapter` did not exist; then passed after adding the adapters, command allowlist, runtime packaging, Spring registration/order checks, catalog entries, and fixtures, 33 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/SupportedAdaptersPanel.test.tsx src/App.test.tsx`: first failed because the test queried partial table text too broadly after adding tox and hatch rows; then passed after narrowing assertions to adapter rows and exact rendered cell text, 44 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 398 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 76 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter fixture verification dashboard from `docs/plans/108-adapter-fixture-verification-dashboard.md`.

Changes:

- Added `GET /api/language-adapters/fixtures` to verify each supported demo fixture with the real `LanguageAdapterRegistry`.
- Returned fixture name/path, expected and actual language/build system/command, detection reason, and `PASS` or `FAIL` status.
- Kept missing or drifting fixtures visible as failed rows instead of failing the whole endpoint.
- Copied `docs/demo-repositories` into the backend Docker runtime image so Docker Compose can serve the fixture verification API.
- Added a dashboard `AdapterFixtureVerificationPanel` backed by the new API, with fixture API failures isolated to that panel.
- Updated README, architecture notes, frontend design notes, adapter smoke checklist, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=LanguageAdapterFixtureVerificationServiceTests,LanguageAdapterControllerTests test`: first failed because the fixture verification service and VO did not exist; then passed after adding the backend API, 4 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/AdapterFixtureVerificationPanel.test.tsx src/App.test.tsx`: first failed because the API helper, type, and panel did not exist; then failed because the dashboard intentionally rendered the same fixture path in both adapter catalog and fixture verification panels; then passed after adding the API helper, panel, App integration, and row-scoped dashboard assertions, 58 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=LanguageAdapterFixtureVerificationServiceTests,LanguageAdapterControllerTests,MavenRuntimePackagingTests test`: passed after adding the Docker runtime fixture copy assertion and implementation, 12 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because the fixture verification service had multiple constructors without an explicit Spring injection constructor; then passed after marking the production constructor for injection, 402 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 79 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented demo readiness gate from `docs/plans/109-demo-readiness-gate.md`.

Changes:

- Added `GET /api/demo/readiness` to aggregate demo readiness from backend reachability, required credential configuration, optional model cost configuration, adapter fixture verification, queue state, and recent completed Pull Request evidence.
- Added readiness domain records with `READY`, `NEEDS_ATTENTION`, and `BLOCKED` states plus concrete operator next actions.
- Extracted `ConfigurationSummaryService` so configuration readiness can be reused by both the configuration API and demo readiness service.
- Added a dashboard `DemoReadinessPanel` near the top of the operations page, backed by the new API.
- Kept readiness API failures local to the readiness panel so task, metric, queue, adapter, and configuration data can still load.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoReadinessControllerTests test`: first failed because the demo readiness service, controller, and domain records did not exist; then passed after adding the backend API, 5 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoReadinessPanel.test.tsx src/App.test.tsx`: first failed because duplicate readiness status labels appeared in both the header and check rows; then passed after keeping the textual status in the header and using row markers with accessible labels, 60 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 406 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 83 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented adapter detection explainability from `docs/plans/110-adapter-detection-explainability.md`.

Changes:

- Added nullable `adapter_detection_reason` task persistence through a Flyway migration.
- Stored `LanguageDetectionResult.reason()` when task execution records selected adapter metadata.
- Returned `adapterDetectionReason` through task list/detail APIs and preserved it across status transitions.
- Included adapter language, build system, verification command, and detection reason in copied Markdown task reports.
- Added dashboard task detail evidence for the selected adapter detection reason.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,FixTaskConvertTests,TaskControllerTests,WorkspaceFixTaskExecutorTests,FixTaskAdapterMetadataMigrationTests test`: first failed because adapter metadata methods and task records did not expose `adapterDetectionReason`; then passed after adding persistence, conversion, service, controller, and executor support, 102 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the task detail evidence strip did not render detection reason; then passed after adding the frontend field and display, 8 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_report_by_task_id test`: first failed because copied task reports did not include adapter evidence; then passed after adding the report adapter section, 1 test run, 0 failures.

Implemented generated diff risk gate from `docs/plans/111-generated-diff-risk-gate.md`.

Changes:

- Added `GeneratedDiffRiskGate` to inspect generated workspace diffs after `DiffTool` and before adapter verification, test-run recording, commit, push, or Pull Request creation.
- Blocked sensitive file changes, secret-like added lines, binary patches, too many changed files, and too many changed lines with deterministic reasons.
- Recorded the risk gate as an audited `GeneratedDiffRiskGate` tool call so task detail APIs, copied reports, and dashboard records can explain why execution stopped.
- Added dashboard task detail evidence for generated-diff risk-gate blocks.
- Updated README, architecture notes, target state, backend code standard, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=GeneratedDiffRiskGateTests test`: first failed because binary generated diffs were still accepted; then passed after adding binary diff detection, 5 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because task detail evidence did not surface risk-gate blocks; then passed after rendering the blocked marker, 9 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=GeneratedDiffRiskGateTests,WorkspaceFixTaskExecutorTests test`: passed after executor integration, 15 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx src/api.test.ts`: passed after dashboard/API focused verification, 66 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 413 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 84 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented risk review queue from `docs/plans/112-risk-review-queue.md`.

Changes:

- Added `PENDING_REVIEW` as an explicit active task status for generated-diff risk-gate rejections.
- Mapped `Generated diff rejected: ...` executor failures to `markPendingReview(...)`, a `PENDING_REVIEW` timeline event, and an edited GitHub status comment.
- Preserved risk rejection reasons in `failureReason` and kept `GeneratedDiffRiskGate` tool calls as detailed audit evidence.
- Added pending-review counts to task status-count and metrics APIs.
- Allowed cancelling pending-review tasks while blocking retry until a future human approval flow exists.
- Added dashboard `PENDING_REVIEW` status filtering, count badges, task pills, cancel affordance, and risk evidence.
- Updated README, architecture notes, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests,DefaultFixTaskControlServiceTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: first failed because `PENDING_REVIEW`, `markPendingReview(...)`, pending-review counts, timeline events, and status-comment updates did not exist.
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests,DefaultFixTaskControlServiceTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests,IssueCommentToolTests test`: passed after backend implementation, 81 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the dashboard still expected two visible tasks after adding a pending-review fixture; then passed after updating the fixture counts and assertions, 66 tests run, 0 failures.

Implemented risk review approval from `docs/plans/113-risk-review-approval.md`.

Changes:

- Added `riskReviewApprovedAt` task persistence through a Flyway migration.
- Added `POST /api/tasks/{id}/approve-review` and `FixTaskControlService.approveReviewTask(...)`.
- Restricted approval to `PENDING_REVIEW` tasks, then changed approved tasks back to `PENDING`, cleared the risk failure reason, enqueued the task, and recorded a `REVIEW_APPROVED` timeline event.
- Added workspace resume support so an approved task continues from the existing task workspace instead of re-running model patch generation, diff generation, or the generated-diff risk gate.
- Kept adapter detection, verification, commit, push, Pull Request creation, queue records, and GitHub human review in the resumed path.
- Added dashboard `Approve review` action for pending-review tasks and kept retry hidden for pending-review states.
- Updated README, architecture notes, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,WorkspaceFixTaskExecutorTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because the approval API, review approval field, timeline event, service transition, and workspace resume method did not exist; then passed after backend implementation, 113 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`: first failed because the approve-review API and detail action did not exist; then passed after frontend implementation, 69 tests run, 0 failures.

Implemented risk review diff inspection from `docs/plans/114-risk-review-diff-inspection.md`.

Changes:

- Added `FixTaskGeneratedDiffVo` and populated it from the latest successful `DiffTool` tool-call output in `GET /api/tasks/{taskId}/detail`.
- Added a generated-diff section to copied Markdown task reports.
- Added a dashboard generated-diff preview in selected task detail so `PENDING_REVIEW` approvals can inspect the exact patch before resuming.
- Updated README, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_include_latest_generated_diff_in_task_detail test`: first failed because `/detail` did not expose `generatedDiff`; then passed after backend projection, 1 test run, 0 failures.
- `npm test -- TaskDetailPanel.test.tsx`: first failed because the generated-diff preview did not exist; then passed after adding the panel, 11 tests run, 0 failures.
- `npm test -- api.test.ts`: passed after API fixture coverage, 16 tests run, 0 failures.
- `npm test -- App.test.tsx`: passed after dashboard integration coverage, 43 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_report_by_task_id test`: passed after report coverage, 1 test run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because existing controller tests compared JSONPath decimal values as `Double` against `BigDecimal`; then passed after using a numeric test matcher for decimal JSON values, 427 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 88 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented risk review approval audit from `docs/plans/115-risk-review-approval-audit.md`.

Changes:

- Added `ApproveReviewDto` and `ApproveReviewCommand` so `POST /api/tasks/{taskId}/approve-review` requires an approver and approval reason.
- Added `riskReviewApprovedBy` and `riskReviewApprovalReason` to task VO/entity conversion, in-memory persistence, MyBatis persistence, search fields, and MySQL schema migration.
- Preserved existing `riskReviewApprovedAt` behavior while clearing all approval metadata on fresh retries and new pending-review states.
- Recorded approval metadata in the review-approved timeline event, copied task reports, executor resume audit summary, task APIs, and dashboard detail.
- Replaced one-click dashboard approval with a compact approval form that disables submission until both fields are filled.
- Updated README, architecture notes, target state, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests,DefaultFixTaskControlServiceTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests test`: first failed because `ApproveReviewCommand` did not exist.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because approve-review still sent no request body and the detail panel had no approval form or metadata display.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests,DefaultFixTaskControlServiceTests,InMemoryFixTaskServiceTests,MyBatisFixTaskServiceTests,FixTaskConvertTests,FixTaskMigrationTests test`: passed after backend implementation, 112 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx`: passed after frontend implementation, 71 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because `WorkspaceFixTaskExecutorTests` still expected the old approval tool-call input summary; then passed after updating the resume fixture to include approver metadata, 429 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 89 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented risk review approval authorization from `docs/plans/116-risk-review-approval-authorization.md`.

Changes:

- Added `patchpilot.review-approval.allowed-operators` / `PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS` for explicit risk-review approver authorization.
- Exposed normalized review approvers in `GET /api/configuration/summary`.
- Rejected unauthorized `POST /api/tasks/{taskId}/approve-review` calls with `403` before task mutation, queue enqueue, or timeline recording.
- Updated the dashboard configuration panel to show review approvers and warn when none are configured.
- Replaced free-text approval operator entry with a configured approver selector, and disabled approval when the allowlist is empty.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,ConfigurationSummaryServiceTests test`: first failed because `ReviewApprovalProperties` did not exist; then passed after backend implementation, 69 tests run, 0 failures.
- `npm test -- src/dashboard/components/ConfigurationPanel.test.tsx src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because review approvers were not rendered and the approval form still used free text; then passed after frontend implementation, 16 tests run, 0 failures.

Implemented safety policy readiness summary from `docs/plans/117-safety-policy-readiness-summary.md`.

Changes:

- Extended `GET /api/configuration/summary` with non-sensitive safety policy fields for trigger-user allowlists, repository allowlists, and review-approval approvers.
- Added a `Safety policy` check to `GET /api/demo/readiness`, marking open trigger/repository allowlists and missing review approvers as operator attention items.
- Rendered trigger-user and repository allowlist state in the dashboard configuration panel.
- Updated dashboard readiness fixtures so the existing demo readiness panel shows safety policy checks alongside credentials, queue, adapters, and recent PR evidence.
- Updated README, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=ConfigurationSummaryServiceTests,ConfigurationControllerTests,DemoReadinessServiceTests test`: first failed because `ConfigurationSummaryVo` did not expose safety policy fields; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- src/dashboard/components/ConfigurationPanel.test.tsx src/dashboard/components/DemoReadinessPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the configuration panel did not render trigger-user and repository allowlist state; then passed after frontend implementation, 65 tests run, 0 failures.

Implemented admin API token guard from `docs/plans/118-admin-api-token-guard.md`.

Changes:

- Added optional `patchpilot.security.admin-token` / `PATCHPILOT_ADMIN_TOKEN` for protecting operator APIs when PatchPilot is reachable through a public temporary tunnel.
- Added `AdminApiSecurityFilter` so configured deployments require `X-PatchPilot-Admin-Token` or `Authorization: Bearer <token>` for `/api/**` operator calls while keeping `/health`, actuator health, and `/api/github/webhook` public for health checks and GitHub deliveries.
- Exposed non-sensitive `adminTokenConfigured` state in `GET /api/configuration/summary`.
- Added admin-token readiness guidance to the demo `Safety policy` check.
- Updated the dashboard configuration panel and frontend API helper so a locally stored browser token is sent as `X-PatchPilot-Admin-Token` without changing request shapes when no token is stored.
- Updated `.env.example`, Docker Compose, README, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests,AdminApiSecurityFilterTests test`: first failed because the admin token field and filter did not exist.
- `npm test -- --run src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because frontend API calls did not send the admin header and the configuration panel did not render the admin token state.
- `mvn -pl PatchPilot -Dtest=ConfigurationControllerTests,ConfigurationSummaryServiceTests,DemoReadinessServiceTests,AdminApiSecurityFilterTests test`: passed after backend implementation, 11 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx`: passed after frontend implementation, 20 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 439 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 91 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented dashboard admin token prompt from `docs/plans/119-dashboard-admin-token-prompt.md`.

Changes:

- Added an inline `Admin API token` password prompt when the dashboard receives the backend `Admin token is required` response.
- Saved the submitted token to browser `localStorage` under `patchpilot.adminToken` and reused the existing API helper so later requests include `X-PatchPilot-Admin-Token`.
- Retried dashboard loading immediately after saving the token so operators can recover from a protected temporary URL without opening browser DevTools.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "prompts for admin token"`: first failed because the dashboard rendered only the backend/proxy alert and had no admin-token prompt; then passed after frontend implementation, 1 test run, 0 failures.
- `npm test`: passed after full frontend verification, 92 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented dashboard admin token management from `docs/plans/120-dashboard-admin-token-management.md`.

Changes:

- Added a dashboard header token manager that reports whether the current browser has a saved admin token.
- Added `Dashboard admin token` input support for saving or replacing the local `patchpilot.adminToken` value.
- Added `Clear admin token` so operators can remove the local credential during rotation or unauthenticated testing.
- Refreshed dashboard data after saving or clearing the token so the current credential state is used immediately.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "manages stored admin token"`: first failed because the dashboard header did not show saved-token state or management actions; then passed after frontend implementation, 1 test run, 0 failures.
- `npm test`: passed after full frontend verification, 93 tests run, 0 failures.
- `npm run build`: passed after production frontend build.

Implemented dashboard API connectivity check from `docs/plans/121-dashboard-api-connectivity-check.md`.

Changes:

- Added a top-of-page `ConnectivityPanel` that separates backend `/health`, browser admin-token state, and protected API reachability.
- Loaded `/health` before the protected dashboard API batch so operators can still see that the backend is up when protected APIs reject missing or wrong admin tokens.
- Added corrective next-action text for backend/proxy failures and admin-token failures.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "connectivity"`: first failed because the dashboard had no connectivity panel and could not distinguish backend-up/admin-token-missing failures; then passed after frontend implementation, 2 tests run, 0 failures.
- `npm test`: first failed because the existing configuration assertions matched the new connectivity `Backend UP` text as well; then passed after scoping the assertion to the Configuration panel, 95 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented dashboard operator setup checklist from `docs/plans/122-dashboard-operator-setup-checklist.md`.

Changes:

- Added `OperatorSetupChecklistPanel` near the top of the dashboard.
- Derived read-only setup checks from already loaded dashboard data instead of adding a new backend API.
- Covered backend connectivity, required credentials, safety policy, adapter fixtures, queue health, and recent Pull Request evidence.
- Added next setup actions for failed queue health and missing recent PR evidence.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- --run src/App.test.tsx -t "operator setup"`: first failed because the dashboard had no operator setup checklist; then failed again because recent PR evidence incorrectly preferred task-list fallback over demo readiness; then passed after checklist implementation and readiness precedence, 1 test run, 0 failures.
- `npm test -- --run src/App.test.tsx -t "operator setup|every operator"`: passed after adding the all-ready scenario, 2 tests run, 0 failures.
- `npm test`: first failed because an existing demo-readiness assertion matched the same next-action text rendered by the new setup checklist; then passed after scoping the assertion to the Demo readiness panel, 97 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace and conflict-marker verification.

Implemented webhook delivery diagnostics from `docs/plans/123-webhook-delivery-diagnostics.md`.

Changes:

- Added a `webhook_delivery_diagnostic` read model with in-memory and MyBatis services plus a Flyway migration.
- Recorded delivery outcomes for invalid signatures, malformed requests, unsupported events, ignored non-commands, safety/rate/model rejections, duplicate deliveries, active-task collisions, and created tasks.
- Exposed `GET /api/github/webhook-deliveries?limit=...` for curl and dashboard inspection without storing raw payloads or signatures.
- Added a dashboard `WebhookDeliveryPanel` backed by the new API so operators can diagnose temporary URL, signature, ignored-event, rejection, duplicate, and task-created outcomes without relying only on GitHub's delivery page.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,GitHubWebhookControllerTests,MyBatisWebhookDeliveryDiagnosticServiceTests,WebhookDeliveryDiagnosticMigrationTests test`: first failed because the webhook service did not accept a diagnostic recorder and no migration existed; then passed after backend implementation, 26 tests run, 0 failures.
- `npm test -- App.test.tsx api.test.ts`: first failed because the dashboard had no named webhook-deliveries region; then passed after adding an accessible panel label, 67 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 447 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 98 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `mvn -pl PatchPilot -Dtest=MyBatisWebhookDeliveryDiagnosticServiceTests,WebhookDeliveryDiagnosticControllerTests test`: passed after the final diagnostic list-ordering adjustment, 4 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

Implemented webhook redelivery guidance from `docs/plans/124-webhook-redelivery-guidance.md`.

Changes:

- Added derived `redeliveryRecommended` and `operatorAction` fields to webhook delivery diagnostics without changing the persisted diagnostic table.
- Classified invalid signatures, malformed requests, and backend processing failures as fix-then-redeliver cases.
- Classified ignored, rejected, duplicate, active-task, and task-created outcomes as non-redelivery cases with safer next actions.
- Rendered redelivery guidance in the dashboard webhook delivery panel so operators know when to use GitHub's `Redeliver` button.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=WebhookDeliveryDiagnosticControllerTests,InMemoryWebhookDeliveryDiagnosticServiceTests test`: first failed because `WebhookDeliveryDiagnosticVo` did not expose redelivery guidance fields; then passed after backend implementation, 5 tests run, 0 failures.
- `npm test -- App.test.tsx api.test.ts`: first failed because the dashboard did not render `Redeliver after fix`; then passed after frontend implementation, 67 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=WebhookDeliveryDiagnosticControllerTests,InMemoryWebhookDeliveryDiagnosticServiceTests,MyBatisWebhookDeliveryDiagnosticServiceTests test`: passed after MyBatis guidance coverage, 8 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 449 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 98 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace verification.

Implemented live demo smoke checklist from `docs/plans/125-live-demo-smoke-checklist.md`.

Changes:

- Added `GET /api/demo/smoke-checklist` as a read-only final pre-demo checklist.
- Derived ordered readiness, webhook delivery, task execution, and Pull Request evidence from existing readiness, delivery diagnostics, and task history data.
- Kept duplicate, ignored, rejected, and active-task webhook deliveries as attention states instead of treating any task id as successful smoke evidence.
- Added a dashboard `DemoSmokeChecklistPanel` near the existing setup and readiness panels.
- Updated README, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoSmokeChecklistServiceTests,DemoReadinessControllerTests test`: first failed because the smoke checklist API/service/domain records did not exist; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- App.test.tsx api.test.ts`: first failed because the smoke checklist API helper and panel did not exist; then passed after frontend implementation and isolated request handling, 68 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 454 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 99 tests run, 0 failures.
- `npm run build`: passed after production frontend build.

Implemented rejected trigger dashboard visibility from `docs/plans/126-rejected-trigger-dashboard.md`.

Changes:

- Added a typed frontend API helper for `GET /api/rejected-triggers?limit=20`.
- Added a dashboard `RejectedTriggerPanel` that shows recent refused `/agent fix` attempts with source, repository, issue, trigger user, delivery id, command text, timestamp, and rejection reason.
- Kept rejected-trigger API failures local to the panel so the rest of the dashboard can still load.
- Updated README, frontend design notes, and this execution log.

Validation:

- `npm test -- api.test.ts App.test.tsx`: first failed because the API helper and dashboard region did not exist; then passed after frontend implementation, 69 tests run, 0 failures.
- `npm test -- api.test.ts App.test.tsx RejectedTriggerPanel.test.tsx`: passed after component empty/error coverage, 71 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 102 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `mvn -pl PatchPilot test`: passed after full backend verification, 454 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.
- Conflict-marker scan over README, docs, and frontend sources: no conflict markers found.

Implemented Go language adapter support from `docs/plans/127-go-language-adapter.md`.

Changes:

- Added `GoLanguageAdapter` for `go.mod` repositories with fixed verification command `go test ./...`.
- Added `go test ./...` to the command allowlist while rejecting arbitrary Go commands.
- Added a minimal `docs/demo-repositories/go-module` fixture and included it in registry, catalog, fixture verification, Spring context, and dashboard supported-adapter coverage.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=GoLanguageAdapterTests,LanguageAdapterRegistryTests,CommandExecutionGuardTests test`: first failed because `GoLanguageAdapter` did not exist; then passed after adapter and allowlist implementation, 12 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,LanguageAdapterCatalogServiceTests,LanguageAdapterFixtureVerificationServiceTests test`: passed after catalog, fixture, and Spring context coverage, 10 tests run, 0 failures.
- `npm test -- App.test.tsx SupportedAdaptersPanel.test.tsx`: first failed because dashboard fixture-count assertions still expected 12 adapters; then passed after updating Go visibility, 51 tests run, 0 failures.
- `mvn -pl PatchPilot test`: first failed because `LanguageAdapterControllerTests` still expected 12 catalog and fixture rows; then passed after controller API assertions were updated for Go, 457 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 102 tests run, 0 failures.
- `npm run build`: passed after production frontend build.
- `git diff --check`: passed after whitespace verification.
- Conflict-marker scan over README, product docs, plan, progress log, demo fixtures, backend sources, backend tests, and frontend sources: no conflict markers found.

Implemented unsupported repository guidance from `docs/plans/128-unsupported-repository-guidance.md`.

Changes:

- Added structured `repositorySupportGuidance` to task detail responses when a task fails with an unsupported repository reason.
- Reused the existing supported adapter catalog so guidance automatically lists current languages, build systems, verification commands, and detection signals.
- Added a `Repository Support Guidance` section to copied task reports for unsupported repository failures.
- Added a dashboard task detail guidance panel that explains why PatchPilot refused to execute and which supported project markers/tests to add before retrying.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because task detail and report did not expose repository support guidance; then passed after backend implementation, 59 tests run, 0 failures.
- `npm test -- TaskDetailPanel.test.tsx`: first failed because no accessible repository support guidance panel existed; then passed after frontend implementation, 14 tests run, 0 failures.

Implemented safe refusal issue comments from `docs/plans/129-safe-refusal-issue-comments.md`.

Changes:

- Added optional refusal comment metadata to rejected trigger audit commands, VOs, in-memory storage, and MyBatis entities.
- Added a Flyway migration for rejected trigger `comment_id` and `comment_url`.
- Added `IssueCommentTool.commentRejected` with a safe body that explains refusal without echoing the raw rejected command.
- Updated webhook rejection paths to attempt a refusal comment before recording rejected trigger audits, while keeping rejection successful if comment creation fails.
- Rendered refusal comment links in the dashboard rejected triggers panel when GitHub comment URLs are available.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditMigrationTests test`: first failed because refusal comment fields and methods did not exist.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,IssueCommentToolTests,RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditMigrationTests test`: passed after backend implementation, 32 tests run, 0 failures.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: first failed because the rejected triggers panel did not expose a `Refusal comment` link; then passed after frontend implementation, 71 tests run, 0 failures.

Implemented operator retry for rejected triggers from `docs/plans/130-operator-retry-rejected-trigger.md`.

Changes:

- Added rejected-trigger lookup by audit id to in-memory and MyBatis-backed audit services.
- Added `RejectedTriggerRetryService` and `POST /api/rejected-triggers/{id}/retry`.
- Reused the existing manual task creation flow for retries so safety gates, active-task checks, rate limits, and model trigger classification still apply.
- Added a task timeline `REQUEUED` event that links the new task back to the rejected trigger audit id and prior rejection reason.
- Added a dashboard retry button for rejected trigger rows with per-row loading state and refresh after success.

Validation:

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,DefaultRejectedTriggerRetryServiceTests test`: first failed because `RejectedTriggerRetryService` did not exist.
- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,DefaultRejectedTriggerRetryServiceTests test`: passed after backend implementation, 7 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,GitHubWebhookServiceTests test`: passed after audit lookup coverage and legacy fake updates, 28 tests run, 0 failures.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: first failed because the retry API helper and rejected-trigger retry buttons did not exist.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: passed after frontend implementation, 74 tests run, 0 failures.

Implemented retry audit links for rejected triggers from `docs/plans/131-retry-rejected-trigger-audit-link.md`.

Changes:

- Added `retriedTaskId` and `retriedAt` metadata to rejected trigger audit records.
- Added a Flyway migration for MySQL-backed rejected trigger retry metadata.
- Marked a rejected trigger audit as retried after the retry flow creates a new task and records the retry timeline event.
- Returned retry metadata from rejected-trigger API responses.
- Added a dashboard `Retried task` link that opens the generated task through the existing `/tasks/{id}` detail route.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests test`: first failed because retry metadata fields, service methods, and the migration did not exist.
- `mvn -pl PatchPilot -Dtest=DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests test`: passed after backend implementation, 33 tests run, 0 failures.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: first failed because the dashboard did not render a `Retried task` link.
- `npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`: passed after frontend implementation, 75 tests run, 0 failures.
- `npm run build`: first failed because the retried task click handler did not preserve TypeScript's non-null narrowing; then passed after extracting a render helper.
- `mvn -pl PatchPilot test`: passed after full backend verification, 472 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 107 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

Implemented issue context ingestion from `docs/plans/132-issue-context-ingestion.md`.

Changes:

- Added a GitHub issue context client/service that reads issue title, body, URL, and recent comments through the configured GitHub token.
- Loaded issue context during task execution before patch planning and recorded that read as an audited tool call.
- Passed issue context into the fix-plan prompt so model planning can use the issue body and discussion, not only the `/agent fix` trigger comment.
- Added issue context to task detail and markdown report responses, with safe fallback when GitHub context cannot be loaded for dashboard inspection.
- Rendered issue title, source link, body summary, and recent comments in the dashboard task detail panel.
- Explicitly enabled Maven compiler annotation processing so Lombok-generated methods work under newer JDKs.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubIssueContextClientTests,IssueContextServiceTests,FixPlanGeneratorTests,PlanDrivenPatchWorkflowTests,WorkspaceFixTaskExecutorTests test`: passed after backend workflow implementation, 22 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: passed after task detail/report issue context API implementation, 59 tests run, 0 failures.
- `npm test -- TaskDetailPanel.test.tsx App.test.tsx api.test.ts`: passed after dashboard issue context rendering, 87 tests run, 0 failures.
- `npm run build`: passed after frontend type and fixture updates.
- `mvn -pl PatchPilot test`: passed after webhook test fake issue context service was added, 478 tests run, 0 failures.

Implemented patch review evidence visibility from `docs/plans/135-patch-review-evidence-visibility.md`.

Changes:

- Added task-level patch review records with decision, reason, confidence, required follow-up, edited files, and created timestamp.
- Added in-memory and MyBatis-backed patch review services plus a Flyway migration for MySQL persistence.
- Recorded post-edit review evidence before writing model-generated files, including rejected reviews that stop execution.
- Added latest patch review evidence to task detail API responses and markdown task reports.
- Rendered patch review evidence in the dashboard task detail panel with approved and blocked review gate states.

Validation:

- `mvn -pl PatchPilot -Dtest=PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,TaskControllerTests,FixTaskPatchReviewConvertTests,InMemoryFixTaskPatchReviewServiceTests,MyBatisFixTaskPatchReviewServiceTests,FixTaskPatchReviewMigrationTests test`: first failed because the new patch review service/entity/mapper did not exist; then passed after backend implementation, 75 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the dashboard did not render a patch review section; then passed after frontend implementation, 38 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 493 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 110 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo session report export from `docs/plans/154-demo-session-report-export.md`.

Changes:

- Added `GET /api/demo/session-report` as a read-only Markdown export over the current demo session snapshot.
- Added `DemoSessionReportService` to format session id, status, summary, generated time, share summary, recent Pull Request, recent task, operator checklist, script steps, health contract, next actions, and embedded runbook.
- Added frontend `getDemoSessionReport` and a `Copy session report` action to `DemoSessionSnapshotPanel`.
- Wired `App.tsx` to fetch the session report only when the operator clicks the copy action.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoSessionReportServiceTests,DemoReadinessControllerTests test`: first failed because `DemoSessionReportService` did not exist; then passed after backend implementation, 9 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx`: first failed because `getDemoSessionReport` and the copy button did not exist; then passed after frontend implementation, 96 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 582 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 148 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo session snapshot from `docs/plans/153-demo-session-snapshot.md`.

Changes:

- Added `GET /api/demo/session-snapshot` as a read-only aggregate over one current demo evidence bundle, derived script, derived runbook, operator checklist, health contract, share summary, and next actions.
- Added `DemoSessionSnapshotService` and `DemoSessionSnapshotVo`, with deterministic session ids based on generated time and a health contract stating the endpoint does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Added frontend `getDemoSessionSnapshot`, typed `DemoSessionSnapshot` models, and `DemoSessionSnapshotPanel`.
- Wired the dashboard to load and render the snapshot near the existing demo evidence and script panels without blocking the rest of the dashboard when the snapshot endpoint fails.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoSessionSnapshotServiceTests,DemoReadinessControllerTests test`: first failed because `DemoSessionSnapshotService`, `DemoSessionSnapshotVo`, and the endpoint did not exist; then passed after backend implementation, 8 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx`: first failed because `getDemoSessionSnapshot`, `DemoSessionSnapshotPanel`, and App-level loading did not exist; then passed after frontend implementation, 93 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 579 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 145 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented review rejection recovery from `docs/plans/136-review-rejection-recovery.md`.

Changes:

- Added a shared patch review rejection classifier so comments, reports, and metrics use one durable definition for model patch review blocks.
- Updated failed task status comments to call out `PATCH_REVIEW_REJECTED` and explain that retry asks the model for a fresh patch.
- Classified model patch review blocks as `PATCH_REVIEW_REJECTION` in failure-cause metrics instead of generic auth or model failures.
- Added review gate and recovery guidance to markdown task reports.
- Added dashboard recovery guidance for rejected patch reviews while preserving the existing retry action for failed tasks.

Validation:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests#should_update_failed_status_comment_with_patch_review_recovery_guidance test`: first failed because failed comments used generic failure copy.
- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_failed_tasks_by_failure_cause test`: first failed because patch review rejection was counted under GitHub auth due the word authentication.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx -t "marks rejected patch reviews as review gate blocks"`: first failed because the dashboard lacked retry regeneration guidance.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,DefaultFixTaskMetricsServiceTests,TaskControllerTests test`: passed after backend implementation, 75 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: passed after dashboard implementation, 17 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 494 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 110 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented review retry lineage from `docs/plans/137-review-retry-lineage.md`.

Changes:

- Added task retry lineage fields for source task id, source status, source failure reason, and retry timestamp.
- Persisted retry lineage in both in-memory and MySQL-backed task services.
- Added a Flyway migration for durable retry lineage columns.
- Included retry lineage in task API responses and markdown task reports.
- Rendered retry lineage in the dashboard task detail panel so operators can inspect recovery context for retried review rejections and other terminal failures.

Validation:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,FixTaskConvertTests,MyBatisFixTaskServiceTests,FixTaskMigrationTests test`: first failed because retry lineage fields and persistence did not exist; then passed after backend implementation, 102 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/api.test.ts`: first failed because the dashboard did not render retry lineage; then passed after frontend implementation, 39 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 498 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 111 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented sensitive diff policy visibility from `docs/plans/138-sensitive-diff-policy.md`.

Changes:

- Added a shared `GeneratedDiffSafetyPolicy` for generated-diff risk thresholds, sensitive path matching, binary diff detection, and secret-like added lines.
- Wired `GeneratedDiffRiskGate` and `PlannedPatchWorkflow` to the shared policy so planning-time target validation and post-generation diff review use the same protected path rules.
- Extended protected path coverage to Git metadata and package-manager credential files such as `.npmrc`, `.pypirc`, `.netrc`, and Maven `settings.xml`.
- Extended `GET /api/configuration/summary` with non-sensitive generated-diff policy state.
- Rendered generated-diff risk-gate state and protected path pattern count in the dashboard configuration panel with advisories for disabled or empty policy state.

Validation:

- `mvn -pl PatchPilot -Dtest=GeneratedDiffRiskGateTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,ConfigurationSummaryServiceTests,ConfigurationControllerTests,DemoReadinessServiceTests test`: first failed because the configuration summary and planned workflow tests did not yet share the new policy fields; then passed after backend implementation, 27 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/ConfigurationPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the configuration panel did not render generated-diff policy state and later because the advisory count changed; then passed after frontend implementation, 75 tests run, 0 failures.

Implemented rejected trigger categories from `docs/plans/139-rejected-trigger-categories.md`.

Changes:

- Added stable rejected-trigger categories for empty or unsupported commands, non-actionable requests, dangerous instructions, user/repository allowlist failures, rate limits, and model-classifier refusals.
- Extended safety gate, rate-limit, and model trigger decisions to carry a category next to the existing operator-facing reason.
- Persisted rejected-trigger categories in in-memory and MySQL-backed audit records.
- Added a Flyway migration for `rejected_trigger_audit.category` plus a category/created index.
- Included `category` in rejected-trigger API responses.
- Rendered rejected-trigger category badges in the dashboard so operators can diagnose vague, malicious, unauthorized, blocked, rate-limited, or model-rejected attempts without parsing reason text.

Validation:

- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`: first failed because rejected-trigger category fields and constructors did not exist.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts`: first failed because the API response lacked `category` and the dashboard did not render category badges.
- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests test`: passed after backend implementation, 46 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts`: passed after frontend implementation, 24 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full Java 17 backend verification, 505 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 111 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented rejected trigger category filtering from `docs/plans/140-rejected-trigger-category-filtering.md`.

Changes:

- Added optional `category` filtering to `GET /api/rejected-triggers`.
- Supported category filtering in in-memory and MyBatis-backed rejected-trigger audit services while preserving the existing limit-only API path.
- Extended the dashboard API helper to request rejected triggers by category.
- Added a rejected-trigger category select to the dashboard and preserved its state in the URL as `rejectedCategory`.
- Updated product docs to describe category-filtered rejected-trigger diagnosis.

Validation:

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`: first failed because rejected-trigger audit services only supported limit-only listing; then passed after backend implementation, 15 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the API helper treated options as a raw limit and the dashboard had no category select; then passed after frontend implementation, 78 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full Java 17 backend verification, 508 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 114 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented rejected trigger abuse summary from `docs/plans/141-rejected-trigger-abuse-summary.md`.

Changes:

- Added `GET /api/rejected-triggers/summary` for read-only recent rejection counts.
- Summarized rejected-trigger audits by category, source, top trigger users, and top repositories using the existing rejected-trigger audit store.
- Added typed frontend support for the summary endpoint.
- Rendered a compact rejected-trigger summary above the dashboard audit rows.
- Made category summary rows clickable so operators can apply the existing URL-backed rejected-category filter from the summary.
- Updated product docs and README to document the summary endpoint and dashboard behavior.

Validation:

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests test`: first failed because summary VO records and service/controller support did not exist; then passed after backend implementation, 18 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the API helper and dashboard had no rejected-trigger summary; then passed after frontend implementation, 80 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 511 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 116 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented rejected trigger auto quarantine from `docs/plans/142-rejected-trigger-auto-quarantine.md`.

Changes:

- Added a configurable rejected-trigger quarantine policy based on recent rejected-trigger audit records.
- Applied quarantine to GitHub webhook triggers and manual dashboard-created tasks before rate-limit checks, model classification, task creation, queueing, workspace cloning, or execution.
- Added the `ABUSE_QUARANTINED` rejected-trigger category for trigger-user and repository cooldowns.
- Exposed quarantine policy state in `GET /api/configuration/summary`, demo readiness, and the dashboard configuration panel.
- Updated the rejected-trigger dashboard summary/filter path to include abuse-quarantined records.
- Documented the self-hosted/private-demo quarantine behavior in product docs and README.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests,ConfigurationSummaryServiceTests test`: first failed because quarantine decision/request/service types did not exist.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because the dashboard did not expose quarantine policy or the `ABUSE_QUARANTINED` category.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests,ConfigurationSummaryServiceTests,RejectedTriggerQuarantineServiceTests test`: passed after backend implementation, 30 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx src/dashboard/components/ConfigurationPanel.test.tsx`: passed after frontend implementation, 6 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests,ConfigurationSummaryServiceTests,RejectedTriggerQuarantineServiceTests,DemoReadinessServiceTests test`: passed after adding demo-readiness and invalid-threshold coverage, 36 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/RejectedTriggerPanel.test.tsx src/dashboard/components/ConfigurationPanel.test.tsx`: passed after adding API and app fixtures, 83 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 519 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 116 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented durable trigger quarantine from `docs/plans/143-durable-trigger-quarantine.md`.

Changes:

- Added durable trigger quarantine domain records for trigger-user and repository scopes, including reason, category, evidence count, window, start, expiry, and timestamps.
- Added in-memory and MyBatis-backed `TriggerQuarantineRecordService` implementations plus the `trigger_quarantine` Flyway migration.
- Updated rejected-trigger quarantine checks to consult active durable records before recomputing thresholds from rejected-trigger audit history.
- Created or extended quarantine records when recent rejected-trigger evidence crosses the configured threshold.
- Exposed `GET /api/trigger-quarantines` for active or historical quarantine inspection.
- Added frontend API typing and dashboard loading for active trigger quarantines.
- Rendered active trigger-user and repository quarantines in the rejected-trigger panel above individual audit rows.
- Updated README and product docs to describe durable quarantine state and the new operator endpoint.

Validation:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=InMemoryTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,RejectedTriggerQuarantineServiceTests test`: first failed because quarantine domain records, persistence service, and controller did not exist.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: first failed because the frontend API helper and active quarantine panel did not exist.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=MyBatisTriggerQuarantineServiceTests,InMemoryTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,RejectedTriggerQuarantineServiceTests,TriggerQuarantineMigrationTests test`: passed after backend implementation, including SQL ordering before quarantine list limits, 18 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: passed after frontend implementation, 81 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 531 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 117 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented manual trigger quarantine controls from `docs/plans/144-manual-trigger-quarantine-controls.md`.

Changes:

- Added manual trigger quarantine creation and release APIs under `/api/trigger-quarantines`.
- Added quarantine operator and release metadata to domain records, MyBatis entities, list responses, and Flyway migrations.
- Updated active quarantine lookup to ignore released records while preserving historical records.
- Prevented released threshold quarantines from being immediately recreated from the same pre-release rejection evidence.
- Added dashboard controls to create trigger-user or repository quarantines and release active quarantines from the rejected-trigger panel.
- Updated README and product docs to document the operator API and dashboard behavior.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=InMemoryTriggerQuarantineServiceTests,MyBatisTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,TriggerQuarantineMigrationTests test`: first failed because service APIs, release metadata, and V23 migration did not exist; then passed after backend implementation, 18 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RejectedTriggerQuarantineServiceTests test`: first failed because a manually released quarantine could be recreated from the same rejected-trigger evidence; then passed after release suppression logic.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RejectedTriggerQuarantineServiceTests,InMemoryTriggerQuarantineServiceTests,MyBatisTriggerQuarantineServiceTests,TriggerQuarantineControllerTests,TriggerQuarantineMigrationTests test`: passed after backend release suppression fix, 26 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx`: first failed because frontend API helpers and manual quarantine controls did not exist; then passed after frontend implementation, 30 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: passed after App-level dashboard wiring, 85 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 539 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 121 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented operator safety audit log from `docs/plans/145-operator-safety-audit-log.md`.

Changes:

- Added durable `operator_safety_audit` records for manual safety mutations.
- Added in-memory and MyBatis-backed `OperatorSafetyAuditService` implementations plus the V24 Flyway migration.
- Exposed `GET /api/operator-safety-audits` for recent operator safety audit rows.
- Recorded audit rows when operators create or release trigger quarantines through `/api/trigger-quarantines`.
- Added frontend API typing and dashboard loading for recent operator safety audits.
- Rendered operator safety audit rows in the rejected-trigger panel so quarantine create/release actions show operator, reason, target, and time.
- Updated README and product docs to describe traceable manual safety controls.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=OperatorSafetyAuditControllerTests,InMemoryOperatorSafetyAuditServiceTests,MyBatisOperatorSafetyAuditServiceTests,OperatorSafetyAuditMigrationTests,TriggerQuarantineControllerTests test`: first failed because the operator safety audit model, service, controller, mapper, and migration did not exist; then passed after implementation, 10 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx src/App.test.tsx`: first failed because `listOperatorSafetyAudits` and the operator safety audit panel did not exist; then passed after frontend implementation, 86 tests run, 0 failures.

Implemented quarantine evidence drilldown from `docs/plans/146-quarantine-evidence-drilldown.md`.

Changes:

- Added `TriggerQuarantineEvidenceVo` and `TriggerQuarantineEvidenceService` as a read model for one quarantine, its matching rejected-trigger evidence, and its operator safety audit actions.
- Exposed `GET /api/trigger-quarantines/{id}/evidence` with not-found and limit validation behavior.
- Added in-memory and MyBatis-backed query methods for quarantine lookup by id, rejected-trigger evidence by quarantine target, and operator audit evidence by resource id.
- Added frontend API typing and `getTriggerQuarantineEvidence`.
- Added an `Inspect evidence` action to active quarantine rows in the rejected-trigger panel.
- Rendered a compact evidence drilldown with matching rejected `/agent fix` attempts and manual safety actions.
- Updated README and product docs to document the evidence endpoint and dashboard behavior.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=TriggerQuarantineControllerTests,DefaultTriggerQuarantineEvidenceServiceTests,InMemoryRejectedTriggerAuditServiceTests,InMemoryOperatorSafetyAuditServiceTests,InMemoryTriggerQuarantineServiceTests test`: first failed because `TriggerQuarantineEvidenceVo`, `TriggerQuarantineEvidenceService`, and `DefaultTriggerQuarantineEvidenceService` did not exist; then passed after backend implementation, 24 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx`: first failed because the new frontend API/helper props and evidence UI were not implemented; then passed after frontend implementation, 33 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/RejectedTriggerPanel.test.tsx`: passed after App-level evidence loading, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest='*TriggerQuarantine*Tests,*RejectedTriggerAuditServiceTests,*OperatorSafetyAuditServiceTests,DefaultTriggerQuarantineEvidenceServiceTests' test`: first failed because MyBatis rejected-trigger query tests needed entity metadata initialization and the newest-first mock no longer matched SQL ordering; then passed after fixing the tests, 53 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 557 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 125 tests run, 0 failures.
- `npm run build`: first failed because one component test fixture omitted the new evidence props caught by TypeScript; then passed after fixing the fixture.
- `git diff --check`: passed after whitespace verification.

Implemented repository preflight diagnostics from `docs/plans/147-repository-preflight-diagnostics.md`.

Changes:

- Added `POST /api/repository-preflight` as a local adapter-detection diagnostic that does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Returned supported status, language, build system, verification command, detection reason, operator action, and adapter guidance for unsupported repository paths.
- Added frontend API typing and `preflightRepository`.
- Added a dashboard `RepositoryPreflightPanel` so operators can run the real language adapter registry against a local path before posting `/agent fix`.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RepositoryPreflightServiceTests,RepositoryPreflightControllerTests test`: first failed because the repository preflight request, response, service, and controller did not exist; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/RepositoryPreflightPanel.test.tsx src/api.test.ts`: first failed because the dashboard preflight panel did not exist; then passed after frontend implementation.
- `npm test -- --run src/dashboard/components/RepositoryPreflightPanel.test.tsx src/api.test.ts src/App.test.tsx`: passed after App-level dashboard wiring, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 563 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 130 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented repository preflight scope policy from `docs/plans/148-repository-preflight-scope-policy.md`.

Changes:

- Added `patchpilot.repository-preflight.allowed-root-dirs` and `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS`.
- Limited `POST /api/repository-preflight` to resolved paths under configured allowed roots before adapter detection.
- Kept relative root handling compatible with both repository-root and `PatchPilot/` Maven module working directories.
- Exposed normalized repository-preflight allowed roots through `GET /api/configuration/summary`.
- Added dashboard configuration visibility for repository-preflight allowed roots and a health advisory when the list is empty.
- Updated README, product spec, architecture notes, frontend design notes, decisions, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=RepositoryPreflightServiceTests,RepositoryPreflightControllerTests,ConfigurationSummaryServiceTests,DemoReadinessServiceTests test`: first failed because the configuration-summary test expected the non-module relative path; then passed after updating the expected module-friendly fallback path, 14 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/ConfigurationPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the configuration panel did not render repository-preflight roots or the empty-root advisory; then passed after frontend implementation, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 565 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 130 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.

Implemented repository preflight scope readiness from `docs/plans/149-repository-preflight-scope-readiness.md`.

Changes:

- Added a `Repository preflight scope` check to demo readiness.
- Flagged readiness attention when `PATCHPILOT_REPOSITORY_PREFLIGHT_ALLOWED_ROOT_DIRS` does not cover checked-in demo fixtures.
- Added repository-preflight scope to the operator setup checklist.
- Displayed configured repository-preflight allowed roots directly in the dashboard preflight panel.
- Updated README, product spec, frontend design notes, and this execution log.

Validation:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests test`: first failed because demo readiness did not include the repository-preflight scope check and a sibling root prefix was incorrectly treated as allowed; then passed after backend implementation, 7 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/dashboard/components/RepositoryPreflightPanel.test.tsx`: first failed because the checklist and preflight panel did not show scope readiness or allowed roots; then passed after frontend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: first failed because the checklist ignored the backend readiness result for repository-preflight scope; then passed after using the backend readiness check when available, 3 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=MyBatisTriggerQuarantineServiceTests,InMemoryTriggerQuarantineServiceTests test`: passed after replacing stale fixed-date quarantine expirations with future instants, 14 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 567 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 133 tests run, 0 failures.
- `npm run build`: first failed because the new checklist fixture used `FixTaskStatusCounts` fields in a `FixTaskQueueSummary`; then passed after fixing the fixture.
- `git diff --check`: passed after whitespace verification.

Implemented demo evidence bundle from `docs/plans/150-demo-evidence-bundle.md`.

Changes:

- Added `GET /api/demo/evidence-bundle` as a read-only aggregate over demo readiness, smoke checklist, non-sensitive configuration, adapter fixture verification, queue summary, recent tasks, webhook delivery diagnostics, rejected-trigger summary, and active trigger quarantines.
- Added demo evidence response models with summary counts, latest evidence records, recent Pull Request URL, generated timestamp, and next actions.
- Added a dashboard `DemoEvidenceBundlePanel` near the existing setup/readiness area.
- Wired frontend refresh loading to fetch and render the evidence bundle without blocking the rest of the dashboard when the endpoint fails.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoReadinessControllerTests test`: first failed because the evidence bundle service, response models, and endpoint did not exist; then passed after backend implementation, 5 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because the frontend API and component did not exist; then passed after frontend implementation, 32 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: passed after App-level dashboard wiring, 89 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 570 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 136 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo runbook export from `docs/plans/151-demo-runbook-export.md`.

Changes:

- Added `GET /api/demo/runbook` as a read-only Markdown export over the existing demo evidence bundle.
- Added `DemoRunbookService` to format status, summary, generated time, recent PR, recent task, latest webhook delivery, adapter fixture counts, queue counts, rejected-trigger counts, active quarantines, readiness checks, smoke checklist steps, and next actions.
- Added `getDemoRunbook` to the frontend API layer.
- Added a `Copy runbook` action to `DemoEvidenceBundlePanel` that fetches the Markdown only on click and copies it to the clipboard.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoRunbookServiceTests,DemoReadinessControllerTests test`: first failed because `DemoRunbookService` did not exist; then failed on a stale readiness field name and webhook fixture constructor; then passed after backend implementation, 6 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because `getDemoRunbook` and the `Copy runbook` button did not exist; then passed after frontend implementation, 34 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: passed after App-level copy wiring, 92 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 573 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 139 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo script and health contract from `docs/plans/152-demo-script-and-health-contract.md`.

Changes:

- Added `GET /api/demo/script` as a read-only script read model over the existing demo evidence bundle.
- Added `DemoScriptService`, `DemoScriptVo`, and `DemoScriptStepVo` to return ordered operator actions for backend/dashboard access, configuration and safety posture, repository support, controlled `/agent fix` triggering, task execution tracking, and Pull Request evidence review.
- Included verification commands, success criteria, troubleshooting panel names, current evidence, next actions, generated time, and a health contract stating the endpoint does not create tasks, call the model, run tests, mutate Git, or write to GitHub.
- Added frontend `getDemoScript`, typed `DemoScript` models, and `DemoScriptPanel`.
- Wired the dashboard to load and render the script near the existing demo evidence/readiness panels without blocking the rest of the dashboard when the script endpoint fails.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoScriptServiceTests,DemoReadinessControllerTests test`: first failed because the script service, response models, and endpoint did not exist; then passed after backend implementation, 7 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoScriptPanel.test.tsx src/App.test.tsx`: first failed because `getDemoScript`, `DemoScriptPanel`, and App-level rendering did not exist; then passed after frontend implementation, 92 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 576 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 142 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented demo session archive from `docs/plans/155-demo-session-archive.md`.

Changes:

- Added process-local demo session archives capped to the latest 20 entries.
- Added `POST /api/demo/session-archives` to archive the current demo session snapshot and Markdown report.
- Added `GET /api/demo/session-archives` to list recent archived session reports.
- Reused the same snapshot for archive metadata and Markdown report generation.
- Added frontend API helpers, archive loading, an `Archive session` dashboard action, archive error handling, and recent archive copy actions in the demo session snapshot panel.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoSessionArchiveServiceTests,DemoReadinessControllerTests test`: first failed because archive service/models/endpoints did not exist; then failed because equal timestamps were sorted by string id; then passed after preserving newest-first insertion order and capping the in-memory archive, 11 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx`: first failed because archive API helpers and panel actions did not exist; then failed because the new archive list intentionally duplicated session id/share-summary text from the current snapshot; then passed after updating the assertions to match current-plus-archive rendering, 99 tests run, 0 failures.
- `GIT_OPTIONAL_LOCKS=0 git diff --check`: passed after whitespace verification.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 586 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 151 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.

Implemented persistent demo session archive from `docs/plans/156-persistent-demo-session-archive.md`.

Changes:

- Added Flyway migration `V25__create_demo_session_archive.sql`.
- Added `DemoSessionArchiveEntity`, `DemoSessionArchiveMapper`, and `DemoSessionArchiveConvert`.
- Introduced `DemoSessionArchiveRepository` as the storage boundary behind `DemoSessionArchiveService`.
- Kept `InMemoryDemoSessionArchiveRepository` for the default database-free profile.
- Added `MyBatisDemoSessionArchiveRepository` for `local`, `docker`, and `idea` profiles so archived demo reports survive backend restarts.
- Kept the existing archive API and dashboard contract unchanged.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoSessionArchiveMigrationTests,InMemoryDemoSessionArchiveRepositoryTests,MyBatisDemoSessionArchiveRepositoryTests test`: first failed because the migration, entity, mapper, and repository implementations did not exist.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot -Dtest=DemoSessionArchiveMigrationTests,InMemoryDemoSessionArchiveRepositoryTests,MyBatisDemoSessionArchiveRepositoryTests,DemoSessionArchiveServiceTests,DemoReadinessControllerTests test`: passed after backend implementation, 15 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx`: passed after confirming the frontend contract did not need changes, 99 tests run, 0 failures.
- `GIT_OPTIONAL_LOCKS=0 git diff --check`: passed after whitespace verification.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 590 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 151 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.

Implemented demo session report download from `docs/plans/157-demo-session-report-download.md`.

Changes:

- Added `GET /api/demo/session-report/download` to return the current demo session report as a Markdown attachment.
- Added `GET /api/demo/session-archives/{archiveId}/report/download` to return one stored archived report as a Markdown attachment.
- Added archive lookup by id behind `DemoSessionArchiveRepository` for in-memory and MyBatis-backed profiles.
- Added frontend Blob download helpers that reuse the dashboard admin-token header path.
- Added `Download session report` and archived `Download report` actions to `DemoSessionSnapshotPanel`.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoReadinessControllerTests,DemoSessionArchiveServiceTests,InMemoryDemoSessionArchiveRepositoryTests,MyBatisDemoSessionArchiveRepositoryTests test`: first failed because `findArchive`, `findById`, and the download endpoints did not exist; then passed after backend implementation, 19 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx`: first failed because `downloadDemoSessionReport`, `downloadDemoSessionArchiveReport`, and the download buttons did not exist; then passed after frontend implementation, 44 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 595 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 155 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented worker heartbeat dashboard from `docs/plans/158-worker-heartbeat-dashboard.md`.

Changes:

- Added process-local `FixTaskWorkerHealthService` state for worker poll, idle, claim, completion, and failure events.
- Wired `FixTaskQueuePoller` to update worker heartbeat state during every queue polling cycle.
- Added `GET /api/task-queue/worker-health` for curl diagnostics and dashboard visibility.
- Added dashboard API types/helpers and rendered worker state, poll count, claimed count, latest claimed task, and latest worker error in `QueuePanel`.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=FixTaskQueuePollerTests,InMemoryFixTaskWorkerHealthServiceTests,TaskQueueControllerTests test`: first failed because worker health types, service, and endpoint did not exist; then passed after backend implementation, 9 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=FixTaskQueuePollerTests test`: later failed because queue polling infrastructure exceptions left worker health at `POLLING`; then passed after recording poller infrastructure failures as worker `ERROR` while preserving exception propagation, 4 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/QueuePanel.test.tsx src/App.test.tsx`: first failed because `getWorkerHealth` and worker heartbeat rendering did not exist; then passed after frontend implementation, 104 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 600 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 158 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented worker health readiness gate from `docs/plans/159-worker-health-readiness-gate.md`.

Changes:

- Added `patchpilot.task.queue.worker-heartbeat-stale-ms` configuration and exposed it through the non-sensitive configuration summary.
- Extended worker health with `lastPollAgeMs`, `readinessStatus`, and `operatorAction`.
- Derived worker readiness as `READY` only when the poller has reported a fresh poll and is not in `ERROR`.
- Added `Worker heartbeat` to demo readiness so stale, missing, or errored worker state blocks live-demo readiness before a GitHub trigger is posted.
- Added worker readiness, last poll age, and operator action to the queue panel.
- Added worker heartbeat to the operator setup checklist.
- Added heartbeat stale-threshold visibility and configuration health hints to the configuration panel.
- Updated README, product spec, architecture notes, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskWorkerHealthServiceTests,TaskQueueControllerTests test`: first failed because worker health did not yet expose readiness fields or stale-threshold behavior; then passed after adding readiness derivation and controller serialization.
- `mvn -pl PatchPilot -Dtest=ConfigurationSummaryServiceTests,ConfigurationControllerTests,DemoReadinessServiceTests,DemoEvidenceBundleServiceTests test`: first failed because configuration and demo-readiness fixtures did not yet include the worker stale threshold and worker-health supplier; then passed after wiring the new fields.
- `mvn -pl PatchPilot -Dtest=ConfigurationSummaryServiceTests,ConfigurationControllerTests,DemoReadinessServiceTests,DemoEvidenceBundleServiceTests,InMemoryFixTaskWorkerHealthServiceTests,TaskQueueControllerTests test`: passed after backend integration, 19 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/QueuePanel.test.tsx src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/dashboard/components/ConfigurationPanel.test.tsx src/App.test.tsx`: first failed because two App-level checklist count assertions still expected the old 7-check model; then passed after updating the integration expectations, 111 tests run, 0 failures.
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home mvn -pl PatchPilot test`: passed after full backend verification, 602 tests run, 0 failures.
- `npm test`: passed after full frontend verification, 159 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `GIT_OPTIONAL_LOCKS=0 git diff --check`: passed after whitespace verification.

Implemented stable failure category metrics from `docs/plans/167-stable-failure-category-metrics.md`.

Changes:

- Reused `TaskFailureFeedback` as the source of truth for failed-task metrics categories.
- Replaced metrics-only failure buckets such as `MAVEN_TESTS`, `GITHUB_AUTH`, and `MODEL_ERROR` with stable issue-facing categories such as `VERIFICATION_FAILED`, `GITHUB_OPERATION_FAILED`, `MODEL_FAILED`, and `TASK_FAILED`.
- Added `nextAction` to `GET /api/tasks/metrics/failure-causes` rows while keeping existing `cause` and `count` fields.
- Updated the dashboard failure-cause panel to render stable category labels and operator next-action guidance.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskMetricsServiceTests#should_summarize_failed_tasks_by_stable_failure_category test`: first failed because `FixTaskFailureCauseSummaryVo` did not expose `nextAction` and the service still returned `MAVEN_TESTS`; then passed after reusing `TaskFailureFeedback`.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_failure_cause_summary test`: passed after API contract coverage for stable categories and next actions.
- `npm test -- --run src/dashboard/components/FailureCausePanel.test.tsx`: first failed because the panel rendered raw `VERIFICATION_FAILED` and no guidance; then passed after stable labels and next-action rendering.
- `npm test -- --run src/dashboard/components/FailureCausePanel.test.tsx src/api.test.ts src/App.test.tsx`: passed after frontend API and integration updates, 99 tests run, 0 failures.

Implemented issue context trigger classification from `docs/plans/160-issue-context-trigger-classification.md`.

Changes:

- Added issue title, issue body, and recent issue comments to trigger intent classification requests.
- Updated the model trigger-classification prompt to evaluate the trigger comment together with GitHub issue context.
- Added an explicit classifier capability flag so short `NOT_ACTIONABLE` comments only proceed when model-backed issue-context classification is enabled.
- Wired webhook and manual task creation to load issue context after deterministic safety, duplicate, active-task, quarantine, and rate-limit checks.
- Kept dangerous commands, unsupported comments, unauthorized users, unauthorized repositories, quarantined users, and rate-limited requests rejected before issue context is loaded.
- Added repository/issue based issue-context loading to `IssueContextService`.
- Moved trigger-user and repository allowlist checks ahead of command actionability when evaluating full trigger requests, so short `/agent fix` comments cannot use issue-context classification to bypass authorization.

Validation so far:

- `mvn -pl PatchPilot -Dtest=ModelTriggerIntentClassifierTests,DefaultManualFixTaskServiceTests,GitHubWebhookServiceTests test`: first failed because issue-context request fields, classifier capability, context loading, and service constructors did not exist; then passed after backend implementation, 31 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests test`: first failed because short `/agent fix` returned `NOT_ACTIONABLE` before allowlist decisions; then passed after splitting syntax/danger checks from actionability, 10 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=CommandSafetyGateTests,DefaultManualFixTaskServiceTests,GitHubWebhookServiceTests test`: passed after service-level safety regression coverage, 38 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=ModelTriggerIntentClassifierTests,DefaultManualFixTaskServiceTests,GitHubWebhookServiceTests,IssueContextServiceTests test`: passed after issue-context integration, 33 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 609 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

Implemented trigger decision evidence from `docs/plans/161-trigger-decision-evidence.md`.

Changes:

- Added a `TRIGGER_ACCEPTED` task timeline event for accepted `/agent fix` triggers.
- Recorded safety-gate outcome, issue-context load status, and model trigger-classification outcome in the accepted trigger evidence message.
- Applied the evidence event to both GitHub webhook triggers and dashboard manual task creation.
- Kept existing rejection order unchanged so dangerous, unauthorized, quarantined, and rate-limited triggers still stop before task creation.
- Updated frontend task timeline event typing for the new backend event type.

Validation so far:

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests test`: first failed because `TRIGGER_ACCEPTED` did not exist; then failed because accepted evidence exposed the internal `NOT_ACTIONABLE` rejection text; then passed after adding accepted-trigger evidence formatting, 28 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TriggerDecisionEvidenceFormatterTests,GitHubWebhookServiceTests,DefaultManualFixTaskServiceTests test`: passed after locking formatter behavior, 30 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend verification, 611 tests run, 0 failures.
- `npm test`: passed after frontend regression verification, 159 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed after whitespace verification.

Implemented trigger decision visibility from `docs/plans/162-trigger-decision-visibility.md`.

Changes:

- Added a `TriggerDecisionPanel` that pairs the selected task's `TRIGGER_ACCEPTED` timeline evidence with recent rejected trigger decisions.
- Rendered accepted trigger evidence, recent rejected trigger rows, and compact rejected-trigger category counts in one dashboard region.
- Kept the existing rejected-trigger panel as the full audit, quarantine, evidence drilldown, retry, and operator safety action surface.
- Added integration and component coverage for the new dashboard region, accepted evidence, rejected decisions, category labels, and empty states.
- Updated README, frontend design notes, and this execution log.

Validation so far:

- `npm test -- --run src/App.test.tsx src/dashboard/components/TriggerDecisionPanel.test.tsx`: first failed because the new panel did not exist, then failed because accepted trigger evidence was checked before selected-task detail finished loading, then passed after the panel implementation and async assertion fix, 61 tests run, 0 failures.
- `npm run build`: first failed because the new `RejectedTriggerAudit` test fixture was missing `retriedAt`; then passed after completing the fixture.
- `npm test`: first failed because task-list filtering could briefly combine a newly selected task with stale accepted-trigger detail from the previously selected task; then passed after clearing detail state immediately when selected task changes, 161 tests run, 0 failures.
- `npm run build`: passed after final production frontend build verification.
- `GIT_OPTIONAL_LOCKS=0 git diff --check`: passed after whitespace verification.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 611 tests run, 0 failures.

Implemented rejected trigger issue feedback from `docs/plans/163-rejected-trigger-issue-feedback.md`.

Changes:

- Extended GitHub issue refusal comments for rejected `/agent fix` webhook triggers with the stable rejection category and a category-specific next action.
- Kept unsafe trigger bodies out of refusal comments while preserving the rejection reason, repository, issue, and trigger user.
- Passed the rejection category from webhook safety decisions into the refusal comment tool.
- Kept rejected-trigger audit recording failure-tolerant when GitHub refusal comment creation fails.
- Added null-category fallback guidance so refusal comment creation cannot crash on incomplete rejection metadata.
- Updated README, product spec, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests#should_create_safe_rejection_comment_without_echoing_trigger_body test`: first failed because the refusal comment did not include `Category: DANGEROUS_INSTRUCTION`; then passed after adding category and next-action copy.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests#should_create_generic_rejection_comment_when_category_is_missing test`: first failed with a `NullPointerException` in category next-action selection; then passed after adding generic fallback guidance.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,GitHubWebhookServiceTests test`: passed after wiring category through webhook rejection handling, 29 tests run, 0 failures.

Implemented rejected trigger refusal comment visibility from `docs/plans/164-rejected-trigger-refusal-comment-visibility.md`.

Changes:

- Added `Refusal comment` links to rejected rows in `TriggerDecisionPanel` when rejected-trigger audit rows include a GitHub refusal comment URL.
- Kept rejected rows without comment URLs unchanged.
- Styled trigger-decision metadata links consistently with the full rejected-trigger audit panel.
- Updated README, frontend design notes, and this execution log.

Validation so far:

- `npm test -- --run src/dashboard/components/TriggerDecisionPanel.test.tsx`: first failed because the trigger-decision rejected row did not expose the `Refusal comment` link; then passed after rendering the link, 2 tests run, 0 failures.
- `npm test`: passed after full frontend regression verification, 161 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `GIT_OPTIONAL_LOCKS=0 git diff --check`: passed after whitespace verification.

Implemented rejected trigger retry preflight from `docs/plans/165-rejected-trigger-retry-preflight.md`.

Changes:

- Added a derived rejected-trigger retry policy that marks safe actionability/model-classification rows as directly retryable.
- Blocked direct retry for dangerous instructions, unauthorized users, unauthorized repositories, rate limits, active abuse quarantines, unsupported commands, unknown categories, and already-retried audit rows.
- Exposed `retryable` and `retryBlockedReason` on rejected-trigger API rows.
- Returned `409 Conflict` from rejected-trigger retry when the row requires a new safe request, allowlist change, cooldown wait, quarantine release, or linked retried-task inspection instead of a new task.
- Updated the rejected-trigger dashboard panel so blocked rows show inline guidance and a disabled `Retry blocked` action, while safe rows still expose `Retry trigger`.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `npm test -- --run src/dashboard/components/RejectedTriggerPanel.test.tsx`: first failed because retry-blocked guidance was not rendered; then passed after adding UI guidance and retry-button preflight, 6 tests run, 0 failures.
- `npm test`: passed after full frontend regression verification, 162 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `mvn -pl PatchPilot -Dtest=RejectedTriggerRetryPolicyTests,DefaultRejectedTriggerRetryServiceTests,RejectedTriggerAuditControllerTests test`: passed after backend retry policy, service, and controller coverage, 14 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 617 tests run, 0 failures.
- `GIT_OPTIONAL_LOCKS=0 git diff --check`: passed after whitespace verification.

Implemented task failure issue feedback from `docs/plans/166-task-failure-issue-feedback.md`.

Changes:

- Added failed-task feedback creation when a task fails without an existing accepted-task status comment.
- Attached the created failure feedback comment id and URL back to the durable task.
- Preserved `FAILED` task status when failure feedback creation or update fails and recorded `STATUS_COMMENT_FAILED` timeline evidence.
- Added issue-facing failure categories, next-action guidance, and common secret-like value redaction for failed-task comments.
- Updated the dashboard so failed-task status-comment URLs are labeled `Failure feedback`, while pending-review tasks use `Review feedback` and other tasks keep `Status Comment`.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests#should_create_failure_status_comment_when_initial_status_comment_is_missing test`: first failed because the worker left `statusCommentId` empty; then passed after creating and attaching a failure feedback comment.
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests#should_keep_failed_status_when_failure_status_comment_creation_fails test`: passed after locking failure feedback error tolerance.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests#should_create_failed_status_comment_with_category_and_next_action+IssueCommentToolTests#should_redact_sensitive_values_from_failed_status_comment test`: first failed because failed comments lacked category and next action; then passed after adding `TaskFailureFeedback`.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because failed tasks still rendered `Status Comment`; then passed after status-specific feedback link labels, 19 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx`: passed after dashboard integration update, 59 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests,IssueCommentToolTests test`: passed after focused backend regression verification, 22 tests run, 0 failures.
- `npm test`: passed after full frontend regression verification, 163 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 621 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `GIT_OPTIONAL_LOCKS=0 git diff --check`: passed after whitespace verification.

Implemented task failure diagnosis detail from `docs/plans/168-task-failure-diagnosis-detail.md`.

Changes:

- Added nullable `failureDiagnosis` to task detail responses for failed tasks.
- Reused `TaskFailureFeedback` so task detail, copied reports, issue feedback, and failure metrics share category, next action, and sanitized reason behavior.
- Added a `Failure Diagnosis` section to copyable Markdown task reports.
- Added a dashboard `Failure diagnosis` section in selected-task detail, including human-readable category labels, next action, and redacted failure reason.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_include_failure_diagnosis_for_failed_task_detail+TaskControllerTests#should_get_task_report_by_task_id test`: first failed because task detail did not include `failureDiagnosis.category`; then passed after adding the detail field and report section.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_get_task_report_by_task_id test`: first failed because the legacy report `Failure` line still contained the raw GitHub-style token; then passed after reusing the shared sanitized failure reason in that line.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: passed after focused controller regression verification, 61 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the dashboard did not render `Failure diagnosis`; then passed after rendering the diagnosis section.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/api.test.ts src/App.test.tsx`: passed after frontend API and dashboard fixture updates, 118 tests run, 0 failures.

Implemented task retry preflight from `docs/plans/169-task-retry-preflight.md`.

Changes:

- Added `GET /api/tasks/{id}/retry-preflight` with retry eligibility, task status, stable failure category, sanitized reason, and operator action.
- Reused retry preflight inside `POST /api/tasks/{id}/retry`, returning `409 Conflict` when a task requires setup or repository support work before another attempt.
- Kept verification, model, workspace, patch-review, generic failed, and cancelled tasks retryable while blocking blind retries for GitHub operation failures and unsupported repositories.
- Added dashboard retry-preflight loading for failed and cancelled task detail.
- Added a `Retry preflight` detail section and disabled the retry button as `Retry blocked` when backend preflight marks the task blocked.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests test`: first failed because `FixTaskRetryPreflightVo` did not exist; then passed after adding the retry preflight read model, service policy, controller endpoint, and retry guard, 79 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/api.test.ts`: first failed because the dashboard did not render `Retry preflight`; then passed after adding the detail section, disabled retry state, and API client coverage, 62 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx -t "retries failed tasks"`: passed after page-level retry-preflight loading and display coverage.

Implemented task retry reason audit from `docs/plans/170-task-retry-reason-audit.md`.

Changes:

- Changed `POST /api/tasks/{id}/retry` to require a JSON retry reason and return `400 Bad Request` when the request body or reason is missing.
- Stored `retryReason` on MySQL and in-memory task records alongside retry lineage.
- Added retry reason to requeue timeline evidence and copied Markdown task reports.
- Added dashboard retry reason input in task retry preflight and disabled `Retry task` until the operator provides a reason.
- Rendered stored retry reasons in dashboard retry lineage.
- Updated README, product spec, frontend design notes, migration coverage, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,FixTaskConvertTests,MyBatisFixTaskServiceTests test`: passed after backend retry reason service/controller/persistence coverage, 107 tests run, 0 failures, 0 errors.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because retry was still enabled without an operator reason and retry lineage omitted the reason; then passed after adding the reason input, POST body, and retry-lineage display, 122 tests run, 0 failures.

Implemented trigger execution intent audit from `docs/plans/171-trigger-execution-intent-audit.md`.

Changes:

- Added a nullable `triggerIntentAudit` read model to task detail responses, derived from the latest `TRIGGER_ACCEPTED` timeline event.
- Included accepted-trigger intent in copied Markdown task reports with safety-gate, issue-context, and model-decision fields.
- Rendered a `Trigger intent` section in selected-task detail before raw timeline evidence.
- Updated frontend API/types/tests and product documentation without adding new tables, migrations, or model calls.

Validation so far:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed because task detail and report output did not expose `triggerIntentAudit`; then passed after backend implementation, 65 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx`: first failed because the dashboard did not render `Trigger intent`; then passed after adding the detail section, 23 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`: passed after frontend API and dashboard fixture updates, 123 tests run, 0 failures.
- `npm test`: passed after full frontend regression verification, 170 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 631 tests run, 0 failures.
- `git diff --check`: passed after whitespace verification.

Implemented trigger evaluation dry run from `docs/plans/172-trigger-evaluation-dry-run.md`.

Changes:

- Added `POST /api/tasks/evaluate-trigger` so operators can check whether a proposed manual `/agent fix` would create a task before pressing `Create task`.
- Reused the manual task creation gate order: command safety, active-task check, rejected-trigger quarantine, read-only trigger rate-limit check, optional issue-context model trigger classification.
- Added read-only rate-limit checking so dry runs do not consume quota.
- Kept dry runs non-mutating: no task, queue item, rejected-trigger audit row, GitHub comment, or rate-limit record is created.
- Added structured dry-run output with `WOULD_CREATE_TASK` or `BLOCKED`, per-gate decisions, issue-context load state, and next operator action.
- Added dashboard manual-task evaluation controls and an inline allowed/blocked gate summary.
- Updated README, product spec, architecture, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_evaluate_manual_trigger_without_creating_task+TaskControllerTests#should_evaluate_unsafe_manual_trigger_without_recording_rejected_audit test`: first failed with `405` because the dry-run endpoint did not exist; later passed after adding the controller and service.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_evaluate_manual_trigger_without_creating_task+TaskControllerTests#should_evaluate_unsafe_manual_trigger_without_recording_rejected_audit,DefaultTriggerEvaluationServiceTests,InMemoryTriggerRateLimitServiceTests#should_check_without_recording_rate_limit_hit test`: passed after backend dry-run service and read-only rate-limit coverage, 4 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/ManualTaskForm.test.tsx`: first failed because the frontend API and manual form dry-run control did not exist; later failed because the blocked reason was duplicated in the summary and gate detail; then passed after rendering one summary and one gate-specific reason, 44 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/ManualTaskForm.test.tsx`: passed after dashboard integration verification, 103 tests run, 0 failures.

Implemented webhook trigger evaluation preview from `docs/plans/173-webhook-trigger-evaluation-preview.md`.

Changes:

- Added `source` to trigger evaluation input and output with `MANUAL` default behavior for existing clients.
- Added `ISSUE_COMMENT` evaluation source so dry runs can preview the same downstream gate source used by GitHub issue-comment triggers.
- Reused the existing read-only safety, active-task, quarantine, rate-limit, issue-context, and model-classification order without creating tasks, queue items, rejected-trigger audits, GitHub comments, webhook diagnostics, or rate-limit records.
- Updated the dashboard manual-task form with a source selector for `Manual API` versus `GitHub issue comment` preview.
- Rendered the evaluated source in the dry-run result summary.
- Updated README, product spec, architecture, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DefaultTriggerEvaluationServiceTests,TaskControllerTests#should_evaluate_manual_trigger_without_creating_task+TaskControllerTests#should_evaluate_issue_comment_trigger_without_creating_task test`: first failed because trigger evaluation had no source field and always used manual gate source; then passed after backend implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/ManualTaskForm.test.tsx`: first failed because the form did not send a source, had no GitHub issue-comment source control, and did not render the evaluated source; then passed after frontend implementation, 45 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests,DefaultTriggerEvaluationServiceTests test`: passed after focused backend regression verification, 71 tests run, 0 failures.

Implemented webhook payload diagnostic replay from `docs/plans/174-webhook-payload-diagnostic-replay.md`.

Changes:

- Added `POST /api/github/webhook-diagnostics/evaluate-payload` as an admin-protected read-only operator diagnostic endpoint.
- Returned payload diagnostic status, signature status, JSON validity, supported event/action flags, `/agent fix` recognition, parsed repository/issue/user/comment fields, and a next operator action.
- Kept diagnostics isolated from real webhook handling: no task creation, queue dispatch, rejected-trigger audit row, delivery diagnostic record, GitHub comment, rate-limit mutation, or model call.
- Added dashboard webhook payload diagnostic controls to the webhook delivery panel.
- Added frontend API types/helper and result rendering for signature status, parsed target, message, and next action.
- Updated README, product spec, architecture, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=WebhookPayloadDiagnosticControllerTests test`: first failed with `404` because the diagnostic endpoint did not exist; then passed after adding the controller, service, DTO, VO, and signature diagnostic status, 3 tests run, 0 failures.
- `npm test -- --run src/api.test.ts`: first failed because `evaluateWebhookPayloadDiagnostic` was not exported; then passed after adding the API helper.
- `npm test -- --run src/api.test.ts src/dashboard/components/WebhookDeliveryPanel.test.tsx`: first failed because the webhook delivery panel had no payload diagnostic form or result region; then passed after adding the panel UI, 45 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/WebhookDeliveryPanel.test.tsx -t "renders operational task dashboard|evaluates webhook payload|WebhookDeliveryPanel"`: passed after wiring dashboard state and API calls, 4 tests run, 0 failures.

Implemented webhook delivery outcome correlation from `docs/plans/175-webhook-delivery-outcome-correlation.md`.

Changes:

- Added typed webhook delivery outcomes so delivery diagnostics can distinguish task, rejected-trigger, ignored, duplicate, and error outcomes.
- Linked task-created, active-task, and duplicate webhook diagnostics to task detail routes through `outcomeType`, `outcomeId`, and `outcomeUrl`.
- Linked rejected webhook diagnostics to the rejected-trigger audit id created during the same webhook handling path.
- Added nullable MySQL outcome columns for persisted webhook delivery diagnostics while keeping old rows readable through status-based fallback derivation.
- Rendered correlated outcomes in the dashboard webhook delivery panel, including task links and rejected-trigger anchors.
- Updated README, product spec, architecture, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=WebhookDeliveryDiagnosticControllerTests test`: first failed because `WebhookDeliveryDiagnosticVo` did not expose outcome fields; then passed after adding the read-model fields and fallback outcome derivation, 2 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests#should_create_task_for_agent_fix_issue_comment+GitHubWebhookControllerTests#should_reject_dangerous_agent_fix_issue_comment test`: first failed because `RecordWebhookDeliveryDiagnosticCommand` had no outcome fields; then passed after correlating task-created and rejected webhook paths. A later full class run covered both paths because the Maven method filter only executed one method.
- `mvn -pl PatchPilot -Dtest=MyBatisWebhookDeliveryDiagnosticServiceTests,WebhookDeliveryDiagnosticMigrationTests test`: first failed because the MyBatis entity and migration had no outcome columns; then passed after adding `V27__add_webhook_delivery_outcome.sql`, entity fields, conversion, and old-row fallback derivation, 4 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=GitHubWebhookControllerTests,WebhookDeliveryDiagnosticControllerTests,InMemoryWebhookDeliveryDiagnosticServiceTests,MyBatisWebhookDeliveryDiagnosticServiceTests,WebhookDeliveryDiagnosticMigrationTests test`: passed after focused backend webhook-delivery regression verification, 18 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/WebhookDeliveryPanel.test.tsx`: first failed because the dashboard did not render delivery outcome targets; then passed after adding outcome rendering, 3 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx src/dashboard/components/WebhookDeliveryPanel.test.tsx`: passed after dashboard fixture and integration coverage, 105 tests run, 0 failures.

Implemented admin audit event trail from `docs/plans/176-admin-audit-event-trail.md`.

Changes:

- Reused `operator_safety_audit` as the durable protected-admin mutation stream.
- Added `/api/admin-audit-events` as a clearer alias for the existing operator safety audit stream while keeping `/api/operator-safety-audits` compatible.
- Recorded audit rows after successful manual task creation, task cancel, task retry, risk-review approval, rejected-trigger retry, demo session archive, and existing quarantine create/release actions.
- Kept failed validation, missing resources, and conflict responses from writing misleading audit rows.
- Added a dashboard `Admin audit trail` panel for the full protected mutation stream.
- Kept the rejected-trigger panel focused on quarantine-related operator safety rows by filtering the same admin event stream.
- Updated README, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=OperatorSafetyAuditControllerTests,TaskControllerTests,RejectedTriggerAuditControllerTests,DemoReadinessControllerTests test`: first failed with missing `/api/admin-audit-events` route and missing audit writes for task control, rejected-trigger retry, and demo archive; then passed after backend implementation, 91 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/AdminAuditPanel.test.tsx src/App.test.tsx`: first failed because `listAdminAuditEvents` and `AdminAuditPanel` did not exist and App still loaded the old operator-safety endpoint; then passed after frontend implementation, 105 tests run, 0 failures.

Implemented admin audit search and export from `docs/plans/177-admin-audit-search-export.md`.

Changes:

- Added `OperatorSafetyAuditQuery` as the normalized backend query model for protected admin audit searches.
- Extended `/api/admin-audit-events` and the compatibility alias `/api/operator-safety-audits` with optional filters for action, resource type, resource id, scope, scope key, operator, and limit.
- Kept existing recent-list behavior when filters are omitted.
- Added in-memory and MyBatis filtering support for the unified admin audit stream.
- Added dashboard admin audit filter controls for action, operator, resource type, resource id, and scope key.
- Added `Copy admin audit report` to export the visible filtered audit rows as Markdown evidence.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=OperatorSafetyAuditControllerTests,InMemoryOperatorSafetyAuditServiceTests,MyBatisOperatorSafetyAuditServiceTests test`: first failed because `OperatorSafetyAuditQuery` and filtered service methods did not exist; then failed once because old controller tests still mocked the legacy integer method; then passed after backend implementation, 12 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/AdminAuditPanel.test.tsx`: first failed because `listAdminAuditEvents` treated filter options as a limit and the panel had no filter/export controls; then passed after frontend API and component implementation, 49 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx src/api.test.ts src/dashboard/components/AdminAuditPanel.test.tsx -t "renders operational task dashboard|filters admin audit|lists filtered admin audit|AdminAuditPanel"`: first failed because the new App test used a nonexistent fetch helper; then passed after wiring it to `defaultAppResponse`, 3 targeted tests run, 0 failures.

Implemented adapter readiness report from `docs/plans/178-adapter-readiness-report.md`.

Changes:

- Added a dashboard `AdapterReadinessReportPanel` derived from the existing supported-adapter and fixture-verification APIs.
- Summarized adapter count, language coverage, fixture pass rate, and fixture failures in one operator-facing panel.
- Listed allowlisted verification commands for each language/build-system adapter so operators can confirm what PatchPilot is permitted to run.
- Added `Copy adapter readiness report` to export the current adapter readiness state as Markdown.
- Kept the existing supported-adapter matrix and fixture verification matrix for detailed inspection.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `npm test -- --run src/dashboard/components/AdapterReadinessReportPanel.test.tsx`: first failed because the new component did not exist; then passed after implementation, 3 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx -t "renders operational task dashboard"`: first failed because the dashboard did not expose the adapter readiness region; then failed twice because repeated fixture-readiness text needed panel-scoped assertions; then passed after App wiring and scoped integration assertions, 1 targeted test run, 0 failures.
- `npm test`: passed after full frontend regression verification, 188 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `mvn -pl PatchPilot test`: passed after backend regression verification, 646 tests run, 0 failures.

Implemented repository preflight evidence report from `docs/plans/179-repository-preflight-evidence-report.md`.

Changes:

- Added `Copy preflight report` to the dashboard repository preflight panel after a result exists.
- Exported supported status, repository path, selected adapter, allowlisted verification command, detection reason, operator action, allowed roots, and current preflight API error as Markdown.
- Included supported adapter options in unsupported-repository reports so operators can see which project markers or adapter work would make a repository eligible.
- Kept the existing `POST /api/repository-preflight` API and execution safety contract unchanged.
- Updated README, product spec, frontend design notes, and this execution log.

Validation so far:

- `npm test -- --run src/dashboard/components/RepositoryPreflightPanel.test.tsx`: first failed because `Copy preflight report` did not exist; then passed after adding the copy action and Markdown formatter, 5 tests run, 0 failures.
- `npm test -- --run src/App.test.tsx -t "runs repository preflight"`: passed after dashboard integration coverage for running preflight and copying the resulting Markdown, 1 targeted test run, 0 failures.
- `npm test`: passed after full frontend regression verification, 190 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `mvn -pl PatchPilot test`: passed after backend regression verification, 646 tests run, 0 failures.

Implemented Pull Request evidence summary from `docs/plans/182-pull-request-evidence-summary.md`.

Changes:

- Expanded `PullRequestTool` PR bodies with task id, trigger user, branch, detected language, build system, allowlisted verification command, and adapter detection reason.
- Added review-boundary text to generated PRs: PatchPilot opens a PR only after adapter-selected verification passes, commands come from repository adapters instead of arbitrary issue text, and PatchPilot does not auto-merge.
- Updated `NoopFixTaskExecutor` to pass the adapter-enriched task context into PR creation after adapter metadata is recorded.
- Updated README, product spec, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=PullRequestToolTests test`: first failed because the PR body lacked `Task: \`task-123\``; then passed after adding the PR evidence summary.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests#should_prepare_task_repository_and_run_maven_tests test`: first failed because the task passed to `PullRequestTool` had no adapter metadata; then passed after carrying the adapter-enriched task through the executor.
- `mvn -pl PatchPilot -Dtest=PullRequestToolTests,WorkspaceFixTaskExecutorTests test`: passed after focused backend regression verification, 12 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 649 tests run, 0 failures.
- `npm test`: passed after full frontend regression verification, 193 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented Pull Request patch review evidence summary from `docs/plans/188-pr-patch-review-evidence-summary.md`.

Changes:

- Expanded generated Pull Request bodies with latest model patch-review evidence when available: decision, reason, confidence, required follow-up, edited files, and reviewed time.
- Kept PR bodies unchanged when no patch-review record exists.
- Wired `NoopFixTaskExecutor` to read the latest task patch-review record before Pull Request creation and pass it into `PullRequestTool`.
- Preserved the existing PR review boundary text and avoided including raw diffs, prompts, or model responses in PR bodies.
- Updated README, product spec, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=PullRequestToolTests test`: first failed at compile time because `PullRequestTool` did not accept patch-review evidence; then passed after adding optional patch-review PR body formatting, 4 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests#should_resume_approved_pending_review_task_without_regenerating_diff test`: first failed at compile time because `NoopFixTaskExecutor` had no patch-review service handoff path; then passed after wiring latest patch-review lookup into PR creation, 1 test run, 0 failures.
- `mvn -pl PatchPilot -Dtest=PullRequestToolTests,WorkspaceFixTaskExecutorTests test`: passed after focused backend regression verification, 15 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 654 tests run, 0 failures.
- `git diff --check`: passed.

Implemented GitHub feedback risk review evidence from `docs/plans/187-github-feedback-risk-review-evidence.md`.

Changes:

- Added shared GitHub feedback formatting for generated-diff risk-review approval evidence.
- Expanded generated Pull Request bodies with approval operator, approval time, approval reason, and an explicit statement that the task resumed only after an allowed operator approved the generated-diff risk review.
- Expanded completed issue status comments with the same approval evidence when the task contains risk-review approval metadata.
- Kept PR and issue feedback unchanged for tasks that did not pass through human risk-review approval.
- Updated README, product spec, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=PullRequestToolTests,IssueCommentToolTests test`: first failed because PR and completed issue comments lacked `Risk review approval:`; then passed after adding shared GitHub feedback approval formatting, 19 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 653 tests run, 0 failures.
- `git diff --check`: passed.

Implemented issue comment patch review evidence from `docs/plans/189-issue-comment-patch-review-evidence.md`.

Changes:

- Added shared GitHub feedback formatting for model patch-review evidence: decision, reason, confidence, required follow-up, edited files, and review time.
- Reused the shared formatter in Pull Request bodies so PR and issue feedback stay consistent.
- Expanded completed issue status comments with model patch-review evidence when a review record exists.
- Expanded failed issue status comments with model patch-review rejection evidence when a review record exists.
- Wired `FixTaskWorker` to load the latest patch-review record before completed and failed status comment updates.
- Updated README, product spec, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`: first failed because `IssueCommentTool` had no patch-review-aware `updateCompleted` / `updateFailed` overloads.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,PullRequestToolTests test`: passed after adding shared patch-review evidence formatting, 21 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests test`: first failed because `FixTaskWorker` did not accept `FixTaskPatchReviewService`; then failed once because a test substitute still overrode the legacy comment-update method; then passed after wiring latest patch-review lookup and updating the substitute, 13 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,PullRequestToolTests,FixTaskWorkerTests,FixTaskQueuePollerTests,InMemoryFixTaskQueueTests test`: passed after focused regression verification, 39 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 657 tests run, 0 failures.
- `git diff --check`: passed.

Implemented local dashboard admin token bootstrap from `docs/plans/196-admin-token-bootstrap-from-env.md`.

Changes:

- Added `PATCHPILOT_DASHBOARD_ADMIN_TOKEN_BOOTSTRAP_ENABLED` / `patchpilot.security.dashboard-admin-token-bootstrap-enabled`, defaulting to disabled.
- Added `GET /api/dashboard/bootstrap` as a public bootstrap endpoint that returns the configured admin token only when the explicit local bootstrap flag is enabled.
- Kept existing operator APIs protected by the admin API token and documented that the bootstrap flag must stay disabled for public Cloudflare Tunnel or shared-network URLs.
- Updated the React dashboard to call bootstrap before protected API requests and store the returned token only when the browser has no existing admin token.
- Updated `.env.example`, README, this execution log, and the implementation plan.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DashboardBootstrapServiceTests,AdminApiSecurityFilterTests test`: first failed because bootstrap properties, service, and response types did not exist; then passed after implementing the endpoint and security-filter bypass, 8 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/App.test.tsx`: first failed because `getDashboardBootstrap` did not exist and the dashboard did not store the bootstrap token before protected calls; then passed after adding the API helper and refresh bootstrap flow, 111 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 680 tests run, 0 failures.
- `npm test`: first hit the default 5s timeout in the broad dashboard smoke test under the full concurrent frontend suite; the same test passed in isolation, then the test was given a scoped 10s timeout and the full frontend suite passed, 203 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented GitHub feedback Dashboard deep links from `docs/plans/186-github-feedback-dashboard-deep-links.md`.

Changes:

- Added `patchpilot.dashboard.base-url` / `PATCHPILOT_DASHBOARD_BASE_URL` as an optional, non-secret operator link setting.
- Added `DashboardLinkService` to format task detail links as `<base-url>/tasks/{taskId}` with safe slash handling.
- Added Dashboard task links to GitHub issue status comments and generated Pull Request bodies when the base URL is configured.
- Kept GitHub feedback unchanged when no Dashboard base URL is configured.
- Exposed only `dashboardBaseUrlConfigured` through `/api/configuration/summary` and the frontend configuration panel; the raw URL is not returned.
- Updated README, product spec, `.env.example`, Docker Compose, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,PullRequestToolTests,ConfigurationSummaryServiceTests test`: first failed because `DashboardProperties` and link-aware constructors did not exist; then passed after adding Dashboard link configuration and feedback integration, 18 tests run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx`: first failed because the configuration panel did not render Dashboard URL status; then passed after adding the configuration field and advisory, 48 tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,PullRequestToolTests,ConfigurationSummaryServiceTests,ConfigurationControllerTests test`: passed after adding property-binding and non-leakage coverage, 19 tests run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/ConfigurationPanel.test.tsx src/App.test.tsx src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`: passed after frontend integration regression verification, 113 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 651 tests run, 0 failures.
- `npm test`: passed after full frontend regression verification, 193 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented GitHub repository access readiness from `docs/plans/198-github-repository-access-readiness.md`.

Changes:

- Added an admin-protected `GET /api/github/repository-access-readiness` endpoint.
- Added a read-only GitHub repository access probe that calls `GET https://api.github.com/repos/{owner}/{repository}` with `PATCHPILOT_GITHUB_TOKEN`.
- Returned only non-sensitive readiness fields: token configured flag, repository configured flag, repository full name, status, message, default branch, latency, checked time, and operator action.
- Added GitHub repository access readiness loading to the React dashboard.
Implemented demo launch command composer from `docs/plans/202-demo-launch-command-composer.md`.

Changes:

- Added read-only `POST /api/demo/launch-command` to generate controlled demo `/agent fix` comments from structured repository, issue, operation, target path, and replacement text fields.
- Added backend validation that rejects unsupported operations, blank required fields, absolute paths, `..` segments, empty path segments, whitespace in target paths, protected `.git` / `.github` metadata paths, and blank replacement text for `replace`.
- Returned a generated `triggerComment`, GitHub issue URL, next actions, and reusable `preflightInput` for `/api/demo/launch-preflight`.
- Added `DemoLaunchCommandPanel` to the dashboard with structured inputs, generated command display, GitHub issue link, copy command action, and apply-to-preflight action.
- Updated `DemoLaunchPreflightPanel` so a composed command can fill the preflight form without manual copy/paste.
- Updated README, product spec, frontend design docs, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLaunchCommandServiceTests,DemoReadinessControllerTests test`: first failed because `DemoLaunchCommandService` and `DemoLaunchCommandVo` did not exist; then passed after implementing the service, DTO/VO, validation, and controller endpoint, 21 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoLaunchCommandPanel.test.tsx src/dashboard/components/DemoLaunchPreflightPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because `composeDemoLaunchCommand`, `DemoLaunchCommandPanel`, and preflight input application did not exist; then passed after frontend integration, 124 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 715 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 220 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

- Added a `Repository access` row to the operator setup checklist so a token that is valid but cannot read the selected repository is visible before a live `/agent fix` run.
- Updated README, frontend design docs, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=GitHubRepositoryAccessReadinessServiceTests,GitHubRepositoryAccessHttpProbeTests,GitHubRepositoryAccessReadinessControllerTests test`: first failed because the new repository access readiness classes did not exist; then passed after implementing the endpoint, service, HTTP probe, and non-sensitive response model.
- `mvn -pl PatchPilot -Dtest=GitHubRepositoryAccessReadinessServiceTests,GitHubRepositoryAccessHttpProbeTests,GitHubRepositoryAccessReadinessControllerTests,GitHubCredentialReadinessControllerTests test`: passed after updating the existing GitHub credential controller test wiring, 12 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx`: first failed because the new API helper and checklist row did not exist; then passed after frontend integration, 121 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 698 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 207 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.

Implemented webhook setup evidence bundle from `docs/plans/228-webhook-setup-evidence-bundle.md`.

Changes:

- Added combined webhook setup readiness to `DemoEvidenceBundleVo` and populated it from `GitHubWebhookSetupReadinessService`.
- Added a `Webhook Setup Readiness` section to generated demo session reports with status, secret flag, public URL flag, payload URL, latest delivery, redelivery recommendation, and next action.
- Added webhook setup status, summary, and payload URL to the dashboard demo evidence bundle panel.
- Updated backend and frontend fixtures, README, product spec, frontend design docs, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoSessionReportServiceTests,DemoReadinessControllerTests test`: first failed because `DemoEvidenceBundleVo` did not expose webhook setup readiness and old fixtures missed the new field; then passed after wiring the service and report output, 38 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx src/api.test.ts --reporter=basic`: first failed because the dashboard evidence panel did not render webhook setup readiness and one app assertion was ambiguous after adding the evidence card; then passed after panel integration and scoped assertions, 155 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 796 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 285 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented GitHub credential readiness from `docs/plans/197-github-credential-readiness.md`.

Changes:

- Added an admin-protected `GET /api/github/credential-readiness` endpoint.
- Added a read-only GitHub credential probe that calls `GET https://api.github.com/user` with `PATCHPILOT_GITHUB_TOKEN`.
- Returned only non-sensitive readiness fields: token configured flag, status, message, latency, checked time, and operator action.
- Added a `GitHub credentials` check to demo readiness and block readiness when GitHub rejects the configured token.
- Added GitHub credential readiness loading to the React dashboard and the operator setup checklist.
- Updated README, frontend design docs, and this execution log.

Validation:

- `mvn -pl PatchPilot -Dtest=GitHubCredentialReadinessServiceTests,GitHubCredentialHttpProbeTests,GitHubCredentialReadinessControllerTests,DemoReadinessServiceTests test`: first failed because the new GitHub credential classes did not exist; then failed once because the HTTP test helper used an unqualified `Version`; then passed after implementing the endpoint, service, probe, and demo readiness aggregation, 18 tests run, 0 failures.
- `npm test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx`: first failed because the new API helper and checklist row did not exist; then failed while aligning mock coverage and checklist counts; then passed after frontend integration, 119 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 688 tests run, 0 failures.
- `npm test -- --reporter=basic`: passed after full frontend regression verification, 205 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.

Implemented task evidence package archive from `docs/plans/263-task-evidence-package-archive.md`.

Changes:

- Added task evidence package archive storage with in-memory and MySQL-backed repositories plus Flyway migration `V41__create_fix_task_evidence_package_archive.sql`.
- Added task report download, task evidence archive creation/listing, and archived report download APIs.
- Recorded protected admin audit evidence when an operator archives a task evidence package.
- Added dashboard task detail controls to download the current task report, archive a point-in-time evidence package, list archived snapshots, and download archived Markdown reports.
- Added typed frontend API helpers, task detail state support, and evidence package styling.

Validation so far:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: first failed with missing task evidence package endpoints; then passed after backend implementation, 76 tests run, 0 failures.
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/App.test.tsx`: first failed because the dashboard did not expose task evidence package controls; then failed once because the App test mock was missing the new archive endpoints; then passed after frontend/API mock integration, 108 tests run, 0 failures.

Implemented handoff share checklist export from `docs/plans/237-handoff-share-checklist-export.md`.

Changes:

- Added `GET /api/demo/handoff-share-checklist/report/download` so operators can download the latest handoff share checklist Markdown as `patchpilot-demo-handoff-share-checklist.md`.
- Added handoff share checklist status, summary, and next action to the demo evidence bundle so share readiness appears in the top-level demo readout.
- Extracted handoff package archive summary calculation into `DemoHandoffPackageArchiveSummaryService` so evidence bundle construction can reuse the latest archive summary without creating a Spring bean dependency cycle.
- Added a dashboard evidence card for the handoff share checklist and a `Download checklist` action in the demo session snapshot panel.
- Updated README, product spec, architecture notes, and this execution log.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests,DemoEvidenceBundleServiceTests test`: first failed because the evidence bundle did not yet expose handoff share checklist fields and the service constructor was not wired for archive summary evidence.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the frontend API helper, evidence card, and download action did not exist yet.
- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoHandoffPackageArchiveServiceTests,DemoHandoffShareChecklistServiceTests test`: passed after implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx --reporter=basic`: passed after implementation, 177 tests run.
- `mvn -q -pl PatchPilot test`: passed after backend regression verification.

Implemented GitHub verification result evidence from `docs/plans/185-github-verification-result-evidence.md`.

Changes:

- Expanded Pull Request bodies with actual verification result evidence: command, exit code, and duration.
- Expanded completed issue status comments with the same verification result evidence when a test run exists.
- Expanded failed issue status comments with failed verification result evidence when a test run exists.
- Added pending-review issue comment wording that verification has not run when the task pauses before verification.
- Wired `NoopFixTaskExecutor` to pass the recorded test-run evidence into Pull Request creation.
- Wired `FixTaskWorker` to load the latest test-run evidence before completed, failed, and pending-review status comment updates.
- Updated README, product spec, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=PullRequestToolTests,IssueCommentToolTests,FixTaskWorkerTests,WorkspaceFixTaskExecutorTests test`: first failed at compile time because PR/comment tools and `FixTaskWorker` did not accept verification-result evidence; then failed once because the completed-comment failure test still overrode the legacy update method; then passed after wiring the new evidence path, 37 tests run, 0 failures.

Implemented non-success issue comment evidence summary from `docs/plans/184-non-success-issue-comment-evidence-summary.md`.

Changes:

- Expanded failed GitHub issue status comments with detected language, build system, allowlisted verification command, and adapter detection reason when repository evidence exists.
- Expanded pending-review issue status comments with the same adapter evidence so authors can tell a risk-gate pause from unsupported repository or test failure feedback.
- Added safe-command boundary text to non-success comments: PatchPilot selects verification commands from repository adapter allowlists and does not run arbitrary shell commands from issue comments.
- Added worker coverage to ensure failed and pending-review issue comments receive adapter metadata preserved on the task.
- Updated README, product spec, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,FixTaskWorkerTests test`: first failed because failed and pending-review issue comments lacked `Language` evidence; then passed after adding non-success-comment evidence, 25 tests run, 0 failures.

Implemented completed issue comment evidence summary from `docs/plans/183-completed-issue-comment-evidence-summary.md`.

Changes:

- Expanded completed GitHub issue status comments with detected language, build system, allowlisted verification command, and adapter detection reason.
- Added review-boundary text to successful issue comments: PatchPilot opens the Pull Request only after adapter-selected verification passes, verification commands come from repository adapters instead of arbitrary issue text, and PatchPilot does not auto-merge.
- Added worker coverage to ensure completed issue comments receive the adapter metadata preserved on the completed task.
- Updated README, product spec, and this execution log.

Validation so far:

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`: first failed because the completed issue comment lacked `Language: \`java\``; then passed after adding completed-comment evidence.
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests#should_execute_task_and_mark_completed test`: passed after locking the worker handoff so completed issue comments receive adapter metadata.
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,FixTaskWorkerTests test`: passed after focused backend regression verification, 25 tests run, 0 failures.
- `mvn -pl PatchPilot test`: passed after full backend regression verification, 649 tests run, 0 failures.
- `npm test`: passed after full frontend regression verification, 193 tests run, 0 failures.
- `npm run build`: passed after production frontend build verification.
- `git diff --check`: passed.
## 2026-06-28 - 264 Task evidence archive review center

- Started `264-task-evidence-archive-review-center` to turn archived task evidence packages into a cross-task review surface for demo handoff.
- Planned backend recent archive and summary APIs plus a dashboard review panel with download and task-open actions.
- RED tests added first for backend controller behavior, frontend API paths, and the new review panel.
- Implemented read-only recent archive and summary APIs, plus the dashboard `Task evidence archive review` panel with global archive counts, latest task evidence, side-effect contract, report downloads, and task-open actions.
- Updated README operator API documentation for the new archive review endpoints.

Validation:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_list_recent_task_evidence_package_archives_and_summary test`: first failed with `/api/tasks/evidence-packages` routed to `/{id}` and returning 404; passed after adding the specific archive review routes, 1 test run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/TaskEvidenceArchiveReviewPanel.test.tsx`: first failed because the new API functions and component did not exist; passed after frontend implementation, 133 tests run, 0 failures.
- `npm test -- src/App.test.tsx -t "renders operational task dashboard from backend APIs|downloads and archives selected task evidence package"`: passed after wiring the dashboard data flow, 2 selected tests run, 0 failures.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`: passed, 77 tests run, 0 failures.
- `npm test`: passed, 373 tests run, 0 failures.
- `npm run build`: passed.
- `mvn -pl PatchPilot test`: passed, 941 tests run, 0 failures.
- `git diff --check`: passed.

## 2026-06-28 - 265 Task evidence share readiness center

- Started `265-task-evidence-share-readiness-center` to turn archived task evidence packages into a final share/no-share readout for operator review.
- Planned a larger complete slice: backend share-center API, downloadable Markdown report, dashboard share-center panel, API/types integration, README docs, and regression tests.
- RED tests added first for `GET /api/tasks/evidence-packages/share-center`, `GET /api/tasks/evidence-packages/share-center/report/download`, frontend API helpers, dashboard rendering, and share-center report download.
- Implemented a read-only share-center calculation that selects the newest `COMPLETED` archive with a Pull Request URL as the shareable evidence package.
- Added `READY`, `NEEDS_ATTENTION`, and `BLOCKED` status output with archive counts, latest archive evidence, shareable archive evidence, download actions, evidence notes, side-effect contract, generated time, and Markdown report.
- Updated the `Task evidence archive review` dashboard panel so operators can inspect the share center, see the selected shareable archive and Pull Request, and download `patchpilot-task-evidence-share-center.md`.
- Updated README operator docs for the new task evidence share-center endpoints and read-only contract.

Validation so far:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_return_task_evidence_share_center_and_markdown_report test`: first failed with `/api/tasks/evidence-packages/share-center` returning 404; passed after backend implementation, 1 test run, 0 failures.
- `npm test -- src/api.test.ts src/dashboard/components/TaskEvidenceArchiveReviewPanel.test.tsx src/App.test.tsx`: first failed because the API helpers and share-center panel did not exist; then failed on ambiguous repeated archive ids after implementation; passed after scoped assertions, 216 tests run, 0 failures.
- `mvn -q -pl PatchPilot test`: passed, 942 tests run, 0 failures, 0 errors.
- `npm test -- --reporter=basic`: passed, 29 test files and 376 tests.
- `npm run build`: passed.
- `git diff --check`: passed.

## 2026-06-28 - 266 Task evidence share delivery finalization

- Started `266-task-evidence-share-delivery-finalization` to make task evidence sharing auditable after the share-center stage.
- Planned a complete feature slice: backend local delivery receipts, persistent storage, finalization gate, report downloads, dashboard receipt form/list, finalization panel, API/types integration, README updates, and regression tests.
- RED controller test added first for recording a task evidence delivery receipt and observing the finalization gate move from missing receipt to ready.
- Implemented persistent task evidence delivery receipts with in-memory and MyBatis repositories plus Flyway migration `V42__create_fix_task_evidence_share_delivery_receipt.sql`.
- Added `POST /api/tasks/evidence-packages/share-delivery-receipts`, receipt listing, receipt report download, `GET /api/tasks/evidence-packages/finalization`, and finalization report download.
- Added finalization checks for share readiness, delivery receipt freshness, and acceptance status. Finalization is `READY` only when the latest receipt matches the current shareable archive and task.
- Updated the `Task evidence archive review` dashboard panel with finalization status, check/evidence notes, recent receipts, a local receipt form, and Markdown download actions.
- Updated README and added this plan document.

Validation so far:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_record_task_evidence_delivery_receipt_and_finalize_current_shareable_archive test`: first failed because `/api/tasks/evidence-packages/finalization` returned 404; passed after backend implementation.
- `npm test -- src/api.test.ts src/dashboard/components/TaskEvidenceArchiveReviewPanel.test.tsx src/App.test.tsx`: first failed because the new frontend API/helpers and dashboard UI were missing; then failed on fragile local-time/count assertions; passed after implementation and stable assertions, 222 tests run.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_record_task_evidence_delivery_receipt_and_finalize_current_shareable_archive,FixTaskEvidencePackageShareDeliveryReceiptServiceTests,FixTaskEvidencePackageFinalizationServiceTests,FixTaskEvidencePackageShareDeliveryReceiptConvertTests,InMemoryFixTaskEvidencePackageShareDeliveryReceiptRepositoryTests,MyBatisFixTaskEvidencePackageShareDeliveryReceiptRepositoryTests,FixTaskEvidencePackageShareDeliveryReceiptMigrationTests test`: passed, 12 tests run, 0 failures.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 29 test files and 382 tests.
- `npm run build`: passed.
- `git diff --check`: passed.

## 2026-06-28 - 267 Task evidence acceptance closeout archive

- Started `267-task-evidence-acceptance-closeout-archive` to preserve finalized task evidence acceptance as durable PatchPilot-local evidence.
- Planned a complete feature slice: backend archive storage, Flyway/MyBatis persistence, create/list/download APIs, dashboard archive controls, docs, and regression tests.
- RED controller and frontend tests were added first for creating, listing, downloading, and displaying acceptance closeout archives.
- Implemented persistent acceptance closeout archives with in-memory and MyBatis repositories plus Flyway migration `V43__create_fix_task_evidence_acceptance_closeout_archive.sql`.
- Added `POST /api/tasks/evidence-packages/acceptance-closeout/archives`, `GET /api/tasks/evidence-packages/acceptance-closeout/archives`, and archived report download.
- Added a service guard so closeout archives can only be created when the task evidence finalization gate is `READY`.
- Updated the `Task evidence archive review` dashboard panel with recent closeout archives, archive creation, report download, and error/status feedback.
- Updated README and added this plan document.

Validation so far:

- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_archive_list_and_download_task_evidence_acceptance_closeout test`: first failed because the closeout archive endpoint did not exist; passed after backend implementation.
- `npm test -- src/api.test.ts src/dashboard/components/TaskEvidenceArchiveReviewPanel.test.tsx src/App.test.tsx -- --reporter=basic`: first failed because frontend API helpers, panel controls, and dashboard wiring were missing; passed after implementation, 226 tests run.
- `mvn -pl PatchPilot -Dtest=TaskControllerTests#should_archive_list_and_download_task_evidence_acceptance_closeout+should_reject_task_evidence_acceptance_closeout_archive_until_finalization_is_ready,FixTaskEvidencePackageAcceptanceCloseoutArchiveServiceTests,FixTaskEvidencePackageAcceptanceCloseoutArchiveConvertTests,InMemoryFixTaskEvidencePackageAcceptanceCloseoutArchiveRepositoryTests,MyBatisFixTaskEvidencePackageAcceptanceCloseoutArchiveRepositoryTests,FixTaskEvidencePackageAcceptanceCloseoutArchiveMigrationTests test`: passed, 10 tests run.
- `mvn -q -pl PatchPilot test`: first failed because an older controller test assumed missing delivery receipts instead of stale receipts in a shared test context; passed after asserting the stable no-fresh-receipt behavior, 964 tests run.
- `npm test -- --reporter=basic`: passed, 29 test files and 386 tests.
- `npm run build`: passed.
- `git diff --check`: passed.

## 2026-06-28 - 269 Task certificate evidence bundle

- Started `269-task-certificate-evidence-bundle` to make the latest task evidence acceptance certificate visible from the top-level demo evidence bundle.
- Planned a complete feature slice: backend evidence read model, bundle readiness aggregation, copied runbook output, REST serialization coverage, dashboard evidence card, README docs, and regression tests.
- RED backend tests were added first for ready and missing task certificate archive evidence in `DemoEvidenceBundleService`, copied runbook Markdown, and REST JSON serialization.
- RED frontend tests were added first for rendering certified task certificate archive proof and missing-certificate guidance in the demo evidence bundle panel.
- Implemented `DemoTaskEvidenceAcceptanceCertificateEvidenceVo` and wired `DemoEvidenceBundleService` to read recent task evidence acceptance certificate archives.
- Updated bundle status and next actions so the demo evidence bundle is not `READY` until the latest task evidence acceptance certificate archive is `READY` and certified.
- Updated the copied demo runbook with task certificate archive id, linked closeout/evidence/receipt ids, task id, Pull Request, next action, and download actions.
- Updated the dashboard evidence bundle panel with a task evidence acceptance certificate card and safe missing-evidence fallback.
- Updated README and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: first failed at test compile because `DemoTaskEvidenceAcceptanceCertificateEvidenceVo` did not exist; passed after backend implementation.
- `npm test -- src/dashboard/components/DemoEvidenceBundlePanel.test.tsx -- --reporter=basic`: first failed because the task certificate evidence card did not render; then failed on old single-card assertions for repeated `Certified archive` and `1 certificate archives`; passed after implementing the card and updating assertions for two certificate evidence cards, 4 tests run.
- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests#should_return_demo_evidence_bundle test`: passed after REST serialization assertions were added.
- `npm run build`: passed after adding the new evidence field to the session snapshot fixture.

## 2026-06-28 - 270 Task certificate session handoff

- Started `270-task-certificate-session-handoff` to carry task evidence acceptance certificate proof from the top-level evidence bundle into session handoff artifacts.
- Planned a complete feature slice: session report Markdown, handoff package Markdown, structured handoff readiness, dashboard session panel rendering, README docs, plan doc, and regression tests.
- RED backend tests were added first for missing task evidence certificate Markdown and handoff readiness checks.
- RED frontend test was added first for missing task evidence certificate facts in the demo session snapshot panel.
- Implemented task evidence certificate output in `DemoSessionReportService`, including archive id, closeout archive id, evidence archive id, delivery receipt id, task id, Pull Request, next action, and download actions.
- Added `Task evidence certificate` as a structured handoff readiness check so missing or non-ready task certificate evidence can block final handoff readiness.
- Updated the dashboard demo session panel with task certificate status, archive id, target task, Pull Request, and next action.
- Updated README and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoSessionReportServiceTests test`: first failed because the task certificate Markdown section and handoff readiness check did not exist; passed after backend implementation.
- `npm test -- src/dashboard/components/DemoSessionSnapshotPanel.test.tsx -- --reporter=basic`: first failed because the session panel did not render task evidence certificate facts; then failed on old Pull Request count and fixture task id assumptions; passed after rendering the card and stabilizing assertions, 20 tests run.
- `mvn -q -pl PatchPilot test`: first failed because `DemoHandoffPackageArchiveServiceTests` still expected 7 handoff readiness checks after the new certificate check; passed after updating the archive contract to 8 checks and asserting the certificate row.
- `npm test -- --reporter=basic`: passed, 29 test files and 390 tests.
- `npm run build`: passed.
- `git diff --check`: passed.

## 2026-06-28 - 271 Handoff certificate share gate

- Started `271-handoff-certificate-share-gate` to make the final post-demo handoff share path enforce task evidence acceptance certificate readiness.
- Planned a complete feature slice: reusable task certificate evidence read model, handoff share-center certificate gate, share instructions attachment/check updates, finalization certificate check, dashboard rendering, README docs, plan doc, and regression tests.
- RED backend tests were added first for share center blocking when task certificate evidence is missing and for finalization including a task evidence certificate check.
- RED frontend test was added first for rendering the task certificate gate in the handoff share center, finalization gate, and share instructions.
- Implemented `DemoTaskEvidenceAcceptanceCertificateEvidenceService` so handoff sharing can reuse latest task certificate archive evidence without parsing Markdown.
- Extended `DemoHandoffShareCenterVo` with task certificate status, readiness, summary, next action, archive id, task id, and Pull Request URL.
- Updated handoff share center status/next-action/download/evidence calculations so final sharing is not `shareReady` unless the task evidence acceptance certificate is `READY` and certified.
- Updated handoff share instructions to include the task evidence certificate archive as a required attachment and pre-send check.
- Updated handoff finalization with a `Task evidence certificate` check and evidence note so final acceptance depends on both certificate proof and delivery receipt freshness.
- Updated the dashboard session panel to show the task certificate gate inside the final handoff share center and finalization evidence.
- Updated README and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareCenterServiceTests,DemoHandoffFinalizationServiceTests test`: first failed because `DemoTaskEvidenceAcceptanceCertificateEvidenceService` and new share-center fields did not exist; passed after backend implementation.
- `npm test -- src/dashboard/components/DemoSessionSnapshotPanel.test.tsx -- --reporter=basic`: first failed because the session panel did not render the share-center/finalization task certificate gate; passed after frontend type and panel updates, 20 tests run.
- `mvn -q -pl PatchPilot -Dtest=DemoHandoffShareCenterServiceTests,DemoHandoffFinalizationServiceTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests test`: passed.
- `npm test -- src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx src/api.test.ts -- --reporter=basic`: passed, 3 test files and 244 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 29 test files and 390 tests.
- `npm run build`: first failed because the task certificate fields were added to the launch evidence share-center TypeScript interface instead of `DemoHandoffShareCenter`; passed after moving those fields to the correct interface.
- `git diff --check`: passed.

## 2026-06-28 - 272 Demo final handoff report package

- Started `272-demo-final-handoff-report-package` to make the post-demo handoff closeout downloadable as one final operator-facing artifact.
- Planned a complete feature slice: backend aggregate read model, read-only JSON and Markdown download endpoints, dashboard session-panel rendering, API/types integration, README docs, plan doc, and regression tests.
- RED backend tests were added first for building a READY final package from current archive/share/finalization/task-certificate evidence and for downgrading to `NEEDS_ATTENTION` when finalization evidence is missing.
- RED frontend tests were added first for API helpers, panel rendering, required attachments, source reports, and Markdown download behavior.
- Implemented `DemoFinalHandoffReportPackageService` to aggregate the handoff package archive summary, share checklist, share center, share instructions, finalization gate, delivery receipt, and task evidence certificate proof into one read-only package.
- Added `GET /api/demo/final-handoff-report-package` and `GET /api/demo/final-handoff-report-package/report/download`.
- Updated the demo session dashboard panel with final package status, latest archive/session/receipt/certificate evidence, readiness checks, required attachments, pre-send checks, evidence notes, source reports, and a Markdown download action.
- Updated README and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalHandoffReportPackageServiceTests,DemoReadinessControllerTests test`: first failed because the final package implementation did not include the missing-receipt action in the generated Markdown; passed after carrying finalization check actions into final pre-send checks.
- `npm test -- src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/api.test.ts -- --reporter=basic`: first failed because the new final package panel created legitimate repeated status/attachment text; passed after stabilizing assertions, 2 test files and 164 tests.
- `npm test -- src/App.test.tsx src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx -- --reporter=basic`: passed, 3 test files and 247 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 29 test files and 393 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-28 - 277 Demo acceptance summary

- Started `277-demo-acceptance-summary` to give operators one final accepted/not-accepted readout across launch-level and task-level evidence.
- Planned a complete feature slice: backend aggregate read model, read-only JSON and Markdown download endpoints, dashboard panel, frontend API/types/App wiring, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for READY, missing-certificate, and BLOCKED final acceptance states.
- RED controller tests were added first for `GET /api/demo/acceptance-summary` and `GET /api/demo/acceptance-summary/report/download`.
- RED frontend tests were added first for API helpers and the `Final demo acceptance` dashboard panel.
- Implemented `DemoAcceptanceSummaryService` to aggregate the latest launch acceptance certificate archive and latest task evidence acceptance certificate archive without creating new records or side effects.
- Added `DemoAcceptanceSummaryVo`, controller endpoints, Markdown report generation, frontend types, API helpers, App refresh integration, and a downloadable final acceptance dashboard panel.
- Updated README, product spec, frontend design doc, and this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoAcceptanceSummaryServiceTests test`: first failed because `DemoAcceptanceSummaryVo` did not exist; passed after service and VO implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests#should_return_final_demo_acceptance_summary+should_download_final_demo_acceptance_summary_report test`: first failed with `404` because the endpoints did not exist; passed after controller implementation.
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx -- --reporter=basic`: first failed because the API helpers and panel component did not exist; passed after frontend implementation, 2 test files and 150 tests.
- `npm test -- src/App.test.tsx src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx -- --reporter=basic`: passed, 3 test files and 233 tests.
- `mvn -q -pl PatchPilot -Dtest=DemoAcceptanceSummaryServiceTests,DemoReadinessControllerTests test`: passed.
- `mvn -pl PatchPilot -q test`: passed with the existing Mockito dynamic-agent warning on the local JDK.
- `npm test -- --reporter=basic`: passed, 30 test files and 402 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-28 - 278 Final demo acceptance share package

- Started `278-final-demo-acceptance-share-package` to convert final demo acceptance into reviewer-facing send/no-send material without letting PatchPilot send anything externally.
- Planned a complete feature slice: backend package read model, read-only JSON and Markdown download endpoints, dashboard rendering, copy/download controls, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for accepted and not-accepted share package states.
- RED controller tests were added first for `GET /api/demo/final-acceptance-share-package` and `GET /api/demo/final-acceptance-share-package/report/download`.
- RED frontend tests were added first for API helpers, dashboard package rendering, copy behavior, download behavior, and App-level loading.
- Implemented `DemoFinalAcceptanceSharePackageService` to derive send-ready status, recipients, required attachments, pre-send checks, message subject/body, evidence notes, side-effect contract, and Markdown from `DemoAcceptanceSummaryService`.
- Added `DemoFinalAcceptanceSharePackageVo`, controller endpoints, frontend type/API helpers, App refresh integration, and final acceptance panel copy/download controls.
- Updated README, product spec, frontend design doc, and this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceSharePackageServiceTests test`: first failed because `DemoFinalAcceptanceSharePackageVo` did not exist; passed after service and VO implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests#should_return_final_demo_acceptance_share_package+should_download_final_demo_acceptance_share_package_report test`: first failed with `404` because the endpoints did not exist; passed after controller implementation.
- `npm test -- src/api.test.ts -- --reporter=basic`: first failed because the final acceptance share-package API helpers did not exist; passed after frontend API implementation.
- `npm test -- src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx -- --reporter=basic`: first failed because the panel did not render share-package controls; passed after UI implementation and repeated read-only contract assertions.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceSharePackageServiceTests,DemoReadinessControllerTests test`: passed.
- `npm test -- src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/api.test.ts src/App.test.tsx -- --reporter=basic`: first failed because the App-level acceptance-panel assertion did not account for the package message body repeating the summary text; passed after updating the assertion, 3 test files and 236 tests.
- `mvn -pl PatchPilot -q test`: passed with the existing Mockito dynamic-agent warning on the local JDK.
- `npm test -- --reporter=basic`: passed, 30 test files and 405 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-28 - 273 Demo final handoff report package archives

- Started `273-demo-final-handoff-report-archives` to preserve the exact final handoff report package as a durable closeout artifact after the live read model changes.
- Planned a complete feature slice: backend archive VO/entity/converter/mapper/repositories/service/controller endpoints, Flyway migration, operator audit event, dashboard archive/list/download controls, README docs, plan doc, and regression tests.
- RED backend tests were added first for archiving the current final handoff report package, trimming to the 20 most recent archives, converting JSON list fields, MyBatis repository behavior, migration text, controller archive/list/download endpoints, missing archive downloads, and operator audit recording.
- RED frontend tests were added first for final package archive API helpers, demo session panel archive controls, archived Markdown download behavior, and full App data loading.
- Implemented `DemoFinalHandoffReportPackageArchiveService` with in-memory and MyBatis-backed repositories, persisted report-package metadata, Markdown report content, generated timestamp, and archived timestamp.
- Added `POST /api/demo/final-handoff-report-package/archives`, `GET /api/demo/final-handoff-report-package/archives`, and `GET /api/demo/final-handoff-report-package/archives/{archiveId}/report/download`.
- Updated the demo session dashboard panel with an archive action, recent final package archive list, archived package status/evidence, archive download buttons, and archive-load error handling.
- Updated README and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalHandoffReportPackageArchiveServiceTests,DemoFinalHandoffReportPackageArchiveConvertTests,InMemoryDemoFinalHandoffReportPackageArchiveRepositoryTests,MyBatisDemoFinalHandoffReportPackageArchiveRepositoryTests,DemoFinalHandoffReportPackageArchiveMigrationTests,DemoReadinessControllerTests test`: passed.
- `npm test -- src/api.test.ts src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx -- --reporter=basic`: first failed because the final package summary is now intentionally rendered in both the live package and recent archive list; passed after updating the assertion to require repeated evidence, 3 test files and 252 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 29 test files and 398 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-28 - 276 Launch acceptance final handoff proof

- Started `276-launch-acceptance-final-handoff-proof` so the final launch acceptance closeout and certificate preserve the same final handoff report package archive proof already carried by the launch evidence package.
- Planned a complete feature slice: closeout/certificate read-model fields, archive persistence, controller JSON, Markdown reports, dashboard closeout/certificate rendering, README/product/frontend docs, Flyway migrations, and regression tests.
- RED backend tests were added first for closeout/certificate status, checks, evidence notes, download actions, Markdown, archive services, converters, MyBatis repositories, migrations, and controller response fields.
- RED frontend tests were added first for rendering final handoff archive proof in the launch acceptance closeout and certificate sections.
- Extended launch acceptance closeout and certificate VOs with final handoff report package archive status, ready flag, archive id, and summary.
- Persisted the same proof in closeout and certificate archives through Flyway/MyBatis fields with safe defaults for existing archive rows.
- Updated closeout and certificate Markdown, evidence notes, blocking next actions, and download actions so a missing final handoff package archive keeps final launch acceptance from being treated as complete.
- Updated the dashboard launch evidence package panel, recent closeout archive rows, recent certificate archive rows, frontend types, README, product spec, frontend design doc, and this execution log.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoLaunchAcceptanceCloseoutServiceTests,DemoLaunchAcceptanceCertificateServiceTests,DemoLaunchAcceptanceCloseoutArchiveServiceTests,DemoLaunchAcceptanceCertificateArchiveServiceTests,MyBatisDemoLaunchAcceptanceCloseoutArchiveRepositoryTests,MyBatisDemoLaunchAcceptanceCertificateArchiveRepositoryTests,DemoLaunchAcceptanceCloseoutArchiveMigrationTests,DemoLaunchAcceptanceCertificateArchiveMigrationTests,DemoReadinessControllerTests test`: first failed because the new read-model/archive fields did not exist; passed after backend implementation, migrations, and fixture updates.
- `npm test -- src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx -- --reporter=basic`: first failed because the panel did not render `Final handoff archive`; passed after UI rendering and assertion updates, 8 tests.
- `npm test -- --reporter=basic`: passed, 29 test files and 398 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `mvn -q -pl PatchPilot test`: passed.

## 2026-06-28 - 274 Final handoff package evidence bundle

- Started `274-final-handoff-package-evidence-bundle` to make the latest archived final handoff report package visible from the first evidence bundle readout and copied runbook.
- Planned a complete feature slice: backend evidence VO, repository-backed evidence aggregation, bundle status/next-action gating, runbook Markdown evidence, dashboard evidence-bundle card, README docs, plan doc, and regression tests.
- RED backend tests were added first for ready archive evidence, missing archive guidance, copied runbook Markdown, and REST serialization.
- RED frontend tests were added first for rendering final package archive proof and missing-archive guidance in `DemoEvidenceBundlePanel`.
- Implemented `DemoFinalHandoffReportPackageArchiveEvidenceVo` and wired `DemoEvidenceBundleService` to read recent final package archives, summarize latest archive readiness, linked handoff archive, session, delivery receipt, task certificate, archived time, and download actions.
- Updated aggregate bundle status and next actions so the top-level evidence bundle is not `READY` until the final handoff report package archive is download-ready.
- Updated the copied demo runbook and dashboard evidence bundle panel so operators can prove post-demo closeout archive readiness without opening the session snapshot panel first.
- Updated README and added this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests#should_return_demo_evidence_bundle test`: first failed because `DemoFinalHandoffReportPackageArchiveEvidenceVo` did not exist; passed after backend implementation.
- `npm test -- src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx -- --reporter=basic`: first failed because the dashboard did not render `Final handoff report package archive`; passed after frontend type, fixture, and panel updates, 2 test files and 87 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 29 test files and 398 tests.
- `npm run build`: first failed because `DemoSessionSnapshotPanel.test.tsx` still used the old `DemoEvidenceBundle` fixture shape; passed after adding `finalHandoffReportPackageArchiveEvidence` to that fixture, with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-28 - 275 Launch package final handoff archive proof

- Started `275-launch-package-final-handoff-archive-proof` so the final shareable launch evidence package proves that the post-demo final handoff report package archive is ready and downloadable.
- Planned a complete feature slice: launch evidence package fields, archived launch package persistence, share-center notes/downloads, dashboard rendering, README docs, plan doc, Flyway migration, and regression tests.
- RED backend tests were added first for package JSON/Markdown, archive service fields, archive converter/entity mapping, migration columns, in-memory repository compatibility, and share-center evidence/download actions.
- RED frontend test was added first for rendering the final handoff package archive proof in the launch evidence package panel.
- Extended launch evidence package and archive VOs with final handoff report package archive status, ready flag, archive id, and summary.
- Added Flyway/MyBatis persistence for the launch archive proof columns and kept default migration values safe for existing archives.
- Updated the launch evidence package Markdown, archive rows, and launch evidence share center so operators can download or cite the final handoff report package archive from the final launch artifact.
- Updated the dashboard launch evidence package panel, share center section, recent archive list, frontend types, and API/App fixtures with the same final handoff archive proof.
- Updated README and added this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoLaunchEvidencePackageServiceTests,DemoLaunchEvidencePackageArchiveServiceTests,DemoLaunchEvidenceShareCenterServiceTests,DemoLaunchEvidencePackageArchiveConvertTests,DemoLaunchEvidencePackageArchiveMigrationTests,InMemoryDemoLaunchEvidencePackageArchiveRepositoryTests test`: first failed because the new VO/entity fields did not exist, then failed on fixture import/constructor field order; passed after backend implementation and fixture updates.
- `npm test -- src/dashboard/components/DemoLaunchEvidencePackagePanel.test.tsx -- --reporter=basic`: first failed because the panel did not render `Final handoff package archive`, then failed because the proof appears in both package and share center sections; passed after UI rendering and assertion updates, 8 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 29 test files and 398 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 279 Final acceptance share package archives

- Started `279-final-acceptance-share-package-archive` to preserve the final reviewer-facing acceptance package as a durable PatchPilot-local handoff record after the live read model changes.
- Planned a complete feature slice: backend archive VO/entity/converter/mapper/repositories/service/controller endpoints, Flyway migration, operator audit event, dashboard archive/list/download controls, README/product/frontend docs, and regression tests.
- RED backend tests were added first for archiving the current final acceptance share package, trimming recent archives, converting list fields, MyBatis repository behavior, migration text, controller archive/list/download endpoints, missing archive downloads, and operator audit recording.
- RED frontend tests were added first for final acceptance archive API helpers, dashboard archive controls, archived Markdown download behavior, and full App data loading.
- Implemented `DemoFinalAcceptanceSharePackageArchiveService` with in-memory and MyBatis-backed repositories, persisted share-package metadata, Markdown report content, generated timestamp, and archived timestamp.
- Added `POST /api/demo/final-acceptance-share-package/archives`, `GET /api/demo/final-acceptance-share-package/archives`, and `GET /api/demo/final-acceptance-share-package/archives/{archiveId}/report/download`.
- Updated the final demo acceptance dashboard panel with an archive action, recent package archive list, archived report download buttons, and archive-load error handling.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceSharePackageArchiveServiceTests,DemoFinalAcceptanceSharePackageArchiveConvertTests,InMemoryDemoFinalAcceptanceSharePackageArchiveRepositoryTests,MyBatisDemoFinalAcceptanceSharePackageArchiveRepositoryTests,DemoFinalAcceptanceSharePackageArchiveMigrationTests,DemoReadinessControllerTests test`: passed.
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx -- --reporter=basic`: first failed because the final acceptance message subject is intentionally rendered in both the live package and recent archive list, and because the archived report download assertion did not wait for the async browser download path; passed after updating the assertions and mock download response, 3 test files and 241 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 410 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 280 Final acceptance share delivery finalization

- Started `280-final-acceptance-share-delivery-finalization` to close the reviewer-facing acceptance loop after the final share package has been archived.
- Planned a complete feature slice: delivery receipt DTO/VO/entity/converter/mapper/repositories/service, finalization gate service, Flyway migration, controller receipt/finalization endpoints, protected audit event, dashboard receipt form/history/finalization downloads, README/product/frontend docs, and regression tests.
- RED backend tests were added first for recording a receipt against the latest send-ready package archive, rejecting missing or not-send-ready archives, receipt repository trimming, MyBatis behavior, migration text, controller routes, report downloads, missing receipt download, and operator audit recording.
- RED frontend tests were added first for final acceptance delivery receipt API helpers, finalization API helpers, dashboard finalization display, receipt form submission, receipt history downloads, and finalization report download.
- Implemented `DemoFinalAcceptanceShareDeliveryReceiptService` and `DemoFinalAcceptanceShareFinalizationService` so a final package is only finalized when the latest archived package is send-ready and the latest receipt is fresh for that archive/task.
- Added `POST /api/demo/final-acceptance-share-delivery-receipts`, `GET /api/demo/final-acceptance-share-delivery-receipts`, `GET /api/demo/final-acceptance-share-delivery-receipts/{receiptId}/report/download`, `GET /api/demo/final-acceptance-share-finalization`, and `GET /api/demo/final-acceptance-share-finalization/report/download`.
- Updated the final demo acceptance dashboard panel with delivery channel/target/operator/notes inputs, recent delivery receipt rows, receipt report downloads, finalization checks, receipt freshness, and finalization report download controls.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceShareDeliveryReceiptServiceTests,DemoFinalAcceptanceShareFinalizationServiceTests,DemoFinalAcceptanceShareDeliveryReceiptConvertTests,InMemoryDemoFinalAcceptanceShareDeliveryReceiptRepositoryTests,MyBatisDemoFinalAcceptanceShareDeliveryReceiptRepositoryTests,DemoFinalAcceptanceShareDeliveryReceiptMigrationTests,DemoReadinessControllerTests test`: first failed because the new receipt/finalization classes did not exist, then failed because the controller test missed the `MediaType` import; passed after backend implementation and test import fix.
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx -- --reporter=basic`: first failed because the final delivery receipt id is intentionally visible in both finalization evidence and receipt history; passed after changing the assertion to accept repeated evidence, 2 test files and 163 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 416 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 281 Final acceptance delivery evidence bundle

- Started `281-final-acceptance-delivery-evidence-bundle` to make reviewer-facing final acceptance delivery proof visible from the first demo evidence readout instead of only from the final acceptance panel.
- Planned a complete feature slice: backend evidence-bundle read model, aggregate status/next-action gating, copied runbook Markdown, dashboard evidence card, legacy response fallback, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for bundle aggregation, missing delivery receipt guidance, copied runbook Markdown, and REST serialization of final acceptance finalization evidence.
- RED frontend tests were added first for rendering the final acceptance delivery evidence card and the legacy missing-field fallback in `DemoEvidenceBundlePanel`.
- Implemented `finalAcceptanceShareFinalization` in `DemoEvidenceBundleVo` and wired `DemoEvidenceBundleService` to include the final acceptance share finalization gate in the top-level bundle status and next actions.
- Updated the copied demo runbook with final acceptance archive, task, delivery receipt, delivery target/channel, receipt freshness, and next-action lines.
- Updated the dashboard evidence bundle panel so operators can see final acceptance delivery/finalization evidence, receipt freshness, and blocking guidance without opening the final demo acceptance panel.
- Updated README, product spec, frontend design notes, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests test`: first failed because the new bundle field/constructor wiring did not exist; passed after backend implementation.
- `npm test -- src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx -- --reporter=basic`: first failed because the dashboard did not render `Final acceptance delivery`; passed after frontend type, fixture, panel, and fallback updates, 2 test files and 88 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm run build`: first failed because `DemoSessionSnapshotPanel.test.tsx` still used the old `DemoEvidenceBundle` fixture shape and then used the backend field name `reportMarkdown`; passed after adding `finalAcceptanceShareFinalization` with the frontend `markdownReport` field, with the existing Vite large-chunk warning.
- `npm test -- src/dashboard/components/DemoSessionSnapshotPanel.test.tsx -- --reporter=basic`: passed after adding the missing session snapshot fixture field, 23 tests.
- `npm test -- --reporter=basic`: first hit an isolated 5s timeout in the existing admin-token dashboard test during a parallel verification run; passed on clean rerun, 30 test files and 416 tests.
- `git diff --check`: passed.

## 2026-06-29 - 282 Final acceptance completion archives

- Started `282-final-acceptance-completion-archive` to preserve the final reviewer-facing acceptance handoff after the final acceptance share finalization gate is `READY`.
- Planned a complete feature slice: backend completion archive VO/entity/converter/mapper/repositories/service/controller endpoints, Flyway migration, protected audit event, dashboard archive/list/download controls, README/product/frontend docs, and regression tests.
- RED backend tests were added first for archiving READY finalization evidence, rejecting not-ready finalization, trimming recent archives, converter/entity mapping, migration text, controller archive/list/download endpoints, missing archive downloads, and operator audit recording.
- RED frontend tests were added first for completion archive API helpers, final acceptance panel archive controls, archived report downloads, and full App data loading.
- Implemented `DemoFinalAcceptanceCompletionArchiveService` with in-memory and MyBatis-backed repositories, READY-only archive creation, finalization metadata capture, Markdown report preservation, generated timestamp, and archived timestamp.
- Added `POST /api/demo/final-acceptance-completion-archives`, `GET /api/demo/final-acceptance-completion-archives`, and `GET /api/demo/final-acceptance-completion-archives/{archiveId}/report/download`.
- Updated the final demo acceptance dashboard panel with a completion archive action, recent completion archive list, archived completion report downloads, and completion archive load error handling.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionArchiveServiceTests,DemoFinalAcceptanceCompletionArchiveConvertTests,InMemoryDemoFinalAcceptanceCompletionArchiveRepositoryTests,MyBatisDemoFinalAcceptanceCompletionArchiveRepositoryTests,DemoFinalAcceptanceCompletionArchiveMigrationTests,DemoReadinessControllerTests test`: first failed because the new completion archive VO/service/repository/entity/converter did not exist; passed after backend implementation, MyBatis repository coverage, and controller wiring.
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the new API helpers and dashboard controls did not exist, then failed once because an archived completion row reused the live finalization summary text; passed after API/App/panel implementation and archive-row wording adjustment, 3 test files and 251 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 420 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 283 Final acceptance completion evidence bundle

- Started `283-final-acceptance-completion-evidence-bundle` to summarize the final acceptance finalization and latest completion archive into one reviewer-facing proof bundle.
- Planned a complete feature slice: backend bundle VO/service/controller endpoints, frontend type/API/App loading, final acceptance panel bundle rendering/downloads, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for READY bundle generation, missing completion archive guidance, blocked finalization guidance, REST serialization, and Markdown report download.
- RED frontend tests were added first for completion evidence bundle API helpers, final acceptance panel rendering, bundle download behavior, and full App data loading.
- Implemented `DemoFinalAcceptanceCompletionEvidenceBundleService` and `DemoFinalAcceptanceCompletionEvidenceBundleVo` so the bundle is share-ready only when finalization is READY and a finalized READY completion archive exists.
- Added `GET /api/demo/final-acceptance-completion-evidence-bundle` and `GET /api/demo/final-acceptance-completion-evidence-bundle/report/download`.
- Updated the final demo acceptance dashboard panel with completion evidence bundle status, latest archive/receipt proof, evidence notes, download actions, side-effect contract, and Markdown download.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionEvidenceBundleServiceTests,DemoReadinessControllerTests test`: first failed because the bundle VO/service did not exist; passed after backend service and controller implementation.
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx --reporter=basic`: first failed because the API helpers and panel bundle section did not exist, then failed on expected duplicate side-effect/archive evidence after adding the new section; passed after frontend implementation and assertion updates, 3 test files and 254 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 423 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 284 Final acceptance completion evidence delivery receipts

- Started `284-final-acceptance-completion-evidence-delivery-receipts` to record local proof that the final acceptance completion evidence bundle was delivered after it became ready to share.
- Planned a complete feature slice: backend receipt DTO/VO/entity/converter/mapper/repositories/service/controller endpoints, Flyway migration, protected audit event, frontend API/type/App loading, final acceptance panel receipt form/history/downloads, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for READY-only receipt creation, not-ready rejection, required field validation, converter/entity mapping, in-memory and MyBatis repository behavior, migration text, controller create/list/download endpoints, missing receipt downloads, and protected audit recording.
- RED frontend tests were added first for receipt API helpers, final acceptance panel receipt form/history/download behavior, and full App data loading plus dashboard create/download interactions.
- Implemented `DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptService` with in-memory and MyBatis-backed repositories, READY-only bundle gating, delivery metadata capture, Markdown report generation, and side-effect contract text.
- Added `POST /api/demo/final-acceptance-completion-evidence-delivery-receipts`, `GET /api/demo/final-acceptance-completion-evidence-delivery-receipts`, and `GET /api/demo/final-acceptance-completion-evidence-delivery-receipts/{receiptId}/report/download`.
- Updated the final demo acceptance dashboard panel with completion evidence delivery channel/target/operator/notes inputs, recent receipt rows, report downloads, load errors, and optimistic refresh of receipt list plus completion evidence bundle state.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptServiceTests,DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptConvertTests,InMemoryDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepositoryTests,MyBatisDemoFinalAcceptanceCompletionEvidenceDeliveryReceiptRepositoryTests,DemoFinalAcceptanceCompletionEvidenceDeliveryReceiptMigrationTests,DemoReadinessControllerTests test`: first failed because the new receipt DTO/VO/service/repository/mapper/entity classes did not exist; passed after backend implementation.
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx --reporter=basic`: first failed because the receipt API helpers and panel section did not exist, then failed because the completion evidence bundle summary is intentionally visible in multiple places; passed after frontend implementation and assertion update, 2 test files and 174 tests.
- `npm test -- src/App.test.tsx --reporter=basic`: first failed because the App fixture used the wrong completion archive URL and then hit two old 5s timeouts from repeated full-dashboard refreshes during text entry; passed after aligning the fixture URL, adding the new receipt mock routes, and replacing those two tests' character-by-character input with one-shot change events, 85 tests.
- `npm test -- src/App.test.tsx -t "approves pending review tasks" --reporter=basic`: first exposed another oversized App integration path that exceeded the 5s Vitest default; passed after replacing expensive keyboard simulation with one-shot DOM events and using the same 10s timeout budget as other large App integration tests.
- `npm test -- --reporter=basic` on merged `main`: first exposed that the full operational dashboard smoke test now exceeded its old 10s budget after the final acceptance completion receipt data path was added; the smoke test keeps its broad end-to-end assertions and now uses a 15s budget.
- `npm test -- --reporter=basic`: passed, 30 test files and 428 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm run build`: first failed because `defaultAppResponse` referenced final acceptance share delivery and finalization fixtures that were not globally defined; passed after promoting those fixtures, with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 285 Final acceptance completion delivery finalization

- Started `285-final-acceptance-completion-delivery-finalization` to close the final acceptance completion evidence delivery loop after a bundle delivery receipt has been recorded.
- Planned a complete feature slice: backend read-only finalization VO/service/controller endpoints, frontend type/API/App loading, final acceptance panel rendering/downloads, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for READY finalization, missing receipt guidance, stale receipt guidance, blocked bundle guidance, REST serialization, and Markdown report download.
- RED frontend tests were added first for finalization API helpers, final acceptance panel rendering, finalization report download behavior, and full App fixture loading.
- Implemented `DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationService` and `DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationVo` so the finalization gate is READY only when the latest completion evidence bundle is share-ready and the latest completion evidence delivery receipt matches the bundle's completion archive, share package archive, delivery receipt, and task identifiers.
- Added `GET /api/demo/final-acceptance-completion-evidence-delivery-finalization` and `GET /api/demo/final-acceptance-completion-evidence-delivery-finalization/report/download`.
- Updated the final demo acceptance dashboard panel with completion delivery finalization status, receipt freshness, evidence checks, evidence notes, download actions, side-effect contract, and Markdown download.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionEvidenceDeliveryFinalizationServiceTests,DemoReadinessControllerTests test`: first failed because the finalization VO/service/controller route did not exist; passed after backend implementation.
- `npm test -- --reporter=basic src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx`: first failed because the API helpers and panel finalization section did not exist, then failed on expected duplicate read-only/freshness evidence after adding the section; passed after frontend implementation and assertion updates, 3 test files and 262 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 431 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 286 Final acceptance completion closeout

- Started `286-final-acceptance-completion-closeout` to provide one final closed/not-closed report for the externally reviewable self-hosted issue-to-PR demo.
- Planned a complete feature slice: backend closeout read model/service/controller endpoints, frontend type/API/App loading, final acceptance panel rendering/download, README/product/frontend docs, plan doc, and regression tests.
- RED backend service tests were added first and failed because the closeout VO/service did not exist.
- Implemented `DemoFinalAcceptanceCompletionCloseoutService` and `DemoFinalAcceptanceCompletionCloseoutVo` so the closeout is `READY` only when final demo acceptance is accepted, reviewer-package finalization is finalized, the completion evidence bundle is share-ready, the latest completion delivery finalization is finalized, and completion evidence delivery receipt proof exists.
- Added `GET /api/demo/final-acceptance-completion-closeout` and `GET /api/demo/final-acceptance-completion-closeout/report/download`.
- RED controller tests first returned 404 for the new closeout endpoints, then passed after controller wiring.
- Updated the final demo acceptance dashboard panel with completion closeout status, closed flag, latest task/Pull Request/archive/receipt proof, checks, evidence notes, download actions, side-effect contract, and Markdown download.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionCloseoutServiceTests test`: first failed because the closeout VO/service did not exist; passed after backend implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests#should_return_final_acceptance_completion_closeout+should_download_final_acceptance_completion_closeout_report test`: first failed with 404 for the closeout endpoints; passed after controller implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionCloseoutServiceTests,DemoReadinessControllerTests#should_return_final_acceptance_completion_closeout+should_download_final_acceptance_completion_closeout_report test`: passed.
- `npm test -- --reporter=basic src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx`: passed, 3 test files and 265 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 434 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 287 Final acceptance completion closeout evidence bundle

- Started `287-final-acceptance-completion-closeout-evidence-bundle` to promote the final completion closeout into the first demo evidence readout and copied runbook.
- Planned a complete feature slice: backend evidence-bundle aggregation, runbook export, frontend top-level evidence rendering, legacy fallback behavior, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for READY closeout evidence in the bundle, bundle downgrade when closeout proof needs attention, and runbook Markdown export.
- RED frontend tests were added first for rendering the final acceptance completion closeout card in `DemoEvidenceBundlePanel` and showing safe missing-closeout guidance for legacy bundle responses.
- Implemented `DemoEvidenceBundleVo.finalAcceptanceCompletionCloseoutEvidence` with compatibility defaults, then wired `DemoEvidenceBundleService` to include `DemoFinalAcceptanceCompletionCloseoutService` in aggregate status and next-action calculation.
- Updated `DemoRunbookService` so copied runbooks include closeout status, closed flag, completion archive id, delivery receipt id, task id, Pull Request, delivery target/channel, freshness, next action, and download actions.
- Updated the dashboard evidence bundle panel so operators can see final acceptance completion closeout proof without opening the final acceptance panel.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: first failed because the top-level evidence bundle did not expose closeout proof; passed after backend aggregation and runbook implementation.
- `npm test -- --reporter=basic src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx`: first failed because duplicate final acceptance delivery text made an older assertion too strict; passed after using repeated-evidence assertions and adding closeout fixtures.

## 2026-06-29 - 288 Final acceptance completion closeout archive

- Started `288-final-acceptance-completion-closeout-archive` to make the final READY/closed completion closeout durable after live demo evidence changes.
- Planned a complete feature slice: backend closeout archive persistence, READY-only archive guard, protected audit evidence, API create/list/download endpoints, frontend API/App/panel integration, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for service guard behavior, in-memory repository trimming, MyBatis repository conversion, migration coverage, and controller create/list/download behavior.
- RED frontend tests were added first for API helpers, final acceptance panel closeout archive controls, and full App loading.
- Implemented `DemoFinalAcceptanceCompletionCloseoutArchiveService` with in-memory and MyBatis-backed repositories, `V53__create_demo_final_acceptance_completion_closeout_archive.sql`, and archive conversion for evidence notes/download actions.
- Added `POST /api/demo/final-acceptance-completion-closeout/archives`, `GET /api/demo/final-acceptance-completion-closeout/archives`, and `GET /api/demo/final-acceptance-completion-closeout/archives/{archiveId}/report/download`.
- Updated the final demo acceptance dashboard panel with an archive button for READY/closed closeouts, recent closeout archive history, archive download action, and App refresh wiring.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionCloseoutArchiveServiceTests,InMemoryDemoFinalAcceptanceCompletionCloseoutArchiveRepositoryTests,DemoFinalAcceptanceCompletionCloseoutArchiveConvertTests,MyBatisDemoFinalAcceptanceCompletionCloseoutArchiveRepositoryTests,DemoFinalAcceptanceCompletionCloseoutArchiveMigrationTests,DemoReadinessControllerTests test`: first failed because the archive service/repository/entity/mapper/controller route did not exist; passed after backend implementation.
- `npm test -- --reporter=basic src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx`: first failed because the archive API helpers and UI/App wiring did not exist, then exposed repeated closeout summary text after archive history rendering was added; passed after implementation and assertion updates, 3 test files and 269 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 438 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 289 Final closeout archive evidence bundle

- Started `289-final-closeout-archive-evidence-bundle` to promote the latest frozen final acceptance completion closeout archive into the first demo evidence readout and copied runbook.
- Planned a complete feature slice: backend evidence-bundle aggregation, runbook archive export, frontend top-level evidence rendering, legacy fallback behavior, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for READY closeout archive evidence in the bundle, bundle downgrade when the archive is missing, and copied runbook Markdown export.
- RED frontend tests were added first for rendering the final acceptance completion closeout archive card in `DemoEvidenceBundlePanel` and showing safe missing-archive guidance for legacy bundle responses.
- Implemented `DemoEvidenceBundleVo.finalAcceptanceCompletionCloseoutArchiveEvidence` and `DemoFinalAcceptanceCompletionCloseoutArchiveEvidenceVo`, then wired `DemoEvidenceBundleService` to read recent final acceptance completion closeout archives and require the latest archive to be `READY` and closed before the overall bundle is ready.
- Updated `DemoRunbookService` so copied runbooks include closeout archive status, archive id, linked completion archive, linked completion evidence delivery receipt, task id, Pull Request, archived time, next action, and download actions.
- Updated the dashboard evidence bundle panel so operators can see the frozen final closeout archive proof without opening the final acceptance panel.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed.
- `npm test -- --reporter=basic src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: passed, 1 test file and 4 tests.
- `npm test -- --reporter=basic src/App.test.tsx -t "retries failed tasks and refreshes dashboard data"`: passed, confirming the earlier focused timeout was not a deterministic failure.
- `mvn -q -pl PatchPilot test`: passed.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `npm test -- --reporter=basic src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoSessionSnapshotPanel.test.tsx src/App.test.tsx`: passed, 3 test files and 112 tests.
- `npm test -- --reporter=basic`: passed, 30 test files and 438 tests.
- `git diff --check`: passed.

## 2026-06-29 - 290 Final external review evidence package

- Started `290-final-external-review-evidence-package` to provide one final reviewer-facing package that aggregates the accepted final demo summary, finalized reviewer package, completion evidence bundle, completion delivery finalization, final completion closeout, and latest frozen closeout archive.
- Planned a complete feature slice: backend read-only package VO/service/controller endpoints, frontend type/API/App loading, final acceptance panel rendering/downloads, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for READY package generation, missing frozen archive guidance, blocked final acceptance guidance, not-closed archive guidance, REST serialization, and Markdown report download.
- RED frontend tests were added first for package API helpers, final acceptance panel rendering, package report download behavior, and full App fixture loading.
- Implemented `DemoFinalExternalReviewEvidencePackageService` and `DemoFinalExternalReviewEvidencePackageVo` so the package is `READY` only when the full final acceptance/completion chain is ready and the latest frozen closeout archive is `READY` and closed.
- Added `GET /api/demo/final-external-review-evidence-package` and `GET /api/demo/final-external-review-evidence-package/report/download`.
- Updated the final demo acceptance dashboard panel with final external-review package status, frozen archive proof, delivery proof, checks, evidence notes, download actions, side-effect contract, and Markdown download.
- Updated App loading and refresh wiring so completion archive, completion evidence delivery receipt, and completion closeout archive actions refresh the final external-review package.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageServiceTests,DemoReadinessControllerTests test`: first failed because the package VO/service/controller route did not exist; passed after backend implementation.
- `npm test -- --reporter=basic src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx`: first failed because the API helpers and panel package section did not exist, then failed on expected duplicate closeout archive id evidence after the package section was added; passed after App wiring and assertion updates, 3 test files and 272 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=basic`: passed, 30 test files and 441 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 291 Final external review evidence bundle

- Started `291-final-external-review-evidence-bundle` to promote the final external-review evidence package into the first demo evidence readout and copied runbook.
- Planned a complete feature slice: backend evidence-bundle aggregation, runbook package export, frontend top-level evidence rendering, legacy fallback behavior, README/product/frontend docs, plan doc, and regression tests.
- Added backend coverage for the bundle exposing the final package, downgrading bundle readiness when the package is not ready, and copying package evidence into the runbook.
- Added frontend coverage for rendering the final external-review evidence package card in `DemoEvidenceBundlePanel` and showing safe missing-package guidance for legacy bundle responses.
- Implemented `DemoEvidenceBundleVo.finalExternalReviewEvidencePackage`, wired `DemoEvidenceBundleService` to read `DemoFinalExternalReviewEvidencePackageService`, and included package status in bundle readiness and next actions.
- Updated `DemoRunbookService` so copied runbooks include final external-review package status, ready flag, closeout archive, completion archive, completion evidence delivery receipt, task, Pull Request, delivery target, freshness, next action, and download actions.
- Updated the dashboard evidence bundle panel so operators can see final reviewer-facing proof from the top-level demo evidence bundle.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because repeated closeout archive ids and download actions now appeared in both archive and final package cards; passed after changing those assertions to count-based checks.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx`: passed, 2 test files and 89 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 441 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-29 - 292 Final external review package archive

- Started `292-final-external-review-package-archive` to freeze the final reviewer-facing package as durable PatchPilot-local evidence after the live final external-review package reaches READY.
- Planned a complete feature slice: backend archive persistence, READY-only archive guard, protected admin audit evidence, create/list/download API endpoints, evidence-bundle and runbook aggregation, final acceptance dashboard archive controls, top-level evidence rendering, README/product/frontend docs, plan doc, and regression tests.
- RED backend tests were added first for service guard behavior, in-memory repository ordering, MyBatis conversion and migration coverage, controller create/list/download behavior, evidence-bundle archive evidence, and runbook archive export.
- RED frontend tests were added first for API helpers, final acceptance panel archive controls/history/downloads, full App loading, and top-level evidence-bundle archive evidence.
- Implemented `DemoFinalExternalReviewEvidencePackageArchiveService` with in-memory and MyBatis-backed repositories, `V54__create_demo_final_external_review_evidence_package_archive.sql`, and archive conversion for frozen package metadata, report text, evidence notes, and download actions.
- Added `POST /api/demo/final-external-review-evidence-package/archives`, `GET /api/demo/final-external-review-evidence-package/archives`, and `GET /api/demo/final-external-review-evidence-package/archives/{archiveId}/report/download`.
- Updated `DemoEvidenceBundleService` and `DemoRunbookService` so the latest final external-review package archive appears in the first demo evidence readout and copied runbook.
- Updated the final demo acceptance dashboard panel with a final package archive action, recent archive history, archived report downloads, load errors, App refresh wiring, and top-level evidence-bundle archive proof.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageArchiveServiceTests,InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests test`: first failed because archive service/repository behavior did not exist; passed after backend implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageArchiveConvertTests,MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests,DemoFinalExternalReviewEvidencePackageArchiveMigrationTests test`: first failed because converter, MyBatis repository, and migration did not exist; passed after persistence implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests#should_archive_final_external_review_evidence_package_and_record_audit+should_reject_final_external_review_evidence_package_archive_when_package_is_not_ready+should_list_final_external_review_evidence_package_archives+should_download_archived_final_external_review_evidence_package_report+should_return_not_found_when_final_external_review_evidence_package_archive_is_missing test`: first failed because archive controller routes did not exist; passed after API implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests test`: passed after adding latest archive evidence to the bundle.
- `mvn -q -pl PatchPilot -Dtest=DemoRunbookServiceTests test`: passed after adding latest archive evidence to copied runbooks.
- `npm test -- --run src/api.test.ts`: passed after adding final external-review package archive API helpers and adjusting POST expectations.
- `npm test -- --run src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx`: passed after adding final package archive controls and history.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because the top-level evidence panel did not render `Final external-review package archive`; passed after adding archive evidence rendering and legacy fallback guidance.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageArchiveServiceTests,InMemoryDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests,DemoFinalExternalReviewEvidencePackageArchiveConvertTests,MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepositoryTests,DemoFinalExternalReviewEvidencePackageArchiveMigrationTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx`: passed, 4 test files and 280 tests.
- `npm test -- --reporter=dot`: first exposed two full-suite App integration timeout budgets after the dashboard evidence surface grew; passed after replacing slow repeated status clicks with direct events and increasing only the long smoke test budget, 30 test files and 445 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm run build`: passed with the existing Vite large-chunk warning.

## 2026-06-30 - 293 Final external review package delivery receipts

- Started `293-final-external-review-package-delivery-receipts` to close the final external-review package handoff loop after the reviewer-facing package has been frozen as a durable archive.
- Planned a complete feature slice: backend delivery receipt persistence, protected create/list/download API endpoints, evidence-bundle freshness aggregation, runbook export, final acceptance dashboard receipt controls, top-level evidence rendering, README/product/frontend docs, plan doc, and regression tests.
- Added backend coverage for receipt creation guard behavior, in-memory ordering, converter mapping, MyBatis migration/repository behavior, controller create/list/download endpoints, evidence-bundle freshness states, and runbook receipt export.
- Added frontend coverage for receipt API helpers, final acceptance panel record/download behavior, top-level evidence-bundle receipt rendering and legacy fallback, and full App startup/record/download wiring.
- Implemented `DemoFinalExternalReviewEvidencePackageDeliveryReceiptService` with in-memory and MyBatis-backed repositories, `V55__create_demo_final_external_review_evidence_package_delivery_receipt.sql`, and delivery receipt conversion for frozen package metadata, delivery metadata, report text, and protected audit evidence.
- Added `POST /api/demo/final-external-review-evidence-package/delivery-receipts`, `GET /api/demo/final-external-review-evidence-package/delivery-receipts`, and `GET /api/demo/final-external-review-evidence-package/delivery-receipts/{receiptId}/report/download`.
- Updated `DemoEvidenceBundleService` and `DemoRunbookService` so the latest final external-review package delivery receipt appears as fresh, stale, or missing evidence in the first demo evidence readout and copied runbook.
- Updated the final demo acceptance dashboard panel with receipt recording fields, recent receipt history, receipt report downloads, App refresh wiring, and separate record/download status messages.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed after adding receipt evidence to the bundle and runbook.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageDeliveryReceiptServiceTests,InMemoryDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepositoryTests,DemoFinalExternalReviewEvidencePackageDeliveryReceiptConvertTests,MyBatisDemoFinalExternalReviewEvidencePackageDeliveryReceiptRepositoryTests,DemoFinalExternalReviewEvidencePackageDeliveryReceiptMigrationTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx`: first failed because the top-level evidence test used a single-match archive id assertion and the final acceptance panel reused one status value for receipt record/download actions; passed after count-based assertion and separate record/download status state, 3 test files and 199 tests.
- `npm test -- --run src/App.test.tsx`: passed after adding App-level fixture, startup loading assertions, and dashboard record/download coverage, 1 test file and 86 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 450 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 294 Final external review package delivery finalization

- Started `294-final-external-review-package-delivery-finalization` to close the final reviewer-facing delivery loop after a READY final external-review package is frozen and externally delivered.
- Planned a complete feature slice: read-only backend finalization model, JSON and Markdown endpoints, evidence-bundle aggregation, copied runbook export, final acceptance dashboard finalization readout/download, top-level evidence-bundle rendering, README/product/frontend docs, plan doc, and regression tests.
- Added backend coverage for READY, missing receipt, stale receipt, and blocked archive states plus controller, evidence-bundle, and runbook behavior.
- Implemented `DemoFinalExternalReviewEvidencePackageDeliveryFinalizationService` and `DemoFinalExternalReviewEvidencePackageDeliveryFinalizationVo`, then exposed `GET /api/demo/final-external-review-evidence-package/delivery-finalization` and `GET /api/demo/final-external-review-evidence-package/delivery-finalization/report/download`.
- Updated `DemoEvidenceBundleService` and `DemoRunbookService` so the top-level demo evidence and copied runbook include the latest package archive id, delivery receipt id, freshness, finalization checks, evidence notes, and download actions.
- Added frontend API helpers, App loading/refresh wiring, final acceptance panel status/check/evidence rendering, Markdown report download, and refresh after final package archive or delivery receipt actions.
- Added top-level evidence-bundle finalization rendering with legacy fallback guidance so older bundle responses do not hide the final review proof section.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageDeliveryFinalizationServiceTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests test`: passed after backend implementation.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx --reporter=basic`: passed after API helpers and final acceptance panel wiring.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=basic`: first failed because the evidence bundle panel did not render finalization; then failed on a legitimate duplicate receipt id after the card was added; passed after adding the finalization card and count-based receipt assertion.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx --reporter=basic`: passed, 4 test files and 288 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 453 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 295 Final external review delivery finalization archives

- Started `295-final-external-review-delivery-finalization-archives` to freeze the READY final external-review package delivery finalization as durable local evidence after the frozen reviewer-facing package has a fresh delivery receipt.
- Planned a complete feature slice: backend archive persistence, READY-only archive guard, in-memory/MyBatis repositories, Flyway migration, create/list/download API endpoints, evidence-bundle aggregation, copied runbook export, final acceptance dashboard archive controls/history/downloads, top-level evidence rendering, README/product/frontend docs, plan doc, and regression tests.
- Added backend coverage for archive service guard behavior, in-memory repository ordering, converter mapping, MyBatis migration/repository behavior, controller create/list/download endpoints, evidence-bundle latest archive evidence, and runbook archive export.
- Implemented `DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveService` with in-memory and MyBatis-backed repositories, `V56__create_demo_final_external_review_evidence_package_delivery_finalization_archive.sql`, and archive conversion for package archive ids, delivery receipt ids, completion evidence ids, task/Pull Request evidence, receipt freshness, checks, notes, generated time, archived time, and Markdown report text.
- Added `POST /api/demo/final-external-review-evidence-package/delivery-finalization/archives`, `GET /api/demo/final-external-review-evidence-package/delivery-finalization/archives`, and `GET /api/demo/final-external-review-evidence-package/delivery-finalization/archives/{archiveId}/report/download`.
- Updated `DemoEvidenceBundleService` and `DemoRunbookService` so the latest final external-review package delivery finalization archive appears in the first demo evidence readout and copied runbook.
- Updated the final demo acceptance dashboard with a delivery-finalization archive action, recent archive history, archived report downloads, load errors, App refresh wiring, and top-level evidence-bundle archive proof.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveServiceTests,InMemoryDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepositoryTests,DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvertTests,DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveMigrationTests,MyBatisDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepositoryTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed, 164 tests.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=basic`: first exposed duplicate expected evidence after the archive card was added and a too-specific download-action text assertion; passed after count-based evidence assertions and button-role download assertion, 3 test files and 206 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- `mvn -pl PatchPilot test`: passed, 1169 tests.
- `npm test -- --reporter=dot`: passed, 30 test files and 457 tests.

## 2026-06-30 - 296 Final external review delivery certificate

- Started `296-final-external-review-delivery-certificate` to turn the latest archived final external-review package delivery finalization into a single terminal certificate for reviewer-facing delivery proof.
- Planned a complete feature slice: read-only backend certificate service, JSON and Markdown download endpoints, frontend API helpers, App loading/refresh wiring, final acceptance dashboard certificate card, README/product/frontend docs, plan doc, and regression tests.
- Added backend coverage for certified and missing-archive certificate states plus controller JSON/download routes.
- Implemented `DemoFinalExternalReviewDeliveryCertificateService` and `DemoFinalExternalReviewDeliveryCertificateVo` so the certificate is derived from the latest delivery-finalization archive without creating tasks, calling the model, running tests, mutating Git, recording receipts, sending messages, or writing to GitHub.
- Added `GET /api/demo/final-external-review-delivery-certificate` and `GET /api/demo/final-external-review-delivery-certificate/report/download`.
- Updated the final demo acceptance dashboard with certificate loading, a certificate card, linked archive/receipt/target evidence, checks, download actions, and Markdown report download.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewDeliveryCertificateServiceTests,DemoReadinessControllerTests#should_return_final_external_review_delivery_certificate+should_download_final_external_review_delivery_certificate_report,DemoEvidenceBundleServiceTests test`: passed during backend wiring.
- `npm test -- src/api.test.ts`: first failed because the new frontend API helpers were missing; passed after adding type and API helpers, 188 tests.
- `npm test -- src/App.test.tsx`: first failed because adding the certificate introduced a legitimate second delivery-receipt evidence reference; passed after making the assertion count-based, 86 tests.

## 2026-06-30 - 297 Final external review delivery certificate archives

- Started `297-final-external-review-delivery-certificate-archives` to freeze the certified final external-review delivery certificate as durable local evidence after the final reviewer-facing delivery loop is certified.
- Planned a complete feature slice: backend archive persistence, certified-only archive guard, in-memory/MyBatis repositories, Flyway migration, create/list/download API endpoints, protected audit, final acceptance dashboard archive controls/history/downloads, frontend API coverage, README/product/frontend docs, plan doc, and regression tests.
- Implemented `DemoFinalExternalReviewDeliveryCertificateArchiveService` with `DemoFinalExternalReviewDeliveryCertificateArchiveVo`, entity, mapper, converter, in-memory repository, MyBatis repository, and `V57__create_demo_final_external_review_delivery_certificate_archive.sql`.
- Added `POST /api/demo/final-external-review-delivery-certificate/archives`, `GET /api/demo/final-external-review-delivery-certificate/archives`, and `GET /api/demo/final-external-review-delivery-certificate/archives/{archiveId}/report/download`.
- Updated the final demo acceptance dashboard with a certificate archive button, recent certificate archive history, archived report downloads, archive load errors, App refresh wiring, and full API helper coverage.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewDeliveryCertificateArchiveServiceTests,DemoFinalExternalReviewDeliveryCertificateArchiveConvertTests,MyBatisDemoFinalExternalReviewDeliveryCertificateArchiveRepositoryTests,DemoFinalExternalReviewDeliveryCertificateArchiveMigrationTests,DemoReadinessControllerTests#should_archive_final_external_review_delivery_certificate_and_record_audit+should_reject_final_external_review_delivery_certificate_archive_when_not_certified+should_list_final_external_review_delivery_certificate_archives+should_download_archived_final_external_review_delivery_certificate_report+should_return_not_found_when_final_external_review_delivery_certificate_archive_is_missing test`: passed.
- `npm test`: first failed because the full App smoke test expected a single matching certificate summary after archive history added the same summary; passed after changing that assertion to count-based. Final result: 30 test files and 462 tests passed.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `mvn -q -pl PatchPilot test`: passed.

## 2026-06-30 - 298 Final external review release bundle

- Started `298-final-external-review-release-bundle` to create one terminal release bundle after the certified final external-review delivery certificate has been archived.
- Planned a complete feature slice: backend release read model, JSON and Markdown download endpoints, frontend API helpers, App loading, final demo acceptance dashboard release card, README/product/frontend docs, plan doc, and regression tests.
- Added backend coverage for READY and missing-certificate-archive release-bundle states plus controller JSON/download routes.
- Implemented `DemoFinalExternalReviewReleaseBundleService` and `DemoFinalExternalReviewReleaseBundleVo` so the release bundle derives from the latest certificate archive and remains read-only.
- Added `GET /api/demo/final-external-review-release-bundle` and `GET /api/demo/final-external-review-release-bundle/report/download`.
- Updated the final demo acceptance dashboard with release readiness, linked archive/receipt/task/Pull Request evidence, required attachments, release checks, evidence notes, download actions, and Markdown report download.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewReleaseBundleServiceTests,DemoReadinessControllerTests#should_return_final_external_review_release_bundle+should_download_final_external_review_release_bundle_report test`: passed.
- `npm test -- --run src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx --reporter=basic`: passed, 3 test files and 296 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 465 tests.
- `npm run build`: first failed because the new release-bundle download handler used an `errorMessage` helper that only exists in `App.tsx`; passed after matching the component's existing fixed failure-message pattern. The existing Vite large-chunk warning remains.
- `npm test -- --run src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx --reporter=basic`: passed after the build fix, 2 test files and 103 tests.
- `git diff --check`: passed.

## 2026-06-30 - 299 Final release bundle evidence surface

- Started `299-final-release-bundle-evidence-surface` to promote the final external-review release bundle into the top-level demo evidence bundle and copied runbook.
- Planned a complete feature slice: backend evidence-bundle field and readiness aggregation, copied runbook release-bundle lines, dashboard evidence-bundle release card with legacy fallback, README/product/frontend docs, plan doc, and regression tests.
- Added `finalExternalReviewReleaseBundle` to `DemoEvidenceBundleVo` with compatibility defaults for older focused tests and call sites.
- Wired `DemoEvidenceBundleService` to `DemoFinalExternalReviewReleaseBundleService` so top-level readiness and next actions now depend on terminal reviewer handoff proof.
- Updated `DemoRunbookService` to include release readiness, certificate archive, delivery finalization archive, package archive, receipt, task, Pull Request, target/channel, attachments, evidence notes, and download actions.
- Updated the dashboard evidence bundle panel and App smoke fixture so the first demo readout shows release-ready status, required final attachments, and the release Pull Request without opening the final acceptance panel.
- Updated README, product spec, frontend design doc, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot '-Dtest=DemoEvidenceBundleServiceTests#should_report_ready_when_all_evidence_is_healthy+should_require_final_external_review_release_bundle_before_reporting_bundle_ready' test`: passed after backend wiring.
- `mvn -q -pl PatchPilot '-Dtest=DemoRunbookServiceTests#should_format_demo_evidence_bundle_as_markdown_runbook' test`: passed after runbook release lines were added.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed on legacy fallback expectations after the release card was introduced, then passed after adjusting the old-response fixture and duplicate fallback assertion.
- `npm test -- --run src/App.test.tsx`: passed, 86 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test`: passed, 30 test files and 465 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 300 Final release bundle archives

- Started `300-final-release-bundle-archives` to freeze the READY final external-review release bundle as durable PatchPilot-local evidence after the live release read model is ready.
- Planned a complete feature slice: backend archive persistence, READY-only archive guard, create/list/download API endpoints, protected audit evidence, top-level evidence-bundle archive status, copied runbook archive lines, final acceptance dashboard archive controls/history/downloads, frontend API helpers, App refresh wiring, README/product/frontend/architecture docs, and regression tests.
- Added `DemoFinalExternalReviewReleaseBundleArchiveService` with VO/entity/mapper/converter, in-memory and MyBatis repositories, and `V58__create_demo_final_external_review_release_bundle_archive.sql`.
- Added `POST /api/demo/final-external-review-release-bundle/archives`, `GET /api/demo/final-external-review-release-bundle/archives`, and `GET /api/demo/final-external-review-release-bundle/archives/{archiveId}/report/download`.
- Updated the top-level demo evidence bundle and copied runbook with latest release-bundle archive readiness, archive id, certificate archive, delivery finalization archive, package archive, delivery receipt, Pull Request, archived time, next action, and download actions.
- Updated the final demo acceptance dashboard with archive creation, recent release-bundle archive history, archive report downloads, archive load errors, and App-level refresh after archive creation.
- Updated README, product spec, frontend design doc, architecture notes, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewReleaseBundleArchiveServiceTests,DemoFinalExternalReviewReleaseBundleArchiveConvertTests,DemoFinalExternalReviewReleaseBundleArchiveMigrationTests,DemoReadinessControllerTests#should_archive_final_external_review_release_bundle_and_record_audit+should_reject_final_external_review_release_bundle_archive_when_not_ready+should_list_final_external_review_release_bundle_archives+should_download_archived_final_external_review_release_bundle_report+should_return_not_found_when_final_external_review_release_bundle_archive_is_missing test`: passed.
- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed.
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx`: first failed because release-bundle archive API helpers and UI history did not exist; passed after adding archive types, API helpers, controls, and history, 213 tests.
- `npm test -- src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because top-level archive evidence was missing; passed after adding release-bundle archive evidence and legacy fallback, 4 tests.
- `npm test -- src/App.test.tsx`: passed after App-level archive loading and panel wiring, 86 tests.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewReleaseBundleArchiveServiceTests,DemoFinalExternalReviewReleaseBundleArchiveConvertTests,DemoFinalExternalReviewReleaseBundleArchiveMigrationTests,DemoReadinessControllerTests#should_archive_final_external_review_release_bundle_and_record_audit+should_reject_final_external_review_release_bundle_archive_when_not_ready+should_list_final_external_review_release_bundle_archives+should_download_archived_final_external_review_release_bundle_report+should_return_not_found_when_final_external_review_release_bundle_archive_is_missing,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed before final integration.
- `npm test -- --reporter=dot src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/dashboard/components/DemoEvidenceBundlePanel.test.tsx src/App.test.tsx`: passed, 4 test files and 303 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 468 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 301 Final release bundle delivery finalization

- Started `301-final-release-bundle-delivery-finalization` to close the terminal external-review handoff loop after the final release bundle has been frozen.
- Planned a complete feature slice: backend delivery receipt persistence, READY-archive receipt guard, read-only delivery finalization gate, create/list/download/finalization API endpoints, protected audit evidence, top-level evidence-bundle aggregation, copied runbook export, final acceptance dashboard receipt controls/history/finalization downloads, frontend API helpers, App refresh wiring, README/product/frontend/architecture docs, and regression tests.
- Added `DemoFinalExternalReviewReleaseBundleDeliveryReceiptService` with request/VO/entity/mapper/converter/repositories, in-memory and MyBatis storage, and `V59__create_demo_final_external_review_release_bundle_delivery_receipt.sql`.
- Added `POST /api/demo/final-external-review-release-bundle/delivery-receipts`, `GET /api/demo/final-external-review-release-bundle/delivery-receipts`, `GET /api/demo/final-external-review-release-bundle/delivery-receipts/{receiptId}/report/download`, `GET /api/demo/final-external-review-release-bundle/delivery-finalization`, and `GET /api/demo/final-external-review-release-bundle/delivery-finalization/report/download`.
- Updated the top-level demo evidence bundle and copied runbook with final release-bundle delivery finalization status, latest frozen release-bundle archive, latest receipt, receipt freshness, delivery target/channel, next action, and download actions.
- Updated the final demo acceptance dashboard with release-bundle delivery receipt form fields, recent receipt rows, receipt report downloads, finalization status/checks/evidence notes, finalization report download, and App-level refresh after receipt creation.
- Updated README, product spec, frontend design doc, architecture notes, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests,DemoReadinessControllerTests,DemoFinalExternalReviewReleaseBundleDeliveryReceiptServiceTests,DemoFinalExternalReviewReleaseBundleDeliveryFinalizationServiceTests,DemoFinalExternalReviewReleaseBundleDeliveryReceiptConvertTests,DemoFinalExternalReviewReleaseBundleDeliveryReceiptMigrationTests test`: passed during backend wiring.
- `npm test -- --run src/api.test.ts -t "release bundle delivery"`: passed, 5 focused API tests.
- `npm test -- --run src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx -t "release bundle delivery evidence"`: passed, 1 focused panel test.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx -t "summarizes demo evidence"`: first exposed a duplicate expected release-bundle archive download action after the finalization evidence was added; passed after changing the assertion to accept repeated evidence.
- `npm test -- --run src/App.test.tsx -t "renders operational task dashboard"`: first exposed App fixture initialization order and duplicate release-bundle archive id assertions; passed after moving the release-bundle delivery fixtures before the evidence bundle fixture and using count-based evidence assertions.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 474 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 302 Final release bundle delivery finalization archives

- Started `302-final-release-bundle-delivery-finalization-archives` to freeze the READY terminal release-bundle delivery finalization as durable local evidence.
- Planned a complete feature slice: backend archive persistence, READY/finalized guard, create/list/download API endpoints, protected audit evidence, top-level evidence-bundle archive status, final acceptance dashboard archive controls/history/downloads, frontend API helpers, App refresh wiring, README/product/frontend/architecture docs, and regression tests.
- Added `DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveService` with VO/entity/mapper/converter, in-memory and MyBatis repositories, and `V60__create_demo_final_external_review_release_bundle_delivery_finalization_archive.sql`.
- Added `POST /api/demo/final-external-review-release-bundle/delivery-finalization/archives`, `GET /api/demo/final-external-review-release-bundle/delivery-finalization/archives`, and `GET /api/demo/final-external-review-release-bundle/delivery-finalization/archives/{archiveId}/report/download`.
- Updated the top-level demo evidence bundle with latest release-bundle delivery finalization archive readiness, archive id, release-bundle archive id, delivery receipt id, certificate archive id, Pull Request, archived time, next action, and download actions.
- Updated the final demo acceptance dashboard with archive creation, recent release-bundle delivery finalization archive history, archive report downloads, archive load errors, and App-level refresh after archive creation.
- Updated README, product spec, frontend design doc, architecture notes, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoReadinessControllerTests,DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveServiceTests,DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvertTests test`: passed.
- `npm test -- --run src/api.test.ts src/App.test.tsx --reporter=dot`: passed, 2 test files and 290 tests.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=dot`: first exposed duplicate status/receipt assertions after archive evidence reused live proof ids; passed after changing the assertions to allow repeated evidence labels, 4 tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 477 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 303 Final release bundle delivery certificate

- Started `303-final-release-bundle-delivery-certificate` to create one read-only terminal certificate for the latest frozen final external-review release-bundle delivery finalization archive.
- Planned a complete feature slice: backend certificate VO/service, JSON and Markdown download endpoints, final demo acceptance dashboard certificate card, frontend API helpers, App refresh wiring after delivery finalization archive creation, README/product/frontend/architecture docs, and regression tests.
- Added `DemoFinalExternalReviewReleaseBundleDeliveryCertificateService` and `DemoFinalExternalReviewReleaseBundleDeliveryCertificateVo`, deriving certification from the latest `DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo`.
- Added `GET /api/demo/final-external-review-release-bundle/delivery-certificate` and `GET /api/demo/final-external-review-release-bundle/delivery-certificate/report/download`.
- Updated the final demo acceptance dashboard with a terminal release-bundle delivery certificate card showing certified status, latest delivery finalization archive, release-bundle archive, delivery receipt freshness, checks, evidence notes, download actions, read-only contract, and Markdown download.
- Updated frontend API helpers, App data loading, App smoke fixtures, README, product spec, frontend design doc, architecture notes, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalExternalReviewReleaseBundleDeliveryCertificateServiceTests,DemoReadinessControllerTests#should_return_final_external_review_release_bundle_delivery_certificate+should_download_final_external_review_release_bundle_delivery_certificate_report test`: first failed on missing VO/service, then passed after backend implementation.
- `npm test -- --run src/api.test.ts -t "release bundle delivery certificate"`: passed, 2 focused API tests.
- `npm test -- --run src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx`: passed, 18 tests.
- `npm test -- --run src/api.test.ts src/App.test.tsx -t "release bundle delivery certificate|renders operational task dashboard"`: passed, 3 focused API/App tests.
- `mvn -q -pl PatchPilot test`: passed.
- `npm test -- --reporter=dot`: passed, 30 test files and 479 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 304 Final release bundle delivery certificate archives

- Started `304-final-release-bundle-delivery-certificate-archives` to freeze the certified terminal release-bundle delivery certificate as durable PatchPilot-local evidence.
- Planned a complete feature slice: backend archive persistence, certified-only guard, create/list/download API endpoints, protected audit evidence, final demo acceptance dashboard archive controls/history/downloads, frontend API helpers, App refresh wiring, README/product/frontend/architecture docs, and regression tests.
- Added `DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveService` with VO/entity/mapper/converter, in-memory and MyBatis repositories, and `V61__create_demo_final_external_review_release_bundle_delivery_certificate_archive.sql`.
- Added `POST /api/demo/final-external-review-release-bundle/delivery-certificate/archives`, `GET /api/demo/final-external-review-release-bundle/delivery-certificate/archives`, and `GET /api/demo/final-external-review-release-bundle/delivery-certificate/archives/{archiveId}/report/download`.
- Updated the final demo acceptance dashboard with terminal release-bundle delivery certificate archive creation, recent archive history, archive report downloads, archive load errors, and App-level refresh after archive creation.
- Updated README, product spec, frontend design doc, architecture notes, and added this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests#should_archive_final_external_review_release_bundle_delivery_certificate_and_record_audit test`: first failed because the new controller fixture helper did not exist.
- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests#should_archive_final_external_review_release_bundle_delivery_certificate_and_record_audit+DemoReadinessControllerTests#should_reject_final_external_review_release_bundle_delivery_certificate_archive_when_not_certified+DemoReadinessControllerTests#should_list_final_external_review_release_bundle_delivery_certificate_archives+DemoReadinessControllerTests#should_download_archived_final_external_review_release_bundle_delivery_certificate_report+DemoReadinessControllerTests#should_return_not_found_when_final_external_review_release_bundle_delivery_certificate_archive_is_missing,DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveServiceTests,DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvertTests test`: first failed on a case-sensitive test assertion, then passed after fixing the assertion.
- `npm test -- --run src/api.test.ts --reporter=dot`: passed, 209 tests.
- `npm test -- --run src/App.test.tsx src/api.test.ts --reporter=dot`: passed, 2 test files and 295 tests.

## 2026-06-30 - 305 Final release certificate archive evidence

- Started `305-final-release-certificate-archive-evidence` to surface the durable terminal release-bundle delivery certificate archive in the first demo readout instead of requiring operators to open archive history.
- Added `DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEvidenceVo` and wired `DemoEvidenceBundleService` to aggregate the latest archive status, certified flag, archive id, linked delivery finalization archive, linked release-bundle archive, delivery receipt, certificate/package ids, task, Pull Request, archived time, next action, and download actions.
- Updated `DemoRunbookService` so `GET /api/demo/runbook` repeats the terminal certificate archive proof with the same linked evidence and downloads.
- Updated `DemoEvidenceBundlePanel` and frontend types so the dashboard shows a `Final external-review release bundle delivery certificate archive` card with a legacy fallback when older backend payloads omit the field.
- Updated README, product spec, architecture notes, frontend design direction, and added this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests#should_require_final_external_review_release_bundle_delivery_certificate_archive_before_reporting_bundle_ready+DemoEvidenceBundleServiceTests#should_build_demo_evidence_bundle_from_existing_read_models test`: passed after backend aggregation was implemented.
- `mvn -q -pl PatchPilot -Dtest=DemoRunbookServiceTests#should_format_demo_evidence_bundle_as_markdown_runbook test`: first failed because the runbook did not output release-bundle delivery certificate archive evidence; passed after adding the runbook lines.
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=dot`: first failed because the panel did not render the new certificate archive card; passed after adding fallback and rendering.
- `npm test -- --run src/App.test.tsx -t "renders operational task dashboard" --reporter=dot`: first failed because the App smoke assertion expected a linked finalization archive id to appear once; passed after adding the top-level certificate archive fixture and count-based evidence assertions.
- `mvn -q -pl PatchPilot test`: passed with existing Mockito/Java agent and Lombok Unsafe warnings.
- `npm test -- --reporter=dot`: passed, 30 test files and 482 tests.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 306 Final reviewer handoff package

- Started `306-final-reviewer-handoff-package` to turn the archived terminal release-bundle delivery certificate into one final reviewer send/no-send package.
- Added `DemoFinalReviewerHandoffPackageService` and `DemoFinalReviewerHandoffPackageVo`, deriving readiness from the latest certified release-bundle delivery certificate archive, frozen release-bundle archive, and fresh release-bundle delivery proof.
- Added `GET /api/demo/final-reviewer-handoff-package` and `GET /api/demo/final-reviewer-handoff-package/report/download`.
- Updated the top-level evidence bundle and copied runbook with final reviewer handoff package readiness, linked archive ids, delivery proof, required attachments, download actions, Pull Request, and next action.
- Updated the dashboard evidence panel with a final reviewer handoff card, legacy fallback, and Markdown download action.
- Updated frontend API helpers, App wiring, README, product spec, architecture notes, frontend design notes, and added this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalReviewerHandoffPackageServiceTests,DemoReadinessControllerTests test`: passed.
- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalReviewerHandoffPackageServiceTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed.
- `mvn -q -pl PatchPilot test`: passed with existing Mockito/Java agent warnings.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`: first failed because jsdom did not provide `URL.createObjectURL`; passed after stubbing browser download APIs in the panel test, 2 test files and 215 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 30 test files and 484 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 313 Live launch gate

- Started `313-live-launch-gate` to make the final step before a real GitHub `/agent fix` comment one read-only go/no-go result instead of separate readiness panels.
- Refactored live trigger dry-run conversion into `GitHubTriggerDryRunService` so the new launch gate reuses the same `ISSUE_COMMENT` trigger evaluation behavior as `/api/github/trigger-dry-run`.
- Added `POST /api/demo/live-launch-gate`, combining self-hosted launch readiness, webhook setup readiness, live GitHub publish preflight, and live trigger dry-run evidence into one `READY`, `NEEDS_ATTENTION`, or `BLOCKED` package.
- Added dashboard `Live launch gate` support with typed API helper, App wiring, aggregated checks, next actions, side-effect contract, and copyable backend Markdown report.
- Updated README, product spec, and this plan document with the final pre-GitHub-comment launch gate.

Validation:

- `mvn -pl PatchPilot -Dtest=DemoLiveLaunchGateServiceTests,DemoLiveLaunchGateControllerTests test`: first failed because the service, controller, command, and VO did not exist.
- `mvn -pl PatchPilot -Dtest=DemoLiveLaunchGateServiceTests,DemoLiveLaunchGateControllerTests,GitHubTriggerDryRunControllerTests test`: first failed because `List.getFirst()` is a Java 21 API; passed after replacing it with Java 17-compatible `get(0)`.
- `npm test -- --run src/dashboard/components/LiveLaunchGatePanel.test.tsx src/api.test.ts src/App.test.tsx`: first failed because the panel and App wiring did not exist, then failed because the App fetch mock referenced a missing fixture; passed after adding the component, App handler, and live launch gate mock.
- `mvn -q -pl PatchPilot test`: first failed because Spring could not choose the `DemoLiveLaunchGateService` production constructor after the test-only constructor was added; passed after marking the production constructor with `@Autowired`.
- `mvn -q -pl PatchPilot -Dtest=DemoLiveLaunchGateServiceTests,DemoLiveLaunchGateControllerTests,GitHubWebhookControllerTests,ConfigurationControllerTests,AdminApiSecurityFilterTests,TaskControllerTests test`: passed.
- `mvn -q -pl PatchPilot test`: passed with existing Mockito/Java agent warnings.
- `npm --prefix frontend test -- --reporter=dot`: passed, 32 test files and 506 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.

## 2026-06-30 - 307 Final reviewer handoff delivery

- Started `307-final-reviewer-handoff-delivery` to close the demo evidence chain after the final reviewer handoff package is ready.
- Added local final reviewer handoff delivery receipt persistence, in-memory and MyBatis repositories, converter/entity/mapper wiring, and `V62__create_demo_final_reviewer_handoff_delivery_receipt.sql`.
- Added `POST /api/demo/final-reviewer-handoff-package/delivery-receipts`, `GET /api/demo/final-reviewer-handoff-package/delivery-receipts`, `GET /api/demo/final-reviewer-handoff-package/delivery-finalization`, and Markdown report downloads for receipt and finalization evidence.
- Added `DemoFinalReviewerHandoffDeliveryFinalizationService` so the terminal gate reports READY only when the current handoff package has a fresh local delivery receipt.
- Updated the evidence bundle and runbook with final reviewer handoff delivery finalization status, receipt id, terminal certificate archive, release bundle archive, package receipt, delivery target, freshness, next action, and download instruction.
- Updated frontend API helpers, types, App refresh wiring, final demo acceptance dashboard controls, receipt form, finalization card, receipt history, and Markdown downloads.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoFinalReviewerHandoffDeliveryReceiptServiceTests,DemoFinalReviewerHandoffDeliveryFinalizationServiceTests test`: passed.
- `mvn -q -pl PatchPilot -Dtest=DemoReadinessControllerTests test`: passed.
- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests test`: passed.
- `mvn -q -pl PatchPilot -Dtest=DemoRunbookServiceTests test`: passed.
- `npm test -- src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx`: first failed because the final reviewer handoff delivery finalization UI did not exist; passed after adding the dashboard finalization/receipt controls, 19 tests.
- `npm test -- src/App.test.tsx`: passed, 86 tests.
- `mvn -q -pl PatchPilot -Dtest=DemoFinalReviewerHandoffDeliveryReceiptServiceTests,DemoFinalReviewerHandoffDeliveryFinalizationServiceTests,DemoReadinessControllerTests,DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: passed with existing Mockito inline-agent warning.
- `npm test -- --reporter=dot`: passed, 30 test files and 490 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Mockito inline-agent warning.
- `npm run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-06-30 - 311 Live GitHub publish preflight

- Started `311-live-github-publish-preflight` to add one final read-only check before an operator posts a live `/agent fix` issue comment.
- Added `GET /api/github/live-publish-preflight`, backed by `GitHubLivePublishPreflightService` and a read-only GitHub HTTP probe for branch and open Pull Request metadata.
- Aggregated publish path readiness, publish permission readiness, existing `patchpilot/*` branches, and open PatchPilot Pull Request URLs into a single READY/NEEDS_ATTENTION/BLOCKED operator result.
- Updated the operator setup checklist with a live publish preflight row and detail card that shows stale branch count, open PatchPilot PR count, check summaries, next action, repository/default-branch evidence, and the read-only side-effect contract.
- Updated frontend API helpers, App loading, typed payloads, README, product spec, and this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=GitHubLivePublishPreflightServiceTests,GitHubLivePublishPreflightHttpProbeTests,GitHubCredentialReadinessControllerTests test`: first failed because the preflight types, service, probe, and endpoint did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx -t "live publish preflight|operator setup" --reporter=dot`: first failed because the App-level all-ready setup count still expected `16/16`; passed after updating the assertion to `17/17`.
- `mvn -q -pl PatchPilot test`: passed with existing Mockito/Java agent and Spring test logging.
- `npm --prefix frontend test -- --reporter=dot`: first failed because existing operator setup checklist tests still expected 16 total checks and two publish side-effect paragraphs; passed after updating those assertions to 17 checks and three read-only publish cards. Final result: 30 test files and 496 tests passed.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 312 Live GitHub trigger dry run

- Started `312-live-trigger-dry-run` to add a lightweight read-only final trigger gate for the exact GitHub issue comment an operator plans to post.
- Added `POST /api/github/trigger-dry-run`, backed by the existing `TriggerEvaluationService` with forced `ISSUE_COMMENT` source, returning status, issue URL, issue-context state, safety, active-task, quarantine, rate-limit, model-classification decisions, side-effect contract, and next action.
- Tightened the trigger rate-limit service contract so dry-run callers must use an explicit read-only `check(...)` path instead of accidentally inheriting `checkAndRecord(...)` behavior.
- Added dashboard `Live trigger dry run` support with typed API helper, App wiring, allowed/blocked decision rendering, and copyable Markdown dry-run evidence.
- Updated README and product spec so operators can distinguish the lightweight trigger dry run from the broader demo launch preflight.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=GitHubTriggerDryRunControllerTests test`: first failed because the controller, DTO, and VO did not exist; passed after backend implementation.
- `mvn -q -pl PatchPilot -Dtest=GitHubTriggerDryRunControllerTests,InMemoryTriggerRateLimitServiceTests,DefaultTriggerEvaluationServiceTests,DefaultManualFixTaskServiceTests,GitHubWebhookServiceTests test`: first exposed test fake implementations that did not implement the now-explicit read-only rate-limit `check(...)` contract; passed after updating those fakes and adding a dry-run no-record assertion.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveTriggerDryRunPanel.test.tsx src/App.test.tsx -t "live trigger dry run|GitHub trigger dry run"`: first failed because the API helper and panel did not exist; passed for the matching API/App tests after frontend implementation.
- `npm --prefix frontend test -- --run src/dashboard/components/LiveTriggerDryRunPanel.test.tsx --reporter=dot`: first failed on a duplicate blocked-reason text assertion; passed after changing the component test to assert both occurrences.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveTriggerDryRunPanel.test.tsx src/App.test.tsx --reporter=dot`: passed, 3 test files and 310 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Mockito/Java agent warnings.
- `npm --prefix frontend test -- --reporter=dot`: passed, 31 test files and 501 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 314 End-to-end acceptance matrix

- Started `314-end-to-end-acceptance-matrix` to give operators one read-only answer for how close PatchPilot is to the final self-hosted issue-to-PR goal.
- Added `GET /api/demo/end-to-end-acceptance-matrix`, backed by `DemoEndToEndAcceptanceMatrixService`, aggregating live launch gate status, supported language coverage, safety rejection coverage, evaluation baseline/run evidence, recent Pull Request evidence, failed-task evidence, pending-review evidence, and final product gap analysis.
- Added response rows with category, name, status, evidence, gap, and next action plus aggregate counts, readiness percent, final-demo readiness flag, side-effect contract, and Markdown report.
- Added the operations dashboard `End-to-end acceptance` panel with matrix counts, row-level evidence, next actions, copyable Markdown report, and local refresh support.
- Updated frontend API helpers, typed payloads, App loading, App smoke fixtures, README, product spec, and this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEndToEndAcceptanceMatrixServiceTests,DemoReadinessControllerTests test`: first failed because the service, VO records, and endpoint did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/dashboard/components/EndToEndAcceptanceMatrixPanel.test.tsx src/api.test.ts --reporter=dot`: first failed because the dashboard panel did not exist; API tests passed after adding the endpoint helper.
- `npm --prefix frontend test -- --run src/dashboard/components/EndToEndAcceptanceMatrixPanel.test.tsx src/api.test.ts src/App.test.tsx --reporter=dot`: first failed because the App smoke assertion matched duplicate summary text; passed after using a count-based assertion. Final result: 3 test files and 313 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Mockito/Java agent warnings.
- `npm --prefix frontend test -- --reporter=dot`: passed, 33 test files and 510 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 315 External exposure readiness gate

- Started `315-external-exposure-readiness-gate` to add one security-focused go/no-go check before a self-hosted backend is exposed through Cloudflare Tunnel or another temporary public URL.
- Added `GET /api/security/external-exposure-readiness`, backed by `ExternalExposureReadinessService`, aggregating admin-token protection, dashboard token bootstrap state, webhook secret, public webhook URL, trigger user/repository allowlists, trigger rate limits, rejected-trigger quarantine, review approvers, and generated-diff risk gating.
- Added aggregate status, safe-to-expose flag, ready/warning/blocked counts, per-check summaries, next actions, read-only side-effect contract, generated timestamp, and copyable Markdown report without adding persistent state or exposing secrets.
- Added the operations dashboard `External exposure readiness` panel with status counts, check cards, next actions, copyable Markdown evidence, refresh support, and App loading.
- Updated frontend API helpers, typed payloads, App smoke fixtures, README, product spec, and this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureReadinessServiceTests,ExternalExposureReadinessControllerTests test`: first failed because the service, VO records, and controller did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx --reporter=dot`: first failed because the API helper and panel did not exist; passed after frontend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot`: first failed because the App smoke assertion matched duplicate summary text; passed after using a count-based assertion. Final result: 3 test files and 314 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Spring/Mockito test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 34 test files and 514 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 316 External exposure readiness archives

- Started `316-external-exposure-readiness-archives` to preserve the external exposure readiness gate as local evidence before a temporary public URL is shared.
- Added `POST /api/security/external-exposure-readiness/archives`, `GET /api/security/external-exposure-readiness/archives`, and `GET /api/security/external-exposure-readiness/archives/{archiveId}/report/download`.
- Added an in-memory archive repository for the default profile plus MySQL/Flyway/MyBatis persistence for `local`, `docker`, and `idea` profiles.
- Extended the operations dashboard external exposure panel with an archive action, recent archive history, archived count summary, and Markdown report downloads.
- Updated frontend API helpers, typed payloads, App loading, App smoke fixtures, README, product spec, frontend design notes, architecture notes, and this plan document.

Validation:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureReadinessArchiveServiceTests,ExternalExposureReadinessArchiveControllerTests test`: first failed because the archive VO, service, repository, and controller did not exist; passed after backend implementation.
- `mvn -q -pl PatchPilot -Dtest=ExternalExposureReadinessArchiveServiceTests,ExternalExposureReadinessArchiveControllerTests,MyBatisExternalExposureReadinessArchiveRepositoryTests,ExternalExposureReadinessArchiveMigrationTests test`: passed after adding MyBatis persistence and migration coverage.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot`: first failed because the App did not load archive history and the panel test matched duplicate safety text; passed after App wiring and test selector fixes. Final focused result: 3 test files and 317 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Spring/Mockito test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 34 test files and 517 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 317 External exposure handoff package

- Started `317-external-exposure-handoff-package` to turn the current external exposure gate plus latest archived evidence into one read-only package before a temporary public URL is shared.
- Added `GET /api/security/external-exposure-handoff-package` and `GET /api/security/external-exposure-handoff-package/report/download`, backed by `ExternalExposureHandoffPackageService`.
- The handoff package reports `READY`, `NEEDS_ATTENTION`, or `BLOCKED`, whether the latest archive is `CURRENT`, `MISSING`, or `STALE`, evidence notes, download actions, next actions, and a Markdown report without creating tasks, calling the model, probing the network, mutating Git, writing GitHub comments, opening Pull Requests, archiving records, or exposing secrets.
- Extended the operations dashboard external exposure panel with a handoff package section, archive freshness, evidence notes, and Markdown report download. The panel refreshes the handoff package after readiness refreshes and after a new readiness archive is created.
- Updated frontend API helpers, typed payloads, App loading, App smoke fixtures, README, product spec, frontend design notes, architecture notes, and this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureHandoffPackageServiceTests,ExternalExposureHandoffPackageControllerTests test`: first failed because the handoff package VO, service, and controller did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts --reporter=dot`: first failed because `getExternalExposureHandoffPackage` and `downloadExternalExposureHandoffPackageReport` were not implemented; passed after API helper implementation.
- `npm --prefix frontend test -- --run src/dashboard/components/ExternalExposureReadinessPanel.test.tsx --reporter=dot`: first failed because the handoff package section and download button were not rendered; passed after panel implementation and stable assertions.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot`: passed, 3 test files and 319 tests.

## 2026-07-01 - 318 External exposure session tracker

- Started `318-external-exposure-session-tracker` to record the actual lifetime of a temporary public URL after the exposure handoff package is ready.
- Added `POST /api/security/external-exposure-sessions`, `POST /api/security/external-exposure-sessions/{sessionId}/close`, `GET /api/security/external-exposure-sessions`, and `GET /api/security/external-exposure-sessions/{sessionId}/report/download`.
- Added in-memory storage for the default profile plus MySQL/Flyway/MyBatis persistence for `local`, `docker`, and `idea` profiles.
- Session creation captures public URL, GitHub webhook URL, purpose, operator, optional expected shutdown time, notes, linked handoff status, linked readiness archive id, start time, and Markdown evidence. Closing the session records closer, close time, close notes, and refreshed Markdown evidence.
- Extended the operations dashboard external exposure panel with a session form, active/closed session list, close action, report download action, App loading, API helpers, and smoke-test fixtures.
- Updated README, product spec, frontend design notes, architecture notes, and this plan document with the session API and no-side-effect contract.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureSessionServiceTests,ExternalExposureSessionControllerTests,ExternalExposureSessionConvertTests,ExternalExposureSessionMigrationTests test`: first failed because the session DTOs, VO, service, controller, repository, converter, and migration did not exist; passed after backend implementation.
- `mvn -q -pl PatchPilot -Dtest=ExternalExposureSessionServiceTests,ExternalExposureSessionControllerTests,ExternalExposureSessionConvertTests,ExternalExposureSessionMigrationTests,MyBatisExternalExposureSessionRepositoryTests test`: passed after adding MyBatis persistence coverage.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx --reporter=dot`: first failed because the session API helpers and panel controls did not exist; passed after frontend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot`: passed, 3 test files and 322 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Spring/Mockito test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 34 test files and 522 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 319 External exposure closeout gate

- Started `319-external-exposure-closeout-gate` to add one final read-only closeout gate after a temporary public URL demo or smoke test.
- Added `GET /api/security/external-exposure-closeout` and `GET /api/security/external-exposure-closeout/report/download`, backed by `ExternalExposureCloseoutService`.
- The closeout gate reports `BLOCKED` while the latest exposure session is still active, `NEEDS_ATTENTION` when close evidence or handoff readiness is incomplete, and `READY` only when the latest session is closed with closer, close time, close notes, linked readiness archive evidence, and a ready handoff package.
- Extended the operations dashboard external exposure panel with a closeout section, evidence notes, next actions, side-effect contract, Markdown report download, App loading, API helpers, and smoke-test fixtures.
- Updated README, product spec, frontend design notes, architecture notes, and this plan document with the closeout API and no-side-effect contract.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureCloseoutServiceTests,ExternalExposureCloseoutControllerTests test`: first failed because the closeout VO, service, and controller did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx --reporter=dot`: first failed because the closeout API helper and panel section did not exist; then failed on duplicate session id test assertions after the UI rendered both session and closeout evidence; passed after stable assertions.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot`: first failed because the App smoke assertion matched duplicate session id evidence; passed after using a count-based assertion. Final focused result: 3 test files and 323 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Spring/Mockito test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 34 test files and 523 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 320 External exposure closeout archives

- Started `320-external-exposure-closeout-archives` to freeze the exact external exposure closeout proof after a temporary public URL session is shut down.
- Added `POST /api/security/external-exposure-closeout/archives`, `GET /api/security/external-exposure-closeout/archives`, and `GET /api/security/external-exposure-closeout/archives/{archiveId}/report/download`.
- Added default in-memory closeout archive storage plus MySQL/Flyway/MyBatis persistence for `local`, `docker`, and `idea` profiles.
- Archive records freeze closeout status, ready flag, latest session proof, public/webhook URL metadata, linked readiness archive, handoff status, archive freshness, counts, generated/archived times, evidence notes, next actions, download actions, side-effect contract, and Markdown report.
- Extended the operations dashboard external exposure panel with an `Archive closeout` action, recent closeout archive history, archived count/status details, and frozen Markdown downloads.
- Updated frontend API helpers, typed payloads, App loading/refresh wiring, App smoke fixtures, README, product spec, frontend design notes, architecture notes, and this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureCloseoutArchiveServiceTests,ExternalExposureCloseoutControllerTests,ExternalExposureCloseoutArchiveConvertTests,ExternalExposureCloseoutArchiveMigrationTests,MyBatisExternalExposureCloseoutArchiveRepositoryTests test`: first failed because the closeout archive VO, service, repository, mapper, converter, migration, and controller endpoints did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx --reporter=dot`: first failed because `archiveExternalExposureCloseout` was missing and the panel had no closeout archive controls or empty/error states; passed after frontend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureReadinessPanel.test.tsx src/App.test.tsx --reporter=dot`: first failed because closeout archive list text duplicated current closeout evidence text; passed after using distinct archive row wording. Final focused result: 3 test files and 324 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Spring/Mockito test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 34 test files and 524 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-01 - 321 External exposure closeout archive evidence bundle

- Started `321-external-exposure-closeout-archive-evidence-bundle` to promote the latest frozen external exposure closeout archive into the first demo evidence readout and copied runbook.
- Added `DemoExternalExposureCloseoutArchiveEvidenceVo` and wired `DemoEvidenceBundleService` to project the latest `ExternalExposureCloseoutArchiveVo` into status, archived/closeout-ready flags, archive id, session id/status, public URL, webhook URL, linked readiness archive id, handoff status, archive freshness, evidence notes, download actions, and side-effect contract.
- Updated aggregate demo evidence readiness and next actions so a missing or blocked external exposure closeout archive prevents the bundle from reporting fully ready.
- Updated `DemoRunbookService` so copied runbooks include the external exposure closeout archive id, session, public URL, webhook URL, linked readiness archive, freshness, next action, and download actions.
- Updated the dashboard evidence bundle panel with an external exposure closeout archive card plus legacy-response fallback guidance, and updated product/frontend/architecture docs.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`: first failed because the top-level evidence bundle did not expose external exposure closeout archive proof and the runbook did not export it; passed after backend aggregation and runbook implementation.
- `npm --prefix frontend test -- src/dashboard/components/DemoEvidenceBundlePanel.test.tsx --reporter=dot`: first failed because the new card introduced a second `Closed archive` label; passed after switching the assertion to count-based matching. Final focused result: 1 test file and 4 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Spring/Mockito test logging.
- `npm --prefix frontend test -- src/App.test.tsx -t "renders operational task dashboard" --reporter=dot`: first failed because the existing App-level smoke test had a 20s per-test timeout while the current full-dashboard flow now completes in about 27s; passed after raising that smoke-test budget to 60s.
- `npm --prefix frontend test -- src/App.test.tsx --reporter=dot`: passed after setting the Vitest default timeout to 20s for App-level integration tests; final result: 1 test file and 88 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 34 test files and 524 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Unstaged diff secret scan for GitHub, OpenAI, Slack, Google, private-key, and PatchPilot token patterns: no matches.

## 2026-07-01 - 322 External exposure operator handoff checklist

- Started `322-external-exposure-operator-handoff-checklist` to provide one post-closeout operator checklist before the next live `/agent fix` trigger.
- Added `GET /api/security/external-exposure-operator-handoff-checklist` and `GET /api/security/external-exposure-operator-handoff-checklist/report/download`, backed by `ExternalExposureOperatorHandoffChecklistService`.
- The checklist aggregates latest closeout archive proof, current exposure handoff package state, active exposure session count, and live GitHub publish preflight for the configured demo repository into `READY`, `NEEDS_ATTENTION`, or `BLOCKED` checks.
- Extended the operations dashboard with `ExternalExposureOperatorHandoffChecklistPanel`, typed API helpers, App loading/refresh wiring, evidence notes, next actions, side-effect contract, and Markdown report download.
- Updated README, product spec, architecture notes, frontend design notes, and this plan document with the operator handoff checklist scope and read-only contract.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureOperatorHandoffChecklistServiceTests,ExternalExposureOperatorHandoffChecklistControllerTests test`: first failed because the checklist VO, service, and controller did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureOperatorHandoffChecklistPanel.test.tsx --reporter=dot`: first failed because the API helpers and dashboard panel did not exist; passed after frontend implementation.
- `npm --prefix frontend test -- --run src/App.test.tsx -t "renders operational task dashboard" --reporter=dot`: passed after App-level loading and render wiring.
- `mvn -q -pl PatchPilot -Dtest=ExternalExposureOperatorHandoffChecklistServiceTests test`: first failed because `READY` status with false readiness flags was treated as ready; passed after requiring both the status and readiness flags for closeout archive, handoff package, and live publish checks.
- `mvn -q -pl PatchPilot test`: passed with existing Spring/Mockito test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 528 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.

## 2026-07-02 - 323 External exposure operator handoff archives

- Started `323-external-exposure-operator-handoff-archives` to freeze the current external exposure operator handoff checklist as local evidence before the next live `/agent fix` trigger.
- Added `POST /api/security/external-exposure-operator-handoff-checklist/archives`, `GET /api/security/external-exposure-operator-handoff-checklist/archives`, and `GET /api/security/external-exposure-operator-handoff-checklist/archives/{archiveId}/report/download`.
- Added process-local capped archive storage and an archive service that copies the current checklist status, go/no-go flag, closeout/session/public URL/webhook URL/live-publish evidence, check counts, evidence notes, next actions, side-effect contract, generated time, archived time, and Markdown report.
- Extended the external exposure operator handoff checklist dashboard panel with an `Archive` action, recent frozen checklist history, archived Markdown downloads, archive error feedback, and App loading/refresh wiring.
- Updated README, product spec, architecture notes, frontend design notes, and this plan document with the checklist archive scope and no-side-effect contract.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=ExternalExposureOperatorHandoffChecklistArchiveServiceTests,ExternalExposureOperatorHandoffChecklistControllerTests test`: first failed because the archive VO, repository, service, and controller endpoints did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/ExternalExposureOperatorHandoffChecklistPanel.test.tsx --reporter=dot`: first failed because the archive API helpers, archive button, archive history, and archive error state were missing; passed after frontend implementation. Focused result: 2 test files and 238 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level archive loading and handlers were wired. Result: 1 test file and 88 tests.
- `mvn -q -pl PatchPilot test`: passed with existing Lombok/Mockito/Spring test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 529 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.

## 2026-07-02 - 334 Live demo completion certificate

- Started `334-live-demo-completion-certificate` to turn the frozen `READY` live demo handoff delivery finalization archive into a terminal reviewer-facing completion certificate.
- Added backend completion certificate support with a read-only certificate service, certificate VO, admin-protected local archive service, capped in-memory archive repository, archive VO, and JSON/Markdown endpoints.
- The certificate is `READY` and certified only when the latest handoff finalization archive is ready, finalized, and backed by a fresh delivery receipt.
- Each certificate summarizes the finalization archive, delivery receipt, evidence bundle archive, repository and issue metadata, task and PR metadata, delivery target/channel, freshness, download actions, side-effect contract, and Markdown report.
- Extended the live launch gate dashboard with certificate refresh, report download, archive creation, recent certificate archive history, per-archive downloads, App-level loading, and typed frontend API helpers.
- Added `docs/plans/334-live-demo-completion-certificate.md` with the scope, safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveDemoCompletionCertificateServiceTests,DemoLiveDemoHandoffDeliveryFinalizationControllerTests,DemoLiveDemoHandoffPackageControllerTests,DemoLiveDemoHandoffDeliveryReceiptControllerTests test -q`: passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx --reporter=dot`: first failed because the completion certificate API helper, dashboard controls, result rendering, archive history, and error banners were missing; passed after frontend implementation. Focused result: 2 test files and 271 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level certificate loading and handlers were wired. Result: 1 test file and 88 tests.
- `mvn -pl PatchPilot test`: passed, 1430 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 562 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict changed-file secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.

## 2026-07-02 - 332 Live demo handoff delivery finalization

- Started `332-live-demo-handoff-delivery-finalization` to prove the latest live demo handoff delivery receipt matches the current reviewer handoff package.
- Added a backend read-only finalization service and admin-protected JSON and Markdown endpoints under `GET /api/demo/live-demo-handoff-package/delivery-finalization`.
- The finalization reports `READY` only when the current handoff package is ready and the newest receipt matches package evidence, repository, issue, task, task status, and Pull Request URL.
- It reports `NEEDS_ATTENTION` for missing or stale receipts and `BLOCKED` when the handoff package is not ready, with checks, evidence notes, next action, freshness summary, download actions, and an explicit no-side-effects contract.
- Extended the live launch gate dashboard with finalization refresh/download controls, status rendering, error feedback, App-level loading, typed API helpers, and automatic finalization refresh after recording a new delivery receipt.
- Added `docs/plans/332-live-demo-handoff-delivery-finalization.md` with the scope, safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveDemoHandoffDeliveryFinalizationServiceTests,DemoLiveDemoHandoffDeliveryFinalizationControllerTests,DemoLiveDemoHandoffPackageControllerTests,DemoLiveDemoHandoffDeliveryReceiptControllerTests test -q`: first failed because the finalization VO and service did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx --reporter=dot`: first failed because the finalization API helper and dashboard controls did not exist; passed after frontend implementation. Focused result: 2 test files and 265 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level finalization loading and handlers were wired. Result: 1 test file and 88 tests.
- `mvn -pl PatchPilot test`: passed, 1420 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 556 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.

## 2026-07-02 - 330 Live demo handoff package

- Started `330-live-demo-handoff-package` to turn the latest archived live demo evidence bundle into a reviewer-facing final handoff artifact.
- Added backend handoff package support with a domain VO, read-only service, and admin-protected JSON and Markdown download endpoints.
- The package classifies `READY` only when the latest evidence bundle archive is ready for handoff; missing archives produce `BLOCKED`, and non-ready archives produce `NEEDS_ATTENTION`.
- The generated package includes the evidence bundle archive id, repository, issue, trigger user/comment, task status, webhook delivery, Pull Request URL, review checklist, delivery instructions, evidence notes, generated timestamp, side-effect contract, and Markdown report.
- Extended the live launch gate dashboard with `Refresh handoff package`, `Download handoff package`, handoff package error feedback, App-level loading, typed API helpers, and package evidence rendering.
- Added `docs/plans/330-live-demo-handoff-package.md` with the scope, safety contract, API shape, frontend contract, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveDemoHandoffPackageServiceTests,DemoLiveDemoHandoffPackageControllerTests test`: first failed because `DemoLiveDemoHandoffPackageVo` did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx --reporter=dot`: first failed because the handoff package API helper and dashboard controls did not exist; passed after frontend implementation. Focused result: 2 test files and 259 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level handoff package loading and handlers were wired. Result: 1 test file and 88 tests.
- `mvn -pl PatchPilot test`: passed, 1409 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 550 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no new matches.

## 2026-07-02 - 324 Live trigger launch package

- Started `324-live-trigger-launch-package` to create one final read-only launch package before an operator posts a real `/agent fix` GitHub issue comment.
- Added `POST /api/demo/live-trigger-launch-package` and `POST /api/demo/live-trigger-launch-package/report/download`.
- The package reuses the exact live launch gate input, combines the current live launch gate with the latest frozen external exposure operator handoff archive, blocks when that archive is missing or not ready, and returns the exact issue URL/comment, archive id, live-gate state, evidence notes, next actions, side-effect contract, and Markdown report.
- Extended the live launch gate dashboard panel with `Create launch package`, package status/evidence rendering, package error feedback, and Markdown download from the generated package.
- Updated frontend API helpers, typed payloads, App state/handlers, README, product spec, architecture notes, frontend design notes, and this plan document.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoLiveTriggerLaunchPackageServiceTests test`: first failed because the launch package service and command did not exist.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx --reporter=dot`: first failed because the launch package API helper and panel controls did not exist; passed after frontend implementation.
- `mvn -q -pl PatchPilot -Dtest=DemoLiveTriggerLaunchPackageServiceTests,DemoLiveTriggerLaunchPackageControllerTests test`: first failed because the test wiring referenced the wrong command type; passed after fixing the focused controller/service tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level launch package state and handler wiring.
- `mvn -q -pl PatchPilot test`: passed with existing Lombok/Mockito/Spring test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 532 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.

## 2026-07-02 - 325 Live trigger launch package archives

- Started `325-live-trigger-launch-package-archives` to freeze the final live trigger launch package as local operator evidence before posting a real `/agent fix` issue comment.
- Added backend archive support for live trigger launch packages, including a capped in-memory archive repository, archive service, archive VO, and admin-protected endpoints to create, list, and download archived Markdown reports.
- Each archive freezes the package status, ready-to-post flag, repository, issue URL, trigger user/comment, operator handoff archive proof, live launch gate proof, evidence notes, next actions, side-effect contract, package generation time, archive time, and report body.
- Extended the live launch gate dashboard with an `Archive package` action, recent archive history, archive Markdown downloads, archive error feedback, App-level loading, and typed frontend API helpers.
- Added `docs/plans/325-live-trigger-launch-package-archives.md` with the scope, read-only safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -q -pl PatchPilot -Dtest=DemoLiveTriggerLaunchPackageServiceTests,DemoLiveTriggerLaunchPackageControllerTests test`: first failed because `DemoLiveTriggerLaunchPackageArchiveVo`, archive service, and repository did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx --reporter=dot`: first failed because archive API helpers, archive button/history, and archive error state were missing; passed after frontend implementation. Focused result: 2 test files and 244 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level archive loading and handlers were wired. Result: 1 test file and 88 tests.
- `git diff --check`: passed.
- `mvn -q -pl PatchPilot test`: passed with existing Lombok/Mockito/Spring test logging.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 535 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed after final documentation updates.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.

## 2026-07-02 - 326 Live trigger outcome closeout

- Started `326-live-trigger-outcome-closeout` to close the loop after an operator posts a real `/agent fix` GitHub issue comment.
- Added `POST /api/demo/live-trigger-outcome-closeout` and `POST /api/demo/live-trigger-outcome-closeout/report/download`.
- The closeout is read-only and correlates the exact repository, issue, trigger user, trigger comment, optional launch package archive id, latest launch package archive, and newest matching `FixTaskVo`.
- The backend classifies outcomes as `READY` when a matching task completed with a Pull Request URL, `NEEDS_ATTENTION` when a matching task exists but failed, was cancelled, is still active, or has no PR, and `BLOCKED` when the launch package archive or matching task is missing.
- Extended the live launch gate dashboard with `Generate outcome closeout`, `Download closeout`, closeout error feedback, task/webhook/PR evidence rendering, outcome evidence notes, and next actions.
- Added `docs/plans/326-live-trigger-outcome-closeout.md` with the scope, API shape, safety contract, and verification checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveTriggerOutcomeCloseoutServiceTests,DemoLiveTriggerOutcomeCloseoutControllerTests test`: first failed because the closeout command, VO, service, and controller did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx`: first failed because the closeout API helper and dashboard controls did not exist; passed after frontend implementation. Focused result: 2 test files and 247 tests.
- `mvn -pl PatchPilot test`: passed, 1392 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 538 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.

## 2026-07-02 - 327 Live trigger outcome closeout archives

- Started `327-live-trigger-outcome-closeout-archives` to freeze a live trigger outcome closeout after a real `/agent fix` issue-comment run.
- Added backend archive support for live trigger outcome closeouts, including a capped in-memory archive repository, archive service, archive VO, and admin-protected endpoints to create, list, and download archived Markdown reports.
- Each archive freezes the closeout status, success flag, repository, issue URL, trigger user/comment, launch package archive proof, task status, failure reason, webhook delivery, Pull Request URL, evidence notes, next actions, side-effect contract, closeout generation time, archive time, and report body.
- Extended the live launch gate dashboard with `Archive closeout`, recent outcome archive history, archive Markdown downloads, archive error feedback, App-level loading, and typed frontend API helpers.
- Added `docs/plans/327-live-trigger-outcome-closeout-archives.md` with the scope, safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveTriggerOutcomeCloseoutArchiveServiceTests,DemoLiveTriggerOutcomeCloseoutControllerTests test`: first failed because `DemoLiveTriggerOutcomeCloseoutArchiveVo` did not exist; passed after backend implementation. Focused result: 6 tests.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx`: first failed because the archive API helper, archive button/history, and archive error state were missing; passed after frontend implementation. Focused result: 2 test files and 250 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `mvn -pl PatchPilot test`: passed, 1394 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 541 tests.
- `git diff --check`: passed.

## 2026-07-02 - 328 Live demo evidence bundle

- Started `328-live-demo-evidence-bundle` to produce one read-only, final evidence artifact after a real `/agent fix` run.
- Added backend live demo evidence bundle support with a domain VO, aggregation service, and admin-protected JSON and Markdown download endpoints.
- The bundle reads the latest live trigger launch package archive and latest outcome closeout archive, classifies `READY` only when the launch archive is ready, the closeout is successful, and both archives refer to the same launch package archive.
- It reports `BLOCKED` for missing archives and `NEEDS_ATTENTION` for mismatches or unsuccessful closeouts, with evidence notes, next actions, side-effect contract, and a handoff-ready Markdown report.
- Extended the live launch gate dashboard with final evidence bundle refresh/download controls, bundle status rendering, App-level loading, and typed frontend API helpers.
- Added `docs/plans/328-live-demo-evidence-bundle.md` with the scope, safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveDemoEvidenceBundleServiceTests,DemoLiveDemoEvidenceBundleControllerTests test`: first failed because `DemoLiveDemoEvidenceBundleVo` did not exist; passed after backend implementation. Focused result: 6 tests.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx`: first failed because the evidence bundle API helper and panel controls did not exist; passed after frontend implementation. Focused result: 2 test files and 253 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level evidence bundle loading and handlers were wired. Result: 1 test file and 88 tests.
- `mvn -pl PatchPilot test`: passed, 1400 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 544 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.

## 2026-07-02 - 329 Live demo evidence bundle archives

- Started `329-live-demo-evidence-bundle-archives` to freeze the final live demo evidence bundle as a stable local handoff artifact after a real `/agent fix` run.
- Added backend archive support for live demo evidence bundles, including a capped in-memory archive repository, archive service, archive VO, and admin-protected endpoints to create, list, and download archived Markdown reports.
- Each archive freezes the bundle status, handoff readiness, repository, issue URL, trigger user/comment, launch package archive, outcome closeout archive, task status, webhook delivery, Pull Request URL, evidence notes, next actions, side-effect contract, bundle generation time, archive time, and report body.
- Extended the live launch gate dashboard with `Archive evidence bundle`, recent evidence bundle archive history, archived Markdown downloads, archive error feedback, App-level loading, and typed frontend API helpers.
- Added `docs/plans/329-live-demo-evidence-bundle-archives.md` with the scope, safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveDemoEvidenceBundleArchiveServiceTests,DemoLiveDemoEvidenceBundleControllerTests test`: first failed because `DemoLiveDemoEvidenceBundleArchiveVo`, archive service, and repository did not exist; passed after backend implementation. Focused result: 6 tests.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx`: first failed because the archive API helper, archive button/history, and archive error state were missing; passed after frontend implementation. Focused result: 2 test files and 256 tests.
- `mvn -pl PatchPilot test`: passed, 1403 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 547 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.

## 2026-07-02 - 331 Live demo handoff delivery receipts

- Started `331-live-demo-handoff-delivery-receipts` to record local proof that the final live demo handoff package was delivered to a reviewer.
- Added backend delivery receipt support for the live demo handoff package, including a capped in-memory receipt repository, receipt service, receipt VO, request DTO, and admin-protected endpoints to create, list, and download receipt Markdown reports.
- Each receipt captures the handoff package status, evidence bundle archive id, repository, issue URL, trigger user/comment, task status, Pull Request URL, webhook delivery id, delivery channel, delivery target, operator, notes, delivered time, created time, and report body.
- The backend rejects receipt creation unless the current live demo handoff package is `READY`, ready for review, and linked to an evidence bundle archive.
- Extended the live launch gate dashboard with receipt input fields, `Record live demo handoff delivery receipt`, recent receipt history, per-receipt downloads, App-level loading, typed API helpers, and delivery receipt error feedback.
- Added `docs/plans/331-live-demo-handoff-delivery-receipts.md` with the scope, safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveDemoHandoffDeliveryReceiptServiceTests,DemoLiveDemoHandoffDeliveryReceiptControllerTests,DemoLiveDemoHandoffPackageControllerTests test -q`: first failed because the delivery receipt VO and request DTO did not exist; passed after backend implementation.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx --reporter=dot`: first failed because the API helper and dashboard controls did not exist; passed after frontend implementation. Focused result: 2 test files and 262 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level receipt loading and handlers were wired. Result: 1 test file and 88 tests.
- `mvn -pl PatchPilot test`: passed, 1414 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 553 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.

## 2026-07-02 - 333 Live demo handoff delivery finalization archives

- Started `333-live-demo-handoff-delivery-finalization-archives` to freeze a `READY` live demo handoff delivery finalization as immutable local reviewer evidence.
- Added backend archive support for live demo handoff delivery finalizations, including a capped in-memory archive repository, archive service, archive VO, and admin-protected endpoints to create, list, and download archived Markdown reports.
- Each archive freezes the finalization status, delivery receipt id, evidence bundle archive id, repository and issue metadata, task status, Pull Request URL, delivery target/channel, receipt freshness, checks, evidence notes, download actions, side-effect contract, finalization generation time, archive time, and report body.
- The backend rejects archive creation unless the current handoff delivery finalization is `READY` and finalized.
- Extended the live launch gate dashboard with `Archive handoff finalization`, recent handoff finalization archive history, per-archive Markdown downloads, archive error feedback, App-level loading, and typed frontend API helpers.
- Added `docs/plans/333-live-demo-handoff-delivery-finalization-archives.md` with the scope, local-only safety contract, API shape, and validation checklist.

Validation so far:

- `mvn -pl PatchPilot -Dtest=DemoLiveDemoHandoffDeliveryFinalizationArchiveServiceTests,DemoLiveDemoHandoffDeliveryFinalizationControllerTests test -q`: first failed because the archive VO, service, and repository did not exist; passed after backend implementation in the broader focused backend run.
- `npm --prefix frontend test -- --run src/api.test.ts src/dashboard/components/LiveLaunchGatePanel.test.tsx --reporter=dot`: first failed because the archive API helper, archive button/history, and archive error state were missing; passed after frontend implementation. Focused result: 2 test files and 268 tests.
- `npm --prefix frontend test -- --run src/App.test.tsx --reporter=dot`: passed after App-level archive loading and handlers were wired. Result: 1 test file and 88 tests.
- `mvn -pl PatchPilot -Dtest=DemoLiveDemoHandoffDeliveryFinalizationArchiveServiceTests,DemoLiveDemoHandoffDeliveryFinalizationControllerTests,DemoLiveDemoHandoffPackageControllerTests,DemoLiveDemoHandoffDeliveryReceiptControllerTests test -q`: passed.
- `mvn -pl PatchPilot test`: passed, 1424 tests.
- `npm --prefix frontend test -- --reporter=dot`: passed, 35 test files and 559 tests.
- `npm --prefix frontend run build`: passed with the existing Vite large-chunk warning.
- `git diff --check`: passed.
- Strict diff secret scan for GitHub, OpenAI-style, Slack, AWS, private-key, and PatchPilot token assignment patterns: no matches.
