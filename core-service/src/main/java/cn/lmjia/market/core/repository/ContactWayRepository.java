package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.ContactWay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface ContactWayRepository extends JpaRepository<ContactWay, Long>, JpaSpecificationExecutor<ContactWay> {
    /**
     * 根据电话号获取联系人.
     * @param mobile 手机号
     * @return 联系人
     */
    ContactWay findByMobile(String mobile);
}
