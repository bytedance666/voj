package com.simplefanc.voj.judger.judge.remote.provider.poj;

import com.simplefanc.voj.judger.judge.remote.pojo.RemoteOjInfo;
import com.simplefanc.voj.judger.judge.remote.account.RemoteAccount;
import com.simplefanc.voj.judger.judge.remote.httpclient.DedicatedHttpClient;
import com.simplefanc.voj.judger.judge.remote.httpclient.DedicatedHttpClientFactory;
import com.simplefanc.voj.judger.judge.remote.httpclient.HttpStatusValidator;
import com.simplefanc.voj.judger.judge.remote.httpclient.SimpleNameValueEntityFactory;
import com.simplefanc.voj.judger.judge.remote.loginer.RetentiveLoginer;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class POJLoginer extends RetentiveLoginer {

    private final DedicatedHttpClientFactory dedicatedHttpClientFactory;

    @Override
    public RemoteOjInfo getOjInfo() {
        return POJInfo.INFO;
    }

    @Override
    protected void loginEnforce(RemoteAccount account) {
        DedicatedHttpClient client = dedicatedHttpClientFactory.build(getOjInfo().mainHost, account.getContext());
        if (client.get("/").getBody().contains(">Log Out</a>")) {
            return;
        }
        HttpEntity entity = SimpleNameValueEntityFactory.create("B1", "login", "password1", account.getPassword(),
                "url", "%2F", "user_id1", account.getAccountId());
        client.post("/login", entity, HttpStatusValidator.SC_MOVED_TEMPORARILY);
    }

}
