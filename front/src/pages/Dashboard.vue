<template>
  <div class="flex h-screen overflow-hidden">

    <!-- Sidebar -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <!-- Content area -->
    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">

      <!-- Site header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">

          <!-- 페이지 타이틀 -->
          <div class="mb-6">
            <h1 class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold">
              Main Dashboard
            </h1>
          </div>

          <!-- ────────── 상단: 업무 통합 현황 + 우측 카드 ────────── -->
          <div class="dashboard-top mb-5">

            <!-- 업무 통합 현황 -->
            <div class="card main-col">
              <div class="card-header">
                <span class="card-title">업무 통합 현황</span>
                <label class="toggle-label">
                  <span>내 업무만 보기</span>
                  <el-switch v-model="myTaskOnly" />
                </label>
              </div>

              <el-table
                :data="pagedTaskData"
                style="width: 100%"
                :header-cell-style="headerStyle"
                :cell-style="cellStyle"
              >
                <el-table-column prop="no"          label="번호"    width="70"  align="center" />
                <el-table-column prop="projectName" label="프로젝트" min-width="160" />
                <el-table-column prop="created"     label="신규"    width="70"  align="center" />
                <el-table-column prop="inProgress"  label="진행"    width="70"  align="center" />
                <el-table-column prop="devDone"     label="개발완료" width="90"  align="center" />
                <el-table-column prop="rejected"    label="반려"    width="70"  align="center" />
                <el-table-column prop="done"        label="종료"    width="70"  align="center" />
                <el-table-column prop="totalSum"    label="합계"    width="70"  align="center" />
              </el-table>

              <div class="pagination-wrap">
                <el-pagination
                  v-model:current-page="workPage"
                  :page-size="workPageSize"
                  :total="taskProjects.length"
                  layout="prev, pager, next"
                  background
                />
              </div>
             
              <div class="new-project-btn-wrap">
                <el-button class="new-project-btn" @click="handleCreateProject">
                  + &nbsp; 새 프로젝트 생성
                </el-button>
              </div>
             
            </div>

     

            <!-- 우측 카드 묶음 -->
            <div class="side-col">

              <!-- 나의 업무 현황 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">나의 업무 현황</span>
                </div>
                <div class="task-body">
                  <div class="total-badge">
                    <span class="total-number">{{ totalTaskCount }}</span>
                  </div>
                  <ul class="dot-list">
                    <li v-for="item in taskStatusList" :key="item.label" class="dot-item">
                      <div class="dot-left">
                        <span class="dot" :style="{ backgroundColor: item.color }" />
                        <span class="dot-label">{{ item.label }}</span>
                      </div>
                      <span class="dot-count">{{ item.count }}</span>
                    </li>
                  </ul>
                </div>
              </div>

              <!-- 새 소식 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">새 소식</span>
                </div>
                <div class="news-body">
                  <ul class="dot-list">
                    <li v-for="item in newsList" :key="item.label" class="dot-item">
                      <div class="dot-left">
                        <span class="dot" :style="{ backgroundColor: item.color }" />
                        <span class="dot-label">{{ item.label }}</span>
                      </div>
                      <span class="dot-count">{{ item.count }}</span>
                    </li>
                  </ul>
                </div>
              </div>

            </div>
          </div>

          <!-- ────────── 하단: 프로젝트 목록 ────────── -->
          <div class="card">
            <div class="card-header">
              <span class="card-title">프로젝트 목록</span>
              <label class="toggle-label">
                <span>내 프로젝트만 보기</span>
                <el-switch v-model="myProjectOnly" />
              </label>
            </div>

            <el-table
              v-loading="loadingProjects"
              :data="pagedProjectData"
              style="width: 100%"
              :header-cell-style="headerStyle"
              :cell-style="cellStyle"
              @row-click="goProjectDashboard"
            >
              <el-table-column prop="parentProjectName" label="프로젝트명" min-width="180" />
              <el-table-column label="진척도" min-width="200">
                <template #default="{ row }">
                  <div class="progress-wrap">
                    <el-progress
                      :percentage="row.progressRate"
                      :stroke-width="8"
                      :show-text="false"
                      color="#2563eb"
                      style="flex: 1"
                    />
                    <span class="progress-text">{{ row.progressRate }}%</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="endDate" label="마감일" width="130" align="center" />
              <el-table-column prop="pmUserId"  label="관리자" width="110" align="center" />
            </el-table>

            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="projectPage"
                :page-size="projectPageSize"
                :total="projectList.length"
                layout="prev, pager, next"
                background
              />
            </div>
          </div>

        </div>
      </main>

    </div>
  </div>

<ProjectCreateModal v-model="createProjectModalOpen" />

</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios';
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectCreateModal from '../project/ProjectCreateModal.vue' 

const sidebarOpen = ref(false)

// ── 모달 열림 ─────────────────────────────────────
const createProjectModalOpen = ref(false)

// ── 토글 ──────────────────────────────────────────
const myTaskOnly    = ref(true)
const myProjectOnly = ref(true)

// ── 페이지네이션 ───────────────────────────────────
const workPage        = ref(1)
const workPageSize    = 5
const projectPage     = ref(1)
const projectPageSize = 5

// ── 더미 데이터 (백엔드 연결 시 onMounted에서 API 호출로 교체) ──
const myTasks = ref({ inProgress: 5, done: 10, rejected: 3, deadline: 1 })

const taskStatusList = computed(() => [
  { label: '진행중',   count: myTasks.value.inProgress, color: '#3b82f6' },
  { label: '완료',     count: myTasks.value.done,       color: '#22c55e' },
  { label: '반려',     count: myTasks.value.rejected,   color: '#9ca3af' },
  { label: '기한임박', count: myTasks.value.deadline,   color: '#ef4444' },
])

const totalTaskCount = computed(() =>
  Object.values(myTasks.value).reduce((sum, v) => sum + v, 0)
)

const newsList = [
  { label: '메세지',      count: 5, color: '#3b82f6' },
  { label: '새 공지사항', count: 3, color: '#22c55e' },
  { label: '업무 생성',   count: 2, color: '#9ca3af' },
]

const router = useRouter();

const taskProjects = ref([]); //workPrpjects
const loadingTasks=ref(false);
const taskError = ref('');

const fetchTaskList = async()=>{
  loadingTasks.value=true;
  taskError.value=''

try {
    const res = await axios.get('/api/TaskListDash')
    console.log(res.data);
    taskProjects.value=res.data
} catch (err) {
    console.error('업무 목록 조회 실패:', err)
    projectError.value='업무 목록 조회 실패'

    if (err.response) {
      console.error('status:', err.response.status)
      console.error('data:', err.response.data)
    } else if (err.request) {
      console.error('요청은 갔는데 응답이 없음')
    } else {
      console.error('axios 설정 오류')
    }
  } finally {
    loadingTasks.value = false
  }
};


const loadingProjects=ref(false);
const projectError = ref('');
const projectList = ref([]);

const fetchProjectList = async()=>{
  loadingProjects.value = true
  projectError.value = ''

  try{
  const res = await axios.get('/api/ProjectList')
  console.log(res.data);
  projectList.value=res.data;
  } catch(err) {
    console.error('프로젝트 목록 조회 실패:', err)
    projectError.value = '프로젝트 목록 조회 실패'

    if (err.response) {
      console.error('status:', err.response.status)
      console.error('data:', err.response.data)
    } else if (err.request) {
      console.error('요청은 갔는데 응답이 없음')
    } else {
      console.error('axios 설정 오류')
    }
  } finally {
    loadingProjects.value = false
  }
  
}

onMounted(()=>{
  fetchProjectList()
  fetchTaskList()
})

// ── 페이징된 데이터 ────────────────────────────────
const pagedTaskData = computed(() => {
  const s = (workPage.value - 1) * workPageSize
  return taskProjects
    .value.slice(s, s + workPageSize)
    .map((item,index) => ({
      ...item,
      no : s + index + 1 //번호 칼럼 없다면 no 빼도 됨
    }))
})

const pagedProjectData = computed(() => {
  const s = (projectPage.value - 1) * projectPageSize
  return projectList.value.slice(s, s + projectPageSize)
})

// ── 테이블 공통 스타일 ─────────────────────────────
const headerStyle = () => ({
  background: '#f8fafc',
  color: '#64748b',
  fontSize: '12px',
  fontWeight: '600',
  borderBottom: '1px solid #e2e8f0',
})
const cellStyle = () => ({
  fontSize: '13px',
  color: '#374151',
  borderBottom: '1px solid #f1f5f9',
})

// ── 이벤트 ────────────────────────────────────────
//버튼 클릭시 모달 창 열기
const handleCreateProject = () => {
  createProjectModalOpen.value = true
}

// ── 행클릭 이벤트──────────────────────────────────
const goProjectDashboard = (row) =>{
  router.push({
    name:'projectDash',
    params: { projectId: row.projectId}
  })
}
</script>

<style scoped>
/* ── 상단 레이아웃 ── */
.dashboard-top {
  display: grid;
  grid-template-columns: 1fr 230px;
  gap: 20px;
  align-items: start;
}
.side-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ── 카드 공통 ── */
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}
.card-header {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-weight: 600;
  font-size: 14px;
  color: #1a1a2e;
}
.toggle-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #555;
  cursor: pointer;
}

/* ── 업무 통합 현황 ── */
.total-cell {
  font-weight: 600;
  color: #2563eb;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}
.new-project-btn-wrap {
  padding: 12px 20px 16px;
}
.new-project-btn {
  width: 100%;
  background: #c7d9f5;
  border: none;
  color: #1a1a2e;
  font-weight: 500;
  font-size: 14px;
  border-radius: 8px;
  height: 40px;
}
.new-project-btn:hover {
  background: #a8c4ef;
}

/* ── 나의 업무 현황 / 새 소식 ── */
.task-body {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}
.news-body {
  padding: 16px 20px;
}
.total-badge {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #eff6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.total-number {
  font-size: 22px;
  font-weight: 700;
  color: #1d4ed8;
}
.dot-list {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.dot-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}
.dot-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot-label { color: #555; }
.dot-count  { font-weight: 600; color: #1a1a2e; }

/* ── 프로젝트 목록 ── */
.progress-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #2563eb;
  min-width: 32px;
}
:deep(.el-table__body-wrapper .el-table__row) {
  cursor: pointer;
}

/* ── ElementPlus 오버라이드 ── */
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}
:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}
:deep(.el-progress-bar__outer) {
  background: #e0eaff;
}

/* ── 반응형 ── */
@media (max-width: 1024px) {
  .dashboard-top { grid-template-columns: 1fr; }
  .side-col { flex-direction: row; }
  .side-col .card { flex: 1; }
}
@media (max-width: 600px) {
  .side-col { flex-direction: column; }
}
</style>
