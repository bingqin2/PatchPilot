package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskPatchReviewConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskPatchReviewEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskPatchReviewVo;
import io.patchpilot.backend.task.mapper.FixTaskPatchReviewMapper;
import io.patchpilot.backend.task.service.FixTaskPatchReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskPatchReviewService implements FixTaskPatchReviewService {

    private final FixTaskPatchReviewMapper patchReviewMapper;

    @Override
    public FixTaskPatchReviewVo recordPatchReview(
            String taskId,
            String decision,
            String reason,
            String confidence,
            String requiredFollowUp,
            List<String> editedFiles,
            Instant createdAt
    ) {
        FixTaskPatchReviewEntity entity = FixTaskPatchReviewConvert.newEntity(
                UUID.randomUUID().toString(),
                taskId,
                decision,
                reason,
                confidence,
                requiredFollowUp,
                editedFiles,
                createdAt
        );
        patchReviewMapper.insert(entity);
        return FixTaskPatchReviewConvert.toVo(entity);
    }

    @Override
    public Optional<FixTaskPatchReviewVo> findLatestPatchReview(String taskId) {
        LambdaQueryWrapper<FixTaskPatchReviewEntity> queryWrapper = new LambdaQueryWrapper<FixTaskPatchReviewEntity>()
                .eq(FixTaskPatchReviewEntity::getTaskId, taskId);
        return patchReviewMapper.selectList(queryWrapper).stream()
                .max(Comparator.comparing(FixTaskPatchReviewEntity::getCreatedAt))
                .map(FixTaskPatchReviewConvert::toVo);
    }
}
