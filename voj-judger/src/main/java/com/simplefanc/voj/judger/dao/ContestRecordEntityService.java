package com.simplefanc.voj.judger.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.simplefanc.voj.common.pojo.entity.contest.ContestRecord;
import com.simplefanc.voj.common.pojo.entity.judge.Judge;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author chenfan
 * @since 2020-10-23
 */
public interface ContestRecordEntityService extends IService<ContestRecord> {

    void updateContestRecord(Judge judge);

}