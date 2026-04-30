<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="班次名称"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.shiftName" placeholder="请输入班次名称"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="班次类型"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-select v-model="queryParams.shiftType" placeholder="请选择班次类型" allowClear>
                  <a-select-option :value="1">常规</a-select-option>
                  <a-select-option :value="2">手术班</a-select-option>
                  <a-select-option :value="3">支援班</a-select-option>
                  <a-select-option :value="4">弹性班</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <a-button type="primary" ghost @click="add">新增</a-button>
        <a-button @click="batchDelete">删除</a-button>
<!--        <a-button @click="batchDelete1">删除</a-button>-->
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <div slot="expandedRowRender" slot-scope="record" style="margin: 0">
          <a-row :gutter="24">
            <!-- 班次详细信息 -->
            <a-col :span="16">
              <a-card title="班次详细信息" size="small" :bordered="false">
                <a-descriptions bordered :column="2" size="small">
                  <a-descriptions-item label="班次名称">
                    {{ record.shiftName }}
                  </a-descriptions-item>
                  <a-descriptions-item label="班次类型">
                    <a-tag :color="getShiftTypeColor(record.shiftType)">
                      {{ getShiftTypeName(record.shiftType) }}
                    </a-tag>
                  </a-descriptions-item>
                  <a-descriptions-item label="上班时间">
                    {{ record.startTime }}
                  </a-descriptions-item>
                  <a-descriptions-item label="下班时间">
                    {{ record.endTime }}
                  </a-descriptions-item>
                  <a-descriptions-item label="允许迟到">
                    {{ record.allowLate || 0 }} 分钟
                  </a-descriptions-item>
                  <a-descriptions-item label="是否跨天">
                    <a-tag :color="record.isCrossDay === 1 ? 'red' : 'green'">
                      {{ record.isCrossDay === 1 ? '是' : '否' }}
                    </a-tag>
                  </a-descriptions-item>
                  <a-descriptions-item label="夜班津贴" :span="2">
                    {{ record.nightAllowance || '- -' }}
                  </a-descriptions-item>
                </a-descriptions>
              </a-card>
            </a-col>

            <!-- 绑定人员列表 -->
            <a-col :span="8">
              <a-card title="绑定医生列表" size="small" :bordered="false">
                <a-empty v-if="!record.staffList || record.staffList.length === 0" description="暂无绑定医生" />
                <div v-else style="max-height: 300px; overflow-y: auto;">
                  <a-list item-layout="horizontal" :data-source="record.staffList">
                    <a-list-item slot="renderItem" slot-scope="staff">
                      <a-list-item-meta>
                        <a-avatar slot="avatar" shape="square" size="large"
                                  :src="staff.images ? 'http://127.0.0.1:9527/imagesWeb/' + staff.images : undefined"
                                  icon="user" />
                        <div slot="title" style="font-weight: 600;">
                          {{ staff.name }}
                        </div>
                        <div slot="description" style="font-size: 12px; color: #666;">
                          <div>{{ staff.deptName }} - {{ staff.positionName }}</div>
                          <div v-if="staff.phone">电话: {{ staff.phone }}</div>
                        </div>
                      </a-list-item-meta>
                    </a-list-item>
                  </a-list>
                </div>
                <div style="margin-top: 12px; text-align: right;">
                  <a-button type="primary" size="small" @click="bindStaff(record)">
                    <a-icon type="usergroup-add" /> 管理绑定
                  </a-button>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-badge status="processing" v-if="record.rackUp === 1"/>
            <a-badge status="error" v-if="record.rackUp === 0"/>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="contentShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.content }}
              </template>
              {{ record.content.slice(0, 30) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon
            type="usergroup-add"
            @click="bindStaff(record)"
            title="绑定医生"            style="margin-right: 8px; font-size: 18px;">
          </a-icon>
          <a-icon
            type="setting"
            theme="twoTone"
            twoToneColor="#4a9ff5"
            @click="edit(record)"
            title="修 改"            style="font-size: 18px;">
          </a-icon>
        </template>
      </a-table>
    </div>
    <bulletin-add
      v-if="bulletinAdd.visiable"
      @close="handleBulletinAddClose"
      @success="handleBulletinAddSuccess"
      :bulletinAddVisiable="bulletinAdd.visiable">
    </bulletin-add>
    <bulletin-edit
      ref="bulletinEdit"
      @close="handleBulletinEditClose"
      @success="handleBulletinEditSuccess"
      :bulletinEditVisiable="bulletinEdit.visiable">
    </bulletin-edit>
    <!-- 绑定医生弹窗 -->
    <a-modal
      v-model="bindModalVisible"
      title="绑定医生"
      :width="700"
      @ok="handleBindSubmit"
      @cancel="handleBindCancel"
    >
      <a-form layout="vertical">
        <a-alert
          message="提示"
          description="请选择需要绑定到该班次的医生，可多选。已绑定的医生会自动选中。"
          type="info"
          show-icon          style="margin-bottom: 16px;"
        />

        <a-form-item label='班次信息'>
          <a-descriptions bordered :column="2" size="small">
            <a-descriptions-item label="班次名称">
              {{ currentShift.shiftName }}
            </a-descriptions-item>
            <a-descriptions-item label="班次类型">
              <a-tag :color="getShiftTypeColor(currentShift.shiftType)">
                {{ getShiftTypeName(currentShift.shiftType) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="上班时间" :span="2">
              {{ currentShift.startTime }} - {{ currentShift.endTime }}
            </a-descriptions-item>
          </a-descriptions>
        </a-form-item>

        <a-form-item label='选择医生' required>
          <a-select
            mode="multiple"
            v-model="selectedStaffIds"
            placeholder="请选择医生（可多选）"            style="width: 100%"
            :max-tag-count="5"
            show-search
            option-filter-prop="children"
          >
            <a-select-option
              v-for="staff in staffList"
              :key="staff.id"
              :value="staff.id"
            >
              {{ staff.name }} - {{ staff.deptName }} - {{ staff.positionName }}
            </a-select-option>
          </a-select>
          <div style="margin-top: 8px; color: #999; font-size: 12px;">
            已选择 {{ selectedStaffIds.length }} 位医生
          </div>
        </a-form-item>

        <a-form-item label='已选医生列表'>
          <a-empty v-if="selectedStaffIds.length === 0" description="暂无选择医生" />
          <div v-if="selectedStaffIds.length !== 0" style="max-height: 200px; overflow-y: auto;">
            <a-tag
              v-for="staffId in selectedStaffIds"
              :key="staffId"
              closable
              @close="removeStaff(staffId)"              style="margin: 4px;"
            >
              {{ getStaffName(staffId) }}
            </a-tag>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import BulletinAdd from './ShiftAdd.vue'
import BulletinEdit from './ShiftEdit.vue'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {BulletinAdd, BulletinEdit, RangeDate},
  data () {
    return {
      advanced: false,
      bulletinAdd: {
        visiable: false
      },
      bulletinEdit: {
        visiable: false
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      staffList: [],
      loading: false,
      bindModalVisible: false,
      currentShift: {},
      selectedStaffIds: [],
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '班次名称',
        dataIndex: 'shiftName',
        ellipsis: true,
        width: 150,
        customRender: (text, row, index) => {
          if (text) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '绑定医生',
        dataIndex: 'staffList',
        ellipsis: true,
        width: 120,
        customRender: (text, row, index) => {
          if (row.staffList && Array.isArray(row.staffList)) {
            const count = row.staffList.length
            return <a-tag color="blue">{count} 人</a-tag>
          } else if (row.staffIds) {
            // 如果没有 staffList，尝试从 staffIds 字符串计算
            const ids = row.staffIds.split(',').filter(id => id.trim() !== '')
            return <a-tag color="blue">{ids.length} 人</a-tag>
          } else {
            return <a-tag color="default">0 人</a-tag>
          }
        }
      }, {
        title: '排班科室',
        dataIndex: 'deptName',
        ellipsis: true,
        width: 150,
        customRender: (text, row, index) => {
          if (text) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '班次类型',
        dataIndex: 'shiftType',
        ellipsis: true,
        width: 100,
        customRender: (text, row, index) => {
          switch (text) {
            case 1:
              return <a-tag color="blue">常规</a-tag>
            case 2:
              return <a-tag color="green">手术班</a-tag>
            case 3:
              return <a-tag color="orange">支援班</a-tag>
            case 4:
              return <a-tag color="purple">弹性班</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '上班时间',
        dataIndex: 'startTime',
        ellipsis: true,
        width: 100,
        customRender: (text, row, index) => {
          if (text) {
            return text.substring(0, 5)
          } else {
            return '- -'
          }
        }
      }, {
        title: '下班时间',
        dataIndex: 'endTime',
        ellipsis: true,
        width: 100,
        customRender: (text, row, index) => {
          if (text) {
            return text.substring(0, 5)
          } else {
            return '- -'
          }
        }
      }, {
        title: '工作时长(小时)',
        dataIndex: 'workHours',
        ellipsis: true,
        width: 120,
        customRender: (text, row, index) => {
          if (row.startTime && row.endTime) {
            const start = row.startTime.substring(0, 5).split(':').map(Number)
            const end = row.endTime.substring(0, 5).split(':').map(Number)

            let startMinutes = start[0] * 60 + start[1]
            let endMinutes = end[0] * 60 + end[1]

            // 如果跨天，结束时间需要加24小时
            if (row.isCrossDay === 1 || endMinutes <= startMinutes) {
              endMinutes += 24 * 60
            }

            const diffMinutes = endMinutes - startMinutes
            const hours = (diffMinutes / 60).toFixed(1)

            return `${hours} 小时`
          } else {
            return '- -'
          }
        }
      }, {
        title: '允许迟到(分钟)',
        dataIndex: 'allowLate',
        ellipsis: true,
        width: 120,
        customRender: (text, row, index) => {
          if (text !== null && text !== undefined) {
            return text
          } else {
            return '- -'
          }
        }
      }, {
        title: '是否跨天',
        dataIndex: 'isCrossDay',
        ellipsis: true,
        width: 100,
        customRender: (text, row, index) => {
          switch (text) {
            case 1:
              return <a-tag color="red">是</a-tag>
            case 0:
              return <a-tag color="green">否</a-tag>
            default:
              return '- -'
          }
        }
      }, {
        title: '夜班津贴',
        dataIndex: 'nightAllowance',
        ellipsis: true,
        width: 100,
        customRender: (text, row, index) => {
          if (text !== null && text !== undefined) {
            return `¥${text}`
          } else {
            return '- -'
          }
        }
      }, {
        title: '操作',
        dataIndex: 'operation',
        width: 100,
        ellipsis: true,
        scopedSlots: {customRender: 'operation'}
      }]
    }
  },
  mounted () {
    this.fetch()
    this.queryStaffList()
  },
  methods: {
    queryStaffList () {
      this.$get('/cos/staff-info/list').then(r => {
        this.staffList = r.data.data || []
      })
    },
    getShiftTypeName (type) {
      const typeMap = {
        1: '常规',
        2: '手术班',
        3: '支援班',
        4: '弹性班'
      }
      return typeMap[type] || '- -'
    },
    getShiftTypeColor (type) {
      const colorMap = {
        1: 'blue',
        2: 'green',
        3: 'orange',
        4: 'purple'
      }
      return colorMap[type] || 'default'
    },
    getStaffName (staffId) {
      const staff = this.staffList.find(s => s.id === staffId)
      return staff ? `${staff.name} (${staff.deptName})` : `医生${staffId}`
    },
    removeStaff (staffId) {
      const index = this.selectedStaffIds.indexOf(staffId)
      if (index > -1) {
        this.selectedStaffIds.splice(index, 1)
      }
    },
    bindStaff (record) {
      this.currentShift = { ...record }
      // 解析已绑定的医生ID
      if (record.staffIds) {
        this.selectedStaffIds = record.staffIds.split(',').map(id => parseInt(id)).filter(id => !isNaN(id))
      } else {
        this.selectedStaffIds = []
      }
      this.bindModalVisible = true
    },
    handleBindCancel () {
      this.bindModalVisible = false
      this.currentShift = {}
      this.selectedStaffIds = []
    },
    handleBindSubmit () {
      if (this.selectedStaffIds.length === 0) {
        this.$message.warning('请至少选择一位医生')
        return
      }

      this.$confirm({
        title: '确认绑定',
        content: `将为班次"${this.currentShift.shiftName}"绑定 ${this.selectedStaffIds.length} 位医生，是否继续？`,
        centered: true,
        onOk: () => {
          // 将医生ID数组转换为逗号分隔的字符串
          const staffIdsStr = this.selectedStaffIds.join(',')

          this.$put('/cos/attendance-shift', {
            id: this.currentShift.id,
            staffIds: staffIdsStr
          }).then(() => {
            this.$message.success('绑定成功')
            this.bindModalVisible = false
            this.currentShift = {}
            this.selectedStaffIds = []
            this.fetch()
          }).catch(() => {
            this.$message.error('绑定失败')
          })
        }
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.bulletinAdd.visiable = true
    },
    handleBulletinAddClose () {
      this.bulletinAdd.visiable = false
    },
    handleBulletinAddSuccess () {
      this.bulletinAdd.visiable = false
      this.$message.success('新增班次成功')
      this.search()
    },
    edit (record) {
      this.$refs.bulletinEdit.setFormValues(record)
      this.bulletinEdit.visiable = true
    },
    handleBulletinEditClose () {
      this.bulletinEdit.visiable = false
    },
    handleBulletinEditSuccess () {
      this.bulletinEdit.visiable = false
      this.$message.success('修改班次成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete1 () {
      this.$get('/cos/supplier-info/batchEditSupplierName').then((r) => {
      })
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/attendance-shift/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      if (params.shiftType === undefined) {
        delete params.shiftType
      }
      this.$get('/cos/attendance-shift/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
