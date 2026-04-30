<template>
  <a-modal v-model="show" title="新增班次" @cancel="onClose" :width="800">
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
        <a-col :span="12">
          <a-form-item label='班次名称' v-bind="formItemLayout">
            <a-input v-decorator="[
            'shiftName',
            { rules: [{ required: true, message: '请输入班次名称!' }] }
            ]" placeholder="如：内科白班、手术加班班次"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='班次类型' v-bind="formItemLayout">
            <a-select v-decorator="[
            'shiftType',
            { initialValue: 1, rules: [{ required: true, message: '请选择班次类型!' }] }
            ]" placeholder="请选择班次类型">
              <a-select-option :value="1">常规</a-select-option>
              <a-select-option :value="2">手术班</a-select-option>
              <a-select-option :value="3">支援班</a-select-option>
              <a-select-option :value="4">弹性班</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='作业周' v-bind="formItemLayout">
            <a-checkbox-group v-decorator="['weekDay', {rules: [{ required: true, message: '请选择班次类型!' }]}]" style="width: 100%;">
              <a-row>
                <a-col :span="3">
                  <a-checkbox :value="1">周一</a-checkbox>
                </a-col>
                <a-col :span="3">
                  <a-checkbox :value="2">周二</a-checkbox>
                </a-col>
                <a-col :span="3">
                  <a-checkbox :value="3">周三</a-checkbox>
                </a-col>
                <a-col :span="3">
                  <a-checkbox :value="4">周四</a-checkbox>
                </a-col>
                <a-col :span="3">
                  <a-checkbox :value="5">周五</a-checkbox>
                </a-col>
                <a-col :span="3">
                  <a-checkbox :value="6">周六</a-checkbox>
                </a-col>
                <a-col :span="3">
                  <a-checkbox :value="7">周日</a-checkbox>
                </a-col>
              </a-row>
            </a-checkbox-group>
            <div style="margin-top: 8px; color: #999; font-size: 12px;">
              提示：选择勾选后则当天进行作业
            </div>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='班次科室' v-bind="formItemLayout">
            <a-select
              v-decorator="[
                'deptId',
                { rules: [{ required: true, message: '请选择作用科室!' }] }
              ]"
              placeholder="请选择作用科室"              style="width: 100%"
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
          <a-form-item label='上班时间' v-bind="formItemLayout">
            <a-time-picker v-decorator="[
            'startTime',
            { rules: [{ required: true, message: '请选择上班时间!' }] }
            ]" format="HH:mm" placeholder="请选择上班时间" style="width: 100%"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='下班时间' v-bind="formItemLayout">
            <a-time-picker v-decorator="[
            'endTime',
            { rules: [{ required: true, message: '请选择下班时间!' }] }
            ]" format="HH:mm" placeholder="请选择下班时间" style="width: 100%"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='允许迟到(分钟)' v-bind="formItemLayout">
            <a-input-number v-decorator="[
            'allowLate',
            { initialValue: 10 }
            ]" :min="0" :max="60" placeholder="缓冲时间" style="width: 100%"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='夜班津贴' v-bind="formItemLayout">
            <a-textarea :rows="6" v-decorator="[
            'nightAllowance'
            ]"/>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import moment from 'moment'
moment.locale('zh-cn')
import {mapState} from 'vuex'
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
      deptList: []
    }
  },
  mounted () {
    this.queryDeptList()
  },
  methods: {
    queryDeptList () {
      this.$get('/cos/dept-info/list').then(r => {
        this.deptList = r.data.data
      })
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
      this.form.validateFields((err, values) => {
        if (!err) {
          this.loading = true
          // 处理 weekDay 字段，将数组转换为逗号分隔的字符串
          let weekDayStr = null
          if (values.weekDay && values.weekDay.length > 0) {
            weekDayStr = values.weekDay.join(',')
          }
          // 格式化时间字段
          const submitData = {
            ...values,
            weekDay: weekDayStr,
            startTime: values.startTime ? moment(values.startTime).format('HH:mm') : null,
            endTime: values.endTime ? moment(values.endTime).format('HH:mm') : null
          }
          this.$post('/cos/attendance-shift', submitData).then((r) => {
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
