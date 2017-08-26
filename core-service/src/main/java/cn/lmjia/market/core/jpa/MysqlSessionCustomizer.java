package cn.lmjia.market.core.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * @author CJ
 */
public class MysqlSessionCustomizer implements SessionCustomizer {

    private static final Log log = LogFactory.getLog(MysqlSessionCustomizer.class);

    @Override
    public void customize(Session session) throws Exception {
        session.getEventManager().addListener(new SessionEventAdapter() {
            @Override
            public void preBeginTransaction(SessionEvent event) {
                try {
                    UnitOfWork work = event.getSession().acquireUnitOfWork();
                    try {
                        work.executeNonSelectingSQL("set names utf8mb4");
                        work.commit();
                        work = null;
                    } finally {
                        if (work != null)
                            work.release();
                    }
                } catch (Exception ex) {
                    log.error("UTF8MB4", ex);
                }

            }
        });
    }
}
