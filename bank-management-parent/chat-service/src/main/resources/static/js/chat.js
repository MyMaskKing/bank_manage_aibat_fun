// 全局变量存储当前选择的文件
let currentFile = null;

// 处理文件选择
document.getElementById('fileUpload').addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        currentFile = file;
        document.getElementById('fileName').textContent = file.name;
        addMessageToChat('bot', `已选择文件：${file.name}\n请告诉我您想要如何处理这个文件？`);
    }
});

// 处理消息发送
function sendMessage(event) {
    event.preventDefault();
    const userInput = document.getElementById('userInput').value.trim();
    if (userInput.length === 0) return false;
    
    // 显示用户消息
    addMessageToChat('user', userInput);
    document.getElementById('userInput').value = '';
    
    // 显示加载指示器
    document.getElementById('loading').style.display = 'block';
    
    // 如果有文件且用户输入包含批处理相关关键词
    if (currentFile && (
        userInput.includes('批量') || 
        userInput.includes('处理文件') || 
        userInput.includes('处理这个') ||
        userInput.includes('文件') ||
        userInput.includes('csv') ||
        userInput.includes('excel')
    )) {
        // 处理文件上传
        const formData = new FormData();
        formData.append('file', currentFile);
        formData.append('instruction', userInput);
        
        fetch('/api/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            addMessageToChat('bot', data.message);
            // 清除当前文件
            currentFile = null;
            document.getElementById('fileName').textContent = '';
            document.getElementById('fileUpload').value = '';
        })
        .catch(error => {
            console.error('Error:', error);
            addMessageToChat('bot', '文件处理失败，请稍后再试。');
        })
        .finally(() => {
            document.getElementById('loading').style.display = 'none';
        });
    } else {
        // 普通消息处理
        fetch('/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 
                message: userInput,
                hasFile: currentFile !== null
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data && data.response) {
                addMessageToChat('bot', data.response);
            } else {
                addMessageToChat('bot', '抱歉，收到了未预期的响应格式。');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            addMessageToChat('bot', '抱歉，发生了错误，请稍后再试。');
        })
        .finally(() => {
            document.getElementById('loading').style.display = 'none';
        });
    }
    
    return false;
}

// 添加消息到聊天窗口
function addMessageToChat(sender, message) {
    const chatContainer = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${sender === 'user' ? 'user-message' : 'bot-message'}`;
    
    const contentDiv = document.createElement('div');
    contentDiv.className = 'message-content';
    contentDiv.innerText = message;
    
    messageDiv.appendChild(contentDiv);
    chatContainer.appendChild(messageDiv);
    
    // 自动滚动到底部
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

// 添加快捷问题按钮
function addQuickQuestionButtons() {
    const buttonContainer = document.getElementById('quickButtons');
    buttonContainer.innerHTML = ''; // 清空现有按钮
    
    // 定义一些常用的问题
    const questions = [
        '查询张三的客户信息',
        '查看李四的OTP状态',
        '更新王五的OTP为开启',
        '查询赵六的OTP状态'
    ];
    
    questions.forEach(question => {
        const button = document.createElement('button');
        button.innerText = question;
        button.className = 'quick-button';
        button.onclick = function() {
            document.getElementById('userInput').value = question;
            sendMessage(new Event('submit'));
        };
        buttonContainer.appendChild(button);
    });
}

// 下载对话记录
function downloadChatHistory() {
    const chatContainer = document.getElementById('chatMessages');
    const messages = chatContainer.querySelectorAll('.message-content');
    let content = '# 银行系统对话记录\n\n';
    content += `日期: ${new Date().toLocaleString()}\n\n`;
    
    messages.forEach(message => {
        const isUser = message.parentElement.classList.contains('user-message');
        content += `${isUser ? '👤 用户' : '🤖 系统'}: ${message.innerText}\n\n`;
    });
    
    // 创建下载链接
    const element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(content));
    element.setAttribute('download', `chat_history_${new Date().toISOString().slice(0,10)}.md`);
    element.style.display = 'none';
    
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
}

// 页面加载完成后执行
window.onload = function() {
    // 添加回车键发送
    document.getElementById('userInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage(new Event('submit'));
        }
    });
    
    // 添加下载按钮事件监听
    document.getElementById('downloadButton').addEventListener('click', downloadChatHistory);
    
    // 添加快捷问题按钮
    addQuickQuestionButtons();
}; 