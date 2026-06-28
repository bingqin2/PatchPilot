package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalAcceptanceSharePackageArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceSharePackageArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceSharePackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceSharePackageArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceSharePackageArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalAcceptanceSharePackageArchiveRepository
        implements DemoFinalAcceptanceSharePackageArchiveRepository {

    private final DemoFinalAcceptanceSharePackageArchiveMapper archiveMapper;

    @Override
    public DemoFinalAcceptanceSharePackageArchiveVo save(DemoFinalAcceptanceSharePackageArchiveVo archive) {
        archiveMapper.insert(DemoFinalAcceptanceSharePackageArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalAcceptanceSharePackageArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalAcceptanceSharePackageArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalAcceptanceSharePackageArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalAcceptanceSharePackageArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalAcceptanceSharePackageArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalAcceptanceSharePackageArchiveConvert::toVo);
    }
}
