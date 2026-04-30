
<template>
  <a-card :bordered="false" class="card-area">
    <!-- 月份选择器 -->
    <div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center;">
      <a-space>
        <a-month-picker
          v-model="selectedMonth"
          format="YYYY-MM"
          placeholder="请选择月份"
          @change="handleMonthChange"
        />
        <a-button type="primary" @click="queryDashboard">
          <a-icon type="search" /> 查询
        </a-button>
      </a-space>
      <a-space>
        <a-statistic title="总人数" :value="staffScheduleData.length" suffix="人" />
        <a-statistic title="当月天数" :value="daysInMonth" suffix="天" />
      </a-space>
    </div>

    <!-- 员工排班卡片列表 -->
    <a-spin :spinning="loading">
      <a-row :gutter="16">
        <a-col
          v-for="staff in staffScheduleData"
          :key="staff.staffId"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"          style="margin-bottom: 16px;"
        >
          <a-card :hoverable="true" size="small">
            <a-card-meta>
              <div slot="title" style="display: flex; align-items: center;">
                <div>
                  <div style="font-weight: 600; font-size: 16px;">{{ staff.staffName }}</div>
                  <div style="color: #999; font-size: 12px; margin-top: 4px;">
                    <a-tag color="blue" size="small">{{ staff.shiftName }}</a-tag>
                  </div>
                </div>
              </div>
            </a-card-meta>

            <!-- 班次时间信息 -->
            <div style="margin-top: 12px; padding: 12px; background: #f5f7fa; border-radius: 4px;">
              <div style="font-size: 12px; color: #666; margin-bottom: 8px;">
                <a-icon type="clock-circle" /> 班次时间
              </div>
              <div v-if="staff.dailySchedules && staff.dailySchedules.length > 0 && staff.shiftName !== '未排班'" style="font-size: 14px; font-weight: 500;">
                {{ formatTime(staff.dailySchedules[0].startTime) }} - {{ formatTime(staff.dailySchedules[0].endTime) }}
              </div>
              <div v-else style="color: #999;">暂无排班</div>
<!--              <div v-if="staff.dailySchedules && staff.dailySchedules.length > 0 && staff.dailySchedules[0].isCrossDay === 1"-->
<!--                   style="margin-left: 4px;display: inline">-->
<!--                <a-tag color="red" size="small">跨天</a-tag>-->
<!--              </div>-->
            </div>

            <!-- 排班日历 -->
            <div style="margin-top: 16px;">
              <div style="font-size: 12px; color: #666; margin-bottom: 8px;">
                <a-icon type="calendar" /> 排班日历
              </div>
              <div class="schedule-calendar">
                <div
                  v-for="day in daysInMonth"
                  :key="day"
                  class="calendar-day"
                  :class="getDayClass(staff, day)"
                  :title="getDayTitle(staff, day)"
                >
                  {{ day }}
                </div>
              </div>
            </div>

            <!-- 图例说明 -->
            <div style="margin-top: 12px; padding-top: 12px; border-top: 1px solid #f0f0f0;">
              <div style="font-size: 11px; color: #999; display: flex; flex-wrap: wrap; gap: 8px;">
                <span><span class="legend-dot normal"></span> 正常</span>
                <span><span class="legend-dot cross"></span> 跨天</span>
                <span><span class="legend-dot empty"></span> 未排</span>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
  </a-card>
</template>

<script>import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: "Dashboard",
  data () {
    return {
      loading: false,
      selectedMonth: moment(),
      staffScheduleData: [],
      daysInMonth: 30
    }
  },
  mounted () {
    this.queryDashboard()
  },
  methods: {
    moment,
    handleMonthChange () {
      this.queryDashboard()
    },
    queryDashboard () {
      this.loading = true
      const monthStr = this.selectedMonth.format('YYYY-MM')
      this.$get('/cos/attendance-shift/queryDashboard', { month: monthStr }).then(res => {
        this.staffScheduleData = res.data.staffScheduleData || []
        this.daysInMonth = res.data.daysInMonth || 30
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    formatTime (timeStr) {
      if (!timeStr) return '- -'
      // 从 "2026-04-01 09:00:00" 中提取 "09:00"
      const parts = timeStr.split(' ')
      if (parts.length >= 2) {
        const timePart = parts[1] // "09:00:00"
        return timePart.substring(0, 5) // "09:00"
      }
      return timeStr
    },
    getDayClass (staff, day) {
      if (!staff.dailySchedules || !Array.isArray(staff.dailySchedules)) {
        return 'empty'
      }

      const record = staff.dailySchedules.find(r => r.day === day)
      if (!record) {
        return 'empty'
      }

      if (record.startTime === null || record.endTime === null) {
        return 'empty'
      }

      if (record.isCrossDay === 1) {
        return 'cross'
      }

      return 'normal'
    },
    getDayTitle (staff, day) {
      if (!staff.dailySchedules || !Array.isArray(staff.dailySchedules)) {
        return '未排班'
      }

      const record = staff.dailySchedules.find(r => r.day === day)
      if (!record) {
        return '未排班'
      }

      const date = record.date ? moment(record.date).format('YYYY-MM-DD') : ''
      const time = this.formatTime(record.startTime) + ' - ' + this.formatTime(record.endTime)
      const cross = record.isCrossDay === 1 ? ' (跨天)' : ''

      return date + '\n' + time + cross
    }
  },
}
</script>

<style scoped>.schedule-calendar {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.calendar-day.normal {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.calendar-day.normal:hover {
  background: #bae7ff;
  transform: scale(1.1);
}

.calendar-day.cross {
  background: #fff1f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.calendar-day.cross:hover {
  background: #ffccc7;
  transform: scale(1.1);
}

.calendar-day.empty {
  background: #fafafa;
  color: #d9d9d9;
  border: 1px dashed #d9d9d9;
}

.legend-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 4px;
}

.legend-dot.normal {
  background: #1890ff;
}

.legend-dot.cross {
  background: #ff4d4f;
}

.legend-dot.empty {
  background: #d9d9d9;
}
</style>
