package io.patchpilot.backend.task.domain.bo;

import io.patchpilot.backend.task.domain.enums.FixTaskStatus;
import io.patchpilot.backend.task.domain.enums.FixTaskSort;

import java.time.Instant;

public record FixTaskListQuery(
        String query,
        FixTaskStatus status,
        String repositoryOwner,
        String repositoryName,
        String language,
        String buildSystem,
        Instant createdAfter,
        Instant createdBefore,
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
        this(query, status, repositoryOwner, repositoryName, null, null, null, null, limit, offset, FixTaskSort.CREATED_AT_DESC);
    }

    public FixTaskListQuery(
            String query,
            FixTaskStatus status,
            String repositoryOwner,
            String repositoryName,
            String language,
            String buildSystem,
            int limit,
            int offset
    ) {
        this(query, status, repositoryOwner, repositoryName, language, buildSystem, null, null, limit, offset, FixTaskSort.CREATED_AT_DESC);
    }

    public FixTaskListQuery(
            String query,
            FixTaskStatus status,
            String repositoryOwner,
            String repositoryName,
            Instant createdAfter,
            Instant createdBefore,
            int limit,
            int offset,
            FixTaskSort sort
    ) {
        this(query, status, repositoryOwner, repositoryName, null, null, createdAfter, createdBefore, limit, offset, sort);
    }

    public FixTaskListQuery(
            String query,
            FixTaskStatus status,
            String repositoryOwner,
            String repositoryName,
            int limit,
            int offset,
            FixTaskSort sort
    ) {
        this(query, status, repositoryOwner, repositoryName, null, null, null, null, limit, offset, sort);
    }

    public static FixTaskListQuery all() {
        return new FixTaskListQuery(null, null, null, null, null, null, null, null, Integer.MAX_VALUE, 0, FixTaskSort.CREATED_AT_DESC);
    }
}
