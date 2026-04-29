package cc.mrbird.febs.cos.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 电子围栏区域配置表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AttendanceGeoFence implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 区域名称（如：东院区住院部、西院急诊楼）
     */
    private String areaName;

    /**
     * 围栏类型: 1-GPS圆形, 2-GPS多边形
     */
    private Integer fenceType;

    /**
     * 中心点经度
     */
    private BigDecimal longitude;

    /**
     * 中心点纬度
     */
    private BigDecimal latitude;

    /**
     * 默认半径(米)
     */
    private Integer radius;

    /**
     * 多边形顶点坐标集合
     */
    private String polygonPoints;

    /**
     * 蓝牙Beacon唯一识别码
     */
    private String beaconUuid;

    /**
     * Wi-Fi MAC地址(BSSID)
     */
    private String wifiBssid;

    /**
     * 状态: 0-禁用, 1-启用
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remark;

    private String createTime;

    private String updateTime;


}
