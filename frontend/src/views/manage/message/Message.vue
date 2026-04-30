<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="员工姓名"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.staffName"/>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item
                label="阅读状态"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-select v-model="queryParams.readStatus" placeholder="请选择状态" style="width: 100%">
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="0">未读</a-select-option>
                  <a-select-option value="1">已读</a-select-option>
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
<!--        <a-button type="primary" ghost @click="add">新增</a-button>-->
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
            type="eye"
            theme="twoTone"
            twoToneColor="#1890ff"
            @click="viewDetail(record)"
            title="查看详情"            style="font-size: 18px;">
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
    <!-- 详情弹窗 -->
    <!-- 详情弹窗 -->
    <a-modal
      v-model="detailModalVisible"
      title="消息详情"
      :width="600"
      :footer="null"
    >
      <a-descriptions bordered :column="1">
        <a-descriptions-item label="员工信息">
          <div style="display: flex; align-items: center;">
            <a-avatar
              size="large"
              :src="currentRecord.staffImages ? 'http://127.0.0.1:9527/imagesWeb/' + currentRecord.staffImages : undefined"
              icon="user"              style="margin-right: 10px;"
            />
            <div>
              <div style="font-weight: 600;">{{ currentRecord.staffName }}</div>
              <div style="color: #999; font-size: 12px;">{{ currentRecord.deptName }} · {{ currentRecord.positionName }}</div>
            </div>
          </div>
        </a-descriptions-item>
        <a-descriptions-item label="消息内容">
          <div style="white-space: pre-wrap; line-height: 1.8;">
            {{ currentRecord.content || '- -' }}
          </div>
        </a-descriptions-item>
        <a-descriptions-item label="发送时间">
          {{ currentRecord.createDate || '- -' }}
        </a-descriptions-item>
        <a-descriptions-item label="阅读状态">
          <a-badge
            :status="currentRecord.readStatus === '1' ? 'success' : 'error'"
            :text="currentRecord.readStatus === '1' ? '已读' : '未读'"
          />
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import BulletinAdd from './MessageAdd.vue'
import BulletinEdit from './MessageEdit.vue'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {BulletinAdd, BulletinEdit, RangeDate},
  data () {
    return {
      detailModalVisible: false,
      currentRecord: {},
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
      loading: false,
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
        title: '员工姓名',
        dataIndex: 'staffName',
        customRender: (text, record, index) => {
          if (!text) return '- -'
          return (
            <div style="display: flex; align-items: center;">
              <a-avatar
                size="large"
                src={ record.staffImages ? 'http://127.0.0.1:9527/imagesWeb/' + record.staffImages : null }
                icon={ record.staffImages ? null : 'user' }
                style="margin-right: 10px;"
              />
              <div>
                <div style="font-weight: 600;">{text}</div>
                <div style="color: #999; font-size: 12px;">{record.deptName} · {record.positionName}</div>
              </div>
            </div>
          )
        },
        width: 200,
        ellipsis: true
      }, {
        title: '消息内容',
        dataIndex: 'content',
        customRender: (text, row, index) => {
          if (text !== null) {
            return (
              <a-tooltip title={text}>
                <div style="max-width: 400px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                  {text}
                </div>
              </a-tooltip>
            )
          } else {
            return '- -'
          }
        },
        ellipsis: true,
        width: 350
      }, {
        title: '发送时间',
        dataIndex: 'createDate',
        customRender: (text, row, index) => {
          if (text !== null) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true,
        width: 160
      }, {
        title: '阅读状态',
        dataIndex: 'readStatus',
        customRender: (text, row, index) => {
          switch (text) {
            case '0':
              return <a-badge status="error" text="未读" />
            case '1':
              return <a-badge status="success" text="已读" />
            default:
              return '- -'
          }
        },
        ellipsis: true,
        width: 100
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'},
        width: 100,
        fixed: 'right'
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    getCheckTypeName (type) {
      const typeMap = {
        1: '上班打卡',
        2: '下班打卡'
      }
      return typeMap[type] || '- -'
    },
    getCheckTypeColor (type) {
      const colorMap = {
        1: 'green',
        2: 'blue'
      }
      return colorMap[type] || 'default'
    },
    getVerifyModeName (mode) {
      const modeMap = {
        'FACE': '人脸识别',
        'FINGERPRINT': '指纹',
        'PASSWORD': '密码',
        'GPS': 'GPS打卡'
      }
      return modeMap[mode] || mode || '- -'
    },
    viewDetail (record) {
      this.currentRecord = { ...record }
      this.detailModalVisible = true
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
      this.$message.success('新增打卡记录成功')
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
      this.$message.success('修改打卡记录成功')
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
          that.$delete('/cos/message-info/' + ids).then(() => {
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
      this.$get('/cos/message-info/page', {
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
