package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.AttendanceSummaryMapper;
import cc.mrbird.febs.cos.dao.StaffInfoMapper;
import cc.mrbird.febs.cos.dao.StaffScheduleMapper;
import cc.mrbird.febs.cos.entity.AttendanceShift;
import cc.mrbird.febs.cos.dao.AttendanceShiftMapper;
import cc.mrbird.febs.cos.entity.AttendanceSummary;
import cc.mrbird.febs.cos.entity.StaffInfo;
import cc.mrbird.febs.cos.entity.StaffSchedule;
import cc.mrbird.febs.cos.service.IAttendanceShiftService;
import cc.mrbird.febs.cos.service.IAttendanceSummaryService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AttendanceShiftServiceImpl extends ServiceImpl<AttendanceShiftMapper, AttendanceShift> implements IAttendanceShiftService {

    private final StaffInfoMapper staffInfoMapper;

    @Lazy
    private final AttendanceSummaryMapper attendanceSummaryService;

    private final StaffScheduleMapper staffScheduleMapper;


    /**
     * 分页获取班次定义信息
     *
     * @param page            分页对象
     * @param attendanceShift 班次定义信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<AttendanceShift> page, AttendanceShift attendanceShift) {
        IPage<LinkedHashMap<String, Object>> pageData = baseMapper.queryPage(page, attendanceShift);
        List<LinkedHashMap<String, Object>> records = pageData.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return pageData;
        }
        List<LinkedHashMap<String, Object>> staffInfoList = staffInfoMapper.selectStaffList(null);
        Map<Integer, LinkedHashMap<String, Object>> staffInfoMap = staffInfoList.stream().collect(
                Collectors.toMap(item -> Integer.parseInt(item.get("id").toString()), item -> item));

        records.forEach(item -> {
            String staffIds = (String) item.get("staffIds");
            if (staffIds != null && !staffIds.isEmpty()) {
                String[] staffIdArray = staffIds.split(",");
                List<LinkedHashMap<String, Object>> staffList = java.util.Arrays.stream(staffIdArray)
                        .map(String::trim)
                        .filter(id -> !id.isEmpty())
                        .map(id -> {
                            try {
                                return staffInfoMap.get(Integer.parseInt(id));
                            } catch (NumberFormatException e) {
                                return null;
                            }
                        })
                        .filter(staff -> staff != null)
                        .collect(Collectors.toList());
                item.put("staffList", staffList);
            } else {
                item.put("staffList", new java.util.ArrayList<>());
            }
        });
        return pageData;
    }

    /**
     * 获取考勤看板信息
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> queryAttendance(String date) {
        List<LinkedHashMap<String, Object>> staffInfoList = staffInfoMapper.selectStaffList(null);
        if (date == null || date.isEmpty()) {
            date = DateUtil.format(new Date(), "yyyy-MM");
        }
        String firstDayOfMonth = date + "-01";
        String lastDayOfMonth = DateUtil.formatDate(DateUtil.endOfMonth(DateUtil.parse(firstDayOfMonth)));

        int daysInMonth = DateUtil.lengthOfMonth(DateUtil.month(DateUtil.parse(firstDayOfMonth)) + 1,
                DateUtil.isLeapYear(DateUtil.year(DateUtil.parse(firstDayOfMonth))));

        List<StaffSchedule> staffScheduleList = staffScheduleMapper.selectList(Wrappers.<StaffSchedule>lambdaQuery());
        Map<Integer, List<StaffSchedule>> staffScheduleMap = staffScheduleList.stream()
                .collect(Collectors.groupingBy(StaffSchedule::getStaffId));

        List<AttendanceSummary> summaryList = attendanceSummaryService.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<AttendanceSummary>lambdaQuery()
                        .ge(AttendanceSummary::getWorkDate, firstDayOfMonth)
                        .le(AttendanceSummary::getWorkDate, lastDayOfMonth));

        Map<Integer, List<AttendanceSummary>> staffSummaryMap = summaryList.stream()
                .collect(Collectors.groupingBy(AttendanceSummary::getStaffId));

        List<AttendanceShift> shiftList = this.list();
        Map<Integer, AttendanceShift> staffShiftMap = new HashMap<>();
        for (AttendanceShift shift : shiftList) {
            if (shift.getStaffIds() != null && !shift.getStaffIds().isEmpty()) {
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
        }

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("staffList", staffInfoList);

        List<LinkedHashMap<String, Object>> attendanceData = new ArrayList<>();

        for (LinkedHashMap<String, Object> staff : staffInfoList) {
            Integer staffId = Integer.parseInt(staff.get("id").toString());
            String staffName = (String) staff.get("name");

            LinkedHashMap<String, Object> staffAttendance = new LinkedHashMap<>();
            staffAttendance.put("staffId", staffId);
            staffAttendance.put("staffName", staffName);

            AttendanceShift shift = staffShiftMap.get(staffId);

            List<AttendanceSummary> summaries = staffSummaryMap.getOrDefault(staffId, new ArrayList<>());
            Map<String, AttendanceSummary> summaryByDate = summaries.stream()
                    .collect(Collectors.toMap(AttendanceSummary::getWorkDate, s -> s, (s1, s2) -> s1));

            List<LinkedHashMap<String, Object>> dailyRecords = new ArrayList<>();

            for (int day = 1; day <= daysInMonth; day++) {
                String currentDate = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(firstDayOfMonth), day - 1));

                int currentDayOfWeek = DateUtil.dayOfWeek(DateUtil.parse(currentDate));
                String currentWeekDay = String.valueOf(currentDayOfWeek - 1);

                boolean shouldShowRecord = false;
                if (shift != null && shift.getWeekDay() != null && !shift.getWeekDay().isEmpty()) {
                    String[] weekDays = shift.getWeekDay().split(",");
                    for (String wd : weekDays) {
                        if (wd.trim().equals(currentWeekDay)) {
                            shouldShowRecord = true;
                            break;
                        }
                    }
                } else {
                    shouldShowRecord = true;
                }

                if (!shouldShowRecord) {
                    continue;
                }

                LinkedHashMap<String, Object> dailyRecord = new LinkedHashMap<>();
                dailyRecord.put("date", currentDate);
                dailyRecord.put("day", day);

                List<StaffSchedule> daySchedules = staffScheduleMap.getOrDefault(staffId, new ArrayList<>()).stream()
                        .filter(schedule -> currentDate.equals(schedule.getWorkDate()))
                        .collect(Collectors.toList());

                StaffSchedule temporarySchedule = daySchedules.stream()
                        .filter(schedule -> "2".equals(schedule.getType()))
                        .findFirst()
                        .orElse(null);

                AttendanceSummary summary = summaryByDate.get(currentDate);
                if (summary != null) {
                    dailyRecord.put("resultStatus", summary.getResultStatus());
                    dailyRecord.put("lateMinutes", summary.getLateMinutes());
                    dailyRecord.put("earlyMinutes", summary.getEarlyMinutes());
                    dailyRecord.put("otMinutes", summary.getOtMinutes());
                    dailyRecord.put("isException", summary.getIsException());
                    dailyRecord.put("startTime", summary.getStartTime());
                    dailyRecord.put("endTime", summary.getEndTime());
                } else {
                    dailyRecord.put("resultStatus", null);
                    dailyRecord.put("lateMinutes", 0);
                    dailyRecord.put("earlyMinutes", 0);
                    dailyRecord.put("otMinutes", 0);
                    dailyRecord.put("isException", 0);
                    dailyRecord.put("startTime", null);
                    dailyRecord.put("endTime", null);
                }

                if (temporarySchedule != null) {
                    dailyRecord.put("temporaryShiftName", temporarySchedule.getRemark());
                    dailyRecord.put("temporaryStartTime", temporarySchedule.getStartTime());
                    dailyRecord.put("temporaryEndTime", temporarySchedule.getEndTime());
                    dailyRecord.put("temporaryType", "临派");
                } else {
                    dailyRecord.put("temporaryShiftName", null);
                    dailyRecord.put("temporaryStartTime", null);
                    dailyRecord.put("temporaryEndTime", null);
                    dailyRecord.put("temporaryType", null);
                }

                dailyRecords.add(dailyRecord);
            }

            staffAttendance.put("dailyRecords", dailyRecords);
            attendanceData.add(staffAttendance);
        }

        result.put("attendanceData", attendanceData);
        result.put("month", date);
        result.put("daysInMonth", daysInMonth);

        return result;
    }

    /**
     * 获取看板信息
     *
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> queryDashboard(String date) {
        List<LinkedHashMap<String, Object>> staffInfoList = staffInfoMapper.selectStaffList(null);
        if (date == null || date.isEmpty()) {
            date = DateUtil.format(new Date(), "yyyy-MM");
        }

        String firstDayOfMonth = date + "-01";
        String lastDayOfMonth = DateUtil.formatDate(DateUtil.endOfMonth(DateUtil.parse(firstDayOfMonth)));

        int daysInMonth = DateUtil.lengthOfMonth(DateUtil.month(DateUtil.parse(firstDayOfMonth)) + 1,
                DateUtil.isLeapYear(DateUtil.year(DateUtil.parse(firstDayOfMonth))));

        List<AttendanceShift> shiftList = this.list();

        List<StaffSchedule> staffScheduleList = staffScheduleMapper.selectList(Wrappers.<StaffSchedule>lambdaQuery());
        Map<Integer, List<StaffSchedule>> staffScheduleMap = staffScheduleList.stream()
                .collect(Collectors.groupingBy(StaffSchedule::getStaffId));

        Map<Integer, AttendanceShift> staffShiftMap = new HashMap<>();
        for (AttendanceShift shift : shiftList) {
            if (shift.getStaffIds() != null && !shift.getStaffIds().isEmpty()) {
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
        }

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("month", date);
        result.put("daysInMonth", daysInMonth);

        List<LinkedHashMap<String, Object>> staffScheduleData = new ArrayList<>();

        for (LinkedHashMap<String, Object> staff : staffInfoList) {
            Integer staffId = Integer.parseInt(staff.get("id").toString());
            String staffName = (String) staff.get("name");

            AttendanceShift shift = staffShiftMap.get(staffId);

            LinkedHashMap<String, Object> staffSchedule = new LinkedHashMap<>();
            staffSchedule.put("staffId", staffId);
            staffSchedule.put("staffName", staffName);

            if (shift != null) {
                staffSchedule.put("shiftId", shift.getId());
                staffSchedule.put("shiftName", shift.getShiftName());
                staffSchedule.put("shiftType", shift.getShiftType());
            } else {
                staffSchedule.put("shiftId", null);
                staffSchedule.put("shiftName", "未排班");
                staffSchedule.put("shiftType", null);
            }

            List<LinkedHashMap<String, Object>> dailySchedules = new ArrayList<>();

            for (int day = 1; day <= daysInMonth; day++) {
                String currentDate = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(firstDayOfMonth), day - 1));

                int currentDayOfWeek = DateUtil.dayOfWeek(DateUtil.parse(currentDate));
                String currentWeekDay = String.valueOf(currentDayOfWeek - 1);

                List<StaffSchedule> daySchedules = staffScheduleMap.getOrDefault(staffId, new ArrayList<>()).stream()
                        .filter(schedule -> currentDate.equals(schedule.getWorkDate()))
                        .collect(Collectors.toList());

                StaffSchedule temporarySchedule = daySchedules.stream()
                        .filter(schedule -> "2".equals(schedule.getType()))
                        .findFirst()
                        .orElse(null);

                boolean shouldShowSchedule = false;
                if (temporarySchedule != null) {
                    shouldShowSchedule = true;
                } else if (shift != null && shift.getWeekDay() != null && !shift.getWeekDay().isEmpty()) {
                    String[] weekDays = shift.getWeekDay().split(",");
                    for (String wd : weekDays) {
                        if (wd.trim().equals(currentWeekDay)) {
                            shouldShowSchedule = true;
                            break;
                        }
                    }
                } else if (shift != null) {
                    shouldShowSchedule = true;
                }

                if (!shouldShowSchedule) {
                    continue;
                }

                LinkedHashMap<String, Object> dailySchedule = new LinkedHashMap<>();
                dailySchedule.put("date", currentDate);
                dailySchedule.put("day", day);

                if (temporarySchedule != null) {
                    String tempStartTime = temporarySchedule.getStartTime();
                    String tempEndTime = temporarySchedule.getEndTime();

                    String startTime = currentDate + " " + tempStartTime;
                    String endTime = currentDate + " " + tempEndTime;

                    if (tempStartTime != null && tempEndTime != null) {
                        LocalTime scheduleStartTime = LocalTime.parse(tempStartTime);
                        LocalTime scheduleEndTime = LocalTime.parse(tempEndTime);

                        if (scheduleStartTime.isAfter(scheduleEndTime)) {
                            String nextDay = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(currentDate), 1));
                            endTime = nextDay + " " + tempEndTime;
                            dailySchedule.put("isCrossDay", 1);
                        } else {
                            dailySchedule.put("isCrossDay", 0);
                        }
                    }

                    dailySchedule.put("startTime", startTime);
                    dailySchedule.put("endTime", endTime);
                    dailySchedule.put("shiftName", temporarySchedule.getRemark() != null ? temporarySchedule.getRemark() : "临派班次");
                    dailySchedule.put("scheduleType", "temporary");
                    dailySchedule.put("shiftId", temporarySchedule.getShiftId());
                    dailySchedule.put("campusId", temporarySchedule.getCampusId());
                } else if (shift != null) {
                    String startTime = currentDate + " " + shift.getStartTime();
                    String endTime = currentDate + " " + shift.getEndTime();

                    LocalTime shiftStartTime = LocalTime.parse(shift.getStartTime());
                    LocalTime shiftEndTime = LocalTime.parse(shift.getEndTime());

                    if (shiftStartTime.isAfter(shiftEndTime)) {
                        String nextDay = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(currentDate), 1));
                        endTime = nextDay + " " + shift.getEndTime();
                        dailySchedule.put("isCrossDay", 1);
                    } else {
                        dailySchedule.put("isCrossDay", 0);
                    }

                    dailySchedule.put("startTime", startTime);
                    dailySchedule.put("endTime", endTime);
                    dailySchedule.put("shiftName", shift.getShiftName());
                    dailySchedule.put("scheduleType", "regular");
                    dailySchedule.put("shiftId", shift.getId());
                    dailySchedule.put("campusId", null);
                } else {
                    dailySchedule.put("startTime", null);
                    dailySchedule.put("endTime", null);
                    dailySchedule.put("shiftName", "未排班");
                    dailySchedule.put("isCrossDay", 0);
                    dailySchedule.put("scheduleType", null);
                    dailySchedule.put("shiftId", null);
                    dailySchedule.put("campusId", null);
                }

                dailySchedules.add(dailySchedule);
            }

            staffSchedule.put("dailySchedules", dailySchedules);
            staffScheduleData.add(staffSchedule);
        }

        result.put("staffScheduleData", staffScheduleData);

        return result;
    }
}
