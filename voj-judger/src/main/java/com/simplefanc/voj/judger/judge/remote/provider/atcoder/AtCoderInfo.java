package com.simplefanc.voj.judger.judge.remote.provider.atcoder;

import com.simplefanc.voj.common.constants.RemoteOj;
import com.simplefanc.voj.judger.judge.remote.pojo.RemoteOjInfo;
import org.apache.http.HttpHost;

public class AtCoderInfo {

    public static final RemoteOjInfo INFO = new RemoteOjInfo(RemoteOj.AtCoder, "AtCoder",
            new HttpHost("atcoder.jp", 443, "https"));
}
