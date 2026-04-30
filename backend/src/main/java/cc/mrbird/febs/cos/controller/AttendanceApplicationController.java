package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendanceApplication;
import cc.mrbird.febs.cos.entity.MessageInfo;
import cc.mrbird.febs.cos.service.IAttendanceApplicationService;
import cc.mrbird.febs.cos.service.IMessageInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/attendance-application")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceApplicationController {

    private final IAttendanceApplicationService attendanceApplicationService;

    private final IMessageInfoService messageInfoService;

    /**
     * 分页获取考勤审批申请信息
     *
     * @param page                  分页对象
     * @param attendanceApplication 考勤审批申请信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AttendanceApplication> page, AttendanceApplication attendanceApplication) {
        return R.ok(attendanceApplicationService.queryPage(page, attendanceApplication));
    }

    /**
     * 获取ID获取考勤审批申请详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(attendanceApplicationService.getById(id));
    }

    /**
     * 获取考勤审批申请信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(attendanceApplicationService.list());
    }

    /**
     * 新增考勤审批申请信息
     *
     * @param attendanceApplication 考勤审批申请信息
     * @return 结果
     */
    @PostMapping
    public R save(AttendanceApplication attendanceApplication) {
        attendanceApplication.setCreateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(attendanceApplicationService.save(attendanceApplication));
    }

    /**
     * 修改考勤审批申请信息
     *
     * @param attendanceApplication 考勤审批申请信息
     * @return 结果
     */
    @PutMapping
    public R edit(AttendanceApplication attendanceApplication) {
        return R.ok(attendanceApplicationService.updateById(attendanceApplication));
    }

    /**
     * 审核考勤审批申请信息
     *
     * @param attendanceApplication 考勤审批申请信息
     * @return 结果
     */
    @PutMapping("/audit")
    public R audit(AttendanceApplication attendanceApplication) {
        AttendanceApplication attendanceApplication1 = attendanceApplicationService.getById(attendanceApplication.getId());

        String applyTypeName = "";
        if (attendanceApplication1.getApplyType() != null) {
            switch (attendanceApplication1.getApplyType()) {
                case 1:
                    applyTypeName = "补卡申请";
                    break;
                case 2:
                    applyTypeName = "请假申请";
                    break;
                case 3:
                    applyTypeName = "调班申请";
                    break;
                case 4:
                    applyTypeName = "加班认定申请";
                    break;
                default:
                    applyTypeName = "考勤申请";
            }
        }

        String auditStatusName = "";
        if (attendanceApplication.getAuditStatus() != null) {
            switch (attendanceApplication.getAuditStatus()) {
                case 0:
                    auditStatusName = "待审核";
                    break;
                case 1:
                    auditStatusName = "已通过";
                    break;
                case 2:
                    auditStatusName = "已拒绝";
                    break;
                default:
                    auditStatusName = "未知状态";
            }
        }

        StringBuilder content = new StringBuilder();
        content.append("【").append(applyTypeName).append("】");

        if (attendanceApplication.getAuditStatus() != null && attendanceApplication.getAuditStatus() == 0) {
            content.append("您有一条新的").append(applyTypeName).append("，请及时处理。");
        } else {
            content.append("您的").append(applyTypeName).append("审批结果：").append(auditStatusName);

            if (attendanceApplication.getAuditStatus() != null && attendanceApplication.getAuditStatus() == 1) {
                content.append("，祝您工作愉快！");
            } else if (attendanceApplication.getAuditStatus() != null && attendanceApplication.getAuditStatus() == 2) {
                content.append("，如有疑问请联系管理员。");
            }
        }

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
        messageInfo.setContent(content.toString());
        messageInfo.setReadStatus("0");
        messageInfo.setStaffId(attendanceApplication1.getStaffId());
        messageInfoService.save(messageInfo);
        return R.ok(attendanceApplicationService.updateById(attendanceApplication));
    }

    /**
     * 删除考勤审批申请信息
     *
     * @param ids ids
     * @return 考勤审批申请信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(attendanceApplicationService.removeByIds(ids));
    }
}
