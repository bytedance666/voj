package com.simplefanc.voj.backend.validator;

import com.simplefanc.voj.backend.common.constants.AccessEnum;
import com.simplefanc.voj.backend.common.exception.StatusFailException;
import com.simplefanc.voj.backend.pojo.dto.ToJudgeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: chenfan
 * @Date: 2022/3/11 11:20
 * @Description:
 */
@Component
@RequiredArgsConstructor
public class JudgeValidator {

    private final AccessValidator accessValidator;

    private final static List<String> VOJ_LANGUAGE_LIST = Arrays.asList("C++", "C++ With O2", "C", "C With O2",
            "Python3", "Python2", "Java", "Golang", "C#", "PHP", "PyPy2", "PyPy3", "JavaScript Node", "JavaScript V8");

    public void validateSubmissionInfo(ToJudgeDto toJudgeDto) {
        if (toJudgeDto.getCid() != null && toJudgeDto.getCid() != 0) {
            accessValidator.validateAccess(AccessEnum.CONTEST_JUDGE);
        } else {
            accessValidator.validateAccess(AccessEnum.PUBLIC_JUDGE);
        }

        if (!toJudgeDto.getIsRemote() && !VOJ_LANGUAGE_LIST.contains(toJudgeDto.getLanguage())) {
            throw new StatusFailException("提交的代码的语言错误！请使用" + VOJ_LANGUAGE_LIST + "中之一的语言！");
        }

        if (toJudgeDto.getCode().length() < 50 && !toJudgeDto.getLanguage().contains("Py")
                && !toJudgeDto.getLanguage().contains("PHP") && !toJudgeDto.getLanguage().contains("JavaScript")) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要低于50！");
        }

        if (toJudgeDto.getCode().length() > 65535) {
            throw new StatusFailException("提交的代码是无效的，代码字符长度请不要超过65535！");
        }
    }

}