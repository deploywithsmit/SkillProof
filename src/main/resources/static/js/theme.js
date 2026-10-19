/**
 * SkillProof Theme Manager
 * Dark mode default with light mode toggle and local preference persistence
 */

const THEME_KEY = 'skillproof_theme';

function initTheme() {
  const saved = localStorage.getItem(THEME_KEY);
  if (saved === 'light' || saved === 'dark') {
    applyTheme(saved);
  } else {
    // Respect OS preference or default to dark
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    applyTheme(prefersDark ? 'dark' : 'dark');
  }

  // Bind toggle buttons
  document.querySelectorAll('.theme-toggle-btn').forEach(btn => {
    btn.addEventListener('click', toggleTheme);
    updateToggleBtn(btn, document.documentElement.getAttribute('data-theme') || 'dark');
  });
}

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem(THEME_KEY, theme);
  document.querySelectorAll('.theme-toggle-btn').forEach(btn => updateToggleBtn(btn, theme));
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'dark';
  const target = current === 'dark' ? 'light' : 'dark';
  applyTheme(target);
}

function updateToggleBtn(btn, theme) {
  if (btn) {
    btn.innerHTML = getIcon(theme === 'dark' ? 'sun' : 'moon', 18);
    btn.setAttribute('title', theme === 'dark' ? 'Switch to light theme' : 'Switch to dark theme');
  }
}

document.addEventListener('DOMContentLoaded', initTheme);
