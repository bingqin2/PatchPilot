package io.patchpilot.backend.security.exposure.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("external_exposure_session")
public class ExternalExposureSessionEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("public_url")
    private String publicUrl;

    @TableField("webhook_url")
    private String webhookUrl;

    @TableField("purpose")
    private String purpose;

    @TableField("operator")
    private String operator;

    @TableField("expected_shutdown_at")
    private Instant expectedShutdownAt;

    @TableField("notes")
    private String notes;

    @TableField("linked_handoff_status")
    private String linkedHandoffStatus;

    @TableField("linked_readiness_archive_id")
    private String linkedReadinessArchiveId;

    @TableField("started_at")
    private Instant startedAt;

    @TableField("closed_by")
    private String closedBy;

    @TableField("closed_at")
    private Instant closedAt;

    @TableField("close_notes")
    private String closeNotes;

    @TableField("report")
    private String report;
}
