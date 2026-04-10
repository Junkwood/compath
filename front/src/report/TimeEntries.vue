<template>
  <div class="page">
    <!-- 필터 바 -->
    <div class="filter-bar">
      <div class="fg">
        <label>프로젝트 제목</label>
        <select v-model="filters.project">
          <option value="">전체</option>
          <option value="프로젝트 A">프로젝트 A</option>
          <option value="프로젝트 B">프로젝트 B</option>
          <option value="프로젝트 C">프로젝트 C</option>
        </select>
      </div>
      <div class="fg">
        <label>담당자 이름</label>
        <select v-model="filters.assignee">
          <option value="">전체</option>
          <option value="김개발">김개발</option>
          <option value="이개발">이개발</option>
          <option value="최개발">최개발</option>
        </select>
      </div>
      <div class="fg">
        <label>업무 유형</label>
        <select v-model="filters.type">
          <option value="">전체</option>
          <option value="개발">개발</option>
          <option value="기획">기획</option>
          <option value="디자인">디자인</option>
          <option value="기타">기타</option>
        </select>
      </div>
      <div class="fg">
        <label>업무명</label>
        <input
          v-model="filters.keyword"
          type="text"
          placeholder="검색어 입력"
        />
      </div>
      <div class="fg">
        <label>등록일</label>
        <div class="date-range">
          <input v-model="filters.dateFrom" type="date" />
          <span>~</span>
          <input v-model="filters.dateTo" type="date" />
        </div>
      </div>
      <button class="btn" @click="applyFilter">검색</button>
    </div>

    <!-- 다운로드 버튼 -->
    <div class="top-bar">
      <button class="btn-xl btn-excel" @click="exportExcel">Excel ↓</button>
      <button class="btn-xl btn-pdf" @click="exportPdf">PDF ↓</button>
    </div>

    <!-- 테이블 -->
    <div class="tbl-wrap">
      <table>
        <thead>
          <tr>
            <th style="width: 44px">
              <input type="checkbox" v-model="selectAll" class="cb" />
            </th>
            <th style="width: 15%">프로젝트 명</th>
            <th style="width: 22%">업무명</th>
            <th style="width: 13%">등록일</th>
            <th style="width: 12%">업무유형</th>
            <th style="width: 13%">담당자</th>
            <th style="width: 12%">소요시간</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="task in paginatedTasks" :key="task.id">
            <tr>
              <td>
                <input v-model="task.selected" type="checkbox" class="cb" />
              </td>
              <td>{{ task.project }}</td>
              <td>{{ task.name }}</td>
              <td>{{ task.date }}</td>
              <td>
                <span class="badge">{{ task.type }}</span>
              </td>
              <td>{{ task.assignee }}</td>
              <td>
                <span class="time-link" @click="toggleDetail(task.id)"
                  >{{ task.hours }}시간</span
                >
              </td>
            </tr>
            <tr v-if="openDetailId === task.id" :id="`tr-${task.id}`">
              <td
                colspan="7"
                style="
                  padding: 0;
                  border-bottom: 0.5px solid var(--color-border-tertiary);
                "
              >
                <DetailPanel :task="task" @close="openDetailId = null" />
              </td>
            </tr>
          </template>
          <tr class="total">
            <td colspan="5"></td>
            <td style="text-align: right; font-size: 14px">합계</td>
            <td style="color: #185fa5; font-size: 15px">
              {{ totalHours }}시간
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 페이징 -->
    <div class="pager">
      <div class="pg" @click="prevPage">‹</div>
      <div
        v-for="p in totalPages"
        :key="p"
        class="pg"
        :class="{ on: p === currentPage }"
        @click="goToPage(p)"
      >
        {{ p }}
      </div>
      <div class="pg" @click="nextPage">›</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "TimeReport",
  components: {
    // 해결책: 런타임에 필요할 때만 파일을 읽어오도록 비동기 선언합니다.
    // 이렇게 하면 '상호 참조'나 '초기화 순서' 에러를 원천 봉쇄할 수 있습니다.
    DetailPanel: () => import("./TimeEntries.vue"),
  },
  data() {
    return {
      projectId: this.$route.params.projectId,
      currentPage: 1,
      itemsPerPage: 10,
      openDetailId: null,
      selectAll: false,
      filters: {
        project: "",
        assignee: "",
        type: "",
        keyword: "",
        dateFrom: "2026-03-01",
        dateTo: "2026-06-30",
      },
      tasks: [
        {
          id: "d1",
          project: "프로젝트 A",
          name: "로그인 화면 구현",
          date: "2026.03.04",
          type: "개발",
          assignee: "김개발",
          hours: "5",
          selected: false,
        },
        {
          id: "d2",
          project: "프로젝트 A",
          name: "회원가입 화면 구현",
          date: "2026.03.04",
          type: "개발",
          assignee: "이개발",
          hours: "7",
          selected: false,
        },
        {
          id: "d3",
          project: "프로젝트 B",
          name: "관리자 권한 구현",
          date: "2026.03.05",
          type: "개발",
          assignee: "이개발",
          hours: "4",
          selected: false,
        },
        {
          id: "d4",
          project: "프로젝트 B",
          name: "그룹 관리 구현",
          date: "2026.03.05",
          type: "개발",
          assignee: "이개발",
          hours: "5",
          selected: false,
        },
      ],
    };
  },
  computed: {
    filteredTasks() {
      return this.tasks.filter((task) => {
        if (this.filters.project && task.project !== this.filters.project)
          return false;
        if (this.filters.assignee && task.assignee !== this.filters.assignee)
          return false;
        if (this.filters.type && task.type !== this.filters.type) return false;
        if (this.filters.keyword && !task.name.includes(this.filters.keyword))
          return false;
        return true;
      });
    },
    paginatedTasks() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      return this.filteredTasks.slice(start, start + this.itemsPerPage);
    },
    totalPages() {
      return Math.ceil(this.filteredTasks.length / this.itemsPerPage) || 1;
    },
    totalHours() {
      return this.filteredTasks.reduce(
        (sum, t) => sum + parseInt(t.hours || 0),
        0,
      );
    },
  },
  watch: {
    selectAll(val) {
      this.paginatedTasks.forEach((t) => (t.selected = val));
    },
  },
  mounted() {
    this.loadTasksByProject(this.projectId);
  },
  methods: {
    // ★ 주의: 이 아래에 methods 블록이 중복되지 않도록 하나로 다 합쳤습니다.
    toggleDetail(id) {
      this.openDetailId = this.openDetailId === id ? null : id;
    },
    applyFilter() {
      this.currentPage = 1;
    },
    exportExcel() {
      alert(`프로젝트 ${this.projectId} Excel 다운로드`);
    },
    exportPdf() {
      alert(`프로젝트 ${this.projectId} PDF 다운로드`);
    },
    goToPage(p) {
      this.currentPage = p;
    },
    prevPage() {
      if (this.currentPage > 1) this.currentPage--;
    },
    nextPage() {
      if (this.currentPage < this.totalPages) this.currentPage++;
    },
    loadTasksByProject(projectId) {
      console.log(`프로젝트 ID: ${projectId} 데이터 로딩`);
    },
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  font-family: var(--font-sans);
}
.page {
  padding: 24px 0;
  color: var(--color-text-primary);
}
.section-title {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 12px;
}
.filter-bar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: flex-end;
  padding: 18px 20px;
  background: var(--color-background-primary);
  border: 0.5px solid var(--color-border-tertiary);
  border-radius: var(--border-radius-lg);
  margin-bottom: 16px;
}
.fg {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.fg label {
  font-size: 12px;
  color: var(--color-text-secondary);
}
.fg select,
.fg input[type="text"] {
  font-size: 13px;
  padding: 7px 10px;
  border: 0.5px solid var(--color-border-tertiary);
  border-radius: var(--border-radius-md);
  background: var(--color-background-secondary);
  color: var(--color-text-primary);
  min-width: 120px;
}
.fg .date-range {
  display: flex;
  align-items: center;
  gap: 6px;
}
.fg input[type="date"] {
  font-size: 13px;
  padding: 7px 10px;
  border: 0.5px solid var(--color-border-tertiary);
  border-radius: var(--border-radius-md);
  background: var(--color-background-secondary);
  color: var(--color-text-primary);
  width: 130px;
}
.btn {
  padding: 7px 18px;
  font-size: 13px;
  border-radius: var(--border-radius-md);
  border: 0.5px solid var(--color-border-secondary);
  cursor: pointer;
  background: var(--color-background-primary);
  color: var(--color-text-primary);
  align-self: flex-end;
}
.btn:hover {
  background: var(--color-background-secondary);
}
.top-bar {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-bottom: 10px;
}
.btn-xl {
  padding: 6px 14px;
  font-size: 12px;
  border-radius: var(--border-radius-md);
  border: none;
  cursor: pointer;
}
.btn-excel {
  background: #1f6e3a;
  color: #fff;
}
.btn-pdf {
  background: #c0392b;
  color: #fff;
}
.tbl-wrap {
  border: 0.5px solid var(--color-border-tertiary);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  margin-bottom: 16px;
}
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  background: var(--color-background-primary);
}
th {
  background: var(--color-background-secondary);
  padding: 12px 16px;
  text-align: center;
  font-weight: 500;
  font-size: 13px;
  color: var(--color-text-secondary);
  border-bottom: 0.5px solid var(--color-border-tertiary);
}
td {
  padding: 13px 16px;
  text-align: center;
  border-bottom: 0.5px solid var(--color-border-tertiary);
}
tr:last-child td {
  border-bottom: none;
}
tr.total td {
  background: var(--color-background-secondary);
  font-weight: 500;
}
.time-link {
  color: #185fa5;
  font-weight: 500;
  cursor: pointer;
  text-decoration: underline;
  font-size: 14px;
}
.time-link:hover {
  color: #0c447c;
}
.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: var(--border-radius-md);
  font-size: 12px;
  background: #e6f1fb;
  color: #0c447c;
}
.cb {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #185fa5;
}
.pager {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}
.pg {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 0.5px solid var(--color-border-tertiary);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  background: var(--color-background-primary);
  color: var(--color-text-secondary);
  font-size: 13px;
}
.pg.on {
  background: #185fa5;
  color: #fff;
  border-color: #185fa5;
}
</style>
