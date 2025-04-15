# 个人事件库配置文件

本文件定义了个人自定义事件意图及其匹配模式。个人事件由银行事件组合而成。

## OPT状态相关

### PERSONAL_OTP_UPDATE_NOTIFY
- BANK_EVENT: [BANK_OTP_UPDATE, BANK_SEND_NOTIFICATION]
- Description: 更新OTP状态并通知用户
- Status: IMPLEMENTED
- Patterns:
  ```
  .*更新.*OTP.*并.*通知.*
  .*修改.*OTP.*然后.*发送.*通知.*
  .*更新.*OTP.*后.*发[送给].*消息.*
  ```
- Examples:
  - 更新张三的OTP状态为关闭并发送通知
  - 修改李四和王五的OTP为开启然后发送通知
  - 更新张三的OTP状态后发给他消息

### PERSONAL_OTP_BATCH_UPDATE
- BANK_EVENT: BANK_OTP_UPDATE
- Description: 批量更新OTP状态
- Status: IMPLEMENTED
- Patterns:
  ```
  .*批量.*更新.*OTP.*
  .*为.*所有.*客户.*设置.*OTP.*
  .*统一.*修改.*OTP.*
  ```
- Examples:
  - 批量更新所有客户的OTP状态为开启
  - 为所有VIP客户设置OTP为关闭
  - 统一修改这批客户的OTP状态

## 客户管理相关

### PERSONAL_CUSTOMER_FULL_INFO
- BANK_EVENT: BANK_CUSTOMER_INFO
- Description: 查询客户完整信息包括OTP状态
- Status: IMPLEMENTED
- Patterns:
  ```
  .*查询.*完整.*信息.*
  .*获取.*全部.*资料.*
  .*查看.*详细.*档案.*包括.*OTP.*
  ```
- Examples:
  - 查询张三的完整客户信息
  - 获取李四和王五的全部资料
  - 查看张三详细档案包括OTP状态

### PERSONAL_CUSTOMER_INFO_AND_OTP
- BANK_EVENT: [BANK_CUSTOMER_INFO, BANK_OTP_STATUS]
- Description: 查询客户信息和OTP状态
- Status: IMPLEMENTED
- Patterns:
  ```
  .*查询.*客户.*信息.*和.*OTP.*
  .*获取.*客户.*资料.*及.*OTP.*
  .*查看.*信息.*与.*OTP.*状态.*
  ```
- Examples:
  - 查询张三的客户信息和OTP状态
  - 获取李四和王五的客户资料及OTP状态
  - 查看张三的信息与OTP状态 