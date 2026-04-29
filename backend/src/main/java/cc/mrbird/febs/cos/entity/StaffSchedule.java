package cc.mrbird.febs.cos.entity;

import java.time.LocalDate;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 员工排班计划表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StaffSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 员工ID
     */
    private Integer staffId;

    /**
     * 所属科室ID
     */
    private Integer deptId;

    /**
     * 排班日期
     */
    private String workDate;

    /**
     * 关联班次ID
     */
    private Integer shiftId;

    /**
     * 执勤院区ID
     */
    private Integer campusId;

    /**
     * 排班状态: 0-正常, 1-调班中, 2-已请假
     */
    private Integer status;

    /**
     * 类型（1.正常 2.临派）
     */
    private String type;
    private String startTime;
    private String endTime;


}
