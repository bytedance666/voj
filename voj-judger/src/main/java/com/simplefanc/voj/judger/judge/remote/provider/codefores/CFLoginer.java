package com.simplefanc.voj.judger.judge.remote.provider.codefores;

import com.simplefanc.voj.judger.judge.remote.httpclient.DedicatedHttpClientFactory;
import com.simplefanc.voj.judger.judge.remote.pojo.RemoteOjInfo;
import com.simplefanc.voj.judger.judge.remote.provider.shared.codeforces.CFStyleLoginer;
import org.springframework.stereotype.Component;

@Component
public class CFLoginer extends CFStyleLoginer {

    public CFLoginer(DedicatedHttpClientFactory dedicatedHttpClientFactory) {
        super(dedicatedHttpClientFactory);
    }

    @Override
    public RemoteOjInfo getOjInfo() {
        return CFInfo.INFO;
    }
}
