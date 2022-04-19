package com.simplefanc.voj.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplefanc.voj.common.pojo.entity.problem.Language;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LanguageMapper extends BaseMapper<Language> {
}