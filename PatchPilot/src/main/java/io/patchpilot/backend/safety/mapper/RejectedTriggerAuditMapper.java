package io.patchpilot.backend.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RejectedTriggerAuditMapper extends BaseMapper<RejectedTriggerAuditEntity> {
}
