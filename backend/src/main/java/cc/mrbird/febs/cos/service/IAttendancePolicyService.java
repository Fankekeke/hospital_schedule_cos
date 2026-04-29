package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AttendancePolicy;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IAttendancePolicyService extends IService<AttendancePolicy> {

    /**
     * 分页获取考勤规则信息
     *
     * @param page             分页对象
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendancePolicy> page, AttendancePolicy attendancePolicy);

    /**
     * 分页获取加班规则信息
     *
     * @param page             分页对象
     * @param attendancePolicy 考勤规则信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> overPage(Page<AttendancePolicy> page, AttendancePolicy attendancePolicy);
}
