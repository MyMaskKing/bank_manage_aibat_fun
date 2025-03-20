# 银行管理系统

基于Spring Boot微服务架构开发的银行管理系统，提供客户信息查询、OTP状态管理等功能。

## 系统架构

系统由四个微服务组成：

1. **api-service** (端口: 8082) - 核心API服务，提供客户数据管理和OTP状态相关操作
2. **intent-service** (端口: 8081) - 意图识别服务，分析用户输入并确定操作类型
3. **conversation-service** (端口: 8083) - 对话记录服务，保存用户交互历史
4. **chat-service** (端口: 8080) - 聊天界面服务，提供用户交互前端

## 技术栈

- **后端**: Spring Boot, JPA, SQLite
- **前端**: HTML5, CSS3, JavaScript
- **数据库**: SQLite

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

## 功能特点

- **智能意图识别**: 自动分析用户输入并执行相应操作
- **客户信息管理**: 查询客户信息、账户余额等
- **OTP管理**: 查询和更新OTP状态
- **批量处理**: 通过CSV文件上传实现批量操作
- **对话记录**: 保存所有交互历史并支持下载

## 批量处理示例

上传CSV文件格式示例:

```
操作,客户名称
查询客户,张三
查询OTP,李四
开启OTP,王五
关闭OTP,赵六
```

## 贡献指南

1. Fork项目仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

[MIT License](LICENSE) 