package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoSessionArchiveConvert;
import io.patchpilot.backend.demo.domain.DemoSessionArchiveVo;
import io.patchpilot.backend.demo.domain.entity.DemoSessionArchiveEntity;
import io.patchpilot.backend.demo.mapper.DemoSessionArchiveMapper;
import io.patchpilot.backend.demo.service.DemoSessionArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoSessionArchiveRepository implements DemoSessionArchiveRepository {

    private final DemoSessionArchiveMapper archiveMapper;

    @Override
    public DemoSessionArchiveVo save(DemoSessionArchiveVo archive) {
        archiveMapper.insert(DemoSessionArchiveConvert.toEntity(archive));
        return archive;
    }

    @Override
    public List<DemoSessionArchiveVo> listRecentArchives(int limit) {
        LambdaQueryWrapper<DemoSessionArchiveEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoSessionArchiveEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return archiveMapper.selectList(queryWrapper).stream()
                .map(DemoSessionArchiveConvert::toVo)
                .toList();
    }
}
