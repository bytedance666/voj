package com.simplefanc.voj.backend.service.admin.system;

import cn.hutool.json.JSONObject;
import com.simplefanc.voj.backend.pojo.dto.*;
import com.simplefanc.voj.common.result.CommonResult;

import java.util.List;

/**
 * @Author: chenfan
 * @Date: 2022/3/9 21:50
 * @Description: 动态修改网站配置，获取后台服务状态及判题服务器的状态
 */
public interface ConfigService {

    /**
     * @MethodName getServiceInfo
     * @Params * @param null
     * @Description 获取当前服务的相关信息以及当前系统的cpu情况，内存使用情况
     * @Return CommonResult
     * @Since 2021/12/3
     */
    JSONObject getServiceInfo();

    List<JSONObject> getJudgeServiceInfo();

    WebConfigDto getWebConfig();

    void setWebConfig(WebConfigDto webConfigDto);

    void deleteHomeCarousel(Long id);

    EmailConfigDto getEmailConfig();

    void setEmailConfig(EmailConfigDto config);

    void testEmail(TestEmailDto testEmailDto);

    DbAndRedisConfigDto getDbAndRedisConfig();

    void setDbAndRedisConfig(DbAndRedisConfigDto config);

    boolean sendNewConfigToNacos();

    SwitchConfigDto getSwitchConfig();

    void setSwitchConfig(SwitchConfigDto config);
}