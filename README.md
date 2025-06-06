- # 经络心智 (MeridianMind) 中医智能问诊系统开发指南

  ## 一、项目概述

  经络心智是一款融合人工智能与传统中医理论的智能问诊系统，旨在通过数字化手段传承和普及中医知识，提高中医诊疗效率，为基层医疗机构和偏远地区提供优质的中医诊疗支持。系统利用大模型技术与知识图谱，实现中医四诊信息采集、辨证论治和个性化治疗方案推荐。

  ### 项目介绍

  MeridianMind 是一个基于芋道开源框架的医疗智能问诊系统，旨在为医疗行业提供先进的AI问诊解决方案。该系统利用先进的大语言模型进行自然语言处理，使医疗诊断和患者咨询更加准确和高效。

  #### 核心功能

  + **智能问诊**：利用AI大模型与患者进行自然语言交流，收集症状信息并提供初步诊断建议
  + **医疗知识库**：内置医疗专业知识库，为AI模型提供专业医疗信息支持
  + **多模型集成**：支持接入多种AI大模型，包括国内外主流模型
  + **患者管理**：完整的患者信息管理系统，包括病史、就诊记录等
  + **诊断记录**：保存完整的问诊过程和诊断结果，便于后续跟踪和分析

  #### 技术特点

  + 基于流式问答技术，提供快速响应的用户体验
  + 利用现有AI模块的能力，实现医疗领域特定的智能问答
  + 支持定制化医疗提示词，提高诊断的准确性和专业性
  + 多端支持，包括Web、App和小程序

  #### 项目结构

  + **MeridianMind-server**：后端服务，基于Spring Boot框架
    - yudao-module-medical：医疗模块，提供智能问诊系统核心功能
    - yudao-module-ai：AI模块，提供大模型集成能力
  + **MeridianMind-admin**：管理后台前端，基于Vue3框架
  + **MeridianMind-app**：移动端App，基于React Native框架

  ## 二、技术架构

  ### 1. 整体架构

  ![](https://cdn.nlark.com/yuque/0/2025/svg/34535766/1740551736743-01b773fb-b8da-49f0-a45a-78a599f573d3.svg)

  ### 2. 技术栈

  #### 后端技术栈

  + **框架**: Spring Boot 3.4.1
  + **ORM**: MyBatis-Plus 3.5.9
  + **数据库**: MySQL + Redis (Redisson)
  + **文档**: Knife4j 4.6.0 (OpenAPI)
  + **安全**: Spring Security
  + **工具库**: Hutool、MapStruct、EasyExcel

  #### 前端技术栈

  + **核心框架**: unibest（`uniapp` + `Vue3` + `Ts` + `Vite5` + `UnoCss`）
  + **开发工具**: VSCode/WebStorm + uni插件
  + **UI组件库**: wot-ui

  #### AI 推理层

  + **大模型平台**: qywit 平台私有部署的 DeepSeek 模型
  + **知识图谱**: 自建中医知识图谱

  ## 三、系统功能模块

  ![](https://cdn.nlark.com/yuque/__mermaid_v3/0aecb0dcfd2074c3e421902a6f4229d8.svg)

  ### 1. 问诊交互模块

  #### 1.1 患者信息采集

  + 个人基础信息
  + 体格参数
  + 既往病史
  + 家族病史

  #### 1.2 四诊信息采集

  + 望诊：舌象、面色分析
  + 闻诊：声音、气味特征记录
  + 问诊：结构化问诊流程
  + 切诊：脉象记录与分析

  ### 2. 诊断分析模块

  + 症状分析归类
  + 证型辨识
  + 病因分析
  + 体质判断
  + 诊断建议生成

  ### 3. 治疗方案模块

  + 治疗原则制定
  + 方剂推荐
  + 针灸穴位建议
  + 调养建议
  + 预防保健指导

  ### 4. 健康管理模块

  + 健康记录跟踪
  + 随访管理
  + 调理效果评估
  + 个性化健康报告

  ## 四、开发指南

  ### 1. 环境搭建

  #### 后端环境搭建

  ```bash
  # 1. 克隆项目骨架
  git clone https://github.com/YourOrg/MeridianMind-server.git
  
  # 2. 安装 JDK 17+ 和 Maven
  
  # 3. 初始化数据库
  mysql -u root -p < docs/sql/meridian-mind.sql
  
  # 4. 配置 application.yml
  # 修改数据库、Redis连接信息
  
  # 5. 编译运行
  mvn clean package
  java -jar target/meridian-mind.jar
  ```

  ### 2. 后端开发指南

  #### 2.1 核心模块实现

  首先配置基础框架，创建以下核心包结构：

  ```plain
  src/main/java/cn/medapp/meridianmind/
  ├── common/           # 通用工具类和常量
  ├── config/           # 配置类
  ├── controller/       # 控制器
  ├── domain/           # 领域模型
  │   ├── entity/       # 数据库实体
  │   ├── vo/           # 视图对象
  │   └── dto/          # 数据传输对象
  ├── repository/       # 数据访问层
  ├── service/          # 服务层
  │   ├── impl/         # 服务实现
  │   └── convert/      # 对象转换
  ├── ai/               # AI相关组件
  │   ├── model/        # 模型交互
  │   └── knowledge/    # 知识图谱
  └── MeridianMindApplication.java   # 启动类
  ```

  #### 2.2 知识图谱构建

  根据设计文档构建中医知识图谱：

  ```java
  // 1. 定义实体模型
  @Data
  public class SymptomNode {
      private String symptomId;
      private String symptomName;
      private String description;
      private String bodyPart;
      private List<String> properties;
      // 其他属性...
  }
  
  // 2. 定义知识图谱服务
  @Service
  public class KnowledgeGraphService {
      
      // 初始化知识图谱
      public void initializeGraph() {
          // 从知识库加载数据
      }
      
      // 查询症状相关的证型
      public List<SyndromeNode> findRelatedSyndromes(List<String> symptomIds) {
          // 实现查询逻辑
      }
      
      // 查询证型相关的方剂
      public List<FormulaNode> findRelatedFormulas(String syndromeId) {
          // 实现查询逻辑
      }
  }
  ```

  #### 2.3 AI模型服务实现

  ```java
  @Service
  public class AiModelService {
      
      @Value("${ai.model.url}")
      private String modelUrl;
      
      private final RestTemplate restTemplate;
      
      // 执行四诊数据分析
      public DiagnosisResult analyzeFourDiagnoses(FourDiagnosesDTO data) {
          // 构建请求参数
          Map<String, Object> params = new HashMap<>();
          params.put("diagnosticData", data);
          params.put("task", "syndrome_differentiation");
          
          // 调用AI模型
          ResponseEntity<DiagnosisResult> response = 
              restTemplate.postForEntity(modelUrl + "/analyze", params, DiagnosisResult.class);
          
          return response.getBody();
      }
      
      // 生成治疗方案
      public TreatmentPlan generateTreatmentPlan(DiagnosisResult diagnosis) {
          // 实现治疗方案生成逻辑
      }
  }
  ```

  #### 2.4 RESTful API 设计

  为移动应用提供API接口：

  ```java
  @RestController
  @RequestMapping("/api/v1/diagnosis")
  @Tag(name = "诊断接口", description = "提供中医诊断相关的API")
  public class DiagnosisController {
      
      private final DiagnosisService diagnosisService;
      
      @PostMapping("/analyze")
      @Operation(summary = "分析四诊数据", description = "根据四诊信息进行中医辨证")
      public CommonResult<DiagnosisVO> analyzeDiagnosticData(
              @RequestBody @Valid DiagnosticDataDTO data) {
          DiagnosisVO result = diagnosisService.analyze(data);
          return CommonResult.success(result);
      }
      
      @GetMapping("/{id}")
      @Operation(summary = "获取诊断结果", description = "根据ID获取诊断详情")
      public CommonResult<DiagnosisDetailVO> getDiagnosisById(@PathVariable Long id) {
          DiagnosisDetailVO detail = diagnosisService.getDetailById(id);
          return CommonResult.success(detail);
      }
  }
  ```

  ### 3. 前端开发指南

  #### 3.1 页面结构设计

  使用 unibest 框架的目录结构组织代码：

  ```bash
  meridian-mind-app/
  ├── .husky/                 # Git hooks配置
  ├── dist/                   # 编译输出目录
  ├── env/                    # 环境变量配置
  │   ├── .env                # 基础环境变量
  │   ├── .env.development    # 开发环境变量
  │   ├── .env.production     # 生产环境变量
  │   └── .env.staging        # 测试环境变量
  ├── node_modules/           # 依赖包
  ├── public/                 # 静态资源目录
  │   ├── favicon.ico         # 网站图标
  │   └── static/             # 其他静态资源
  ├── scripts/                # 构建脚本
  │   ├── build.js            # 构建脚本
  │   ├── postupgrade.js      # 升级后处理脚本
  │   └── release.js          # 发布脚本
  ├── src/                    # 源代码目录
  │   ├── api/                # API请求
  │   │   ├── index.ts        # API导出
  │   │   ├── modules/        # API模块
  │   │   │   ├── user.ts     # 用户相关API
  │   │   │   └── message.ts  # 消息相关API
  │   │   └── request.ts      # 请求封装
  │   ├── assets/             # 资源文件
  │   │   ├── fonts/          # 字体文件
  │   │   ├── images/         # 图片资源
  │   │   └── icons/          # 图标资源
  │   ├── components/         # 全局组件
  │   │   ├── common/         # 通用组件
  │   │   │   ├── MButton.vue # 按钮组件
  │   │   │   └── MCard.vue   # 卡片组件
  │   │   └── business/       # 业务组件
  │   │       ├── UserInfo.vue# 用户信息组件
  │   │       └── MessageItem.vue # 消息条目组件
  │   ├── config/             # 全局配置
  │   │   ├── index.ts        # 配置导出
  │   │   └── settings.ts     # 应用设置
  │   ├── constants/          # 常量定义
  │   │   ├── index.ts        # 常量导出
  │   │   ├── api.ts          # API常量
  │   │   └── enum.ts         # 枚举常量
  │   ├── hooks/              # 自定义钩子
  │   │   ├── useAuth.ts      # 认证钩子
  │   │   └── useMessage.ts   # 消息钩子
  │   ├── interceptors/       # 拦截器
  │   │   ├── request.ts      # 请求拦截器
  │   │   └── response.ts     # 响应拦截器
  │   ├── layouts/            # 布局文件
  │   │   ├── BasicLayout.vue # 基础布局
  │   │   └── EmptyLayout.vue # 空布局
  │   ├── pages/              # 页面
  │   │   ├── index/          # 首页
  │   │   │   └── index.vue   # 首页组件
  │   │   ├── login/          # 登录页
  │   │   │   └── index.vue   # 登录页组件
  │   │   └── message/        # 消息页
  │   │       └── index.vue   # 消息页组件
  │   ├── pages-sub/          # 子页面
  │   │   └── settings/       # 设置子页面
  │   │       └── index.vue   # 设置页组件
  │   ├── service/            # 服务
  │   │   ├── http.ts         # HTTP服务
  │   │   └── storage.ts      # 存储服务
  │   ├── static/             # 静态资源
  │   │   ├── images/         # 图片
  │   │   └── styles/         # 样式
  │   ├── store/              # 状态管理
  │   │   ├── index.ts        # 状态管理入口
  │   │   ├── modules/        # 状态模块
  │   │   │   ├── user.ts     # 用户状态
  │   │   │   └── app.ts      # 应用状态
  │   │   └── types.ts        # 状态类型定义
  │   ├── style/              # 全局样式
  │   │   ├── index.scss      # 主样式入口
  │   │   ├── variables.scss  # 变量定义
  │   │   └── mixins.scss     # 混合器定义
  │   ├── types/              # 类型定义
  │   │   ├── index.ts        # 类型导出
  │   │   ├── user.ts         # 用户类型
  │   │   └── message.ts      # 消息类型
  │   ├── uni_modules/        # uni扩展组件
  │   ├── utils/              # 工具函数
  │   │   ├── index.ts        # 工具函数导出
  │   │   ├── auth.ts         # 认证工具
  │   │   ├── formatter.ts    # 格式化工具
  │   │   └── validate.ts     # 验证工具
  │   ├── App.vue             # 应用根组件
  │   ├── env.d.ts            # 环境类型声明
  │   ├── main.ts             # 应用入口
  │   ├── manifest.json       # 应用配置
  │   ├── pages.json          # 页面配置
  │   ├── typings.ts          # 全局类型定义
  │   └── uni.scss            # uni-app样式变量
  ├── .commitlintrc.cjs       # Commit规范配置
  ├── .editorconfig           # 编辑器配置
  ├── .eslintignore           # ESLint忽略配置
  ├── .eslintrc.cjs           # ESLint配置
  ├── .gitignore              # Git忽略配置
  ├── .npmrc                  # NPM配置
  ├── .prettierignore         # Prettier忽略配置
  ├── .prettierrc.cjs         # Prettier配置
  ├── .stylelintignore        # StyleLint忽略配置
  ├── .stylelintrc.cjs        # StyleLint配置
  ├── index.html              # HTML模板
  ├── LICENSE                 # 许可证
  ├── manifest.config.ts      # 应用清单配置
  ├── package.json            # 项目依赖配置
  ├── pages.config.ts         # 页面路由配置
  ├── pnpm-lock.yaml          # 依赖锁定文件
  ├── README.md               # 项目说明文档
  ├── tsconfig.json           # TypeScript配置
  ├── uno.config.ts           # UnoCSS配置
  └── vite.config.ts          # Vite构建配置
  ```
  
  #### 3.2 UI设计风格实现
  
  根据设计风格要求，设置主题：
  
  ```javascript
  // uno.config.ts
  import { defineConfig } from 'unocss'
  import { presetAttributify, presetUno } from 'unocss'
  
  export default defineConfig({
    presets: [
      presetAttributify(),
      presetUno()
    ],
    theme: {
      colors: {
        primary: '#3A7D44',     // 中国传统绿色
        secondary: '#9CC5A1',   // 淡雅绿色
        accent: '#5D3A3A',      // 中医药材色
        background: '#F9F5F0',  // 米白色背景
        surface: '#FFFFFF',
        text: '#333333',
        disabled: '#C7C7CD',
        placeholder: '#9B9B9B',
      },
      fontFamily: {
        sans: ['PingFang SC', 'Helvetica Neue', 'Arial', 'sans-serif'],
        serif: ['Songti SC', 'SimSun', 'serif'],
      }
    },
    shortcuts: {
      'btn': 'px-4 py-2 rounded-lg font-medium',
      'btn-primary': 'btn bg-primary text-white',
      'btn-outline': 'btn border border-primary text-primary',
      'card': 'bg-white rounded-lg shadow p-4',
      'form-label': 'text-sm text-text mb-1 block',
      'form-input': 'w-full p-3 rounded-lg border border-gray-300 focus:border-primary',
    }
  })
  ```
  
  设置全局样式
  
  ```css
  // styles/variables.scss
  :root {
    --primary-color: #3A7D44;
    --secondary-color: #9CC5A1;
    --accent-color: #5D3A3A;
    --background-color: #F9F5F0;
    --surface-color: #FFFFFF;
    --text-color: #333333;
    --disabled-color: #C7C7CD;
    --placeholder-color: #9B9B9B;
  
    // 字体大小
    --font-size-xs: 12px;
    --font-size-sm: 14px;
    --font-size-md: 16px;
    --font-size-lg: 18px;
    --font-size-xl: 20px;
    --font-size-xxl: 24px;
  
    // 间距
    --spacing-xs: 4px;
    --spacing-sm: 8px;
    --spacing-md: 16px;
    --spacing-lg: 24px;
    --spacing-xl: 32px;
  
    // 圆角
    --radius-sm: 4px;
    --radius-md: 8px;
    --radius-lg: 16px;
  }
  ```
  
  #### 3.3 四诊信息采集界面实现
  
  以问诊模块为例：
  
  ```jsx
  <!-- pages/diagnosis/four-diagnoses.vue -->
  <template>
    <view class="page-container bg-background">
      <view class="p-4">
        <text class="text-xl font-bold text-primary">四诊信息采集</text>
        <text class="text-sm text-text mt-2 mb-4 block">
          通过望、闻、问、切全面了解您的健康状况
        </text>
        
        <!-- 进度条 -->
        <view class="mb-4">
          <progress :percent="(activeStep + 1) / 4 * 100" stroke-width="3" 
                    activeColor="var(--primary-color)" backgroundColor="#E5E5E5" />
        </view>
        
        <!-- 步骤内容 -->
        <view class="card">
          <template v-if="activeStep === 0">
            <view class="p-2">
              <text class="text-lg font-medium">望诊</text>
              <text class="text-sm text-gray-600">观察舌象、面色等</text>
            </view>
            <view class="p-2">
              <tongue-analyzer @analysis-complete="updateDiagnosticData('inspection', { tongue: $event })" />
              <!-- 其他望诊组件 -->
            </view>
          </template>
          
          <template v-else-if="activeStep === 1">
            <view class="p-2">
              <text class="text-lg font-medium">闻诊</text>
              <text class="text-sm text-gray-600">记录声音、气味特征</text>
            </view>
            <view class="p-2">
              <!-- 闻诊组件 -->
            </view>
          </template>
          
          <template v-else-if="activeStep === 2">
            <view class="p-2">
              <text class="text-lg font-medium">问诊</text>
              <text class="text-sm text-gray-600">详细了解症状</text>
            </view>
            <view class="p-2">
              <symptom-selector @selection-change="updateDiagnosticData('inquiry', { symptoms: $event })" />
              <!-- 其他问诊组件 -->
            </view>
          </template>
          
          <template v-else-if="activeStep === 3">
            <view class="p-2">
              <text class="text-lg font-medium">切诊</text>
              <text class="text-sm text-gray-600">脉象记录</text>
            </view>
            <view class="p-2">
              <pulse-recorder @record-complete="updateDiagnosticData('palpation', { pulse: $event })" />
              <!-- 其他切诊组件 -->
            </view>
          </template>
        </view>
        
        <!-- 底部按钮 -->
        <view class="flex justify-between mt-4">
          <button class="btn-outline" 
                  :disabled="activeStep === 0"
                  @click="activeStep = Math.max(0, activeStep - 1)">
            上一步
          </button>
          
          <button v-if="activeStep < 3" 
                  class="btn-primary"
                  @click="activeStep = Math.min(3, activeStep + 1)">
            下一步
          </button>
          <button v-else 
                  class="btn-primary"
                  @click="submitDiagnosticData">
            完成诊断
          </button>
        </view>
      </view>
    </view>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive } from 'vue'
  import { onLoad } from '@dcloudio/uni-app'
  import TongueAnalyzer from '@/components/diagnosis/TongueAnalyzer.vue'
  import SymptomSelector from '@/components/diagnosis/SymptomSelector.vue'
  import PulseRecorder from '@/components/diagnosis/PulseRecorder.vue'
  import { submitDiagnosis } from '@/api/diagnosis'
  
  // 当前步骤
  const activeStep = ref(0)
  
  // 诊断数据
  const diagnosticData = reactive({
    inspection: {}, // 望诊数据
    auscultation: {}, // 闻诊数据
    inquiry: {}, // 问诊数据
    palpation: {} // 切诊数据
  })
  
  // 更新诊断数据
  const updateDiagnosticData = (type: string, data: Record<string, any>) => {
    Object.assign(diagnosticData[type as keyof typeof diagnosticData], data)
  }
  
  // 提交诊断数据
  const submitDiagnosticData = async () => {
    try {
      const result = await submitDiagnosis(diagnosticData)
      uni.navigateTo({
        url: `/pages/diagnosis/diagnosis-result?diagnosisId=${result.id}`
      })
    } catch (error) {
      console.error('提交诊断数据失败', error)
      uni.showToast({
        title: '提交失败，请重试',
        icon: 'none'
      })
    }
  }
  
  // 页面加载
  onLoad(() => {
    // 初始化逻辑
  })
  </script>
  
  <style lang="scss">
  .page-container {
    min-height: 100vh;
  }
  </style>
  ```
  
  #### 3.4 诊断结果与方案展示
  
  ```jsx
  <!-- pages/diagnosis/diagnosis-result.vue -->
  <template>
    <view class="page-container bg-background">
      <view class="p-4">
        <text class="text-xl font-bold text-primary">辨证结果</text>
        
        <view v-if="loading" class="flex justify-center items-center py-10">
          <uni-load-more status="loading" />
        </view>
        
        <template v-else>
          <!-- 证型诊断 -->
          <view class="card my-2">
            <view class="p-2">
              <text class="text-lg font-medium">证型诊断</text>
            </view>
            <view class="p-2">
              <!-- 证型标签 -->
              <view class="flex flex-wrap mb-2">
                <view v-for="(syndrome, index) in diagnosis.syndromes" 
                      :key="index"
                      class="mr-2 mb-2 px-2 py-1 bg-secondary rounded-full">
                  <text class="text-sm">{{ syndrome.name }}</text>
                </view>
              </view>
              
              <text class="mt-2 text-text">{{ diagnosis.diagnosisDescription }}</text>
            </view>
          </view>
          
          <!-- 体质评估 -->
          <view class="card my-2">
            <view class="p-2">
              <text class="text-lg font-medium">体质评估</text>
            </view>
            <view class="p-2">
              <view class="flex items-center">
                <text class="icon-account mr-2"></text>
                <view>
                  <text class="font-medium">{{ diagnosis.constitution.name }}</text>
                  <text class="text-sm text-gray-600 block">{{ diagnosis.constitution.description }}</text>
                </view>
              </view>
            </view>
          </view>
          
          <!-- 查看治疗方案按钮 -->
          <button class="btn-primary w-full mt-4"
                  @click="navigateToTreatmentPlan">
            查看治疗方案
          </button>
        </template>
      </view>
    </view>
  </template>
  
  <script setup lang="ts">
  import { ref, reactive } from 'vue'
  import { onLoad } from '@dcloudio/uni-app'
  import { getDiagnosisById } from '@/api/diagnosis'
  
  // 诊断ID
  const diagnosisId = ref('')
  
  // 诊断结果
  const diagnosis = reactive({
    syndromes: [],
    diagnosisDescription: '',
    constitution: {
      name: '',
      description: ''
    }
  })
  
  // 加载状态
  const loading = ref(true)
  
  // 导航到治疗方案页面
  const navigateToTreatmentPlan = () => {
    uni.navigateTo({
      url: `/pages/treatment/treatment-plan?diagnosisId=${diagnosisId.value}`
    })
  }
  
  // 页面加载
  onLoad((options) => {
    if (options.diagnosisId) {
      diagnosisId.value = options.diagnosisId
      fetchDiagnosis()
    }
  })
  
  // 获取诊断结果
  const fetchDiagnosis = async () => {
    try {
      loading.value = true
      const result = await getDiagnosisById(diagnosisId.value)
      Object.assign(diagnosis, result)
    } catch (error) {
      console.error('获取诊断结果失败', error)
      uni.showToast({
        title: '获取诊断结果失败',
        icon: 'none'
      })
    } finally {
      loading.value = false
    }
  }
  </script>
  ```
  
  ### 4. AI 模型集成
  
  #### 4.1 配置 qywit 平台
  
  在 qywit 平台创建 DeepSeek 模型 Agent：
  
  1. 登录 qywit 平台管理控制台
  2. 创建新的 Agent，选择 DeepSeek 模型
  3. 上传中医知识库资料作为 Agent 的知识源
  4. 配置系统提示词，引导模型进行中医诊断推理
  5. 创建 API 密钥用于后端服务调用
  
  #### 4.2 后端集成 AI 模型
  
  ```java
  @Service
  public class QywitModelService implements AiModelService {
  
      @Value("${ai.qywit.url}")
      private String apiUrl;
      
      @Value("${ai.qywit.key}")
      private String apiKey;
      
      private final RestTemplate restTemplate;
      
      @Override
      public DiagnosisResult analyzeFourDiagnoses(FourDiagnosesDTO data) {
          HttpHeaders headers = new HttpHeaders();
          headers.set("Authorization", "Bearer " + apiKey);
          headers.setContentType(MediaType.APPLICATION_JSON);
          
          // 构建提示词
          String prompt = buildDiagnosticPrompt(data);
          
          Map<String, Object> requestBody = new HashMap<>();
          requestBody.put("model", "deepseek-r1");
          requestBody.put("prompt", prompt);
          requestBody.put("max_tokens", 2000);
          requestBody.put("temperature", 0.2);
          
          HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
          ResponseEntity<QywitResponse> response = 
              restTemplate.postForEntity(apiUrl + "/v1/chat/completions", entity, QywitResponse.class);
          
          if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
              return parseDiagnosisResult(response.getBody().getChoices().get(0).getMessage().getContent());
          }
          
          throw new ServiceException("AI分析失败");
      }
      
      private String buildDiagnosticPrompt(FourDiagnosesDTO data) {
          // 构建结构化提示词，包含四诊信息
          StringBuilder prompt = new StringBuilder();
          prompt.append("作为中医智能诊断系统，请根据以下四诊信息进行辨证论治分析：\n\n");
          prompt.append("【望诊信息】\n");
          // 添加望诊数据
          prompt.append("舌象：").append(data.getInspection().getTongue()).append("\n");
          prompt.append("面色：").append(data.getInspection().getFacialColor()).append("\n");
          
          // 添加闻诊、问诊、切诊信息
          // ...
          
          prompt.append("\n请进行以下分析：\n");
          prompt.append("1. 八纲辨证结果\n");
          prompt.append("2. 主要证型判断\n");
          prompt.append("3. 体质评估\n");
          prompt.append("4. 诊断建议\n");
          
          return prompt.toString();
      }
      
      private DiagnosisResult parseDiagnosisResult(String aiResponse) {
          // 解析AI返回的文本结果，转换为结构化的DiagnosisResult对象
          // 使用正则表达式或其他方式提取关键信息
          
          DiagnosisResult result = new DiagnosisResult();
          // 设置解析后的属性
          return result;
      }
  }
  ```
  
  ### 5. 知识图谱构建与应用
  
  #### 5.1 使用 Neo4j 构建知识图谱
  
  ```java
  @Configuration
  public class Neo4jConfig {
      
      @Bean
      public Driver neo4jDriver(
              @Value("${spring.neo4j.uri}") String uri,
              @Value("${spring.neo4j.authentication.username}") String username,
              @Value("${spring.neo4j.authentication.password}") String password) {
          return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
      }
  }
  
  @Repository
  public class Neo4jKnowledgeRepository implements KnowledgeRepository {
      
      private final Driver driver;
      
      // 查询与症状相关的证型
      @Override
      public List<SyndromeNode> findSyndromesBySymptoms(List<String> symptomIds) {
          try (Session session = driver.session()) {
              return session.readTransaction(tx -> {
                  String query = 
                      "MATCH (s:Symptom)-[r:MANIFESTS]->(d:Syndrome) " +
                      "WHERE s.symptomId IN $symptomIds " +
                      "RETURN d, count(s) AS symptomCount " +
                      "ORDER BY symptomCount DESC";
                  
                  Map<String, Object> params = Map.of("symptomIds", symptomIds);
                  Result result = tx.run(query, params);
                  
                  List<SyndromeNode> syndromes = new ArrayList<>();
                  result.forEachRemaining(record -> {
                      // 解析结果并创建SyndromeNode对象
                      syndromes.add(mapToSyndromeNode(record.get("d").asNode()));
                  });
                  
                  return syndromes;
              });
          }
      }
      
      // 查询适用于证型的方剂
      @Override
      public List<FormulaNode> findFormulasBySyndrome(String syndromeId) {
          // 实现类似的Neo4j查询
      }
      
      // 更多知识图谱查询方法
      // ...
      
      private SyndromeNode mapToSyndromeNode(org.neo4j.driver.Node node) {
          // 将Neo4j节点映射为应用实体对象
      }
  }
  ```
  
  #### 5.2 知识图谱初始化脚本
  
  创建 Neo4j 初始化脚本，加载中医知识：
  
  ```cypher
  // 创建症状节点
  CREATE (s1:Symptom {
    symptomId: "SYM001",
    symptomName: "头痛",
    description: "头部疼痛感，可表现为胀痛、刺痛、钝痛等",
    bodyPart: "头部",
    properties: ["胀痛", "刺痛", "钝痛", "跳痛"]
  });
  
  // 创建证型节点
  CREATE (d1:Syndrome {
    syndromeId: "SYN001",
    syndromeName: "肝郁脾虚",
    category: "脏腑辨证",
    description: "肝气郁结，疏泄失常，影响脾的运化功能",
    pathogenesis: "情志不畅，肝失疏泄，横逆犯脾，脾失健运"
  });
  
  // 创建方剂节点
  CREATE (f1:Formula {
    formulaId: "FOR001",
    formulaName: "逍遙散",
    source: "《太平惠民和剂局方》",
    functions: "疏肝解郁，健脾养血"
  });
  
  // 创建关系
  MATCH (s:Symptom {symptomId: "SYM001"}), (d:Syndrome {syndromeId: "SYN001"})
  CREATE (s)-[:MANIFESTS {confidence: 0.8, significance: "主症"}]->(d);
  
  MATCH (d:Syndrome {syndromeId: "SYN001"}), (f:Formula {formulaId: "FOR001"})
  CREATE (d)-[:TREATED_BY {effectiveness: "良好", priority: "首选"}]->(f);
  ```
  
  ## 五、部署指南
  
  ### 1. 后端部署
  
  ```bash
  # 打包Spring Boot应用
  mvn clean package -DskipTests
  
  # 创建Docker镜像
  docker build -t meridian-mind-server:1.0 .
  
  # 运行容器
  docker run -d --name meridian-server \
    -p 8080:8080 \
    -e SPRING_PROFILES_ACTIVE=prod \
    -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/meridian_mind \
    -e SPRING_DATASOURCE_USERNAME=root \
    -e SPRING_DATASOURCE_PASSWORD=password \
    -e SPRING_REDIS_HOST=redis \
    meridian-mind-server:1.0
  ```
  
  ### 2. 移动应用打包
  
  ```bash
  # 构建Android APK
  eas build -p android --profile preview
  
  # 构建iOS应用
  eas build -p ios --profile preview
  
  # 通过Expo发布
  eas update --channel preview
  ```
  
  ## 六、设计风格指南
  
  ### 1. 色彩规范
  
  | 颜色名称 | 色值    | 用途                     |
  | -------- | ------- | ------------------------ |
  | 主色调   | #3A7D44 | 导航栏、主按钮、重点强调 |
  | 辅助色   | #9CC5A1 | 次要按钮、卡片边框、标签 |
  | 点缀色   | #5D3A3A | 特殊强调、中医元素       |
  | 背景色   | #F9F5F0 | 页面背景                 |
  | 文本色   | #333333 | 主要文字                 |
  | 灰色     | #9B9B9B | 次要文字、占位符         |


  ### 2. 组件样式规范

  #### 按钮

  + 主要按钮：使用主色调背景，圆角边缘，简洁文字
  + 次要按钮：使用辅助色或描边样式
  + 禁用状态：降低不透明度，保持形状

  #### 卡片

  + 白色背景，轻微阴影
  + 圆角度为8px
  + 内边距一致（16px）
  + 避免过多嵌套
  + 使用卡片分组相关信息

  #### 表单

  + 标签置于输入框上方
  + 足够的输入框高度（至少48px）
  + 明确的输入提示
  + 错误状态明显但不刺眼
  + 分组和分段长表单

  ### 3. 排版规范

  + 标题：18-22sp，使用sans-serif-medium字体
  + 正文：14-16sp，使用sans-serif字体
  + 主要内容左对齐
  + 控制行高为字体大小的1.5倍
  + 段落间距为行高的1.5倍

  ### 4. 图标和插图风格

  + 使用简约线性图标
  + 中医元素可使用传统中国风插图，但风格简洁现代
  + 关键诊断信息配以直观图示

  ## 七、注意事项与常见问题

  ### 1. 性能优化

  + 使用 FlatList 替代 ScrollView 处理长列表
  + 实现组件懒加载和按需渲染
  + 使用 React.memo() 和 useCallback 减少不必要的重渲染
  + 图片资源使用适当分辨率和格式
  + 使用批量更新减少状态变更次数

  ### 2. 安全措施

  + 使用 HTTPS 进行 API 通信
  + 实施 JWT 令牌认证与刷新机制
  + 对敏感健康数据进行加密存储
  + 应用内不存储明文密码
  + 定期更新依赖和安全补丁

  ### 3. 离线功能支持

  + 使用 AsyncStorage 缓存用户数据
  + 实现离线表单保存与同步
  + 关键健康知识本地存储
  + 网络状态检测与适应性UI

  ## 九、后续开发计划

  1. 实现舌象图像识别功能
  2. 开发患者健康记录长期跟踪系统
  3. 增加中药配方详情和购买渠道
  4. 添加医患交流平台
  5. 集成可穿戴设备数据分析

  