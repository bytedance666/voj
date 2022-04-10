package com.simplefanc.voj.dao.problem.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.simplefanc.voj.dao.problem.CodeTemplateEntityService;
import com.simplefanc.voj.mapper.CodeTemplateMapper;
import com.simplefanc.voj.pojo.entity.problem.CodeTemplate;

/**
 * @Author: chenfan
 * @Date: 2021/4/24 10:27
 * @Description:
 */
@Service
public class CodeTemplateEntityServiceImpl extends ServiceImpl<CodeTemplateMapper, CodeTemplate> implements CodeTemplateEntityService {
}