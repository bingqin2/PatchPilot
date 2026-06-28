package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoLaunchAcceptanceCloseoutArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchAcceptanceCloseoutArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchAcceptanceCloseoutArchiveMapper;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCloseoutArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoLaunchAcceptanceCloseoutArchiveRepository implements DemoLaunchAcceptanceCloseoutArchiveRepository {

    private final DemoLaunchAcceptanceCloseoutArchiveMapper archiveMapper;

    @Override
    public DemoLaunchAcceptanceCloseoutArchiveVo save(DemoLaunchAcceptanceCloseoutArchiveVo archive) {
        archiveMapper.insert(DemoLaunchAcceptanceCloseoutArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoLaunchAcceptanceCloseoutArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoLaunchAcceptanceCloseoutArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoLaunchAcceptanceCloseoutArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoLaunchAcceptanceCloseoutArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoLaunchAcceptanceCloseoutArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoLaunchAcceptanceCloseoutArchiveConvert::toVo);
    }
}
