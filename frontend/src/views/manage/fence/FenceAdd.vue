<template>
  <a-modal v-model="show" title="新增电子围栏" @cancel="onClose" :width="800">
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
        <a-col :span="24" style="margin-bottom: 15px">
          <a-input-search
            placeholder="请输入地址进行搜索"
            enter-button="搜索"
            @search="onSearchAddress"
            v-model="searchAddress"            style="width: 300px; margin-bottom: 10px;"
          />
          <div id="areas" class="map-container">

          </div>
        </a-col>
        <a-col :span="12">
          <a-form-item label='区域名称' v-bind="formItemLayout">
            <a-input v-decorator="[
            'areaName',
            { rules: [{ required: true, message: '请输入区域名称!' }] }
            ]" placeholder="如：东院区住院部、西院急诊楼"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='半径(米)' v-bind="formItemLayout">
            <a-input-number v-decorator="[
            'radius',
            { initialValue: 100 }
            ]" :min="1" :max="10000" style="width: 100%"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='经度' v-bind="formItemLayout">
            <a-input v-decorator="[
            'longitude',
            { rules: [{ required: true, message: '请在地图上选择位置!' }] }
            ]" placeholder="点击地图选择位置" disabled/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='纬度' v-bind="formItemLayout">
            <a-input v-decorator="[
            'latitude',
            { rules: [{ required: true, message: '请在地图上选择位置!' }] }
            ]" placeholder="点击地图选择位置" disabled/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label='状态' v-bind="formItemLayout">
            <a-select v-decorator="[
            'status',
            { initialValue: 1 }
            ]">
              <a-select-option :value="1">启用</a-select-option>
              <a-select-option :value="0">禁用</a-select-option>
            </a-select>
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
      map: null,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      searchAddress: '',
      currentLocation: null
    }
  },
  mounted () {
    setTimeout(() => {
      this.initMap()
    }, 500)
  },
  methods: {
    /**
     * 初始化地图
     */
    initMap () {
      this.map = new BMapGL.Map('areas')
      this.map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12)
      this.map.enableScrollWheelZoom(true)
      // this.map.setDisplayOptions({poiIcon: false})
      this.addMapEventListeners() // 添加地图事件监听
    },

    /**
     * 添加地图事件监听
     */
    addMapEventListeners () {
      // 地图点击事件，用于选择地点
      this.map.addEventListener('click', (e) => {
        const point = e.latlng
        this.setMapMarker(point)

        // 反地理编码获取地址
        const geocoder = new BMapGL.Geocoder()
        geocoder.getLocation(point, (result) => {
          if (result && result.address) {
            this.searchAddress = result.address
            this.currentLocation = {
              address: result.address,
              point: point
            }

            // 更新表单中的经纬度
            this.form.setFieldsValue({
              longitude: point.lng.toString(),
              latitude: point.lat.toString()
            })
          }
        })
      })
    },

    /**
     * 在地图上设置标记
     */
    setMapMarker(point) {
      // 清除之前的覆盖物
      this.map.clearOverlays()

      // 设置地图中心点
      this.map.centerAndZoom(point, 16)

      // 添加标记
      const marker = new BMapGL.Marker(point)
      this.map.addOverlay(marker)

      // 添加圆形围栏范围
      const radius = this.form.getFieldValue('radius') || 100
      const circle = new BMapGL.Circle(point, radius, {
        strokeColor: '#1890ff',
        strokeWeight: 2,
        strokeOpacity: 0.8,
        fillColor: '#1890ff',
        fillOpacity: 0.2
      })
      this.map.addOverlay(circle)
    },

    /**
     * 搜索地址功能
     */
    onSearchAddress(value) {
      if (!value) return

      const geolocation = new BMapGL.Geocoder()
      geolocation.getPoint(value, (point) => {
        if (point) {
          this.setMapMarker(point)

          // 更新表单中的经纬度
          this.form.setFieldsValue({
            longitude: point.lng.toString(),
            latitude: point.lat.toString()
          })

          // 保存当前位置信息
          this.currentLocation = {
            address: value,
            point: point
          }
        } else {
          this.$message.warning('未找到该地址，请尝试其他关键词')
        }
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
          // 准备提交数据
          const submitData = {
            areaName: values.areaName,
            longitude: values.longitude,
            latitude: values.latitude,
            radius: values.radius,
            status: values.status,
            remark: values.remark,
            fenceType: 1
          }
          this.$post('/cos/attendance-geo-fence', submitData).then((r) => {
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

/* 地图容器样式 */
#areas {
  width: 100%;
  height: 300px;
  border: 1px solid #e8e8e8;
}

</style>
