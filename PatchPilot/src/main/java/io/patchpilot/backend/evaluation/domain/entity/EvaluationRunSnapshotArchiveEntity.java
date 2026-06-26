package io.patchpilot.backend.evaluation.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("evaluation_run_snapshot_archive")
public class EvaluationRunSnapshotArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("preview_run_id")
    private String previewRunId;

    @TableField("title")
    private String title;

    @TableField("status")
    private String status;

    @TableField("case_count")
    private int caseCount;

    @TableField("supported_fix_case_count")
    private int supportedFixCaseCount;

    @TableField("safety_rejection_case_count")
    private int safetyRejectionCaseCount;

    @TableField("covered_languages")
    private String coveredLanguages;

    @TableField("covered_build_systems")
    private String coveredBuildSystems;

    @TableField("expected_verification_commands")
    private String expectedVerificationCommands;

    @TableField("safety_rejection_categories")
    private String safetyRejectionCategories;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("side_effect_contract")
    private String sideEffectContract;

    @TableField("report")
    private String report;
}
