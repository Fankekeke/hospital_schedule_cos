package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.AttendanceApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface AttendanceApplicationMapper extends BaseMapper<AttendanceApplication> {

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @param attendanceApplication 考勤申请信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceApplication> page, @Param("queryForm") AttendanceApplication attendanceApplication);
}
