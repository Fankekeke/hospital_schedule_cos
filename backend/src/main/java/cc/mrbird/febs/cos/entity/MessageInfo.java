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
 * 消息通知
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MessageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 员工ID
     */
    private Integer staffId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    private String createDate;

    /**
     * 状态（0.未读 1.已读）
     */
    private String readStatus;

    /**
     * 已读时间
     */
    private String checkDate;

    /**
     * 员工名称
     */
    @TableField(exist = false)
    private String staffName;


}
