package io.patchpilot.backend.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.evaluation.convert.EvaluationRunSnapshotArchiveConvert;
import io.patchpilot.backend.evaluation.domain.EvaluationRunSnapshotArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunSnapshotArchiveEntity;
import io.patchpilot.backend.evaluation.mapper.EvaluationRunSnapshotArchiveMapper;
import io.patchpilot.backend.evaluation.service.EvaluationRunSnapshotArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisEvaluationRunSnapshotArchiveRepository implements EvaluationRunSnapshotArchiveRepository {

    private final EvaluationRunSnapshotArchiveMapper archiveMapper;

    @Override
    public EvaluationRunSnapshotArchiveVo save(EvaluationRunSnapshotArchiveVo archive) {
        archiveMapper.insert(EvaluationRunSnapshotArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<EvaluationRunSnapshotArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<EvaluationRunSnapshotArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(EvaluationRunSnapshotArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(EvaluationRunSnapshotArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<EvaluationRunSnapshotArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(EvaluationRunSnapshotArchiveConvert::toVo);
    }
}
