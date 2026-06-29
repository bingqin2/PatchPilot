package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewEvidencePackageArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewEvidencePackageArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewEvidencePackageArchiveRepository
        implements DemoFinalExternalReviewEvidencePackageArchiveRepository {

    private final DemoFinalExternalReviewEvidencePackageArchiveMapper archiveMapper;

    @Override
    public DemoFinalExternalReviewEvidencePackageArchiveVo save(
            DemoFinalExternalReviewEvidencePackageArchiveVo archive
    ) {
        archiveMapper.insert(DemoFinalExternalReviewEvidencePackageArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalExternalReviewEvidencePackageArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewEvidencePackageArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewEvidencePackageArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewEvidencePackageArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewEvidencePackageArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalExternalReviewEvidencePackageArchiveConvert::toVo);
    }
}
