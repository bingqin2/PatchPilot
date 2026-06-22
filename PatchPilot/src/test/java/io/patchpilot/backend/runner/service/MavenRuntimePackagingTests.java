package io.patchpilot.backend.runner.service;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MavenRuntimePackagingTests {

    @Test
    void should_package_maven_in_java17_runtime_image() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("FROM maven:3.9-eclipse-temurin-17 AS build");
        assertThat(dockerfile).contains("FROM maven:3.9-eclipse-temurin-17\n\nWORKDIR /app");
        assertThat(dockerfile).doesNotContain("git ca-certificates maven");
    }

    @Test
    void should_install_nodejs_and_npm_for_node_adapter_verification() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("apt-get install -y --no-install-recommends");
        assertThat(dockerfile).contains("git ca-certificates");
        assertThat(dockerfile).contains("nodejs npm");
    }

    @Test
    void should_install_pnpm_and_yarn_for_node_package_manager_adapters() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("npm install -g pnpm yarn");
    }

    @Test
    void should_install_bun_for_node_bun_adapter() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("curl -fsSL https://bun.sh/install | bash");
        assertThat(dockerfile).contains("BUN_INSTALL=/usr/local/bun");
        assertThat(dockerfile).contains("PATH=\"/usr/local/bun/bin:${PATH}\"");
    }

    @Test
    void should_install_python_and_pytest_for_python_adapter_verification() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("python3");
        assertThat(dockerfile).contains("python3-pip");
        assertThat(dockerfile).contains("python3-pytest");
    }

    @Test
    void should_install_poetry_and_uv_for_python_project_runner_adapters() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("python3 -m pip install --no-cache-dir poetry uv");
    }

    @Test
    void should_install_tox_nox_and_hatch_for_python_runner_adapters() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("python3 -m pip install --no-cache-dir poetry uv tox nox hatch");
    }

    @Test
    void should_copy_adapter_demo_fixtures_for_runtime_fixture_verification_api() throws Exception {
        String dockerfile = Files.readString(Path.of("Dockerfile"));

        assertThat(dockerfile).contains("COPY docs/demo-repositories /app/docs/demo-repositories");
    }
}
