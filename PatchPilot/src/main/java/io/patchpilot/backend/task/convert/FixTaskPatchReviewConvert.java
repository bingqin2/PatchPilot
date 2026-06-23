package io.patchpilot.backend.task.convert;

import io.patchpilot.backend.task.domain.entity.FixTaskPatchReviewEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public final class FixTaskPatchReviewConvert {

    private FixTaskPatchReviewConvert() {
    }

    public static FixTaskPatchReviewEntity newEntity(
            String id,
            String taskId,
            String decision,
            String reason,
            String confidence,
            String requiredFollowUp,
            List<String> editedFiles,
            Instant createdAt
    ) {
        FixTaskPatchReviewEntity entity = new FixTaskPatchReviewEntity();
        entity.setId(id);
        entity.setTaskId(taskId);
        entity.setDecision(decision);
        entity.setReason(reason);
        entity.setConfidence(confidence);
        entity.setRequiredFollowUp(requiredFollowUp);
        entity.setEditedFiles(toStoredFiles(editedFiles));
        entity.setCreatedAt(createdAt);
        return entity;
    }

    public static FixTaskPatchReviewVo toVo(FixTaskPatchReviewEntity entity) {
        return new FixTaskPatchReviewVo(
                entity.getId(),
                entity.getTaskId(),
                entity.getDecision(),
                entity.getReason(),
                entity.getConfidence(),
                entity.getRequiredFollowUp(),
                fromStoredFiles(entity.getEditedFiles()),
                entity.getCreatedAt()
        );
    }

    private static String toStoredFiles(List<String> editedFiles) {
        return String.join("\n", editedFiles);
    }

    private static List<String> fromStoredFiles(String editedFiles) {
        if (editedFiles == null || editedFiles.isBlank()) {
            return List.of();
        }
        return Arrays.stream(editedFiles.split("\\R"))
                .filter(file -> !file.isBlank())
                .toList();
    }
}
