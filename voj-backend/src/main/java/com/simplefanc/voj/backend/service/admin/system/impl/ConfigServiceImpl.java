package com.simplefanc.voj.backend.service.admin.system.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.simplefanc.voj.backend.common.exception.StatusFailException;
import com.simplefanc.voj.backend.common.utils.ConfigUtil;
import com.simplefanc.voj.backend.common.utils.RestTemplateUtil;
import com.simplefanc.voj.backend.config.ConfigVo;
import com.simplefanc.voj.backend.dao.common.FileEntityService;
import com.simplefanc.voj.backend.pojo.dto.*;
import com.simplefanc.voj.backend.service.admin.system.ConfigService;
import com.simplefanc.voj.backend.service.email.EmailService;
import com.simplefanc.voj.common.pojo.entity.common.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @Author: chenfan
 * @Date: 2022/3/9 21:50
 * @Description: 动态修改网站配置，获取后台服务状态及判题服务器的状态
 */
@Service
@Slf4j(topic = "voj")
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigVo configVo;

    private final EmailService emailService;

    private final FileEntityService fileEntityService;

    private final ConfigUtil configUtil;

    private final DiscoveryClient discoveryClient;

    private final RestTemplateUtil restTemplateUtil;

    @Value("${service-url.name}")
    private String judgeServiceName;

    @Value("${spring.application.name}")
    private String currentServiceName;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosServerAddr;

    @Value("voj" + "-" + "${spring.profiles.active}" + ".yml")
    private String dataId;

    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    @Value("${spring.cloud.nacos.config.username}")
    private String nacosUsername;

    @Value("${spring.cloud.nacos.config.password}")
    private String nacosPassword;

    /**
     * @MethodName getServiceInfo
     * @Params @param null
     * @Description 获取当前服务的相关信息以及当前系统的cpu情况，内存使用情况
     * @Return CommonResult
     * @Since 2021/12/3
     */
    @Override
    public JSONObject getServiceInfo() {
        JSONObject result = new JSONObject();

        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(currentServiceName);
        String response = restTemplateUtil.get(nacosServerAddr, "/nacos/v1/ns/operator/metrics", String.class);

        JSONObject jsonObject = JSONUtil.parseObj(response);
        // 获取当前数据后台所在机器环境
        // 当前机器的cpu核数
        int cores = OshiUtil.getCpuInfo().getCpuNum();
        double cpuLoad = 100 - OshiUtil.getCpuInfo().getFree();
        // 当前服务所在机器cpu使用率
        String percentCpuLoad = String.format("%.2f", cpuLoad) + "%";
        // 当前服务所在机器总内存
        double totalVirtualMemory = OshiUtil.getMemory().getTotal();
        // 当前服务所在机器空闲内存
        double freePhysicalMemorySize = OshiUtil.getMemory().getAvailable();
        double value = freePhysicalMemorySize / totalVirtualMemory;
        // 当前服务所在机器内存使用率
        String percentMemoryLoad = String.format("%.2f", (1 - value) * 100) + "%";
        result.set("nacos", jsonObject);
        result.set("backupCores", cores);
        result.set("backupService", serviceInstances);
        result.set("backupPercentCpuLoad", percentCpuLoad);
        result.set("backupPercentMemoryLoad", percentMemoryLoad);
        return result;
    }

    @Override
    public List<JSONObject> getJudgeServiceInfo() {
        List<JSONObject> serviceInfoList = new LinkedList<>();
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(judgeServiceName);
        for (ServiceInstance serviceInstance : serviceInstances) {
            String result = restTemplateUtil.get(serviceInstance.getUri(), "/get-sys-config", String.class);
            JSONObject jsonObject = JSONUtil.parseObj(result);
            jsonObject.set("service", serviceInstance);
            serviceInfoList.add(jsonObject);
        }
        return serviceInfoList;
    }

    @Override
    public WebConfigDto getWebConfig() {
        return BeanUtil.copyProperties(configVo, WebConfigDto.class);
//        return WebConfigDto.builder().baseUrl(UnicodeUtil.toString(configVo.getBaseUrl()))
//                .name(UnicodeUtil.toString(configVo.getName()))
//                .shortName(UnicodeUtil.toString(configVo.getShortName()))
//                .description(UnicodeUtil.toString(configVo.getDescription()))
//                .register(configVo.getRegister())
//                .problem(configVo.getProblem())
//                .training(configVo.getTraining())
//                .contest(configVo.getContest())
//                .status(configVo.getStatus())
//                .rank(configVo.getRank())
//                .discussion(configVo.getDiscussion())
//                .introduction(configVo.getIntroduction())
//                .recordName(UnicodeUtil.toString(configVo.getRecordName()))
//                .recordUrl(UnicodeUtil.toString(configVo.getRecordUrl()))
//                .projectName(UnicodeUtil.toString(configVo.getProjectName()))
//                .projectUrl(UnicodeUtil.toString(configVo.getProjectUrl())).build();
    }

    @Override
    public void setWebConfig(WebConfigDto webConfigDto) {
        if (!StrUtil.isEmpty(webConfigDto.getBaseUrl())) {
            configVo.setBaseUrl(webConfigDto.getBaseUrl());
        }
        if (!StrUtil.isEmpty(webConfigDto.getName())) {
            configVo.setName(webConfigDto.getName());
        }
        if (!StrUtil.isEmpty(webConfigDto.getShortName())) {
            configVo.setShortName(webConfigDto.getShortName());
        }
        if (!StrUtil.isEmpty(webConfigDto.getDescription())) {
            configVo.setDescription(webConfigDto.getDescription());
        }
//        if (webConfigDto.getRegister() != null) {
//            configVo.setRegister(webConfigDto.getRegister());
//        }
//        if (webConfigDto.getCodeVisibleStartTime() != null) {
//            configVo.setCodeVisibleStartTime(webConfigDto.getCodeVisibleStartTime());
//        }
        if (!StrUtil.isEmpty(webConfigDto.getRecordName())) {
            configVo.setRecordName(webConfigDto.getRecordName());
        }
        if (!StrUtil.isEmpty(webConfigDto.getRecordUrl())) {
            configVo.setRecordUrl(webConfigDto.getRecordUrl());
        }
        if (!StrUtil.isEmpty(webConfigDto.getProjectName())) {
            configVo.setProjectName(webConfigDto.getProjectName());
        }
        if (!StrUtil.isEmpty(webConfigDto.getProjectUrl())) {
            configVo.setProjectUrl(webConfigDto.getProjectUrl());
        }

        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    @Override
    public void deleteHomeCarousel(Long id) {
        File imgFile = fileEntityService.getById(id);
        if (imgFile == null) {
            throw new StatusFailException("文件id错误，图片不存在");
        }
        boolean isOk = fileEntityService.removeById(id);
        if (isOk) {
            FileUtil.del(imgFile.getFilePath());
        } else {
            throw new StatusFailException("删除失败！");
        }
    }

    @Override
    public EmailConfigDto getEmailConfig() {
        return BeanUtil.copyProperties(configVo, EmailConfigDto.class);
//        return EmailConfigDto.builder().emailUsername(configVo.getEmailUsername())
//                .emailPassword(configVo.getEmailPassword()).emailHost(configVo.getEmailHost())
//                .emailPort(configVo.getEmailPort()).emailSsl(configVo.getEmailSsl()).build();
    }

    @Override
    public void setEmailConfig(EmailConfigDto config) {
        if (!StrUtil.isEmpty(config.getEmailHost())) {
            configVo.setEmailHost(config.getEmailHost());
        }
        if (!StrUtil.isEmpty(config.getEmailPassword())) {
            configVo.setEmailPassword(config.getEmailPassword());
        }

        if (config.getEmailPort() != null) {
            configVo.setEmailPort(config.getEmailPort());
        }

        if (!StrUtil.isEmpty(config.getEmailUsername())) {
            configVo.setEmailUsername(config.getEmailUsername());
        }

        if (config.getEmailSsl() != null) {
            configVo.setEmailSsl(config.getEmailSsl());
        }

        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    @Override
    public void testEmail(TestEmailDto testEmailDto) {
        String email = testEmailDto.getEmail();
        if (StrUtil.isEmpty(email)) {
            throw new StatusFailException("测试的邮箱不能为空！");
        }
        boolean isEmail = Validator.isEmail(email);
        if (isEmail) {
            emailService.testEmail(email);
        } else {
            throw new StatusFailException("测试的邮箱格式不正确！");
        }
    }

    @Override
    public DbAndRedisConfigDto getDbAndRedisConfig() {
        return BeanUtil.copyProperties(configVo, DbAndRedisConfigDto.class);
//        return DbAndRedisConfigDto.builder().dbName(configVo.getMysqlDbName()).dbHost(configVo.getMysqlHost())
//                .dbPort(configVo.getMysqlPort()).dbUsername(configVo.getMysqlUsername())
//                .dbPassword(configVo.getMysqlPassword()).redisHost(configVo.getRedisHost())
//                .redisPort(configVo.getRedisPort()).redisPassword(configVo.getRedisPassword()).build();
    }

    @Override
    public void setDbAndRedisConfig(DbAndRedisConfigDto config) {
        if (!StrUtil.isEmpty(config.getDbName())) {
            configVo.setMysqlDbName(config.getDbName());
        }

        if (!StrUtil.isEmpty(config.getDbHost())) {
            configVo.setMysqlHost(config.getDbHost());
        }
        if (config.getDbPort() != null) {
            configVo.setMysqlPort(config.getDbPort());
        }
        if (!StrUtil.isEmpty(config.getDbUsername())) {
            configVo.setMysqlUsername(config.getDbUsername());
        }
        if (!StrUtil.isEmpty(config.getDbPassword())) {
            configVo.setMysqlPassword(config.getDbPassword());
        }

        if (!StrUtil.isEmpty(config.getRedisHost())) {
            configVo.setRedisHost(config.getRedisHost());
        }

        if (config.getRedisPort() != null) {
            configVo.setRedisPort(config.getRedisPort());
        }
        if (!StrUtil.isEmpty(config.getRedisPassword())) {
            configVo.setRedisPassword(config.getRedisPassword());
        }

        boolean isOk = sendNewConfigToNacos();

        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

    @Override
    public boolean sendNewConfigToNacos() {
        Properties properties = new Properties();
        properties.put("serverAddr", nacosServerAddr);

        // if need username and password to login
        properties.put("username", nacosUsername);
        properties.put("password", nacosPassword);

        com.alibaba.nacos.api.config.ConfigService configService;
        boolean isOk = false;
        try {
            configService = NacosFactory.createConfigService(properties);
            isOk = configService.publishConfig(dataId, group,
                    configUtil.getConfigContent(), ConfigType.YAML.getType());
        } catch (NacosException e) {
            log.error("通过 Nacos 修改网站配置异常--------------->", e);
        }
        return isOk;
    }

    @Override
    public SwitchConfigDto getSwitchConfig() {
        return BeanUtil.copyProperties(configVo, SwitchConfigDto.class);
//        return SwitchConfigDto.builder()
//                .openPublicDiscussion(configVo.getOpenPublicDiscussion())
//                .openContestComment(configVo.getOpenContestComment())
//                .openPublicJudge(configVo.getOpenPublicJudge())
//                .openContestJudge(configVo.getOpenContestJudge())
//                .defaultSubmitInterval(configVo.getDefaultSubmitInterval())
//                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setSwitchConfig(SwitchConfigDto config) {
//        if (config.getOpenPublicDiscussion() != null) {
//            configVo.setOpenPublicDiscussion(config.getOpenPublicDiscussion());
//        }
//        if (config.getOpenContestComment() != null) {
//            configVo.setOpenContestComment(config.getOpenContestComment());
//        }
        if (config.getOpenPublicJudge() != null) {
            configVo.setOpenPublicJudge(config.getOpenPublicJudge());
        }
        if (config.getOpenContestJudge() != null) {
            configVo.setOpenContestJudge(config.getOpenContestJudge());
        }
        if (config.getDefaultSubmitInterval() != null) {
            if (config.getDefaultSubmitInterval() >= 0) {
                configVo.setDefaultSubmitInterval(config.getDefaultSubmitInterval());
            } else {
                configVo.setDefaultSubmitInterval(0);
            }
        }
        if (config.getCodeVisibleStartTime() != null) {
            configVo.setCodeVisibleStartTime(config.getCodeVisibleStartTime());
        }

        if (config.getRegister() != null) {
            configVo.setRegister(config.getRegister());
        }
        if (config.getProblem() != null) {
            configVo.setProblem(config.getProblem());
        }
        if (config.getTraining() != null) {
            configVo.setTraining(config.getTraining());
        }
        if (config.getContest() != null) {
            configVo.setContest(config.getContest());
        }
        if (config.getStatus() != null) {
            configVo.setStatus(config.getStatus());
        }
        if (config.getRank() != null) {
            configVo.setRank(config.getRank());
        }
        if (config.getDiscussion() != null) {
            configVo.setDiscussion(config.getDiscussion());
        }
        if (config.getIntroduction() != null) {
            configVo.setIntroduction(config.getIntroduction());
        }
        boolean isOk = sendNewConfigToNacos();
        if (!isOk) {
            throw new StatusFailException("修改失败");
        }
    }

}