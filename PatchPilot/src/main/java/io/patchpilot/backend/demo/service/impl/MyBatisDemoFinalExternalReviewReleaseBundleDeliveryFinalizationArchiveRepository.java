package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository
        implements DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveRepository {

    private final DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveMapper archiveMapper;

    @Override
    public DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo save(
            DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo archive
    ) {
        archiveMapper.insert(DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalExternalReviewReleaseBundleDeliveryFinalizationArchiveConvert::toVo);
    }
}
