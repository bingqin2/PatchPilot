package io.patchpilot.backend.security.exposure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.security.exposure.convert.ExternalExposureSessionConvert;
import io.patchpilot.backend.security.exposure.domain.ExternalExposureSessionVo;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureSessionEntity;
import io.patchpilot.backend.security.exposure.mapper.ExternalExposureSessionMapper;
import io.patchpilot.backend.security.exposure.service.ExternalExposureSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisExternalExposureSessionRepository implements ExternalExposureSessionRepository {

    private final ExternalExposureSessionMapper sessionMapper;

    @Override
    public ExternalExposureSessionVo save(ExternalExposureSessionVo session) {
        sessionMapper.insertOrUpdate(ExternalExposureSessionConvert.toEntity(session));
        return session;
    }

    @Override
    public List<ExternalExposureSessionVo> listRecentSessions(int limit) {
        LambdaQueryWrapper<ExternalExposureSessionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(ExternalExposureSessionEntity::getStartedAt)
                .last("LIMIT " + limit);
        return sessionMapper.selectList(queryWrapper).stream()
                .map(ExternalExposureSessionConvert::toVo)
                .toList();
    }

    @Override
    public Optional<ExternalExposureSessionVo> findById(String sessionId) {
        return Optional.ofNullable(sessionMapper.selectById(sessionId))
                .map(ExternalExposureSessionConvert::toVo);
    }
}
