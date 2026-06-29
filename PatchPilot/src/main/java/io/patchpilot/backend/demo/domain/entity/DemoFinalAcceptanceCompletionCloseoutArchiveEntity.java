package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_acceptance_completion_closeout_archive")
public class DemoFinalAcceptanceCompletionCloseoutArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("closed")
    private Boolean closed;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("latest_share_package_archive_id")
    private String latestSharePackageArchiveId;

    @TableField("latest_completion_archive_id")
    private String latestCompletionArchiveId;

    @TableField("latest_completion_evidence_delivery_receipt_id")
    private String latestCompletionEvidenceDeliveryReceiptId;

    @TableField("latest_delivery_target")
    private String latestDeliveryTarget;

    @TableField("latest_delivery_channel")
    private String latestDeliveryChannel;

    @TableField("latest_delivered_at")
    private String latestDeliveredAt;

    @TableField("delivery_receipt_freshness")
    private String deliveryReceiptFreshness;

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
