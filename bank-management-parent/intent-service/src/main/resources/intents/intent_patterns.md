# 银行业务意图配置

本文件定义了系统支持的所有业务意图及其匹配模式。每个意图包含：
- 意图名称（INTENT_NAME）
- API名称（API_NAME）
- 意图描述（Description）
- 匹配模式（Patterns）：支持正则表达式
- 示例输入（Examples）
- 实现状态（Status）：IMPLEMENTED/TESTING

## 客户信息相关

### QUERY_CUSTOMER_INFO
- API_NAME: queryCustomer
- Description: 查询客户基本信息
- Status: IMPLEMENTED
- Patterns:
  ```
  .*查询.*客户信息.*
  .*查看.*客户资料.*
  .*(.+)的个人信息.*
  .*客户(.+)的详细信息.*
  ```
- Examples:
  - 查询张三的客户信息
  - 查看李四的个人资料
  - 王五的个人信息是什么

## OTP相关

### QUERY_OTP_STATUS
- API_NAME: queryOtpStatus
- Description: 查询客户OTP状态
- Status: IMPLEMENTED
- Patterns:
  ```
  .*查询.*OTP状态.*
  .*查看.*OTP.*
  .*(.+)的OTP是否开启.*
  .*(.+)的OTP状态.*
  ```
- Examples:
  - 查询张三的OTP状态
  - 李四的OTP是否开启
  - 查看王五的OTP

### UPDATE_OTP_STATUS
- API_NAME: updateOtpFlg
- Description: 更新客户OTP状态
- Status: IMPLEMENTED
- Patterns:
  ```
  .*更新.*OTP.*
  .*修改.*OTP.*
  .*开启.*OTP.*
  .*关闭.*OTP.*
  .*(.+)的OTP设置为.*
  ```
- Examples:
  - 更新张三的OTP为开启
  - 关闭李四的OTP
  - 修改王五的OTP状态

## 账户相关

### QUERY_ACCOUNT_BALANCE
- API_NAME: queryBalance
- Description: 查询账户余额
- Status: TESTING
- Patterns:
  ```
  .*查询.*账户余额.*
  .*查看.*余额.*
  .*(.+)的账户余额.*
  .*(.+)有多少钱.*
  ```
- Examples:
  - 查询张三的账户余额
  - 李四有多少钱
  - 查看王五的余额

### QUERY_TRANSACTION_HISTORY
- API_NAME: queryTransactions
- Description: 查询交易历史
- Status: TESTING
- Patterns:
  ```
  .*交易记录.*
  .*交易历史.*
  .*账单明细.*
  .*(.+)的交易明细.*
  ```
- Examples:
  - 查看张三的交易记录
  - 李四的账单明细
  - 最近的交易历史

## 信用卡相关

### QUERY_CREDIT_LIMIT
- API_NAME: queryCreditLimit
- Description: 查询信用卡额度
- Status: TESTING
- Patterns:
  ```
  .*信用额度.*
  .*信用卡额度.*
  .*(.+)的信用额度.*
  .*可用额度.*
  ```
- Examples:
  - 查询张三的信用额度
  - 李四的信用卡额度是多少
  - 查看可用额度

### UPDATE_CREDIT_LIMIT
- API_NAME: updateCreditLimit
- Description: 更新信用卡额度
- Status: TESTING
- Patterns:
  ```
  .*调整.*信用额度.*
  .*修改.*信用卡额度.*
  .*提高.*额度.*
  .*降低.*额度.*
  ```
- Examples:
  - 调整张三的信用额度
  - 提高李四的额度
  - 修改信用卡额度

## 贷款相关

### QUERY_LOAN_STATUS
- API_NAME: queryLoanStatus
- Description: 查询贷款状态
- Status: TESTING
- Patterns:
  ```
  .*贷款状态.*
  .*贷款进度.*
  .*(.+)的贷款情况.*
  .*查询.*贷款.*
  ```
- Examples:
  - 查询张三的贷款状态
  - 李四的贷款进度
  - 查看贷款情况

### APPLY_LOAN
- API_NAME: applyLoan
- Description: 申请贷款
- Status: TESTING
- Patterns:
  ```
  .*申请.*贷款.*
  .*办理.*贷款.*
  .*想要贷款.*
  .*贷款申请.*
  ```
- Examples:
  - 我要申请贷款
  - 办理个人贷款
  - 想要申请房贷

## 投资理财相关

### QUERY_INVESTMENT_PRODUCTS
- API_NAME: queryInvestmentProducts
- Description: 查询投资理财产品
- Status: TESTING
- Patterns:
  ```
  .*理财产品.*
  .*投资产品.*
  .*基金产品.*
  .*理财推荐.*
  ```
- Examples:
  - 查看理财产品
  - 有什么投资产品
  - 推荐基金产品

### QUERY_INVESTMENT_STATUS
- API_NAME: queryInvestmentStatus
- Description: 查询投资收益状况
- Status: TESTING
- Patterns:
  ```
  .*理财收益.*
  .*投资收益.*
  .*(.+)的理财状况.*
  .*基金收益.*
  ```
- Examples:
  - 查询张三的理财收益
  - 李四的投资收益怎么样
  - 查看基金收益

## 风险评估

### RISK_ASSESSMENT
- API_NAME: assessRisk
- Description: 进行风险评估
- Status: TESTING
- Patterns:
  ```
  .*风险评估.*
  .*风险测评.*
  .*评估风险.*
  .*测试风险.*
  ```
- Examples:
  - 进行风险评估
  - 我要做风险测评
  - 评估投资风险

## 开户服务

### SCHEDULE_ACCOUNT_OPENING
- API_NAME: scheduleAccountOpening
- Description: 预约开户服务
- Status: TESTING
- Patterns:
  ```
  .*预约开户.*
  .*预约开卡.*
  .*想要开户.*
  .*办理开户.*
  ```
- Examples:
  - 我要预约开户
  - 办理银行卡开户
  - 想要开通账户 