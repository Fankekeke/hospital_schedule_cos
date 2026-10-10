![](https://img.shields.io/badge/License-MIT-blue.svg)

![](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg)

![](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)

# 医疗考勤打卡系统 (Medical Attendance System)

一个专为医疗机构（如医院、诊所、科室）设计的全方位考勤与排班管理系统。系统深度结合医疗行业的特殊工作场景，提供从排班、打卡、审批到数据分析的完整闭环，旨在提升医院人事管理效率，保障医疗合规性。

#### 安装环境
️**注意**：请确保您的开发环境已安装以下依赖。

JAVA 环境 

Node.js环境 [https://nodejs.org/en/] 选择14.17

Yarn 打开cmd， 输入npm install -g yarn !!!必须安装完毕nodejs

Mysql 数据库 一定要把账户和密码记住

redis

Idea 编译器

WebStorm OR VScode 编译器

#### 采用技术及功能

后端：SpringBoot、MybatisPlus、MySQL、Redis、
前端：Vue、Apex、Antd、Axios

平台前端：vue(框架) + vuex(全局缓存) + rue-router(路由) + axios(请求插件) + apex(图表)  + antd-ui(ui组件)

平台后台：springboot(框架) + redis(缓存中间件) + shiro(权限中间件) + mybatisplus(orm) + restful风格接口 + mysql(数据库)

开发环境：windows10 or windows7 ， vscode or webstorm ， idea + lambok

---

## 核心特性

- **双端权限隔离**：提供功能丰富的管理员后台与便捷的员工移动端。
- **医疗场景定制**：支持临派排班、调班申请、加班认定等医疗行业专属流程。
- **智能考勤打卡**：集成 GPS 电子围栏与人脸识别，确保考勤数据真实有效。
- **多维度数据看板**：提供出缺勤率、加班成本、合规预警等核心指标分析。
- **灵活规则引擎**：支持自定义班次定义、加班规则及考勤规则配置。

---

## ️ 功能模块概览

### 管理员端 (Admin Dashboard)

管理员端提供全局视角的考勤数据监控与规则配置能力：

**数据看板与统计**

- **当日考勤仪表盘**：实时掌握全院/科室当日出勤动态。
- **考勤看板 / 排班看板**：可视化展示排班与考勤状态。
- **考勤汇总**：一键生成多维度考勤统计报表。
- **出缺勤率 / 异常频次**：量化分析员工出勤健康度。
- **加班成本**：自动核算并展示加班成本数据。
- **牛马排名**：趣味/直观展示加班时长或出勤排名（可自定义指标）。

**规则与基础配置**

- **考勤规则 / 加班规则**：灵活配置各类考勤与加班计算逻辑。
- **班次定义 / 班次覆盖**：支持复杂医疗班次的创建与覆盖管理。
- **电子围栏**：精准设置打卡地理范围。
- **科室管理 / 岗位管理 / 医生管理**：维护组织架构与人员基础信息。

**排班与审批**

- **临派排班**：快速处理突发或临时排班需求。
- **排班差异**：对比计划排班与实际排班的差异。
- **考勤审批**：集中处理请假、调班、补卡等审批流。

**消息与合规**

- **消息通知 / 公告信息**：定向或全局推送系统通知与医院公告。
- **合规预警**：自动识别并预警超时加班、连续排班等合规风险。
- **人脸注册**：统一管理员工人脸生物特征库。
- **人脸打卡**：支持管理员端查看或管理人脸打卡记录。
- **打卡记录**：全量查询与追溯员工原始打卡数据。

---

### 员工端 (Employee Portal)

员工端注重移动化与自助服务，提升日常使用体验：

- **GPS经纬度打卡**：基于地理位置的精准签到/签退。
- **人脸识别打卡**：秒级识别，杜绝代打卡。
- **个人信息**：查看与维护个人基础档案。
- **我的考勤**：实时查看个人考勤日历、排班计划与出勤状态。
- **考勤审批**：在线发起请假、调班、补卡等申请并追踪进度。
- **临派班次 / 调班申请**：快速响应临时排班或自主发起换班。
- **加班认定**：提交加班申请并确认加班时长。
- **消息通知 / 公告查看**：第一时间接收排班变动、医院通知及系统消息。

---

## 项目结构

```text
pharmacy-dispensing-system/
├── backend/            # 后端 API 服务
│   ├── src/
│   │   ├── controllers/  # 控制器
│   │   ├── models/       # 数据模型
│   │   ├── routes/       # 路由
│   │   └── services/     # 业务逻辑
│   └── package.json
├── frontend/           # 前端 Web 应用
│   ├── src/
│   │   ├── views/      # 页面视图
│   │   ├── components/ # 公共组件
│   │   └── store/      # 状态管理
│   └── package.json
├── docs/               # 项目文档与 API 接口说明
├── LICENSE
└── README.md
```
---


#### 前台启动方式
安装所需文件 yarn install 
运行 yarn run dev

#### 默认后台账户密码
[管理员]
admin
123456

[员工]
fank
123456

#### 项目截图

|  |  |
|---------------------|---------------------|
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/85df5eb3-5cda-4084-8ce1-0912abf027d4.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/cf9d33fe-2676-4ae1-883a-f9e836e70163.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/82e9aad1-f82e-41ab-85f1-877d1bb2475c.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/cc652f33-9ad3-4580-bdfe-f6fb944e4cd4.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/55c04d94-c828-45d4-99bc-cadde05f1dcb.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/cb6fa1b7-d616-4327-921b-001296f20ba6.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/027afb2e-5baa-409e-8308-f1ed2f318587.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/bdae0964-0f11-4f86-bc49-5c2cb97c5eea.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/8eced760-5bdd-444b-9df9-214f6c2d5f68.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/8905321d-916d-4901-865e-0c87e7e39984.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/8c11c045-c263-487d-882b-a07d936fe2ea.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/562560b7-954a-4d7f-82fe-ac2a5a2105de.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/5bf29a80-1ecd-4871-9427-16d63c49707e.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/8497b25e-e98c-40df-a36c-9c5f930cf632.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/4ec3d88c-038d-41bc-8f1f-63befec8f473.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/5790bc36-eaed-46b8-8ce1-9a0eef48b491.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/4a8ad167-6317-4c8e-8122-0145e833acf6.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/5767e077-bc5a-47c1-a3ab-2426f7df8e11.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/3f43a3db-0ee3-4012-9f6b-04a7a6971ccc.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1152ba41-5a0f-49af-b874-8da09214a030.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/1df1286f-5463-4af7-80a3-ba2d3a1abacd.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/999f0f99-7e3f-40a2-b669-d370982b3697.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/f0882a8d-b462-4b13-acdb-92433b12c0f0.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/936e2c15-cc7e-4d2b-b691-c3b231c7b6cc.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/f1ae701e-04e3-4a6b-8791-589f9c1af310.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/868c7d6d-0d09-4604-bc02-e396766aa4cf.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/ea8523cb-5862-4497-8917-082aa0d71e4e.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/846d496e-9132-4d49-bf9e-593bf5bffc12.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/e4445239-f4f2-4f7a-b211-dd13d6a39470.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/756c2b14-96d8-4121-9650-e25f5726430a.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/e988e38a-d771-4974-8946-77af3aac86bd.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/466a732c-67c7-4db8-ac5a-cf7efabeb1d2.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/d8952805-7cfd-4b72-99c3-bc6f7c41fef8.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/436c5f38-9e5a-4de0-8613-8bdbb0727447.png) |
| ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/cf72ae8a-b8f6-4338-b067-15dfc0896b6f.png) | ![](https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/416a55a8-e7a9-408b-bbbe-74f15862c4e4.png) |

#### 演示视频

暂无

#### 获取方式

Email: fan1ke2ke@gmail.com

WeChat: `Storm_Berserker`

`附带部署与讲解服务，因为要恰饭资源非免费，伸手党勿扰，谢谢理解😭`

> 1.项目纯原创，不做二手贩子 2.一次购买终身有效 3.项目讲解持续到答辩结束 4.非常负责的答辩指导 5.**黑奴价格**

> 项目部署调试不好包退！功能逻辑没讲明白包退！

#### 其它资源
[2026年-答辩顺利通过-客户评价🀄](https://berserker287.github.io/2026/06/29/2026%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2025年-答辩顺利通过-客户评价🍜](https://berserker287.github.io/2025/06/18/2025%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2024年-答辩顺利通过-客户评价👻](https://berserker287.github.io/2024/06/06/2024%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2023年-答辩顺利通过-客户评价🐢](https://berserker287.github.io/2023/06/14/2023%E5%B9%B4%E7%AD%94%E8%BE%A9%E9%A1%BA%E5%88%A9%E9%80%9A%E8%BF%87/)

[2022年-答辩通过率100%-客户评价🐣](https://berserker287.github.io/2022/05/25/%E9%A1%B9%E7%9B%AE%E4%BA%A4%E6%98%93%E8%AE%B0%E5%BD%95/)

[毕业答辩导师提问的高频问题](https://berserker287.github.io/2023/06/13/%E6%AF%95%E4%B8%9A%E7%AD%94%E8%BE%A9%E5%AF%BC%E5%B8%88%E6%8F%90%E9%97%AE%E7%9A%84%E9%AB%98%E9%A2%91%E9%97%AE%E9%A2%98/)

[50个高频答辩问题-技术篇](https://berserker287.github.io/2023/06/13/50%E4%B8%AA%E9%AB%98%E9%A2%91%E7%AD%94%E8%BE%A9%E9%97%AE%E9%A2%98-%E6%8A%80%E6%9C%AF%E7%AF%87/)

[计算机毕设答辩时都会问到哪些问题？](https://www.zhihu.com/question/31020988)

[计算机专业毕业答辩小tips](https://zhuanlan.zhihu.com/p/145911029)

#### 接JAVAWEB毕设，纯原创，价格公道，诚信第一

`网站建设、小程序、H5、APP、各种系统 选题+开题报告+任务书+程序定制+安装调试+项目讲解+论文+答辩PPT`

More info: [悲伤的橘子树](https://berserker287.github.io/)

<p><img align="center" src="https://fank-bucket-oss.oss-cn-beijing.aliyuncs.com/img/%E5%90%88%E4%BD%9C%E7%89%A9%E6%96%99%E6%A0%B7%E5%BC%8F%20(3).png" alt="fankekeke" /></p>
****
