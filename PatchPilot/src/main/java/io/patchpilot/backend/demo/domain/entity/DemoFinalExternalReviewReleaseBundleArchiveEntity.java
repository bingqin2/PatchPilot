package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_external_review_release_bundle_archive")
public class DemoFinalExternalReviewReleaseBundleArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("release_ready")
    private Boolean releaseReady;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("latest_certificate_archive_id")
    private String latestCertificateArchiveId;

    @TableField("latest_delivery_finalization_archive_id")
    private String latestDeliveryFinalizationArchiveId;

    @TableField("latest_package_archive_id")
    private String latestPackageArchiveId;

    @TableField("latest_delivery_receipt_id")
    private String latestDeliveryReceiptId;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("latest_delivery_target")
    private String latestDeliveryTarget;

    @TableField("latest_delivery_channel")
    private String latestDeliveryChannel;

    @TableField("latest_delivered_at")
    private String latestDeliveredAt;

    @TableField("latest_certificate_archived_at")
    private Instant latestCertificateArchivedAt;

    @TableField("required_attachments_json")
    private String requiredAttachmentsJson;

    @TableField("release_checks_json")
    private String releaseChecksJson;

    @TableField("evidence_notes_json")
    private String evidenceNotesJson;

    @TableField("download_actions_json")
    private String downloadActionsJson;

    @TableField("side_effect_contract")
    private String sideEffectContract;

    @TableField("report")
    private String report;

    @TableField("generated_at")
    private Instant generatedAt;

    @TableField("archived_at")
    private Instant archivedAt;
}
