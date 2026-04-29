package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AttendanceGeoFence;
import cc.mrbird.febs.cos.service.IAttendanceGeoFenceService;
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
@RequestMapping("/cos/attendance-geo-fence")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceGeoFenceController {

    private final IAttendanceGeoFenceService attendanceGeoFenceService;

    /**
     * 分页获取电子围栏信息
     *
     * @param page                  分页对象
     * @param attendanceGeoFence 电子围栏信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AttendanceGeoFence> page, AttendanceGeoFence attendanceGeoFence) {
        return R.ok(attendanceGeoFenceService.queryPage(page, attendanceGeoFence));
    }

    /**
     * 获取ID获取电子围栏详情
     *
     * @param id 主键
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(attendanceGeoFenceService.getById(id));
    }

    /**
     * 获取电子围栏信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(attendanceGeoFenceService.list());
    }

    /**
     * 新增电子围栏信息
     *
     * @param attendanceGeoFence 电子围栏信息
     * @return 结果
     */
    @PostMapping
    public R save(AttendanceGeoFence attendanceGeoFence) {
        attendanceGeoFence.setCreateTime(DateUtil.formatDateTime(new Date()));
        return R.ok(attendanceGeoFenceService.save(attendanceGeoFence));
    }

    /**
     * 修改电子围栏信息
     *
     * @param attendanceGeoFence 电子围栏信息
     * @return 结果
     */
    @PutMapping
    public R edit(AttendanceGeoFence attendanceGeoFence) {
        return R.ok(attendanceGeoFenceService.updateById(attendanceGeoFence));
    }

    /**
     * 删除电子围栏信息
     *
     * @param ids ids
     * @return 电子围栏信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(attendanceGeoFenceService.removeByIds(ids));
    }
}
