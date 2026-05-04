// navbar.js

document.addEventListener('DOMContentLoaded', () => {
    // 動態注入下拉選單需要的 CSS 樣式
    injectDropdownStyles();
    // 渲染導覽列
    renderNavbar();
});

// 1. 動態注入 CSS (這樣就不用修改所有 HTML 的 <style>)
function injectDropdownStyles() {
    const style = document.createElement('style');
    style.textContent = `
        /* 使用者區塊容器 (為了定位下拉選單) */
        .user-dropdown-container {
            position: relative;
            display: inline-block;
            margin-left: 1.5rem;
        }

        /* 觸發下拉選單的按鈕/文字 */
        .user-dropdown-trigger {
            color: #bdc3c7;
            font-size: 0.9rem;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 5px;
            padding: 5px 0;
            transition: color 0.3s;
        }

        .user-dropdown-trigger:hover {
            color: var(--white);
        }

        /* 下拉選單本體 */
        .user-dropdown-menu {
            display: none; /* 預設隱藏 */
            position: absolute;
            top: 100%; /* 顯示在觸發器正下方 */
            right: 0;  /* 靠右對齊 */
            background-color: var(--white);
            min-width: 150px;
            box-shadow: 0 8px 16px rgba(0,0,0,0.1);
            border-radius: 8px;
            z-index: 1001;
            overflow: hidden;
            margin-top: 10px;
        }

        /* 當含有 'show' 類別時顯示 */
        .user-dropdown-menu.show {
            display: block;
            animation: fadeInDown 0.2s ease-out;
        }

        /* 下拉選單內的連結 */
        .user-dropdown-menu a {
            color: var(--text) !important; /* 覆蓋導覽列預設的白色 */
            padding: 12px 16px;
            text-decoration: none;
            display: block;
            margin: 0 !important; /* 覆蓋 nav-links a 的 margin-left */
            font-size: 0.9rem !important;
            transition: background-color 0.2s;
            border-bottom: 1px solid #f1f1f1;
        }
        
        .user-dropdown-menu a:last-child {
            border-bottom: none;
        }

        .user-dropdown-menu a:hover {
            background-color: #f8f9fa;
            color: var(--accent) !important;
        }

        /* 箭頭小動畫 */
        .dropdown-arrow {
            font-size: 0.7em;
            transition: transform 0.3s;
        }
        .user-dropdown-container.active .dropdown-arrow {
            transform: rotate(180deg);
        }

        @keyframes fadeInDown {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }
    `;
    document.head.appendChild(style);
}

// 2. 渲染導覽列
async function renderNavbar() {
    const headerContainer = document.getElementById('global-header');

    if (!headerContainer) return;

    try {
        const response = await fetch('/api/user/me', { credentials: 'include' });
        let navLinksHtml = '';

        if (response.ok) {
            // --- 已登入狀態 ---
            const user = await response.json();

            navLinksHtml = `
                <a href="index.html">首頁</a>
                <a href="cart.html">購物車 <span id="cartCount" style="background: var(--accent); color: white; padding: 2px 6px; border-radius: 10px; font-size: 0.8rem;">0</span></a>
                <a href="orders.html">我的訂單</a>
                
                <!-- 新增：使用者下拉選單結構 -->
                <div class="user-dropdown-container" id="userDropdownContainer">
                    <div class="user-dropdown-trigger" onclick="toggleDropdown(event)">
                        Hi, ${user.username} <span class="dropdown-arrow">▼</span>
                    </div>
                    <div class="user-dropdown-menu" id="userDropdownMenu">
                        <a href="update-username.html">更改名稱</a>
                        <a href="change-password.html">更改密碼</a>
                        <a href="#" onclick="handleLogout(event)">登出</a>
                    </div>
                </div>
            `;
        } else {
            // --- 未登入狀態 ---
            navLinksHtml = `
                <a href="index.html">首頁</a>
                <a href="login.html">登入/註冊</a>
                <a href="login.html">購物車 <span id="cartCount" style="background: var(--accent); color: white; padding: 2px 6px; border-radius: 10px; font-size: 0.8rem;">0</span></a>
            `;
        }

        headerContainer.innerHTML = `
            <h1 onclick="window.location.href='index.html'" style="cursor: pointer; margin: 0; font-size: 1.5rem; letter-spacing: 2px;">幻象書屋</h1>
            <nav class="nav-links" id="navContent" style="display: flex; align-items: center;">
                ${navLinksHtml}
            </nav>
        `;

        updateCartBadge();

    } catch (error) {
        console.error('導覽列初始化失敗:', error);
    }
}

// 3. 切換下拉選單顯示/隱藏
function toggleDropdown(event) {
    event.stopPropagation(); // 阻止事件冒泡，避免點擊觸發器時立刻觸發 body 的點擊事件
    const menu = document.getElementById('userDropdownMenu');
    const container = document.getElementById('userDropdownContainer');

    if (menu) {
        menu.classList.toggle('show');
        container.classList.toggle('active');
    }
}

// 4. 點擊畫面其他地方時，自動收合下拉選單
window.onclick = function (event) {
    const menu = document.getElementById('userDropdownMenu');
    const container = document.getElementById('userDropdownContainer');

    // 如果點擊的目標不是下拉選單按鈕，也不是下拉選單內部，就收合選單
    if (menu && menu.classList.contains('show')) {
        if (!event.target.closest('.user-dropdown-container')) {
            menu.classList.remove('show');
            container.classList.remove('active');
        }
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
    if (e) e.preventDefault();
    if (!confirm('確定要登出嗎？')) return;

    try {
        await fetch('/api/logout', { method: 'POST' });
        window.location.href = 'index.html';
    } catch (error) {
        alert('登出失敗，請稍後再試');
    }
}