package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskEvidencePackageArchiveConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageArchiveVo;
import io.patchpilot.backend.task.mapper.FixTaskEvidencePackageArchiveMapper;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskEvidencePackageArchiveRepository implements FixTaskEvidencePackageArchiveRepository {

    private final FixTaskEvidencePackageArchiveMapper archiveMapper;

    @Override
    public FixTaskEvidencePackageArchiveVo save(FixTaskEvidencePackageArchiveVo archive) {
        archiveMapper.insert(FixTaskEvidencePackageArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<FixTaskEvidencePackageArchiveVo> listByTaskId(String taskId, int limit) {
        LambdaQueryWrapper<FixTaskEvidencePackageArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(FixTaskEvidencePackageArchiveEntity::getTaskId, taskId)
                .orderByDesc(FixTaskEvidencePackageArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(FixTaskEvidencePackageArchiveConvert::toVo)
                .toList();
    }

    @Override
    public List<FixTaskEvidencePackageArchiveVo> listRecent(int limit) {
        LambdaQueryWrapper<FixTaskEvidencePackageArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(FixTaskEvidencePackageArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(FixTaskEvidencePackageArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(FixTaskEvidencePackageArchiveConvert::toVo);
    }
}
