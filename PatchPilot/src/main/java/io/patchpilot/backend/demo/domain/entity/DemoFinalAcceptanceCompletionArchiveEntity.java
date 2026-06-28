package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_final_acceptance_completion_archive")
public class DemoFinalAcceptanceCompletionArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("finalized")
    private Boolean finalized;

    @TableField("summary")
    private String summary;

    @TableField("next_action")
    private String nextAction;

    @TableField("latest_archive_id")
    private String latestArchiveId;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_delivery_receipt_id")
    private String latestDeliveryReceiptId;

    @TableField("latest_delivery_target")
    private String latestDeliveryTarget;

    @TableField("latest_delivery_channel")
    private String latestDeliveryChannel;

    @TableField("latest_delivered_at")
    private String latestDeliveredAt;

    @TableField("delivery_receipt_freshness")
    private String deliveryReceiptFreshness;

    @TableField("delivery_receipt_fresh")
    private Boolean deliveryReceiptFresh;

    @TableField("delivery_receipt_freshness_summary")
    private String deliveryReceiptFreshnessSummary;

    @TableField("evidence_notes_json")
    private String evidenceNotesJson;

    @TableField("report")
    private String report;

    @TableField("generated_at")
    private Instant generatedAt;

    @TableField("archived_at")
    private Instant archivedAt;
}
