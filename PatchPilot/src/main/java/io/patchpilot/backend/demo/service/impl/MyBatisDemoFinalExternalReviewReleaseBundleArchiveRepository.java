package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalExternalReviewReleaseBundleArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalExternalReviewReleaseBundleArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalExternalReviewReleaseBundleArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalExternalReviewReleaseBundleArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalExternalReviewReleaseBundleArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalExternalReviewReleaseBundleArchiveRepository
        implements DemoFinalExternalReviewReleaseBundleArchiveRepository {

    private final DemoFinalExternalReviewReleaseBundleArchiveMapper archiveMapper;

    @Override
    public DemoFinalExternalReviewReleaseBundleArchiveVo save(
            DemoFinalExternalReviewReleaseBundleArchiveVo archive
    ) {
        archiveMapper.insert(DemoFinalExternalReviewReleaseBundleArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalExternalReviewReleaseBundleArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalExternalReviewReleaseBundleArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalExternalReviewReleaseBundleArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalExternalReviewReleaseBundleArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalExternalReviewReleaseBundleArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalExternalReviewReleaseBundleArchiveConvert::toVo);
    }
}
