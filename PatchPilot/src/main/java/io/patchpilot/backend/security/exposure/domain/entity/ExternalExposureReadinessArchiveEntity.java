package io.patchpilot.backend.security.exposure.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("external_exposure_readiness_archive")
public class ExternalExposureReadinessArchiveEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("status")
    private String status;

    @TableField("safe_to_expose")
    private Boolean safeToExpose;

    @TableField("summary")
    private String summary;

    @TableField("ready_count")
    private Integer readyCount;

    @TableField("needs_attention_count")
    private Integer needsAttentionCount;

    @TableField("blocked_count")
    private Integer blockedCount;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("created_at")
    private Instant createdAt;

    @TableField("report")
    private String report;
}
