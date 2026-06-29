package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalAcceptanceCompletionCloseoutArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceCompletionCloseoutArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceCompletionCloseoutArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceCompletionCloseoutArchiveMapper;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceCompletionCloseoutArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalAcceptanceCompletionCloseoutArchiveRepository
        implements DemoFinalAcceptanceCompletionCloseoutArchiveRepository {

    private final DemoFinalAcceptanceCompletionCloseoutArchiveMapper archiveMapper;

    @Override
    public DemoFinalAcceptanceCompletionCloseoutArchiveVo save(
            DemoFinalAcceptanceCompletionCloseoutArchiveVo archive
    ) {
        archiveMapper.insert(DemoFinalAcceptanceCompletionCloseoutArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoFinalAcceptanceCompletionCloseoutArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoFinalAcceptanceCompletionCloseoutArchiveEntity> queryWrapper =
                new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalAcceptanceCompletionCloseoutArchiveEntity::getArchivedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoFinalAcceptanceCompletionCloseoutArchiveConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalAcceptanceCompletionCloseoutArchiveVo> findById(String archiveId) {
        return Optional.ofNullable(archiveMapper.selectById(archiveId))
                .map(DemoFinalAcceptanceCompletionCloseoutArchiveConvert::toVo);
    }
}
