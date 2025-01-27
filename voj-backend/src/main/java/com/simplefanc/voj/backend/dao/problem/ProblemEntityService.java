package com.simplefanc.voj.backend.dao.problem;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.simplefanc.voj.backend.pojo.dto.ProblemDto;
import com.simplefanc.voj.backend.pojo.vo.ImportProblemVo;
import com.simplefanc.voj.backend.pojo.vo.ProblemVo;
import com.simplefanc.voj.common.pojo.entity.problem.Problem;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author: chenfan
 * @since 2021-10-23
 */

public interface ProblemEntityService extends IService<Problem> {

    Page<ProblemVo> getProblemList(int limit, int currentPage, String title, Integer difficulty,
                                   List<Long> tagIds, String oj, boolean isAdmin);

    boolean adminUpdateProblem(ProblemDto problemDto);

    boolean adminAddProblem(ProblemDto problemDto);

    ImportProblemVo buildExportProblem(Long pid, List<HashMap<String, Object>> problemCaseList,
                                       HashMap<Long, String> languageMap, HashMap<Long, String> tagMap);

}
