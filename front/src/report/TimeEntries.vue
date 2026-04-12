<template>
  <div class="flex h-screen overflow-hidden bg-gray-50 dark:bg-gray-900">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />
    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <Header :sidebarOpen="sidebarOpen" @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <main class="page">

        <!-- ── 페이지 헤더 ── -->
        <div class="page-header">
          <div class="page-header-left">
            <button class="btn-back" @click="$router.push({ name: 'TimeReport' })">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M19 12H5M11 6l-6 6 6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              타임 리포트
            </button>
            <span class="breadcrumb-sep">/</span>
            <div>
              <h1 class="page-title">타임 엔트리</h1>
              <p class="page-subtitle">업무별 시간 기록 현황</p>
            </div>
          </div>
          <button class="btn-add" @click="showAddModal = true">
            <svg width="13" height="13" viewBox="0 0 14 14" fill="none">
              <path d="M7 1v12M1 7h12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
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
              <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
              <path d="M16 2v4M8 2v4M3 10h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
            <span class="period-text">{{ periodLabel }}</span>
          </div>
        </div>

        <!-- ── 메인 대시보드 그리드 ── -->
        <div class="dashboard-grid">

          <!-- 왼쪽: ②③ 차트 2행 -->
          <div class="charts-left">

            <!-- ② 소요시간 + 담당자 투입현황 -->
            <div class="chart-row">
              <div class="chart-card">
                <div class="chart-card-header">
                  <span class="chart-card-title">소요시간 프로젝트</span>
                  <span class="chart-unit">US (Hour)</span>
                </div>
                <div class="chart-canvas-wrap">
                  <canvas ref="barChart1"></canvas>
                </div>
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
                  <span class="chart-unit">US (Hour)</span>
                </div>
                <div class="chart-canvas-wrap">
                  <canvas ref="hbarChart1"></canvas>
                </div>
                <div class="hbar-footer">
                  <span class="hbar-count">담당자 합계 = {{ uniqueAssignees }}명</span>
                  <div class="mini-pager">
                    <button class="mpg" @click="hPage1 = Math.max(1, hPage1 - 1)">‹</button>
                    <button
                      v-for="p in hTotalPages1" :key="p"
                      class="mpg" :class="{ on: p === hPage1 }"
                      @click="hPage1 = p"
                    >{{ p }}</button>
                    <button class="mpg" @click="hPage1 = Math.min(hTotalPages1, hPage1 + 1)">›</button>
                  </div>
                </div>
              </div>
            </div>

            <!-- ③ 동일 구성 두 번째 행 (날짜별 추이) -->
            <div class="chart-row">
              <div class="chart-card">
                <div class="chart-card-header">
                  <span class="chart-card-title">날짜별 소요시간 추이</span>
                  <span class="chart-unit">US (Hour)</span>
                </div>
                <div class="chart-canvas-wrap">
                  <canvas ref="lineChart"></canvas>
                </div>
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
                  <span class="chart-unit">US (Hour)</span>
                </div>
                <div class="chart-canvas-wrap">
                  <canvas ref="hbarChart2"></canvas>
                </div>
                <div class="hbar-footer">
                  <span class="hbar-count">업무 합계 = {{ filteredEntries.length }}건</span>
                  <div class="mini-pager">
                    <button class="mpg" @click="hPage2 = Math.max(1, hPage2 - 1)">‹</button>
                    <button
                      v-for="p in hTotalPages2" :key="p"
                      class="mpg" :class="{ on: p === hPage2 }"
                      @click="hPage2 = p"
                    >{{ p }}</button>
                    <button class="mpg" @click="hPage2 = Math.min(hTotalPages2, hPage2 + 1)">›</button>
                  </div>
                </div>
              </div>
            </div>

          </div>

          <!-- ④ 오른쪽: 도넛 + 통계 -->
          <div class="charts-right">
            <div class="chart-card donut-card">
              <div class="chart-card-header" style="justify-content:center;flex-direction:column;align-items:center;gap:2px">
                <span class="chart-card-title">업무 유형별 비중 (Donut Chart)</span>
                <span style="font-size:11px;color:#94a3b8">(업무 유형별 비중)</span>
              </div>
              <div class="donut-canvas-wrap">
                <canvas ref="donutChart"></canvas>
                <div class="donut-center">
                  <span class="donut-total">{{ totalHours }}h</span>
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

            <!-- 통계 4개 -->
            <div class="stat-grid">
              <div class="stat-card">
                <div class="stat-val">{{ totalHours }}h</div>
                <div class="stat-lbl">총 소요시간</div>
              </div>
              <div class="stat-card">
                <div class="stat-val">{{ filteredEntries.length }}</div>
                <div class="stat-lbl">총 엔트리</div>
              </div>
              <div class="stat-card">
                <div class="stat-val">{{ uniqueAssignees }}</div>
                <div class="stat-lbl">참여 담당자</div>
              </div>
              <div class="stat-card">
                <div class="stat-val">{{ uniqueProjects }}</div>
                <div class="stat-lbl">프로젝트</div>
              </div>
            </div>
          </div>

        </div>

        <!-- ── 목록 테이블 카드 ── -->
        <div class="table-card">
          <div class="table-topbar">
            <div class="filter-row">
              <div class="select-wrap">
                <select v-model="filters.project">
                  <option value="">전체 프로젝트</option>
                  <option v-for="p in projectOptions.slice(1)" :key="p.value" :value="p.label">{{ p.label }}</option>
                </select>
              </div>
              <div class="select-wrap">
                <select v-model="filters.assignee">
                  <option value="">전체 담당자</option>
                  <option value="김개발">김개발</option>
                  <option value="이개발">이개발</option>
                  <option value="최개발">최개발</option>
                </select>
              </div>
              <div class="select-wrap">
                <select v-model="filters.type">
                  <option value="">전체 유형</option>
                  <option value="개발">개발</option>
                  <option value="기획">기획</option>
                  <option value="디자인">디자인</option>
                  <option value="기타">기타</option>
                </select>
              </div>
              <div class="search-wrap">
                <svg width="13" height="13" viewBox="0 0 20 20" fill="none">
                  <circle cx="9" cy="9" r="6" stroke="#94a3b8" stroke-width="1.8"/>
                  <path d="M14 14l3 3" stroke="#94a3b8" stroke-width="1.8" stroke-linecap="round"/>
                </svg>
                <input v-model="filters.keyword" type="text" placeholder="업무명 검색" @input="currentPage=1" />
              </div>
              <div class="date-row">
                <input v-model="filters.dateFrom" type="date" class="date-inp" />
                <span class="date-sep">~</span>
                <input v-model="filters.dateTo" type="date" class="date-inp" />
              </div>
              <button class="btn-reset" @click="resetFilter">초기화</button>
              <button class="btn-search" @click="currentPage=1">검색</button>
            </div>
            <div class="topbar-right">
              <span class="count-badge">총 {{ filteredEntries.length }}건</span>
              <button class="btn-export btn-excel" @click="exportExcel">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5z"/></svg>
                Excel
              </button>
              <button class="btn-export btn-pdf" @click="exportPdf">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5z"/></svg>
                PDF
              </button>
            </div>
          </div>

          <div class="tbl-wrap">
            <table>
              <thead>
                <tr>
                  <th style="width:40px"><input type="checkbox" v-model="selectAll" class="cb" /></th>
                  <th>프로젝트</th>
                  <th>업무명</th>
                  <th>날짜</th>
                  <th>유형</th>
                  <th>담당자</th>
                  <th>소요시간</th>
                  <th>메모</th>
                  <th style="width:72px">관리</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="paginatedEntries.length === 0">
                  <td colspan="9" class="empty-cell">등록된 엔트리가 없습니다.</td>
                </tr>
                <tr
                  v-for="entry in paginatedEntries" :key="entry.id"
                  class="data-row" @click="openDetail(entry)"
                >
                  <td @click.stop><input v-model="entry.selected" type="checkbox" class="cb" /></td>
                  <td>{{ entry.project }}</td>
                  <td class="task-name-cell">{{ entry.name }}</td>
                  <td>{{ entry.date }}</td>
                  <td><span class="badge" :class="`badge--${entry.type}`">{{ entry.type }}</span></td>
                  <td>
                    <div class="assignee-cell">
                      <div class="avatar">{{ entry.assignee[0] }}</div>
                      {{ entry.assignee }}
                    </div>
                  </td>
                  <td><span class="hours-val">{{ entry.hours }}h</span></td>
                  <td class="memo-cell">{{ entry.memo || '—' }}</td>
                  <td @click.stop class="action-cell">
                    <button class="btn-act btn-edit" @click="editEntry(entry)" title="수정">
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </button>
                    <button class="btn-act btn-del" @click="deleteEntry(entry.id)" title="삭제">
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                        <polyline points="3 6 5 6 21 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                        <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                        <path d="M10 11v6M14 11v6M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                      </svg>
                    </button>
                  </td>
                </tr>
                <tr class="total-row">
                  <td colspan="5"></td>
                  <td class="total-label">합계</td>
                  <td class="total-value">{{ totalHours }}h</td>
                  <td colspan="2"></td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pager">
            <button class="pg" @click="prevPage">‹</button>
            <button
              v-for="p in totalPages" :key="p"
              class="pg" :class="{ on: p === currentPage }"
              @click="currentPage = p"
            >{{ p }}</button>
            <button class="pg" @click="nextPage">›</button>
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
                    <span class="dk">프로젝트</span>
                    <span class="dv">{{ selectedEntry.project }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="dk">업무명</span>
                    <span class="dv">{{ selectedEntry.name }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="dk">날짜</span>
                    <span class="dv">{{ selectedEntry.date }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="dk">유형</span>
                    <span class="badge" :class="`badge--${selectedEntry.type}`">{{ selectedEntry.type }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="dk">담당자</span>
                    <div class="assignee-cell">
                      <div class="avatar">{{ selectedEntry.assignee[0] }}</div>
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
                <div class="form-grid">
                  <div class="form-group">
                    <label class="form-label">프로젝트</label>
                    <select v-model="form.project" class="form-select">
                      <option value="">선택</option>
                      <option value="프로젝트 A">프로젝트 A</option>
                      <option value="프로젝트 B">프로젝트 B</option>
                      <option value="프로젝트 C">프로젝트 C</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label class="form-label">업무명</label>
                    <input v-model="form.name" type="text" class="form-input" placeholder="업무명 입력" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">날짜</label>
                    <input v-model="form.date" type="date" class="form-input" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">업무 유형</label>
                    <select v-model="form.type" class="form-select">
                      <option value="">선택</option>
                      <option value="개발">개발</option>
                      <option value="기획">기획</option>
                      <option value="디자인">디자인</option>
                      <option value="기타">기타</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label class="form-label">담당자</label>
                    <select v-model="form.assignee" class="form-select">
                      <option value="">선택</option>
                      <option value="김개발">김개발</option>
                      <option value="이개발">이개발</option>
                      <option value="최개발">최개발</option>
                    </select>
                  </div>
                  <div class="form-group">
                    <label class="form-label">소요시간 (h)</label>
                    <input v-model="form.hours" type="number" min="0" class="form-input" placeholder="0" />
                  </div>
                  <div class="form-group form-group--full">
                    <label class="form-label">메모</label>
                    <textarea v-model="form.memo" class="form-textarea" placeholder="메모를 입력하세요" rows="3"></textarea>
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
import { defineComponent, ref, computed, watch, onMounted, nextTick } from "vue";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import Chart from "chart.js/auto";

const ALL_ENTRIES = [
  { id:"e1", taskId:"d1", project:"프로젝트 A", name:"로그인 화면 구현",    date:"2026.03.04", type:"개발",   assignee:"김개발", hours:2, memo:"OAuth 연동 포함",   selected:false },
  { id:"e2", taskId:"d1", project:"프로젝트 A", name:"로그인 화면 구현",    date:"2026.03.04", type:"개발",   assignee:"김개발", hours:3, memo:"에러 처리 추가",    selected:false },
  { id:"e3", taskId:"d2", project:"프로젝트 A", name:"회원가입 화면 구현",  date:"2026.03.04", type:"개발",   assignee:"이개발", hours:4, memo:"유효성 검사",        selected:false },
  { id:"e4", taskId:"d2", project:"프로젝트 A", name:"회원가입 화면 구현",  date:"2026.03.05", type:"개발",   assignee:"이개발", hours:3, memo:"이메일 인증 추가",   selected:false },
  { id:"e5", taskId:"d3", project:"프로젝트 B", name:"관리자 권한 구현",    date:"2026.03.05", type:"개발",   assignee:"이개발", hours:4, memo:"RBAC 구현",          selected:false },
  { id:"e6", taskId:"d4", project:"프로젝트 B", name:"그룹 관리 구현",      date:"2026.03.05", type:"개발",   assignee:"이개발", hours:5, memo:"",                   selected:false },
  { id:"e7", taskId:"d5", project:"프로젝트 C", name:"서비스 기획서 작성",  date:"2026.03.06", type:"기획",   assignee:"최개발", hours:3, memo:"1차 검토 반영",      selected:false },
  { id:"e8", taskId:"d6", project:"프로젝트 C", name:"UI 컴포넌트 디자인",  date:"2026.03.07", type:"디자인", assignee:"김개발", hours:6, memo:"디자인 시스템 기반", selected:false },
];

export default defineComponent({
  name: "TimeEntries",
  components: { Sidebar, Header },

  setup() {
    /* ─── refs ─── */
    const barChart1  = ref(null);
    const hbarChart1 = ref(null);
    const lineChart  = ref(null);
    const hbarChart2 = ref(null);
    const donutChart = ref(null);

    /* ─── state ─── */
    const sidebarOpen   = ref(false);
    const entries       = ref(ALL_ENTRIES.map(e => ({ ...e })));
    const currentPage   = ref(1);
    const itemsPerPage  = 10;
    const selectAll     = ref(false);
    const selectedEntry = ref(null);
    const showAddModal  = ref(false);
    const editingEntry  = ref(null);
    const selectedProject = ref("");
    const hPage1 = ref(1);
    const hPage2 = ref(1);
    const HB_PER = 5;

    const form = ref({ project:"", name:"", date:"", type:"", assignee:"", hours:"", memo:"" });
    const filters = ref({
      project:"", assignee:"", type:"", keyword:"",
      dateFrom:"2026-03-01", dateTo:"2026-06-30",
    });

    /* ─── static data ─── */
    const projectOptions = [
      { value:"", label:"전체" },
      { value:"A", label:"프로젝트 A" },
      { value:"B", label:"프로젝트 B" },
      { value:"C", label:"프로젝트 C" },
    ];
    const barLegend = [
      { label:"프로젝트 A", color:"#3b82f6" },
      { label:"프로젝트 B", color:"#94a3b8" },
      { label:"프로젝트 C", color:"#cbd5e1" },
    ];
    const donutColors = { 개발:"#3b82f6", 기획:"#f59e0b", 디자인:"#f97316", 기타:"#94a3b8" };

    /* ─── computed ─── */
    const filteredEntries = computed(() => {
      return entries.value.filter(e => {
        if (filters.value.project  && e.project  !== filters.value.project)  return false;
        if (filters.value.assignee && e.assignee !== filters.value.assignee) return false;
        if (filters.value.type     && e.type     !== filters.value.type)     return false;
        if (filters.value.keyword  && !e.name.includes(filters.value.keyword)) return false;
        return true;
      });
    });

    const paginatedEntries = computed(() => {
      const s = (currentPage.value - 1) * itemsPerPage;
      return filteredEntries.value.slice(s, s + itemsPerPage);
    });

    const totalPages      = computed(() => Math.max(1, Math.ceil(filteredEntries.value.length / itemsPerPage)));
    const totalHours      = computed(() => filteredEntries.value.reduce((s, e) => s + Number(e.hours || 0), 0));
    const uniqueAssignees = computed(() => new Set(filteredEntries.value.map(e => e.assignee)).size);
    const uniqueProjects  = computed(() => new Set(filteredEntries.value.map(e => e.project)).size);
    const periodLabel     = computed(() => "2026.03.20 ~ 2026.06.20");

    const donutData = computed(() => {
      const typeHours = {};
      filteredEntries.value.forEach(e => {
        typeHours[e.type] = (typeHours[e.type] || 0) + Number(e.hours || 0);
      });
      return Object.entries(typeHours).map(([label, val]) => ({
        label, val, color: donutColors[label] || "#94a3b8",
      }));
    });

    // 담당자별 시간 (페이징용)
    const assigneeHours = computed(() => {
      const map = {};
      filteredEntries.value.forEach(e => {
        map[e.assignee] = (map[e.assignee] || 0) + Number(e.hours || 0);
      });
      return Object.entries(map).sort((a, b) => b[1] - a[1]);
    });
    const hTotalPages1 = computed(() => Math.max(1, Math.ceil(assigneeHours.value.length / HB_PER)));
    const pagedAssigneeHours = computed(() => {
      const s = (hPage1.value - 1) * HB_PER;
      return assigneeHours.value.slice(s, s + HB_PER);
    });

    // 업무명별 시간 (페이징용)
    const taskHours = computed(() => {
      const map = {};
      filteredEntries.value.forEach(e => {
        map[e.name] = (map[e.name] || 0) + Number(e.hours || 0);
      });
      return Object.entries(map).sort((a, b) => b[1] - a[1]);
    });
    const hTotalPages2 = computed(() => Math.max(1, Math.ceil(taskHours.value.length / HB_PER)));
    const pagedTaskHours = computed(() => {
      const s = (hPage2.value - 1) * HB_PER;
      return taskHours.value.slice(s, s + HB_PER);
    });

    // 날짜별 시간
    const dateHours = computed(() => {
      const map = {};
      filteredEntries.value.forEach(e => { map[e.date] = (map[e.date] || 0) + Number(e.hours || 0); });
      return Object.entries(map).sort((a, b) => a[0].localeCompare(b[0]));
    });

    // 프로젝트별 총 시간
    const projectHours = computed(() => {
      const map = {};
      entries.value.forEach(e => { map[e.project] = (map[e.project] || 0) + Number(e.hours || 0); });
      return Object.entries(map);
    });

    /* ─── chart instances ─── */
    let chartInstances = {};

    function destroyChart(key) {
      if (chartInstances[key]) { chartInstances[key].destroy(); delete chartInstances[key]; }
    }

    const CHART_DEFAULTS = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
    };
    const AXIS_STYLE = {
      ticks: { color: "#94a3b8", font: { size: 10 } },
      grid:  { color: "rgba(0,0,0,0.05)" },
      border:{ display: false },
    };

    function buildBarChart() {
      destroyChart("bar1");
      if (!barChart1.value) return;
      const labels = projectHours.value.map(([p]) => p);
      const data   = projectHours.value.map(([, h]) => h);
      const colors = ["#3b82f6", "#94a3b8", "#cbd5e1"];
      chartInstances.bar1 = new Chart(barChart1.value, {
        type: "bar",
        data: {
          labels,
          datasets: [{ data, backgroundColor: labels.map((_, i) => colors[i] || "#cbd5e1"), borderRadius: 3, barThickness: 36 }],
        },
        options: {
          ...CHART_DEFAULTS,
          scales: {
            x: { ...AXIS_STYLE },
            y: { ...AXIS_STYLE, min: 0, ticks: { ...AXIS_STYLE.ticks, callback: v => v + "h" } },
          },
        },
      });
    }

    function buildHBar1() {
      destroyChart("hbar1");
      if (!hbarChart1.value) return;
      const data = pagedAssigneeHours.value;
      chartInstances.hbar1 = new Chart(hbarChart1.value, {
        type: "bar",
        data: {
          labels: data.map(([a]) => a),
          datasets: [{ data: data.map(([, h]) => h), backgroundColor: "#3b82f6", borderRadius: 3, barThickness: 16 }],
        },
        options: {
          ...CHART_DEFAULTS,
          indexAxis: "y",
          scales: {
            x: { ...AXIS_STYLE, min: 0, ticks: { ...AXIS_STYLE.ticks, callback: v => v + "h" } },
            y: { ...AXIS_STYLE, grid: { display: false } },
          },
        },
      });
    }

    function buildLineChart() {
      destroyChart("line");
      if (!lineChart.value) return;
      const data = dateHours.value;
      chartInstances.line = new Chart(lineChart.value, {
        type: "line",
        data: {
          labels: data.map(([d]) => d),
          datasets: [{
            data: data.map(([, h]) => h),
            borderColor: "#3b82f6",
            backgroundColor: "rgba(59,130,246,0.08)",
            pointBackgroundColor: "#3b82f6",
            pointRadius: 4,
            tension: 0.3,
            fill: true,
          }],
        },
        options: {
          ...CHART_DEFAULTS,
          scales: {
            x: { ...AXIS_STYLE },
            y: { ...AXIS_STYLE, min: 0, ticks: { ...AXIS_STYLE.ticks, callback: v => v + "h" } },
          },
        },
      });
    }

    function buildHBar2() {
      destroyChart("hbar2");
      if (!hbarChart2.value) return;
      const data = pagedTaskHours.value;
      chartInstances.hbar2 = new Chart(hbarChart2.value, {
        type: "bar",
        data: {
          labels: data.map(([n]) => n.length > 8 ? n.slice(0, 8) + "…" : n),
          datasets: [{ data: data.map(([, h]) => h), backgroundColor: "#6366f1", borderRadius: 3, barThickness: 16 }],
        },
        options: {
          ...CHART_DEFAULTS,
          indexAxis: "y",
          scales: {
            x: { ...AXIS_STYLE, min: 0, ticks: { ...AXIS_STYLE.ticks, callback: v => v + "h" } },
            y: { ...AXIS_STYLE, grid: { display: false } },
          },
        },
      });
    }

    function buildDonut() {
      destroyChart("donut");
      if (!donutChart.value) return;
      const data = donutData.value;
      chartInstances.donut = new Chart(donutChart.value, {
        type: "doughnut",
        data: {
          labels: data.map(d => d.label),
          datasets: [{
            data: data.map(d => d.val),
            backgroundColor: data.map(d => d.color),
            borderWidth: 2,
            borderColor: "#ffffff",
          }],
        },
        options: {
          ...CHART_DEFAULTS,
          cutout: "58%",
          plugins: {
            legend: { display: false },
            tooltip: { callbacks: { label: ctx => ` ${ctx.label}: ${ctx.parsed}h` } },
          },
        },
      });
    }

    function rebuildAllCharts() {
      nextTick(() => {
        buildBarChart();
        buildHBar1();
        buildLineChart();
        buildHBar2();
        buildDonut();
      });
    }

    /* ─── watchers ─── */
    watch(filteredEntries, () => { currentPage.value = 1; rebuildAllCharts(); });
    watch(hPage1, () => { nextTick(buildHBar1); });
    watch(hPage2, () => { nextTick(buildHBar2); });

    /* ─── lifecycle ─── */
    onMounted(rebuildAllCharts);

    /* ─── methods ─── */
    function selectProject(val) {
      selectedProject.value = val;
      const proj = projectOptions.find(p => p.value === val);
      filters.value.project = proj && val !== "" ? proj.label : "";
    }

    function resetFilter() {
      filters.value = { project:"", assignee:"", type:"", keyword:"", dateFrom:"2026-03-01", dateTo:"2026-06-30" };
      selectedProject.value = "";
      currentPage.value = 1;
    }

    function openDetail(entry) { selectedEntry.value = entry; }

    function editEntry(entry) {
      editingEntry.value = entry;
      form.value = { ...entry };
    }

    function deleteEntry(id) {
      if (confirm("정말 삭제하시겠습니까?")) {
        entries.value = entries.value.filter(e => e.id !== id);
      }
    }

    function saveEntry() {
      if (editingEntry.value) {
        const idx = entries.value.findIndex(e => e.id === editingEntry.value.id);
        if (idx !== -1) entries.value.splice(idx, 1, { ...editingEntry.value, ...form.value });
      } else {
        entries.value.push({ ...form.value, id: "e" + Date.now(), taskId: "", selected: false });
      }
      closeModal();
    }

    function closeModal() {
      showAddModal.value = false;
      editingEntry.value = null;
      form.value = { project:"", name:"", date:"", type:"", assignee:"", hours:"", memo:"" };
    }

    function prevPage() { if (currentPage.value > 1) currentPage.value--; }
    function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
    function exportExcel() { alert("Excel 다운로드"); }
    function exportPdf()   { alert("PDF 다운로드"); }

    watch(selectAll, val => { paginatedEntries.value.forEach(e => (e.selected = val)); });

    return {
      barChart1, hbarChart1, lineChart, hbarChart2, donutChart,
      sidebarOpen, entries, currentPage, selectAll, selectedEntry,
      showAddModal, editingEntry, form, filters, selectedProject,
      hPage1, hPage2, hTotalPages1, hTotalPages2,
      projectOptions, barLegend, donutData,
      filteredEntries, paginatedEntries, totalPages,
      totalHours, uniqueAssignees, uniqueProjects, periodLabel,
      selectProject, resetFilter, openDetail, editEntry,
      deleteEntry, saveEntry, closeModal, prevPage, nextPage,
      exportExcel, exportPdf,
    };
  },
});
</script>

<style scoped>
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

.page {
  padding: 24px 28px;
  color: #1e293b;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

/* ── 페이지 헤더 ── */
.page-header {
  display: flex; align-items: flex-end; justify-content: space-between;
  margin-bottom: 16px; padding-bottom: 14px;
  border-bottom: 1px solid #e2e8f0;
}
.page-header-left { display: flex; align-items: center; gap: 10px; }
.btn-back {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 5px 11px; font-size: 12px; font-weight: 500;
  background: #f1f5f9; color: #475569;
  border: 1px solid #e2e8f0; border-radius: 4px; cursor: pointer;
}
.btn-back:hover { background: #e2e8f0; }
.breadcrumb-sep { color: #cbd5e1; font-size: 14px; }
.page-title    { font-size: 18px; font-weight: 700; color: #0f172a; letter-spacing: -0.3px; }
.page-subtitle { font-size: 12px; color: #94a3b8; margin-top: 2px; }
.btn-add {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 7px 14px; font-size: 12px; font-weight: 600;
  background: #1e40af; color: #fff;
  border: none; border-radius: 4px; cursor: pointer;
}
.btn-add:hover { background: #1e3a8a; }

/* ── ① 프로젝트 헤더 바 ── */
.proj-header-bar {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; border: 1px solid #e2e8f0; border-radius: 6px;
  padding: 12px 18px; margin-bottom: 14px;
}
.proj-info { display: flex; align-items: center; gap: 14px; }
.proj-label { font-size: 12px; font-weight: 600; color: #64748b; white-space: nowrap; }
.proj-chips { display: flex; gap: 6px; flex-wrap: wrap; }
.proj-chip {
  padding: 4px 12px; font-size: 12px; font-weight: 500;
  background: #f8fafc; color: #64748b;
  border: 1px solid #e2e8f0; border-radius: 3px; cursor: pointer;
  transition: all .12s;
}
.proj-chip:hover { background: #eff6ff; color: #1d4ed8; border-color: #bfdbfe; }
.proj-chip.active { background: #1e40af; color: #fff; border-color: #1e40af; }
.period-wrap { display: flex; align-items: center; gap: 6px; }
.period-text { font-size: 13px; color: #475569; font-weight: 500; }

/* ── 대시보드 그리드 ── */
.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 14px;
  margin-bottom: 14px;
  align-items: start;
}
.charts-left { display: flex; flex-direction: column; gap: 14px; }
.chart-row   { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }

.chart-card {
  background: #fff; border: 1px solid #e2e8f0; border-radius: 6px;
  padding: 14px 16px;
}
.chart-card-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 10px;
}
.chart-card-title { font-size: 12px; font-weight: 600; color: #334155; }
.chart-unit       { font-size: 10px; color: #94a3b8; }

.chart-canvas-wrap { position: relative; height: 150px; }
.chart-canvas-wrap canvas { width: 100% !important; }

.chart-legend { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 8px; }
.legend-item  { display: flex; align-items: center; gap: 4px; font-size: 10px; color: #64748b; }
.legend-sq    { width: 8px; height: 8px; border-radius: 1px; flex-shrink: 0; }

.hbar-footer {
  display: flex; align-items: center; justify-content: space-between;
  margin-top: 8px; padding-top: 6px; border-top: 1px solid #f1f5f9;
}
.hbar-count { font-size: 10px; color: #94a3b8; }
.mini-pager { display: flex; gap: 3px; }
.mpg {
  width: 20px; height: 20px; display: flex; align-items: center; justify-content: center;
  font-size: 11px; border: 1px solid #e2e8f0; border-radius: 3px;
  background: #fff; color: #64748b; cursor: pointer;
}
.mpg:hover { background: #f1f5f9; }
.mpg.on    { background: #1e40af; color: #fff; border-color: #1e40af; }

/* ── ④ 도넛 ── */
.charts-right { display: flex; flex-direction: column; gap: 10px; }
.donut-card { display: flex; flex-direction: column; }
.donut-canvas-wrap { position: relative; height: 180px; margin: 4px 0; }
.donut-center {
  position: absolute; inset: 0;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  pointer-events: none;
}
.donut-total     { font-size: 18px; font-weight: 700; color: #0f172a; }
.donut-total-lbl { font-size: 10px; color: #94a3b8; }
.donut-legend { display: flex; flex-wrap: wrap; justify-content: center; gap: 8px; margin-top: 8px; }
.donut-leg-item { display: flex; align-items: center; gap: 4px; font-size: 11px; color: #64748b; }

/* 통계 그리드 */
.stat-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.stat-card {
  background: #fff; border: 1px solid #e2e8f0; border-radius: 6px;
  padding: 12px 14px; text-align: center;
}
.stat-val { font-size: 20px; font-weight: 700; color: #0f172a; letter-spacing: -0.3px; }
.stat-lbl { font-size: 10px; color: #94a3b8; margin-top: 2px; }

/* ── 테이블 카드 ── */
.table-card {
  background: #fff; border: 1px solid #e2e8f0; border-radius: 6px;
  overflow: hidden;
}
.table-topbar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 16px; gap: 10px; flex-wrap: wrap;
  border-bottom: 1px solid #e2e8f0; background: #f8fafc;
}
.filter-row { display: flex; align-items: center; gap: 6px; flex-wrap: wrap; }
.select-wrap select,
.search-wrap input,
.date-inp {
  height: 30px; padding: 0 8px; font-size: 12px;
  border: 1px solid #d1d5db; border-radius: 4px;
  background: #fff; color: #374151; outline: none;
}
.select-wrap select:focus,
.search-wrap input:focus,
.date-inp:focus { border-color: #3b82f6; }
.search-wrap { position: relative; display: flex; align-items: center; }
.search-wrap svg { position: absolute; left: 7px; pointer-events: none; }
.search-wrap input { padding-left: 26px; width: 130px; }
.date-row { display: flex; align-items: center; gap: 4px; }
.date-sep { font-size: 11px; color: #94a3b8; }
.btn-reset {
  height: 30px; padding: 0 12px; font-size: 12px;
  background: #f1f5f9; color: #64748b;
  border: 1px solid #e2e8f0; border-radius: 4px; cursor: pointer;
}
.btn-reset:hover { background: #e2e8f0; }
.btn-search {
  height: 30px; padding: 0 14px; font-size: 12px; font-weight: 600;
  background: #1e40af; color: #fff;
  border: none; border-radius: 4px; cursor: pointer;
}
.btn-search:hover { background: #1e3a8a; }

.topbar-right { display: flex; align-items: center; gap: 6px; }
.count-badge {
  padding: 2px 10px; font-size: 11px; font-weight: 600;
  background: #eff6ff; color: #1d4ed8;
  border: 1px solid #bfdbfe; border-radius: 999px;
}
.btn-export {
  display: inline-flex; align-items: center; gap: 4px;
  height: 28px; padding: 0 10px; font-size: 11px; font-weight: 600;
  border-radius: 4px; cursor: pointer;
}
.btn-excel { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
.btn-excel:hover { background: #dcfce7; }
.btn-pdf   { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.btn-pdf:hover   { background: #fee2e2; }

/* ── 테이블 ── */
.tbl-wrap { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; font-size: 12.5px; }
thead { background: #f8fafc; }
th {
  padding: 9px 14px; text-align: center;
  font-size: 11px; font-weight: 600; color: #64748b;
  border-bottom: 1px solid #e2e8f0; white-space: nowrap;
}
td {
  padding: 10px 14px; text-align: center;
  border-bottom: 1px solid #f1f5f9; color: #334155;
}
.data-row { cursor: pointer; transition: background .1s; }
.data-row:hover { background: #f8faff; }
.task-name-cell { text-align: left; font-weight: 500; color: #1e293b; }
.memo-cell { text-align: left; color: #94a3b8; font-size: 11px; max-width: 130px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.empty-cell { text-align: center; padding: 36px; color: #94a3b8; font-size: 13px; }

.badge { display: inline-block; padding: 2px 8px; border-radius: 3px; font-size: 10px; font-weight: 600; }
.badge--개발   { background: #eff6ff; color: #1d4ed8; }
.badge--기획   { background: #fdf4ff; color: #7e22ce; }
.badge--디자인 { background: #fff7ed; color: #c2410c; }
.badge--기타   { background: #f0fdf4; color: #15803d; }

.assignee-cell { display: inline-flex; align-items: center; gap: 6px; }
.avatar {
  width: 22px; height: 22px; border-radius: 50%;
  background: #1e40af; color: #fff;
  font-size: 10px; font-weight: 700;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.hours-val { font-weight: 700; color: #1d4ed8; }

.action-cell { display: flex; align-items: center; justify-content: center; gap: 3px; }
.btn-act {
  width: 26px; height: 26px; border-radius: 4px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border: 1px solid; transition: all .12s;
}
.btn-edit { background: #f0fdf4; color: #16a34a; border-color: #bbf7d0; }
.btn-edit:hover { background: #dcfce7; }
.btn-del  { background: #fef2f2; color: #dc2626; border-color: #fecaca; }
.btn-del:hover  { background: #fee2e2; }

.total-row td { background: #f8fafc; font-weight: 600; border-top: 2px solid #e2e8f0; border-bottom: none; }
.total-label  { text-align: right; font-size: 12px; color: #64748b; }
.total-value  { color: #1d4ed8; font-size: 14px; font-weight: 700; }

.cb { width: 14px; height: 14px; cursor: pointer; accent-color: #1d4ed8; }

/* ── 페이저 ── */
.pager { display: flex; justify-content: center; gap: 4px; padding: 12px 0; }
.pg {
  width: 28px; height: 28px; display: flex; align-items: center; justify-content: center;
  font-size: 12px; border: 1px solid #e2e8f0; border-radius: 4px;
  background: #fff; color: #64748b; cursor: pointer;
}
.pg:hover { background: #f1f5f9; }
.pg.on    { background: #1e40af; color: #fff; border-color: #1e40af; font-weight: 700; }

/* ── 상세 패널 ── */
.detail-overlay {
  position: fixed; inset: 0; background: rgba(15,23,42,.3); z-index: 999;
  display: flex; justify-content: flex-end;
}
.detail-panel {
  width: 360px; height: 100%; background: #fff;
  box-shadow: -4px 0 20px rgba(0,0,0,.1);
  display: flex; flex-direction: column;
}
.detail-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 22px; border-bottom: 1px solid #e2e8f0;
}
.detail-title { font-size: 15px; font-weight: 700; color: #0f172a; }
.icon-btn {
  width: 26px; height: 26px; display: flex; align-items: center; justify-content: center;
  background: #f1f5f9; border: none; border-radius: 4px; cursor: pointer; color: #64748b; font-size: 12px;
}
.icon-btn:hover { background: #e2e8f0; }
.detail-body { padding: 20px 22px; flex: 1; overflow-y: auto; }
.detail-rows { display: flex; flex-direction: column; gap: 14px; }
.detail-row  { display: flex; align-items: center; justify-content: space-between; }
.dk { font-size: 11px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: .04em; }
.dv { font-size: 13px; color: #1e293b; font-weight: 500; }
.detail-divider { height: 1px; background: #e2e8f0; margin: 18px 0; }
.detail-memo-block { display: flex; flex-direction: column; gap: 7px; }
.detail-memo {
  font-size: 12px; color: #475569; line-height: 1.6;
  background: #f8fafc; padding: 10px 12px; border-radius: 4px;
  border: 1px solid #e2e8f0;
}
.detail-actions { display: flex; gap: 8px; margin-top: 20px; }
.btn-detail-edit, .btn-detail-del {
  flex: 1; display: inline-flex; align-items: center; justify-content: center;
  padding: 8px 0; font-size: 12px; font-weight: 600;
  border-radius: 4px; cursor: pointer;
}
.btn-detail-edit { background: #eff6ff; color: #1d4ed8; border: 1px solid #bfdbfe; }
.btn-detail-edit:hover { background: #dbeafe; }
.btn-detail-del  { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.btn-detail-del:hover  { background: #fee2e2; }

/* ── 모달 ── */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(15,23,42,.4); z-index: 1000;
  display: flex; align-items: center; justify-content: center;
}
.modal {
  width: 520px; background: #fff; border-radius: 8px;
  box-shadow: 0 16px 48px rgba(0,0,0,.14); overflow: hidden;
}
.modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 18px 22px; border-bottom: 1px solid #e2e8f0;
}
.modal-title { font-size: 15px; font-weight: 700; color: #0f172a; }
.modal-body  { padding: 22px; }
.form-grid   { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }
.form-group  { display: flex; flex-direction: column; gap: 5px; }
.form-group--full { grid-column: 1 / -1; }
.form-label  { font-size: 11px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: .04em; }
.form-input, .form-select {
  padding: 8px 10px; border: 1px solid #d1d5db; border-radius: 4px;
  font-size: 12px; color: #374151; background: #f9fafb; outline: none; width: 100%;
}
.form-input:focus, .form-select:focus { border-color: #3b82f6; background: #fff; }
.form-textarea {
  padding: 8px 10px; border: 1px solid #d1d5db; border-radius: 4px;
  font-size: 12px; color: #374151; background: #f9fafb; outline: none; width: 100%;
  resize: vertical; font-family: inherit;
}
.form-textarea:focus { border-color: #3b82f6; background: #fff; }
.modal-footer {
  display: flex; justify-content: flex-end; gap: 8px;
  padding: 14px 22px; border-top: 1px solid #e2e8f0; background: #f8fafc;
}
.btn-cancel {
  padding: 7px 18px; font-size: 12px; font-weight: 600;
  background: #f1f5f9; color: #64748b;
  border: 1px solid #e2e8f0; border-radius: 4px; cursor: pointer;
}
.btn-cancel:hover { background: #e2e8f0; }
.btn-save {
  padding: 7px 22px; font-size: 12px; font-weight: 600;
  background: #1e40af; color: #fff;
  border: none; border-radius: 4px; cursor: pointer;
}
.btn-save:hover { background: #1e3a8a; }

/* ── 트랜지션 ── */
.slide-enter-active .detail-panel,
.slide-leave-active .detail-panel { transition: transform .22s cubic-bezier(.4,0,.2,1); }
.slide-enter-active, .slide-leave-active { transition: opacity .22s; }
.slide-enter-from, .slide-leave-to { opacity: 0; }
.slide-enter-from .detail-panel { transform: translateX(100%); }
.slide-leave-to   .detail-panel { transform: translateX(100%); }

.fade-enter-active, .fade-leave-active { transition: opacity .18s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>