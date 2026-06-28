package io.patchpilot.backend.task.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("fix_task_evidence_acceptance_closeout_archive")
public class FixTaskEvidencePackageAcceptanceCloseoutArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("accepted")
    private Boolean accepted;

    @TableField("summary")
    private String summary;

    @TableField("latest_archive_id")
    private String latestArchiveId;

    @TableField("latest_task_id")
    private String latestTaskId;

    @TableField("latest_pull_request_url")
    private String latestPullRequestUrl;

    @TableField("latest_delivery_receipt_id")
    private String latestDeliveryReceiptId;

    @TableField("latest_delivery_target")
    private String latestDeliveryTarget;

    @TableField("latest_delivery_channel")
    private String latestDeliveryChannel;

    @TableField("delivery_receipt_freshness")
    private String deliveryReceiptFreshness;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("report")
    private String report;
}
