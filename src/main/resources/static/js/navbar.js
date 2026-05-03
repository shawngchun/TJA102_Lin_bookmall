// navbar.js

// 當網頁 DOM 載入完成後執行
document.addEventListener('DOMContentLoaded', () => {
    renderNavbar();
});

async function renderNavbar() {
    // 尋找放置導覽列的容器
    const headerContainer = document.getElementById('global-header');

    if (!headerContainer) {
        console.error("找不到導覽列容器！請確認 HTML 中有 <header id='global-header'></header>");
        return;
    }

    try {
        // 1. 檢查使用者登入狀態
        const response = await fetch('/api/user/me', { credentials: 'include' });

        let navLinksHtml = '';

        if (response.ok) {
            // --- 已登入狀態 ---
            const user = await response.json();
            navLinksHtml = `
                <a href="index.html">首頁</a>
                <span style="color: #bdc3c7; margin-left: 1.5rem; font-size: 0.9rem;">Hi, ${user.username}</span>
                <a href="cart.html">購物車 <span id="cartCount" style="background: var(--accent); color: white; padding: 2px 6px; border-radius: 10px; font-size: 0.8rem;">0</span></a>
                <a href="orders.html">我的訂單</a>
                <a href="#" onclick="handleLogout(event)">登出</a>
            `;
        } else {
            // --- 未登入狀態 ---
            navLinksHtml = `
                <a href="index.html">首頁</a>
                <a href="login.html">登入/註冊</a>
                <a href="cart.html">購物車 <span id="cartCount" style="background: var(--accent); color: white; padding: 2px 6px; border-radius: 10px; font-size: 0.8rem;">0</span></a>
            `;
        }

        // 2. 將 HTML 結構注入到 <header> 中
        headerContainer.innerHTML = `
            <h1 onclick="window.location.href='index.html'" style="cursor: pointer; margin: 0; font-size: 1.5rem; letter-spacing: 2px;">幻象書屋</h1>
            <nav class="nav-links" id="navContent" style="display: flex; align-items: center;">
                ${navLinksHtml}
            </nav>
        `;

        // 3. 渲染完導覽列後，更新購物車數量
        updateCartBadge();

    } catch (error) {
        console.error('導覽列初始化失敗:', error);
    }
}

// 統一更新購物車數字
async function updateCartBadge() {
    try {
        const response = await fetch('/api/cart/count', { credentials: 'include' });
        if (response.ok) {
            const count = await response.json();
            const badge = document.getElementById('cartCount');
            if (badge) badge.textContent = count;
        }
    } catch (error) {
        console.warn('無法更新購物車數量');
    }
}

// 統一登出處理
async function handleLogout(e) {
    if (e) e.preventDefault(); // 阻止 a 標籤的預設跳轉行為
    if (!confirm('確定要登出嗎？')) return;

    try {
        await fetch('/api/logout', { method: 'POST' });
        window.location.href = 'index.html'; // 登出後統一導回首頁
    } catch (error) {
        alert('登出失敗，請稍後再試');
    }
}