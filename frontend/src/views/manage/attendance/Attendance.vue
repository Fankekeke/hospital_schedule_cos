
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
        <a-button type="primary" @click="queryAttendance">
          <a-icon type="search" /> 查询
        </a-button>
      </a-space>
      <a-space>
        <a-statistic title="总人数" :value="staffList.length" suffix="人" />
        <a-statistic title="正常天数" :value="totalNormalDays" suffix="天" value-style="color: #52c41a" />
        <a-statistic title="异常天数" :value="totalExceptionDays" suffix="天" value-style="color: #ff4d4f" />
      </a-space>
    </div>

    <!-- 员工考勤卡片列表 -->
    <a-spin :spinning="loading">
      <a-row :gutter="16">
        <a-col
          v-for="staff in attendanceData"
          :key="staff.staffId"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"          style="margin-bottom: 16px;"
        >
          <a-card :hoverable="true" size="small">
            <a-card-meta>
              <div slot="title" style="display: flex; align-items: center;">
                <a-avatar
                  size="large"
                  :src="getStaffImage(staff.staffId)"
                  icon="user"                  style="margin-right: 10px;"
                />
                <div>
                  <div style="font-weight: 600;">{{ staff.staffName }}</div>
                  <div style="color: #999; font-size: 12px;">{{ getStaffInfo(staff.staffId) }}</div>
                </div>
              </div>
            </a-card-meta>

            <!-- 考勤统计 -->
            <div style="margin-top: 16px;">
              <a-row :gutter="8">
                <a-col :span="8">
                  <div class="stat-item">
                    <div class="stat-value normal">{{ getStatCount(staff, 1) }}</div>
                    <div class="stat-label">正常</div>
                  </div>
                </a-col>
                <a-col :span="8">
                  <div class="stat-item">
                    <div class="stat-value late">{{ getStatCount(staff, 2) }}</div>
                    <div class="stat-label">迟到</div>
                  </div>
                </a-col>
                <a-col :span="8">
                  <div class="stat-item">
                    <div class="stat-value early">{{ getStatCount(staff, 3) }}</div>
                    <div class="stat-label">早退</div>
                  </div>
                </a-col>
              </a-row>
              <a-row :gutter="8" style="margin-top: 12px;">
                <a-col :span="8">
                  <div class="stat-item">
                    <div class="stat-value missing">{{ getStatCount(staff, 4) }}</div>
                    <div class="stat-label">缺卡</div>
                  </div>
                </a-col>
                <a-col :span="8">
                  <div class="stat-item">
                    <div class="stat-value absent">{{ getStatCount(staff, 5) }}</div>
                    <div class="stat-label">旷工</div>
                  </div>
                </a-col>
                <a-col :span="8">
                  <div class="stat-item">
                    <div class="stat-value overtime">{{ getStatCount(staff, 6) }}</div>
                    <div class="stat-label">加班</div>
                  </div>
                </a-col>
              </a-row>
            </div>

            <!-- 查看详情按钮 -->
            <div style="margin-top: 16px; text-align: center;">
              <a-button type="link" size="small" @click="viewDetail(staff)">
                <a-icon type="eye" /> 查看明细
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>

    <!-- 详情弹窗 -->
    <a-modal
      v-model="detailModalVisible"
      :title="(currentStaff ? currentStaff.staffName : '') + ' - ' + (selectedMonth ? selectedMonth.format('YYYY年MM月') : '') + ' 考勤明细'"
      :width="1200"
      :footer="null"
    >
      <a-table
        :columns="detailColumns"
        :data-source="currentStaff && currentStaff.dailyRecords ? currentStaff.dailyRecords : []"
        :pagination="false"
        size="small"
        :scroll="{ y: 400 }"
      >
        <template slot="resultStatus" slot-scope="text">
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
      </a-table>
    </a-modal>
  </a-card>
</template>

<script>import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: "Attendance",
  data () {
    return {
      loading: false,
      selectedMonth: moment(),
      attendanceData: [],
      staffList: [],
      detailModalVisible: false,
      currentStaff: null,
      detailColumns: [
        {
          title: '日期',
          dataIndex: 'date',
          width: 120
        },
        {
          title: '星期',
          dataIndex: 'day',
          width: 80,
          customRender: (text, record) => {
            const date = moment(record.date)
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
          customRender: (text) => text > 0 ? text : '- -'
        },
        {
          title: '早退(分钟)',
          dataIndex: 'earlyMinutes',
          width: 100,
          customRender: (text) => text > 0 ? text : '- -'
        },
        {
          title: '加班(分钟)',
          dataIndex: 'otMinutes',
          width: 100,
          customRender: (text) => text > 0 ? text : '- -'
        },
        {
          title: '是否异常',
          dataIndex: 'isException',
          scopedSlots: { customRender: 'isException' },
          width: 100
        }
      ]
    }
  },
  computed: {
    totalNormalDays () {
      let total = 0
      this.attendanceData.forEach(staff => {
        staff.dailyRecords.forEach(record => {
          if (record.resultStatus === 1) total++
        })
      })
      return total
    },
    totalExceptionDays () {
      let total = 0
      this.attendanceData.forEach(staff => {
        staff.dailyRecords.forEach(record => {
          if (record.isException === 1) total++
        })
      })
      return total
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
      this.$get('/cos/attendance-shift/queryAttendance', { month: monthStr }).then(res => {
        this.attendanceData = res.data.attendanceData || []
        this.staffList = res.data.staffList || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getStaffImage (staffId) {
      const staff = this.staffList.find(s => s.id === staffId)
      return staff && staff.images ? 'http://127.0.0.1:9527/imagesWeb/' + staff.images : null
    },
    getStaffInfo (staffId) {
      const staff = this.staffList.find(s => s.id === staffId)
      if (!staff) return '- -'
      return `${staff.deptName} · ${staff.positionName}`
    },
    getStatCount (staff, status) {
      return staff.dailyRecords.filter(r => r.resultStatus === status).length
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
      return statusMap[status] || '- -'
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
    viewDetail (staff) {
      this.currentStaff = staff
      this.detailModalVisible = true
    }
  },
}
</script>

<style scoped>.stat-item {
  text-align: center;
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  line-height: 1.2;
}

.stat-value.normal {
  color: #52c41a;
}

.stat-value.late {
  color: #fa8c16;
}

.stat-value.early {
  color: #13c2c2;
}

.stat-value.missing {
  color: #ff4d4f;
}

.stat-value.absent {
  color: #722ed1;
}

.stat-value.overtime {
  color: #1890ff;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}
</style>
