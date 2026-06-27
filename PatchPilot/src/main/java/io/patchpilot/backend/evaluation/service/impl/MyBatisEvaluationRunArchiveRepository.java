package io.patchpilot.backend.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.evaluation.convert.EvaluationRunArchiveConvert;
import io.patchpilot.backend.evaluation.domain.EvaluationRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunArchiveEntity;
import io.patchpilot.backend.evaluation.mapper.EvaluationRunArchiveMapper;
import io.patchpilot.backend.evaluation.service.EvaluationRunArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisEvaluationRunArchiveRepository implements EvaluationRunArchiveRepository {

    private final EvaluationRunArchiveMapper archiveMapper;

    @Override
    public EvaluationRunArchiveVo save(EvaluationRunArchiveVo archive) {
        archiveMapper.insert(EvaluationRunArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<EvaluationRunArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<EvaluationRunArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(EvaluationRunArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(EvaluationRunArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<EvaluationRunArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(EvaluationRunArchiveConvert::toVo);
    }
}
