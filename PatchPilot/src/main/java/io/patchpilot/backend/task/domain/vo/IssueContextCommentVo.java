package io.patchpilot.backend.task.domain.vo;

public record IssueContextCommentVo(
        long id,
        String author,
        String body,
        String createdAt,
        String url
) {
}
