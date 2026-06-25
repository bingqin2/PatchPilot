package io.patchpilot.backend.demo;

import io.patchpilot.backend.demo.domain.DemoLaunchCommandVo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class DemoLaunchCommandService {

    public DemoLaunchCommandVo compose(DemoLaunchCommandRequestDto request) {
        RequestParts parts = requestParts(request);
        String triggerComment = triggerComment(parts);
        DemoLaunchPreflightRequestDto preflightInput = new DemoLaunchPreflightRequestDto(
                parts.repositoryOwner(),
                parts.repositoryName(),
                parts.issueNumber(),
                parts.triggerUser(),
                triggerComment
        );
        String githubIssueUrl = "https://github.com/%s/%s/issues/%d".formatted(
                parts.repositoryOwner(),
                parts.repositoryName(),
                parts.issueNumber()
        );
        return new DemoLaunchCommandVo(
                triggerComment,
                preflightInput,
                githubIssueUrl,
                "Prepared a demo /agent fix %s command for %s/%s#%d.".formatted(
                        parts.operation(),
                        parts.repositoryOwner(),
                        parts.repositoryName(),
                        parts.issueNumber()
                ),
                List.of(
                        "Run launch preflight with the generated command before posting it on GitHub.",
                        "Post the generated command on %s only after preflight reports ready.".formatted(githubIssueUrl)
                )
        );
    }

    private static RequestParts requestParts(DemoLaunchCommandRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        String repositoryOwner = requiredText(request.repositoryOwner(), "repositoryOwner must not be blank");
        String repositoryName = requiredText(request.repositoryName(), "repositoryName must not be blank");
        if (request.issueNumber() == null || request.issueNumber() < 1) {
            throw new IllegalArgumentException("issueNumber must be positive");
        }
        String triggerUser = requiredText(request.triggerUser(), "triggerUser must not be blank");
        String operation = requiredText(request.operation(), "operation must not be blank").toLowerCase(Locale.ROOT);
        if (!operation.equals("replace") && !operation.equals("touch")) {
            throw new IllegalArgumentException("operation must be replace or touch");
        }
        String targetPath = normalizedTargetPath(request.targetPath());
        String replacementText = null;
        if (operation.equals("replace")) {
            replacementText = normalizedReplacementText(request.replacementText());
        }
        return new RequestParts(
                repositoryOwner,
                repositoryName,
                request.issueNumber(),
                triggerUser,
                operation,
                targetPath,
                replacementText
        );
    }

    private static String triggerComment(RequestParts parts) {
        if (parts.operation().equals("touch")) {
            return "/agent fix touch " + parts.targetPath();
        }
        return "/agent fix replace " + parts.targetPath() + " " + parts.replacementText();
    }

    private static String normalizedTargetPath(String value) {
        String targetPath = requiredText(value, "targetPath must not be blank").replace('\\', '/');
        if (targetPath.startsWith("/")) {
            throw new IllegalArgumentException("targetPath must be repository-relative");
        }
        if (targetPath.contains("//")) {
            throw new IllegalArgumentException("targetPath must not contain empty path segments");
        }
        if (containsWhitespace(targetPath)) {
            throw new IllegalArgumentException("targetPath must not contain whitespace");
        }
        List<String> segments = Arrays.asList(targetPath.split("/"));
        if (segments.contains("..")) {
            throw new IllegalArgumentException("targetPath must not contain .. path segments");
        }
        if (segments.stream().anyMatch(String::isBlank)) {
            throw new IllegalArgumentException("targetPath must not contain empty path segments");
        }
        if (segments.get(0).equals(".git") || segments.get(0).equals(".github")) {
            throw new IllegalArgumentException("targetPath must not target protected repository metadata");
        }
        return targetPath;
    }

    private static String normalizedReplacementText(String value) {
        if (!hasText(value)) {
            throw new IllegalArgumentException("replacementText must not be blank for replace commands");
        }
        if (value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("replacementText must be a single line");
        }
        return value.replaceAll("\\s+", " ").trim();
    }

    private static String requiredText(String value, String message) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean containsWhitespace(String value) {
        return value.chars().anyMatch(Character::isWhitespace);
    }

    private record RequestParts(
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String operation,
            String targetPath,
            String replacementText
    ) {
    }
}
