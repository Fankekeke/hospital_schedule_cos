<template>
  <a-modal v-model="show" title="新增临派排班" @cancel="onClose" :width="600">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        提交
      </a-button>
    </template>
    <a-form :form="form" layout="vertical">
      <a-row :gutter="20">
        <a-col :span="16">
          <a-form-item label='员工' v-bind="formItemLayout">
            <a-select
              v-decorator="[
                'staffId',
                { rules: [{ required: true, message: '请选择员工!' }] }
              ]"
              placeholder="请选择员工"              style="width: 100%"
              @change="handleStaffChange"
            >
              <a-select-option
                v-for="staff in staffList"
                :key="staff.id"
                :value="staff.id"
              >
                {{ staff.name }} - {{ staff.deptName }} - {{ staff.positionName }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label='所属科室' v-bind="formItemLayout">
            <a-select
              v-decorator="[
                'deptId',
                { rules: [{ required: true, message: '请选择科室!' }] }
              ]"
              placeholder="请选择科室"              style="width: 100%"
              disabled
            >
              <a-select-option
                v-for="dept in deptList"
                :key="dept.id"
                :value="dept.id"
              >
                {{ dept.deptName }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='排班日期' v-bind="formItemLayout">
            <a-date-picker
              v-decorator="[
                'workDate',
                { rules: [{ required: true, message: '请选择排班日期!' }] }
              ]"              style="width: 100%"
              format="YYYY-MM-DD"
              placeholder="请选择排班日期"
            />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='开始时间' v-bind="formItemLayout">
            <a-time-picker
              v-decorator="[
                'startTime',
                { rules: [{ required: true, message: '请选择开始时间!' }] }
              ]"              style="width: 100%"
              format="HH:mm:ss"
              placeholder="请选择开始时间"
            />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='结束时间' v-bind="formItemLayout">
            <a-time-picker
              v-decorator="[
                'endTime',
                { rules: [{ required: true, message: '请选择结束时间!' }] }
              ]"              style="width: 100%"
              format="HH:mm:ss"
              placeholder="请选择结束时间"
            />
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='备注' v-bind="formItemLayout">
            <a-textarea v-decorator="[
            'remark'
            ]" :rows="3" placeholder="请输入备注信息"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')
function getBase64 (file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result)
    reader.onerror = error => reject(error)
  })
}
const formItemLayout = {
  labelCol: { span: 24 },
  wrapperCol: { span: 24 }
}
export default {
  name: 'BulletinAdd',
  props: {
    bulletinAddVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.bulletinAddVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      formItemLayout,
      form: this.$form.createForm(this),
      loading: false,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      staffList: [],
      deptList: []
    }
  },
  mounted () {
    this.queryStaffList()
    this.queryDeptList()
  },
  methods: {
    queryStaffList () {
      this.$get('/cos/staff-info/list').then(r => {
        this.staffList = r.data.data || []
      })
    },
    queryDeptList () {
      this.$get('/cos/dept-info/list').then(r => {
        this.deptList = r.data.data || []
      })
    },
    handleStaffChange (staffId) {
      // 根据选中的员工ID找到对应的员工信息
      const selectedStaff = this.staffList.find(staff => staff.id === staffId)
      if (selectedStaff && selectedStaff.deptId) {
        // 自动设置科室ID
        this.form.setFieldsValue({
          deptId: selectedStaff.deptId
        })
      }
    },
    handleCancel () {
      this.previewVisible = false
    },
    async handlePreview (file) {
      if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj)
      }
      this.previewImage = file.url || file.preview
      this.previewVisible = true
    },
    picHandleChange ({ fileList }) {
      this.fileList = fileList
    },
    reset () {
      this.loading = false
      this.form.resetFields()
    },
    onClose () {
      this.reset()
      this.$emit('close')
    },
    handleSubmit () {
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        images.push(image.response)
      })
      this.form.validateFields((err, values) => {
        values.images = images.length > 0 ? images.join(',') : null
        if (!err) {
          this.loading = true
          // 格式化日期和时间
          if (values.workDate) {
            values.workDate = moment(values.workDate).format('YYYY-MM-DD')
          }
          if (values.startTime) {
            values.startTime = moment(values.startTime).format('HH:mm:ss')
          }
          if (values.endTime) {
            values.endTime = moment(values.endTime).format('HH:mm:ss')
          }
          values.type = 2
          values.status = 0
          this.$post('/cos/staff-schedule', {
            ...values
          }).then((r) => {
            this.reset()
            this.$emit('success')
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
