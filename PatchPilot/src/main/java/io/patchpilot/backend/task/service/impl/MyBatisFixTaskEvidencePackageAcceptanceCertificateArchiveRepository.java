package io.patchpilot.backend.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.task.convert.FixTaskEvidencePackageAcceptanceCertificateArchiveConvert;
import io.patchpilot.backend.task.domain.entity.FixTaskEvidencePackageAcceptanceCertificateArchiveEntity;
import io.patchpilot.backend.task.domain.vo.FixTaskEvidencePackageAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.task.mapper.FixTaskEvidencePackageAcceptanceCertificateArchiveMapper;
import io.patchpilot.backend.task.service.FixTaskEvidencePackageAcceptanceCertificateArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisFixTaskEvidencePackageAcceptanceCertificateArchiveRepository
        implements FixTaskEvidencePackageAcceptanceCertificateArchiveRepository {

    private final FixTaskEvidencePackageAcceptanceCertificateArchiveMapper archiveMapper;

    @Override
    public FixTaskEvidencePackageAcceptanceCertificateArchiveVo save(
            FixTaskEvidencePackageAcceptanceCertificateArchiveVo archive
    ) {
        archiveMapper.insert(FixTaskEvidencePackageAcceptanceCertificateArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<FixTaskEvidencePackageAcceptanceCertificateArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(FixTaskEvidencePackageAcceptanceCertificateArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(FixTaskEvidencePackageAcceptanceCertificateArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<FixTaskEvidencePackageAcceptanceCertificateArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(FixTaskEvidencePackageAcceptanceCertificateArchiveConvert::toVo);
    }
}
