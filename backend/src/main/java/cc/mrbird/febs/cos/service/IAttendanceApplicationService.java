package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.AttendanceApplication;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IAttendanceApplicationService extends IService<AttendanceApplication> {

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @param attendanceApplication 考勤申请信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceApplication> page, AttendanceApplication attendanceApplication);
}
