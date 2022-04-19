package com.simplefanc.voj.server.dao.training;


import com.baomidou.mybatisplus.extension.service.IService;
import com.simplefanc.voj.common.pojo.entity.training.TrainingRecord;
import com.simplefanc.voj.server.pojo.vo.TrainingRecordVo;

import java.util.List;

/**
 * @Author: chenfan
 * @Date: 2021/11/21 23:38
 * @Description:
 */
public interface TrainingRecordEntityService extends IService<TrainingRecord> {

    List<TrainingRecordVo> getTrainingRecord(Long tid);

}