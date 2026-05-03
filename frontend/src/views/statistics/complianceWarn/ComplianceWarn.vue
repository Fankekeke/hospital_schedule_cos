
<template>
  <a-card :bordered="false" style="width: 100%">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="6" :sm="24">
            <a-form-item label="统计月份">
              <a-month-picker
                v-model="queryParam.date"
                placeholder="请选择月份"                style="width: 100%"
                format="YYYY-MM"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearch" icon="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="handleReset" icon="reload">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <a-row :gutter="16" v-if="summaryData" style="margin-bottom: 16px">
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card risk-total">
          <a-statistic
            title="风险人员总数"
            :value="summaryData.totalRiskStaff"
            suffix="人"
            :value-style="{ color: '#ff4d4f', fontWeight: 'bold' }"
          >
            <template #prefix>
              <a-icon type="warning" theme="filled" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card avg-risk">
          <a-statistic
            title="平均风险分"
            :value="summaryData.avgRiskScore"
            :precision="2"
            suffix="分"
            :value-style="{ color: '#faad14' }"
          >
            <template #prefix>
              <a-icon type="dashboard" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card immediate-action">
          <a-statistic
            title="需立即介入"
            :value="summaryData.immediateActionCount"
            suffix="人"
            :value-style="{ color: '#ff4d4f' }"
          >
            <template #prefix>
              <a-icon type="exclamation-circle" theme="filled" />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="stat-card highest-risk">
          <div style="text-align: center">
            <div style="font-size: 12px; color: #999; margin-bottom: 8px">⚠️ 最高风险人员</div>
            <div style="font-size: 18px; font-weight: bold; color: #ff4d4f; margin-bottom: 4px">
              {{ summaryData.highestRiskStaffName }}
            </div>
            <div style="font-size: 14px">
              <a-tag color="red">{{ summaryData.highestWarnLevel }}</a-tag>
            </div>
            <div style="font-size: 12px; color: #666; margin-top: 4px">
              风险分: <span style="color: #ff4d4f; font-weight: bold">{{ summaryData.highestRiskScore }}</span>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" v-if="summaryData" style="margin-bottom: 16px">
      <a-col :span="6">
        <a-card :bordered="false" class="level-card red-warning">
          <a-statistic
            title="红色预警"
            :value="summaryData.redWarningCount || 0"
            suffix="人"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px">严重违规</div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="level-card orange-warning">
          <a-statistic
            title="橙色预警"
            :value="summaryData.orangeWarningCount || 0"
            suffix="人"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px">高度风险</div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="level-card yellow-warning">
          <a-statistic
            title="黄色预警"
            :value="summaryData.yellowWarningCount || 0"
            suffix="人"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px">中度风险</div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bordered="false" class="level-card blue-warning">
          <a-statistic
            title="蓝色预警"
            :value="summaryData.blueWarningCount || 0"
            suffix="人"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px">轻度风险</div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" v-if="dataSource.length > 0" style="margin-bottom: 16px">
      <a-col :span="12">
        <a-card title="🚨 风险人员排行榜 TOP 10" :bordered="false">
          <apexchart
            type="bar"
            height="400"
            :options="riskRankChartOptions"
            :series="riskRankSeries"
          />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="预警等级分布" :bordered="false">
          <apexchart
            type="pie"
            height="400"
            :options="warnLevelDistChartOptions"
            :series="warnLevelDistSeries"
          />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" v-if="dataSource.length > 0" style="margin-bottom: 16px">
      <a-col :span="12">
        <a-card title="异常类型分布统计" :bordered="false">
          <apexchart
            type="bar"
            height="350"
            :options="exceptionTypeChartOptions"
            :series="exceptionTypeSeries"
          />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="异常率/缺勤率对比(%)" :bordered="false">
          <apexchart
            type="bar"
            height="350"
            :options="rateCompareChartOptions"
            :series="rateCompareSeries"
          />
        </a-card>
      </a-col>
    </a-row>

    <a-divider v-if="dataSource.length > 0" />

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      bordered
      :scroll="{ x: 1500 }"
      row-key="staffId"
      @change="handleTableChange"
      :expandedRowKeys="expandedRowKeys"
      @expand="handleExpand"
      :rowClassName="getRowClassName"
    >
      <template slot="rank" slot-scope="text, record">
        <div class="rank-badge" :class="getRankClass(text)">
          <span v-if="text === 1">🥇</span>
          <span v-else-if="text === 2">🥈</span>
          <span v-else-if="text === 3">🥉</span>
          <span v-else>{{ text }}</span>
        </div>
      </template>
      <template slot="staffInfo" slot-scope="text, record">
        <div>
          <div style="font-weight: bold">{{ record.staffName }}</div>
          <div style="font-size: 12px; color: #999">{{ record.code }}</div>
          <div style="font-size: 12px; color: #1890ff" v-if="record.phone && record.phone !== '-'">
            <a-icon type="phone" /> {{ record.phone }}
          </div>
        </div>
      </template>
      <template slot="exceptionRate" slot-scope="text">
        <a-progress
          :percent="text"
          :stroke-color="getExceptionColor(text)"
          status="active"
          size="small"
        />
      </template>
      <template slot="absentRate" slot-scope="text">
        <span :style="{ color: getAbsentColor(text), fontWeight: 'bold' }">
          {{ text }}%
        </span>
      </template>
      <template slot="consecutiveExceptionDays" slot-scope="text">
        <a-badge
          :count="text"
          :numberStyle="{ backgroundColor: text >= 3 ? '#ff4d4f' : '#faad14' }"
        />
      </template>
      <template slot="riskScore" slot-scope="text, record">
        <div>
          <a-progress
            :percent="text"
            :stroke-color="getRiskColor(record.riskLevel)"
            :show-info="false"
            size="small"
          />
          <div style="text-align: center; margin-top: 4px; font-weight: bold; color: #ff4d4f">
            {{ text }} 分
          </div>
        </div>
      </template>
      <template slot="warnLevel" slot-scope="text, record">
<!--        <a-badge-->
<!--          :status="getWarnStatus(text)"-->
<!--          :text="text"-->
<!--        />-->
        <div style="margin-top: 4px">
          <a-tag :color="getWarnTagColor(record.riskLevel)">{{ record.riskLevel }}</a-tag>
        </div>
      </template>
      <template slot="actionRequired" slot-scope="text">
        <a-tag :color="getActionColor(text)" style="font-weight: bold">
          {{ text }}
        </a-tag>
      </template>
      <template slot="hrSuggestions" slot-scope="text">
        <div v-for="(suggestion, index) in text" :key="index" style="margin-bottom: 4px; font-size: 12px">
          <a-icon type="check-circle" theme="twoTone" two-tone-color="#faad14" />
          {{ suggestion }}
        </div>
      </template>
      <template slot="operation" slot-scope="text, record">
        <a-space>
          <a-button type="link" size="small" @click="toggleExpand(record)">
            {{ expandedRowKeys.includes(record.staffId) ? '收起' : '展开' }}详情
          </a-button>
          <a-button type="link" size="small" icon="phone" v-if="record.phone && record.phone !== '-'" @click="handleCall(record)">
            联系
          </a-button>
        </a-space>
      </template>

<!--      <template slot="expandedRowRender" slot-scope="record">-->
<!--        <a-card title="最近异常记录 TOP 10" :bordered="false" size="small">-->
<!--          <a-table-->
<!--            :columns="exceptionColumns"-->
<!--            :data-source="record.recentExceptions"-->
<!--            :pagination="false"-->
<!--            size="small"-->
<!--            row-key="workDate"-->
<!--          >-->
<!--            <template slot="statusName" slot-scope="text">-->
<!--              <a-tag :color="getExceptionStatusColor(text)">-->
<!--                {{ text }}-->
<!--              </a-tag>-->
<!--            </template>-->
<!--            <template slot="lateMinutes" slot-scope="text">-->
<!--              <span v-if="text > 0" style="color: #faad14; font-weight: bold">{{ text }} 分钟</span>-->
<!--              <span v-else>-</span>-->
<!--            </template>-->
<!--            <template slot="earlyMinutes" slot-scope="text">-->
<!--              <span v-if="text > 0" style="color: #ff4d4f; font-weight: bold">{{ text }} 分钟</span>-->
<!--              <span v-else>-</span>-->
<!--            </template>-->
<!--            <template slot="otMinutes" slot-scope="text">-->
<!--              <span v-if="text > 0" style="color: #52c41a; font-weight: bold">{{ text }} 分钟</span>-->
<!--              <span v-else>-</span>-->
<!--            </template>-->
<!--          </a-table>-->
<!--        </a-card>-->
<!--      </template>-->
    </a-table>
  </a-card>
</template>

<script>import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: "ComplianceWarn",
  data () {
    return {
      loading: false,
      dataSource: [],
      summaryData: null,
      queryParam: {
        date: null,
        staffName: '',
        warnLevel: ''
      },
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`,
        total: 0
      },
      expandedRowKeys: [],
      riskRankChartOptions: {
        chart: {
          type: 'bar',
          toolbar: {
            show: true
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '60%',
            borderRadius: 4,
            dataLabels: {
              position: 'top'
            }
          }
        },
        dataLabels: {
          enabled: true,
          formatter: function (val) {
            return val + " 分"
          },
          offsetY: -20,
          style: {
            fontSize: '12px',
            colors: ['#304758']
          }
        },
        xaxis: {
          categories: []
        },
        yaxis: {
          title: {
            text: '风险分数'
          },
          max: 100
        },
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'light',
            type: 'horizontal',
            shadeIntensity: 0.25,
            inverseColors: true,
            opacityFrom: 1,
            opacityTo: 1,
            stops: [50, 0, 100, 100]
          }
        },
        colors: ['#ff4d4f'],
        tooltip: {
          y: {
            formatter: function (val) {
              return val + " 分"
            }
          }
        }
      },
      riskRankSeries: [
        {
          name: '风险分数',
          data: []
        }
      ],
      warnLevelDistChartOptions: {
        chart: {
          type: 'pie',
          toolbar: {
            show: true
          }
        },
        labels: [],
        colors: ['#ff4d4f', '#ff7a45', '#faad14', '#1890ff'],
        legend: {
          position: 'bottom'
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + " 人"
            }
          }
        }
      },
      warnLevelDistSeries: [],
      exceptionTypeChartOptions: {
        chart: {
          type: 'bar',
          toolbar: {
            show: true
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%',
            borderRadius: 4
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        yaxis: {
          title: {
            text: '次数/天数'
          }
        },
        fill: {
          opacity: 1
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val
            }
          }
        },
        colors: ['#faad14', '#ff4d4f', '#ff7875', '#8c8c8c'],
        legend: {
          position: 'top',
          horizontalAlign: 'center'
        }
      },
      exceptionTypeSeries: [
        {
          name: '迟到次数',
          data: []
        },
        {
          name: '早退次数',
          data: []
        },
        {
          name: '缺勤天数',
          data: []
        },
        {
          name: '加班次数',
          data: []
        }
      ],
      rateCompareChartOptions: {
        chart: {
          type: 'bar',
          toolbar: {
            show: true
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            columnWidth: '55%',
            borderRadius: 4
          }
        },
        dataLabels: {
          enabled: false
        },
        stroke: {
          show: true,
          width: 2,
          colors: ['transparent']
        },
        xaxis: {
          categories: []
        },
        yaxis: {
          title: {
            text: '百分比(%)'
          },
          max: 100
        },
        fill: {
          opacity: 1
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val.toFixed(2) + "%"
            }
          }
        },
        colors: ['#ff4d4f', '#faad14'],
        legend: {
          position: 'top',
          horizontalAlign: 'center'
        }
      },
      rateCompareSeries: [
        {
          name: '异常率',
          data: []
        },
        {
          name: '缺勤率',
          data: []
        }
      ],
      columns: [
        {
          title: '排名',
          dataIndex: 'rank',
          key: 'rank',
          width: 80,
          fixed: 'left',
          scopedSlots: { customRender: 'rank' }
        },
        {
          title: '人员信息',
          key: 'staffInfo',
          width: 180,
          fixed: 'left',
          scopedSlots: { customRender: 'staffInfo' }
        },
        {
          title: '应工作天数',
          dataIndex: 'totalWorkDays',
          key: 'totalWorkDays',
          width: 110
        },
        {
          title: '异常天数',
          dataIndex: 'exceptionDays',
          key: 'exceptionDays',
          width: 100
        },
        {
          title: '正常天数',
          dataIndex: 'normalDays',
          key: 'normalDays',
          width: 100
        },
        {
          title: '异常率',
          dataIndex: 'exceptionRate',
          key: 'exceptionRate',
          width: 130,
          scopedSlots: { customRender: 'exceptionRate' }
        },
        {
          title: '迟到次数',
          dataIndex: 'lateCount',
          key: 'lateCount',
          width: 100
        },
        {
          title: '早退次数',
          dataIndex: 'earlyLeaveCount',
          key: 'earlyLeaveCount',
          width: 100
        },
        {
          title: '缺勤天数',
          dataIndex: 'absentCount',
          key: 'absentCount',
          width: 100
        },
        {
          title: '缺勤率',
          dataIndex: 'absentRate',
          key: 'absentRate',
          width: 100,
          scopedSlots: { customRender: 'absentRate' }
        },
        {
          title: '连续异常天数',
          dataIndex: 'consecutiveExceptionDays',
          key: 'consecutiveExceptionDays',
          width: 130,
          scopedSlots: { customRender: 'consecutiveExceptionDays' }
        },
        {
          title: '风险分数',
          dataIndex: 'riskScore',
          key: 'riskScore',
          width: 150,
          scopedSlots: { customRender: 'riskScore' }
        },
        {
          title: '预警等级',
          key: 'warnLevel',
          width: 150,
          scopedSlots: { customRender: 'warnLevel' }
        },
        {
          title: '处理建议',
          dataIndex: 'hrSuggestions',
          key: 'hrSuggestions',
          width: 250,
          scopedSlots: { customRender: 'hrSuggestions' }
        },
        {
          title: '处理要求',
          dataIndex: 'actionRequired',
          key: 'actionRequired',
          width: 120,
          scopedSlots: { customRender: 'actionRequired' }
        },
      ],
      exceptionColumns: [
        {
          title: '工作日期',
          dataIndex: 'workDate',
          key: 'workDate',
          width: 120
        },
        {
          title: '异常状态',
          dataIndex: 'statusName',
          key: 'statusName',
          width: 100,
          scopedSlots: { customRender: 'statusName' }
        },
        {
          title: '迟到时长',
          dataIndex: 'lateMinutes',
          key: 'lateMinutes',
          width: 120,
          scopedSlots: { customRender: 'lateMinutes' }
        },
        {
          title: '早退时长',
          dataIndex: 'earlyMinutes',
          key: 'earlyMinutes',
          width: 120,
          scopedSlots: { customRender: 'earlyMinutes' }
        },
        {
          title: '加班时长',
          dataIndex: 'otMinutes',
          key: 'otMinutes',
          width: 120,
          scopedSlots: { customRender: 'otMinutes' }
        },
        {
          title: '审核备注',
          dataIndex: 'auditRemark',
          key: 'auditRemark',
          width: 200
        }
      ]
    }
  },
  mounted () {
    this.queryComplianceWarn()
  },
  methods: {
    queryComplianceWarn () {
      this.loading = true
      const params = {}

      if (this.queryParam.date) {
        params.date = this.queryParam.date.format('YYYY-MM')
      }

      if (this.queryParam.staffName) {
        params.staffName = this.queryParam.staffName
      }

      if (this.queryParam.warnLevel) {
        params.warnLevel = this.queryParam.warnLevel
      }

      this.$get('/cos/web/complianceWarn', params).then(res => {
        const data = res.data.data || []

        const summary = data.find(item => item.type === 'summary')
        this.summaryData = summary || null

        this.dataSource = data.filter(item => item.type !== 'summary')
        this.pagination.total = this.dataSource.length

        this.updateCharts()
        this.loading = false
      }).catch(() => {
        this.loading = false
        this.$message.error('查询异常')
      })
    },
    updateCharts () {
      const top10 = this.dataSource.slice(0, 10)
      const names = top10.map(item => item.staffName)
      const scores = top10.map(item => item.riskScore || 0)

      this.riskRankChartOptions.xaxis.categories = names
      this.riskRankSeries = [
        {
          name: '风险分数',
          data: scores
        }
      ]

      const warnLevelCounts = {}
      this.dataSource.forEach(item => {
        const level = item.warnLevel || '未知'
        warnLevelCounts[level] = (warnLevelCounts[level] || 0) + 1
      })

      this.warnLevelDistChartOptions.labels = Object.keys(warnLevelCounts)
      this.warnLevelDistSeries = Object.values(warnLevelCounts)

      const lateCounts = this.dataSource.map(item => item.lateCount || 0)
      const earlyLeaveCounts = this.dataSource.map(item => item.earlyLeaveCount || 0)
      const absentCounts = this.dataSource.map(item => item.absentCount || 0)
      const overtimeCounts = this.dataSource.map(item => item.overtimeCount || 0)

      this.exceptionTypeChartOptions.xaxis.categories = this.dataSource.map(item => item.staffName)
      this.exceptionTypeSeries = [
        {
          name: '迟到次数',
          data: lateCounts
        },
        {
          name: '早退次数',
          data: earlyLeaveCounts
        },
        {
          name: '缺勤天数',
          data: absentCounts
        },
        {
          name: '加班次数',
          data: overtimeCounts
        }
      ]

      const exceptionRates = this.dataSource.map(item => item.exceptionRate || 0)
      const absentRates = this.dataSource.map(item => item.absentRate || 0)

      this.rateCompareChartOptions.xaxis.categories = this.dataSource.map(item => item.staffName)
      this.rateCompareSeries = [
        {
          name: '异常率',
          data: exceptionRates
        },
        {
          name: '缺勤率',
          data: absentRates
        }
      ]
    },
    getRankClass (rank) {
      if (rank === 1) return 'rank-gold'
      if (rank === 2) return 'rank-silver'
      if (rank === 3) return 'rank-bronze'
      return 'rank-normal'
    },
    getRowClassName (record) {
      if (record.warnLevel === '红色预警') return 'row-red'
      if (record.warnLevel === '橙色预警') return 'row-orange'
      if (record.rank <= 3) return `row-rank-${record.rank}`
      return ''
    },
    getExceptionColor (rate) {
      if (rate >= 80) {
        return '#ff4d4f'
      } else if (rate >= 50) {
        return '#ff7a45'
      } else if (rate >= 30) {
        return '#faad14'
      } else {
        return '#52c41a'
      }
    },
    getAbsentColor (rate) {
      if (rate === 0) {
        return '#52c41a'
      } else if (rate <= 5) {
        return '#faad14'
      } else {
        return '#ff4d4f'
      }
    },
    getRiskColor (level) {
      const colorMap = {
        '严重违规': '#ff4d4f',
        '高度风险': '#ff7a45',
        '中度风险': '#faad14',
        '轻度风险': '#1890ff'
      }
      return colorMap[level] || '#d9d9d9'
    },
    getWarnStatus (level) {
      const statusMap = {
        '红色预警': 'error',
        '橙色预警': 'warning',
        '黄色预警': 'default',
        '蓝色预警': 'processing'
      }
      return statusMap[level] || 'default'
    },
    getWarnTagColor (level) {
      const colorMap = {
        '严重违规': 'red',
        '高度风险': 'orange',
        '中度风险': 'gold',
        '轻度风险': 'blue'
      }
      return colorMap[level] || 'default'
    },
    getActionColor (action) {
      if (action === '立即介入') return 'red'
      if (action === '尽快沟通') return 'orange'
      if (action === '持续关注') return 'gold'
      return 'blue'
    },
    getExceptionStatusColor (status) {
      const colorMap = {
        '迟到': 'orange',
        '早退': 'red',
        '旷工': 'purple',
        '加班': 'green'
      }
      return colorMap[status] || 'default'
    },
    toggleExpand (record) {
      const index = this.expandedRowKeys.indexOf(record.staffId)
      if (index > -1) {
        this.expandedRowKeys.splice(index, 1)
      } else {
        this.expandedRowKeys.push(record.staffId)
      }
    },
    handleExpand (expanded, record) {
      const keys = [...this.expandedRowKeys]
      if (expanded) {
        keys.push(record.staffId)
      } else {
        const index = keys.indexOf(record.staffId)
        if (index > -1) {
          keys.splice(index, 1)
        }
      }
      this.expandedRowKeys = keys
    },
    handleCall (record) {
      this.$message.info(`拨打 ${record.staffName} 电话: ${record.phone}`)
    },
    handleSearch () {
      this.pagination.defaultCurrent = 1
      this.expandedRowKeys = []
      this.queryComplianceWarn()
    },
    handleReset () {
      this.queryParam = {
        date: null,
        staffName: '',
        warnLevel: ''
      }
      this.pagination.defaultCurrent = 1
      this.expandedRowKeys = []
      this.queryComplianceWarn()
    },
    handleTableChange (pagination) {
      this.pagination.defaultCurrent = pagination.current
      this.pagination.defaultPageSize = pagination.pageSize
    }
  }
}
</script>

<style scoped>.table-page-search-wrapper {
  margin-bottom: 16px;
}
.stat-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}
.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}
.risk-total {
  border-left: 4px solid #ff4d4f;
}
.avg-risk {
  border-left: 4px solid #faad14;
}
.immediate-action {
  border-left: 4px solid #ff4d4f;
}
.highest-risk {
  background: linear-gradient(135deg, #fff1f0 0%, #fff 100%);
  border: 2px solid #ff4d4f;
}
.level-card {
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.level-card.red-warning {
  border-top: 3px solid #ff4d4f;
}
.level-card.orange-warning {
  border-top: 3px solid #ff7a45;
}
.level-card.yellow-warning {
  border-top: 3px solid #faad14;
}
.level-card.blue-warning {
  border-top: 3px solid #1890ff;
}
.rank-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: bold;
  font-size: 14px;
}
.rank-gold {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 215, 0, 0.4);
}
.rank-silver {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  color: #fff;
  box-shadow: 0 2px 8px rgba(192, 192, 192, 0.4);
}
.rank-bronze {
  background: linear-gradient(135deg, #cd7f32, #e6a56b);
  color: #fff;
  box-shadow: 0 2px 8px rgba(205, 127, 50, 0.4);
}
.rank-normal {
  background: #f0f0f0;
  color: #666;
}
.row-red {
  background: linear-gradient(90deg, #fff1f0 0%, #fff 100%);
}
.row-orange {
  background: linear-gradient(90deg, #fff7e6 0%, #fff 100%);
}
.row-rank-1 {
  background: linear-gradient(90deg, #fff7e6 0%, #fff 100%);
}
.row-rank-2 {
  background: linear-gradient(90deg, #f5f5f5 0%, #fff 100%);
}
.row-rank-3 {
  background: linear-gradient(90deg, #fff2e8 0%, #fff 100%);
}
</style>
