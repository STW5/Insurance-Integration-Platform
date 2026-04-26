const state = {
  historyPage: 0,
  historySize: 20,
  historyTotalPages: 0,
};

const el = (id) => document.getElementById(id);

async function api(path, options = {}) {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });

  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`;
    try {
      const body = await res.json();
      if (body?.error) message = body.error;
    } catch (_) {}
    throw new Error(message);
  }

  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

function toIsoLocal(datetimeLocalValue) {
  if (!datetimeLocalValue) return null;
  return new Date(datetimeLocalValue).toISOString().slice(0, 19);
}

function fmtDate(value) {
  if (!value) return '-';
  return new Date(value).toLocaleString('ko-KR');
}

function statusTag(status) {
  if (status === 'SUCCESS' || status === 'NORMAL') return `<span class="tag tag-success">${status}</span>`;
  if (status === 'FAILED') return `<span class="tag tag-failed">${status}</span>`;
  return `<span class="tag tag-stopped">${status}</span>`;
}

function setStatus(msg) {
  el('statusMessage').textContent = msg;
}

async function loadDashboard() {
  const period = el('dashboardPeriod').value;
  const from = toIsoLocal(el('dashboardFrom').value);
  const to = toIsoLocal(el('dashboardTo').value);

  const qs = new URLSearchParams({ period });
  if (period === 'CUSTOM') {
    if (from) qs.set('from', from);
    if (to) qs.set('to', to);
  }

  const data = await api(`/api/dashboard?${qs.toString()}`);

  el('summaryCards').innerHTML = `
    <div class="card"><div class="label">총 실행</div><div class="value">${data.totalExecutions}</div></div>
    <div class="card"><div class="label">성공</div><div class="value" style="color:#16a34a">${data.successCount}</div></div>
    <div class="card"><div class="label">실패</div><div class="value" style="color:#dc2626">${data.failureCount}</div></div>
    <div class="card"><div class="label">장애 인터페이스</div><div class="value">${data.failedInterfaceCount}</div></div>
  `;

  const protocolRows = Object.entries(data.protocolStatus || {}).map(([protocol, stat]) => `
    <tr>
      <td>${protocol}</td>
      <td>${stat.total}</td>
      <td>${stat.success}</td>
      <td>${stat.failed}</td>
    </tr>
  `).join('');
  el('protocolTableBody').innerHTML = protocolRows || '<tr><td colspan="4">데이터 없음</td></tr>';

  const failureRows = (data.recentFailures || []).map((h) => `
    <tr>
      <td>${h.historyId}</td>
      <td>${h.interfaceCode}</td>
      <td>${fmtDate(h.startedAt)}</td>
      <td>${h.errorMessage || '-'}</td>
    </tr>
  `).join('');
  el('recentFailureBody').innerHTML = failureRows || '<tr><td colspan="4">최근 실패 없음</td></tr>';
}

async function loadInterfaces() {
  const qs = new URLSearchParams();
  const protocolType = el('filterProtocol').value;
  const targetInstitution = el('filterInstitution').value.trim();
  const healthStatus = el('filterHealth').value;
  const active = el('filterActive').value;

  if (protocolType) qs.set('protocolType', protocolType);
  if (targetInstitution) qs.set('targetInstitution', targetInstitution);
  if (healthStatus) qs.set('healthStatus', healthStatus);
  if (active) qs.set('active', active);

  const data = await api(`/api/interfaces?${qs.toString()}`);
  const rows = (data || []).map((item) => `
    <tr>
      <td>${item.interfaceCode}</td>
      <td>${item.interfaceName}</td>
      <td>${item.targetInstitution}</td>
      <td>${item.protocolType}</td>
      <td>${statusTag(item.healthStatus)}</td>
      <td>${item.active ? 'Y' : 'N'}</td>
      <td>${fmtDate(item.lastExecutedAt)}</td>
      <td>
        <button onclick="executeInterface('${item.interfaceCode}', false)">실행</button>
        <button onclick="executeInterface('${item.interfaceCode}', true)">테스트</button>
      </td>
    </tr>
  `).join('');

  el('interfaceTableBody').innerHTML = rows || '<tr><td colspan="8">조회 결과 없음</td></tr>';
}

async function loadHistories() {
  const qs = new URLSearchParams();
  const interfaceCode = el('historyInterfaceCode').value.trim();
  const executionStatus = el('historyStatus').value;
  const triggerType = el('historyTriggerType').value;
  const failuresOnly = el('historyFailuresOnly').checked;

  if (interfaceCode) qs.set('interfaceCode', interfaceCode);
  if (executionStatus) qs.set('executionStatus', executionStatus);
  if (triggerType) qs.set('triggerType', triggerType);
  if (failuresOnly) qs.set('failuresOnly', 'true');

  qs.set('page', String(state.historyPage));
  qs.set('size', String(state.historySize));
  qs.set('sortBy', 'startedAt');
  qs.set('direction', 'DESC');

  const data = await api(`/api/executions/histories?${qs.toString()}`);
  state.historyTotalPages = data.totalPages;

  el('paginationText').textContent = `${data.page + 1} / ${Math.max(data.totalPages, 1)}`;

  const rows = (data.content || []).map((h) => `
    <tr>
      <td>${h.historyId}</td>
      <td>${h.interfaceCode}</td>
      <td>${h.triggerType}</td>
      <td>${fmtDate(h.startedAt)}</td>
      <td>${fmtDate(h.endedAt)}</td>
      <td>${statusTag(h.executionStatus)}</td>
      <td>${h.attemptCount}</td>
      <td>${h.errorMessage || '-'}</td>
      <td>
        ${h.executionStatus === 'FAILED' ? `<button class="btn-danger" onclick="reprocessHistory(${h.historyId})">재처리</button>` : '-'}
      </td>
    </tr>
  `).join('');

  el('historyTableBody').innerHTML = rows || '<tr><td colspan="9">조회 결과 없음</td></tr>';
}

async function executeInterface(interfaceCode, testExecution) {
  const requestSummary = prompt('요청 요약(선택)', testExecution ? 'UI 테스트 실행' : 'UI 수동 실행') || '';
  try {
    await api(`/api/executions/interfaces/${encodeURIComponent(interfaceCode)}`, {
      method: 'POST',
      body: JSON.stringify({ testExecution, requestSummary }),
    });
    setStatus(`[성공] ${interfaceCode} 실행 완료`);
    await Promise.all([loadInterfaces(), loadHistories(), loadDashboard()]);
  } catch (e) {
    alert(`실행 실패: ${e.message}`);
  }
}

async function reprocessHistory(historyId) {
  const requestSummary = prompt('재처리 요청 요약(선택)', `UI 재처리 historyId=${historyId}`) || '';
  try {
    await api(`/api/executions/histories/${historyId}/reprocess`, {
      method: 'POST',
      body: JSON.stringify({ requestSummary }),
    });
    setStatus(`[성공] historyId=${historyId} 재처리 완료`);
    await Promise.all([loadInterfaces(), loadHistories(), loadDashboard()]);
  } catch (e) {
    alert(`재처리 실패: ${e.message}`);
  }
}

function bindEvents() {
  el('reloadDashboardBtn').addEventListener('click', () => loadDashboard().catch(handleError));
  el('reloadInterfacesBtn').addEventListener('click', () => loadInterfaces().catch(handleError));
  el('reloadHistoriesBtn').addEventListener('click', async () => {
    state.historyPage = 0;
    await loadHistories().catch(handleError);
  });

  el('prevPageBtn').addEventListener('click', async () => {
    if (state.historyPage <= 0) return;
    state.historyPage -= 1;
    await loadHistories().catch(handleError);
  });

  el('nextPageBtn').addEventListener('click', async () => {
    if (state.historyPage + 1 >= state.historyTotalPages) return;
    state.historyPage += 1;
    await loadHistories().catch(handleError);
  });

  el('dashboardPeriod').addEventListener('change', () => {
    const custom = el('dashboardPeriod').value === 'CUSTOM';
    el('dashboardFrom').disabled = !custom;
    el('dashboardTo').disabled = !custom;
  });
}

function handleError(error) {
  console.error(error);
  setStatus(`[오류] ${error.message}`);
}

async function init() {
  bindEvents();
  el('dashboardPeriod').dispatchEvent(new Event('change'));
  setStatus('데이터 조회 중...');

  await Promise.all([
    loadDashboard(),
    loadInterfaces(),
    loadHistories(),
  ]);

  setStatus('연결 완료');
}

window.executeInterface = executeInterface;
window.reprocessHistory = reprocessHistory;

init().catch(handleError);
