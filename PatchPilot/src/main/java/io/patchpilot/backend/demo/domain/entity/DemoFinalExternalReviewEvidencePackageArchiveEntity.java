package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_external_review_evidence_package_archive")
public class DemoFinalExternalReviewEvidencePackageArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("ready_for_external_review")
    private Boolean readyForExternalReview;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("final_acceptance_share_package_archive_id")
    private String finalAcceptanceSharePackageArchiveId;

    @TableField("completion_archive_id")
    private String completionArchiveId;

    @TableField("completion_evidence_delivery_receipt_id")
    private String completionEvidenceDeliveryReceiptId;

    @TableField("closeout_archive_id")
    private String closeoutArchiveId;

    @TableField("delivery_target")
    private String deliveryTarget;

    @TableField("delivery_channel")
    private String deliveryChannel;

    @TableField("delivered_at")
    private String deliveredAt;

    @TableField("delivery_receipt_freshness")
    private String deliveryReceiptFreshness;

    @TableField("closeout_archived_at")
    private Instant closeoutArchivedAt;

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
