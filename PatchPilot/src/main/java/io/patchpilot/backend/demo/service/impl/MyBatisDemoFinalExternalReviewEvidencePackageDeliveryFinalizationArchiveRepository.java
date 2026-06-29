package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository
        implements DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveRepository {

    private final DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveMapper archiveMapper;

    @Override
    public DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo save(
            DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo archive
    ) {
        archiveMapper.insert(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalExternalReviewEvidencePackageDeliveryFinalizationArchiveConvert::toVo);
    }
}
