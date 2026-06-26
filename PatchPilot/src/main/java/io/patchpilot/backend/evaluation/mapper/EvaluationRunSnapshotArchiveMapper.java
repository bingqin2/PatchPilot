package io.patchpilot.backend.evaluation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.evaluation.domain.entity.EvaluationRunSnapshotArchiveEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EvaluationRunSnapshotArchiveMapper extends BaseMapper<EvaluationRunSnapshotArchiveEntity> {
}
