package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoSelfHostedLaunchReadinessArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoSelfHostedLaunchReadinessArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoSelfHostedLaunchReadinessArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoSelfHostedLaunchReadinessArchiveMapper;
import io.patchpilot.backend.demo.service.DemoSelfHostedLaunchReadinessArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoSelfHostedLaunchReadinessArchiveRepository implements DemoSelfHostedLaunchReadinessArchiveRepository {

    private final DemoSelfHostedLaunchReadinessArchiveMapper archiveMapper;

    @Override
    public DemoSelfHostedLaunchReadinessArchiveVo save(DemoSelfHostedLaunchReadinessArchiveVo archive) {
        archiveMapper.insert(DemoSelfHostedLaunchReadinessArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoSelfHostedLaunchReadinessArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoSelfHostedLaunchReadinessArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoSelfHostedLaunchReadinessArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoSelfHostedLaunchReadinessArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoSelfHostedLaunchReadinessArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoSelfHostedLaunchReadinessArchiveConvert::toVo);
    }
}
