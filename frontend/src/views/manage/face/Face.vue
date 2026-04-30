
<template>
  <a-card :bordered="false" class="card-area">
    <!-- 搜索区域 -->
    <div style="margin-bottom: 16px;">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-input-search
            v-model="searchText"
            placeholder="请输入员工姓名搜索"
            enter-button
            @search="handleSearch"
          />
        </a-col>
        <a-col :span="18" style="text-align: right;">
          <a-space>
            <a-statistic title="总人数" :value="staffList.length" suffix="人" />
            <a-statistic
              title="已录入人脸"
              :value="faceRegisteredCount"
              suffix="人"
              value-style="color: #52c41a"
            />
            <a-statistic
              title="未录入人脸"
              :value="faceNotRegisteredCount"
              suffix="人"
              value-style="color: #ff4d4f"
            />
          </a-space>
        </a-col>
      </a-row>
    </div>

    <!-- 员工列表 -->
    <a-spin :spinning="loading">
      <a-row :gutter="16">
        <a-col
          v-for="staff in filteredStaffList"
          :key="staff.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"          style="margin-bottom: 16px;"
        >
          <a-card :hoverable="true" size="small">
            <div style="text-align: center;">
              <!-- 员工头像 -->
              <a-avatar
                :size="80"
                :src="staff.images ? 'http://127.0.0.1:9527/imagesWeb/' + staff.images : undefined"
                icon="user"
              />

              <!-- 员工信息 -->
              <div style="margin-top: 12px;">
                <div style="font-weight: 600; font-size: 16px;">{{ staff.name }}</div>
                <div style="color: #999; font-size: 12px; margin-top: 4px;">
                  {{ staff.deptName }} · {{ staff.positionName }}
                </div>
              </div>

              <!-- 人脸照片状态 -->
              <div style="margin-top: 16px; padding: 12px; background: #f5f7fa; border-radius: 4px;">
                <div style="font-size: 12px; color: #666; margin-bottom: 8px;">
                  人脸照片
                </div>

                <!-- 已录入 -->
                <div v-if="staff.faceImages">
                  <a-image
                    :width="100"
                    :src="'http://127.0.0.1:9527/imagesWeb/' + staff.faceImages"
                  />
                  <div style="margin-top: 8px;">
                    <a-badge status="success" text="已录入" />
                  </div>
                  <a-button
                    type="link"
                    size="small"
                    @click="handleUpload(staff)"                    style="margin-top: 8px;"
                  >
                    <a-icon type="reload" /> 重新上传
                  </a-button>
                </div>

                <!-- 未录入 -->
                <div v-else>
                  <a-empty description="未录入" :image="simpleImage" />
                  <a-button
                    type="primary"
                    size="small"
                    @click="handleUpload(staff)"
                    block
                  >
                    <a-icon type="upload" /> 上传人脸照片
                  </a-button>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 空状态 -->
      <a-empty v-if="filteredStaffList.length === 0" description="暂无员工数据" />
    </a-spin>

    <!-- 上传人脸照片弹窗 -->
    <a-modal
      v-model="uploadModalVisible"
      :title="'上传人脸照片 - ' + (currentStaff ? currentStaff.name : '')"
      :width="600"
      @ok="handleUploadSubmit"
      @cancel="handleUploadCancel"
    >
      <a-form layout="vertical">
        <a-alert
          message="提示"
          description="请上传清晰的人脸照片，支持jpg、png格式，文件大小不超过5MB。建议使用正面免冠照片。"
          type="info"
          show-icon          style="margin-bottom: 16px;"
        />

        <a-form-item label="员工信息">
          <a-descriptions bordered :column="2" size="small">
            <a-descriptions-item label="姓名">
              {{ currentStaff ? currentStaff.name : '- -' }}
            </a-descriptions-item>
            <a-descriptions-item label="科室">
              {{ currentStaff ? currentStaff.deptName : '- -' }}
            </a-descriptions-item>
            <a-descriptions-item label="职位">
              {{ currentStaff ? currentStaff.positionName : '- -' }}
            </a-descriptions-item>
            <a-descriptions-item label="性别">
              {{ currentStaff && currentStaff.sex === '1' ? '男' : '女' }}
            </a-descriptions-item>
          </a-descriptions>
        </a-form-item>

        <a-form-item label="人脸照片" required>
          <a-upload
            name="file"
            :action="uploadAction"
            list-type="picture-card"
            :file-list="fileList"
            :before-upload="beforeUpload"
            @change="handleFileChange"
            @preview="handlePreview"
          >
            <div v-if="fileList.length < 1">
              <a-icon type="plus" />
              <div class="ant-upload-text">上传</div>
            </div>
          </a-upload>

          <a-modal :visible="previewVisible" :footer="null" @cancel="handlePreviewCancel">
            <img alt="example" style="width: 100%" :src="previewImage" />
          </a-modal>
        </a-form-item>
      </a-form>
    </a-modal>
  </a-card>
</template>

<script>import { Empty } from 'ant-design-vue'

export default {
  name: "Face",
  data () {
    return {
      loading: false,
      searchText: '',
      staffList: [],
      uploadModalVisible: false,
      currentStaff: null,
      fileList: [],
      previewVisible: false,
      previewImage: '',
      uploadAction: 'http://127.0.0.1:9527/file/fileUpload/',
      simpleImage: Empty.PRESENTED_IMAGE_SIMPLE
    }
  },
  computed: {
    filteredStaffList () {
      if (!this.searchText) {
        return this.staffList
      }
      return this.staffList.filter(staff =>
        staff.name.indexOf(this.searchText) !== -1
      )
    },
    faceRegisteredCount () {
      return this.staffList.filter(s => s.faceImages).length
    },
    faceNotRegisteredCount () {
      return this.staffList.filter(s => !s.faceImages).length
    }
  },
  mounted () {
    this.queryStaffList()
  },
  methods: {
    queryStaffList () {
      this.loading = true
      this.$get('/cos/staff-info/list').then(r => {
        this.staffList = r.data.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleSearch () {
      // 搜索功能由 computed 自动处理
    },
    handleUpload (staff) {
      this.currentStaff = staff
      this.fileList = []

      // 如果已有照片，显示预览
      if (staff.faceImages) {
        this.fileList = [{
          uid: '-1',
          name: staff.faceImages,
          status: 'done',
          url: 'http://127.0.0.1:9527/imagesWeb/' + staff.faceImages
        }]
      }

      this.uploadModalVisible = true
    },
    handleUploadCancel () {
      this.uploadModalVisible = false
      this.currentStaff = null
      this.fileList = []
      this.previewVisible = false
    },
    handleUploadSubmit () {
      if (this.fileList.length === 0) {
        this.$message.warning('请上传人脸照片')
        return
      }

      const file = this.fileList[0]
      let faceImages = ''

      // 如果是新上传的文件
      if (file.response) {
        faceImages = file.response
      } else if (file.name) {
        // 如果是已有的文件
        faceImages = file.name
      }

      this.$put('/cos/staff-info', {
        id: this.currentStaff.id,
        faceImages: faceImages
      }).then(() => {
        this.$message.success('上传成功')
        this.uploadModalVisible = false
        this.currentStaff = null
        this.fileList = []
        this.queryStaffList()
      }).catch(() => {
        this.$message.error('上传失败')
      })
    },
    beforeUpload (file) {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
      if (!isJpgOrPng) {
        this.$message.error('只能上传 JPG/PNG 格式的图片!')
        return false
      }
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
        return false
      }
      return false // 阻止自动上传，手动处理
    },
    handleFileChange (info) {
      this.fileList = info.fileList.slice(-1) // 只保留最后一个文件
    },
    handlePreview (file) {
      this.previewImage = file.url || file.thumbUrl
      this.previewVisible = true
    },
    handlePreviewCancel () {
      this.previewVisible = false
    }
  },
}
</script>

<style scoped>.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>
