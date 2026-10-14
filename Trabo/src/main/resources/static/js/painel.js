/* ═════════════════════════════════════════════════════════════════
   TRABIO — painel.js
   Lógica interativa do Painel do Usuário e Prestador
   ═════════════════════════════════════════════════════════════════ */

const token = localStorage.getItem('trabio_token');
let isPrestador = false;

// ── Navegação por Abas (Tabs) ─────────────────────────────────
const tabBtns = document.querySelectorAll('.tab-btn');
const tabPanes = document.querySelectorAll('.tab-pane');

function showTab(targetId) {
  tabBtns.forEach(btn => btn.classList.remove('active'));
  tabPanes.forEach(pane => pane.classList.remove('active'));

  const btn = document.querySelector(`.tab-btn[data-target="${targetId}"]`);
  const pane = document.getElementById(targetId);

  if (btn && pane) {
    btn.classList.add('active');
    pane.classList.add('active');
  }
}

tabBtns.forEach(btn => {
  btn.addEventListener('click', () => {
    const target = btn.getAttribute('data-target');
    window.location.hash = target;
    showTab(target);
  });
});

window.addEventListener('hashchange', () => {
  const hash = window.location.hash.replace('#', '');
  if (hash) showTab(hash);
});

// Inicialização da Página
document.addEventListener('DOMContentLoaded', async () => {
  const hash = window.location.hash.replace('#', '');
  if (hash) showTab(hash);

  await carregarDadosUsuario();
  carregarFavoritos();
  carregarNotificacoes();
});

// ── Meus Favoritos ────────────────────────────────────────────
function getFavoritosIds() {
  try {
    return JSON.parse(localStorage.getItem('trabio_favoritos') || '[]');
  } catch (_) {
    return [];
  }
}

async function carregarFavoritos() {
  const container = document.getElementById('favoritos-container');
  if (!container) return;

  const ids = getFavoritosIds();

  if (ids.length === 0) {
    container.innerHTML = `
      <div class="bento-card" style="text-align: center; padding: 60px;">
        <div style="margin-bottom: 16px;">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
        </div>
        <h3 style="color: #1B4332;">Nenhum favorito ainda</h3>
        <p style="color: #6B7280; margin-top: 8px;">Vá para a busca e clique no coração dos profissionais que você gostou.</p>
        <button class="btn-primary" style="margin: 20px auto 0;" onclick="window.location.href='/buscar.html'">Buscar Profissionais</button>
      </div>`;
    return;
  }

  container.innerHTML = `<div style="color:#6B7280; padding: 12px 0; font-size:0.9rem;">Carregando favoritos...</div>`;

  try {
    const res = await fetch('/api/prestadores', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Erro na API');

    const todos = await res.json();
    const favoritos = todos.filter(p => ids.includes(String(p.id)));

    if (favoritos.length === 0) {
      container.innerHTML = `
        <div class="bento-card" style="text-align: center; padding: 60px;">
          <div style="margin-bottom: 16px;">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
          </div>
          <h3 style="color: #1B4332;">Seus favoritos foram removidos</h3>
          <p style="color: #6B7280;">Os profissionais que você favoritou não estão mais disponíveis.</p>
          <button class="btn-primary" style="margin: 20px auto 0;" onclick="window.location.href='/buscar.html'">Buscar Profissionais</button>
        </div>`;
      return;
    }

    const grid = document.createElement('div');
    grid.style.cssText = 'display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px;';

    favoritos.forEach(p => {
      const nome = p.nome || 'Profissional';
      const inicial = nome.charAt(0).toUpperCase();
      const cardColor = p.corPerfil || '#2D6A4F';
      const zapLink = p.whatsapp ? `https://wa.me/55${p.whatsapp.replace(/\D/g, '')}` : null;

      const card = document.createElement('div');
      card.className = 'service-card';
      card.style.cursor = 'default';
      card.innerHTML = `
        <div style="height:5px; background:${cardColor}; border-radius:16px 16px 0 0; margin:-24px -24px 16px -24px;"></div>
        <div style="display:flex; align-items:center; gap:14px; margin-bottom:12px;">
          <div style="width:52px;height:52px;border-radius:50%;background:#D8F3DC;display:flex;align-items:center;justify-content:center;font-size:1.2rem;font-weight:bold;color:#1B4332;overflow:hidden;flex-shrink:0;border:2px solid #fff;box-shadow:0 2px 8px rgba(0,0,0,0.08);">
            ${p.fotoPerfil ? `<img src="${p.fotoPerfil}" alt="${nome}" style="width:100%;height:100%;object-fit:cover;">` : inicial}
          </div>
          <div>
            <h3 style="margin:0;color:#1B4332;font-size:1rem;">${nome}</h3>
            <span style="font-size:0.8rem;color:#40916C;">📍 ${p.estado || ''} — ${p.cidade || 'Local não informado'}</span>
          </div>
        </div>
        <p style="color:#6B7280;font-size:0.88rem;line-height:1.4;margin-bottom:14px;">${p.descricao ? p.descricao.substring(0, 100) + '...' : 'Sem descrição.'}</p>
        <div class="service-actions">
          ${zapLink
            ? `<a href="${zapLink}" target="_blank" class="btn-primary" style="font-size:0.8rem;padding:7px 14px;gap:6px;text-decoration:none;">
                <svg width="14" height="14" fill="currentColor" viewBox="0 0 24 24"><path d="M.057 24l1.687-6.163a11.867 11.867 0 01-1.587-5.946C.16 5.335 5.495 0 12.05 0a11.82 11.82 0 018.413 3.488A11.824 11.824 0 0123.94 11.9c-.003 6.556-5.338 11.892-11.893 11.892a11.9 11.9 0 01-5.688-1.448L.057 24z"/></svg>
                WhatsApp
              </a>`
            : ''}
          <button class="btn-outline" style="font-size:0.8rem;padding:7px 14px;color:#EF4444;border-color:#EF4444; display:flex; align-items:center; gap:6px;"
            onclick="desfavoritar(${JSON.stringify(p.id)}, this.closest('.service-card'))">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path></svg>
            Remover
          </button>
        </div>`;
      grid.appendChild(card);
    });

    container.innerHTML = '';
    container.appendChild(grid);

  } catch (err) {
    container.innerHTML = `<div class="bento-card" style="text-align:center;padding:40px;color:#6B7280;">Erro ao carregar favoritos. Tente novamente mais tarde.</div>`;
  }
}

function desfavoritar(id, cardEl) {
  const sid = String(id);
  const favs = getFavoritosIds().filter(x => x !== sid);
  localStorage.setItem('trabio_favoritos', JSON.stringify(favs));

  try {
    fetch(`/api/prestadores/${id}/favoritar`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    });
  } catch (e) {
    console.error('Erro ao desfavoritar no backend', e);
  }

  if (cardEl) {
    cardEl.style.transition = 'opacity 0.3s';
    cardEl.style.opacity = '0';
    setTimeout(() => { cardEl.remove(); carregarFavoritos(); }, 300);
  }
  if (typeof showToastInfo === 'function') showToastInfo('Profissional removido dos favoritos.');
}

// ── Notificações ──────────────────────────────────────────────
async function carregarNotificacoes() {
  const container = document.getElementById('notificacoes-container');
  if (!container) return;

  try {
    const res = await fetch('/api/notificacoes', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (res.ok) {
      const notificacoes = await res.json();
      if (notificacoes.length === 0) {
        container.innerHTML = `
          <div style="padding: 40px; text-align: center; color: #9CA3AF;">
            <svg width="48" height="48" fill="none" stroke="currentColor" stroke-width="1.5" style="margin: 0 auto 16px;"><path d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path></svg>
            <h3 style="color: #6B7280;">Tudo tranquilo!</h3>
            <p>Você não tem notificações no momento.</p>
          </div>`;
        return;
      }

      let html = '';
      notificacoes.forEach(n => {
        const date = new Date(n.createdAt).toLocaleString('pt-BR');
        const icon = n.tipo === 'LIKE' ? '❤️' : (n.tipo === 'VIEW' ? '👁️' : '🔔');
        const bgColor = n.lida ? '#fff' : '#F0FDF4';

        html += `
          <div style="display: flex; gap: 16px; padding: 20px; border-bottom: 1px solid #E5E7EB; background: ${bgColor}; align-items: flex-start;">
            <div style="font-size: 1.5rem; flex-shrink: 0;">${icon}</div>
            <div>
              <p style="margin: 0; color: #1B4332; font-weight: ${n.lida ? '500' : '700'}; font-size: 1rem;">${n.mensagem}</p>
              <span style="color: #6B7280; font-size: 0.8rem; margin-top: 4px; display: block;">${date}</span>
            </div>
            ${!n.lida ? '<div style="width: 8px; height: 8px; background: #25D366; border-radius: 50%; margin-top: 6px; margin-left: auto;"></div>' : ''}
          </div>`;
      });
      container.innerHTML = html;
    }
  } catch (err) {
    container.innerHTML = `<div style="padding: 20px; color: #EF4444;">Erro ao carregar notificações.</div>`;
  }
}

async function marcarNotificacoesLidas() {
  try {
    await fetch('/api/notificacoes/lidas', {
      method: 'PUT',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    carregarNotificacoes();
    if (typeof showToastSuccess === 'function') showToastSuccess('Notificações marcadas como lidas.');
  } catch (e) {
    if (typeof showToastError === 'function') showToastError('Erro ao atualizar notificações.');
  }
}

// ── Dados do Usuário Logado ───────────────────────────────────
async function carregarDadosUsuario() {
  try {
    const res = await fetch('/api/usuarios/me', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (res.ok) {
      const u = await res.json();
      document.getElementById('sidebar-name').textContent = u.nome;

      const roleBadge = document.getElementById('sidebar-role');
      if (u.role === 'PRESTADOR') {
        roleBadge.textContent = 'CONTA PRESTADOR';
        roleBadge.classList.remove('badge-cliente');
      } else {
        roleBadge.textContent = 'CONTA CLIENTE';
        roleBadge.classList.add('badge-cliente');
      }

      document.getElementById('dados-nome').value = u.nome;
      document.getElementById('dados-email').value = u.email;

      if (u.fotoPerfil) {
        const imgHtml = `<img src="${u.fotoPerfil}" alt="Avatar">`;
        document.getElementById('sidebar-avatar').innerHTML = imgHtml;
        document.getElementById('dados-avatar-preview').innerHTML = imgHtml;
        const topbarAv = document.getElementById('topbar-avatar');
        if (topbarAv) topbarAv.innerHTML = `<img src="${u.fotoPerfil}" alt="${u.nome}" style="width:100%;height:100%;object-fit:cover;">`;
      } else {
        const inicial = u.nome ? u.nome.charAt(0).toUpperCase() : 'U';
        document.getElementById('sidebar-avatar').textContent = inicial;
        document.getElementById('dados-avatar-preview').textContent = inicial;
        const topbarAv = document.getElementById('topbar-avatar');
        if (topbarAv) topbarAv.textContent = inicial;
      }

      if (u.role === 'PRESTADOR') {
        isPrestador = true;
        document.querySelectorAll('.pro-only').forEach(el => el.style.display = 'flex');
        carregarPerfilPrestador();
        if (!window.location.hash) showTab('dashboard');
      } else {
        document.querySelectorAll('.pro-only').forEach(el => el.style.display = 'none');
        if (!window.location.hash || window.location.hash === '#dashboard' || window.location.hash === '#servicos') {
          showTab('dados');
          window.location.hash = 'dados';
        }
      }
    } else if (res.status === 401) {
      logout();
    } else {
      if (typeof showToastError === 'function') showToastError("Erro ao carregar dados do usuário.");
    }
  } catch (err) {
    if (typeof showToastError === 'function') showToastError("Erro de conexão ao carregar dados do usuário.");
  }
}

// ── Perfil do Prestador (Portfólio) ───────────────────────────
async function carregarPerfilPrestador() {
  if (!isPrestador) return;
  try {
    const res = await fetch('/api/prestadores/me', { headers: { 'Authorization': `Bearer ${token}` } });
    if (res.ok) {
      const meuPerfil = await res.json();
      if (meuPerfil) {
        window.meuPerfilSalvo = meuPerfil;

        const dashRating = document.getElementById('dash-rating');
        if (dashRating) dashRating.textContent = meuPerfil.mediaAvaliacoes > 0 ? meuPerfil.mediaAvaliacoes.toFixed(1) : 'Novo';

        if (meuPerfil.descricao) document.getElementById('port-sobre').value = meuPerfil.descricao;
        if (meuPerfil.estado) document.getElementById('port-estado').value = meuPerfil.estado;
        if (meuPerfil.cidade) document.getElementById('port-cidade').value = meuPerfil.cidade;
        if (meuPerfil.enderecoCompleto) document.getElementById('port-endereco').value = meuPerfil.enderecoCompleto;
        if (meuPerfil.whatsapp) document.getElementById('port-whatsapp').value = meuPerfil.whatsapp;
        if (meuPerfil.emailContato) document.getElementById('port-email').value = meuPerfil.emailContato;

        document.getElementById('port-visivel').checked = meuPerfil.visivel !== false;
        if (meuPerfil.corPerfil && typeof aplicarCor === 'function') aplicarCor(meuPerfil.corPerfil);

        // Carrega serviços existentes
        if (meuPerfil.servicos && meuPerfil.servicos.length > 0) {
          servicesContainer.innerHTML = '';
          meuPerfil.servicos.forEach(s => {
            const row = document.createElement('div');
            row.className = 'dynamic-row';
            row.style = 'background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 12px; padding: 16px; display: flex; gap: 16px; align-items: center; margin-top: 10px;';
            row.innerHTML = `
              <div class="form-group" style="margin: 0; flex: 1;">
                <label style="font-size: 0.85rem;">Nome do Serviço *</label>
                <input type="text" class="serv-titulo-input" required value="${s.titulo}" />
              </div>
              <div class="form-group" style="margin: 0; flex: 2;">
                <label style="font-size: 0.85rem;">Breve Descrição *</label>
                <input type="text" class="serv-desc-input" required value="${s.descricao || ''}" />
              </div>
              <button type="button" class="remove-btn" style="width: 40px; height: 40px; background: #FEF2F2; color: #EF4444; border: 1px solid #FCA5A5; border-radius: 8px; cursor: pointer; align-self: end;">X</button>
            `;
            servicesContainer.appendChild(row);
          });
        }

        // Carrega tabela de preços
        if (meuPerfil.pacotesPrecos && meuPerfil.pacotesPrecos.length > 0) {
          pricingContainer.innerHTML = '';
          meuPerfil.pacotesPrecos.forEach(p => {
            const row = document.createElement('div');
            row.className = 'dynamic-row';
            row.style = 'background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 12px; padding: 16px; display: grid; grid-template-columns: 3fr 3fr 3fr 2fr 40px; gap: 16px; align-items: end; margin-top: 10px;';
            row.innerHTML = `
              <div class="form-group" style="margin: 0;">
                <label style="font-size: 0.85rem;">Item / Pacote *</label>
                <input type="text" class="preco-item-input" required value="${p.pacote}" />
              </div>
              <div class="form-group" style="margin: 0;">
                <label style="font-size: 0.85rem;">Preço / Valor *</label>
                <input type="text" class="preco-valor-input" required value="${p.preco}" />
              </div>
              <div class="form-group" style="margin: 0;">
                <label style="font-size: 0.85rem;">Dias</label>
                <input type="text" class="preco-dias-input" value="${p.dias || ''}" />
              </div>
              <div class="form-group" style="margin: 0;">
                <label style="font-size: 0.85rem;">Horário</label>
                <input type="text" class="preco-hora-input" value="${p.horario || ''}" />
              </div>
              <button type="button" class="remove-btn" style="width: 40px; height: 40px; background: #FEF2F2; color: #EF4444; border: 1px solid #FCA5A5; border-radius: 8px; cursor: pointer;">X</button>
            `;
            pricingContainer.appendChild(row);
          });
        }

        // Carrega galeria
        if (meuPerfil.galeria && meuPerfil.galeria.length > 0) {
          galleryContainer.innerHTML = '';
          meuPerfil.galeria.forEach(url => {
            const row = document.createElement('div');
            row.className = 'dynamic-row';
            row.style = 'background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 12px; padding: 12px; display: flex; gap: 12px; align-items: center; margin-top: 10px;';
            row.innerHTML = `
              <div class="form-group" style="margin: 0; flex: 1;">
                <input type="url" class="galeria-url-input" value="${url}" />
              </div>
              <div class="img-preview" style="width: 40px; height: 40px; background: #E5E7EB; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #9CA3AF; overflow: hidden; background-size: cover; background-position: center; background-image: url('${url}');">
              </div>
              <label class="upload-gallery-btn" title="Enviar arquivo do computador" style="width: 32px; height: 32px; flex-shrink: 0; background: #E8F5E9; color: #2D6A4F; border: 1px solid #52B788; border-radius: 8px; cursor: pointer; display:flex; align-items:center; justify-content:center; margin:0;">
                <input type="file" style="display:none;" accept="image/*" onchange="uploadGaleriaFoto(event, this)">
                <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
              </label>
              <button type="button" class="remove-btn" style="width: 32px; height: 32px; background: #FEF2F2; color: #EF4444; border: 1px solid #FCA5A5; border-radius: 8px; cursor: pointer; display:flex; align-items:center; justify-content:center;">X</button>
            `;
            galleryContainer.appendChild(row);
            bindImagePreview(row.querySelector('.galeria-url-input'), row.querySelector('.img-preview'));
          });
        }
      }
    }
  } catch (e) {
    console.error("Erro ao carregar perfil de prestador", e);
  }
}

// ── Linhas Dinâmicas de Serviços, Preços e Galeria ─────────────
const servicesContainer = document.getElementById('services-container');
const addServiceBtn = document.getElementById('add-service-btn');
if (addServiceBtn) {
  addServiceBtn.addEventListener('click', () => {
    const row = document.createElement('div');
    row.className = 'dynamic-row';
    row.style = 'background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 12px; padding: 16px; display: flex; gap: 16px; align-items: center; margin-top: 10px;';
    row.innerHTML = `
      <div class="form-group" style="margin: 0; flex: 1;">
        <label style="font-size: 0.85rem;">Nome do Serviço *</label>
        <input type="text" class="serv-titulo-input" required placeholder="Novo Serviço" />
      </div>
      <div class="form-group" style="margin: 0; flex: 2;">
        <label style="font-size: 0.85rem;">Breve Descrição *</label>
        <input type="text" class="serv-desc-input" required placeholder="Descrição detalhada..." />
      </div>
      <button type="button" class="remove-btn" style="width: 40px; height: 40px; background: #FEF2F2; color: #EF4444; border: 1px solid #FCA5A5; border-radius: 8px; cursor: pointer; align-self: end;">X</button>
    `;
    servicesContainer.appendChild(row);
  });
}

const pricingContainer = document.getElementById('pricing-container');
const addPriceBtn = document.getElementById('add-price-btn');
if (addPriceBtn) {
  addPriceBtn.addEventListener('click', () => {
    const row = document.createElement('div');
    row.className = 'dynamic-row';
    row.style = 'background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 12px; padding: 16px; display: grid; grid-template-columns: 3fr 3fr 3fr 2fr 40px; gap: 16px; align-items: end; margin-top: 10px;';
    row.innerHTML = `
      <div class="form-group" style="margin: 0;">
        <label style="font-size: 0.85rem;">Item / Pacote *</label>
        <input type="text" class="preco-item-input" required placeholder="Novo Item" />
      </div>
      <div class="form-group" style="margin: 0;">
        <label style="font-size: 0.85rem;">Preço / Valor *</label>
        <input type="text" class="preco-valor-input" required placeholder="R$ 0,00" />
      </div>
      <div class="form-group" style="margin: 0;">
        <label style="font-size: 0.85rem;">Dias</label>
        <input type="text" class="preco-dias-input" placeholder="Seg a Sex" />
      </div>
      <div class="form-group" style="margin: 0;">
        <label style="font-size: 0.85rem;">Horário</label>
        <input type="text" class="preco-hora-input" placeholder="00:00 - 00:00" />
      </div>
      <button type="button" class="remove-btn" style="width: 40px; height: 40px; background: #FEF2F2; color: #EF4444; border: 1px solid #FCA5A5; border-radius: 8px; cursor: pointer;">X</button>
    `;
    pricingContainer.appendChild(row);
  });
}

const galleryContainer = document.getElementById('gallery-container');
const addGalleryBtn = document.getElementById('add-gallery-btn');

function bindImagePreview(input, previewDiv) {
  if (!input || !previewDiv) return;
  input.addEventListener('input', (e) => {
    const url = e.target.value;
    if (url.match(/\.(jpeg|jpg|gif|png|webp)$/i) || url.startsWith('http') || url.startsWith('/api/arquivos/')) {
      previewDiv.innerHTML = '';
      previewDiv.style.backgroundImage = `url('${url}')`;
    } else {
      previewDiv.style.backgroundImage = 'none';
      previewDiv.innerHTML = '<svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>';
    }
  });
}

const initialGalleryInput = document.querySelector('.galeria-url-input');
const initialGalleryPreview = document.querySelector('.img-preview');
if (initialGalleryInput && initialGalleryPreview) bindImagePreview(initialGalleryInput, initialGalleryPreview);

const initialPickBtn = document.querySelector('.pick-gallery-btn');
if (initialPickBtn) bindPickBtn(initialPickBtn);

// ── Modal Seletor de Fotos ────────────────────────────────────
let currentPickTarget = null;
const pickerModal = document.createElement('div');
pickerModal.id = 'photo-picker-modal';
pickerModal.style = 'display:none; position:fixed; inset:0; background:rgba(0,0,0,0.6); z-index:9999; align-items:center; justify-content:center;';
pickerModal.innerHTML = `
  <div style="background:#fff; border-radius:20px; padding:28px; max-width:540px; width:90%; max-height:80vh; overflow-y:auto;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
      <h3 style="margin:0; color:#1B4332;">Escolher foto da galeria</h3>
      <button id="picker-close" style="background:none; border:none; font-size:1.4rem; cursor:pointer; color:#6B7280;">&times;</button>
    </div>
    <p style="color:#6B7280; font-size:0.9rem; margin-bottom:16px;">Selecione uma das fotos já salvas no seu portfólio:</p>
    <div id="picker-grid" style="display:grid; grid-template-columns:repeat(3, 1fr); gap:12px;"></div>
    <p id="picker-empty" style="color:#9CA3AF; text-align:center; display:none;">Nenhuma foto salva ainda. Adicione fotos ao portfólio primeiro.</p>
  </div>
`;
document.body.appendChild(pickerModal);

document.getElementById('picker-close').addEventListener('click', () => pickerModal.style.display = 'none');
pickerModal.addEventListener('click', (e) => { if (e.target === pickerModal) pickerModal.style.display = 'none'; });

function openPhotoPicker(inputTarget) {
  currentPickTarget = inputTarget;
  const pickerGrid = document.getElementById('picker-grid');
  const pickerEmpty = document.getElementById('picker-empty');
  pickerGrid.innerHTML = '';

  const urls = [];
  document.querySelectorAll('#gallery-container .galeria-url-input').forEach(i => { if (i.value) urls.push(i.value); });
  if (window.meuPerfilSalvo && window.meuPerfilSalvo.galeria) {
    window.meuPerfilSalvo.galeria.forEach(u => { if (!urls.includes(u)) urls.push(u); });
  }

  if (urls.length === 0) {
    pickerEmpty.style.display = 'block';
  } else {
    pickerEmpty.style.display = 'none';
    urls.forEach(url => {
      const thumb = document.createElement('div');
      thumb.style = `width:100%; padding-bottom:100%; border-radius:12px; cursor:pointer; background:url('${url}') center/cover; border:3px solid transparent; transition:border 0.2s;`;
      thumb.title = url;
      thumb.addEventListener('mouseenter', () => thumb.style.borderColor = '#52B788');
      thumb.addEventListener('mouseleave', () => thumb.style.borderColor = 'transparent');
      thumb.addEventListener('click', () => {
        currentPickTarget.value = url;
        currentPickTarget.dispatchEvent(new Event('input'));
        pickerModal.style.display = 'none';
      });
      pickerGrid.appendChild(thumb);
    });
  }
  pickerModal.style.display = 'flex';
}

function bindPickBtn(btn) {
  if (!btn) return;
  btn.addEventListener('click', () => {
    const row = btn.closest('.dynamic-row');
    openPhotoPicker(row.querySelector('.galeria-url-input'));
  });
}

if (addGalleryBtn) {
  addGalleryBtn.addEventListener('click', () => {
    const row = document.createElement('div');
    row.className = 'dynamic-row';
    row.style = 'background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 12px; padding: 12px; display: flex; gap: 12px; align-items: center; margin-top: 10px;';
    row.innerHTML = `
      <div class="form-group" style="margin: 0; flex: 1;">
        <input type="url" class="galeria-url-input" placeholder="https://exemplo.com/imagem.jpg" />
      </div>
      <button type="button" class="pick-gallery-btn" title="Escolher da galeria" style="width: 36px; height: 36px; flex-shrink: 0; background: #E9F5EE; color: #2D6A4F; border: 1px solid #52B788; border-radius: 8px; cursor: pointer; display:flex; align-items:center; justify-content:center;">
        <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
      </button>
      <label class="upload-gallery-btn" title="Enviar arquivo do computador" style="width: 36px; height: 36px; flex-shrink: 0; background: #E8F5E9; color: #2D6A4F; border: 1px solid #52B788; border-radius: 8px; cursor: pointer; display:flex; align-items:center; justify-content:center; margin:0;">
        <input type="file" style="display:none;" accept="image/*" onchange="uploadGaleriaFoto(event, this)">
        <svg width="18" height="18" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
      </label>
      <div class="img-preview" style="width: 40px; height: 40px; background: #E5E7EB; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #9CA3AF; overflow: hidden; background-size: cover; background-position: center;">
        <svg width="20" height="20" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
      </div>
      <button type="button" class="remove-btn" style="width: 32px; height: 32px; background: #FEF2F2; color: #EF4444; border: 1px solid #FCA5A5; border-radius: 8px; cursor: pointer; display:flex; align-items:center; justify-content:center;">X</button>
    `;
    galleryContainer.appendChild(row);
    bindImagePreview(row.querySelector('.galeria-url-input'), row.querySelector('.img-preview'));
    bindPickBtn(row.querySelector('.pick-gallery-btn'));
  });
}

document.addEventListener('click', (e) => {
  if (e.target.closest('.remove-btn')) {
    const btn = e.target.closest('.remove-btn');
    const row = btn.closest('.dynamic-row');
    if (row) row.remove();
  }
});

// ── Salvar Portfólio ──────────────────────────────────────────
const portfolioForm = document.getElementById('portfolio-form');
if (portfolioForm) {
  portfolioForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const servicos = [];
    document.querySelectorAll('#services-container .dynamic-row').forEach(row => {
      const t = row.querySelector('.serv-titulo-input').value;
      const d = row.querySelector('.serv-desc-input').value;
      if (t) servicos.push({ titulo: t, descricao: d });
    });

    const pacotes = [];
    document.querySelectorAll('#pricing-container .dynamic-row').forEach(row => {
      const item = row.querySelector('.preco-item-input').value;
      const val = row.querySelector('.preco-valor-input').value;
      const dias = row.querySelector('.preco-dias-input').value;
      const hora = row.querySelector('.preco-hora-input').value;
      if (item && val) pacotes.push({ pacote: item, preco: val, dias: dias, horario: hora });
    });

    const galeria = [];
    document.querySelectorAll('#gallery-container .galeria-url-input').forEach(input => {
      if (input.value) galeria.push(input.value);
    });

    const payload = {
      descricao: document.getElementById('port-sobre').value,
      estado: document.getElementById('port-estado').value,
      cidade: document.getElementById('port-cidade').value,
      enderecoCompleto: document.getElementById('port-endereco').value,
      whatsapp: document.getElementById('port-whatsapp').value,
      emailContato: document.getElementById('port-email').value,
      servicos: servicos,
      pacotes: pacotes,
      galeria: galeria,
      visivel: document.getElementById('port-visivel').checked,
      corPerfil: document.getElementById('port-cor').value
    };

    try {
      const res = await fetch('/api/prestadores/me/portfolio', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        if (typeof showToastSuccess === 'function') showToastSuccess("Portfólio salvo com sucesso!");
        carregarPerfilPrestador();
      } else {
        if (typeof showToastError === 'function') showToastError("Erro ao salvar portfólio.");
      }
    } catch (err) {
      if (typeof showToastError === 'function') showToastError("Erro de conexão ao salvar portfólio.");
    }
  });
}

const btnLimparPortfolio = document.getElementById('btn-limpar-portfolio');
if (btnLimparPortfolio) {
  btnLimparPortfolio.addEventListener('click', () => {
    if (confirm('Tem certeza que deseja limpar todos os campos do formulário? (Lembre-se de salvar para aplicar no perfil)')) {
      document.getElementById('port-sobre').value = '';
      document.getElementById('port-estado').value = '';
      document.getElementById('port-cidade').value = '';
      document.getElementById('port-endereco').value = '';
      document.getElementById('port-whatsapp').value = '';
      document.getElementById('port-email').value = '';
      document.getElementById('port-visivel').checked = false;

      if (servicesContainer) servicesContainer.innerHTML = '';
      if (pricingContainer) pricingContainer.innerHTML = '';
      if (galleryContainer) galleryContainer.innerHTML = '';
    }
  });
}

const btnSalvarPortfolioTop = document.getElementById('btn-salvar-portfolio-top');
if (btnSalvarPortfolioTop && portfolioForm) {
  btnSalvarPortfolioTop.addEventListener('click', () => {
    portfolioForm.requestSubmit();
  });
}

// ── Color Picker ──────────────────────────────────────────────
function aplicarCor(cor) {
  const portCor = document.getElementById('port-cor');
  const corPreview = document.getElementById('cor-preview');
  if (portCor) portCor.value = cor;
  if (corPreview) corPreview.style.background = `linear-gradient(135deg, ${cor}, ${darkenColor(cor, 20)})`;

  document.querySelectorAll('.cor-preset').forEach(b => {
    b.style.borderColor = b.dataset.cor === cor ? '#fff' : 'transparent';
    b.style.outline = b.dataset.cor === cor ? `3px solid ${cor}` : 'none';
  });
}

function darkenColor(hex, amount) {
  if (!hex || hex.length < 7) return '#1B4332';
  let r = parseInt(hex.slice(1, 3), 16);
  let g = parseInt(hex.slice(3, 5), 16);
  let b = parseInt(hex.slice(5, 7), 16);
  r = Math.max(0, r - amount);
  g = Math.max(0, g - amount);
  b = Math.max(0, b - amount);
  return '#' + [r, g, b].map(v => v.toString(16).padStart(2, '0')).join('');
}

document.querySelectorAll('.cor-preset').forEach(btn => {
  btn.addEventListener('click', () => aplicarCor(btn.dataset.cor));
});

const portCorInput = document.getElementById('port-cor');
if (portCorInput) {
  portCorInput.addEventListener('input', (e) => aplicarCor(e.target.value));
}
aplicarCor('#2D6A4F');

// ── Upload de Avatar e Fotos ──────────────────────────────────
async function uploadAvatar(e) {
  const file = e.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  try {
    const res = await fetch('/api/usuarios/perfil/foto', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData
    });
    if (res.ok) {
      if (typeof showToastSuccess === 'function') showToastSuccess('Foto de perfil atualizada!');
      carregarDadosUsuario();
    } else {
      if (typeof showToastError === 'function') showToastError('Erro ao enviar foto.');
    }
  } catch (err) {
    if (typeof showToastError === 'function') showToastError('Erro de conexão ao enviar foto.');
  }
}

async function uploadGaleriaFoto(e, element) {
  const file = e.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);

  if (typeof showToastInfo === 'function') showToastInfo('Enviando imagem...');

  try {
    const res = await fetch('/api/arquivos/upload', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` },
      body: formData
    });
    if (res.ok) {
      const url = await res.text();
      const row = element.closest('.dynamic-row');
      const inputUrl = row.querySelector('.galeria-url-input');
      inputUrl.value = url;
      inputUrl.dispatchEvent(new Event('input'));
      if (typeof showToastSuccess === 'function') showToastSuccess('Imagem enviada e link preenchido!');
    } else {
      if (typeof showToastError === 'function') showToastError('Erro ao enviar imagem para a galeria.');
    }
  } catch (err) {
    if (typeof showToastError === 'function') showToastError('Erro de conexão ao enviar imagem.');
  }
}

// ── Logout ────────────────────────────────────────────────────
async function logout() {
  const token = localStorage.getItem('trabio_token');
  if (token) {
    try {
      await fetch('/api/auth/logout', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
    } catch (_) { /* falha silenciosa */ }
  }
  localStorage.removeItem('trabio_token');
  localStorage.removeItem('trabio_role');
  window.location.href = '/login.html';
}
