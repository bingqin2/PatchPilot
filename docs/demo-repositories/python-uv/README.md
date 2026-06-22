# Python uv Demo

Adapter: Python/uv

Verification command: `uv run pytest`

This fixture contains `uv.lock`, `pyproject.toml`, pytest configuration, a tiny Python module, and a pytest test. PatchPilot should detect it through the Python/uv adapter before the broader Python/pytest adapter.
