package io.patchpilot.backend.task.domain.vo;

import java.util.List;

public record FixTaskPageVo(
        List<FixTaskVo> items,
        int limit,
        int offset,
        boolean hasMore
) {
}
