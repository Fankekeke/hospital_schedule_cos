package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.AttendanceGeoFence;
import cc.mrbird.febs.cos.dao.AttendanceGeoFenceMapper;
import cc.mrbird.febs.cos.service.IAttendanceGeoFenceService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class AttendanceGeoFenceServiceImpl extends ServiceImpl<AttendanceGeoFenceMapper, AttendanceGeoFence> implements IAttendanceGeoFenceService {

    /**
     * 分页获取电子围栏信息
     *
     * @param page                  分页对象
     * @param attendanceGeoFence 电子围栏信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceGeoFence> page, AttendanceGeoFence attendanceGeoFence) {
        return baseMapper.queryPage(page, attendanceGeoFence);
    }
}
