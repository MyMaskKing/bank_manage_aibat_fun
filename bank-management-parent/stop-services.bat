@echo off
chcp 65001 > nul
echo 正在停止银行管理系统服务...

:: 获取当前日期作为日志目录名
set log_date=%date:~0,4%%date:~5,2%%date:~8,2%
set log_dir=logs\%log_date%

:: 创建日志目录
if not exist %log_dir% mkdir %log_dir%

:: 获取当前时间作为日志文件名
set log_file=%log_dir%\stop_%time:~0,2%%time:~3,2%%time:~6,2%.log

:: 记录停止时间
echo 停止时间: %date% %time%
echo 停止时间: %date% %time% > %log_file%

:: 停止服务并记录日志
echo 正在停止 API 服务...
taskkill /F /FI "WINDOWTITLE eq API Service" >> %log_file% 2>&1
if %errorlevel% equ 0 (
    echo API 服务已停止
    echo API 服务已停止 >> %log_file%
) else (
    echo API 服务停止失败或未运行
    echo API 服务停止失败或未运行 >> %log_file%
)

echo 正在停止聊天服务...
taskkill /F /FI "WINDOWTITLE eq Chat Service" >> %log_file% 2>&1
if %errorlevel% equ 0 (
    echo 聊天服务已停止
    echo 聊天服务已停止 >> %log_file%
) else (
    echo 聊天服务停止失败或未运行
    echo 聊天服务停止失败或未运行 >> %log_file%
)

echo 正在停止意图识别服务...
taskkill /F /FI "WINDOWTITLE eq Intent Service" >> %log_file% 2>&1
if %errorlevel% equ 0 (
    echo 意图识别服务已停止
    echo 意图识别服务已停止 >> %log_file%
) else (
    echo 意图识别服务停止失败或未运行
    echo 意图识别服务停止失败或未运行 >> %log_file%
)

echo 正在停止对话记录服务...
taskkill /F /FI "WINDOWTITLE eq Conversation Service" >> %log_file% 2>&1
if %errorlevel% equ 0 (
    echo 对话记录服务已停止
    echo 对话记录服务已停止 >> %log_file%
) else (
    echo 对话记录服务停止失败或未运行
    echo 对话记录服务停止失败或未运行 >> %log_file%
)

echo 所有服务已停止
echo 日志文件保存在 %log_dir% 目录下
echo 服务停止完成 >> %log_file%

:: 打开日志文件夹
explorer %log_dir%

:: 保持窗口打开
pause 