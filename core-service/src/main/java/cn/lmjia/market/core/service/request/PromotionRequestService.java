package cn.lmjia.market.core.service.request;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 升级申请服务
 *
 * @author CJ
 */
public interface PromotionRequestService {

    /**
     * @param login
     * @return 当前申请；或者null
     */
    @Transactional(readOnly = true)
    PromotionRequest currentRequest(Login login);

}
