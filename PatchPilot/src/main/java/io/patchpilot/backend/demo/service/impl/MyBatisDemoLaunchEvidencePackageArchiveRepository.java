package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoLaunchEvidencePackageArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidencePackageArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchEvidencePackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchEvidencePackageArchiveMapper;
import io.patchpilot.backend.demo.service.DemoLaunchEvidencePackageArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoLaunchEvidencePackageArchiveRepository implements DemoLaunchEvidencePackageArchiveRepository {

    private final DemoLaunchEvidencePackageArchiveMapper archiveMapper;

    @Override
    public DemoLaunchEvidencePackageArchiveVo save(DemoLaunchEvidencePackageArchiveVo archive) {
        archiveMapper.insert(DemoLaunchEvidencePackageArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoLaunchEvidencePackageArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoLaunchEvidencePackageArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoLaunchEvidencePackageArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoLaunchEvidencePackageArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoLaunchEvidencePackageArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoLaunchEvidencePackageArchiveConvert::toVo);
    }
}
