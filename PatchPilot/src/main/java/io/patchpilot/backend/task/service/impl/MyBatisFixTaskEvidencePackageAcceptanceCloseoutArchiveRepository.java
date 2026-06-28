package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.task.mapper.FixTaskEvidencePackageAcceptanceCloseoutArchiveMapper;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskEvidencePackageAcceptanceCloseoutArchiveRepository
        implements FixTaskEvidencePackageAcceptanceCloseoutArchiveRepository {

    private final FixTaskEvidencePackageAcceptanceCloseoutArchiveMapper archiveMapper;

    @Override
    public FixTaskEvidencePackageAcceptanceCloseoutArchiveVo save(
            FixTaskEvidencePackageAcceptanceCloseoutArchiveVo archive
    ) {
        archiveMapper.insert(FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageAcceptanceCloseoutArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(FixTaskEvidencePackageAcceptanceCloseoutArchiveConvert::toVo);
    }
}
