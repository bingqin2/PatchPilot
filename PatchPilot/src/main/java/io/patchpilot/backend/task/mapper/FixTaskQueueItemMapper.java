package io.patchpilot.backend.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.task.domain.entity.FixTaskQueueItemEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FixTaskQueueItemMapper extends BaseMapper<FixTaskQueueItemEntity> {
}
