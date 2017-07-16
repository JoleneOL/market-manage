package cn.lmjia.market.dealer.repository;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.support.PromotionRequestStatus;
import cn.lmjia.market.dealer.entity.PromotionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface PromotionRequestRepository extends JpaRepository<PromotionRequest, Long>
        , JpaSpecificationExecutor<PromotionRequest> {

    List<PromotionRequest> findByWhoseAndRequestStatusOrderByIdDesc(Login login, PromotionRequestStatus status);

}
