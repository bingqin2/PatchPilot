package io.patchpilot.backend.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.task.domain.entity.FixTaskTestRunEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FixTaskTestRunMapper extends BaseMapper<FixTaskTestRunEntity> {
}
