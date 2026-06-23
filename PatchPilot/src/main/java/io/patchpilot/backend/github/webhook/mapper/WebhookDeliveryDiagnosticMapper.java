package io.patchpilot.backend.github.webhook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WebhookDeliveryDiagnosticMapper extends BaseMapper<WebhookDeliveryDiagnosticEntity> {
}
