package com.simplefanc.voj.judger.judge.remote.provider.tkoj;

import cn.hutool.crypto.SecureUtil;
import com.simplefanc.voj.judger.judge.remote.pojo.RemoteOjInfo;
import com.simplefanc.voj.judger.judge.remote.account.RemoteAccount;
import com.simplefanc.voj.judger.judge.remote.httpclient.*;
import com.simplefanc.voj.judger.judge.remote.loginer.RetentiveLoginer;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TKOJLoginer extends RetentiveLoginer {

    private final DedicatedHttpClientFactory dedicatedHttpClientFactory;

    @Override
    public RemoteOjInfo getOjInfo() {
        return TKOJInfo.INFO;
    }

    @Override
    protected void loginEnforce(RemoteAccount account) throws Exception {
        DedicatedHttpClient client = dedicatedHttpClientFactory.build(getOjInfo().mainHost, account.getContext());
        if (client.get("/loginpage.php", HttpStatusValidator.SC_OK).getBody().contains("Please logout First!")) {
            return;
        }
        HttpEntity entity = SimpleNameValueEntityFactory.create("user_id", account.getAccountId(), "password",
                SecureUtil.md5(account.getPassword()), "csrf", TKOJVerifyUtil.getCsrf(client), "vcode",
                TKOJVerifyUtil.getCaptcha(client));
        HttpPost post = new HttpPost("/login.php");
        post.setEntity(entity);
        client.execute(post, HttpStatusValidator.SC_OK, new HttpBodyValidator("history.go(-2);"));
    }

}
