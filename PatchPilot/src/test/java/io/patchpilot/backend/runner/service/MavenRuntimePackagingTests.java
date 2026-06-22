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

        assertThat(dockerfile).contains("apt-get install -y --no-install-recommends git ca-certificates nodejs npm");
    }
}
