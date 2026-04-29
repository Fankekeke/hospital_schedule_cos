package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendanceRawRecord;
import cc.mrbird.febs.cos.service.IAttendanceRawRecordService;
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
@RequestMapping("/cos/attendance-raw-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceRawRecordController {

    private final IAttendanceRawRecordService attendanceRawRecordService;

    /**
     * 分页获取打卡记录信息
     *
     * @param page                分页对象
     * @param attendanceRawRecord 打卡记录信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AttendanceRawRecord> page, AttendanceRawRecord attendanceRawRecord) {
        return R.ok(attendanceRawRecordService.queryPage(page, attendanceRawRecord));
    }

    /**
     * 获取ID获取打卡记录详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(attendanceRawRecordService.getById(id));
    }

    /**
     * 获取打卡记录信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(attendanceRawRecordService.list());
    }

    /**
     * 新增打卡记录信息
     *
     * @param attendanceRawRecord 打卡记录信息
     * @return 结果
     */
    @PostMapping
    public R save(AttendanceRawRecord attendanceRawRecord) {
        attendanceRawRecord.setCheckTime(DateUtil.formatDateTime(new Date()));
        return R.ok(attendanceRawRecordService.save(attendanceRawRecord));
    }

    /**
     * 修改打卡记录信息
     *
     * @param attendanceRawRecord 打卡记录信息
     * @return 结果
     */
    @PutMapping
    public R edit(AttendanceRawRecord attendanceRawRecord) {
        return R.ok(attendanceRawRecordService.updateById(attendanceRawRecord));
    }

    /**
     * 删除打卡记录信息
     *
     * @param ids ids
     * @return 打卡记录信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(attendanceRawRecordService.removeByIds(ids));
    }
}
