package io.patchpilot.backend.github.webhook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.github.webhook.convert.WebhookDeliveryDiagnosticConvert;
import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticEntity;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticVo;
import io.patchpilot.backend.github.webhook.mapper.WebhookDeliveryDiagnosticMapper;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisWebhookDeliveryDiagnosticService implements WebhookDeliveryDiagnosticService {

    private final WebhookDeliveryDiagnosticMapper diagnosticMapper;

    @Override
    public WebhookDeliveryDiagnosticVo record(RecordWebhookDeliveryDiagnosticCommand command) {
        WebhookDeliveryDiagnosticEntity entity = WebhookDeliveryDiagnosticConvert.newEntity(
                UUID.randomUUID().toString(),
                command,
                Instant.now()
        );
        diagnosticMapper.insert(entity);
        return WebhookDeliveryDiagnosticConvert.toVo(entity);
    }

    @Override
    public List<WebhookDeliveryDiagnosticVo> listRecent(int limit) {
        LambdaQueryWrapper<WebhookDeliveryDiagnosticEntity> queryWrapper = new LambdaQueryWrapper<WebhookDeliveryDiagnosticEntity>()
                .orderByDesc(WebhookDeliveryDiagnosticEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return diagnosticMapper.selectList(queryWrapper).stream()
                .sorted(Comparator.comparing(WebhookDeliveryDiagnosticEntity::getCreatedAt).reversed())
                .map(WebhookDeliveryDiagnosticConvert::toVo)
                .toList();
    }
}
