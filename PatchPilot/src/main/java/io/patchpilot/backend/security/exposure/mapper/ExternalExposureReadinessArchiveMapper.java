package io.patchpilot.backend.security.exposure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.security.exposure.domain.entity.ExternalExposureReadinessArchiveEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExternalExposureReadinessArchiveMapper extends BaseMapper<ExternalExposureReadinessArchiveEntity> {
}
