package io.patchpilot.backend.safety.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("trigger_quarantine")
public class TriggerQuarantineEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("scope")
    private String scope;

    @TableField("scope_key")
    private String scopeKey;

    @TableField("reason")
    private String reason;

    @TableField("category")
    private String category;

    @TableField("evidence_count")
    private Integer evidenceCount;

    @TableField("window_ms")
    private Long windowMs;

    @TableField("started_at")
    private Instant startedAt;

    @TableField("expires_at")
    private Instant expiresAt;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("updated_at")
    private Instant updatedAt;
}
