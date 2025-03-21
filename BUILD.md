# 构建与启动指南

## 构建步骤

1. 确保已安装 JDK 17 和 Maven 3.9+
2. 在项目根目录执行构建命令：
   ```bash
   mvn clean package
   ```
3. 等待构建完成，确保所有服务都构建成功

### 温馨提示：也可以通过realase中bank-jars来进行构建

## 服务启动顺序

服务启动的推荐顺序如下：

### 1. api-service（端口8082）
首先启动此服务，因为它包含数据库模型和基础功能
负责提供核心API和数据访问

```bash
cd bank-management-parent/api-service
mvn spring-boot:run
```

或通过jar文件启动：
```bash
java -jar bank-jars/api-service.jar
```

### 2. intent-service（端口8081）
第二个启动，因为它依赖于基础数据
负责意图识别功能

```bash
cd bank-management-parent/intent-service
mvn spring-boot:run
```

或通过jar文件启动：
```bash
java -jar bank-jars/intent-service.jar
```

### 3. conversation-service（端口8083）
第三个启动，因为它可能依赖于意图服务
负责存储和管理对话记录

```bash
cd bank-management-parent/conversation-service
mvn spring-boot:run
```

或通过jar文件启动：
```bash
java -jar bank-jars/conversation-service.jar
```

### 4. chat-service（端口8080）
最后启动，因为它依赖所有其他服务
负责与用户交互的前端界面

```bash
cd bank-management-parent/chat-service
mvn spring-boot:run
```

或通过jar文件启动：
```bash
java -jar bank-jars/chat-service.jar
```

## 快速启动脚本

为了方便快速启动所有服务，可以使用以下脚本：

### Windows环境（start-services.bat）

```batch
@echo off
echo 启动银行管理系统服务...

echo 1. 启动API服务(8082)...
start cmd /k "java -jar bank-jars\api-service.jar"
timeout /t 15

echo 2. 启动意图识别服务(8081)...
start cmd /k "java -jar bank-jars\intent-service.jar"
timeout /t 10

echo 3. 启动对话记录服务(8083)...
start cmd /k "java -jar bank-jars\conversation-service.jar"
timeout /t 10

echo 4. 启动聊天界面服务(8080)...
start cmd /k "java -jar bank-jars\chat-service.jar"

echo 所有服务已启动，请访问http://localhost:8080/chat进行测试
```

### Linux/Mac环境（start-services.sh）

```bash
#!/bin/bash
echo "启动银行管理系统服务..."

echo "1. 启动API服务(8082)..."
java -jar bank-jars/api-service.jar > api-service.log 2>&1 &
sleep 15

echo "2. 启动意图识别服务(8081)..."
java -jar bank-jars/intent-service.jar > intent-service.log 2>&1 &
sleep 10

echo "3. 启动对话记录服务(8083)..."
java -jar bank-jars/conversation-service.jar > conversation-service.log 2>&1 &
sleep 10

echo "4. 启动聊天界面服务(8080)..."
java -jar bank-jars/chat-service.jar > chat-service.log 2>&1 &

echo "所有服务已启动，请访问http://localhost:8080/chat进行测试"
```

确保给Shell脚本添加执行权限：
```bash
chmod +x start-services.sh
```

## 测试聊天功能

1. 打开浏览器，访问 http://localhost:8080/chat
2. 在聊天界面中输入测试命令：
   - "更新张三的OTP Flg为true"
   - "查询李四的OTP状态"
   - "查询张三和李四的信息"

## 批量查询功能测试

1. 准备一个CSV文件，包含客户名称列，例如：
   ```
   客户名称
   张三
   李四
   王五
   ```
2. 在聊天界面上传该文件，并输入查询指令（如"查询OTP状态"）
3. 系统将处理CSV中的每个客户，并返回合并结果