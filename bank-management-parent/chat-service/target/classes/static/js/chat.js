// 发送用户输入到服务器
function sendUserInput() {
    const userInput = document.getElementById('userInput').value.trim();
    if (userInput.length === 0) return;
    
    // 显示用户消息
    addMessageToChat('user', userInput);
    document.getElementById('userInput').value = '';
    
    // 发送到服务器
    fetch('/api/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: userInput })
    })
    .then(response => response.json())
    .then(data => {
        // 显示服务器响应
        addMessageToChat('bot', data.response);
    })
    .catch(error => {
        console.error('Error:', error);
        addMessageToChat('bot', '抱歉，发生了错误，请稍后再试。');
    });
}

// 添加消息到聊天窗口
function addMessageToChat(sender, message) {
    const chatContainer = document.getElementById('chatContainer');
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message', sender === 'user' ? 'user-message' : 'bot-message');
    
    messageDiv.innerText = message;
    chatContainer.appendChild(messageDiv);
    
    // 自动滚动到底部
    chatContainer.scrollTop = chatContainer.scrollHeight;
}

// 添加快捷问题按钮
function addQuickQuestionButtons() {
    const buttonContainer = document.getElementById('quickButtons');
    
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
        button.classList.add('quick-button');
        button.onclick = function() {
            document.getElementById('userInput').value = question;
            sendUserInput();
        };
        buttonContainer.appendChild(button);
    });
}

// 下载对话记录
function downloadChatHistory() {
    const chatContainer = document.getElementById('chatContainer');
    const messages = chatContainer.querySelectorAll('.message');
    let content = '# 银行系统对话记录\n\n';
    content += `日期: ${new Date().toLocaleString()}\n\n`;
    
    messages.forEach(message => {
        const isUser = message.classList.contains('user-message');
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

// 添加文件上传功能
function setupFileUpload() {
    const fileInput = document.getElementById('fileUpload');
    const uploadForm = document.getElementById('uploadForm');
    
    uploadForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = new FormData(uploadForm);
        
        fetch('/api/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            addMessageToChat('bot', `文件上传结果: ${data.message}`);
        })
        .catch(error => {
            console.error('Error uploading file:', error);
            addMessageToChat('bot', '文件上传失败，请稍后再试。');
        });
    });
}

// 页面加载完成后执行
window.onload = function() {
    // 添加发送按钮事件监听
    document.getElementById('sendButton').addEventListener('click', sendUserInput);
    
    // 添加回车键发送
    document.getElementById('userInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendUserInput();
        }
    });
    
    // 添加下载按钮事件监听
    document.getElementById('downloadButton').addEventListener('click', downloadChatHistory);
    
    // 添加快捷问题按钮
    addQuickQuestionButtons();
    
    // 设置文件上传
    setupFileUpload();
    
    // 欢迎消息
    addMessageToChat('bot', '您好！我是银行智能助手，请问有什么可以帮您？');
}; 