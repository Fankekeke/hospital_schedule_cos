package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendanceApplication;
import cc.mrbird.febs.cos.service.IAttendanceApplicationService;
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
