package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.deal.SalesAchievement;
import cn.lmjia.market.core.entity.deal.Salesman;
import cn.lmjia.market.core.repository.deal.SalesAchievementRepository;
import cn.lmjia.market.core.service.SalesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class SalesmanServiceImpl implements SalesmanService {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SalesAchievementRepository salesAchievementRepository;

    @Override
    public void salesmanShareTo(long salesmanId, Login login) {
        SalesAchievement achievement = new SalesAchievement();
        achievement.setTargetLogin(login);
        achievement.setTargetTime(LocalDateTime.now());
        achievement.setWhose(get(salesmanId));
        salesAchievementRepository.save(achievement);
    }

    @Override
    public SalesAchievement pick(Login login) {
        SalesAchievement achievement = salesAchievementRepository
                .findTop1ByTargetLoginAndPickedFalseOrderByTargetTimeDesc(login);
        if (achievement == null) {
            return null;
        }
        // 把之前未pick的记录 清理掉 因为没有任何意义了
        salesAchievementRepository.findByTargetLoginAndPickedFalse(login).stream()
                .filter(salesAchievement -> !salesAchievement.equals(achievement))
                .forEach(salesAchievementRepository::delete);
        achievement.setPicked(true);
        return achievement;
    }

    @Override
    public List<SalesAchievement> all(Salesman salesman) {
        return salesAchievementRepository.findByWhose(salesman);
    }

    @Override
    public Salesman get(long id) {
        return entityManager.getReference(Salesman.class, id);
//        return salesmanRepository.getOne(id);
//        return null;
    }

    @Override
    public Salesman newSalesman(Login login, BigDecimal rate, String rank) {
        Salesman salesman = new Salesman();
        salesman.setLogin(login);
        salesman.setSalesRate(rate);
        salesman.setRank(rank);
        entityManager.persist(salesman);
        return salesman;
    }
}
