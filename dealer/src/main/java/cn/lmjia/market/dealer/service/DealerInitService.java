package cn.lmjia.market.dealer.service;

import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.repository.deal.AgentLevelRepository;
import cn.lmjia.market.core.service.ContactWayService;
import cn.lmjia.market.core.service.LoginService;
import me.jiangcai.lib.jdbc.JdbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

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
    @Autowired
    private ContactWayService contactWayService;

    @PostConstruct
    @Transactional
    public void defaultAgents() {
        long count = agentLevelRepository.count((root, query, cb) -> cb.isNull(root.get("superior")));
        if (count == 0) {
            Login login = loginService.newLogin(environment.getProperty("default.agent.loginName"
                    , "master"), null
                    , environment.getProperty("default.agent.password", "123456"));
            agentService.addAgent(null, login
                    , environment.getProperty("default.agent.name", "默认名称"), LocalDate.now()
                    , LocalDate.now().plusYears(1), 0, 0, null);
            contactWayService.updateName(login, environment.getProperty("default.agent.name", "默认名称"));
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
                    addFunction(name, statement, "agentLevel");
                    addFunction(name, statement, "agentBelongs");
                } catch (IOException e) {
                    throw new InternalError(e);
                }
            }

        });
    }

    private void addFunction(String name, Statement statement, String functionName) throws IOException, SQLException {
        // 分2种情况 一种直接存在，第二种有这么几条
        final ClassPathResource resource = new ClassPathResource("/functions/" + functionName + "."
                + name + ".sql");
        if (resource.exists())
            executeResource(statement, resource);
        else {
            int i = 0;
            while (true) {
                final ClassPathResource subResource = new ClassPathResource("/functions/" + functionName + "_"
                        + (i++) + "." + name + ".sql");
                if (subResource.exists())
                    executeResource(statement, subResource);
                else
                    break;
            }
        }
    }

    private void executeResource(Statement statement, Resource resource) throws IOException, SQLException {
        String agentLevel = StreamUtils.copyToString(resource.getInputStream(), Charset.forName("UTF-8"));
        statement.executeUpdate(agentLevel);
    }

}
