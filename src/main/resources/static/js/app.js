document.addEventListener('DOMContentLoaded', () => {
  const html = document.documentElement;

  const botaoTema = document.querySelector('[data-tema]');
  const rotularTema = () => {
    if (!botaoTema) return;
    const escuro = html.getAttribute('data-bs-theme') === 'dark';
    botaoTema.innerHTML = escuro
      ? '<i class="bi bi-sun"></i> Tema claro'
      : '<i class="bi bi-moon-stars"></i> Tema escuro';
  };
  rotularTema();
  if (botaoTema) botaoTema.addEventListener('click', () => {
    const novo = html.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
    html.setAttribute('data-bs-theme', novo);
    try { localStorage.setItem('tema', novo); } catch (e) {}
    rotularTema();
  });

  document.querySelectorAll('[data-filtrar]').forEach(campo => {
    const alvo = document.querySelector(campo.dataset.filtrar) || document.querySelector('.tabela table');
    if (!alvo) return;
    campo.addEventListener('input', () => {
      const q = campo.value.toLowerCase();
      alvo.querySelectorAll('tbody tr').forEach(tr => {
        tr.hidden = !tr.textContent.toLowerCase().includes(q);
      });
    });
  });

  document.querySelectorAll('[data-contador]').forEach(area => {
    const saida = document.querySelector(area.dataset.contador);
    const atualizar = () => saida.textContent = area.value.length + '/' + area.maxLength;
    area.addEventListener('input', atualizar);
    atualizar();
  });

  document.querySelectorAll('.toasts .alert').forEach(a => {
    setTimeout(() => { a.classList.add('saindo'); setTimeout(() => a.remove(), 300); },
               a.classList.contains('alert-success') ? 5000 : 8000);
  });

  const dlg = document.createElement('dialog');
  dlg.className = 'confirmar';
  dlg.innerHTML = '<h3>Confirmar ação</h3><p id="dlgMsg"></p>'
    + '<div class="acoes"><button type="button" class="btn btn-light" data-nao>Cancelar</button>'
    + '<button type="button" class="btn btn-danger" data-sim>Confirmar</button></div>';
  document.body.appendChild(dlg);
  let pendente = null;

  document.querySelectorAll('form[onsubmit*="confirm("]').forEach(f => {
    const m = f.getAttribute('onsubmit').match(/confirm\('([^']*)'\)/);
    if (m) { f.dataset.confirm = m[1]; f.removeAttribute('onsubmit'); }
  });
  document.querySelectorAll('form[data-confirm]').forEach(f => {
    f.addEventListener('submit', e => {
      e.preventDefault();
      pendente = f;
      dlg.querySelector('#dlgMsg').textContent = f.dataset.confirm;
      dlg.showModal();
    });
  });
  dlg.querySelector('[data-nao]').addEventListener('click', () => dlg.close());
  dlg.querySelector('[data-sim]').addEventListener('click', () => { dlg.close(); if (pendente) pendente.submit(); });
});
