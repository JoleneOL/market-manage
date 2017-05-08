package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.AgentLevelRepository;
import cn.lmjia.market.core.service.LoginService;
import me.jiangcai.lib.jdbc.JdbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author CJ
 */
@Service
public class DealerInitService {

    @Autowired
    private Environment environment;
    @Autowired
    private AgentService agentService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private AgentLevelRepository agentLevelRepository;
    @Autowired
    private JdbcService jdbcService;

    @PostConstruct
    @Transactional
    public void defaultAgents() {
        long count = agentLevelRepository.count((root, query, cb) -> cb.isNull(root.get("superior")));
        if (count == 0) {
            Login login = new Login();
            login.setLoginName(environment.getProperty("default.agent.loginName", "master"));
            login = loginService.password(login
                    , environment.getProperty("default.agent.password", "masterIsMaster"));

            agentService.addTopAgent(login, environment.getProperty("default.agent.name", "默认名称"));
        }
    }

    @PostConstruct
    @Transactional
    public void functions() throws SQLException {
        jdbcService.runJdbcWork(connection -> {
            String name;
            if (connection.profile().isMySQL()) {
                name = "mysql";
            } else if (connection.profile().isH2()) {
                name = "h2";
            } else
                throw new IllegalStateException("not support database platform:" + connection.profile());

            try (Statement statement = connection.getConnection().createStatement()) {
                try {
                    String agentLevel = StreamUtils.copyToString(new ClassPathResource("/functions/agentLevel."
                            + name + ".sql").getInputStream(), Charset.forName("UTF-8"));
                    statement.executeUpdate(agentLevel);
                } catch (IOException e) {
                    throw new InternalError(e);
                }
            }

        });
    }

}
