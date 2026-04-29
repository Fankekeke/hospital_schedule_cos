package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.AttendancePolicy;
import cc.mrbird.febs.cos.dao.AttendancePolicyMapper;
import cc.mrbird.febs.cos.service.IAttendancePolicyService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class AttendancePolicyServiceImpl extends ServiceImpl<AttendancePolicyMapper, AttendancePolicy> implements IAttendancePolicyService {


    /**
     * 分页获取考勤规则信息
     *
     * @param page             分页对象
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendancePolicy> page, AttendancePolicy attendancePolicy) {
        return baseMapper.queryPage(page, attendancePolicy);
    }

    /**
     * 分页获取加班规则信息
     *
     * @param page             分页对象
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> overPage(Page<AttendancePolicy> page, AttendancePolicy attendancePolicy) {
        return baseMapper.overPage(page, attendancePolicy);
    }
}
