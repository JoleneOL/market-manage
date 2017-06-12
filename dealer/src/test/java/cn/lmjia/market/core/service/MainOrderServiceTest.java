package cn.lmjia.market.core.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.dealer.service.CommissionRateService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class MainOrderServiceTest extends DealerServiceTest {

    private static final Log log = LogFactory.getLog(MainOrderServiceTest.class);
    @Autowired
    private CommissionRateService commissionRateService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private QuickTradeService quickTradeService;

    @Test
    public void newOrder() throws Exception {
        Login login1 = randomLogin(false);
        Login login2 = randomLogin(false);
        newRandomOrderFor(login1, login2);
//        newRandomOrderFor(login1, login2);
//        newRandomOrderFor(login1, login2);
    }

}