# Node yarn Demo

Adapter: Node/yarn

Verification command: `yarn test`

This fixture contains `package.json`, `yarn.lock`, a tiny JavaScript module, and a Node test script. PatchPilot should detect it through the Node/yarn adapter before the broader npm adapter.
