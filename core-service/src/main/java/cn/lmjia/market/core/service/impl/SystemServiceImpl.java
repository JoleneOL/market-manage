package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.SystemService;
import me.jiangcai.lib.sys.service.SystemStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author CJ
 */
@Service("systemService")
public class SystemServiceImpl implements SystemService {

    @Autowired
    private Environment environment;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private SystemStringService systemStringService;

    @Override
    public String toUrl(String uri) {
        return environment.getProperty("market.url", "http://localhost") + uri;
    }

    @Override
    public String getCompanyCustomerServiceTel() {
        return systemStringService.getCustomSystemString("withdraw.invoice.companyTelephone"
                , null, true, String.class, "0571-88187913");
    }

    @Override
    public boolean allowWithdrawDisplay(Login login) {
        return login != null
                && (systemStringService.getCustomSystemString("market.withdraw.allowAll", null
                , true, Boolean.class, true)
                || agentLevelRepository.findByLogin(login).size() != 0);
    }

    @Override
    public void updateNonAgentAbleToGainCommission(boolean value) {
        systemStringService.updateSystemString("market.NonAgentAbleToGainCommission", value);
    }

    @Override
    public boolean isNonAgentAbleToGainCommission() {
        return systemStringService.getCustomSystemString("market.NonAgentAbleToGainCommission"
                , "market.NonAgentAbleToGainCommission.comment", true, Boolean.class, true);
    }

    @Override
    public void updateRegularLoginAsAnyOrder(boolean value) {
        systemStringService.updateSystemString("market.RegularLoginAsAnyOrder", value);
    }

    @Override
    public boolean isRegularLoginAsAnyOrder() {
        return systemStringService.getCustomSystemString("market.RegularLoginAsAnyOrder"
                , "market.RegularLoginAsAnyOrder.comment", true, Boolean.class, true);
    }

    @Override
    public BigDecimal getRegularLoginAsSingleOrderAmount() {
        return systemStringService.getCustomSystemString("market.RegularLoginAsSingleOrderAmount"
                , "market.RegularLoginAsSingleOrderAmount.comment", true, BigDecimal.class, null);
    }

    @Override
    public BigDecimal getRegularLoginAsTotalOrderAmount() {
        return systemStringService.getCustomSystemString("market.RegularLoginAsTotalOrderAmount"
                , "market.RegularLoginAsTotalOrderAmount.comment", true, BigDecimal.class, null);
    }

    @Override
    public BigDecimal getRegularLoginAs24HOrderAmount() {
        return systemStringService.getCustomSystemString("market.RegularLoginAs24HOrderAmount"
                , "market.RegularLoginAs24HOrderAmount.comment", true, BigDecimal.class, null);
    }
}
