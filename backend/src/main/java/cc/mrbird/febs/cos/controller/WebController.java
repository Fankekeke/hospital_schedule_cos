package cc.mrbird.febs.cos.controller;

import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cos/web")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebController {

    private final IAttendanceApplicationService attendanceApplicationService;

    private final IAttendancePolicyService attendancePolicyService;

    private final IAttendanceShiftService attendanceShiftService;

    private final IAttendanceRawRecordService attendanceRawRecordService;

    private final IAttendanceSummaryService attendanceSummaryService;

    @GetMapping("/anomalyFrequencyAnalysis")
    public R anomalyFrequencyAnalysis(String date, String dimension) {
        return R.ok(attendanceSummaryService.anomalyFrequencyAnalysis(date, dimension));
    }

}
