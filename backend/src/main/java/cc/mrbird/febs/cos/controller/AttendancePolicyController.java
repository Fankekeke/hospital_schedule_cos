package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.exception.FebsException;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendancePolicy;
import cc.mrbird.febs.cos.service.IAttendancePolicyService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
@RequestMapping("/cos/attendance-policy")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendancePolicyController {

    private final IAttendancePolicyService attendancePolicyService;

    /**
     * 分页获取考勤规则信息
     *
     * @param page             分页对象
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AttendancePolicy> page, AttendancePolicy attendancePolicy) {
        return R.ok(attendancePolicyService.queryPage(page, attendancePolicy));
    }

    /**
     * 分页获取加班规则信息
     *
     * @param page             分页对象
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    @GetMapping("/overPage")
    public R overPage(Page<AttendancePolicy> page, AttendancePolicy attendancePolicy) {
        return R.ok(attendancePolicyService.overPage(page, attendancePolicy));
    }

    /**
     * 获取ID获取考勤规则详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(attendancePolicyService.getById(id));
    }

    /**
     * 获取考勤规则信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(attendancePolicyService.list());
    }

    /**
     * 新增考勤规则信息
     *
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    @PostMapping
    public R save(AttendancePolicy attendancePolicy) throws FebsException {
        long count = attendancePolicyService.count(Wrappers.<AttendancePolicy>lambdaQuery()
                .eq(AttendancePolicy::getTargetId, attendancePolicy.getTargetId())
                .eq(AttendancePolicy::getTargetType, attendancePolicy.getTargetType()));
        if (count > 0) {
            throw new FebsException("该科室已存在相同类型的考勤规则");
        }
        attendancePolicy.setUpdateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(attendancePolicyService.save(attendancePolicy));
    }

    /**
     * 修改考勤规则信息
     *
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    @PutMapping
    public R edit(AttendancePolicy attendancePolicy) throws FebsException {
        long count = attendancePolicyService.count(Wrappers.<AttendancePolicy>lambdaQuery()
                .eq(AttendancePolicy::getTargetId, attendancePolicy.getTargetId())
                .eq(AttendancePolicy::getTargetType, attendancePolicy.getTargetType())
                .ne(AttendancePolicy::getId, attendancePolicy.getId()));
        if (count > 0) {
            throw new FebsException("该科室已存在相同类型的考勤规则");
        }
        attendancePolicy.setUpdateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(attendancePolicyService.updateById(attendancePolicy));
    }

    /**
     * 删除考勤规则信息
     *
     * @param ids ids
     * @return 考勤规则信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(attendancePolicyService.removeByIds(ids));
    }
}
