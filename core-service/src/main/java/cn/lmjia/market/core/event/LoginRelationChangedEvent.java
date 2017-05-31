package cn.lmjia.market.core.event;

import cn.lmjia.market.core.entity.Login;
import lombok.Data;
import me.jiangcai.lib.thread.ThreadLocker;

/**
 * 人物关系发生改变时
 *
 * @author CJ
 */
@Data
public class LoginRelationChangedEvent implements ThreadLocker {

    /**
     * 谁的人物关系发生了改变
     */
    private final Login who;

    @Override
    public Object lockObject() {
        return ("LoginRelationChangedEvent-" + who.getLoginName()).intern();
    }
}
