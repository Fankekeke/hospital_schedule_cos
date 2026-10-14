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
| ![](./assets/image%20(1).png) | ![](./assets/image%20(2).png) |
| ![](./assets/image%20(3).png) | ![](./assets/image%20(4).png) |
| ![](./assets/image%20(5).png) | ![](./assets/image%20(6).png) |
| ![](./assets/image%20(7).png) | ![](./assets/image%20(8).png) |
| ![](./assets/image%20(9).png) | ![](./assets/image%20(10).png) |
| ![](./assets/image%20(11).png) | ![](./assets/image%20(12).png) |
| ![](./assets/image%20(13).png) | ![](./assets/image%20(14).png) |
| ![](./assets/image%20(15).png) | ![](./assets/image%20(16).png) |
| ![](./assets/image%20(17).png) | ![](./assets/image%20(18).png) |
| ![](./assets/image%20(19).png) | ![](./assets/image%20(20).png) |
| ![](./assets/image%20(21).png) | ![](./assets/image%20(22).png) |
| ![](./assets/image%20(23).png) | ![](./assets/image%20(24).png) |
| ![](./assets/image%20(25).png) | ![](./assets/image%20(26).png) |
| ![](./assets/image%20(27).png) | ![](./assets/image%20(28).png) |
| ![](./assets/image%20(29).png) | ![](./assets/image%20(30).png) |
| ![](./assets/image%20(31).png) | ![](./assets/image%20(32).png) |
| ![](./assets/image%20(33).png) | ![](./assets/image%20(34).png) |
| ![](./assets/image%20(35).png) | ![](./assets/image%20(36).png) |

#### 演示视频

暂无

#### 获取方式

Email: fan1ke2ke@gmail.com

WeChat: `Storm_Berserker`

`附带部署与讲解服务，因为要恰饭资源非免费，伸手党勿扰，谢谢理解😭`

> 1.项目纯原创，不做二手贩子 2.一次购买终身有效 3.项目讲解持续到答辩结束 4.非常负责的答辩指导 5.**黑奴价格**

> 项目部署调试不好包退！功能逻辑没讲明白包退！

<p><img align="center" src="./assets/product_word.png" alt="fankekeke" /></p>

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

<p><img align="center" src="./assets/wechat_group.png" alt="fankekeke" /></p>

<p><img align="center" src="./assets/chat_qr_code.png" alt="fankekeke" width=250 /></p>

**感谢查看**
