# 银行管理系统

基于Spring Boot微服务架构开发的银行管理系统，提供客户信息查询、OTP状态管理等功能，支持自然语言交互和批量处理。

## 文档导航

- [系统设计文档](DESGIN.md) - 详细的系统设计说明
- [构建与启动指南](BUILD.md) - 构建和部署说明
- [对话记录](docs/conversations) - 用户交互记录

## 系统架构

系统由四个微服务组成：

1. **api-service** (端口: 8082) - 核心API服务，提供客户数据管理和OTP状态相关操作
2. **intent-service** (端口: 8081) - 意图识别服务，分析用户输入并确定操作类型
3. **conversation-service** (端口: 8083) - 对话记录服务，保存用户交互历史
4. **chat-service** (端口: 8080) - 聊天界面服务，提供用户交互前端

## 主要功能

1. **智能意图识别**
   - 自然语言处理，识别用户意图
   - 支持多种查询和操作模式
   - 基于配置的可扩展意图识别

2. **客户信息管理**
   - 查询客户基本信息
   - 管理客户OTP状态
   - 批量处理客户数据

3. **多客户批量操作**
   - 通过自然语言指令查询多个客户
   - 通过CSV文件批量处理客户数据
   - 结果自动合并展示

4. **用户友好界面**
   - 聊天式交互界面
   - 文件上传功能
   - 对话历史记录

## 技术栈

- **后端**: Spring Boot 3.x, JPA, SQLite
- **前端**: Thymeleaf 3.x, Bootstrap 5.x, JavaScript
- **数据库**: SQLite 3.x
- **构建工具**: Maven 3.9+

## 本地开发环境搭建

### 前提条件

- JDK 17
- Maven 3.8+

### 编译和运行

**1. 克隆仓库**

```bash
git clone https://github.com/yourusername/bank-management-system.git
cd bank-management-system
```

**2. 编译项目**

```bash
mvn clean package
```

**3. 启动服务**

Windows:
```bash
cd bank-management-parent
start-services.bat
```

Linux/Mac:
```bash
cd bank-management-parent
chmod +x start-services.sh
./start-services.sh
```

### 服务启动顺序

为避免依赖问题，建议按以下顺序启动服务:

1. api-service (8082)
2. intent-service (8081)
3. conversation-service (8083)
4. chat-service (8080)

详细的启动说明请参考[构建与启动指南](BUILD.md)。

## 使用示例

### 单客户查询

在聊天界面输入：
```
查询张三的OTP状态
```

系统将返回张三的OTP状态信息。

### 多客户查询

在聊天界面输入：
```
查询张三和李四的信息
```

系统将分别查询张三和李四的信息，并合并展示结果。

### 更新OTP状态

在聊天界面输入：
```
更新王五的OTP为true
```

系统将更新王五的OTP状态为true。

### 批量处理

1. 准备一个CSV文件，格式如下：
```
客户名称
张三
李四
王五
```

2. 在聊天界面上传该文件，并输入：
```
查询OTP状态
```

3. 系统将为CSV文件中的每个客户执行查询，并合并展示结果。

## 配置意图识别

意图识别模式配置在 `intent_patterns.md` 文件中，可以方便地添加新的意图匹配模式。

示例配置：
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
```

完整的意图配置和系统设计详情请参考[系统设计文档](DESGIN.md)。

## 对话记录

系统会自动保存所有用户交互记录，可以在[对话记录](docs/conversations)目录中查看。每条记录包含：
- 用户输入
- 系统响应
- 时间戳
- API调用信息

## 贡献指南

1. Fork项目仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

[MIT License](LICENSE) 