<template>
  <div class="flex h-screen overflow-hidden bg-gray-50 dark:bg-gray-900">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />
    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <Header :sidebarOpen="sidebarOpen" @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <main class="page">
        <!-- ── 페이지 헤더 ── -->
        <div class="page-header">
          <div class="page-header-left">
            <!-- 뒤로가기: projectId를 params로 넘겨서 TimeReport가 올바른 프로젝트로 init -->
            <button class="btn-back" @click="goBack">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M19 12H5M11 6l-6 6 6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              타임 리포트
            </button>
            <span class="breadcrumb-sep">/</span>
            <div>
              <h1 class="page-title">타임 엔트리</h1>
              <p class="page-subtitle">{{ taskInfo?.title ?? '' }} · 업무별 시간 기록 현황</p>
            </div>
          </div>
          <button class="btn-add" @click="showAddModal = true">
            <svg width="13" height="13" viewBox="0 0 14 14" fill="none">
              <path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
            </svg>
            엔트리 추가
          </button>
        </div>

        <!-- ── ① 프로젝트 헤더 바 ── -->
        <div class="proj-header-bar">
          <div class="proj-info">
            <span class="proj-label">프로젝트 명</span>
            <div class="proj-chips">
              <button
                v-for="p in projectOptions"
                :key="p.value"
                class="proj-chip"
                :class="{ active: selectedProject === p.value }"
                @click="selectProject(p.value)"
              >{{ p.label }}</button>
            </div>
          </div>
          <div class="period-wrap">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" style="color:#64748b">
              <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2" />
              <path d="M16 2v4M8 2v4M3 10h18" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
            </svg>
            <span class="period-text">{{ periodLabel }}</span>
          </div>
        </div>

        <!-- ── 메인 대시보드 그리드 ── -->
        <!-- 차트 전체: 선택된 프로젝트 칩 기준 (전체 or 특정 하위) -->
        <div class="dashboard-grid">
          <div class="charts-left">
            <div class="chart-row">
              <div class="chart-card">
                <div class="chart-card-header">
                  <span class="chart-card-title">소요시간 프로젝트</span>
                  <span class="chart-unit">US (Hour) · {{ selectedProjectLabel }}</span>
                </div>
                <div class="chart-canvas-wrap"><canvas ref="barChart1"></canvas></div>
                <div class="chart-legend">
                  <span v-for="item in barLegend" :key="item.label" class="legend-item">
                    <span class="legend-sq" :style="{ background: item.color }"></span>
                    {{ item.label }}
                  </span>
                </div>
              </div>

              <div class="chart-card">
                <div class="chart-card-header">
                  <span class="chart-card-title">담당자별 투입 현황</span>
                  <span class="chart-unit">US (Hour) · {{ selectedProjectLabel }}</span>
                </div>
                <div class="chart-canvas-wrap"><canvas ref="hbarChart1"></canvas></div>
                <div class="hbar-footer">
                  <span class="hbar-count">담당자 합계 = {{ filteredUniqueAssignees }}명</span>
                  <div class="mini-pager">
                    <button class="mpg" @click="hPage1 = Math.max(1, hPage1 - 1)">‹</button>
                    <button v-for="p in hTotalPages1" :key="p" class="mpg" :class="{ on: p === hPage1 }" @click="hPage1 = p">{{ p }}</button>
                    <button class="mpg" @click="hPage1 = Math.min(hTotalPages1, hPage1 + 1)">›</button>
                  </div>
                </div>
              </div>
            </div>

            <div class="chart-row">
              <div class="chart-card">
                <div class="chart-card-header">
                  <span class="chart-card-title">날짜별 소요시간 추이</span>
                  <span class="chart-unit">US (Hour) · {{ selectedProjectLabel }}</span>
                </div>
                <div class="chart-canvas-wrap"><canvas ref="lineChart"></canvas></div>
                <div class="chart-legend">
                  <span class="legend-item">
                    <span class="legend-sq" style="background:#3b82f6;border-radius:50%"></span>
                    일별 투입시간
                  </span>
                </div>
              </div>

              <div class="chart-card">
                <div class="chart-card-header">
                  <span class="chart-card-title">업무명별 소요시간</span>
                  <span class="chart-unit">US (Hour) · {{ selectedProjectLabel }}</span>
                </div>
                <div class="chart-canvas-wrap"><canvas ref="hbarChart2"></canvas></div>
                <div class="hbar-footer">
                  <span class="hbar-count">업무 합계 = {{ chartEntries.length }}건</span>
                  <div class="mini-pager">
                    <button class="mpg" @click="hPage2 = Math.max(1, hPage2 - 1)">‹</button>
                    <button v-for="p in hTotalPages2" :key="p" class="mpg" :class="{ on: p === hPage2 }" @click="hPage2 = p">{{ p }}</button>
                    <button class="mpg" @click="hPage2 = Math.min(hTotalPages2, hPage2 + 1)">›</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 오른쪽 도넛+통계: 선택된 프로젝트 칩 기준 -->
          <div class="charts-right">
            <div class="chart-card donut-card">
              <div class="chart-card-header" style="justify-content:center;flex-direction:column;align-items:center;gap:2px">
                <span class="chart-card-title">업무 유형별 비중</span>
                <span style="font-size:11px;color:#94a3b8">{{ selectedProjectLabel }}</span>
              </div>
              <div class="donut-canvas-wrap">
                <canvas ref="donutChart"></canvas>
                <div class="donut-center">
                  <span class="donut-total">{{ chartTotalHours }}h</span>
                  <span class="donut-total-lbl">총시간</span>
                </div>
              </div>
              <div class="donut-legend">
                <span v-for="item in donutData" :key="item.label" class="donut-leg-item">
                  <span class="legend-sq" :style="{ background: item.color }"></span>
                  <span>{{ item.label }}</span>
                </span>
              </div>
            </div>

            <div class="stat-grid">
              <div class="stat-card">
                <div class="stat-val">{{ chartTotalHours }}h</div>
                <div class="stat-lbl">총 소요시간</div>
              </div>
              <div class="stat-card">
                <div class="stat-val">{{ chartEntries.length }}</div>
                <div class="stat-lbl">총 엔트리</div>
              </div>
              <div class="stat-card">
                <div class="stat-val">{{ filteredUniqueAssignees }}</div>
                <div class="stat-lbl">참여 담당자</div>
              </div>
              <div class="stat-card">
                <div class="stat-val">{{ filteredUniqueProjects }}</div>
                <div class="stat-lbl">프로젝트</div>
              </div>
            </div>
          </div>
        </div>

        <!-- ── 해당 task 엔트리 테이블 ── -->
        <!-- 이 테이블만 현재 task(taskId) 기준 -->
        <div class="table-card">
          <div class="table-topbar">
            <div class="task-label-wrap">
              <span class="task-badge">현재 업무</span>
              <span class="task-title-text">{{ taskInfo?.title ?? '-' }}</span>
            </div>
            <div class="topbar-right">
              <span class="count-badge">총 {{ filteredEntries.length }}건 · {{ taskTotalHours }}h</span>
              <div class="filter-row">
                <div class="date-row">
                  <input v-model="filters.dateFrom" type="date" class="date-inp" />
                  <span class="date-sep">~</span>
                  <input v-model="filters.dateTo" type="date" class="date-inp" />
                </div>
                <div class="search-wrap">
                  <svg width="13" height="13" viewBox="0 0 20 20" fill="none">
                    <circle cx="9" cy="9" r="6" stroke="#94a3b8" stroke-width="1.8" />
                    <path d="M14 14l3 3" stroke="#94a3b8" stroke-width="1.8" stroke-linecap="round" />
                  </svg>
                  <input v-model="filters.keyword" type="text" placeholder="메모 검색" @input="entryPage = 1" />
                </div>
                <button class="btn-reset" @click="resetFilter">초기화</button>
              </div>
              <button class="btn-export btn-excel" @click="exportExcel">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5z" /></svg>
                Excel
              </button>
              <button class="btn-export btn-pdf" @click="exportPdf">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5z" /></svg>
                PDF
              </button>
            </div>
          </div>

          <div class="tbl-wrap">
            <table>
              <thead>
                <tr>
                  <th style="width:40px"><input type="checkbox" v-model="selectAll" class="cb" /></th>
                  <th>날짜</th>
                  <th>담당자</th>
                  <th>소요시간</th>
                  <th>메모</th>
                  <th style="width:72px">관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading">
                  <td colspan="6" class="empty-cell">⌛ 로딩중...</td>
                </tr>
                <tr v-else-if="paginatedEntries.length === 0">
                  <td colspan="6" class="empty-cell">등록된 엔트리가 없습니다.</td>
                </tr>
                <tr v-for="entry in paginatedEntries" :key="entry.id" class="data-row" @click="openDetail(entry)">
                  <td @click.stop><input v-model="entry.selected" type="checkbox" class="cb" /></td>
                  <td>{{ entry.date }}</td>
                  <td>
                    <div class="assignee-cell">
                      <div class="avatar">{{ entry.assignee?.[0] }}</div>
                      {{ entry.assignee }}
                    </div>
                  </td>
                  <td><span class="hours-val">{{ entry.hours }}h</span></td>
                  <td class="memo-cell">{{ entry.memo || '—' }}</td>
                  <td @click.stop class="action-cell">
                    <button class="btn-act btn-edit" @click="editEntry(entry)" title="수정">
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                      </svg>
                    </button>
                    <button class="btn-act btn-del" @click="deleteEntry(entry.id)" title="삭제">
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                        <polyline points="3 6 5 6 21 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                        <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                        <path d="M10 11v6M14 11v6M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
                      </svg>
                    </button>
                  </td>
                </tr>
                <tr class="total-row">
                  <td colspan="2"></td>
                  <td class="total-label">합계</td>
                  <td class="total-value">{{ taskTotalHours }}h</td>
                  <td colspan="2"></td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pager">
            <button class="pg" @click="prevEntryPage">‹</button>
            <button v-for="p in entryTotalPages" :key="p" class="pg" :class="{ on: p === entryPage }" @click="entryPage = p">{{ p }}</button>
            <button class="pg" @click="nextEntryPage">›</button>
          </div>
        </div>

        <!-- ── 상세 사이드 패널 ── -->
        <transition name="slide">
          <div class="detail-overlay" v-if="selectedEntry" @click.self="selectedEntry = null">
            <div class="detail-panel">
              <div class="detail-header">
                <h3 class="detail-title">엔트리 상세</h3>
                <button class="icon-btn" @click="selectedEntry = null">✕</button>
              </div>
              <div class="detail-body">
                <div class="detail-rows">
                  <div class="detail-row">
                    <span class="dk">업무명</span>
                    <span class="dv">{{ taskInfo?.title ?? '-' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="dk">날짜</span>
                    <span class="dv">{{ selectedEntry.date }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="dk">담당자</span>
                    <div class="assignee-cell">
                      <div class="avatar">{{ selectedEntry.assignee?.[0] }}</div>
                      {{ selectedEntry.assignee }}
                    </div>
                  </div>
                  <div class="detail-row">
                    <span class="dk">소요시간</span>
                    <span class="hours-val">{{ selectedEntry.hours }}h</span>
                  </div>
                </div>
                <div class="detail-divider"></div>
                <div class="detail-memo-block">
                  <span class="dk">메모</span>
                  <p class="detail-memo">{{ selectedEntry.memo || '메모 없음' }}</p>
                </div>
                <div class="detail-actions">
                  <button class="btn-detail-edit" @click="editEntry(selectedEntry); selectedEntry = null">수정</button>
                  <button class="btn-detail-del" @click="deleteEntry(selectedEntry.id); selectedEntry = null">삭제</button>
                </div>
              </div>
            </div>
          </div>
        </transition>

        <!-- ── 엔트리 추가/수정 모달 ── -->
        <transition name="fade">
          <div class="modal-overlay" v-if="showAddModal || editingEntry" @click.self="closeModal">
            <div class="modal">
              <div class="modal-header">
                <h3 class="modal-title">{{ editingEntry ? '엔트리 수정' : '엔트리 추가' }}</h3>
                <button class="icon-btn" @click="closeModal">✕</button>
              </div>
              <div class="modal-body">
                <div class="modal-task-info">
                  <span class="modal-task-label">업무</span>
                  <span class="modal-task-name">{{ taskInfo?.title ?? '-' }}</span>
                </div>
                <div class="form-grid">
                  <div class="form-group">
                    <label class="form-label">작업일</label>
                    <input v-model="form.workDate" type="date" class="form-input" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">소요시간 (h)</label>
                    <input v-model="form.hours" type="number" min="0" step="0.5" class="form-input" placeholder="0" />
                  </div>
                  <div class="form-group form-group--full">
                    <label class="form-label">메모</label>
                    <textarea v-model="form.taskDesc" class="form-textarea" placeholder="작업 내용을 입력하세요" rows="4"></textarea>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn-cancel" @click="closeModal">취소</button>
                <button class="btn-save" @click="saveEntry">{{ editingEntry ? '수정 완료' : '추가' }}</button>
              </div>
            </div>
          </div>
        </transition>
      </main>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed, watch, onMounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Sidebar from '../partials/Sidebar.vue';
import Header from '../partials/Header.vue';
import Chart from 'chart.js/auto';
import api from '../utils/api';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import NanumGothicBase64 from '../assets/fonts/NanumGothic.js';

export default defineComponent({
  name: 'TimeEntries',
  components: { Sidebar, Header },

  setup() {
    const route     = useRoute();
    const router    = useRouter();
    const taskId    = route.params.taskId;
    const projectId = route.params.projectId; // 상위 프로젝트 ID — 뒤로가기에 사용

    /* ─── canvas refs ─── */
    const barChart1  = ref(null);
    const hbarChart1 = ref(null);
    const lineChart  = ref(null);
    const hbarChart2 = ref(null);
    const donutChart = ref(null);

    /* ─── state ─── */
    const sidebarOpen  = ref(false);
    const loading      = ref(false);

    // 현재 task 엔트리 (테이블 전용)
    const entries      = ref([]);
    // 프로젝트 전체 집계 (차트 전용 — reportList)
    const allEntries   = ref([]);

    const taskInfo     = ref(null);
    const projectList  = ref([]);
    const userList     = ref([]);
    const taskTypeList = ref([]);

    const selectedProject = ref('');
    const hPage1 = ref(1);
    const hPage2 = ref(1);
    const HB_PER = 5;

    // 테이블 전용 상태
    const entryPage    = ref(1);
    const itemsPerPage = 10;
    const selectAll    = ref(false);
    const selectedEntry = ref(null);
    const showAddModal  = ref(false);
    const editingEntry  = ref(null);

    const form    = ref({ workDate: '', hours: '', taskDesc: '' });
    const filters = ref({ keyword: '', dateFrom: '', dateTo: '' });

    const donutColors = { 개발: '#3b82f6', 기획: '#f59e0b', 디자인: '#f97316', 기타: '#94a3b8' };

    /* ─── 뒤로가기: projectId를 넘겨서 TimeReport가 올바른 상위 프로젝트로 init ─── */
    function goBack() {
      router.push({ name: 'TimeReport', params: { projectId } });
    }

    /* ─── fetchData ─── */
    const fetchData = async () => {
      loading.value = true;
      try {
        // ① 현재 task 상세 + task 엔트리 + 메타
        const { data } = await api.get('/task-total-info', { params: { taskId } });
        taskInfo.value     = data.taskDetail?.[0]  ?? null;
        projectList.value  = data.projectList      ?? [];
        userList.value     = data.userList         ?? [];
        taskTypeList.value = data.taskTypeList     ?? [];

        // 현재 task 엔트리만 (테이블용)
        entries.value = (data.timeEntryList ?? []).map(e => ({
          id:       e.timeEntryId,
          taskId:   taskId,
          name:     taskInfo.value?.title ?? '-',
          date:     formatDate(e.workDate),
          type:     taskInfo.value?.typeName ?? '-',
          assignee: e.userName ?? '-',
          hours:    Number(e.hours ?? 0),
          memo:     e.taskDesc ?? '',
          userId:   e.userId,
          selected: false,
        }));

        // ② 프로젝트 전체 집계 (차트용) — SP_GET_TIME_ENTRY_REPORT
        const { data: reportData } = await api.get('/report/time-entry', {
          params: { projectId },
        });

        allEntries.value = (reportData.reportList ?? []).map(r => ({
          id:        r.taskId,
          project:   r.parentProjectName ?? r.projectName ?? '-',
          projectId: String(r.projectId ?? ''),
          name:      r.title ?? '-',
          date:      formatDate(r.lastWorkDate),
          type:      r.typeName ?? '-',
          assignee:  r.userName ?? '-',
          hours:     Number(r.totalHours ?? 0),
        }));

      } catch (e) {
        console.error('타임 엔트리 조회 실패', e);
      } finally {
        loading.value = false;
      }
    };

    /* ─── 유틸 ─── */
    function formatDate(val) {
      if (!val) return '-';
      return String(val).substring(0, 10).replace(/-/g, '.');
    }

    /* ─── computed: 프로젝트 칩 ─── */
    const projectOptions = computed(() => {
      const opts = [{ value: '', label: '전체' }];
      projectList.value.forEach(p => opts.push({ value: String(p.projectId), label: p.projectName }));
      return opts;
    });

    const selectedProjectLabel = computed(() => {
      if (!selectedProject.value) return '전체 프로젝트';
      return projectOptions.value.find(p => p.value === selectedProject.value)?.label ?? '전체 프로젝트';
    });

    const barLegend = computed(() =>
      projectOptions.value.slice(1).map((p, i) => ({
        label: p.label,
        color: ['#3b82f6', '#94a3b8', '#cbd5e1'][i] ?? '#cbd5e1',
      }))
    );

    const periodLabel = computed(() => {
      if (!taskInfo.value) return '-';
      const s = taskInfo.value.startDate ? formatDate(taskInfo.value.startDate) : '?';
      const e = taskInfo.value.dueDate   ? formatDate(taskInfo.value.dueDate)   : '?';
      return `${s} ~ ${e}`;
    });

    /* ─── computed: 차트용 — 칩 선택 기준 allEntries 필터 ─── */
    const chartEntries = computed(() => {
      if (!selectedProject.value) return allEntries.value;
      return allEntries.value.filter(e => e.projectId === selectedProject.value);
    });

    const chartTotalHours = computed(() =>
      chartEntries.value.reduce((s, e) => s + Number(e.hours || 0), 0)
    );
    const filteredUniqueAssignees = computed(() =>
      new Set(chartEntries.value.map(e => e.assignee)).size
    );
    const filteredUniqueProjects = computed(() =>
      new Set(chartEntries.value.map(e => e.projectId)).size
    );

    const projectHours = computed(() => {
      const map = {};
      chartEntries.value.forEach(e => { map[e.project] = (map[e.project] || 0) + Number(e.hours || 0); });
      return Object.entries(map);
    });

    const allAssigneeHours = computed(() => {
      const map = {};
      chartEntries.value.forEach(e => { map[e.assignee] = (map[e.assignee] || 0) + Number(e.hours || 0); });
      return Object.entries(map).sort((a, b) => b[1] - a[1]);
    });
    const hTotalPages1 = computed(() => Math.max(1, Math.ceil(allAssigneeHours.value.length / HB_PER)));
    const pagedAssigneeHours = computed(() => {
      const s = (hPage1.value - 1) * HB_PER;
      return allAssigneeHours.value.slice(s, s + HB_PER);
    });

    const allDateHours = computed(() => {
      const map = {};
      chartEntries.value.forEach(e => { map[e.date] = (map[e.date] || 0) + Number(e.hours || 0); });
      return Object.entries(map).sort((a, b) => a[0].localeCompare(b[0]));
    });

    const allTaskHours = computed(() => {
      const map = {};
      chartEntries.value.forEach(e => { map[e.name] = (map[e.name] || 0) + Number(e.hours || 0); });
      return Object.entries(map).sort((a, b) => b[1] - a[1]);
    });
    const hTotalPages2 = computed(() => Math.max(1, Math.ceil(allTaskHours.value.length / HB_PER)));
    const pagedTaskHours = computed(() => {
      const s = (hPage2.value - 1) * HB_PER;
      return allTaskHours.value.slice(s, s + HB_PER);
    });

    const donutData = computed(() => {
      const typeHours = {};
      chartEntries.value.forEach(e => { typeHours[e.type] = (typeHours[e.type] || 0) + Number(e.hours || 0); });
      return Object.entries(typeHours).map(([label, val]) => ({ label, val, color: donutColors[label] || '#94a3b8' }));
    });

    /* ─── computed: 테이블용 — 현재 task entries 필터 ─── */
    const filteredEntries = computed(() => {
      return entries.value.filter(e => {
        if (filters.value.keyword && !e.memo.includes(filters.value.keyword)) return false;
        if (filters.value.dateFrom && e.date < filters.value.dateFrom.replace(/-/g, '.')) return false;
        if (filters.value.dateTo   && e.date > filters.value.dateTo.replace(/-/g, '.'))   return false;
        return true;
      });
    });

    const paginatedEntries = computed(() => {
      const s = (entryPage.value - 1) * itemsPerPage;
      return filteredEntries.value.slice(s, s + itemsPerPage);
    });

    const entryTotalPages = computed(() => Math.max(1, Math.ceil(filteredEntries.value.length / itemsPerPage)));

    // 테이블 합계 — 현재 task 기준
    const taskTotalHours = computed(() =>
      filteredEntries.value.reduce((s, e) => s + Number(e.hours || 0), 0)
    );

    /* ─── 차트 인스턴스 ─── */
    let chartInstances = {};
    function destroyChart(key) {
      if (chartInstances[key]) { chartInstances[key].destroy(); delete chartInstances[key]; }
    }
    const CD = { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } };
    const AX = { ticks: { color: '#94a3b8', font: { size: 10 } }, grid: { color: 'rgba(0,0,0,0.05)' }, border: { display: false } };

    function buildBarChart() {
      destroyChart('bar1'); if (!barChart1.value) return;
      const colors = ['#3b82f6', '#94a3b8', '#cbd5e1'];
      chartInstances.bar1 = new Chart(barChart1.value, {
        type: 'bar',
        data: { labels: projectHours.value.map(([p]) => p), datasets: [{ data: projectHours.value.map(([,h]) => h), backgroundColor: projectHours.value.map((_,i) => colors[i]||'#cbd5e1'), borderRadius: 3, barThickness: 36 }] },
        options: { ...CD, scales: { x: AX, y: { ...AX, min: 0, ticks: { ...AX.ticks, callback: v => v+'h' } } } },
      });
    }
    function buildHBar1() {
      destroyChart('hbar1'); if (!hbarChart1.value) return;
      const data = pagedAssigneeHours.value;
      chartInstances.hbar1 = new Chart(hbarChart1.value, {
        type: 'bar',
        data: { labels: data.map(([a]) => a), datasets: [{ data: data.map(([,h]) => h), backgroundColor: '#3b82f6', borderRadius: 3, barThickness: 16 }] },
        options: { ...CD, indexAxis: 'y', scales: { x: { ...AX, min: 0, ticks: { ...AX.ticks, callback: v => v+'h' } }, y: { ...AX, grid: { display: false } } } },
      });
    }
    function buildLineChart() {
      destroyChart('line'); if (!lineChart.value) return;
      const data = allDateHours.value;
      chartInstances.line = new Chart(lineChart.value, {
        type: 'line',
        data: { labels: data.map(([d]) => d), datasets: [{ data: data.map(([,h]) => h), borderColor: '#3b82f6', backgroundColor: 'rgba(59,130,246,0.08)', pointBackgroundColor: '#3b82f6', pointRadius: 4, tension: 0.3, fill: true }] },
        options: { ...CD, scales: { x: AX, y: { ...AX, min: 0, ticks: { ...AX.ticks, callback: v => v+'h' } } } },
      });
    }
    function buildHBar2() {
      destroyChart('hbar2'); if (!hbarChart2.value) return;
      const data = pagedTaskHours.value;
      chartInstances.hbar2 = new Chart(hbarChart2.value, {
        type: 'bar',
        data: { labels: data.map(([n]) => n.length > 8 ? n.slice(0,8)+'…' : n), datasets: [{ data: data.map(([,h]) => h), backgroundColor: '#6366f1', borderRadius: 3, barThickness: 16 }] },
        options: { ...CD, indexAxis: 'y', scales: { x: { ...AX, min: 0, ticks: { ...AX.ticks, callback: v => v+'h' } }, y: { ...AX, grid: { display: false } } } },
      });
    }
    function buildDonut() {
      destroyChart('donut'); if (!donutChart.value) return;
      const data = donutData.value;
      chartInstances.donut = new Chart(donutChart.value, {
        type: 'doughnut',
        data: { labels: data.map(d => d.label), datasets: [{ data: data.map(d => d.val), backgroundColor: data.map(d => d.color), borderWidth: 2, borderColor: '#ffffff' }] },
        options: { ...CD, cutout: '58%', plugins: { legend: { display: false }, tooltip: { callbacks: { label: ctx => ` ${ctx.label}: ${ctx.parsed}h` } } } },
      });
    }
    function rebuildAllCharts() { nextTick(() => { buildBarChart(); buildHBar1(); buildLineChart(); buildHBar2(); buildDonut(); }); }

    /* ─── watchers ─── */
    watch(allEntries, () => nextTick(rebuildAllCharts));
    watch(selectedProject, () => { hPage1.value = 1; hPage2.value = 1; nextTick(rebuildAllCharts); });
    watch(hPage1, () => nextTick(buildHBar1));
    watch(hPage2, () => nextTick(buildHBar2));
    watch(selectAll, val => { paginatedEntries.value.forEach(e => (e.selected = val)); });

    /* ─── lifecycle ─── */
    onMounted(async () => { await fetchData(); rebuildAllCharts(); });

    /* ─── 테이블 액션 ─── */
    function selectProject(val) { selectedProject.value = val; }
    function resetFilter() { filters.value = { keyword: '', dateFrom: '', dateTo: '' }; entryPage.value = 1; }
    function openDetail(entry) { selectedEntry.value = entry; }

    function editEntry(entry) {
      editingEntry.value = entry;
      form.value = { workDate: entry.date?.replace(/\./g, '-') ?? '', hours: entry.hours, taskDesc: entry.memo };
    }

    async function deleteEntry(id) {
      if (!confirm('정말 삭제하시겠습니까?')) return;
      try { await api.delete(`/time-entry/${id}`); await fetchData(); rebuildAllCharts(); }
      catch (e) { console.error('삭제 실패', e); }
    }

    async function saveEntry() {
      try {
        if (editingEntry.value) {
          await api.put(`/time-entry/${editingEntry.value.id}`, { workDate: form.value.workDate, hours: form.value.hours, taskDesc: form.value.taskDesc });
        } else {
          await api.post('/time-entry', { taskId, workDate: form.value.workDate, hours: form.value.hours, taskDesc: form.value.taskDesc });
        }
        await fetchData(); rebuildAllCharts(); closeModal();
      } catch (e) { console.error('저장 실패', e); }
    }

    function closeModal() { showAddModal.value = false; editingEntry.value = null; form.value = { workDate: '', hours: '', taskDesc: '' }; }
    function prevEntryPage() { if (entryPage.value > 1) entryPage.value--; }
    function nextEntryPage() { if (entryPage.value < entryTotalPages.value) entryPage.value++; }

    /* ─── 내보내기 ─── */
    const getExportData = () => {
      const checked = filteredEntries.value.filter(e => e.selected);
      return checked.length > 0 ? checked : filteredEntries.value;
    };
    function exportExcel() {
      const rows = getExportData().map(e => ({ 업무명: e.name, 날짜: e.date, 담당자: e.assignee, '소요시간(h)': e.hours, 메모: e.memo || '-' }));
      const ws = XLSX.utils.json_to_sheet(rows);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, '타임엔트리');
      XLSX.writeFile(wb, '타임엔트리.xlsx');
    }
    function exportPdf() {
      const doc = new jsPDF({ orientation: 'landscape' });
      doc.addFileToVFS('NanumGothic.ttf', NanumGothicBase64);
      doc.addFont('NanumGothic.ttf', 'NanumGothic', 'normal');
      doc.setFont('NanumGothic', 'normal');
      doc.setFontSize(14);
      doc.text(`타임 엔트리 — ${taskInfo.value?.title ?? ''}`, 14, 15);
      autoTable(doc, {
        head: [['날짜', '담당자', '소요시간', '메모']],
        body: getExportData().map(e => [e.date, e.assignee, `${e.hours}h`, e.memo || '-']),
        startY: 20, styles: { fontSize: 9, font: 'NanumGothic' },
        headStyles: { fillColor: [30, 64, 175], font: 'NanumGothic', fontStyle: 'normal' },
        didParseCell: d => { d.cell.styles.font = 'NanumGothic'; },
      });
      doc.setFontSize(11);
      doc.text(`합계: ${taskTotalHours.value}h`, 14, doc.lastAutoTable.finalY + 8);
      doc.save('타임엔트리.pdf');
    }

    return {
      barChart1, hbarChart1, lineChart, hbarChart2, donutChart,
      sidebarOpen, loading,
      allEntries, entries,
      taskInfo, periodLabel, projectOptions, selectedProject, selectedProjectLabel, barLegend,
      chartEntries, chartTotalHours, filteredUniqueAssignees, filteredUniqueProjects, donutData,
      filteredEntries, paginatedEntries, entryPage, entryTotalPages, taskTotalHours,
      hPage1, hPage2, hTotalPages1, hTotalPages2,
      selectAll, selectedEntry, showAddModal, editingEntry, form, filters,
      goBack, selectProject, resetFilter, openDetail, editEntry, deleteEntry,
      saveEntry, closeModal, prevEntryPage, nextEntryPage, exportExcel, exportPdf, formatDate,
    };
  },
});
</script>

<style scoped>
*,*::before,*::after { box-sizing:border-box; margin:0; padding:0; }
.page { padding:24px 28px; color:#1e293b; font-family:'Pretendard','Noto Sans KR',sans-serif; }

.page-header { display:flex; align-items:flex-end; justify-content:space-between; margin-bottom:16px; padding-bottom:14px; border-bottom:1px solid #e2e8f0; }
.page-header-left { display:flex; align-items:center; gap:10px; }
.btn-back { display:inline-flex; align-items:center; gap:5px; padding:5px 11px; font-size:12px; font-weight:500; background:#f1f5f9; color:#475569; border:1px solid #e2e8f0; border-radius:4px; cursor:pointer; }
.btn-back:hover { background:#e2e8f0; }
.breadcrumb-sep { color:#cbd5e1; font-size:14px; }
.page-title { font-size:18px; font-weight:700; color:#0f172a; letter-spacing:-0.3px; }
.page-subtitle { font-size:12px; color:#94a3b8; margin-top:2px; }
.btn-add { display:inline-flex; align-items:center; gap:5px; padding:7px 14px; font-size:12px; font-weight:600; background:#1e40af; color:#fff; border:none; border-radius:4px; cursor:pointer; }
.btn-add:hover { background:#1e3a8a; }

.proj-header-bar { display:flex; align-items:center; justify-content:space-between; background:#fff; border:1px solid #e2e8f0; border-radius:6px; padding:12px 18px; margin-bottom:14px; }
.proj-info { display:flex; align-items:center; gap:14px; }
.proj-label { font-size:12px; font-weight:600; color:#64748b; white-space:nowrap; }
.proj-chips { display:flex; gap:6px; flex-wrap:wrap; }
.proj-chip { padding:4px 12px; font-size:12px; font-weight:500; background:#f8fafc; color:#64748b; border:1px solid #e2e8f0; border-radius:3px; cursor:pointer; transition:all 0.12s; }
.proj-chip:hover { background:#eff6ff; color:#1d4ed8; border-color:#bfdbfe; }
.proj-chip.active { background:#1e40af; color:#fff; border-color:#1e40af; }
.period-wrap { display:flex; align-items:center; gap:6px; }
.period-text { font-size:13px; color:#475569; font-weight:500; }

.dashboard-grid { display:grid; grid-template-columns:1fr 280px; gap:14px; margin-bottom:14px; align-items:start; }
.charts-left { display:flex; flex-direction:column; gap:14px; }
.chart-row { display:grid; grid-template-columns:1fr 1fr; gap:14px; }
.chart-card { background:#fff; border:1px solid #e2e8f0; border-radius:6px; padding:14px 16px; }
.chart-card-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:10px; }
.chart-card-title { font-size:12px; font-weight:600; color:#334155; }
.chart-unit { font-size:10px; color:#94a3b8; }
.chart-canvas-wrap { position:relative; height:150px; }
.chart-canvas-wrap canvas { width:100% !important; }
.chart-legend { display:flex; flex-wrap:wrap; gap:10px; margin-top:8px; }
.legend-item { display:flex; align-items:center; gap:4px; font-size:10px; color:#64748b; }
.legend-sq { width:8px; height:8px; border-radius:1px; flex-shrink:0; }
.hbar-footer { display:flex; align-items:center; justify-content:space-between; margin-top:8px; padding-top:6px; border-top:1px solid #f1f5f9; }
.hbar-count { font-size:10px; color:#94a3b8; }
.mini-pager { display:flex; gap:3px; }
.mpg { width:20px; height:20px; display:flex; align-items:center; justify-content:center; font-size:11px; border:1px solid #e2e8f0; border-radius:3px; background:#fff; color:#64748b; cursor:pointer; }
.mpg:hover { background:#f1f5f9; }
.mpg.on { background:#1e40af; color:#fff; border-color:#1e40af; }

.charts-right { display:flex; flex-direction:column; gap:10px; }
.donut-card { display:flex; flex-direction:column; }
.donut-canvas-wrap { position:relative; height:180px; margin:4px 0; }
.donut-center { position:absolute; inset:0; display:flex; flex-direction:column; align-items:center; justify-content:center; pointer-events:none; }
.donut-total { font-size:18px; font-weight:700; color:#0f172a; }
.donut-total-lbl { font-size:10px; color:#94a3b8; }
.donut-legend { display:flex; flex-wrap:wrap; justify-content:center; gap:8px; margin-top:8px; }
.donut-leg-item { display:flex; align-items:center; gap:4px; font-size:11px; color:#64748b; }
.stat-grid { display:grid; grid-template-columns:1fr 1fr; gap:8px; }
.stat-card { background:#fff; border:1px solid #e2e8f0; border-radius:6px; padding:12px 14px; text-align:center; }
.stat-val { font-size:20px; font-weight:700; color:#0f172a; letter-spacing:-0.3px; }
.stat-lbl { font-size:10px; color:#94a3b8; margin-top:2px; }

/* 테이블 */
.table-card { background:#fff; border:1px solid #e2e8f0; border-radius:6px; overflow:hidden; }
.table-topbar { display:flex; align-items:center; justify-content:space-between; padding:10px 16px; gap:10px; flex-wrap:wrap; border-bottom:1px solid #e2e8f0; background:#f8fafc; }
.task-label-wrap { display:flex; align-items:center; gap:8px; }
.task-badge { padding:2px 8px; font-size:10px; font-weight:700; background:#eff6ff; color:#1d4ed8; border:1px solid #bfdbfe; border-radius:999px; white-space:nowrap; }
.task-title-text { font-size:13px; font-weight:600; color:#1e293b; }
.topbar-right { display:flex; align-items:center; gap:6px; flex-wrap:wrap; }
.filter-row { display:flex; align-items:center; gap:6px; }
.search-wrap { position:relative; display:flex; align-items:center; }
.search-wrap svg { position:absolute; left:7px; pointer-events:none; }
.search-wrap input { padding:0 8px 0 26px; height:30px; width:120px; font-size:12px; border:1px solid #d1d5db; border-radius:4px; background:#fff; color:#374151; outline:none; }
.search-wrap input:focus { border-color:#3b82f6; }
.date-row { display:flex; align-items:center; gap:4px; }
.date-inp { height:30px; padding:0 8px; font-size:12px; border:1px solid #d1d5db; border-radius:4px; background:#fff; color:#374151; outline:none; }
.date-inp:focus { border-color:#3b82f6; }
.date-sep { font-size:11px; color:#94a3b8; }
.btn-reset { height:30px; padding:0 12px; font-size:12px; background:#f1f5f9; color:#64748b; border:1px solid #e2e8f0; border-radius:4px; cursor:pointer; }
.btn-reset:hover { background:#e2e8f0; }
.count-badge { padding:2px 10px; font-size:11px; font-weight:600; background:#eff6ff; color:#1d4ed8; border:1px solid #bfdbfe; border-radius:999px; white-space:nowrap; }
.btn-export { display:inline-flex; align-items:center; gap:4px; height:28px; padding:0 10px; font-size:11px; font-weight:600; border-radius:4px; cursor:pointer; }
.btn-excel { background:#f0fdf4; color:#16a34a; border:1px solid #bbf7d0; }
.btn-excel:hover { background:#dcfce7; }
.btn-pdf { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }
.btn-pdf:hover { background:#fee2e2; }

.tbl-wrap { overflow-x:auto; }
table { width:100%; border-collapse:collapse; font-size:12.5px; }
thead { background:#f8fafc; }
th { padding:9px 14px; text-align:center; font-size:11px; font-weight:600; color:#64748b; border-bottom:1px solid #e2e8f0; white-space:nowrap; }
td { padding:10px 14px; text-align:center; border-bottom:1px solid #f1f5f9; color:#334155; }
.data-row { cursor:pointer; transition:background 0.1s; }
.data-row:hover { background:#f8faff; }
.memo-cell { text-align:left; color:#94a3b8; font-size:11px; max-width:200px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.empty-cell { text-align:center; padding:36px; color:#94a3b8; font-size:13px; }
.assignee-cell { display:inline-flex; align-items:center; gap:6px; }
.avatar { width:22px; height:22px; border-radius:50%; background:#1e40af; color:#fff; font-size:10px; font-weight:700; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.hours-val { font-weight:700; color:#1d4ed8; }
.action-cell { display:flex; align-items:center; justify-content:center; gap:3px; }
.btn-act { width:26px; height:26px; border-radius:4px; display:flex; align-items:center; justify-content:center; cursor:pointer; border:1px solid; transition:all 0.12s; }
.btn-edit { background:#f0fdf4; color:#16a34a; border-color:#bbf7d0; }
.btn-edit:hover { background:#dcfce7; }
.btn-del { background:#fef2f2; color:#dc2626; border-color:#fecaca; }
.btn-del:hover { background:#fee2e2; }
.total-row td { background:#f8fafc; font-weight:600; border-top:2px solid #e2e8f0; border-bottom:none; }
.total-label { text-align:right; font-size:12px; color:#64748b; }
.total-value { color:#1d4ed8; font-size:14px; font-weight:700; }
.cb { width:14px; height:14px; cursor:pointer; accent-color:#1d4ed8; }

.pager { display:flex; justify-content:center; gap:4px; padding:12px 0; }
.pg { width:28px; height:28px; display:flex; align-items:center; justify-content:center; font-size:12px; border:1px solid #e2e8f0; border-radius:4px; background:#fff; color:#64748b; cursor:pointer; }
.pg:hover { background:#f1f5f9; }
.pg.on { background:#1e40af; color:#fff; border-color:#1e40af; font-weight:700; }

/* 상세 패널 */
.detail-overlay { position:fixed; inset:0; background:rgba(15,23,42,0.3); z-index:999; display:flex; justify-content:flex-end; }
.detail-panel { width:360px; height:100%; background:#fff; box-shadow:-4px 0 20px rgba(0,0,0,0.1); display:flex; flex-direction:column; }
.detail-header { display:flex; align-items:center; justify-content:space-between; padding:18px 22px; border-bottom:1px solid #e2e8f0; }
.detail-title { font-size:15px; font-weight:700; color:#0f172a; }
.icon-btn { width:26px; height:26px; display:flex; align-items:center; justify-content:center; background:#f1f5f9; border:none; border-radius:4px; cursor:pointer; color:#64748b; font-size:12px; }
.icon-btn:hover { background:#e2e8f0; }
.detail-body { padding:20px 22px; flex:1; overflow-y:auto; }
.detail-rows { display:flex; flex-direction:column; gap:14px; }
.detail-row { display:flex; align-items:center; justify-content:space-between; }
.dk { font-size:11px; font-weight:600; color:#64748b; text-transform:uppercase; letter-spacing:0.04em; }
.dv { font-size:13px; color:#1e293b; font-weight:500; }
.detail-divider { height:1px; background:#e2e8f0; margin:18px 0; }
.detail-memo-block { display:flex; flex-direction:column; gap:7px; }
.detail-memo { font-size:12px; color:#475569; line-height:1.6; background:#f8fafc; padding:10px 12px; border-radius:4px; border:1px solid #e2e8f0; }
.detail-actions { display:flex; gap:8px; margin-top:20px; }
.btn-detail-edit,.btn-detail-del { flex:1; display:inline-flex; align-items:center; justify-content:center; padding:8px 0; font-size:12px; font-weight:600; border-radius:4px; cursor:pointer; }
.btn-detail-edit { background:#eff6ff; color:#1d4ed8; border:1px solid #bfdbfe; }
.btn-detail-edit:hover { background:#dbeafe; }
.btn-detail-del { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }
.btn-detail-del:hover { background:#fee2e2; }

/* 모달 */
.modal-overlay { position:fixed; inset:0; background:rgba(15,23,42,0.4); z-index:1000; display:flex; align-items:center; justify-content:center; }
.modal { width:420px; background:#fff; border-radius:8px; box-shadow:0 16px 48px rgba(0,0,0,0.14); overflow:hidden; }
.modal-header { display:flex; align-items:center; justify-content:space-between; padding:18px 22px; border-bottom:1px solid #e2e8f0; }
.modal-title { font-size:15px; font-weight:700; color:#0f172a; }
.modal-task-info { display:flex; align-items:center; gap:8px; padding:10px 0 16px; border-bottom:1px solid #f1f5f9; margin-bottom:16px; }
.modal-task-label { font-size:10px; font-weight:700; color:#1d4ed8; background:#eff6ff; border:1px solid #bfdbfe; padding:2px 8px; border-radius:999px; }
.modal-task-name { font-size:13px; font-weight:600; color:#1e293b; }
.modal-body { padding:22px; }
.form-grid { display:grid; grid-template-columns:1fr 1fr; gap:14px; }
.form-group { display:flex; flex-direction:column; gap:5px; }
.form-group--full { grid-column:1 / -1; }
.form-label { font-size:11px; font-weight:600; color:#64748b; text-transform:uppercase; letter-spacing:0.04em; }
.form-input { padding:8px 10px; border:1px solid #d1d5db; border-radius:4px; font-size:12px; color:#374151; background:#f9fafb; outline:none; width:100%; }
.form-input:focus { border-color:#3b82f6; background:#fff; }
.form-textarea { padding:8px 10px; border:1px solid #d1d5db; border-radius:4px; font-size:12px; color:#374151; background:#f9fafb; outline:none; width:100%; resize:vertical; font-family:inherit; }
.form-textarea:focus { border-color:#3b82f6; background:#fff; }
.modal-footer { display:flex; justify-content:flex-end; gap:8px; padding:14px 22px; border-top:1px solid #e2e8f0; background:#f8fafc; }
.btn-cancel { padding:7px 18px; font-size:12px; font-weight:600; background:#f1f5f9; color:#64748b; border:1px solid #e2e8f0; border-radius:4px; cursor:pointer; }
.btn-cancel:hover { background:#e2e8f0; }
.btn-save { padding:7px 22px; font-size:12px; font-weight:600; background:#1e40af; color:#fff; border:none; border-radius:4px; cursor:pointer; }
.btn-save:hover { background:#1e3a8a; }

.slide-enter-active .detail-panel,.slide-leave-active .detail-panel { transition:transform 0.22s cubic-bezier(0.4,0,0.2,1); }
.slide-enter-active,.slide-leave-active { transition:opacity 0.22s; }
.slide-enter-from,.slide-leave-to { opacity:0; }
.slide-enter-from .detail-panel { transform:translateX(100%); }
.slide-leave-to .detail-panel { transform:translateX(100%); }
.fade-enter-active,.fade-leave-active { transition:opacity 0.18s; }
.fade-enter-from,.fade-leave-to { opacity:0; }
</style>