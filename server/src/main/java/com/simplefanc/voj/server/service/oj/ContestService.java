package com.simplefanc.voj.server.service.oj;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplefanc.voj.common.pojo.entity.common.Announcement;
import com.simplefanc.voj.server.pojo.dto.ContestPrintDto;
import com.simplefanc.voj.server.pojo.dto.ContestRankDto;
import com.simplefanc.voj.server.pojo.dto.RegisterContestDto;
import com.simplefanc.voj.server.pojo.dto.UserReadContestAnnouncementDto;
import com.simplefanc.voj.server.pojo.vo.*;

import java.util.List;

/**
 * @Author: chenfan
 * @Date: 2022/3/11 22:26
 * @Description:
 */

public interface ContestService {

    IPage<ContestVo> getContestList(Integer limit, Integer currentPage, Integer status, Integer type, String keyword);

    ContestVo getContestInfo(Long cid);

    void toRegisterContest(RegisterContestDto registerContestDto);

    AccessVo getContestAccess(Long cid);

    List<ContestProblemVo> getContestProblem(Long cid);

    ProblemInfoVo getContestProblemDetails(Long cid, String displayId);


    // TODO 参数过多
    IPage<JudgeVo> getContestSubmissionList(Integer limit,
                                            Integer currentPage,
                                            Boolean onlyMine,
                                            String displayId,
                                            Integer searchStatus,
                                            String searchUsername,
                                            Long searchCid,
                                            Boolean beforeContestSubmit,
                                            Boolean completeProblemID);

    IPage getContestRank(ContestRankDto contestRankDto);

    IPage<AnnouncementVo> getContestAnnouncement(Long cid, Integer limit, Integer currentPage);


    List<Announcement> getContestUserNotReadAnnouncement(UserReadContestAnnouncementDto userReadContestAnnouncementDto);


    void submitPrintText(ContestPrintDto contestPrintDto);

}