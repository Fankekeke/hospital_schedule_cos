package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.StaffSchedule;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IStaffScheduleService extends IService<StaffSchedule> {

    /**
     * 分页获取排班计划信息
     *
     * @param page          分页对象
     * @param staffSchedule 排班计划信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<StaffSchedule> page, StaffSchedule staffSchedule);
}
