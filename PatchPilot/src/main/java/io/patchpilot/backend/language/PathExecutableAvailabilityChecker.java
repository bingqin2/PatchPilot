package io.patchpilot.backend.language;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class PathExecutableAvailabilityChecker implements ExecutableAvailabilityChecker {

    @Override
    public boolean isAvailable(String executable) {
        if (!StringUtils.hasText(executable)) {
            return false;
        }
        Path executablePath = Path.of(executable);
        if (executablePath.isAbsolute() || executable.contains("/")) {
            return Files.isExecutable(executablePath.toAbsolutePath().normalize());
        }
        return pathEntries().stream()
                .map(entry -> entry.resolve(executable).normalize())
                .anyMatch(Files::isExecutable);
    }

    private static java.util.List<Path> pathEntries() {
        String path = System.getenv("PATH");
        if (!StringUtils.hasText(path)) {
            return java.util.List.of();
        }
        return java.util.Arrays.stream(path.split(java.io.File.pathSeparator))
                .filter(StringUtils::hasText)
                .map(Path::of)
                .toList();
    }
}
