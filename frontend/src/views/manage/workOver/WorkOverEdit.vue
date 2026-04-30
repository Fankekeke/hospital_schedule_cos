<template>
  <a-modal v-model="show" title="修改加班规则" @cancel="onClose" :width="550">
    <template slot="footer">
      <a-button key="back" @click="onClose">
        取消
      </a-button>
      <a-button key="submit" type="primary" :loading="loading" @click="handleSubmit">
        修改
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
            <span> 加班规则配置</span>
          </div>
        </a-col>
        <a-col :span="24">
          <a-form-item label='' v-bind="formItemLayout">
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label='开始时间(分钟后)'>
                  <a-input-number
                    v-decorator="['start_after']"
                    :min="0"                    style="width: 100%"
                    placeholder="如: 30"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label='计算单位'>
                  <a-select v-decorator="['unit']">
                    <a-select-option value="minute">分钟</a-select-option>
                    <a-select-option value="hour">小时</a-select-option>
                  </a-select>
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
  name: 'BulletinEdit',
  props: {
    bulletinEditVisiable: {
      default: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    show: {
      get: function () {
        return this.bulletinEditVisiable
      },
      set: function () {
      }
    }
  },
  data () {
    return {
      rowId: null,
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
    imagesInit (images) {
      if (images !== null && images !== '') {
        let imageList = []
        images.split(',').forEach((image, index) => {
          imageList.push({uid: index, name: image, status: 'done', url: 'http://127.0.0.1:9527/imagesWeb/' + image})
        })
        this.fileList = imageList
      }
    },
    setFormValues ({...bulletin}) {
      this.rowId = bulletin.id
      let fields = ['policyName', 'targetId']
      let obj = {}
      this.form.getFieldDecorator('start_after')
      this.form.getFieldDecorator('unit')
      // 解析 overtimeRule JSON 字符串
      console.log(bulletin.overtimeRule)
      if (bulletin.overtimeRule) {
        try {
          const config = JSON.parse(bulletin.overtimeRule)
          obj.start_after = config.start_after
          obj.unit = config.unit
        } catch (e) {
          console.error('解析加班规则配置失败:', e)
        }
      }
      Object.keys(bulletin).forEach((key) => {
        if (key === 'images') {
          this.fileList = []
          this.imagesInit(bulletin['images'])
        }
        if (key === 'rackUp' || key === 'type') {
          bulletin[key] = bulletin[key].toString()
        }
        if (fields.indexOf(key) !== -1) {
          this.form.getFieldDecorator(key)
          obj[key] = bulletin[key]
        }
      })
      this.form.setFieldsValue(obj)
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
        if (image.response !== undefined) {
          images.push(image.response)
        } else {
          images.push(image.name)
        }
      })
      this.form.validateFields((err, values) => {
        values.id = this.rowId
        values.images = images.length > 0 ? images.join(',') : null

        // 提取加班规则配置字段并转为JSON
        const overtimeRuleConfig = {
          start_after: values.start_after || 0,
          unit: values.unit || 'hour'
        }

        // 删除原始字段
        delete values.start_after
        delete values.unit
        // 将配置对象转为JSON字符串
        values.targetType = 2
        values.overtimeRule = JSON.stringify(overtimeRuleConfig)
        if (!err) {
          this.loading = true
          this.$put('/cos/attendance-policy', {
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
