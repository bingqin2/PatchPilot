package io.patchpilot.backend.task.service;

import io.patchpilot.backend.task.domain.vo.FixTaskVo;

@FunctionalInterface
public interface FixTaskAdapterMetadataRecorder {

    FixTaskAdapterMetadataRecorder NOOP = (id, language, buildSystem, verificationCommand, adapterDetectionReason) -> null;

    default FixTaskVo recordAdapterMetadata(
            String id,
            String language,
            String buildSystem,
            String verificationCommand
    ) {
        return recordAdapterMetadata(id, language, buildSystem, verificationCommand, null);
    }

    FixTaskVo recordAdapterMetadata(
            String id,
            String language,
            String buildSystem,
            String verificationCommand,
            String adapterDetectionReason
    );
}
