package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.StaffSchedule;
import cc.mrbird.febs.cos.service.IStaffScheduleService;
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
@RequestMapping("/cos/staff-schedule")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StaffScheduleController {

    private final IStaffScheduleService staffScheduleService;

    /**
     * 分页获取排班计划信息
     *
     * @param page          分页对象
     * @param staffSchedule 排班计划信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<StaffSchedule> page, StaffSchedule staffSchedule) {
        return R.ok(staffScheduleService.queryPage(page, staffSchedule));
    }

    /**
     * 获取ID获取排班计划详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(staffScheduleService.getById(id));
    }

    /**
     * 获取排班计划信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(staffScheduleService.list());
    }

    /**
     * 新增排班计划信息
     *
     * @param staffSchedule 排班计划信息
     * @return 结果
     */
    @PostMapping
    public R save(StaffSchedule staffSchedule) {
        staffSchedule.setWorkDate(DateUtil.formatDateTime(new Date()));
        return R.ok(staffScheduleService.save(staffSchedule));
    }

    /**
     * 修改排班计划信息
     *
     * @param staffSchedule 排班计划信息
     * @return 结果
     */
    @PutMapping
    public R edit(StaffSchedule staffSchedule) {
        return R.ok(staffScheduleService.updateById(staffSchedule));
    }

    /**
     * 删除排班计划信息
     *
     * @param ids ids
     * @return 排班计划信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(staffScheduleService.removeByIds(ids));
    }
}
