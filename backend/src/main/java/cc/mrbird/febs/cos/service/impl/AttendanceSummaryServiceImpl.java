package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.dao.AttendanceSummaryMapper;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceSummaryServiceImpl extends ServiceImpl<AttendanceSummaryMapper, AttendanceSummary> implements IAttendanceSummaryService {

    private final IStaffInfoService staffInfoService;

    private final IAttendancePolicyService attendancePolicyService;

    private final IAttendanceRawRecordService attendanceRawRecordService;

    private final IAttendanceShiftService attendanceShiftService;

    /**
     * 分页获取考勤汇总信息
     *
     * @param page              分页对象
     * @param attendanceSummary 考勤汇总信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceSummary> page, AttendanceSummary attendanceSummary) {
        return baseMapper.queryPage(page, attendanceSummary);
    }

    /**
     * 生成考勤汇总信息
     */
    @Override
    public boolean generateAttendanceSummary() {
        String yesterday = DateUtil.formatDate(DateUtil.yesterday());

        List<StaffInfo> staffInfoList = staffInfoService.list();
        if (staffInfoList == null || staffInfoList.isEmpty()) {
            return false;
        }

        this.remove(Wrappers.<AttendanceSummary>lambdaQuery()
                .eq(AttendanceSummary::getWorkDate, yesterday));

        List<AttendanceRawRecord> attendanceRawRecordList = attendanceRawRecordService.list();

        List<AttendanceShift> attendanceShiftList = attendanceShiftService.list();

        List<AttendancePolicy> attendancePolicyList = attendancePolicyService.list(Wrappers.<AttendancePolicy>lambdaQuery()
                .eq(AttendancePolicy::getTargetType, 2));

        Map<Integer, List<AttendanceRawRecord>> staffRecordMap = attendanceRawRecordList.stream()
                .filter(record -> record.getCheckTime().startsWith(yesterday))
                .collect(Collectors.groupingBy(AttendanceRawRecord::getStaffId));

        Map<Integer, AttendanceShift> staffShiftMap = new HashMap<>();
        for (AttendanceShift shift : attendanceShiftList) {
            if (StrUtil.isBlank(shift.getStaffIds())) {
                continue;
            }
            String[] staffIdArray = shift.getStaffIds().split(",");
            for (String id : staffIdArray) {
                try {
                    Integer staffId = Integer.parseInt(id.trim());
                    if (!staffShiftMap.containsKey(staffId)) {
                        staffShiftMap.put(staffId, shift);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

        List<AttendanceSummary> toAddList = new ArrayList<>();

        for (StaffInfo staff : staffInfoList) {
            AttendanceShift shift = staffShiftMap.get(staff.getId());
            if (shift == null) {
                continue;
            }

            List<AttendanceRawRecord> records = staffRecordMap.getOrDefault(staff.getId(), new ArrayList<>());

            AttendanceRawRecord checkInRecord = records.stream()
                    .filter(r -> r.getCheckType() == 1)
                    .min((r1, r2) -> r1.getCheckTime().compareTo(r2.getCheckTime()))
                    .orElse(null);

            AttendanceRawRecord checkOutRecord = records.stream()
                    .filter(r -> r.getCheckType() == 2)
                    .max((r1, r2) -> r1.getCheckTime().compareTo(r2.getCheckTime()))
                    .orElse(null);

            AttendanceSummary summary = new AttendanceSummary();
            summary.setStaffId(staff.getId());
            summary.setWorkDate(yesterday);
            String startTime = yesterday + " " + shift.getStartTime();
            String endTime = yesterday + " " + shift.getEndTime();

            if (shift.getIsCrossDay() != null && shift.getIsCrossDay() == 1) {
                String nextDay = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(yesterday), 1));
                endTime = nextDay + " " + shift.getEndTime();
            }

            summary.setStartTime(startTime);
            summary.setEndTime(endTime);

            LocalDateTime startDateTime = LocalDateTime.parse(startTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endDateTime = LocalDateTime.parse(endTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            int allowLate = shift.getAllowLate() != null ? shift.getAllowLate() : 0;

            int resultStatus = 1;
            Integer lateMinutes = 0;
            Integer earlyMinutes = 0;
            Integer isException = 0;

            if (checkInRecord == null) {
                resultStatus = 4;
                isException = 1;
            } else {
                LocalDateTime checkInDateTime = LocalDateTime.parse(checkInRecord.getCheckTime(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime allowLateDateTime = startDateTime.plusMinutes(allowLate);

                if (checkInDateTime.isAfter(startDateTime)) {
                    lateMinutes = (int) java.time.Duration.between(startDateTime, checkInDateTime).toMinutes();

                    if (checkInDateTime.isAfter(allowLateDateTime)) {
                        resultStatus = 2;
                        isException = 1;
                    }
                }
            }

            if (checkOutRecord == null) {
                if (resultStatus == 4) {
                    resultStatus = 5;
                } else {
                    resultStatus = 4;
                }
                isException = 1;
            } else {
                LocalDateTime checkOutDateTime = LocalDateTime.parse(checkOutRecord.getCheckTime(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                if (checkOutDateTime.isBefore(endDateTime)) {
                    earlyMinutes = (int) java.time.Duration.between(checkOutDateTime, endDateTime).toMinutes();
                    resultStatus = 3;
                    isException = 1;
                }
            }

            Integer otMinutes = 0;
            BigDecimal allowanceAmount = BigDecimal.ZERO;

            if (checkOutRecord != null && checkInRecord != null) {
                LocalDateTime checkOutDateTime = LocalDateTime.parse(checkOutRecord.getCheckTime(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                if (checkOutDateTime.isAfter(endDateTime)) {
                    otMinutes = (int) java.time.Duration.between(endDateTime, checkOutDateTime).toMinutes();

                    if (attendancePolicyList != null && !attendancePolicyList.isEmpty()) {
                        for (AttendancePolicy policy : attendancePolicyList) {
                            if (policy.getOvertimeRule() != null) {
                                try {
                                    com.alibaba.fastjson.JSONObject overtimeRule = com.alibaba.fastjson.JSON.parseObject(policy.getOvertimeRule());
                                    int startAfter = overtimeRule.getIntValue("start_after");

                                    if (otMinutes > startAfter) {
                                        String unit = overtimeRule.getString("unit");
                                        if ("hour".equals(unit)) {
                                            otMinutes = otMinutes / 60;
                                        }

                                        if (shift.getNightAllowance() != null && !shift.getNightAllowance().isEmpty()) {
                                            allowanceAmount = new BigDecimal(shift.getNightAllowance());
                                        }
                                    } else {
                                        otMinutes = 0;
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
            }

            if (resultStatus == 1 && otMinutes > 0) {
                resultStatus = 6;
            }

            summary.setResultStatus(resultStatus);
            summary.setLateMinutes(lateMinutes);
            summary.setEarlyMinutes(earlyMinutes);
            summary.setOtMinutes(otMinutes);
            summary.setAllowanceAmount(allowanceAmount);
            summary.setIsException(isException);

            toAddList.add(summary);
        }

        if (!toAddList.isEmpty()) {
            return this.saveBatch(toAddList);
        }
        return false;
    }

    @Override
    public boolean generateAttendanceSummaryFix() {
        String firstDayOfMonth = DateUtil.formatDate(DateUtil.beginOfMonth(new Date()));
        String lastDayOfMonth = DateUtil.formatDate(DateUtil.endOfMonth(new Date()));

        List<StaffInfo> staffInfoList = staffInfoService.list();
        if (staffInfoList == null || staffInfoList.isEmpty()) {
            return false;
        }

        this.remove(Wrappers.<AttendanceSummary>lambdaQuery()
                .ge(AttendanceSummary::getWorkDate, firstDayOfMonth)
                .le(AttendanceSummary::getWorkDate, lastDayOfMonth));

        List<AttendanceRawRecord> attendanceRawRecordList = attendanceRawRecordService.list(Wrappers.<AttendanceRawRecord>lambdaQuery()
                .ge(AttendanceRawRecord::getCheckTime, firstDayOfMonth + " 00:00:00")
                .le(AttendanceRawRecord::getCheckTime, lastDayOfMonth + " 23:59:59"));

        List<AttendanceShift> attendanceShiftList = attendanceShiftService.list();

        List<AttendancePolicy> attendancePolicyList = attendancePolicyService.list(Wrappers.<AttendancePolicy>lambdaQuery()
                .eq(AttendancePolicy::getTargetType, 2));

        Map<Integer, List<AttendanceRawRecord>> staffRecordMap = attendanceRawRecordList.stream()
                .filter(record -> record.getCheckTime().compareTo(firstDayOfMonth + " 00:00:00") >= 0
                        && record.getCheckTime().compareTo(lastDayOfMonth + " 23:59:59") <= 0)
                .collect(Collectors.groupingBy(AttendanceRawRecord::getStaffId));

        Map<Integer, AttendanceShift> staffShiftMap = new HashMap<>();
        for (AttendanceShift shift : attendanceShiftList) {
            if (StrUtil.isBlank(shift.getStaffIds())) {
                continue;
            }
            String[] staffIdArray = shift.getStaffIds().split(",");
            for (String id : staffIdArray) {
                try {
                    Integer staffId = Integer.parseInt(id.trim());
                    if (!staffShiftMap.containsKey(staffId)) {
                        staffShiftMap.put(staffId, shift);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

        List<AttendanceSummary> toAddList = new ArrayList<>();

        int daysInMonth = DateUtil.lengthOfMonth(DateUtil.month(new Date()) + 1, DateUtil.isLeapYear(DateUtil.year(new Date())));

        for (StaffInfo staff : staffInfoList) {
            AttendanceShift shift = staffShiftMap.get(staff.getId());
            if (shift == null) {
                continue;
            }

            for (int day = 1; day <= daysInMonth; day++) {
                String currentDate = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(firstDayOfMonth), day - 1));
                List<AttendanceRawRecord> records = staffRecordMap.entrySet().stream()
                        .filter(entry -> entry.getKey().equals(staff.getId()))
                        .flatMap(entry -> entry.getValue().stream())
                        .filter(record -> record.getCheckTime().startsWith(currentDate))
                        .collect(Collectors.toList());

                AttendanceRawRecord checkInRecord = records.stream()
                        .filter(r -> r.getCheckType() == 1)
                        .min((r1, r2) -> r1.getCheckTime().compareTo(r2.getCheckTime()))
                        .orElse(null);

                AttendanceRawRecord checkOutRecord = records.stream()
                        .filter(r -> r.getCheckType() == 2)
                        .max((r1, r2) -> r1.getCheckTime().compareTo(r2.getCheckTime()))
                        .orElse(null);

                AttendanceSummary summary = new AttendanceSummary();
                summary.setStaffId(staff.getId());
                summary.setWorkDate(currentDate);
                String startTime = currentDate + " " + shift.getStartTime();
                String endTime = currentDate + " " + shift.getEndTime();

                LocalTime shiftStartTime = LocalTime.parse(shift.getStartTime());
                LocalTime shiftEndTime = LocalTime.parse(shift.getEndTime());

                if (shiftStartTime.isAfter(shiftEndTime)) {
                    String nextDay = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(currentDate), 1));
                    endTime = nextDay + " " + shift.getEndTime();
                }

                summary.setStartTime(startTime);
                summary.setEndTime(endTime);

                LocalDateTime startDateTime = LocalDateTime.parse(startTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime endDateTime = LocalDateTime.parse(endTime, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                int allowLate = shift.getAllowLate() != null ? shift.getAllowLate() : 0;

                int resultStatus = 1;
                Integer lateMinutes = 0;
                Integer earlyMinutes = 0;
                Integer isException = 0;

                if (checkInRecord == null) {
                    resultStatus = 4;
                    isException = 1;
                } else {
                    LocalDateTime checkInDateTime = LocalDateTime.parse(checkInRecord.getCheckTime(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime allowLateDateTime = startDateTime.plusMinutes(allowLate);

                    if (checkInDateTime.isAfter(startDateTime)) {
                        lateMinutes = (int) java.time.Duration.between(startDateTime, checkInDateTime).toMinutes();

                        if (checkInDateTime.isAfter(allowLateDateTime)) {
                            resultStatus = 2;
                            isException = 1;
                        }
                    }
                }

                if (checkOutRecord == null) {
                    if (resultStatus == 4) {
                        resultStatus = 5;
                    } else {
                        resultStatus = 4;
                    }
                    isException = 1;
                } else {
                    LocalDateTime checkOutDateTime = LocalDateTime.parse(checkOutRecord.getCheckTime(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    if (checkOutDateTime.isBefore(endDateTime)) {
                        earlyMinutes = (int) java.time.Duration.between(checkOutDateTime, endDateTime).toMinutes();
                        resultStatus = 3;
                        isException = 1;
                    }
                }

                Integer otMinutes = 0;
                BigDecimal allowanceAmount = BigDecimal.ZERO;

                if (checkOutRecord != null && checkInRecord != null) {
                    LocalDateTime checkOutDateTime = LocalDateTime.parse(checkOutRecord.getCheckTime(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    if (checkOutDateTime.isAfter(endDateTime)) {
                        otMinutes = (int) java.time.Duration.between(endDateTime, checkOutDateTime).toMinutes();

                        if (attendancePolicyList != null && !attendancePolicyList.isEmpty()) {
                            for (AttendancePolicy policy : attendancePolicyList) {
                                if (policy.getOvertimeRule() != null) {
                                    try {
                                        com.alibaba.fastjson.JSONObject overtimeRule = com.alibaba.fastjson.JSON.parseObject(policy.getOvertimeRule());
                                        int startAfter = overtimeRule.getIntValue("start_after");

                                        if (otMinutes > startAfter) {
                                            String unit = overtimeRule.getString("unit");
                                            if ("hour".equals(unit)) {
                                                otMinutes = otMinutes / 60;
                                            }

                                            if (shift.getNightAllowance() != null && !shift.getNightAllowance().isEmpty()) {
                                                allowanceAmount = new BigDecimal(shift.getNightAllowance());
                                            }
                                        } else {
                                            otMinutes = 0;
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    }
                }

                if (resultStatus == 1 && otMinutes > 0) {
                    resultStatus = 6;
                }

                summary.setResultStatus(resultStatus);
                summary.setLateMinutes(lateMinutes);
                summary.setEarlyMinutes(earlyMinutes);
                summary.setOtMinutes(otMinutes);
                summary.setAllowanceAmount(allowanceAmount);
                summary.setIsException(isException);

                toAddList.add(summary);
            }
        }

        if (!toAddList.isEmpty()) {
            return this.saveBatch(toAddList);
        }
        return false;
    }
}
