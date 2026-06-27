package io.patchpilot.backend.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunArchiveEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EvaluationRunArchiveMapper extends BaseMapper<EvaluationRunArchiveEntity> {
}
