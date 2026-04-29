package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AttendanceGeoFence;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IAttendanceGeoFenceService extends IService<AttendanceGeoFence> {

    /**
     * 分页获取电子围栏信息
     *
     * @param page                  分页对象
     * @param attendanceGeoFence 电子围栏信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceGeoFence> page, AttendanceGeoFence attendanceGeoFence);
}
