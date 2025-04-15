# 银行事件库配置文件

本文件定义了银行标准业务事件意图及其匹配模式。银行事件由标准事件组合而成。

## 客户信息相关

### BANK_CUSTOMER_INFO
- STANDARD_EVENT: STANDARD_QUERY_CUSTOMER
- Description: 查询客户基本信息
- Status: IMPLEMENTED
- Patterns:
  ```
  .*查询.*客户信息.*
  .*查看.*客户资料.*
  .*(.+)的个人信息.*
  .*客户(.+)的详细信息.*
  .*查[看询].*和.*的.*信息.*
  .*查[看询].*(及|和|与).*的.*信息.*
  ```
- Examples:
  - 查询张三的客户信息
  - 查看李四的个人资料
  - 王五的个人信息是什么
  - 查看张三和李四的信息
  - 查询张三及李四的客户信息

## OTP相关

### BANK_OTP_STATUS
- STANDARD_EVENT: STANDARD_QUERY_OTP
- Description: 查询客户OTP状态
- Status: IMPLEMENTED
- Patterns:
  ```
  .*查询.*OTP状态.*
  .*查看.*OTP.*
  .*(.+)的OTP是否开启.*
  .*(.+)的OTP状态.*
  .*查[看询].*和.*的OTP.*
  .*查[看询].*(及|和|与).*的OTP.*
  ```
- Examples:
  - 查询张三的OTP状态
  - 李四的OTP是否开启
  - 查看王五的OTP
  - 查看张三和李四的OTP状态
  - 查询张三及李四的OTP

### BANK_OTP_UPDATE
- STANDARD_EVENT: [STANDARD_UPDATE_OTP, STANDARD_LOG_OPERATION]
- Description: 更新客户OTP状态
- Status: IMPLEMENTED
- Patterns:
  ```
  .*更新.*OTP.*
  .*修改.*OTP.*
  .*开启.*OTP.*
  .*关闭.*OTP.*
  .*(.+)的OTP设置为.*
  .*(及|和|与).*的OTP.*[开关启闭].*
  ```
- ParameterExtraction:
  - otpValue: 
    ```
    .*(开启|关闭|true|false).*
    ```
- Examples:
  - 更新张三的OTP为开启
  - 关闭李四的OTP
  - 修改王五的OTP状态
  - 开启张三和李四的OTP
  - 关闭张三及李四的OTP

### BANK_SEND_NOTIFICATION
- STANDARD_EVENT: [STANDARD_SEND_EMAIL, STANDARD_SEND_SMS]
- Description: 发送通知给用户
- Status: IMPLEMENTED
- Patterns:
  ```
  .*发送.*通知.*
  .*发送.*消息.*
  .*通知.*客户.*
  ```
- Examples:
  - 发送通知给张三
  - 给李四发送消息
  - 通知客户王五 