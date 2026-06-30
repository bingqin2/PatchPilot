package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository
        implements DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveRepository {

    private final DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveMapper archiveMapper;

    @Override
    public DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo save(
            DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo archive
    ) {
        archiveMapper.insert(DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveConvert::toVo);
    }
}
