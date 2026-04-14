<template>
  <div class="flex h-screen overflow-hidden bg-gray-50 dark:bg-gray-900">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <Header :sidebarOpen="sidebarOpen" @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <main class="page">
        <div class="page-header">
          <div>
            <h1 class="page-title">타임 리포트</h1>
            <p class="page-subtitle">프로젝트별 소요 시간을 확인하세요</p>
          </div>
        </div>

        <!-- 필터 바 -->
        <div class="filter-card">
          <div class="filter-row">
            <div class="fg">
              <label class="filter-label">하위 프로젝트</label>
              <div class="select-wrap">
                <select v-model="filters.projectId">
                  <option value="">전체</option>
                  <option v-for="p in subProjectList" :key="p.projectId" :value="p.projectId">
                    {{ p.projectName }}
                  </option>
                </select>
                <span class="select-arrow">▾</span>
              </div>
            </div>

            <div class="fg">
              <label class="filter-label">담당자 이름</label>
              <div class="select-wrap">
                <select v-model="filters.assigneeUserId">
                  <option value="">전체</option>
                  <option v-for="u in assigneeList" :key="u.userId" :value="u.userId">
                    {{ u.userName }}
                  </option>
                </select>
                <span class="select-arrow">▾</span>
              </div>
            </div>

            <div class="fg">
              <label class="filter-label">업무 유형</label>
              <div class="select-wrap">
                <select v-model="filters.taskTypeId">
                  <option value="">전체</option>
                  <option v-for="t in taskTypeList" :key="t.taskTypeId" :value="t.taskTypeId">
                    {{ t.typeName }}
                  </option>
                </select>
                <span class="select-arrow">▾</span>
              </div>
            </div>

            <div class="fg fg--wide">
              <label class="filter-label">업무명</label>
              <div class="search-wrap">
                <svg class="search-icon" viewBox="0 0 20 20" fill="none">
                  <circle cx="9" cy="9" r="6" stroke="#9ca3af" stroke-width="1.8" />
                  <path d="M14 14l3 3" stroke="#9ca3af" stroke-width="1.8" stroke-linecap="round" />
                </svg>
                <input v-model="filters.keyword" type="text" placeholder="검색어 입력" class="search-input" />
              </div>
            </div>

            <div class="fg fg--date">
              <label class="filter-label">작업일</label>
              <div class="date-range">
                <input v-model="filters.dateFrom" type="date" class="filter-input" />
                <span class="date-sep">~</span>
                <input v-model="filters.dateTo" type="date" class="filter-input" />
              </div>
            </div>

            <div class="filter-actions">
              <button class="btn-reset" @click="resetFilter">초기화</button>
              <button class="btn-search" @click="fetchReport">검색</button>
            </div>
          </div>
        </div>

        <!-- 목록 카드 -->
        <div class="list-card">
          <div class="list-topbar">
            <span class="count-badge" v-if="reportList.length > 0">총 {{ reportList.length }}건</span>
            <span v-else></span>
            <div class="export-group">
              <button class="btn-export btn-export--excel" @click="exportExcel">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5zM8.5 17l-1.5-2.5L5.5 17H4l2.2-3.3L4.1 10.5h1.5l1.4 2.3 1.4-2.3H10L8 13.7 10.1 17H8.5zm5.5 0h-1.3l-2.5-6.5H11l1.8 4.8 1.8-4.8H16L13.5 17h.5z" />
                </svg>
                Excel
              </button>
              <button class="btn-export btn-export--pdf" @click="exportPdf">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5zM7 15.5c0 .8-.6 1.5-1.5 1.5H5v1.5H3.5V12H5.5c.9 0 1.5.7 1.5 1.5v2zm4.5 1c0 .8-.6 1.5-1.5 1.5H8V12h2c.9 0 1.5.7 1.5 1.5v3zm4-3H14v1h1.5v1H14v1.5H12.5V12H15.5v1.5z" />
                  <path d="M5 13.5v2h.5v-2H5zm3 0v3h.5v-3H8z" />
                </svg>
                PDF
              </button>
            </div>
          </div>

          <div class="tbl-wrap">
            <table>
              <thead>
                <tr>
                  <th style="width:44px"><input type="checkbox" v-model="selectAll" class="cb" /></th>
                  <th>프로젝트 명</th>
                  <th>업무명</th>
                  <th>최근 작업일</th>
                  <th>업무유형</th>
                  <th>담당자</th>
                  <th>총 소요시간</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading">
                  <td colspan="7" class="empty-cell">⌛ 로딩중...</td>
                </tr>
                <tr v-else-if="paginatedList.length === 0">
                  <td colspan="7" class="empty-cell">등록된 데이터가 없습니다.</td>
                </tr>
                <template v-else>
                  <tr v-for="item in paginatedList" :key="item.taskId" class="data-row">
                    <td @click.stop>
                      <input v-model="item.selected" type="checkbox" class="cb" />
                    </td>
                    <td class="project-cell">
                      <span class="project-main">{{ item.parentProjectName ?? item.projectName }}</span>
                      <span v-if="item.subProjectName" class="project-sub">└ {{ item.subProjectName }}</span>
                    </td>
                    <td class="task-name">{{ item.title }}</td>
                    <td>{{ formatDate(item.lastWorkDate) }}</td>
                    <td>
                      <span class="badge" :class="`badge--${item.typeName}`">{{ item.typeName }}</span>
                    </td>
                    <td>
                      <div class="assignee-cell">
                        <div class="avatar">{{ item.userName?.[0] }}</div>
                        {{ item.userName }}
                      </div>
                    </td>
                    <td>
                      <!--
                        rootProjectId를 params로 넘김
                        → TimeEntries에서 goBack() 시 이 값으로 돌아옴
                      -->
                      <router-link
                        :to="{
                          name: 'TimeEntries',
                          params: {
                            taskId:    item.taskId,
                            projectId: rootProjectId,
                          },
                        }"
                        class="time-link"
                      >
                        {{ item.totalHours }}시간
                      </router-link>
                    </td>
                  </tr>
                </template>
                <tr class="total-row">
                  <td colspan="5"></td>
                  <td class="total-label">합계</td>
                  <td class="total-value">{{ totalHours }}시간</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="pager">
            <div class="pg" @click="prevPage">‹</div>
            <div v-for="p in totalPages" :key="p" class="pg" :class="{ on: p === currentPage }" @click="goToPage(p)">{{ p }}</div>
            <div class="pg" @click="nextPage">›</div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { storeToRefs } from 'pinia';
import Sidebar from '../partials/Sidebar.vue';
import Header from '../partials/Header.vue';
import { useTimeReportStore } from '../stores/useTimeReportStore';
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import NanumGothicBase64 from '../assets/fonts/NanumGothic.js';

export default {
  name: 'TimeReport',
  components: { Sidebar, Header },
  data() {
    return { sidebarOpen: false };
  },
  setup() {
    const store = useTimeReportStore();
    const {
      loading, currentPage, totalPages, selectAll,
      reportList, assigneeList, taskTypeList, subProjectList,
      filters, paginatedList, totalHours, rootProjectId,
    } = storeToRefs(store);

    return {
      loading, currentPage, totalPages, selectAll,
      reportList, assigneeList, taskTypeList, subProjectList,
      filters, paginatedList, totalHours, rootProjectId,
      fetchReport:  store.fetchReport,
      resetFilter:  store.resetFilter,
      formatDate:   store.formatDate,
      goToPage:     store.goToPage,
      prevPage:     store.prevPage,
      nextPage:     store.nextPage,
    };
  },
  watch: {
    selectAll(val) {
      this.paginatedList.forEach(t => (t.selected = val));
    },
  },
  mounted() {
    const store = useTimeReportStore();
    // $route.params.projectId: 첫 진입 시 상위 라우트에서 받거나,
    // TimeEntries goBack() 으로 돌아올 때도 동일한 projectId가 넘어옴
    store.init(this.$route.params.projectId);
  },
  methods: {
    getExportData() {
      const checked = this.reportList.filter(t => t.selected);
      return checked.length > 0 ? checked : this.reportList;
    },
    exportExcel() {
      const rows = this.getExportData().map(item => ({
        프로젝트명:      item.parentProjectName ?? item.projectName,
        하위프로젝트:    item.subProjectName ?? '-',
        업무명:          item.title,
        최근작업일:      this.formatDate(item.lastWorkDate),
        업무유형:        item.typeName,
        담당자:          item.userName,
        '총소요시간(h)': item.totalHours,
      }));
      const ws = XLSX.utils.json_to_sheet(rows);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, '타임리포트');
      XLSX.writeFile(wb, '타임리포트.xlsx');
    },
    exportPdf() {
      const doc = new jsPDF({ orientation: 'landscape' });
      doc.addFileToVFS('NanumGothic.ttf', NanumGothicBase64);
      doc.addFont('NanumGothic.ttf', 'NanumGothic', 'normal');
      doc.setFont('NanumGothic', 'normal');
      doc.setFontSize(14);
      doc.text('타임 리포트', 14, 15);
      const headers = [['프로젝트명','하위프로젝트','업무명','최근작업일','업무유형','담당자','총소요시간']];
      const body = this.getExportData().map(item => [
        item.parentProjectName ?? item.projectName,
        item.subProjectName ?? '-',
        item.title,
        this.formatDate(item.lastWorkDate),
        item.typeName,
        item.userName,
        `${item.totalHours}시간`,
      ]);
      autoTable(doc, {
        head: headers, body, startY: 20,
        styles: { fontSize: 9, font: 'NanumGothic' },
        headStyles: { fillColor: [30, 64, 175], font: 'NanumGothic', fontStyle: 'normal' },
        didParseCell: data => { data.cell.styles.font = 'NanumGothic'; },
      });
      doc.setFontSize(11);
      doc.text(`합계: ${this.totalHours}시간`, 14, doc.lastAutoTable.finalY + 8);
      doc.save('타임리포트.pdf');
    },
  },
};
</script>

<style scoped>
* { box-sizing:border-box; margin:0; padding:0; font-family:'Pretendard','Noto Sans KR',sans-serif; }
.page { padding:28px 32px; color:#1e293b; }
.page-header { display:flex; align-items:flex-end; justify-content:space-between; margin-bottom:20px; padding-bottom:16px; border-bottom:1px solid #e2e8f0; }
.page-title { font-size:22px; font-weight:700; color:#0f172a; letter-spacing:-0.3px; }
.page-subtitle { font-size:13px; color:#94a3b8; margin-top:3px; }

.filter-card { background:#fff; border:1px solid #e2e8f0; border-radius:12px; padding:16px 20px; margin-bottom:16px; box-shadow:0 1px 4px rgba(0,0,0,0.05); }
.filter-row { display:flex; align-items:flex-end; gap:10px; flex-wrap:nowrap; }
.fg { display:flex; flex-direction:column; gap:5px; flex:1; min-width:110px; }
.fg--wide { flex:2.5; min-width:180px; }
.fg--date { flex:2; min-width:230px; }
.filter-label { font-size:11px; font-weight:600; color:#64748b; letter-spacing:0.05em; text-transform:uppercase; white-space:nowrap; }
.select-wrap { position:relative; }
.select-wrap select { appearance:none; width:100%; padding:8px 26px 8px 10px; border:1px solid #cbd5e1; border-radius:8px; font-size:13px; color:#334155; background:#f8fafc; cursor:pointer; outline:none; transition:border-color 0.15s,box-shadow 0.15s; }
.select-wrap select:focus { border-color:#3b82f6; box-shadow:0 0 0 3px rgba(59,130,246,0.1); background:#fff; }
.select-arrow { position:absolute; right:8px; top:50%; transform:translateY(-50%); color:#94a3b8; font-size:11px; pointer-events:none; }
.date-range { display:flex; align-items:center; gap:6px; }
.date-sep { font-size:13px; color:#94a3b8; flex-shrink:0; }
.filter-input { padding:8px; border:1px solid #cbd5e1; border-radius:8px; font-size:13px; color:#334155; background:#f8fafc; outline:none; width:100%; transition:border-color 0.15s,box-shadow 0.15s; }
.filter-input:focus { border-color:#3b82f6; box-shadow:0 0 0 3px rgba(59,130,246,0.1); background:#fff; }
.search-wrap { position:relative; display:flex; align-items:center; }
.search-icon { position:absolute; left:9px; width:15px; height:15px; pointer-events:none; }
.search-input { width:100%; padding:8px 10px 8px 30px; border:1px solid #cbd5e1; border-radius:8px; font-size:13px; color:#334155; background:#f8fafc; outline:none; transition:border-color 0.15s,box-shadow 0.15s; }
.search-input:focus { border-color:#3b82f6; box-shadow:0 0 0 3px rgba(59,130,246,0.1); background:#fff; }
.filter-actions { display:flex; gap:8px; align-items:flex-end; flex-shrink:0; }
.btn-reset { padding:8px 14px; background:#f1f5f9; color:#64748b; font-size:13px; font-weight:600; border-radius:8px; border:1px solid #e2e8f0; cursor:pointer; white-space:nowrap; transition:all 0.15s; }
.btn-reset:hover { background:#e2e8f0; color:#334155; }
.btn-search { padding:8px 18px; background:#1e40af; color:#fff; font-size:13px; font-weight:600; border-radius:8px; border:none; cursor:pointer; white-space:nowrap; transition:background 0.15s; }
.btn-search:hover { background:#1e3a8a; }

.list-card { background:#fff; border:1px solid #e2e8f0; border-radius:12px; overflow:hidden; box-shadow:0 1px 4px rgba(0,0,0,0.05); }
.list-topbar { display:flex; align-items:center; justify-content:space-between; padding:14px 20px 10px; }
.count-badge { display:inline-flex; align-items:center; padding:3px 12px; background:#eff6ff; color:#1d4ed8; font-size:12px; font-weight:700; border-radius:999px; border:1px solid #bfdbfe; }
.export-group { display:flex; gap:8px; }
.btn-export { display:inline-flex; align-items:center; gap:5px; padding:6px 14px; font-size:12px; font-weight:600; border-radius:7px; cursor:pointer; transition:all 0.15s; }
.btn-export--excel { background:#f0fdf4; color:#16a34a; border:1px solid #bbf7d0; }
.btn-export--excel:hover { background:#dcfce7; }
.btn-export--pdf { background:#fef2f2; color:#dc2626; border:1px solid #fecaca; }
.btn-export--pdf:hover { background:#fee2e2; }

.tbl-wrap { border-top:1px solid #e2e8f0; overflow-x:auto; }
table { width:100%; border-collapse:collapse; font-size:13.5px; }
thead { background:#f8fafc; }
th { padding:11px 16px; text-align:center; font-size:11px; font-weight:700; color:#64748b; letter-spacing:0.04em; text-transform:uppercase; border-bottom:1px solid #e2e8f0; white-space:nowrap; }
td { padding:13px 16px; text-align:center; border-bottom:1px solid #f1f5f9; color:#334155; }
.data-row { transition:background 0.1s; }
.data-row:hover { background:#f8faff; }
.project-cell { display:flex; flex-direction:column; align-items:center; gap:2px; }
.project-main { font-size:13px; font-weight:500; color:#1e293b; }
.project-sub { font-size:11px; color:#94a3b8; }
.task-name { text-align:left; font-weight:500; color:#1e293b; }
.empty-cell { text-align:center; padding:40px; color:#94a3b8; font-size:14px; }
.badge { display:inline-block; padding:3px 10px; border-radius:999px; font-size:11px; font-weight:600; }
.badge--개발 { background:#eff6ff; color:#1d4ed8; }
.badge--기획 { background:#fdf4ff; color:#7e22ce; }
.badge--디자인 { background:#fff7ed; color:#c2410c; }
.badge--기타 { background:#f0fdf4; color:#15803d; }
.assignee-cell { display:inline-flex; align-items:center; gap:7px; }
.avatar { width:26px; height:26px; border-radius:50%; background:linear-gradient(135deg,#1d4ed8,#7c3aed); color:#fff; font-size:11px; font-weight:700; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.time-link { display:inline-flex; align-items:center; gap:5px; color:#1d4ed8; font-weight:700; font-size:13px; text-decoration:none; padding:4px 10px; background:#eff6ff; border:1px solid #bfdbfe; border-radius:7px; transition:all 0.15s; }
.time-link:hover { background:#dbeafe; color:#1e3a8a; transform:translateX(2px); }
.total-row td { background:#f8fafc; font-weight:600; border-top:2px solid #e2e8f0; border-bottom:none; }
.total-label { text-align:right; font-size:13px; color:#64748b; }
.total-value { color:#1d4ed8; font-size:15px; font-weight:700; }

.pager { display:flex; justify-content:center; align-items:center; gap:5px; padding:16px 0; }
.pg { width:32px; height:32px; display:flex; align-items:center; justify-content:center; border:1px solid #e2e8f0; border-radius:8px; cursor:pointer; background:#fff; color:#64748b; font-size:13px; transition:all 0.15s; }
.pg:hover { background:#f1f5f9; color:#334155; }
.pg.on { background:#1e40af; color:#fff; border-color:#1e40af; font-weight:700; }
.cb { width:15px; height:15px; cursor:pointer; accent-color:#1d4ed8; }
</style>