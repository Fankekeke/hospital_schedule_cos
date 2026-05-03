
<template>
  <a-card :bordered="false" class="face-check-card">
    <div class="face-check-container">
      <a-row :gutter="24">
        <!-- 左侧摄像头区域 -->
        <a-col :span="14">
          <div class="camera-section">
            <div class="section-header">
              <a-icon type="camera" theme="twoTone" twoToneColor="#1890ff" />
              <span class="section-title">人脸识别打卡</span>
              <a-badge
                :status="cameraStatus === 'active' ? 'success' : 'default'"
                :text="cameraStatus === 'active' ? '摄像头已开启' : '摄像头未开启'"
              />
            </div>

            <div class="camera-wrapper">
              <div class="video-container">
                <video
                  id="videoCamera"
                  :width="videoWidth"
                  :height="videoHeight"
                  autoplay
                  playsinline
                ></video>
                <canvas                  style="display:none;"
                                         id="canvasCamera"
                                         :width="videoWidth"
                                         :height="videoHeight"
                ></canvas>

                <!-- 扫描框效果 -->
                <div v-if="cameraStatus === 'active'" class="scan-overlay">
                  <div class="scan-frame">
                    <div class="corner corner-tl"></div>
                    <div class="corner corner-tr"></div>
                    <div class="corner corner-bl"></div>
                    <div class="corner corner-br"></div>
                    <div class="scan-line"></div>
                  </div>
                  <div class="scan-tip">
                    <a-icon type="user" />
                    <span>请将面部对准框内</span>
                  </div>
                </div>
              </div>

              <!-- 拍照预览 -->
              <div v-if="imgSrc" class="preview-container">
                <div class="preview-header">
                  <a-icon type="picture" theme="twoTone" twoToneColor="#52c41a" />
                  <span>识别结果</span>
                </div>
                <div class="preview-image">
                  <img :src="imgSrc" alt="识别截图">
                </div>
              </div>
            </div>

            <div class="camera-controls">
              <a-space size="middle">
                <a-button
                  v-if="cameraStatus !== 'active'"
                  type="primary"
                  icon="video-camera"
                  @click.stop.prevent="getCompetence"
                >
                  打开摄像头
                </a-button>
                <a-button
                  v-if="cameraStatus === 'active'"
                  danger
                  icon="stop"
                  @click.stop.prevent="stopNavigator"
                >
                  关闭摄像头
                </a-button>
                <a-button
                  type="primary"
                  icon="scan"
                  :disabled="cameraStatus !== 'active'"
                  :loading="verifying"
                  @click.stop.prevent="setImage"
                >
                  {{ verifying ? '识别中...' : '开始识别' }}
                </a-button>
              </a-space>
            </div>
          </div>
        </a-col>

        <!-- 右侧说明区域 -->
        <a-col :span="10">
          <div class="info-section">
            <div class="section-header">
              <a-icon type="info-circle" theme="twoTone" twoToneColor="#faad14" />
              <span class="section-title">使用说明</span>
            </div>

            <div class="info-content">
              <a-alert
                message="人脸识别打卡流程"
                type="info"
                show-icon
                :closable="false"
                class="mb-16"
              />

              <div class="steps-list">
                <div class="step-item">
                  <div class="step-number">1</div>
                  <div class="step-content">
                    <div class="step-title">打开摄像头</div>
                    <div class="step-desc">点击"打开摄像头"按钮，允许浏览器访问摄像头权限</div>
                  </div>
                </div>

                <div class="step-item">
                  <div class="step-number">2</div>
                  <div class="step-content">
                    <div class="step-title">调整位置</div>
                    <div class="step-desc">将面部对准扫描框，保持光线充足，正对摄像头</div>
                  </div>
                </div>

                <div class="step-item">
                  <div class="step-number">3</div>
                  <div class="step-content">
                    <div class="step-title">开始识别</div>
                    <div class="step-desc">点击"开始识别"按钮，系统将进行人脸识别验证</div>
                  </div>
                </div>

                <div class="step-item">
                  <div class="step-number">4</div>
                  <div class="step-content">
                    <div class="step-title">完成打卡</div>
                    <div class="step-desc">识别成功后自动完成打卡，显示识别结果</div>
                  </div>
                </div>
              </div>

              <a-divider />

              <div class="tips-section">
                <h4 class="tips-title">
                  <a-icon type="exclamation-circle" theme="twoTone" twoToneColor="#ff4d4f" />
                  注意事项
                </h4>
                <ul class="tips-list">
                  <li><a-icon type="check" style="color: #52c41a;" /> 请确保光线充足，避免逆光</li>
                  <li><a-icon type="check" style="color: #52c41a;" /> 摘下口罩、墨镜等遮挡物</li>
                  <li><a-icon type="check" style="color: #52c41a;" /> 保持面部在扫描框内</li>
                  <li><a-icon type="check" style="color: #52c41a;" /> 识别时请勿大幅移动</li>
                  <li><a-icon type="check" style="color: #52c41a;" /> 支持正常化妆，避免浓妆</li>
                </ul>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
  </a-card>
</template>

<script>import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Post',
  components: {RangeDate},
  data () {
    return {
      videoWidth: 640,
      videoHeight: 480,
      imgSrc: '',
      localImageSrc: '',
      thisCancas: null,
      thisContext: null,
      thisVideo: null,
      orderDetail: null,
      verifyLoading: false,
      cameraStatus: 'inactive',
      verifying: false
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    })
  },
  mounted () {
  },
  methods: {
    getCompetence () {
      var _this = this
      this.thisCancas = document.getElementById('canvasCamera')
      this.thisContext = this.thisCancas.getContext('2d')
      this.thisVideo = document.getElementById('videoCamera')

      if (navigator.mediaDevices === undefined) {
        navigator.mediaDevices = {}
      }

      if (navigator.mediaDevices.getUserMedia === undefined) {
        navigator.mediaDevices.getUserMedia = function (constraints) {
          var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.getUserMedia
          if (!getUserMedia) {
            return Promise.reject(new Error('getUserMedia is not implemented in this browser'))
          }
          return new Promise(function (resolve, reject) {
            getUserMedia.call(navigator, constraints, resolve, reject)
          })
        }
      }

      var constraints = {
        audio: false,
        video: {
          width: this.videoWidth,
          height: this.videoHeight,
          facingMode: 'user'
        }
      }

      navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
        _this.cameraStatus = 'active'

        if ('srcObject' in _this.thisVideo) {
          _this.thisVideo.srcObject = stream
        } else {
          _this.thisVideo.src = window.URL.createObjectURL(stream)
        }

        _this.thisVideo.onloadedmetadata = function (e) {
          _this.thisVideo.play()
        }

        _this.$message.success('摄像头已开启')
      }).catch(err => {
        console.log(err)
        _this.$message.error('无法访问摄像头，请检查权限设置')
      })
    },

    setImage () {
      if (this.cameraStatus !== 'active') {
        this.$message.warning('请先打开摄像头')
        return
      }

      var _this = this
      _this.thisContext.drawImage(_this.thisVideo, 0, 0, _this.videoWidth, _this.videoHeight)
      var image = this.thisCancas.toDataURL('image/png')
      let data = { file: image.replace(/^data:image\/\w+;base64,/, '') }

      this.verifying = true
      this.$post('/cos/face/verification', data).then((r) => {
        this.verifying = false
        if (r.data.code == 500) {
          this.$message.error(r.data.msg)
        } else {
          this.$message.success(r.data.msg)
          this.imgSrc = image
        }
      }).catch(() => {
        this.verifying = false
        this.$message.error('识别失败，请重试')
      })
    },

    scanQRCode () {
    },

    dataURLtoFile (dataurl, filename) {
      var arr = dataurl.split(',')
      var mime = arr[0].match(/:(.*?);/)[1]
      var bstr = atob(arr[1])
      var n = bstr.length
      var u8arr = new Uint8Array(n)
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
      }
      return new File([u8arr], filename, { type: mime })
    },

    stopNavigator () {
      if (this.thisVideo && this.thisVideo.srcObject) {
        this.thisVideo.srcObject.getTracks()[0].stop()
        this.cameraStatus = 'inactive'
        this.imgSrc = ''
        this.$message.success('摄像头已关闭')
      }
    },

    beforeUpload (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        this.localImageSrc = e.target.result
      }
      reader.readAsDataURL(file)
      return false
    },

    scanLocalQRCode () {
      if (!this.localImageSrc) {
        this.$message.warning('请先选择本地图片')
        return
      }

      const img = new Image()
      img.onload = () => {
        this.thisCancas.width = img.width
        this.thisCancas.height = img.height
        this.thisContext.drawImage(img, 0, 0)
        const imageData = this.thisContext.getImageData(0, 0, img.width, img.height)
        const code = jsQR(imageData.data, imageData.width, imageData.height)

        if (code) {
          this.$message.success('二维码内容: ' + code.data)
          this.queryScenicOrderDetail(code.data)
        } else {
          this.$message.warning('未检测到二维码')
        }
      }
      img.src = this.localImageSrc
    },

    queryScenicOrderDetail (orderCode) {
      this.$get('/cos/scenic-order/queryScenicOrderDetail', { orderCode: orderCode }).then((r) => {
        this.orderDetail = r.data.data
        console.log(this.orderDetail)
      })
    },

    verifyOrder () {
      this.verifyLoading = true
      this.$get('/cos/scenic-order/verifyOrder', {
        orderCode: this.orderDetail.code
      }).then((r) => {
        this.verifyLoading = false
        this.$message.success('订单核销成功')
        this.orderDetail.orderStatus = 2
        this.queryScenicOrderDetail(this.orderDetail.code)
      }).catch((error) => {
        this.verifyLoading = false
        this.$message.error('核销请求失败: ' + error.message)
      })
    }
  },
  watch: {}
}
</script>

<style lang="less" scoped>.face-check-card {
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.face-check-container {
  padding: 16px 0;
}

.camera-section, .info-section {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
  padding: 24px;
  height: 100%;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8e8e8;

  .anticon {
    font-size: 20px;
    margin-right: 8px;
  }

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #2c3e50;
    flex: 1;
  }
}

.camera-wrapper {
  margin-bottom: 20px;
}

.video-container {
  position: relative;
  width: 100%;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

  video {
    display: block;
    width: 100%;
    height: auto;
    transform: scaleX(-1);
  }
}

.scan-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  pointer-events: none;
}

.scan-frame {
  position: relative;
  width: 280px;
  height: 280px;
  border: 2px solid rgba(24, 144, 255, 0.3);
  border-radius: 12px;
  animation: frame-pulse 2s ease-in-out infinite;
}

.corner {
  position: absolute;
  width: 30px;
  height: 30px;
  border: 3px solid #1890ff;

  &.corner-tl {
    top: -2px;
    left: -2px;
    border-right: none;
    border-bottom: none;
    border-top-left-radius: 12px;
  }

  &.corner-tr {
    top: -2px;
    right: -2px;
    border-left: none;
    border-bottom: none;
    border-top-right-radius: 12px;
  }

  &.corner-bl {
    bottom: -2px;
    left: -2px;
    border-right: none;
    border-top: none;
    border-bottom-left-radius: 12px;
  }

  &.corner-br {
    bottom: -2px;
    right: -2px;
    border-left: none;
    border-top: none;
    border-bottom-right-radius: 12px;
  }
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(to right, transparent, #1890ff, transparent);
  animation: scan-move 2s linear infinite;
  box-shadow: 0 0 8px #1890ff;
}

@keyframes scan-move {
  0% {
    top: 0;
  }
  100% {
    top: 100%;
  }
}

@keyframes frame-pulse {
  0%, 100% {
    border-color: rgba(24, 144, 255, 0.3);
  }
  50% {
    border-color: rgba(24, 144, 255, 0.6);
  }
}

.scan-tip {
  margin-top: 20px;
  padding: 12px 24px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 24px;
  color: #fff;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;

  .anticon {
    font-size: 16px;
  }
}

.preview-container {
  margin-top: 16px;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .preview-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    font-size: 14px;
    font-weight: 500;
    color: #52c41a;

    .anticon {
      margin-right: 6px;
      font-size: 16px;
    }
  }

  .preview-image {
    text-align: center;

    img {
      max-width: 100%;
      max-height: 200px;
      border-radius: 6px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}

.camera-controls {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
}

.info-content {
  .mb-16 {
    margin-bottom: 16px;
  }
}

.steps-list {
  margin-top: 16px;
}

.step-item {
  display: flex;
  margin-bottom: 16px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateX(4px);
  }

  .step-number {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 14px;
    margin-right: 12px;
    flex-shrink: 0;
    box-shadow: 0 2px 6px rgba(24, 144, 255, 0.3);
  }

  .step-content {
    flex: 1;

    .step-title {
      font-weight: 500;
      color: #2c3e50;
      margin-bottom: 4px;
      font-size: 14px;
    }

    .step-desc {
      font-size: 12px;
      color: #666;
      line-height: 1.5;
    }
  }
}

.tips-section {
  .tips-title {
    display: flex;
    align-items: center;
    font-size: 14px;
    font-weight: 600;
    color: #2c3e50;
    margin-bottom: 12px;

    .anticon {
      margin-right: 6px;
      font-size: 16px;
    }
  }

  .tips-list {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      padding: 6px 0;
      font-size: 13px;
      color: #666;
      line-height: 1.6;
      display: flex;
      align-items: flex-start;

      .anticon {
        margin-right: 6px;
        margin-top: 2px;
        flex-shrink: 0;
      }
    }
  }
}
</style>
