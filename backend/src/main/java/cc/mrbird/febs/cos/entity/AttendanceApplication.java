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
 * 考勤审批申请表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AttendanceApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请人
     */
    private Integer staffId;

    /**
     * 1-补卡, 2-请假, 3-调班, 4-加班认定
     */
    private Integer applyType;

    /**
     * 开始时间
     */
    private String startDatetime;

    /**
     * 结束时间
     */
    private String endDatetime;

    /**
     * 申请理由
     */
    private String reason;

    /**
     * 审批状态: 0-待审, 1-通过, 2-拒绝
     */
    private Integer auditStatus;

    /**
     * 审批人ID
     */
    private Integer auditorId;

    private String createTime;

    @TableField(exist = false)
    private String staffName;

    private String approvalContent;


}
