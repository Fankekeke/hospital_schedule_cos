package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.StaffSchedule;
import cc.mrbird.febs.cos.dao.StaffScheduleMapper;
import cc.mrbird.febs.cos.service.IStaffScheduleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class StaffScheduleServiceImpl extends ServiceImpl<StaffScheduleMapper, StaffSchedule> implements IStaffScheduleService {

    /**
     * 分页获取排班计划信息
     *
     * @param page          分页对象
     * @param staffSchedule 排班计划信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<StaffSchedule> page, StaffSchedule staffSchedule) {
        return baseMapper.queryPage(page, staffSchedule);
    }
}
