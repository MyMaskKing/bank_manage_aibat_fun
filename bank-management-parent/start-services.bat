@echo off
chcp 65001 > nul
echo 正在启动银行管理系统服务...

:: 获取当前日期作为日志目录名
set log_date=%date:~0,4%%date:~5,2%%date:~8,2%
set log_dir=logs\%log_date%

:: 创建日志目录
if not exist %log_dir% mkdir %log_dir%

:: 获取当前时间作为日志文件名
set log_file=%log_dir%\start_%time:~0,2%%time:~3,2%%time:~6,2%.log

:: 记录启动时间
echo 启动时间: %date% %time%
echo 启动时间: %date% %time% > %log_file%

:: 启动服务并记录日志
echo 正在启动 API 服务...
cd api-service
start "API Service" java -jar target\api-service-1.0-SNAPSHOT.jar > ..\%log_dir%\api-service.log 2>&1
cd ..

echo 正在启动聊天服务...
cd chat-service
start "Chat Service" java -jar target\chat-service-1.0-SNAPSHOT.jar > ..\%log_dir%\chat-service.log 2>&1
cd ..

echo 正在启动意图识别服务...
cd intent-service
start "Intent Service" java -jar target\intent-service-1.0-SNAPSHOT.jar > ..\%log_dir%\intent-service.log 2>&1
cd ..

echo 正在启动对话记录服务...
cd conversation-service
start "Conversation Service" java -jar target\conversation-service-1.0-SNAPSHOT.jar > ..\%log_dir%\conversation-service.log 2>&1
cd ..

echo 所有服务已启动
echo 日志文件保存在 %log_dir% 目录下
echo 服务启动完成 >> %log_file%

:: 打开日志文件夹
explorer %log_dir%

:: 保持窗口打开
pause 