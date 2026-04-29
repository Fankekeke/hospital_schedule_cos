package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.AttendanceRawRecord;
import cc.mrbird.febs.cos.dao.AttendanceRawRecordMapper;
import cc.mrbird.febs.cos.service.IAttendanceRawRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class AttendanceRawRecordServiceImpl extends ServiceImpl<AttendanceRawRecordMapper, AttendanceRawRecord> implements IAttendanceRawRecordService {

    /**
     * 分页获取打卡记录信息
     *
     * @param page                分页对象
     * @param attendanceRawRecord 打卡记录信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceRawRecord> page, AttendanceRawRecord attendanceRawRecord) {
        return baseMapper.queryPage(page, attendanceRawRecord);
    }
}
