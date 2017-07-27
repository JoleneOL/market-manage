package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.withdraw.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WechatWithdrawRepository extends JpaRepository<Withdraw, Long> {
}
