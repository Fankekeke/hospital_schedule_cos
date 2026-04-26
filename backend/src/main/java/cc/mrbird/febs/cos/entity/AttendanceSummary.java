package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 考勤汇总结果表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AttendanceSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 关联排班计划ID
     */
    private Long scheduleId;

    /**
     * 员工ID
     */
    private Integer staffId;

    /**
     * 日期
     */
    private String workDate;

    /**
     * 状态: 1-正常, 2-迟到, 3-早退, 4-缺卡, 5-旷工
     */
    private Integer resultStatus;

    /**
     * 迟到时长(分钟)
     */
    private Integer lateMinutes;

    /**
     * 早退时长(分钟)
     */
    private Integer earlyMinutes;

    /**
     * 加班时长(分钟)
     */
    private Integer otMinutes;

    /**
     * 本班次实结津贴
     */
    private BigDecimal allowanceAmount;

    /**
     * 是否存在异常
     */
    private Integer isException;

    /**
     * 人工修正备注
     */
    private String auditRemark;


}
