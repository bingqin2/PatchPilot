package io.patchpilot.backend.task.domain.bo;

import io.patchpilot.backend.task.domain.enums.FixTaskStatus;

public record FixTaskListQuery(
        String query,
        FixTaskStatus status,
        String repositoryOwner,
        String repositoryName,
        int limit,
        int offset
) {

    public static FixTaskListQuery all() {
        return new FixTaskListQuery(null, null, null, null, Integer.MAX_VALUE, 0);
    }
}
