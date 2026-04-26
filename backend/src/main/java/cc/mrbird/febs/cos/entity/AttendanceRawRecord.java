package cc.mrbird.febs.cos.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 原始打卡记录表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AttendanceRawRecord implements Serializable {

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
     * 打卡时间
     */
    private String checkTime;

    /**
     * 1-上班打卡, 2-下班打卡
     */
    private Integer checkType;

    /**
     * 验证方式: GPS, BLUETOOTH, FACE
     */
    private String verifyMode;

    /**
     * 经纬度或蓝牙Beacon ID
     */
    private String locationInfo;

    /**
     * Wi-Fi名称
     */
    private String wifiSsid;

    /**
     * 打卡设备序列号
     */
    private String deviceId;

    /**
     * 数据是否加密: 0-否, 1-是
     */
    private Integer isEncrypted;


}
