package cc.mrbird.febs.cos.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 考勤规则策略表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AttendancePolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 策略名称
     */
    private String policyName;

    /**
     * 作用对象: 1-打卡, 2-加班
     */
    private Integer targetType;

    /**
     * 院区ID或科室ID
     */
    private Integer targetId;

    /**
     * GPS允许打卡范围(米)
     */
    private Integer gpsRange;

    /**
     * 验证组合: FACE,WIFI,GPS,BLUETOOTH
     */
    private String verifyMethods;

    /**
     * 加班规则配置（如：{"start_after": 30, "unit": "hour"}）
     */
    private String overtimeRule;

    private String updateTime;

    @TableField(exist = false)
    private String deptName;


}
