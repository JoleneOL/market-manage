package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.PromotionRequestStatus;
import cn.lmjia.market.core.repository.request.PromotionRequestRepository;
import cn.lmjia.market.core.service.request.PromotionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service
public class PromotionRequestServiceImpl implements PromotionRequestService {

    @Autowired
    private PromotionRequestRepository promotionRequestRepository;

    @Override
    public PromotionRequest currentRequest(Login login) {
        return promotionRequestRepository.findByWhoseAndRequestStatusOrderByIdDesc(login, PromotionRequestStatus.requested)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
