/**
 * SkillProof Central API Client & Navigation Architecture
 */

let cachedCsrfToken = null;

function getCookie(name) {
  const match = document.cookie.match(new RegExp('(^|;\\s*)(' + name + ')=([^;]*)'));
  return match ? decodeURIComponent(match[3]) : null;
}

async function syncCsrfToken() {
  if (cachedCsrfToken) return cachedCsrfToken;
  const cookieVal = getCookie('XSRF-TOKEN');
  if (cookieVal) {
    cachedCsrfToken = cookieVal;
    return cachedCsrfToken;
  }
  try {
    const res = await fetch('/api/v1/auth/csrf', { credentials: 'same-origin' });
    if (res.ok) {
      const json = await res.json();
      if (json.data && json.data.token) {
        cachedCsrfToken = json.data.token;
      }
    }
  } catch (e) {
    // Ignore initial fetch error
  }
  return cachedCsrfToken || getCookie('XSRF-TOKEN') || '';
}

async function apiFetch(endpoint, options = {}) {
  const opts = { ...options };
  opts.credentials = 'same-origin';
  opts.headers = { ...(opts.headers || {}) };

  const method = (opts.method || 'GET').toUpperCase();
  if (method !== 'GET' && method !== 'HEAD') {
    const csrfToken = await syncCsrfToken();
    if (csrfToken) {
      opts.headers['X-XSRF-TOKEN'] = csrfToken;
    }
  }

  // Set JSON content-type if body is an object and not FormData
  if (opts.body && !(opts.body instanceof FormData) && typeof opts.body === 'object') {
    opts.headers['Content-Type'] = 'application/json';
    opts.body = JSON.stringify(opts.body);
  }

  const response = await fetch(endpoint, opts);

  // If unauthorized on protected API
  if (response.status === 401) {
    const isAuthPage = window.location.pathname.includes('auth.html') || 
                       window.location.pathname.endsWith('index.html') || 
                       window.location.pathname === '/';
    if (!isAuthPage) {
      sessionStorage.setItem('skillproof_redirect', window.location.pathname);
      window.location.href = '/auth.html?step=login';
      return;
    }
  }

  const contentType = response.headers.get('content-type');
  let data = null;

  if (contentType && contentType.includes('application/json')) {
    data = await response.json();
  }

  if (!response.ok) {
    const errorMsg = (data && (data.message || data.error)) || `Request failed with status ${response.status}`;
    const err = new Error(errorMsg);
    err.status = response.status;
    err.data = data;
    throw err;
  }

  return data;
}

// User state caching
let currentUser = null;

async function fetchCurrentUser() {
  try {
    const res = await apiFetch('/api/v1/auth/me');
    if (res && res.data) {
      currentUser = res.data;
      return currentUser;
    }
  } catch (e) {
    currentUser = null;
  }
  return null;
}

async function logout() {
  try {
    await apiFetch('/api/v1/auth/logout', { method: 'POST' });
  } catch (e) {
    // Ignore error
  }
  currentUser = null;
  showToast('Logged out successfully', 'info');
  setTimeout(() => {
    window.location.href = '/auth.html?step=login';
  }, 400);
}

/**
 * Shared Application Sidebar Generator
 */
function renderAppSidebar(activePageId) {
  const sidebarContainer = document.getElementById('app-sidebar');
  if (!sidebarContainer) return;

  const isMentorOrAdmin = currentUser && (currentUser.role === 'MENTOR' || currentUser.role === 'ADMIN');

  const navItems = [
    { id: 'dashboard', label: 'Overview', href: '/dashboard.html', icon: 'layout' },
    { id: 'skills', label: 'Explore Skills', href: '/skills.html', icon: 'compass' },
    { id: 'challenges', label: 'Challenges', href: '/challenges.html', icon: 'code-2' },
    { id: 'submissions', label: 'My Submissions', href: '/submissions.html', icon: 'file-code' },
    { id: 'verified', label: 'Verified Skills', href: '/verified.html', icon: 'award' },
    { id: 'leaderboard', label: 'Leaderboard', href: '/leaderboard.html', icon: 'trophy' },
    ...(isMentorOrAdmin ? [{ id: 'mentor', label: 'Mentor Review', href: '/mentor.html', icon: 'shield' }] : []),
    { id: 'profile', label: 'My Profile', href: currentUser ? `/profile.html?user=${currentUser.username}` : '/profile.html', icon: 'user' },
    { id: 'settings', label: 'Settings', href: '/settings.html', icon: 'settings' }
  ];

  const initials = currentUser && currentUser.fullName ? 
    currentUser.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : 'SP';

  const userDisplayName = currentUser ? currentUser.fullName : 'Guest Developer';
  const userRole = currentUser ? currentUser.role : 'GUEST';

  sidebarContainer.innerHTML = `
    <div class="sidebar-header">
      <a href="/index.html" class="brand-logo">
        <div class="brand-icon">
          ${getIcon('shield', 18)}
        </div>
        <span>SkillProof</span>
        <span class="brand-badge">PROVE</span>
      </a>
      <button class="icon-btn mobile-menu-btn" id="close-sidebar-btn" aria-label="Close navigation">
        ${getIcon('x', 18)}
      </button>
    </div>

    <div class="sidebar-nav">
      <div class="nav-section-title">Navigation</div>
      ${navItems.map(item => `
        <a href="${item.href}" class="nav-item ${activePageId === item.id ? 'active' : ''}">
          <span class="icon">${getIcon(item.icon, 18)}</span>
          <span>${item.label}</span>
        </a>
      `).join('')}
    </div>

    <div class="sidebar-footer">
      <div class="user-profile-widget">
        <div class="user-avatar">${initials}</div>
        <div class="user-meta">
          <div class="user-name" title="${userDisplayName}">${userDisplayName}</div>
          <div class="user-role-badge">${userRole}</div>
        </div>
        <button id="logout-btn" class="icon-btn" title="Sign out" style="width:30px;height:30px;border:none;">
          ${getIcon('log-out', 16)}
        </button>
      </div>
    </div>
  `;

  // Bind close and logout buttons
  const closeBtn = document.getElementById('close-sidebar-btn');
  if (closeBtn) {
    closeBtn.addEventListener('click', () => {
      sidebarContainer.classList.remove('mobile-open');
    });
  }

  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', logout);
  }
}

/**
 * Shared Top Navigation Bar Handler
 */
function initTopbar() {
  const menuToggle = document.getElementById('mobile-menu-toggle');
  const sidebar = document.getElementById('app-sidebar');
  if (menuToggle && sidebar) {
    menuToggle.addEventListener('click', () => {
      sidebar.classList.toggle('mobile-open');
    });
  }

  // Load unread notifications count if user logged in
  if (currentUser) {
    apiFetch('/api/v1/notifications/unread-count')
      .then(res => {
        if (res && res.data && res.data.unreadCount > 0) {
          const dot = document.getElementById('notification-dot');
          if (dot) dot.style.display = 'block';
        }
      })
      .catch(() => {});
  }
}

window.SkillProof = {
  apiFetch,
  fetchCurrentUser,
  getCurrentUser: () => currentUser,
  logout,
  renderAppSidebar,
  initTopbar
};
