# 标准事件库配置文件

本文件定义了标准基础操作API及其功能说明。这些API是最小粒度的操作，可被组合使用。

## 用户信息相关

### STANDARD_QUERY_CUSTOMER
- API_NAME: queryCustomer
- Description: 查询客户基本信息
- Status: IMPLEMENTED
- Database: customers
- Operation: SELECT
- Parameters:
  - customerName: 客户姓名
- ReturnFields: 
  - customer_id: 客户ID
  - customer_name: 客户姓名
  - otp_flg: OTP状态
  - other_fields: 其他客户信息字段

### STANDARD_UPDATE_CUSTOMER
- API_NAME: updateCustomer
- Description: 更新客户基本信息
- Status: IMPLEMENTED
- Database: customers
- Operation: UPDATE
- Parameters:
  - customerName: 客户姓名
  - fieldName: 字段名
  - fieldValue: 字段值
- UpdateFields:
  - ${fieldName}: 动态字段，由参数指定

## OTP相关

### STANDARD_QUERY_OTP
- API_NAME: queryOtpStatus
- Description: 查询客户OTP状态
- Status: IMPLEMENTED
- Database: customers
- Operation: SELECT
- Parameters:
  - customerName: 客户姓名
- ReturnFields:
  - otp_flg: OTP状态

### STANDARD_UPDATE_OTP
- API_NAME: updateOtpFlg
- Description: 更新客户OTP状态
- Status: IMPLEMENTED
- Database: customers
- Operation: UPDATE
- Parameters:
  - customerName: 客户姓名
  - otpValue: OTP状态值(true/false)
- UpdateFields:
  - otp_flg: OTP状态字段

## 系统功能

### STANDARD_LOG_OPERATION
- API_NAME: logOperation
- Description: 记录操作日志
- Status: IMPLEMENTED
- Database: operation_logs
- Operation: INSERT
- Parameters:
  - operationType: 操作类型
  - targetId: 目标ID
  - details: 操作详情
- InsertFields:
  - operation_type: 操作类型
  - target_id: 目标ID
  - details: 操作详情
  - created_at: 创建时间(自动填入当前时间)

### STANDARD_SEND_EMAIL
- API_NAME: sendEmail
- Description: 发送电子邮件通知
- Status: IMPLEMENTED
- ServiceType: EMAIL
- Operation: SEND
- Parameters:
  - recipientName: 收件人姓名
  - recipientEmail: 收件人邮箱
  - subject: 邮件主题
  - content: 邮件内容
- Implementation: JavaMail API

### STANDARD_SEND_SMS
- API_NAME: sendSms
- Description: 发送短信通知
- Status: IMPLEMENTED
- ServiceType: SMS
- Operation: SEND
- Parameters:
  - phoneNumber: 手机号码
  - content: 短信内容
- Implementation: 短信服务提供商API 