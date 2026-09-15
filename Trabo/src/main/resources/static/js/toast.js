function showToastError(msg) {
  let toastContainer = document.getElementById('toast-container');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    
    const style = document.createElement('style');
    style.innerHTML = `
      #toast-container {
        position: fixed;
        bottom: 20px;
        right: 20px;
        display: flex;
        flex-direction: column;
        gap: 10px;
        z-index: 99999;
      }
      .toast-error {
        background: #fff;
        border-left: 6px solid #ef4444;
        box-shadow: 0 10px 25px rgba(220, 38, 38, 0.15);
        border-radius: 12px;
        padding: 16px 20px;
        display: flex;
        align-items: flex-start;
        gap: 12px;
        max-width: 400px;
        transform: translateX(120%);
        animation: slideIn 0.4s forwards;
        font-family: 'Outfit', sans-serif;
      }
      .toast-error.hide {
        animation: slideOut 0.4s forwards;
      }
      .toast-icon {
        background: #fef2f2;
        color: #ef4444;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.2rem;
        flex-shrink: 0;
      }
      .toast-content {
        flex: 1;
      }
      .toast-title {
        color: #7f1d1d;
        font-weight: 700;
        margin-bottom: 4px;
        font-size: 1rem;
      }
      .toast-message {
        color: #b91c1c;
        font-size: 0.9rem;
        line-height: 1.4;
      }
      .toast-close {
        background: none;
        border: none;
        color: #ef4444;
        cursor: pointer;
        font-size: 1.2rem;
        padding: 0;
        line-height: 1;
        opacity: 0.6;
      }
      .toast-close:hover { opacity: 1; }
      @keyframes slideIn {
        from { transform: translateX(120%); }
        to { transform: translateX(0); }
      }
      @keyframes slideOut {
        from { transform: translateX(0); }
        to { transform: translateX(120%); }
      }
    `;
    document.head.appendChild(style);
    document.body.appendChild(toastContainer);
  }

  const toast = document.createElement('div');
  toast.className = 'toast-error';
  toast.innerHTML = `
    <div class="toast-icon">⚠️</div>
    <div class="toast-content">
      <div class="toast-title">Ops, algo deu errado!</div>
      <div class="toast-message">${msg}</div>
    </div>
    <button class="toast-close">&times;</button>
  `;

  toastContainer.appendChild(toast);

  const closeBtn = toast.querySelector('.toast-close');
  
  const removeToast = () => {
    toast.classList.add('hide');
    setTimeout(() => {
      if(toast.parentNode) toast.parentNode.removeChild(toast);
    }, 400);
  };
  
  closeBtn.addEventListener('click', removeToast);

  // Auto remove after 6 seconds
  setTimeout(removeToast, 6000);
}
