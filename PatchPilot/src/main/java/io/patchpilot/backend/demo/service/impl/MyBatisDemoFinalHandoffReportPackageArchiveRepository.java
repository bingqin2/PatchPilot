package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalHandoffReportPackageArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalHandoffReportPackageArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalHandoffReportPackageArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalHandoffReportPackageArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalHandoffReportPackageArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalHandoffReportPackageArchiveRepository
        implements DemoFinalHandoffReportPackageArchiveRepository {

    private final DemoFinalHandoffReportPackageArchiveMapper archiveMapper;

    @Override
    public DemoFinalHandoffReportPackageArchiveVo save(DemoFinalHandoffReportPackageArchiveVo archive) {
        archiveMapper.insert(DemoFinalHandoffReportPackageArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalHandoffReportPackageArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalHandoffReportPackageArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalHandoffReportPackageArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalHandoffReportPackageArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalHandoffReportPackageArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalHandoffReportPackageArchiveConvert::toVo);
    }
}
