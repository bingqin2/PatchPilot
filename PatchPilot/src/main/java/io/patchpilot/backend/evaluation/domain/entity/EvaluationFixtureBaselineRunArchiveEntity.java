package io.patchpilot.backend.evaluation.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("evaluation_fixture_baseline_run_archive")
public class EvaluationFixtureBaselineRunArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("total_case_count")
    private int totalCaseCount;

    @TableField("executed_case_count")
    private int executedCaseCount;

    @TableField("passed_case_count")
    private int passedCaseCount;

    @TableField("failed_case_count")
    private int failedCaseCount;

    @TableField("skipped_case_count")
    private int skippedCaseCount;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("side_effect_contract")
    private String sideEffectContract;

    @TableField("next_action")
    private String nextAction;

    @TableField("report")
    private String report;
}
