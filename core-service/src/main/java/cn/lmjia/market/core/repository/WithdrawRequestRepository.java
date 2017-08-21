package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long> {

}
