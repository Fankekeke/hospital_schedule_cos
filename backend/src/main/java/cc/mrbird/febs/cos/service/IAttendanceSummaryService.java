package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AttendanceSummary;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

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

    /**
     * 生成考勤汇总信息
     */
    boolean generateAttendanceSummary();
    boolean generateAttendanceSummaryFix();

    /**
     * 异常频次分析
     *
     * @param date      月份，格式：yyyy-MM
     * @param dimension 统计维度：dept-部门，staff-个人
     * @return 异常频次分析结果
     */
    List<LinkedHashMap<String, Object>> anomalyFrequencyAnalysis(String date, String dimension);
}
