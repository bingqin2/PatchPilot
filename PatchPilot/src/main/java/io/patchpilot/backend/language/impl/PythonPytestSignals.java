package io.patchpilot.backend.language.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

final class PythonPytestSignals {

    private PythonPytestSignals() {
    }

    static boolean hasPytestConfiguration(Path repositoryRoot) {
        return Files.isRegularFile(repositoryRoot.resolve("pytest.ini"))
                || containsText(repositoryRoot.resolve("pyproject.toml"), "[tool.pytest.ini_options]");
    }

    static boolean hasPytestDependencyInRequirements(Path repositoryRoot) {
        Path requirementsFile = repositoryRoot.resolve("requirements.txt");
        if (!Files.isRegularFile(requirementsFile)) {
            return false;
        }
        try {
            return Files.readAllLines(requirementsFile).stream()
                    .map(line -> line.strip().toLowerCase(Locale.ROOT))
                    .anyMatch(PythonPytestSignals::isPytestRequirement);
        } catch (IOException e) {
            return false;
        }
    }

    static boolean hasPytestDependencyInPyproject(Path repositoryRoot) {
        String pyproject = readString(repositoryRoot.resolve("pyproject.toml"));
        return containsPytestDependency(pyproject);
    }

    static boolean containsText(Path path, String expectedText) {
        return readString(path).contains(expectedText);
    }

    private static String readString(Path path) {
        if (!Files.isRegularFile(path)) {
            return "";
        }
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }

    private static boolean containsPytestDependency(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);
        return normalized.contains("pytest =")
                || normalized.contains("\"pytest")
                || normalized.contains("'pytest");
    }

    private static boolean isPytestRequirement(String line) {
        return line.equals("pytest")
                || line.startsWith("pytest==")
                || line.startsWith("pytest>=")
                || line.startsWith("pytest<=")
                || line.startsWith("pytest~=")
                || line.startsWith("pytest[");
    }
}
