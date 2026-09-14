/* ══════════════════════════════════════
   TRABIO — main.js
   Interações da landing page
   ══════════════════════════════════════ */

// ── Auth Check ───────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('trabio_token');
  const role = localStorage.getItem('trabio_role');
  
  const loggedOutActions = document.getElementById('nav-actions-logged-out');
  const loggedInActions = document.getElementById('nav-actions-logged-in');
  
  if (token && loggedInActions) {
    if(loggedOutActions) loggedOutActions.style.display = 'none';
    loggedInActions.style.display = 'flex';
    
    if (role === 'PRESTADOR') {
      const linkServicos = document.getElementById('nav-link-servicos');
      if (linkServicos) linkServicos.style.display = 'block';
    }
    
    const profileBtn = document.getElementById('profile-dropdown-btn');
    const dropdown = document.getElementById('profile-dropdown');
    
    if(profileBtn && dropdown) {
      profileBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        dropdown.classList.toggle('show');
      });
      document.addEventListener('click', (e) => {
        if (!dropdown.contains(e.target)) {
          dropdown.classList.remove('show');
        }
      });
    }
    
    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
      btnLogout.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('trabio_token');
        localStorage.removeItem('trabio_role');
        window.location.reload();
      });
    }
  }
});

// ── Navbar scroll effect ─────────────────────
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
  if (window.scrollY > 40) {
    navbar.classList.add('scrolled');
  } else {
    navbar.classList.remove('scrolled');
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
  const cat    = document.getElementById('select-categoria')?.value || '';
  const cidade = document.getElementById('input-cidade')?.value.trim() || '';
  if (!cat && !cidade) {
    shakeElement(document.getElementById('search-bar'));
    return;
  }
  const params = new URLSearchParams();
  if (cat)    params.set('categoria', cat);
  if (cidade) params.set('cidade', cidade);
  
  showToast(' Redirecionando para resultados...');
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

// ── Profile mock ──────────────────────────────
document.querySelectorAll('.btn-ver-perfil').forEach(btn => {
  btn.addEventListener('click', () => showToast(' Faça login para ver o perfil completo'));
});

console.log('%cTrabio ', 'font-size:20px;font-weight:bold;color:#2D6A4F;');
console.log('%cPlataforma de serviços autônomos — versão MVP', 'color:#52B788;');

