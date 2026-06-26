package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoReadinessSnapshotArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoReadinessSnapshotArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoReadinessSnapshotArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoReadinessSnapshotArchiveMapper;
import io.patchpilot.backend.demo.service.DemoReadinessSnapshotArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoReadinessSnapshotArchiveRepository implements DemoReadinessSnapshotArchiveRepository {

    private final DemoReadinessSnapshotArchiveMapper archiveMapper;

    @Override
    public DemoReadinessSnapshotArchiveVo save(DemoReadinessSnapshotArchiveVo archive) {
        archiveMapper.insert(DemoReadinessSnapshotArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoReadinessSnapshotArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoReadinessSnapshotArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoReadinessSnapshotArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoReadinessSnapshotArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoReadinessSnapshotArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoReadinessSnapshotArchiveConvert::toVo);
    }
}
