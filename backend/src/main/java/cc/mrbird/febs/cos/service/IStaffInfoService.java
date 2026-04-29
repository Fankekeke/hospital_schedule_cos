package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.StaffInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IStaffInfoService extends IService<StaffInfo> {

    /**
     * 分页获取医生信息
     *
     * @param page          分页对象
     * @param staffInfo 医生信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectStaffPage(Page<StaffInfo> page, StaffInfo staffInfo);

    /**
     * 查询用户信息详情【公告信息】
     *
     * @param userId 主键ID
     * @return 结果
     */
    LinkedHashMap<String, Object> selectBulletinDetail(Integer userId);

    /**
     * 查询医生信息
     *
     * @param enterpriseId 医生id
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> selectStaffList(Integer enterpriseId);

    /**
     * 获取医生列表
     *
     * @param enterpriseId 医生ID
     * @return 结果
     */
    List<LinkedHashMap<String, Object>> queryStaffListByStaff(Integer enterpriseId, Integer userId);

    /**
     * 根据用户id查询医生信息
     *
     * @param userId 用户id
     * @return 结果
     */
    LinkedHashMap<String, Object> queryStaffByUserId(Integer userId);
}
