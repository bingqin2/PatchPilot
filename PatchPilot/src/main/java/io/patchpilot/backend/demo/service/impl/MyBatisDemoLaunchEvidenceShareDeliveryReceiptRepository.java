package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoLaunchEvidenceShareDeliveryReceiptConvert;
import io.patchpilot.backend.demo.domain.DemoLaunchEvidenceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoLaunchEvidenceShareDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoLaunchEvidenceShareDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.DemoLaunchEvidenceShareDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoLaunchEvidenceShareDeliveryReceiptRepository
        implements DemoLaunchEvidenceShareDeliveryReceiptRepository {

    private final DemoLaunchEvidenceShareDeliveryReceiptMapper receiptMapper;

    @Override
    public DemoLaunchEvidenceShareDeliveryReceiptVo save(DemoLaunchEvidenceShareDeliveryReceiptVo receipt) {
        receiptMapper.insert(DemoLaunchEvidenceShareDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<DemoLaunchEvidenceShareDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<DemoLaunchEvidenceShareDeliveryReceiptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoLaunchEvidenceShareDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(DemoLaunchEvidenceShareDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoLaunchEvidenceShareDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(DemoLaunchEvidenceShareDeliveryReceiptConvert::toVo);
    }
}
