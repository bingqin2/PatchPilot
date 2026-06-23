package io.patchpilot.backend.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.safety.domain.OperatorSafetyAuditEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperatorSafetyAuditMapper extends BaseMapper<OperatorSafetyAuditEntity> {
}
