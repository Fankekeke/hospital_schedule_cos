
<template>
  <div class="attendance-container">
    <a-card :bordered="false" class="attendance-card">
      <!-- 顶部统计区域 -->
      <div class="header-section">
        <div class="title-area">
          <a-icon type="schedule" class="title-icon" />
          <h2 class="page-title">我的考勤</h2>
        </div>

        <div class="control-area">
          <a-space size="large">
            <a-month-picker
              v-model="selectedMonth"
              format="YYYY-MM"
              placeholder="请选择月份"
              @change="handleMonthChange"
              class="month-picker"
            />
            <a-button type="primary" @click="queryAttendance" class="query-btn">
              <a-icon type="search" /> 查询
            </a-button>
          </a-space>
        </div>
      </div>

      <!-- 统计卡片 -->
      <a-row :gutter="16" class="stats-row">
        <a-col :span="6">
          <a-card :bordered="false" class="stat-card stat-normal">
            <a-statistic
              title="正常天数"
              :value="normalDays"
              suffix="天"
              :value-style="{ color: '#52c41a' }"
            >
              <template #prefix>
                <a-icon type="check-circle" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card :bordered="false" class="stat-card stat-exception">
            <a-statistic
              title="异常天数"
              :value="exceptionDays"
              suffix="天"
              :value-style="{ color: '#ff4d4f' }"
            >
              <template #prefix>
                <a-icon type="exclamation-circle" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card :bordered="false" class="stat-card stat-overtime">
            <a-statistic
              title="加班时长"
              :value="totalOtMinutes"
              suffix="分钟"
              :value-style="{ color: '#1890ff' }"
            >
              <template #prefix>
                <a-icon type="clock-circle" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card :bordered="false" class="stat-card stat-total">
            <a-statistic
              title="应出勤天数"
              :value="workDays"
              suffix="天"
              :value-style="{ color: '#722ed1' }"
            >
              <template #prefix>
                <a-icon type="calendar" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>

      <!-- 日历视图 -->
      <div class="calendar-section">
        <h3 class="section-title">
          <a-icon type="calendar" /> 考勤日历
        </h3>
        <a-calendar :locale="calendarLocale" class="attendance-calendar">
          <template slot="dateCellRender" slot-scope="value">
            <div class="calendar-cell">
              <a-badge
                v-if="getDayStatus(value)"
                :status="getDayStatus(value).status"
                :text="getDayStatus(value).text"
              />
            </div>
          </template>
        </a-calendar>
      </div>

      <!-- 每日打卡详情表格 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="table" /> 打卡明细
        </h3>
        <a-table
          :columns="detailColumns"
          :data-source="dailyAttendanceList"
          :pagination="pagination"
          :loading="loading"
          row-key="id"
          class="detail-table"
          size="middle"
        >
          <template slot="resultStatus" slot-scope="text, record">
            <a-tag :color="getStatusColor(text)">
              {{ getStatusName(text) }}
            </a-tag>
          </template>

          <template slot="isException" slot-scope="text">
            <a-badge
              :status="text === 1 ? 'error' : 'success'"
              :text="text === 1 ? '异常' : '正常'"
            />
          </template>

          <template slot="checkRecords" slot-scope="text, record">
            <div v-if="record.checkRecords && record.checkRecords.length > 0">
              <div v-for="(rec, index) in record.checkRecords" :key="index" class="check-record-item">
                <a-tag :color="rec.checkType === 1 ? 'green' : 'blue'" size="small">
                  {{ rec.checkType === 1 ? '上班' : '下班' }}
                </a-tag>
                <span class="check-time">{{ rec.checkTime }}</span>
                <a-tag size="small">{{ getVerifyMode(rec.verifyMode) }}</a-tag>
              </div>
            </div>
            <span v-else class="no-record">无打卡记录</span>
          </template>

          <template slot="action" slot-scope="text, record">
            <a-button type="link" size="small" @click="viewDetail(record)">
              <a-icon type="eye" /> 详情
            </a-button>
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- 详情弹窗 -->
    <a-modal
      v-model="detailModalVisible"
      title="考勤详情"
      width="800px"
      :footer="null"
    >
      <div v-if="currentRecord" class="detail-content">
        <a-descriptions bordered :column="2" size="middle">
          <a-descriptions-item label="日期">
            {{ currentRecord.workDate }}
          </a-descriptions-item>
          <a-descriptions-item label="星期">
            {{ getWeekDay(currentRecord.workDate) }}
          </a-descriptions-item>
          <a-descriptions-item label="上班时间">
            {{ currentRecord.startTime || '- -' }}
          </a-descriptions-item>
          <a-descriptions-item label="下班时间">
            {{ currentRecord.endTime || '- -' }}
          </a-descriptions-item>
          <a-descriptions-item label="考勤状态">
            <a-tag :color="getStatusColor(currentRecord.resultStatus)">
              {{ getStatusName(currentRecord.resultStatus) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="是否异常">
            <a-badge
              :status="currentRecord.isException === 1 ? 'error' : 'success'"
              :text="currentRecord.isException === 1 ? '异常' : '正常'"
            />
          </a-descriptions-item>
          <a-descriptions-item label="迟到时长">
            {{ currentRecord.lateMinutes > 0 ? currentRecord.lateMinutes + ' 分钟' : '- -' }}
          </a-descriptions-item>
          <a-descriptions-item label="早退时长">
            {{ currentRecord.earlyMinutes > 0 ? currentRecord.earlyMinutes + ' 分钟' : '- -' }}
          </a-descriptions-item>
          <a-descriptions-item label="加班时长">
            {{ currentRecord.otMinutes > 0 ? currentRecord.otMinutes + ' 分钟' : '- -' }}
          </a-descriptions-item>
          <a-descriptions-item label="补贴金额">
            ¥{{ currentRecord.allowanceAmount || '0.00' }}
          </a-descriptions-item>
        </a-descriptions>

        <h4 class="subsection-title">打卡记录</h4>
        <a-timeline v-if="currentRecord.checkRecords && currentRecord.checkRecords.length > 0">
          <a-timeline-item
            v-for="(rec, index) in currentRecord.checkRecords"
            :key="index"
            :color="rec.checkType === 1 ? 'green' : 'blue'"
          >
            <p class="timeline-title">
              <a-tag :color="rec.checkType === 1 ? 'green' : 'blue'" size="small">
                {{ rec.checkType === 1 ? '上班打卡' : '下班打卡' }}
              </a-tag>
            </p>
            <p class="timeline-time">{{ rec.checkTime }}</p>
            <p class="timeline-info">打卡方式：{{ getVerifyMode(rec.verifyMode) }}</p>
          </a-timeline-item>
        </a-timeline>
        <a-empty v-else description="暂无打卡记录" />

        <div class="action-buttons">
          <a-divider />
          <h4 class="subsection-title">相关操作</h4>
          <a-space wrap>
            <a-button
              v-if="canSupplementCard"
              type="primary"
              icon="form"
              @click="showApplyModal(1)"
            >
              申请补卡
            </a-button>
            <a-button
              v-if="canApplyOvertime"
              type="primary"
              icon="clock-circle"
              @click="showApplyModal(4)"
            >
              加班认定
            </a-button>
            <a-button
              type="default"
              icon="calendar"
              @click="showApplyModal(2)"
            >
              请假申请
            </a-button>
            <a-button
              type="default"
              icon="swap"
              @click="showApplyModal(3)"
            >
              调班申请
            </a-button>
          </a-space>
        </div>
      </div>
    </a-modal>

    <!-- 统一申请弹窗 -->
    <a-modal
      v-model="applyModalVisible"
      :title="getApplyTitle()"
      width="600px"
      @ok="handleApplySubmit"
      @cancel="handleApplyCancel"
    >
      <a-form-model
        ref="applyForm"
        :model="applyForm"
        :rules="applyRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-model-item label="申请类型">
          <span>{{ getApplyTypeName() }}</span>
        </a-form-model-item>

        <a-form-model-item
          v-if="applyForm.applyType === 1"
          label="补卡类型"
          prop="checkType"
        >
          <a-radio-group v-model="applyForm.checkType">
            <a-radio :value="1">上班打卡</a-radio>
            <a-radio :value="2">下班打卡</a-radio>
          </a-radio-group>
        </a-form-model-item>

        <a-form-model-item label="开始时间" prop="startDatetime">
          <a-date-picker
            v-if="applyForm.applyType === 2 || applyForm.applyType === 3"
            v-model="applyForm.startDatetime"
            show-time            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择开始时间"
          />
          <a-time-picker
            v-else-if="applyForm.applyType === 1 || applyForm.applyType === 4"
            v-model="applyForm.startDatetime"            style="width: 100%"
            format="HH:mm:ss"
            placeholder="请选择时间"
          />
        </a-form-model-item>

        <a-form-model-item label="结束时间" prop="endDatetime">
          <a-date-picker
            v-if="applyForm.applyType === 2 || applyForm.applyType === 3"
            v-model="applyForm.endDatetime"
            show-time            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择结束时间"
          />
          <a-time-picker
            v-else-if="applyForm.applyType === 4"
            v-model="applyForm.endDatetime"            style="width: 100%"
            format="HH:mm:ss"
            placeholder="请选择时间"
          />
          <span v-else-if="applyForm.applyType === 1">-</span>
        </a-form-model-item>

        <a-form-model-item label="申请理由" prop="reason">
          <a-textarea
            v-model="applyForm.reason"
            :rows="4"
            placeholder="请输入申请理由"
          />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>import moment from 'moment'
import {mapState} from "vuex"
moment.locale('zh-cn')

export default {
  name: "Attendance",
  data () {
    return {
      loading: false,
      selectedMonth: moment(),
      attendanceData: null,
      detailModalVisible: false,
      currentRecord: null,
      calendarLocale: {
        lang: {
          locale: 'zh_CN',
          month: '月',
          year: '年',
          today: '今天',
          monthSelect: '选择月份',
          yearSelect: '选择年份',
          dateFormat: 'YYYY年M月D日',
          dateTimeFormat: 'YYYY年M月D日 HH时mm分ss秒',
          previousMonth: '上个月 (翻页上键)',
          nextMonth: '下个月 (翻页下键)',
          previousYear: '去年 (控制左键)',
          nextYear: '明年 (控制右键)'
        }
      },
      pagination: {
        pageSize: 10,
        showSizeChanger: true,
        showTotal: (total) => `共 ${total} 条`,
        pageSizeOptions: ['10', '20', '50']
      },
      detailColumns: [
        {
          title: '日期',
          dataIndex: 'workDate',
          width: 120,
          fixed: 'left'
        },
        {
          title: '星期',
          key: 'weekDay',
          width: 80,
          customRender: (text, record) => {
            const date = moment(record.workDate)
            return `周${['日', '一', '二', '三', '四', '五', '六'][date.day()]}`
          }
        },
        {
          title: '上班时间',
          dataIndex: 'startTime',
          width: 160
        },
        {
          title: '下班时间',
          dataIndex: 'endTime',
          width: 160
        },
        {
          title: '考勤状态',
          dataIndex: 'resultStatus',
          scopedSlots: { customRender: 'resultStatus' },
          width: 100
        },
        {
          title: '迟到(分钟)',
          dataIndex: 'lateMinutes',
          width: 100,
          customRender: (text) => text > 0 ? text : '-'
        },
        {
          title: '早退(分钟)',
          dataIndex: 'earlyMinutes',
          width: 100,
          customRender: (text) => text > 0 ? text : '-'
        },
        {
          title: '加班(分钟)',
          dataIndex: 'otMinutes',
          width: 100,
          customRender: (text) => text > 0 ? text : '-'
        },
        {
          title: '是否异常',
          dataIndex: 'isException',
          scopedSlots: { customRender: 'isException' },
          width: 100
        },
        {
          title: '打卡记录',
          dataIndex: 'checkRecords',
          scopedSlots: { customRender: 'checkRecords' },
          width: 250
        },
        {
          title: '操作',
          key: 'action',
          scopedSlots: { customRender: 'action' },
          width: 100,
          fixed: 'right'
        }
      ],

      applyModalVisible: false,
      applyForm: {
        applyType: 1,
        checkType: 1,
        startDatetime: null,
        endDatetime: null,
        reason: ''
      },
      applyRules: {
        checkType: [
          {
            required: true,
            message: '请选择补卡类型',
            trigger: 'change',
            validator: (rule, value, callback) => {
              if (this.applyForm.applyType === 1 && !value) {
                callback(new Error('请选择补卡类型'))
              } else {
                callback()
              }
            }
          }
        ],
        startDatetime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endDatetime: [
          {
            required: true,
            message: '请选择结束时间',
            trigger: 'change',
            validator: (rule, value, callback) => {
              if ((this.applyForm.applyType === 2 || this.applyForm.applyType === 3 || this.applyForm.applyType === 4) && !value) {
                callback(new Error('请选择结束时间'))
              } else {
                callback()
              }
            }
          }
        ],
        reason: [
          { required: true, message: '请输入申请理由', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    normalDays () {
      if (!this.attendanceData || !this.attendanceData.dailyAttendance) return 0
      return this.attendanceData.dailyAttendance.filter(day =>
        day.summary && day.summary.resultStatus === 1
      ).length
    },
    exceptionDays () {
      if (!this.attendanceData || !this.attendanceData.dailyAttendance) return 0
      return this.attendanceData.dailyAttendance.filter(day =>
        day.summary && day.summary.isException === 1
      ).length
    },
    totalOtMinutes () {
      if (!this.attendanceData || !this.attendanceData.dailyAttendance) return 0
      return this.attendanceData.dailyAttendance.reduce((sum, day) => {
        return sum + (day.summary ? day.summary.otMinutes : 0)
      }, 0)
    },
    workDays () {
      if (!this.attendanceData || !this.attendanceData.dailyAttendance) return 0
      return this.attendanceData.dailyAttendance.filter(day => day.summary !== null).length
    },
    dailyAttendanceList () {
      if (!this.attendanceData || !this.attendanceData.dailyAttendance) return []
      return this.attendanceData.dailyAttendance
        .filter(day => day.summary !== null)
        .map(day => ({
          id: day.summary.id,
          workDate: day.date,
          startTime: day.startTime,
          endTime: day.endTime,
          resultStatus: day.summary.resultStatus,
          lateMinutes: day.summary.lateMinutes,
          earlyMinutes: day.summary.earlyMinutes,
          otMinutes: day.summary.otMinutes,
          isException: day.summary.isException,
          allowanceAmount: day.summary.allowanceAmount,
          checkRecords: day.checkRecords || []
        }))
    },
    canSupplementCard () {
      if (!this.currentRecord) return false
      const hasCheckIn = this.currentRecord.checkRecords &&
        this.currentRecord.checkRecords.some(r => r.checkType === 1)
      const hasCheckOut = this.currentRecord.checkRecords &&
        this.currentRecord.checkRecords.some(r => r.checkType === 2)
      return !hasCheckIn || !hasCheckOut
    },
    canApplyOvertime () {
      if (!this.currentRecord) return false
      const hasCheckIn = this.currentRecord.checkRecords &&
        this.currentRecord.checkRecords.some(r => r.checkType === 1)
      const hasCheckOut = this.currentRecord.checkRecords &&
        this.currentRecord.checkRecords.some(r => r.checkType === 2)
      if (!hasCheckIn || !hasCheckOut) return false

      if (this.currentRecord.otMinutes && this.currentRecord.otMinutes > 0) {
        return true
      }

      if (this.currentRecord.startTime && this.currentRecord.endTime) {
        const checkIn = this.currentRecord.checkRecords.find(r => r.checkType === 1)
        const checkOut = this.currentRecord.checkRecords.find(r => r.checkType === 2)
        if (checkIn && checkOut) {
          const actualWorkMinutes = moment(checkOut.checkTime).diff(moment(checkIn.checkTime), 'minutes')
          const scheduledWorkMinutes = moment(this.currentRecord.endTime, 'HH:mm:ss')
            .diff(moment(this.currentRecord.startTime, 'HH:mm:ss'), 'minutes')
          return actualWorkMinutes > scheduledWorkMinutes + 30
        }
      }
      return false
    }
  },
  mounted () {
    this.queryAttendance()
  },
  methods: {
    moment,
    handleMonthChange () {
      this.queryAttendance()
    },
    queryAttendance () {
      this.loading = true
      const monthStr = this.selectedMonth.format('YYYY-MM')
      this.$get('/cos/attendance-shift/queryAttendanceByStaff', {
        date: monthStr,
        staffId: this.currentUser.userId
      }).then(res => {
        this.loading = false
        this.attendanceData = res.data
        console.log('考勤数据:', this.attendanceData)
      }).catch(() => {
        this.loading = false
      })
    },
    getDayStatus (value) {
      if (!this.attendanceData || !this.attendanceData.dailyAttendance) return null

      const dateStr = value.format('YYYY-MM-DD')
      const dayData = this.attendanceData.dailyAttendance.find(d => d.date === dateStr)

      if (!dayData || !dayData.summary) return null

      const statusMap = {
        1: { status: 'success', text: '正常' },
        2: { status: 'warning', text: '迟到' },
        3: { status: 'processing', text: '早退' },
        4: { status: 'error', text: '缺卡' },
        5: { status: 'error', text: '旷工' },
        6: { status: 'success', text: '加班' }
      }

      return statusMap[dayData.summary.resultStatus] || null
    },
    getStatusName (status) {
      const statusMap = {
        1: '正常',
        2: '迟到',
        3: '早退',
        4: '缺卡',
        5: '旷工',
        6: '加班'
      }
      return statusMap[status] || '未知'
    },
    getStatusColor (status) {
      const colorMap = {
        1: 'green',
        2: 'orange',
        3: 'cyan',
        4: 'red',
        5: 'purple',
        6: 'blue'
      }
      return colorMap[status] || 'default'
    },
    getVerifyMode (mode) {
      const modeMap = {
        'FACE': '人脸识别',
        'FINGERPRINT': '指纹',
        'PASSWORD': '密码',
        'CARD': '刷卡'
      }
      return modeMap[mode] || mode || '未知'
    },
    getWeekDay (dateStr) {
      const date = moment(dateStr)
      return `周${['日', '一', '二', '三', '四', '五', '六'][date.day()]}`
    },
    viewDetail (record) {
      this.currentRecord = record
      this.detailModalVisible = true
    },

    showApplyModal (applyType) {
      this.applyForm.applyType = applyType
      this.applyForm.checkType = 1
      this.applyForm.startDatetime = null
      this.applyForm.endDatetime = null
      this.applyForm.reason = ''

      if (applyType === 1 && this.currentRecord) {
        const hasCheckIn = this.currentRecord.checkRecords &&
          this.currentRecord.checkRecords.some(r => r.checkType === 1)
        this.applyForm.checkType = hasCheckIn ? 2 : 1
      }

      this.applyModalVisible = true
    },

    getApplyTitle () {
      const titles = {
        1: '补卡申请',
        2: '请假申请',
        3: '调班申请',
        4: '加班认定申请'
      }
      return titles[this.applyForm.applyType] || '申请'
    },

    getApplyTypeName () {
      const types = {
        1: '补卡',
        2: '请假',
        3: '调班',
        4: '加班认定'
      }
      return types[this.applyForm.applyType] || '未知'
    },

    handleApplySubmit () {
      this.$refs.applyForm.validate(valid => {
        if (valid) {
          const params = {
            staffId: this.currentUser.userId,
            applyType: this.applyForm.applyType,
            reason: this.applyForm.reason
          }

          if (this.applyForm.applyType === 1) {
            params.checkType = this.applyForm.checkType
            params.startDatetime = this.currentRecord.workDate + ' ' +
              moment(this.applyForm.startDatetime).format('HH:mm:ss')
          } else if (this.applyForm.applyType === 4) {
            params.startDatetime = this.currentRecord.workDate + ' ' +
              moment(this.applyForm.startDatetime).format('HH:mm:ss')
            params.endDatetime = this.currentRecord.workDate + ' ' +
              moment(this.applyForm.endDatetime).format('HH:mm:ss')
          } else {
            params.startDatetime = moment(this.applyForm.startDatetime).format('YYYY-MM-DD HH:mm:ss')
            params.endDatetime = moment(this.applyForm.endDatetime).format('YYYY-MM-DD HH:mm:ss')
          }

          this.$post('/cos/attendance-application', params).then(() => {
            this.$message.success('申请提交成功')
            this.handleApplyCancel()
            this.queryAttendance()
          }).catch(() => {
            this.$message.error('申请提交失败')
          })
        }
      })
    },

    handleApplyCancel () {
      this.applyModalVisible = false
      this.$refs.applyForm.resetFields()
    }
  },
}
</script>

<style scoped>.attendance-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf9 100%);
  padding: 20px;
}

.attendance-card {
  border-radius: 2px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

/* 头部区域 */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #ecf0f1;
}

.title-area {
  display: flex;
  align-items: center;
}

.title-icon {
  font-size: 28px;
  color: #3498db;
  margin-right: 12px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.control-area {
  display: flex;
  align-items: center;
}

.month-picker {
  width: 160px;
}

.query-btn {
  padding: 0 24px;
  border-radius: 2px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.query-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 2px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.stat-normal {
  background: linear-gradient(135deg, #ffffff 0%, #f0f9f0 100%);
  border-left: 2px solid #52c41a;
}

.stat-exception {
  background: linear-gradient(135deg, #ffffff 0%, #fff0f0 100%);
  border-left: 2px solid #ff4d4f;
}

.stat-overtime {
  background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
  border-left: 2px solid #1890ff;
}

.stat-total {
  background: linear-gradient(135deg, #ffffff 0%, #f5f0ff 100%);
  border-left: 2px solid #722ed1;
}

/* 区块标题 */
.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 24px 0 16px 0;
  display: flex;
  align-items: center;
}

.section-title a-icon {
  margin-right: 8px;
  color: #3498db;
}

/* 日历样式 */
.calendar-section {
  margin-top: 24px;
}

.attendance-calendar {
  border-radius: 2px;
  overflow: hidden;
}

.calendar-cell {
  text-align: center;
  padding: 4px 0;
}

/* 详情表格 */
.detail-section {
  margin-top: 24px;
}

.detail-table {
  border-radius: 2px;
  overflow: hidden;
}

.check-record-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.check-time {
  font-size: 12px;
  color: #666;
}

.no-record {
  color: #999;
  font-style: italic;
}

/* 详情弹窗 */
.detail-content {
  padding: 16px 0;
}

.subsection-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 24px 0 16px 0;
}

.timeline-title {
  margin: 0 0 4px 0;
}

.timeline-time {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #333;
}

.timeline-info {
  margin: 0;
  font-size: 12px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .attendance-container {
    padding: 10px;
  }

  .header-section {
    flex-direction: column;
    gap: 16px;
  }

  .stats-row {
    margin-bottom: 16px;
  }

  .stat-card {
    margin-bottom: 12px;
  }
}
</style>
