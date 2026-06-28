package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_handoff_report_package_archive")
public class DemoFinalHandoffReportPackageArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("download_ready")
    private Boolean downloadReady;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("latest_archive_id")
    private String latestArchiveId;

    @TableField("latest_session_id")
    private String latestSessionId;

    @TableField("latest_delivery_receipt_id")
    private String latestDeliveryReceiptId;

    @TableField("task_certificate_archive_id")
    private String taskCertificateArchiveId;

    @TableField("task_certificate_ready")
    private Boolean taskCertificateReady;

    @TableField("readiness_checks_json")
    private String readinessChecksJson;

    @TableField("required_attachments_json")
    private String requiredAttachmentsJson;

    @TableField("pre_send_checks_json")
    private String preSendChecksJson;

    @TableField("evidence_notes_json")
    private String evidenceNotesJson;

    @TableField("source_reports_json")
    private String sourceReportsJson;

    @TableField("report")
    private String report;

    @TableField("generated_at")
    private Instant generatedAt;

    @TableField("archived_at")
    private Instant archivedAt;
}
