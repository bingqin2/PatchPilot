package io.patchpilot.backend.task.domain.vo;

import java.util.List;

public record IssueContextVo(
        String title,
        String body,
        String url,
        List<IssueContextCommentVo> comments
) {
}
