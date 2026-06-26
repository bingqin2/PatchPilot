package io.patchpilot.backend.evaluation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.evaluation.convert.EvaluationFixtureBaselineRunArchiveConvert;
import io.patchpilot.backend.evaluation.domain.EvaluationFixtureBaselineRunArchiveVo;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationFixtureBaselineRunArchiveEntity;
import io.patchpilot.backend.evaluation.mapper.EvaluationFixtureBaselineRunArchiveMapper;
import io.patchpilot.backend.evaluation.service.EvaluationFixtureBaselineRunArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisEvaluationFixtureBaselineRunArchiveRepository implements EvaluationFixtureBaselineRunArchiveRepository {

    private final EvaluationFixtureBaselineRunArchiveMapper archiveMapper;

    @Override
    public EvaluationFixtureBaselineRunArchiveVo save(EvaluationFixtureBaselineRunArchiveVo archive) {
        archiveMapper.insert(EvaluationFixtureBaselineRunArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<EvaluationFixtureBaselineRunArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<EvaluationFixtureBaselineRunArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(EvaluationFixtureBaselineRunArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(EvaluationFixtureBaselineRunArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<EvaluationFixtureBaselineRunArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(EvaluationFixtureBaselineRunArchiveConvert::toVo);
    }
}
