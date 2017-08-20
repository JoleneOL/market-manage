package cn.lmjia.market.core;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.MainOrder_;
import cn.lmjia.market.core.entity.record.MainOrderRecord;
import cn.lmjia.market.core.entity.record.MainOrderRecord_;
import cn.lmjia.market.core.entity.record.ProductAmountRecord;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ActiveProfiles("mysql2")
public class UpgradeTest extends CoreServiceTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void go() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // 新旧数据应该保持同一规格

        // 原来存在good的商品
        CriteriaQuery<MainOrder> orderCq = cb.createQuery(MainOrder.class);
        Root<MainOrder> mainOrderRoot = orderCq.from(MainOrder.class);

        entityManager.createQuery(orderCq
                .select(mainOrderRoot)
                .where(cb.isNotNull(mainOrderRoot.get(MainOrder_.good)))
        )
                .getResultList().forEach(mainOrder -> {
            assertThat(mainOrder.getAmounts())
                    .as("新的amounts应该是有数据的")
                    .isNotNull()
                    .as("应该只有一种商品")
                    .hasSize(1)
            ;
            assertThat(mainOrder.getAmounts().get(mainOrder.getGood()))
                    .as("数量应该准确")
                    .isNotNull()
                    .isEqualTo(mainOrder.getAmount());
        });
        // 价格缓存
        orderCq = cb.createQuery(MainOrder.class);
        mainOrderRoot = orderCq.from(MainOrder.class);
        entityManager.createQuery(orderCq
                .select(mainOrderRoot)
                .where(cb.isNotNull(mainOrderRoot.get(MainOrder_.goodTotalPrice)))
        ).getResultList().forEach(mainOrder -> {
            assertThat(mainOrder.getGoodTotalPriceAmountIndependent())
                    .as("缓存的价格需要准确")
                    .isCloseTo(mainOrder.getGoodTotalPrice().multiply(BigDecimal.valueOf(mainOrder.getAmount()))
                            , Offset.offset(new BigDecimal("0.00000000001")));
        });
        // 价格缓存2
        orderCq = cb.createQuery(MainOrder.class);
        mainOrderRoot = orderCq.from(MainOrder.class);
        entityManager.createQuery(orderCq
                .select(mainOrderRoot)
                .where(cb.isNotNull(mainOrderRoot.get(MainOrder_.goodCommissioningPrice)))
        ).getResultList().forEach(mainOrder -> {
            assertThat(mainOrder.getGoodCommissioningPriceAmountIndependent())
                    .as("缓存的价格需要准确")
                    .isCloseTo(mainOrder.getGoodCommissioningPrice().multiply(BigDecimal.valueOf(mainOrder.getAmount()))
                            , Offset.offset(new BigDecimal("0.00000000001")));
        });

        // orderBody 都已设置
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        mainOrderRoot = orderCq.from(MainOrder.class);
        assertThat(entityManager.createQuery(countCq
                .select(cb.count(mainOrderRoot))
                .where(cb.isNull(mainOrderRoot.get(MainOrder_.orderBody)))
        ).getSingleResult())
                .as("数据都已被安全设置")
                .isEqualTo(0);
        // 记录设置
        CriteriaQuery<MainOrderRecord> recordCq = cb.createQuery(MainOrderRecord.class);
        Root<MainOrderRecord> mainOrderRecordRoot = orderCq.from(MainOrderRecord.class);
        entityManager.createQuery(recordCq
                .select(mainOrderRecordRoot)
                .where(cb.isNotNull(mainOrderRecordRoot.get(MainOrderRecord_.productName)))
        )
                .getResultList().forEach(mainOrderRecord -> {
            assertThat(mainOrderRecord.getAmountRecord())
                    .as("新的amounts应该是有数据的")
                    .isNotNull()
                    .as("应该只有一种商品")
                    .hasSize(1)
            ;
            ProductAmountRecord record = mainOrderRecord.getAmountRecord().get(0);
            assertThat(record.getProductName())
                    .isEqualTo(mainOrderRecord.getProductName());
            assertThat(record.getProductType())
                    .isEqualTo(mainOrderRecord.getProductType());
            assertThat(record.getAmount())
                    .isEqualTo(mainOrderRecord.getAmount());
        });
    }

}
