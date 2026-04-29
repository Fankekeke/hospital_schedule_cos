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
      previewImage: ''
    }
  },
  methods: {
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
          // 格式化时间字段
          const submitData = {
            ...values,
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
