package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoFinalAcceptanceShareDeliveryReceiptConvert;
import io.patchpilot.backend.demo.domain.DemoFinalAcceptanceShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoFinalAcceptanceShareDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoFinalAcceptanceShareDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.DemoFinalAcceptanceShareDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoFinalAcceptanceShareDeliveryReceiptRepository
        implements DemoFinalAcceptanceShareDeliveryReceiptRepository {

    private final DemoFinalAcceptanceShareDeliveryReceiptMapper receiptMapper;

    @Override
    public DemoFinalAcceptanceShareDeliveryReceiptVo save(
            DemoFinalAcceptanceShareDeliveryReceiptVo receipt
    ) {
        receiptMapper.insert(DemoFinalAcceptanceShareDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<DemoFinalAcceptanceShareDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<DemoFinalAcceptanceShareDeliveryReceiptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoFinalAcceptanceShareDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(DemoFinalAcceptanceShareDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoFinalAcceptanceShareDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(DemoFinalAcceptanceShareDeliveryReceiptConvert::toVo);
    }
}
