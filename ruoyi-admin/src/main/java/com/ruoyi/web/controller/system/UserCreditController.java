package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.CreditLog;
import com.ruoyi.system.domain.UserCredit;
import com.ruoyi.system.service.IUserCreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信誉控制器
 *
 * @author student-hub
 * @date 2026-03-18
 */
@Slf4j
@RestController
@RequestMapping("/user/credit")
public class UserCreditController extends BaseController {

    @Autowired
    private IUserCreditService userCreditService;

    /**
     * 获取用户信誉信息
     */
    @GetMapping("/{userId}")
    public AjaxResult getUserCredit(@PathVariable Long userId) {
        UserCredit credit = userCreditService.getUserCredit(userId);
        return AjaxResult.success(credit);
    }

    /**
     * 获取用户信誉历史记录
     */
    @GetMapping("/log/{userId}")
    public AjaxResult getCreditLogs(@PathVariable Long userId) {
        List<CreditLog> logs = userCreditService.getCreditLogs(userId);
        return AjaxResult.success(logs);
    }

    /**
     * 检查用户是否被禁用
     */
    @GetMapping("/{userId}/banned")
    public AjaxResult checkBanned(@PathVariable Long userId) {
        boolean banned = userCreditService.isUserBanned(userId);
        return AjaxResult.success(banned);
    }
}
