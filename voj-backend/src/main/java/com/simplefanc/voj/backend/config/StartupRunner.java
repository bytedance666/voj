package com.simplefanc.voj.backend.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.simplefanc.voj.backend.dao.judge.RemoteJudgeAccountEntityService;
import com.simplefanc.voj.backend.config.property.RemoteAccountProperties;
import com.simplefanc.voj.backend.service.admin.system.ConfigService;
import com.simplefanc.voj.common.pojo.entity.judge.RemoteJudgeAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: chenfan
 * @Date: 2021/2/19 22:11
 * @Description: 项目启动后，初始化运行该 run 方法，根据 .env 中自定义配置修改 Nacos 配置
 */
@Component
@Slf4j(topic = "voj")
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final ConfigVo configVo;

    private final ConfigService configService;

    private final RemoteJudgeAccountEntityService remoteJudgeAccountEntityService;

    @Value("${OPEN_REMOTE_JUDGE:true}")
    private String openRemoteJudge;

    /**
     * jwt配置
     */
    @Value("${JWT_TOKEN_SECRET:default}")
    private String tokenSecret;

    @Value("${JWT_TOKEN_EXPIRE:86400}")
    private String tokenExpire;

    @Value("${JWT_TOKEN_FRESH_EXPIRE:43200}")
    private String checkRefreshExpire;

    /**
     * 数据库配置
     */
    @Value("${MYSQL_USERNAME:root}")
    private String mysqlUsername;

    @Value("${MYSQL_ROOT_PASSWORD:root}")
    private String mysqlPassword;

    @Value("${MYSQL_DATABASE_NAME:voj}")
    private String mysqlDbName;

    @Value("${MYSQL_HOST:172.20.0.3}")
    private String mysqlHost;

    @Value("${MYSQL_PUBLIC_HOST:172.20.0.3}")
    private String mysqlPublicHost;

    @Value("${MYSQL_PORT:3306}")
    private Integer mysqlPort;

    /**
     * 缓存配置
     */
    @Value("${REDIS_HOST:172.20.0.2}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private Integer redisPort;

    @Value("${REDIS_PASSWORD:}")
    private String redisPassword;

    /**
     * 判题服务token
     */
    @Value("${JUDGE_TOKEN:default}")
    private String judgeToken;

    /**
     * 邮箱配置
     */
    @Value("${EMAIL_USERNAME:your_email_username}")
    private String emailUsername;

    @Value("${EMAIL_PASSWORD:your_email_password}")
    private String emailPassword;

    @Value("${EMAIL_SERVER_HOST:your_email_host}")
    private String emailHost;

    @Value("${EMAIL_SERVER_PORT:465}")
    private Integer emailPort;

    @Value("${spring.profiles.active}")
    private String profile;

    private final RemoteAccountProperties remoteAccountProps;

    @Override
    public void run(String... args) {
        if ("dev".equals(profile)) {
            return;
        }

        // 动态修改nacos上的配置文件
        if ("default".equals(judgeToken)) {
            configVo.setJudgeToken(IdUtil.fastSimpleUUID());
        } else {
            configVo.setJudgeToken(judgeToken);
        }

        if ("default".equals(tokenSecret)) {
            configVo.setTokenSecret(IdUtil.fastSimpleUUID());
        } else {
            configVo.setTokenSecret(tokenSecret);
        }
        configVo.setTokenExpire(tokenExpire);
        configVo.setCheckRefreshExpire(checkRefreshExpire);

        configVo.setMysqlUsername(mysqlUsername);
        configVo.setMysqlPassword(mysqlPassword);
        configVo.setMysqlHost(mysqlHost);
        configVo.setMysqlPublicHost(mysqlPublicHost);
        configVo.setMysqlPort(mysqlPort);
        configVo.setMysqlDbName(mysqlDbName);

        configVo.setRedisHost(redisHost);
        configVo.setRedisPort(redisPort);
        configVo.setRedisPassword(redisPassword);

        configVo.setEmailHost(emailHost);
        configVo.setEmailPort(emailPort);
        configVo.setEmailUsername(emailUsername);
        configVo.setEmailPassword(emailPassword);

        configService.sendNewConfigToNacos();

        if ("true".equals(openRemoteJudge)) {
            addRemoteJudgeAccountToDb();
        }
    }

    private void addRemoteJudgeAccountToDb() {
        // 初始化清空表
        remoteJudgeAccountEntityService.remove(new QueryWrapper<>());
        List<RemoteJudgeAccount> accountList = new LinkedList<>();
        for (RemoteAccountProperties.RemoteOJ remoteOj : remoteAccountProps.getOjs()) {
            for (RemoteAccountProperties.Account account : remoteOj.getAccounts()) {
                accountList.add(new RemoteJudgeAccount().setUsername(account.getUsername())
                        .setPassword(account.getPassword()).setStatus(true).setVersion(0L).setOj(remoteOj.getOj()));
            }
        }
        if (!remoteJudgeAccountEntityService.saveOrUpdateBatch(accountList)) {
            log.error("RemoteJudgeAccount添加失败------------>{}", "请检查配置文件，然后重新启动！");
        }
    }

}
