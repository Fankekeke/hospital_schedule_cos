<template>
  <a-modal v-model="show" title="新增打卡规则" @cancel="onClose" :width="550">
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
        <a-col :span="24">
          <a-form-item label='策略名称' v-bind="formItemLayout">
            <a-input v-decorator="[
            'policyName',
            { rules: [{ required: true, message: '请输入策略名称!' }] }
            ]"/>
          </a-form-item>
        </a-col>
        <a-col :span="24">
          <a-form-item label='作用科室' v-bind="formItemLayout">
            <a-select
              v-decorator="[
                'targetId',
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
        <a-col :span="24">
          <div class="section-title">
            <a-icon type="setting" theme="filled" />
            <span> 打卡规则配置</span>
          </div>
        </a-col>
        <a-col :span="24">
          <a-form-item label='' v-bind="formItemLayout">
            <a-row :gutter="16">
              <a-col :span="24">
                <a-form-item label='GPS允许打卡范围(米)'>
                  <a-input-number
                    v-decorator="['gpsRange']"
                    :min="0"
                    :max="10000"                    style="width: 100%"
                    placeholder="如: 500"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
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
      deptList: [],
      fileList: [],
      previewVisible: false,
      previewImage: ''
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
      // 获取图片List
      let images = []
      this.fileList.forEach(image => {
        images.push(image.response)
      })
      this.form.validateFields((err, values) => {
        values.images = images.length > 0 ? images.join(',') : null

        if (!err) {
          this.loading = true
          values.targetType = 1
          this.$post('/cos/attendance-policy', {
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

<style scoped>.section-title {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 16px;
  background: #f5f7fa;
  border-left: 4px solid #1890ff;
  border-radius: 1px;
}

.title-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  border-radius: 50%;
  margin-right: 10px;
  box-shadow: 0 2px 4px rgba(102, 126, 234, 0.4);
}

.title-text {
  font-size: 15px;
  font-weight: 600;
  color: #262626;
}
</style>
