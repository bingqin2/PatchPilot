package io.patchpilot.backend.language.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.language.LanguageAdapter;
import io.patchpilot.backend.language.domain.LanguageDetectionResult;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

abstract class NodePackageManagerLanguageAdapter implements LanguageAdapter {

    private final ObjectMapper objectMapper;
    private final String buildSystem;
    private final List<String> lockfileNames;
    private final List<String> verificationCommand;
    private final String supportedReason;

    protected NodePackageManagerLanguageAdapter(
            String buildSystem,
            String lockfileName,
            List<String> verificationCommand,
            String supportedReason
    ) {
        this(new ObjectMapper(), buildSystem, lockfileName == null ? List.of() : List.of(lockfileName), verificationCommand, supportedReason);
    }

    protected NodePackageManagerLanguageAdapter(
            String buildSystem,
            List<String> lockfileNames,
            List<String> verificationCommand,
            String supportedReason
    ) {
        this(new ObjectMapper(), buildSystem, lockfileNames, verificationCommand, supportedReason);
    }

    NodePackageManagerLanguageAdapter(
            ObjectMapper objectMapper,
            String buildSystem,
            String lockfileName,
            List<String> verificationCommand,
            String supportedReason
    ) {
        this(objectMapper, buildSystem, lockfileName == null ? List.of() : List.of(lockfileName), verificationCommand, supportedReason);
    }

    NodePackageManagerLanguageAdapter(
            ObjectMapper objectMapper,
            String buildSystem,
            List<String> lockfileNames,
            List<String> verificationCommand,
            String supportedReason
    ) {
        this.objectMapper = objectMapper;
        this.buildSystem = buildSystem;
        this.lockfileNames = lockfileNames == null ? List.of() : List.copyOf(lockfileNames);
        this.verificationCommand = List.copyOf(verificationCommand);
        this.supportedReason = supportedReason;
    }

    @Override
    public LanguageDetectionResult detect(Path repositoryDir) {
        Path repositoryRoot = repositoryDir.toAbsolutePath().normalize();
        Path packageJson = repositoryRoot.resolve("package.json");
        if (!Files.isRegularFile(packageJson)) {
            return unsupported("Unsupported repository: no package.json found");
        }
        if (!lockfileNames.isEmpty() && lockfileNames.stream().noneMatch(lockfileName -> Files.isRegularFile(repositoryRoot.resolve(lockfileName)))) {
            return unsupported("Unsupported repository: no " + String.join(" or ", lockfileNames) + " found");
        }
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(packageJson.toFile());
        } catch (IOException e) {
            return unsupported("Unsupported repository: package.json could not be parsed");
        }
        if (!StringUtils.hasText(rootNode.path("scripts").path("test").asText())) {
            return unsupported("Unsupported repository: package.json has no scripts.test command");
        }
        return LanguageDetectionResult.supported("node", buildSystem, verificationCommand, supportedReason);
    }

    private LanguageDetectionResult unsupported(String reason) {
        return LanguageDetectionResult.unsupported("node", buildSystem, reason);
    }
}
