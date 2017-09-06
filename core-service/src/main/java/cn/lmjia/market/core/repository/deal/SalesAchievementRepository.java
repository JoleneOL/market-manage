package cn.lmjia.market.core.repository.deal;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.deal.Salesman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface SalesAchievementRepository extends JpaRepository<SalesAchievement, Long>
        , JpaSpecificationExecutor<SalesAchievement> {

    SalesAchievement findTop1ByTargetLoginAndPickedFalseOrderByTargetTimeDesc(Login login);

    List<SalesAchievement> findByTargetLoginAndPickedFalse(Login login);

    List<SalesAchievement> findByWhose(Salesman salesman);

}
