package io.patchpilot.backend.safety.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("operator_safety_audit")
public class OperatorSafetyAuditEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("action")
    private String action;

    @TableField("resource_type")
    private String resourceType;

    @TableField("resource_id")
    private String resourceId;

    @TableField("scope")
    private String scope;

    @TableField("scope_key")
    private String scopeKey;

    @TableField("operator")
    private String operator;

    @TableField("reason")
    private String reason;

    @TableField("created_at")
    private Instant createdAt;
}
