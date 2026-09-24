/* ═══════════════════════════════════════
   TRABIO — toast.js
   Sistema de notificações elegantes
   ═══════════════════════════════════════ */

function _ensureToastContainer() {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';

    const style = document.createElement('style');
    style.innerHTML = `
      #toast-container {
        position: fixed;
        bottom: 24px;
        right: 24px;
        display: flex;
        flex-direction: column;
        gap: 12px;
        z-index: 99999;
        pointer-events: none;
      }
      .toast-item {
        pointer-events: all;
        background: #fff;
        border-radius: 16px;
        padding: 14px 18px;
        display: flex;
        align-items: center;
        gap: 14px;
        max-width: 420px;
        min-width: 280px;
        box-shadow: 0 8px 32px rgba(0,0,0,0.14);
        transform: translateX(130%);
        animation: toastSlideIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
        font-family: 'Outfit', sans-serif;
        border-left: 6px solid #ccc;
      }
      .toast-item.hide {
        animation: toastSlideOut 0.35s ease forwards;
      }
      .toast-item.toast-error   { border-left-color: #ef4444; }
      .toast-item.toast-success { border-left-color: #2D6A4F; }
      .toast-item.toast-info    { border-left-color: #3B82F6; }

      .toast-icon-wrap {
        width: 40px; height: 40px;
        border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        flex-shrink: 0; overflow: hidden;
      }
      .toast-icon-wrap.err  { background: #fef2f2; }
      .toast-icon-wrap.suc  { background: #E8F5E9; }
      .toast-icon-wrap.info { background: #EFF6FF; }
      .toast-icon-wrap img  { width: 28px; height: 28px; object-fit: contain; border-radius: 6px; }
      .toast-icon-wrap span { font-size: 1.2rem; }

      .toast-body { flex: 1; }
      .toast-title {
        font-weight: 700; font-size: 0.95rem; margin-bottom: 2px;
      }
      .toast-item.toast-error   .toast-title { color: #7f1d1d; }
      .toast-item.toast-success .toast-title { color: #1B4332; }
      .toast-item.toast-info    .toast-title { color: #1e3a5f; }

      .toast-msg { font-size: 0.85rem; line-height: 1.4; }
      .toast-item.toast-error   .toast-msg { color: #b91c1c; }
      .toast-item.toast-success .toast-msg { color: #2D6A4F; }
      .toast-item.toast-info    .toast-msg { color: #1d4ed8; }

      .toast-close {
        background: none; border: none; cursor: pointer;
        font-size: 1.1rem; padding: 0; line-height: 1;
        opacity: 0.45; transition: opacity 0.2s; color: #374151;
      }
      .toast-close:hover { opacity: 1; }

      @keyframes toastSlideIn {
        from { transform: translateX(130%); opacity: 0; }
        to   { transform: translateX(0);    opacity: 1; }
      }
      @keyframes toastSlideOut {
        from { transform: translateX(0);    opacity: 1; }
        to   { transform: translateX(130%); opacity: 0; }
      }
    `;
    document.head.appendChild(style);
    document.body.appendChild(container);
  }
  return container;
}

function _createToast({ type, iconHtml, title, msg, duration = 5000 }) {
  const container = _ensureToastContainer();

  const toast = document.createElement('div');
  toast.className = `toast-item toast-${type}`;
  toast.innerHTML = `
    <div class="toast-icon-wrap ${type === 'error' ? 'err' : type === 'success' ? 'suc' : 'info'}">${iconHtml}</div>
    <div class="toast-body">
      <div class="toast-title">${title}</div>
      <div class="toast-msg">${msg}</div>
    </div>
    <button class="toast-close">&times;</button>
  `;

  container.appendChild(toast);

  const removeToast = () => {
    toast.classList.add('hide');
    setTimeout(() => { if (toast.parentNode) toast.parentNode.removeChild(toast); }, 380);
  };

  toast.querySelector('.toast-close').addEventListener('click', removeToast);
  setTimeout(removeToast, duration);
}

/* ── API pública ─────────────────────── */

function showToastError(msg) {
  _createToast({
    type: 'error',
    iconHtml: '<span>⚠️</span>',
    title: 'Ops, algo deu errado!',
    msg: msg,
    duration: 6000
  });
}

function showToastSuccess(msg) {
  const logoImg = `<img src="/images/logo-trabio.png" alt="Trabio">`;
  _createToast({
    type: 'success',
    iconHtml: logoImg,
    title: 'Sucesso!',
    msg: msg,
    duration: 4500
  });
}

function showToastInfo(msg) {
  _createToast({
    type: 'info',
    iconHtml: '<span>ℹ️</span>',
    title: 'Informação',
    msg: msg,
    duration: 4000
  });
}
