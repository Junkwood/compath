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

            <div class="fg">
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
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5z"/>
                </svg>
                Excel
              </button>
              <button class="btn-export btn-export--pdf" @click="exportPdf">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5z"/>
                </svg>
                PDF
              </button>
            </div>
          </div>

          <div class="tbl-wrap">
            <table>
              <thead>
                <tr>
                  <th style="width:44px">
                    <input type="checkbox" v-model="selectAll" class="cb" />
                  </th>
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
                  <tr
                    v-for="item in paginatedList"
                    :key="item.taskId"
                    class="data-row"
                  >
                    <td @click.stop>
                      <input v-model="item.selected" type="checkbox" class="cb" />
                    </td>
                    <td>{{ item.projectName }}</td>
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
                      <router-link
                        :to="{ name: 'TimeEntries', params: { taskId: item.taskId } }"
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
            <div
              v-for="p in totalPages" :key="p"
              class="pg" :class="{ on: p === currentPage }"
              @click="goToPage(p)"
            >{{ p }}</div>
            <div class="pg" @click="nextPage">›</div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import api from "../utils/api.js";
import Sidebar from "../partials/Sidebar.vue";
import Header  from "../partials/Header.vue";

export default {
  name: "TimeReport",
  components: { Sidebar, Header },
  data() {
    return {
      sidebarOpen: false,
      loading: false,
      currentPage: 1,
      itemsPerPage: 10,
      selectAll: false,

      reportList:     [],
      assigneeList:   [],
      taskTypeList:   [],
      subProjectList: [],

      rootProjectId: null,

      filters: {
        projectId:      "",
        assigneeUserId: "",
        taskTypeId:     "",
        keyword:        "",
        dateFrom:       "2026-03-01",
        dateTo:         "2026-06-30",
      },
    };
  },

  computed: {
    paginatedList() {
      const s = (this.currentPage - 1) * this.itemsPerPage;
      return this.reportList.slice(s, s + this.itemsPerPage);
    },
    totalPages() {
      return Math.ceil(this.reportList.length / this.itemsPerPage) || 1;
    },
    // 전체 totalHours 합산
    totalHours() {
      return this.reportList.reduce((sum, t) => sum + (t.totalHours || 0), 0);
    },
  },

  watch: {
    selectAll(val) {
      this.paginatedList.forEach(t => (t.selected = val));
    },
  },

  mounted() {
    this.rootProjectId = this.$route.params.projectId
      ? Number(this.$route.params.projectId)
      : null;

    this.fetchSubProjectList();
    this.fetchReport();
  },

  methods: {
    async fetchSubProjectList() {
      try {
        const { data } = await api.get("/project/list");
        const all = (data ?? []).map(p => ({
          projectId:       Number(p.projectId       ?? p.PROJECT_ID),
          projectName:     p.projectName     ?? p.PROJECT_NAME,
          parentProjectId: (p.parentProjectId ?? p.PARENT_PROJECT_ID)
            ? Number(p.parentProjectId ?? p.PARENT_PROJECT_ID)
            : null,
        }));
        this.subProjectList = all.filter(p => p.parentProjectId === this.rootProjectId);
      } catch (e) {
        console.error("프로젝트 목록 조회 실패", e);
      }
    },

    async fetchReport() {
      this.loading = true;
      try {
        const { data } = await api.get("/report/time-entry", {
          params: {
            projectId:      this.filters.projectId || this.rootProjectId || null,
            assigneeUserId: this.filters.assigneeUserId || null,
            taskTypeId:     this.filters.taskTypeId     || null,
            keyword:        this.filters.keyword        || null,
            dateFrom:       this.filters.dateFrom       || null,
            dateTo:         this.filters.dateTo         || null,
          },
        });

        this.reportList   = data.reportList   ?? [];
        this.assigneeList = data.assigneeList ?? [];
        this.taskTypeList = data.taskTypeList ?? [];
        this.currentPage  = 1;

      } catch (e) {
        console.error("타임 리포트 조회 실패", e);
      } finally {
        this.loading = false;
      }
    },

    resetFilter() {
      this.filters = {
        projectId:      "",
        assigneeUserId: "",
        taskTypeId:     "",
        keyword:        "",
        dateFrom:       "2026-03-01",
        dateTo:         "2026-06-30",
      };
      this.fetchReport();
    },

    formatDate(val) {
      if (!val) return "-";
      return String(val).substring(0, 10).replace(/-/g, ".");
    },

    exportExcel() { alert("Excel 다운로드"); },
    exportPdf()   { alert("PDF 다운로드"); },
    goToPage(p)   { this.currentPage = p; },
    prevPage()    { if (this.currentPage > 1) this.currentPage--; },
    nextPage()    { if (this.currentPage < this.totalPages) this.currentPage++; },
  },
};
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; font-family: 'Pretendard', 'Noto Sans KR', sans-serif; }

.page { padding: 28px 32px; color: #1e293b; }

.page-header {
  display: flex; align-items: flex-end; justify-content: space-between;
  margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #e2e8f0;
}
.page-title   { font-size: 22px; font-weight: 700; color: #0f172a; letter-spacing: -0.3px; }
.page-subtitle { font-size: 13px; color: #94a3b8; margin-top: 3px; }

.filter-card {
  background: #fff; border: 1px solid #e2e8f0; border-radius: 12px;
  padding: 18px 20px; margin-bottom: 16px; box-shadow: 0 1px 4px rgba(0,0,0,.05);
}
.filter-row { display: flex; align-items: flex-end; gap: 12px; flex-wrap: wrap; }
.fg { display: flex; flex-direction: column; gap: 5px; flex: 1; min-width: 120px; }
.fg--wide { flex: 2; min-width: 180px; }
.filter-label { font-size: 11px; font-weight: 600; color: #64748b; letter-spacing: .05em; text-transform: uppercase; }
.select-wrap { position: relative; }
.select-wrap select {
  appearance: none; width: 100%; padding: 8px 28px 8px 10px;
  border: 1px solid #cbd5e1; border-radius: 8px; font-size: 13px;
  color: #334155; background: #f8fafc; cursor: pointer; outline: none;
  transition: border-color .15s, box-shadow .15s;
}
.select-wrap select:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,.1); background: #fff; }
.select-arrow { position: absolute; right: 9px; top: 50%; transform: translateY(-50%); color: #94a3b8; font-size: 11px; pointer-events: none; }
.filter-input {
  padding: 8px 10px; border: 1px solid #cbd5e1; border-radius: 8px;
  font-size: 13px; color: #334155; background: #f8fafc; outline: none;
  width: 100%; transition: border-color .15s, box-shadow .15s;
}
.filter-input:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,.1); background: #fff; }
.search-wrap { position: relative; display: flex; align-items: center; }
.search-icon { position: absolute; left: 10px; width: 15px; height: 15px; pointer-events: none; }
.search-input {
  width: 100%; padding: 8px 10px 8px 30px; border: 1px solid #cbd5e1; border-radius: 8px;
  font-size: 13px; color: #334155; background: #f8fafc; outline: none;
  transition: border-color .15s, box-shadow .15s;
}
.search-input:focus { border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59,130,246,.1); background: #fff; }
.date-range { display: flex; align-items: center; gap: 6px; }
.date-sep { font-size: 13px; color: #94a3b8; }
.filter-actions { display: flex; gap: 8px; align-items: flex-end; }
.btn-reset {
  padding: 8px 14px; background: #f1f5f9; color: #64748b; font-size: 13px;
  font-weight: 600; border-radius: 8px; border: 1px solid #e2e8f0; cursor: pointer;
  transition: all .15s; white-space: nowrap;
}
.btn-reset:hover { background: #e2e8f0; color: #334155; }
.btn-search {
  padding: 8px 18px; background: #1e40af; color: #fff; font-size: 13px;
  font-weight: 600; border-radius: 8px; border: none; cursor: pointer;
  transition: background .15s; white-space: nowrap;
}
.btn-search:hover { background: #1e3a8a; }

.list-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 4px rgba(0,0,0,.05); }
.list-topbar { display: flex; align-items: center; justify-content: space-between; padding: 14px 20px 10px; }
.count-badge { display: inline-flex; align-items: center; padding: 3px 12px; background: #eff6ff; color: #1d4ed8; font-size: 12px; font-weight: 700; border-radius: 999px; border: 1px solid #bfdbfe; }
.export-group { display: flex; gap: 8px; }
.btn-export { display: inline-flex; align-items: center; gap: 5px; padding: 6px 14px; font-size: 12px; font-weight: 600; border-radius: 7px; cursor: pointer; transition: all .15s; }
.btn-export--excel { background: #f0fdf4; color: #16a34a; border: 1px solid #bbf7d0; }
.btn-export--excel:hover { background: #dcfce7; }
.btn-export--pdf { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
.btn-export--pdf:hover { background: #fee2e2; }

.tbl-wrap { border-top: 1px solid #e2e8f0; overflow: hidden; }
table { width: 100%; border-collapse: collapse; font-size: 13.5px; }
thead { background: #f8fafc; }
th { padding: 11px 16px; text-align: center; font-size: 11px; font-weight: 700; color: #64748b; letter-spacing: .04em; text-transform: uppercase; border-bottom: 1px solid #e2e8f0; }
td { padding: 13px 16px; text-align: center; border-bottom: 1px solid #f1f5f9; color: #334155; }
.data-row { transition: background .1s; }
.data-row:hover { background: #f8faff; }
.task-name { text-align: left; font-weight: 500; color: #1e293b; }
.empty-cell { text-align: center; padding: 40px; color: #94a3b8; font-size: 14px; }

.badge { display: inline-block; padding: 3px 10px; border-radius: 999px; font-size: 11px; font-weight: 600; }
.badge--개발   { background: #eff6ff; color: #1d4ed8; }
.badge--기획   { background: #fdf4ff; color: #7e22ce; }
.badge--디자인 { background: #fff7ed; color: #c2410c; }
.badge--기타   { background: #f0fdf4; color: #15803d; }

.assignee-cell { display: inline-flex; align-items: center; gap: 7px; }
.avatar {
  width: 24px; height: 24px; border-radius: 50%;
  background: linear-gradient(135deg, #1d4ed8, #7c3aed);
  color: #fff; font-size: 11px; font-weight: 700;
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}

.time-link {
  display: inline-flex; align-items: center; gap: 5px;
  color: #1d4ed8; font-weight: 700; font-size: 13px;
  text-decoration: none; padding: 4px 10px;
  background: #eff6ff; border: 1px solid #bfdbfe;
  border-radius: 7px; transition: all .15s;
}
.time-link:hover { background: #dbeafe; color: #1e3a8a; transform: translateX(2px); }

.total-row td { background: #f8fafc; font-weight: 600; border-top: 2px solid #e2e8f0; border-bottom: none; }
.total-label { text-align: right; font-size: 13px; color: #64748b; }
.total-value { color: #1d4ed8; font-size: 15px; font-weight: 700; }

.pager { display: flex; justify-content: center; align-items: center; gap: 5px; padding: 16px 0; }
.pg { width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; border: 1px solid #e2e8f0; border-radius: 8px; cursor: pointer; background: #fff; color: #64748b; font-size: 13px; transition: all .15s; }
.pg:hover { background: #f1f5f9; color: #334155; }
.pg.on { background: #1e40af; color: #fff; border-color: #1e40af; font-weight: 700; }

.cb { width: 15px; height: 15px; cursor: pointer; accent-color: #1d4ed8; }
</style>