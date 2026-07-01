package io.patchpilot.backend.security.exposure.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("external_exposure_closeout_archive")
public class ExternalExposureCloseoutArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("closeout_ready")
    private Boolean closeoutReady;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("latest_session_id")
    private String latestSessionId;

    @TableField("latest_session_status")
    private String latestSessionStatus;

    @TableField("public_url")
    private String publicUrl;

    @TableField("webhook_url")
    private String webhookUrl;

    @TableField("purpose")
    private String purpose;

    @TableField("operator")
    private String operator;

    @TableField("started_at")
    private Instant startedAt;

    @TableField("closed_by")
    private String closedBy;

    @TableField("closed_at")
    private Instant closedAt;

    @TableField("close_notes")
    private String closeNotes;

    @TableField("linked_readiness_archive_id")
    private String linkedReadinessArchiveId;

    @TableField("handoff_status")
    private String handoffStatus;

    @TableField("archive_freshness")
    private String archiveFreshness;

    @TableField("ready_count")
    private Integer readyCount;

    @TableField("needs_attention_count")
    private Integer needsAttentionCount;

    @TableField("blocked_count")
    private Integer blockedCount;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("next_actions")
    private String nextActions;

    @TableField("evidence_notes")
    private String evidenceNotes;

    @TableField("download_actions")
    private String downloadActions;

    @TableField("side_effect_contract")
    private String sideEffectContract;

    @TableField("generated_at")
    private Instant generatedAt;

    @TableField("archived_at")
    private Instant archivedAt;

    @TableField("report")
    private String report;
}
