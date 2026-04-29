package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AttendanceShift;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IAttendanceShiftService extends IService<AttendanceShift> {

    /**
     * 分页获取班次定义信息
     *
     * @param page            分页对象
     * @param attendanceShift 班次定义信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceShift> page, AttendanceShift attendanceShift);
}
