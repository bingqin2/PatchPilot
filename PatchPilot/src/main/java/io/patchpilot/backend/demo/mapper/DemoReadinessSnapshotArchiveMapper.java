package io.patchpilot.backend.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.demo.domain.entity.DemoReadinessSnapshotArchiveEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoReadinessSnapshotArchiveMapper extends BaseMapper<DemoReadinessSnapshotArchiveEntity> {
}
