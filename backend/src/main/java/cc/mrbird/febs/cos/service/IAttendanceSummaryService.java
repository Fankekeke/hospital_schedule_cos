package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AttendanceSummary;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IAttendanceSummaryService extends IService<AttendanceSummary> {

    /**
     * 分页获取考勤汇总信息
     *
     * @param page              分页对象
     * @param attendanceSummary 考勤汇总信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceSummary> page, AttendanceSummary attendanceSummary);
}
