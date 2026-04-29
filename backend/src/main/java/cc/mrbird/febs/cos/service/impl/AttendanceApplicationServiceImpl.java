package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.AttendanceApplication;
import cc.mrbird.febs.cos.dao.AttendanceApplicationMapper;
import cc.mrbird.febs.cos.service.IAttendanceApplicationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class AttendanceApplicationServiceImpl extends ServiceImpl<AttendanceApplicationMapper, AttendanceApplication> implements IAttendanceApplicationService {

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @param attendanceApplication 考勤申请信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceApplication> page, AttendanceApplication attendanceApplication) {
        return baseMapper.queryPage(page, attendanceApplication);
    }
}
