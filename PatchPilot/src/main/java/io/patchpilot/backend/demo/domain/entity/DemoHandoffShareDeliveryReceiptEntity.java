package io.patchpilot.backend.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("demo_handoff_share_delivery_receipt")
public class DemoHandoffShareDeliveryReceiptEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("handoff_archive_id")
    private String handoffArchiveId;

    @TableField("session_id")
    private String sessionId;

    @TableField("delivery_channel")
    private String deliveryChannel;

    @TableField("delivery_target")
    private String deliveryTarget;

    @TableField("operator")
    private String operator;

    @TableField("notes")
    private String notes;

    @TableField("message_subject")
    private String messageSubject;

    @TableField("delivered_at")
    private Instant deliveredAt;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("markdown_report")
    private String markdownReport;
}
