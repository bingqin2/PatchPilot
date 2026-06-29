package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewDeliveryCertificateArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewDeliveryCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewDeliveryCertificateArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewDeliveryCertificateArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewDeliveryCertificateArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewDeliveryCertificateArchiveRepository
        implements DemoFinalExternalReviewDeliveryCertificateArchiveRepository {

    private final DemoFinalExternalReviewDeliveryCertificateArchiveMapper archiveMapper;

    @Override
    public DemoFinalExternalReviewDeliveryCertificateArchiveVo save(
            DemoFinalExternalReviewDeliveryCertificateArchiveVo archive
    ) {
        archiveMapper.insert(DemoFinalExternalReviewDeliveryCertificateArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalExternalReviewDeliveryCertificateArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewDeliveryCertificateArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewDeliveryCertificateArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewDeliveryCertificateArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewDeliveryCertificateArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalExternalReviewDeliveryCertificateArchiveConvert::toVo);
    }
}
