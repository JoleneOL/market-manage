package cn.lmjia.market.core;

import cn.lmjia.market.core.entity.JpaElementSelect;
import cn.lmjia.market.core.entity.JpaElementSelect_;
import cn.lmjia.market.core.entity.OneForElement;
import cn.lmjia.market.core.entity.OneForEntity;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author CJ
 */
public class JpaElementSelectTest extends CoreServiceTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback(false)
    public void go() {
        JpaElementSelect select = newSelect();
        entityManager.persist(select);
        entityManager.flush();

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
//        CriteriaQuery<List> cq = cb.createQuery(List.class);
        Root<JpaElementSelect> root = cq.from(JpaElementSelect.class);

        ListJoin<JpaElementSelect, OneForEntity> entityListJoin = root.join(JpaElementSelect_.entityList);
        root.fetch(JpaElementSelect_.entityList);
//        entityListJoin.get("data");

        entityManager.createQuery(cq
//                        .select(root.get(JpaElementSelect_.entityList))
                        .multiselect(root.get("name")
//                        , root.get("elementList")
                                , entityListJoin
//                                , root.join(JpaElementSelect_.entityList)
//                                , cb.array(entityListJoin)
//                                cb.
                        )
                        .distinct(true)
//                .groupBy(root)
        )
                .getResultList()
                .forEach(tuple -> {
                    System.out.println(tuple.get(0));
//                    System.out.println(tuple.get(1));
//                    System.out.println(tuple.get(2));
                });

    }

    private JpaElementSelect newSelect() {
        JpaElementSelect select = new JpaElementSelect();
        select.setName(RandomStringUtils.randomNumeric(10));
        select.setElementList(new ArrayList<>());
        select.setEntityList(new ArrayList<>());

        int count = 2 + random.nextInt(5);
        while (count-- > 0) {
            OneForElement element = new OneForElement();
            element.setData(UUID.randomUUID().toString());
            select.getElementList().add(element);
        }

        count = 2 + random.nextInt(5);
        while (count-- > 0) {
            OneForEntity element = new OneForEntity();
            element.setData(UUID.randomUUID().toString());
            select.getEntityList().add(element);
        }

        return select;
    }

}
