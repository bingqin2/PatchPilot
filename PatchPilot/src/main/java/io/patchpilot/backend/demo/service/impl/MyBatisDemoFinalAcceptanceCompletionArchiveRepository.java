package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalAcceptanceCompletionArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceCompletionArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalAcceptanceCompletionArchiveRepository
        implements DemoFinalAcceptanceCompletionArchiveRepository {

    private final DemoFinalAcceptanceCompletionArchiveMapper archiveMapper;

    @Override
    public DemoFinalAcceptanceCompletionArchiveVo save(DemoFinalAcceptanceCompletionArchiveVo archive) {
        archiveMapper.insert(DemoFinalAcceptanceCompletionArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalAcceptanceCompletionArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalAcceptanceCompletionArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalAcceptanceCompletionArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalAcceptanceCompletionArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalAcceptanceCompletionArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalAcceptanceCompletionArchiveConvert::toVo);
    }
}
