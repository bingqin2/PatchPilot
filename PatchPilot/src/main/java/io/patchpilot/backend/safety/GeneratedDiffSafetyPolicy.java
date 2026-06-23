package io.patchpilot.backend.safety;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class GeneratedDiffSafetyPolicy {

    private static final int DEFAULT_MAX_CHANGED_LINES = 400;
    private static final int DEFAULT_MAX_FILES_CHANGED = 20;

    private static final List<String> DEFAULT_PROTECTED_PATH_MARKERS = List.of(
            ".env",
            ".env.",
            ".git/",
            ".github/workflows/",
            ".npmrc",
            ".pypirc",
            ".netrc",
            "settings.xml",
            ".m2/settings.xml",
            ".pem",
            ".key",
            ".p12",
            ".jks",
            "id_rsa",
            "id_ed25519"
    );

    private static final Pattern SECRET_ASSIGNMENT_PATTERN = Pattern.compile(
            "(?i).*(token|secret|api[_-]?key|password|private[_-]?key)\\s*[:=]\\s*['\\\"]?[A-Za-z0-9_./+=-]{16,}.*"
    );
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile(".*BEGIN [A-Z ]*PRIVATE KEY.*");

    public boolean enabled() {
        return true;
    }

    public int protectedPathCount() {
        return DEFAULT_PROTECTED_PATH_MARKERS.size();
    }

    public int maxChangedLines() {
        return DEFAULT_MAX_CHANGED_LINES;
    }

    public int maxFilesChanged() {
        return DEFAULT_MAX_FILES_CHANGED;
    }

    public boolean isProtectedPath(String path) {
        if (path == null || path.isBlank()) {
            return false;
        }
        String normalized = path.toLowerCase(Locale.ROOT);
        return DEFAULT_PROTECTED_PATH_MARKERS.stream()
                .anyMatch(marker -> matchesProtectedMarker(normalized, marker));
    }

    public boolean isBinaryDiffLine(String line) {
        return line.startsWith("Binary files ")
                || line.equals("GIT binary patch");
    }

    public boolean isChangedContentLine(String line) {
        if (line.startsWith("+++ ") || line.startsWith("--- ")) {
            return false;
        }
        return line.startsWith("+") || line.startsWith("-");
    }

    public boolean isAddedSecretLikeLine(String line) {
        if (!line.startsWith("+") || line.startsWith("+++ ")) {
            return false;
        }
        return SECRET_ASSIGNMENT_PATTERN.matcher(line).matches()
                || PRIVATE_KEY_PATTERN.matcher(line).matches();
    }

    private static boolean matchesProtectedMarker(String normalizedPath, String marker) {
        return switch (marker) {
            case ".env" -> normalizedPath.equals(".env") || normalizedPath.endsWith("/.env");
            case ".env." -> normalizedPath.startsWith(".env.") || normalizedPath.contains("/.env.");
            case ".github/workflows/" -> normalizedPath.startsWith(marker) || normalizedPath.contains("/" + marker);
            case ".npmrc", ".pypirc", ".netrc" -> normalizedPath.equals(marker) || normalizedPath.endsWith("/" + marker);
            case "settings.xml" -> normalizedPath.equals(marker) || normalizedPath.endsWith("/" + marker);
            case ".m2/settings.xml" -> normalizedPath.endsWith(marker);
            default -> normalizedPath.contains(marker);
        };
    }
}
