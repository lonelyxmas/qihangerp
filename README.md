# 启航跨境电商ERP系统

## 一、项目概述

启航电商ERP系统-跨境版是一套为跨境电商卖家开发的ERP系统，主体功能包括：商品库、同步商品到电商平台、店铺商品管理、店铺分类属性管理、店铺库存管理、订单库、店铺订单管理、库存管理等功能。

目前支持shopfiy独立站和Shein，后续计划支持Amazon、Temu、TikTokShop等。


本系统后端采用SpringCloud微服务架构，前端采用Vue2+Element。

![](docs/preview.png)

## 二、项目说明
**本系统后端采用SpringCloud微服务架构，前端采用vue+element**

### 2.1 主要版本
+ 后端：
  + `Java`: 17
  + `SpringBoot`: 3.0.13
  + `SpringCloud`：2022.0.0
+ 前端：
  + `vue2`
  + `elementUI`

### 2.2 存储及中间件
+ `MySQL`:数据库,版本8.x。
+ `minio文`:文件存储,用于图片存储。
+ `Redis`:在线用户信息、缓存。
+ `Eureka`:注册中心。

### 2.3 项目结构
#### 2.3.1 eureka-server
**注册中心**


#### 2.3.2 gateway
**网关Gateway，使用8080端口**

#### 2.3.3 oms-api
**系统主体OMS功能模块，对接各大平台接口**

#### 2.3.4 sys-api
**系统模块**


#### 2.3.4 core
公共类库

+ common
+ security

#### 2.3.5 vue
**前端项目**


## 三、如何使用？
### 3.1、开发环境配置
+ MySQL数据库创建
  + 运行MySQL脚本`docs\sql\qihangerp-cbe.sql`导入数据到主库`qihangerp-cbe`


+ 启动Redis

  
+ 启动minio


  
### 3.2、启动后端

+ 启动`eureka-server`注册中心
+ 启动`gateway`gateway
+ 启动`oms-api`项目
+ 启动`sys-api`项目

### 3.3、启动前端 `vue`
+ `npm install`
+ `npm run dev`
+ 打包`npm run build:prod`
+ 访问web
  + 访问地址：`http://localhost`
  + 登录名：`admin`
  + 登录密码：`admin123`




## 📦 启航电商开源生态

启航电商旗下开源项目矩阵：

| 项目               | 定位                                     | Gitee | GitHub                                                  |
|:-------------------|:-----------------------------------------|:-----|:--------------------------------------------------------|
| 启航电商ERP  | **电商业务AI底座（单体应用，v4.1）**     | [Gitee](https://gitee.com/qiliping/qihang-erp-open) | [GitHub](https://github.com/zeasin/qihang-erp-open)     |
| OMS 订单中台       | 轻量级订单管理                           | [Gitee](https://gitee.com/qiliping/qihang-oms) | [GitHub](https://github.com/zeasin/qihang-oms)          |
| 启航零售ERP       | 线下零售管理平台                         | [Gitee](https://gitee.com/qiliping/qihang-retail) | [GitHub](https://github.com/zeasin/qihang-retail)          |
| **启航跨境电商ERP ⬅**   | 跨境电商专用版       | [Gitee](https://gitee.com/qiliping/qihang-cb-erp) | [GitHub](https://github.com/zeasin/qihang-cb-erp)                                          |

## 💼 商业版

👉 **[启航电商ERP企业版](https://qihangerp.cn)**





## 📱 关注我们

|                   公众号：启航电商ERP                   |                   个人号：码农老齐                   |
|:-----------------------------------------------:|:--------------------------------------------:|
|                 产品动态·行业方案·客户案例                  |                技术实战·开源故事·创业心得                |
| <img src="docs/wxmp_qherp.jpg" width="200px" /> | <img src="docs/wxmp_qi.jpg" width="200px" /> |


**感谢关注！我希望将从事电商 10 余年的行业经验沉淀在代码中，帮助大家真正提升经营效率。**

💖 如果项目对您有帮助，请点个 **Star ⭐** 给予鼓励！


---

