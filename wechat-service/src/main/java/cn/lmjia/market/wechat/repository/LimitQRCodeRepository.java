package cn.lmjia.market.wechat.repository;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.wechat.entity.LimitQRCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface LimitQRCodeRepository extends JpaRepository<LimitQRCode, Integer> {

    //id最大的
    LimitQRCode findTopOrderByIdDesc();

    //最长没用的
    LimitQRCode findTopOrderByLastUseTimeAsc();

    LimitQRCode findByLogin(Login login);

}
