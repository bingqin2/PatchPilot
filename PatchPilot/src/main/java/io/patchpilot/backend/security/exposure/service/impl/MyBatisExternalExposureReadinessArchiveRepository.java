package io.patchpilot.backend.security.exposure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.security.exposure.convert.ExternalExposureReadinessArchiveConvert;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureReadinessArchiveVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureReadinessArchiveEntity;
import io.patchpilot.backend.security.exposure.mapper.ExternalExposureReadinessArchiveMapper;
import io.patchpilot.backend.security.exposure.service.ExternalExposureReadinessArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisExternalExposureReadinessArchiveRepository implements ExternalExposureReadinessArchiveRepository {

    private final ExternalExposureReadinessArchiveMapper archiveMapper;

    @Override
    public ExternalExposureReadinessArchiveVo save(ExternalExposureReadinessArchiveVo archive) {
        archiveMapper.insert(ExternalExposureReadinessArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<ExternalExposureReadinessArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<ExternalExposureReadinessArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(ExternalExposureReadinessArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(ExternalExposureReadinessArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<ExternalExposureReadinessArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(ExternalExposureReadinessArchiveConvert::toVo);
    }
}
