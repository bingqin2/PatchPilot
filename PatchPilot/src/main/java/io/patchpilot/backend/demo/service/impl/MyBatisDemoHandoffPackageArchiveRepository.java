package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoHandoffPackageArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoHandoffPackageArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoHandoffPackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoHandoffPackageArchiveMapper;
import io.patchpilot.backend.demo.service.DemoHandoffPackageArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoHandoffPackageArchiveRepository implements DemoHandoffPackageArchiveRepository {

    private final DemoHandoffPackageArchiveMapper archiveMapper;

    @Override
    public DemoHandoffPackageArchiveVo save(DemoHandoffPackageArchiveVo archive) {
        archiveMapper.insert(DemoHandoffPackageArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoHandoffPackageArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoHandoffPackageArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoHandoffPackageArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoHandoffPackageArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoHandoffPackageArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoHandoffPackageArchiveConvert::toVo);
    }
}
