package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.AttendanceSummary;
import cc.mrbird.febs.cos.dao.AttendanceSummaryMapper;
import cc.mrbird.febs.cos.service.IAttendanceSummaryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class AttendanceSummaryServiceImpl extends ServiceImpl<AttendanceSummaryMapper, AttendanceSummary> implements IAttendanceSummaryService {

    /**
     * 分页获取考勤汇总信息
     *
     * @param page              分页对象
     * @param attendanceSummary 考勤汇总信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceSummary> page, AttendanceSummary attendanceSummary) {
        return baseMapper.queryPage(page, attendanceSummary);
    }
}
