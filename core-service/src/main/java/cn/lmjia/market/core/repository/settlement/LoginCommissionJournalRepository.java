package cn.lmjia.market.core.repository.settlement;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.settlement.LoginCommissionJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author CJ
 */
public interface LoginCommissionJournalRepository extends JpaRepository<LoginCommissionJournal, String>
        , JpaSpecificationExecutor<LoginCommissionJournal> {

    List<LoginCommissionJournal> findByLoginOrderByHappenTimeAsc(Login login);
}
