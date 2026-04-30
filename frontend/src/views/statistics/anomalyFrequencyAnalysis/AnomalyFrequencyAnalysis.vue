
<template>
  <a-card :bordered="false">
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
            <a-form-item label="统计维度">
              <a-select v-model="queryParam.dimension" placeholder="请选择统计维度">
                <a-select-option value="staff">按个人统计</a-select-option>
                <a-select-option value="dept">按部门统计</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleSearch" icon="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="handleReset" icon="reload">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="false"
      bordered
      row-key="staffId"
    >
      <template slot="lateCount" slot-scope="text, record">
        <span style="color: #ff4d4f">{{ text }}</span>
      </template>
      <template slot="earlyCount" slot-scope="text, record">
        <span style="color: #faad14">{{ text }}</span>
      </template>
    </a-table>
  </a-card>
</template>

<script>export default {
  name: "AnomalyFrequencyAnalysis",
  data () {
    return {
      loading: false,
      dataSource: [],
      queryParam: {
        date: null,
        dimension: 'staff'
      },
      columns: [
        {
          title: '人员姓名',
          dataIndex: 'staffName',
          key: 'staffName',
          width: 120
        },
        {
          title: '工号',
          dataIndex: 'code',
          key: 'code',
          width: 180
        },
        {
          title: '迟到次数',
          dataIndex: 'lateCount',
          key: 'lateCount',
          width: 100,
          scopedSlots: { customRender: 'lateCount' }
        },
        {
          title: '迟到总时长(分钟)',
          dataIndex: 'totalLateMinutes',
          key: 'totalLateMinutes',
          width: 130
        },
        {
          title: '早退次数',
          dataIndex: 'earlyCount',
          key: 'earlyCount',
          width: 100,
          scopedSlots: { customRender: 'earlyCount' }
        },
        {
          title: '早退总时长(分钟)',
          dataIndex: 'totalEarlyMinutes',
          key: 'totalEarlyMinutes',
          width: 130
        },
        {
          title: '总打卡记录数',
          dataIndex: 'totalRecords',
          key: 'totalRecords',
          width: 120
        }
      ]
    }
  },
  mounted () {
    this.anomalyFrequencyAnalysis()
  },
  methods: {
    anomalyFrequencyAnalysis () {
      this.loading = true
      const params = {}

      // 处理日期参数，格式为 yyyy-MM
      if (this.queryParam.date) {
        params.date = this.queryParam.date.format('YYYY-MM')
      }

      // 处理统计维度参数
      if (this.queryParam.dimension) {
        params.dimension = this.queryParam.dimension
      }

      this.$get('/cos/web/anomalyFrequencyAnalysis', params).then(res => {
        this.dataSource = res.data.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
        this.$message.error('查询异常')
      })
    },
    handleSearch () {
      this.anomalyFrequencyAnalysis()
    },
    handleReset () {
      this.queryParam = {
        date: null,
        dimension: 'staff'
      }
      this.anomalyFrequencyAnalysis()
    }
  }
}
</script>

<style scoped>.table-page-search-wrapper {
  margin-bottom: 16px;
}
</style>
