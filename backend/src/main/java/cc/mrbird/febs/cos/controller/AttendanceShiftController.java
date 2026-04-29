package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendanceShift;
import cc.mrbird.febs.cos.service.IAttendanceShiftService;
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
@RequestMapping("/cos/attendance-shift")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceShiftController {

    private final IAttendanceShiftService attendanceShiftService;

    /**
     * 分页获取班次定义信息
     *
     * @param page            分页对象
     * @param attendanceShift 班次定义信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AttendanceShift> page, AttendanceShift attendanceShift) {
        return R.ok(attendanceShiftService.queryPage(page, attendanceShift));
    }

    /**
     * 获取ID获取班次定义详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(attendanceShiftService.getById(id));
    }

    /**
     * 获取班次定义信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(attendanceShiftService.list());
    }

    /**
     * 新增班次定义信息
     *
     * @param attendanceShift 班次定义信息
     * @return 结果
     */
    @PostMapping
    public R save(AttendanceShift attendanceShift) {
        if (attendanceShift.getStartTime() != null && attendanceShift.getEndTime() != null) {
            int comparison = attendanceShift.getStartTime().compareTo(attendanceShift.getEndTime());
            if (comparison > 0) {
                attendanceShift.setIsCrossDay(1);
            } else {
                attendanceShift.setIsCrossDay(0);
            }
        }
        return R.ok(attendanceShiftService.save(attendanceShift));
    }

    /**
     * 修改班次定义信息
     *
     * @param attendanceShift 班次定义信息
     * @return 结果
     */
    @PutMapping
    public R edit(AttendanceShift attendanceShift) {
        if (attendanceShift.getStartTime() != null && attendanceShift.getEndTime() != null) {
            int comparison = attendanceShift.getStartTime().compareTo(attendanceShift.getEndTime());
            if (comparison > 0) {
                attendanceShift.setIsCrossDay(1);
            } else {
                attendanceShift.setIsCrossDay(0);
            }
        }
        return R.ok(attendanceShiftService.updateById(attendanceShift));
    }

    /**
     * 删除班次定义信息
     *
     * @param ids ids
     * @return 班次定义信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(attendanceShiftService.removeByIds(ids));
    }
}
