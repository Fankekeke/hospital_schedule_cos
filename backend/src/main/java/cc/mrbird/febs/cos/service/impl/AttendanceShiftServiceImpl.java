package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.AttendanceShift;
import cc.mrbird.febs.cos.dao.AttendanceShiftMapper;
import cc.mrbird.febs.cos.service.IAttendanceShiftService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class AttendanceShiftServiceImpl extends ServiceImpl<AttendanceShiftMapper, AttendanceShift> implements IAttendanceShiftService {


    /**
     * 分页获取班次定义信息
     *
     * @param page            分页对象
     * @param attendanceShift 班次定义信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceShift> page, AttendanceShift attendanceShift) {
        return baseMapper.queryPage(page, attendanceShift);
    }
}
