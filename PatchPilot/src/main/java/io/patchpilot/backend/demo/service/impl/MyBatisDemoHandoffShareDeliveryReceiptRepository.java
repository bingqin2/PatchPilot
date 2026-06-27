package io.patchpilot.backend.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.patchpilot.backend.demo.convert.DemoHandoffShareDeliveryReceiptConvert;
import io.patchpilot.backend.demo.domain.DemoHandoffShareDeliveryReceiptVo;
import io.patchpilot.backend.demo.domain.entity.DemoHandoffShareDeliveryReceiptEntity;
import io.patchpilot.backend.demo.mapper.DemoHandoffShareDeliveryReceiptMapper;
import io.patchpilot.backend.demo.service.DemoHandoffShareDeliveryReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile({"local", "docker", "idea"})
@RequiredArgsConstructor
public class MyBatisDemoHandoffShareDeliveryReceiptRepository implements DemoHandoffShareDeliveryReceiptRepository {

    private final DemoHandoffShareDeliveryReceiptMapper receiptMapper;

    @Override
    public DemoHandoffShareDeliveryReceiptVo save(DemoHandoffShareDeliveryReceiptVo receipt) {
        receiptMapper.insert(DemoHandoffShareDeliveryReceiptConvert.toEntity(receipt));
        return receipt;
    }

    @Override
    public List<DemoHandoffShareDeliveryReceiptVo> listRecentReceipts(int limit) {
        LambdaQueryWrapper<DemoHandoffShareDeliveryReceiptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByDesc(DemoHandoffShareDeliveryReceiptEntity::getCreatedAt)
                .last("LIMIT " + limit);
        return receiptMapper.selectList(queryWrapper).stream()
                .map(DemoHandoffShareDeliveryReceiptConvert::toVo)
                .toList();
    }

    @Override
    public Optional<DemoHandoffShareDeliveryReceiptVo> findById(String receiptId) {
        return Optional.ofNullable(receiptMapper.selectById(receiptId))
                .map(DemoHandoffShareDeliveryReceiptConvert::toVo);
    }
}
