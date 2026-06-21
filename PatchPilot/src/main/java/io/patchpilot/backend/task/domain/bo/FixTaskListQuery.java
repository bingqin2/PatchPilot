package io.patchpilot.backend.task.domain.bo;

import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskSort;

public record FixTaskListQuery(
        String query,
        FixTaskStatus status,
        String repositoryOwner,
        String repositoryName,
        int limit,
        int offset,
        FixTaskSort sort
) {

    public FixTaskListQuery(
            String query,
            FixTaskStatus status,
            String repositoryOwner,
            String repositoryName,
            int limit,
            int offset
    ) {
        this(query, status, repositoryOwner, repositoryName, limit, offset, FixTaskSort.CREATED_AT_DESC);
    }

    public static FixTaskListQuery all() {
        return new FixTaskListQuery(null, null, null, null, Integer.MAX_VALUE, 0, FixTaskSort.CREATED_AT_DESC);
    }
}
