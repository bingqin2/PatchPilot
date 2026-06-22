package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.GeneratedDiffRiskDecision;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class GeneratedDiffRiskGate {

    private static final int MAX_CHANGED_LINES = 400;
    private static final int MAX_FILES_CHANGED = 20;

    private static final Pattern DIFF_PATH_PATTERN = Pattern.compile("^diff --git a/(.+?) b/(.+)$");
    private static final Pattern SECRET_ASSIGNMENT_PATTERN = Pattern.compile(
            "(?i).*(token|secret|api[_-]?key|password|private[_-]?key)\\s*[:=]\\s*['\\\"]?[A-Za-z0-9_./+=-]{16,}.*"
    );
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile(".*BEGIN [A-Z ]*PRIVATE KEY.*");

    public GeneratedDiffRiskDecision evaluate(String diff) {
        if (diff == null || diff.isBlank()) {
            return GeneratedDiffRiskDecision.accepted();
        }

        int changedLines = 0;
        int filesChanged = 0;
        for (String line : diff.lines().toList()) {
            java.util.regex.Matcher pathMatcher = DIFF_PATH_PATTERN.matcher(line);
            if (pathMatcher.matches()) {
                filesChanged++;
                String newPath = pathMatcher.group(2);
                if (isSensitivePath(newPath)) {
                    return GeneratedDiffRiskDecision.rejected("sensitive path " + newPath);
                }
            }

            if (isBinaryDiffLine(line)) {
                return GeneratedDiffRiskDecision.rejected("binary file change");
            }
            if (isChangedContentLine(line)) {
                changedLines++;
            }
            if (isAddedSecretLikeLine(line)) {
                return GeneratedDiffRiskDecision.rejected("secret-like added line");
            }
        }

        if (filesChanged > MAX_FILES_CHANGED) {
            return GeneratedDiffRiskDecision.rejected("too many files changed: " + filesChanged);
        }
        if (changedLines > MAX_CHANGED_LINES) {
            return GeneratedDiffRiskDecision.rejected("too many changed lines: " + changedLines);
        }

        return GeneratedDiffRiskDecision.accepted();
    }

    private static boolean isSensitivePath(String path) {
        String normalized = path.toLowerCase(Locale.ROOT);
        return normalized.equals(".env")
                || normalized.startsWith(".env.")
                || normalized.endsWith("/.env")
                || normalized.contains("/.env.")
                || normalized.startsWith(".github/workflows/")
                || normalized.contains("/.github/workflows/")
                || normalized.endsWith(".pem")
                || normalized.endsWith(".key")
                || normalized.endsWith(".p12")
                || normalized.endsWith(".jks")
                || normalized.contains("id_rsa")
                || normalized.contains("id_dsa")
                || normalized.contains("id_ed25519");
    }

    private static boolean isBinaryDiffLine(String line) {
        return line.startsWith("Binary files ")
                || line.equals("GIT binary patch");
    }

    private static boolean isChangedContentLine(String line) {
        if (line.startsWith("+++ ") || line.startsWith("--- ")) {
            return false;
        }
        return line.startsWith("+") || line.startsWith("-");
    }

    private static boolean isAddedSecretLikeLine(String line) {
        if (!line.startsWith("+") || line.startsWith("+++ ")) {
            return false;
        }
        return SECRET_ASSIGNMENT_PATTERN.matcher(line).matches()
                || PRIVATE_KEY_PATTERN.matcher(line).matches();
    }
}
