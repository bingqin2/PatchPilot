package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoLaunchAcceptanceCertificateArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoLaunchAcceptanceCertificateArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchAcceptanceCertificateArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchAcceptanceCertificateArchiveMapper;
import io.patchpilot.backend.demo.service.DemoLaunchAcceptanceCertificateArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoLaunchAcceptanceCertificateArchiveRepository implements DemoLaunchAcceptanceCertificateArchiveRepository {

    private final DemoLaunchAcceptanceCertificateArchiveMapper archiveMapper;

    @Override
    public DemoLaunchAcceptanceCertificateArchiveVo save(DemoLaunchAcceptanceCertificateArchiveVo archive) {
        archiveMapper.insert(DemoLaunchAcceptanceCertificateArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoLaunchAcceptanceCertificateArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoLaunchAcceptanceCertificateArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoLaunchAcceptanceCertificateArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoLaunchAcceptanceCertificateArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoLaunchAcceptanceCertificateArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoLaunchAcceptanceCertificateArchiveConvert::toVo);
    }
}
