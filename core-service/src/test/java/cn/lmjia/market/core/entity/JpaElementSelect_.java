package cn.lmjia.market.core.entity;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author CJ
 */
@StaticMetamodel(JpaElementSelect.class)
public abstract class JpaElementSelect_ {
    public static volatile SingularAttribute<JpaElementSelect, Long> id;
    public static volatile SingularAttribute<JpaElementSelect, String> name;
    public static volatile ListAttribute<JpaElementSelect, OneForEntity> entityList;
}
