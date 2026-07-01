package io.patchpilot.backend.security.exposure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.security.exposure.convert.ExternalExposureCloseoutArchiveConvert;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureCloseoutArchiveVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureCloseoutArchiveEntity;
import io.patchpilot.backend.security.exposure.mapper.ExternalExposureCloseoutArchiveMapper;
import io.patchpilot.backend.security.exposure.service.ExternalExposureCloseoutArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisExternalExposureCloseoutArchiveRepository implements ExternalExposureCloseoutArchiveRepository {

    private final ExternalExposureCloseoutArchiveMapper archiveMapper;

    @Override
    public ExternalExposureCloseoutArchiveVo save(ExternalExposureCloseoutArchiveVo archive) {
        archiveMapper.insert(ExternalExposureCloseoutArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<ExternalExposureCloseoutArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<ExternalExposureCloseoutArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(ExternalExposureCloseoutArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(ExternalExposureCloseoutArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<ExternalExposureCloseoutArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(ExternalExposureCloseoutArchiveConvert::toVo);
    }
}
