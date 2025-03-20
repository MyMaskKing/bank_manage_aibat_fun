# 银行后台管理功能提示词

## 技术框架

- **后端**: Java 17.0.14
- **Web框架**: Spring Boot X.x
- **数据库**: SQLite X.x
- **ORM框架**: Hibernate X.x
- **构建工具**: Maven 3.9
- **前端**: Thymeleaf 3.x
- **API设计**: RESTful API设计风格

## Chat画面

- **功能**: 显示聊天界面，接收用户输入和文件上传，展示API返回结果。
- **API**:
  - `GET /chat`: 获取聊天界面。
  - `POST /chat/send`: 提交用户输入和文件，返回API结果。

## 用户意图识别功能

- **功能**: 分析用户输入，确定要调用的API。
- **API**:
  - `POST /intent/analyze`: 分析用户输入，返回要调用的API名称。

## API执行功能

- **功能**: 根据用户意图识别结果，调用相应的API并执行。
- **API**:
  - `POST /api/execute`: 执行相应的API，返回执行结果。

## 对话记录功能

- **功能**: 将对话记录以Markdown文件形式保存到本地。
- **API**:
  - `POST /conversation/save`: 保存对话记录为Markdown文件。

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

| record_id | user_input          | api_result                  |
|-----------|---------------------|-----------------------------|
| 1         | 更新客户1的OTP Flg  | 已成功更新客户1的OTP Flg为true |
| 2         | 查询客户2的OTP状态  | 客户2的OTP状态为true        |
| 3         | 更新客户3的OTP Flg  | 已成功更新客户3的OTP Flg为true |

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

## 调用步骤

1. **显示Chat画面**:
   - 调用 `GET /chat` 接口，获取聊天界面。

2. **获取用户输入和文件**:
   - 用户在聊天界面中输入内容并上传文件，点击发送按钮。

3. **识别用户意图**:
   - 前端调用 `POST /intent/analyze` 接口，传入用户输入的内容，得到要调用的API名称。

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

## 测试用API示例：用户OTP Flg更新的详细步骤

假设用户输入“更新客户123、456的OTP Flg为true”，并上传了一个包含客户ID列表的文件。

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
