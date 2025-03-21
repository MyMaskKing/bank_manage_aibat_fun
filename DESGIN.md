# 银行后台管理系统设计文档

## 技术框架

- **后端**: Java 17.0.14
- **Web框架**: Spring Boot 3.x
- **数据库**: SQLite 3.x
- **ORM框架**: Hibernate 6.x (通过Spring Data JPA)
- **构建工具**: Maven 3.9
- **前端**: Thymeleaf 3.x
- **API设计**: RESTful API设计风格

## 系统架构

系统采用微服务架构，由以下四个服务组成：

1. **api-service** (端口: 8082)
   - 核心功能和数据访问服务
   - 提供客户信息、OTP状态等基础API

2. **intent-service** (端口: 8081)
   - 负责自然语言处理和意图识别
   - 分析用户输入并确定对应的操作

3. **conversation-service** (端口: 8083)
   - 管理对话历史记录
   - 提供对话保存和查询功能

4. **chat-service** (端口: 8080)
   - 提供用户界面和交互
   - 集成其他服务的功能

## Chat画面

- **功能**: 显示聊天界面，接收用户输入和文件上传，展示API返回结果。
- **API**:
  - `GET /chat`: 获取聊天界面。
  - `POST /chat/send`: 提交用户输入和文件，返回API结果。

## 用户意图识别功能

- **功能**: 分析用户输入，确定要调用的API及其参数。
- **API**:
  - `POST /intent/analyze`: 分析用户输入，返回API名称、调用次数和参数列表。
- **配置方式**: 通过Markdown文件定义意图匹配模式和API映射。

## API执行功能

- **功能**: 根据用户意图识别结果，调用相应的API并执行。
- **API**:
  - `POST /api/execute`: 执行相应的API，返回执行结果。
  - `GET /api/customer/{name}`: 获取指定客户信息。
  - `POST /api/customer/otp/update`: 更新客户OTP状态。
  - `GET /api/customer/otp/status`: 查询客户OTP状态。

## 对话记录功能

- **功能**: 将对话记录以Markdown文件形式保存到本地。
- **API**:
  - `POST /conversation/save`: 保存对话记录为Markdown文件。
  - `GET /conversation/history`: 获取历史对话记录。

## 测试用API示例：用户OTP Flg更新

- **功能**: 更新用户的OTP标志位。
- **API**:
  - `POST /api/updateOtpFlg`: 更新用户的OTP标志位。

## 测试数据准备

在测试数据库中，准备以下测试数据：

### 客户表 (customers)

| customer_id | customer_name | otp_flg |
|-------------|---------------|---------|
| 1           | 张三          | false   |
| 2           | 李四          | true    |
| 3           | 王五          | false   |
| 4           | 赵六          | true    |

### 对话记录表 (conversation_records)

| record_id | user_input          | api_result                  | timestamp           |
|-----------|---------------------|-----------------------------|--------------------|
| 1         | 更新客户1的OTP Flg  | 已成功更新客户1的OTP Flg为true | 2023-07-01 10:00:00 |
| 2         | 查询客户2的OTP状态  | 客户2的OTP状态为true        | 2023-07-01 10:05:00 |
| 3         | 更新客户3的OTP Flg  | 已成功更新客户3的OTP Flg为true | 2023-07-01 10:10:00 |

## Chat画面提示词

### 显示聊天界面
- **API**: `GET /chat`
- **功能**: 渲染聊天界面，包括对话区域、输入框和上传文件的按钮。
- **界面元素**:
  - 对话区域：显示历史对话记录。
  - 输入框：用户输入文本内容。
  - 上传按钮：用户上传文件。
  - 发送按钮：提交用户输入和文件。

### 获取用户输入和文件
- **API**: `POST /chat/send`
- **功能**: 接收用户在聊天界面中的输入和文件上传操作，返回API执行结果。
- **请求参数**:
  - `userInput` (String): 用户输入的文本内容。
  - `file` (MultipartFile): 用户上传的文件。
- **响应参数**:
  - `result` (String): API执行结果。

## 多客户处理功能

系统支持同时处理多个客户的查询或操作请求：

1. **自然语言多客户查询**
   - 示例：`查询张三和李四的信息`
   - 实现：系统识别多个客户名，为每个客户单独执行查询
   - 结果：合并多个客户的查询结果并格式化返回

2. **批量文件处理**
   - 支持上传包含多个客户的CSV文件
   - 对文件中的每个客户执行相同操作
   - 汇总结果并统一返回

## 调用步骤

1. **显示Chat画面**:
   - 调用 `GET /chat` 接口，获取聊天界面。

2. **获取用户输入和文件**:
   - 用户在聊天界面中输入内容并上传文件，点击发送按钮。

3. **识别用户意图**:
   - 前端调用 `POST /intent/analyze` 接口，传入用户输入的内容，得到要调用的API名称、调用次数和参数列表。

4. **准备API参数**:
   - 根据用户上传的文件和输入内容，准备API所需的参数。

5. **执行API**:
   - 前端调用 `POST /api/execute` 接口，传入API名称和参数，得到API执行结果。

6. **展示API结果**:
   - 前端将API返回的结果展示在聊天界面的对话区域中。

7. **保存对话记录为Markdown文件**:
   - 前端调用 `POST /conversation/save` 接口，传入对话历史记录和文件保存路径，将对话记录保存为Markdown文件。

## 对话记录保存为Markdown文件的详细步骤

在保存对话记录为Markdown文件的功能中，`POST /conversation/save` 接口的具体逻辑如下：

- **构造Markdown内容**:
  - 遍历对话历史记录，为每轮对话构建Markdown格式的字符串。
  - 每轮对话以表格形式呈现，包含用户输入和API结果两列。

- **写入文件**:
  - 使用文件操作将构造好的Markdown内容写入到指定的文件路径。
  - 如果文件已存在，追加内容；如果不存在，创建新文件。

- **返回结果**:
  - 如果文件成功保存，返回成功信息；否则，返回错误信息。

## 意图识别配置[意图识别.md](bank-management-parent/intent-service/src/main/resources/intents/intent_patterns.md)

系统支持通过Markdown文件配置意图识别模式：

```markdown
### 查询客户OTP状态
- API_NAME: queryOtpStatus
- Status: IMPLEMENTED

```
.*查[看询].*(?:的)?OTP状态.*
```

### 更新客户OTP状态
- API_NAME: updateOtpStatus
- Status: IMPLEMENTED

```
.*更新.*(?:的)?OTP.*为(true|false)
```

### 查询客户信息
- API_NAME: queryCustomerInfo
- Status: IMPLEMENTED

```
.*查[看询].*(?:的)?(?:客户)?信息.*
```
```

## 测试用API示例：用户OTP Flg更新的详细步骤

假设用户输入"更新客户123、456的OTP Flg为true"，并上传了一个包含客户ID列表的文件。

1. **用户输入分析**:
   - 调用 `POST /intent/analyze` 接口，传入用户输入的内容，识别出要调用的API为`updateOtpFlg`。

2. **准备API参数**:
   - 从用户上传的文件中读取客户ID列表，解析出`customerIds`为[123, 456]。
   - 根据用户输入解析出`newFlgValue`为true。

3. **执行API**:
   - 调用 `POST /api/execute` 接口，传入API名称`updateOtpFlg`和参数`{"customerIds": [123, 456], "newFlgValue": true}`，得到API执行结果。

4. **API执行逻辑**:
   - 在后端`updateOtpFlg`方法中，遍历`customerIds`，将每个客户的OTP Flg更新为`newFlgValue`。
   - 记录更新操作的日志，返回更新成功的信息。

5. **展示和记录结果**:
   - 前端将API返回的结果展示在聊天界面的对话区域中。
   - 调用`POST /conversation/save` 接口，将本次对话记录保存到Markdown文件中。
