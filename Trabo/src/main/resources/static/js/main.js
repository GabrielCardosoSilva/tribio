/* ══════════════════════════════════════
   TRABIO — main.js
   Interações da landing page
   ══════════════════════════════════════ */

// ── Auth Check ───────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('trabio_token');
  const role = localStorage.getItem('trabio_role');
  
  if (token) {
    if (role === 'PRESTADOR') {
      const linkServicos = document.getElementById('nav-link-servicos');
      if (linkServicos) linkServicos.style.display = 'block';
    }
    
    const btnLogin = document.getElementById('btn-login');
    const btnCadastro = document.getElementById('btn-cadastro');
    const navLoggedIn = document.getElementById('nav-actions-logged-in');
    
    if (btnLogin) btnLogin.style.display = 'none';
    if (btnCadastro) btnCadastro.style.display = 'none';
    if (navLoggedIn) navLoggedIn.style.display = 'block';

    carregarAvatarNavGlobal();
  } else {
    // Hide 'Profissionais' section if not logged in
    const destaquesSec = document.getElementById('destaques');
    if (destaquesSec) destaquesSec.style.display = 'none';
    
    document.querySelectorAll('#nav-links a[href="#destaques"]').forEach(link => {
      if (link.parentElement) link.parentElement.style.display = 'none';
    });
  }
});

async function carregarAvatarNavGlobal() {
  try {
    const token = localStorage.getItem('trabio_token');
    const res = await fetch('/api/usuarios/me', { headers: { 'Authorization': `Bearer ${token}` } });
    if (res.ok) {
      const u = await res.json();
      const avatarEl = document.getElementById('nav-profile-avatar');
      if (avatarEl) {
        if (u.fotoPerfil) {
          avatarEl.innerHTML = `<img src="${u.fotoPerfil}" alt="${u.nome}" style="width:100%;height:100%;object-fit:cover;border-radius:50%;">`;
        } else {
          avatarEl.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color:#2D6A4F; margin: auto; display: block; width: 100%; height: 100%; padding: 4px;"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>`;
        }
      }
    } else if (res.status === 401 || res.status === 403) {
      // Token inválido ou expirado (ex: banco resetado)
      localStorage.removeItem('trabio_token');
      localStorage.removeItem('trabio_role');
      window.location.reload();
    }
  } catch(_) {}
}

// ── Profile Dropdown Logic ───────────────────
document.addEventListener('DOMContentLoaded', () => {
  const profileBtn = document.getElementById('profile-dropdown-btn');
  const profileDropdown = document.getElementById('profile-dropdown');
  if (profileBtn && profileDropdown) {
    profileBtn.addEventListener('click', (e) => {
      e.stopPropagation();
      profileDropdown.classList.toggle('show');
    });
    document.addEventListener('click', (e) => {
      if (!profileDropdown.contains(e.target)) {
        profileDropdown.classList.remove('show');
      }
    });
  }

  const btnLogout = document.getElementById('btn-logout');
  if (btnLogout) {
    btnLogout.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('trabio_token');
      localStorage.removeItem('trabio_role');
      window.location.href = '/';
    });
  }
});

// ── Navbar scroll effect ─────────────────────
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
  if (navbar && !navbar.classList.contains('always-scrolled')) {
    if (window.scrollY > 40) {
      navbar.classList.add('scrolled');
    } else {
      navbar.classList.remove('scrolled');
    }
  }
}, { passive: true });

// ── Hamburger menu ───────────────────────────
const hamburger = document.getElementById('hamburger');
const navLinks  = document.getElementById('nav-links');
const navActions = document.querySelector('.nav-actions');

hamburger?.addEventListener('click', () => {
  const open = navLinks.style.display === 'flex';
  navLinks.style.display    = open ? 'none' : 'flex';
  navActions.style.display  = open ? 'none' : 'flex';
  if (!open) {
    navLinks.style.flexDirection  = 'column';
    navLinks.style.position       = 'absolute';
    navLinks.style.top            = '72px';
    navLinks.style.left           = '0';
    navLinks.style.right          = '0';
    navLinks.style.background     = 'rgba(255,255,255,.97)';
    navLinks.style.backdropFilter = 'blur(20px)';
    navLinks.style.padding        = '16px 24px 24px';
    navActions.style.flexDirection = 'column';
    navActions.style.position      = 'absolute';
    navActions.style.top           = 'calc(72px + 180px)';
    navActions.style.left          = '0';
    navActions.style.right         = '0';
    navActions.style.padding       = '0 24px 24px';
    navActions.style.background    = 'rgba(255,255,255,.97)';
  }
});

// ── Smooth scroll for nav links ───────────────
document.querySelectorAll('a[href^="#"]').forEach(link => {
  link.addEventListener('click', e => {
    const target = document.querySelector(link.getAttribute('href'));
    if (target) {
      e.preventDefault();
      target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      // Close mobile menu
      navLinks.style.display = '';
      navActions.style.display = '';
    }
  });
});

// ── Quick-tag click sets select + input ───────
document.querySelectorAll('.hero-tags .tag').forEach(tag => {
  tag.addEventListener('click', () => {
    const cat = tag.dataset.cat;
    const select = document.getElementById('select-categoria');
    if (select) {
      for (let opt of select.options) {
        if (opt.value === cat) { select.value = cat; break; }
      }
    }
    document.getElementById('input-cidade')?.focus();
  });
});

// ── Category cards click ──────────────────────
document.querySelectorAll('.cat-card').forEach(card => {
  card.addEventListener('click', e => {
    e.preventDefault();
    const cat = card.dataset.cat;
    if (cat) {
      const select = document.getElementById('select-categoria');
      for (let opt of select?.options || []) {
        if (opt.value === cat) { select.value = cat; break; }
      }
      document.getElementById('hero')?.scrollIntoView({ behavior: 'smooth' });
      setTimeout(() => document.getElementById('input-cidade')?.focus(), 700);
    }
  });
});

// ── Search button ─────────────────────────────
document.getElementById('btn-search')?.addEventListener('click', () => {
  // Verificar se está logado
  if (!localStorage.getItem('trabio_token')) {
    showToast('⚠ Você precisa estar logado para buscar profissionais.');
    setTimeout(() => { window.location.href = '/login.html'; }, 1200);
    return;
  }

  const cat    = document.getElementById('select-categoria')?.value || '';
  const cidade = document.getElementById('input-cidade')?.value.trim() || '';
  if (!cat && !cidade) {
    shakeElement(document.getElementById('search-bar'));
    return;
  }
  const params = new URLSearchParams();
  if (cat)    params.set('categoria', cat);
  if (cidade) params.set('cidade', cidade);

  showToast('🔍 Redirecionando para resultados...');
  setTimeout(() => {
    window.location.href = `/buscar.html?${params.toString()}`;
  }, 800);
});

function shakeElement(el) {
  if (!el) return;
  el.style.animation = 'none';
  el.offsetHeight; // reflow
  el.style.animation = 'shake 0.4s ease';
  el.addEventListener('animationend', () => { el.style.animation = ''; }, { once: true });
}

// Adiciona keyframe shake no CSS dinâmico
const shakeStyle = document.createElement('style');
shakeStyle.textContent = `
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%       { transform: translateX(-8px); }
  40%       { transform: translateX(8px); }
  60%       { transform: translateX(-5px); }
  80%       { transform: translateX(5px); }
}`;
document.head.appendChild(shakeStyle);

// ── Animated counters ─────────────────────────
function animateCounter(el, target, duration = 1800) {
  const isDecimal = el.dataset.target.includes('.');
  if (isDecimal) { el.textContent = '0'; return; }
  let start = 0;
  const step = Math.ceil(target / (duration / 16));
  const timer = setInterval(() => {
    start = Math.min(start + step, target);
    el.textContent = start.toLocaleString('pt-BR');
    if (start >= target) clearInterval(timer);
  }, 16);
}

const statsObserver = new IntersectionObserver(entries => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      document.querySelectorAll('.stat-number').forEach(el => {
        const target = parseFloat(el.dataset.target);
        if (target < 10) {
          el.textContent = el.dataset.target;
        } else {
          animateCounter(el, target);
        }
      });
      statsObserver.disconnect();
    }
  });
}, { threshold: 0.3 });

const statsBar = document.getElementById('stats-bar');
if (statsBar) statsObserver.observe(statsBar);

// ── Scroll reveal ─────────────────────────────
const revealObserver = new IntersectionObserver(entries => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible');
      revealObserver.unobserve(entry.target);
    }
  });
}, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' });

// Apply reveal to cards and sections
const revealTargets = [
  '.step-card', '.cat-card', '.pro-card',
  '.testimonial-card', '.feature-item',
  '.section-header'
];
document.querySelectorAll(revealTargets.join(',')).forEach((el, i) => {
  el.classList.add('reveal');
  el.style.transitionDelay = `${(i % 4) * 80}ms`;
  revealObserver.observe(el);
});

// ── Testimonial carousel ──────────────────────
let currentSlide = 0;
const track = document.getElementById('testimonials-track');
const dots   = document.querySelectorAll('.dot');
const cards  = track ? track.querySelectorAll('.testimonial-card') : [];
const totalSlides = cards.length;

function goToSlide(index) {
  if (!track || totalSlides === 0) return;
  currentSlide = ((index % totalSlides) + totalSlides) % totalSlides;
  const cardWidth = cards[0].offsetWidth + 24; // gap
  track.style.transform = `translateX(-${currentSlide * cardWidth}px)`;
  dots.forEach((dot, i) => dot.classList.toggle('active', i === currentSlide));
}

dots.forEach(dot => {
  dot.addEventListener('click', () => goToSlide(parseInt(dot.dataset.index)));
});

// Auto-advance
let carouselTimer = setInterval(() => goToSlide(currentSlide + 1), 4500);
track?.addEventListener('mouseenter', () => clearInterval(carouselTimer));
track?.addEventListener('mouseleave', () => {
  carouselTimer = setInterval(() => goToSlide(currentSlide + 1), 4500);
});

// Handle resize
window.addEventListener('resize', () => goToSlide(currentSlide), { passive: true });

// ── Toast notification ────────────────────────
function showToast(message, type = 'info') {
  const toast = document.createElement('div');
  toast.style.cssText = `
    position: fixed; bottom: 28px; right: 28px; z-index: 9999;
    background: ${type === 'error' ? '#EF4444' : '#2D6A4F'};
    color: #fff; padding: 14px 22px; border-radius: 12px;
    font-family: 'Outfit', sans-serif; font-size: .95rem; font-weight: 500;
    box-shadow: 0 8px 24px rgba(0,0,0,.2);
    transform: translateY(80px); opacity: 0;
    transition: transform .35s cubic-bezier(.4,0,.2,1), opacity .35s ease;
  `;
  toast.textContent = message;
  document.body.appendChild(toast);
  requestAnimationFrame(() => {
    toast.style.transform = 'translateY(0)';
    toast.style.opacity = '1';
  });
  setTimeout(() => {
    toast.style.transform = 'translateY(80px)';
    toast.style.opacity = '0';
    toast.addEventListener('transitionend', () => toast.remove());
  }, 3500);
}

// ── WhatsApp mock (em prod usa número real) ───
document.querySelectorAll('.btn-whatsapp').forEach(btn => {
  btn.addEventListener('click', () => {
    showToast(' Abrindo WhatsApp...');
    // Em prod: window.open(`https://wa.me/55${numero}?text=...`);
  });
});

// ── CTA Buscar Auth Check ─────────────────────
const ctaUsuarioBtn = document.getElementById('cta-usuario-btn');
if (ctaUsuarioBtn) {
  ctaUsuarioBtn.addEventListener('click', (e) => {
    if (!localStorage.getItem('trabio_token')) {
      e.preventDefault();
      window.location.href = '/login.html';
    }
  });
}

// ── Profile mock ──────────────────────────────
document.querySelectorAll('.btn-ver-perfil').forEach(btn => {
  btn.addEventListener('click', () => showToast(' Faça login para ver o perfil completo'));
});

console.log('%cTrabio ', 'font-size:20px;font-weight:bold;color:#2D6A4F;');
console.log('%cPlataforma de serviços autônomos — versão MVP', 'color:#52B788;');

