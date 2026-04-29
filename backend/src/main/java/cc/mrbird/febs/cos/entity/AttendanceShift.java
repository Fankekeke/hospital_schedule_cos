package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 班次定义表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AttendanceShift implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 班次名称（如：内科白班、手术加班班次）
     */
    private String shiftName;

    /**
     * 班次类型: 1-常规, 2-手术班, 3-支援班, 4-弹性班
     */
    private Integer shiftType;

    /**
     * 标准上班时间
     */
    private String startTime;

    /**
     * 标准下班时间
     */
    private String endTime;

    /**
     * 允许迟到分钟数（缓冲时间）
     */
    private Integer allowLate;

    /**
     * 是否跨天: 0-否, 1-是
     */
    private Integer isCrossDay;

    /**
     * 夜班津贴/补助标准
     */
    private String nightAllowance;

    /**
     * 删除标识
     */
    private String delFlag;


}
