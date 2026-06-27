package io.patchpilot.backend.evaluation.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("evaluation_run_archive")
public class EvaluationRunArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("total_case_count")
    private int totalCaseCount;

    @TableField("supported_fix_case_count")
    private int supportedFixCaseCount;

    @TableField("safety_rejection_case_count")
    private int safetyRejectionCaseCount;

    @TableField("executed_fix_case_count")
    private int executedFixCaseCount;

    @TableField("passed_fix_case_count")
    private int passedFixCaseCount;

    @TableField("failed_fix_case_count")
    private int failedFixCaseCount;

    @TableField("skipped_case_count")
    private int skippedCaseCount;

    @TableField("covered_languages")
    private String coveredLanguages;

    @TableField("covered_build_systems")
    private String coveredBuildSystems;

    @TableField("safety_rejection_categories")
    private String safetyRejectionCategories;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("side_effect_contract")
    private String sideEffectContract;

    @TableField("next_action")
    private String nextAction;

    @TableField("report")
    private String report;
}
