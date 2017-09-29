package cn.lmjia.market.core.repository;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.withdraw.WithdrawRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WithdrawRequestRepository extends JpaRepository<WithdrawRequest, Long>
        , JpaSpecificationExecutor<WithdrawRequest> {

}
