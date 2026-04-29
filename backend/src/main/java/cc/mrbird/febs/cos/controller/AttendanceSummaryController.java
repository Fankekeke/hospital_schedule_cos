package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendanceSummary;
import cc.mrbird.febs.cos.service.IAttendanceSummaryService;
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
@RequestMapping("/cos/attendance-summary")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceSummaryController {

    private final IAttendanceSummaryService attendanceSummaryService;

    /**
     * 分页获取考勤汇总信息
     *
     * @param page              分页对象
     * @param attendanceSummary 考勤汇总信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AttendanceSummary> page, AttendanceSummary attendanceSummary) {
        return R.ok(attendanceSummaryService.queryPage(page, attendanceSummary));
    }

    /**
     * 获取ID获取考勤汇总详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(attendanceSummaryService.getById(id));
    }

    /**
     * 获取考勤汇总信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(attendanceSummaryService.list());
    }

    /**
     * 新增考勤汇总信息
     *
     * @param attendanceSummary 考勤汇总信息
     * @return 结果
     */
    @PostMapping
    public R save(AttendanceSummary attendanceSummary) {
        attendanceSummary.setWorkDate(DateUtil.formatDateTime(new Date()));
        return R.ok(attendanceSummaryService.save(attendanceSummary));
    }

    /**
     * 修改考勤汇总信息
     *
     * @param attendanceSummary 考勤汇总信息
     * @return 结果
     */
    @PutMapping
    public R edit(AttendanceSummary attendanceSummary) {
        return R.ok(attendanceSummaryService.updateById(attendanceSummary));
    }

    /**
     * 删除考勤汇总信息
     *
     * @param ids ids
     * @return 考勤汇总信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(attendanceSummaryService.removeByIds(ids));
    }
}
