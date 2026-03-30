<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <!-- Content area -->
    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- Site header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- 페이지 타이틀 + 프로젝트명 + 기간 -->
          <div class="mb-6 proj-title-row">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                프로젝트 대시보드
              </h2>
              <div class="proj-name-row">
                <span class="proj-name"
                  >【 {{ projectInfo.projectName }} 】</span
                >
                <span class="proj-period"
                  >{{ projectInfo.startDate }} - {{ projectInfo.endDate }}</span
                >
              </div>
            </div>

            <div class="proj-title-right">
              <el-button class="setting-btn" @click="handleProjectSetting">
                ⚙ 프로젝트 설정
              </el-button>
            </div>
          </div>

          <!-- ────────── 상단: 업무현황 + 프로젝트 구성원 ────────── -->
          <div class="proj-top mb-5">
            <!-- 좌측: 업무 현황 + 공지사항 + 하위 프로젝트 -->
            <div class="proj-left-col">
              <!-- 업무 현황 -->
              <div class="card mb-5">
                <div class="card-header">
                  <span class="card-title">업무 현황</span>
                  <el-button class="view-task-btn" @click="handleViewTasks">
                    업무 목록 보기
                  </el-button>
                </div>
                <div class="task-table-wrap">
                  <el-table
                    :data="taskSummaryData"
                    class="task-status-table"
                    style="width: 100%"
                    :header-cell-style="taskHeaderStyle"
                    :cell-style="taskCellStyle"
                  >
                    <el-table-column prop="type" label="유형" min-width="90" />
                    <el-table-column
                      prop="total"
                      label="전체"
                      min-width="70"
                      align="center"
                    />
                    <el-table-column
                      prop="inProgress"
                      label="진행중"
                      min-width="80"
                      align="center"
                    />
                    <el-table-column
                      prop="done"
                      label="완료"
                      min-width="70"
                      align="center"
                    />
                    <el-table-column
                      prop="rejected"
                      label="반려"
                      min-width="70"
                      align="center"
                    />
                    <el-table-column
                      prop="totalSum"
                      label="합계"
                      min-width="70"
                      align="center"
                    />
                  </el-table>
                </div>
              </div>

              <!-- 공지사항 -->
              <div class="card mb-5">
                <div class="card-header">
                  <span class="card-title">공지사항</span>
                </div>
                <div class="notice-body">
                  <ul class="notice-list">
                    <li
                      v-for="item in noticeList"
                      :key="item.id"
                      class="notice-item"
                      @click="handleNoticeClick(item)"
                    >
                      <div class="notice-left">
                        <span class="notice-title">{{ item.title }}</span>
                        <span v-if="item.isNew" class="notice-badge">NEW</span>
                      </div>
                      <span class="notice-date">{{ item.date }}</span>
                    </li>
                  </ul>
                </div>
              </div>

              <!-- 하위 프로젝트 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">하위 프로젝트 목록</span>
                  <el-button class="add-sub-btn" @click="handleAddSubProject">
                    + 하위 프로젝트 생성
                  </el-button>
                </div>
            <div class="sub-project-body">
              <template v-if="pagedMilestones.length > 0">
                <div class="sub-project-group">
                  <div class="sub-project-stage-title">
                    마일스톤 
                    {{ currentMilestone?.milestoneName }}
                  </div>

                  <div class="sub-project-table-wrap">
                    <el-table
                      :data="currentMilestone.projects"
                      class="sub-project-table"
                      style="width: 100%"
                      :show-header="false"
                      :cell-style="subCellStyle"
                    >
                      <el-table-column prop="projectName" min-width="220" />
                      <el-table-column label="PL" width="140" align="right">
                        <template #default="{ row }">
                          <span class="sub-pl">PL {{ row.userName }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>

                  <div class="pagination-wrap" v-if="pagedMilestones.length > 1">
                    <el-pagination
                      v-model:current-page="milestonePage"
                      :page-size="1"
                      :total="pagedMilestones.length"
                      layout="prev, pager, next"
                      background
                    />
                  </div>

                </div>
              </template>

              <div v-else class="sub-empty">
                하위 프로젝트가 없습니다.
              </div>
            </div>

            </div>
            </div>
            <!-- 우측: 프로젝트 구성원 + 나의 메모 -->
            <div class="proj-right-col">
              <!-- 프로젝트 구성원 -->
              <div class="card mb-5">
                <div class="card-header">
                  <span class="card-title">프로젝트 구성원</span>
                </div>
                <div class="member-body">
                  <div
                    v-for="member in projectMembers"
                    :key="member.name"
                    class="member-item"
                  >
                    <div
                      class="member-avatar"
                      :style="{ backgroundColor: member.avatarColor }"
                    >
                      {{ member.name.charAt(0) }}
                    </div>
                    <div class="member-info">
                      <span class="member-name">{{ member.name }}</span>
                      <span class="member-role-badge" :class="member.roleClass">
                        {{ member.role }}
                      </span>
                    </div>
                  </div>
                  <div class="member-empty-row">
                    <span class="member-empty-text"
                      >구성원이 아직 지정되지 않았습니다.</span
                    >
                  </div>
                </div>
              </div>

              <!-- 나의 메모 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">나의 메모</span>
                  <el-button class="memo-add-btn" @click="handleAddMemo" circle>
                    +
                  </el-button>
                </div>
                <div class="memo-body">
                  <template v-if="memoList.length > 0" >
                  <div
                    v-for="(memo, index) in memoList"
                    :key="memo.id"
                    class="memo-card"
                    :class="getMemoColorClass(index)"
                  >
                    <div class="memo-content">
                      <div class="memo-date">{{ memo.createdAt }}</div>
                      <div class="memo-text">{{ memo.memoContent}}</div>
                    </div>
                    <el-button
                      class="memo-del-btn"
                      @click="handleDeleteMemo(memo.memoId)"
                      text
                    >
                      ✕
                    </el-button>
                  </div>
                  </template>

                  <div v-else class="memo-empty">
                    등록된 메모가 없습니다.
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

<ProjectMemoModal
  v-model="memoModalVisible"
  @submitted="handleMemoSubmitted"
/>

</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from '../partials/Sidebar.vue'
import Header from '../partials/Header.vue'
import axios from 'axios'
import ProjectMemoModal from '../project/ProjectMemoModal.vue'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);

// ── 업무 현황 (하드코딩) ────────────────────────────
const taskSummaryData = ref([
  {
    type: "개발",
    total: 13,
    inProgress: 9,
    done: 0,
    rejected: 1,
    totalSum: 23,
  },
  {
    type: "기획",
    total: 13,
    inProgress: 9,
    done: 0,
    rejected: 1,
    totalSum: 23,
  },
  {
    type: "업무",
    total: 18,
    inProgress: 4,
    done: 1,
    rejected: 0,
    totalSum: 23,
  },
  { type: "기타", total: 5, inProgress: 4, done: 1, rejected: 0, totalSum: 10 },
  {
    type: "다스트",
    total: 5,
    inProgress: 0,
    done: 0,
    rejected: 0,
    totalSum: 2,
  },
]);

// ── 공지사항 (하드코딩) ──────────────────────────────
const noticeList = ref([
  { id: 1, title: "서비스 현황성 적용 안내", date: "2026-03-19", isNew: true },
  { id: 2, title: "서비스 현황성 적용 안내", date: "2026-06-18", isNew: false },
  {
    id: 3,
    title: "공지사항 등록 및 수정 안내",
    date: "2026-06-18",
    isNew: false,
  },
  {
    id: 4,
    title: "공지사항 등록 및 수정 안내",
    date: "2026-06-18",
    isNew: false,
  },
]);


// ── 프로젝트 구성원 (하드코딩) ───────────────────────
const projectMembers = ref([
  { name: "김관리", role: "PM", roleClass: "role-pm", avatarColor: "#3b82f6" },
  { name: "이카엘", role: "PL", roleClass: "role-pl", avatarColor: "#8b5cf6" },
  {
    name: "개발1팀",
    role: "팀원",
    roleClass: "role-dev",
    avatarColor: "#10b981",
  },
  {
    name: "최지우",
    role: "개발자",
    roleClass: "role-dev",
    avatarColor: "#f59e0b",
  },
  { name: "최지우", role: "QA", roleClass: "role-qa", avatarColor: "#ef4444" },
  {
    name: "김관리",
    role: "관리자",
    roleClass: "role-mgr",
    avatarColor: "#6366f1",
  },
]);

// ── 나의 메모 ──────────────────────────────────
const memoList = ref([])

const fetchMemoList= async() => {
  try{
    const projectId = route.params.projectId
    const userId = authStore.user?.userId
    const res = await axios.get(`/api/MemoList/${projectId}`,{
      params:{ userId }
    })
    memoList.value = res.data
    } catch(err){
      console.error('메모 목록 조회 실패 :', err)
    }
}

const getMemoColorClass = (index) => {
  const colorClasses = ['memo-blue', 'memo-yellow', 'memo-pink']
  return colorClasses[index % colorClasses.length]
}


const projectInfo = ref({
  projectId: null,
  projecttName: "",
  startDate: "",
  endDate: "",
});

const fetchProjectDetail = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await axios.get(`/api/ProjectDetail/${projectId}`);
    projectInfo.value = res.data;
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

// ── 하위 프로젝트 ──────────────────────────────
const subProjects = ref([])
const milestonePage = ref(1)

const fetchSubProject = async()=>{
    try{
      const projectId = route.params.projectId
      const res = await axios.get(`/api/ProjectSubDetail/${projectId}`)
      subProjects.value = res.data
      milestonePage.value=1
    } catch(err){
      console.error('하위프로젝트 조회 실패:', err)
    }
}

//milestoneID 기준 그루핑
const pagedMilestones = computed(() => {
  const map = new Map()

  subProjects.value.forEach((item) => {
    const key = item.milestoneId

    if (!map.has(key)) {
      map.set(key, {
        milestoneId: item.milestoneId,
        milestoneName: item.milestoneName,
        projects: []
      })
    }

    map.get(key).projects.push({
      projectId: item.projectId,
      projectName: item.projectName,
      userName: item.userName
    })
  })

  return Array.from(map.values())
})

const currentMilestone = computed(() => {
  return pagedMilestones.value[milestonePage.value - 1] || null
})

const memoModalVisible = ref(false)

// ── 이벤트 핸들러 ────────────────────────────────────
const handleProjectSetting  = () => { /* TODO: 설정 페이지 이동 */ }
const handleViewTasks       = () => { /* TODO: 업무 목록 페이지 이동 */ }
const handleNoticeClick     = (item) => { /* TODO: 공지사항 상세 */ }
const handleAddSubProject   = () => { /* TODO: 하위 프로젝트 생성 모달 */ }
const handleAddMemo = () => {
  memoModalVisible.value = true
}
const handleDeleteMemo = (id) => {
  memoList.value = memoList.value.filter(m => m.id !== id)
}

const handleMemoSubmitted = async (payload) => {
  try{
    const projectId = route.params.projectId
    const userId = authStore.user?.userId

    if(!userId) {
      console.warn('로그인 사용자 정보가 없습니다.')
      return
    }

    await axios.post(`/api/MemoRegister`,{
      projectId,
      userId,
      memoContent:payload.text
    })

    await fetchMemoList()
  } catch (err) {
    console.error('메모 등록에 실패:', err)
  }
}


onMounted(()=>{
  fetchProjectDetail();
  fetchSubProject();
  fetchMemoList();
})


// ── 테이블 공통 스타일 ─────────────────────────────
const subCellStyle = () => ({
  fontSize: '13px',
  color: '#374151',
  borderBottom: '1px solid #dcdfe6',
  padding: '7px 12px',
  height: '36px'
})

</script>

<style scoped>
/* ────────────────────────────────────────────
   프로젝트 타이틀 행
──────────────────────────────────────────── */
.proj-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}
.proj-title-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.proj-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}
.proj-period {
  font-size: 13px;
  color: #64748b;
}
.setting-btn {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  height: 36px;
  padding: 0 14px;
}
.setting-btn:hover {
  background: #e2e8f0;
}

/* ────────────────────────────────────────────
   상단 2단 레이아웃
──────────────────────────────────────────── */
.proj-top {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 20px;
  align-items: start;
  min-width: 0;
}
.proj-left-col {
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}
.proj-right-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

/* ────────────────────────────────────────────
   카드 공통 (Main Dashboard와 동일)
──────────────────────────────────────────── */
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

/* ────────────────────────────────────────────
   업무 현황
──────────────────────────────────────────── */
.view-task-btn {
  background: #c7d9f5;
  border: none;
  color: #1a1a2e;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  height: 32px;
  padding: 0 12px;
}
.view-task-btn:hover {
  background: #a8c4ef;
}

/* ────────────────────────────────────────────
   공지사항
──────────────────────────────────────────── */
.notice-body {
  padding: 8px 0;
}
.notice-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #f8fafc;
}
.notice-item:last-child {
  border-bottom: none;
}
.notice-item:hover {
  background: #f0f7ff;
}
.notice-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.notice-title {
  font-size: 13px;
  color: #374151;
}
.notice-title::before {
  content: "·";
  color: #94a3b8;
  margin-right: 6px;
}
.notice-badge {
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: 4px;
  letter-spacing: 0.03em;
}
.notice-date {
  font-size: 12px;
  color: #94a3b8;
}

/* ────────────────────────────────────────────
   하위 프로젝트
──────────────────────────────────────────── */
/* 하위 프로젝트 */
.add-sub-btn {
  background: #c7d9f5;
  border: none;
  color: #000000;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  height: 32px;
  padding: 0 12px;
}
.add-sub-btn:hover {
  background: #a8c4ef;
}

.sub-project-body {
  padding: 12px 16px 0;
}

.sub-project-group {
  /* border: 1px solid #6b7280; */
  border-radius: 14px;
  background: #fff;
  overflow: hidden;
}

.sub-project-stage-title {
  padding: 10px 14px 6px;
  font-size: 16px;
  line-height: 1.35;
  color: #000000;
  white-space: pre-line;
}

.sub-project-table-wrap {
  max-height: 216px;   /* 6행 정도 */
  overflow-y: auto;
  /* border-top: 1px solid #6b7280; */
}

.sub-project-table :deep(.el-table) {
  border: none !important;
  font-size: 12px;
}

.sub-project-table :deep(.el-table__inner-wrapper::before),
.sub-project-table :deep(.el-table::before) {
  display: none;
}

.sub-project-table :deep(td.el-table__cell) {
  padding: 0 !important;
  height: 36px;
  /* border-bottom: 1px solid #6b7280 !important; */
}

.sub-project-table :deep(.cell) {
  padding: 0 12px !important;
  line-height: 36px;
  color: #111827;
}

.sub-project-table :deep(tr:last-child td.el-table__cell) {
  border-bottom: none !important;
}

.sub-pl {
  font-size: 12px;
  color: #111827;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}

.sub-empty {
  padding: 24px 0;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
}

/* ────────────────────────────────────────────
   프로젝트 구성원
──────────────────────────────────────────── */
.member-body {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.member-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.member-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}
.member-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}
.member-name {
  font-size: 13px;
  color: #374151;
  font-weight: 500;
}
.member-role-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 7px;
  border-radius: 99px;
  letter-spacing: 0.03em;
}
.role-pm {
  background: #dbeafe;
  color: #1d4ed8;
}
.role-pl {
  background: #ede9fe;
  color: #6d28d9;
}
.role-dev {
  background: #d1fae5;
  color: #065f46;
}
.role-qa {
  background: #fee2e2;
  color: #b91c1c;
}
.role-mgr {
  background: #e0e7ff;
  color: #3730a3;
}

.member-empty-row {
  padding-top: 4px;
  border-top: 1px solid #f0f0f0;
}
.member-empty-text {
  font-size: 12px;
  color: #9ca3af;
}

/* ────────────────────────────────────────────
   나의 메모
──────────────────────────────────────────── */
.memo-add-btn {
  background: #3b82f6;
  color: #fff;
  border: none;
  width: 28px;
  height: 28px;
  font-size: 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  line-height: 1;
}
.memo-add-btn:hover {
  background: #2563eb;
}
.memo-body {
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.memo-card {
  border-radius: 8px;
  padding: 10px 12px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}
.memo-blue  { background: #dbeafe; }
.memo-green { background: #d1fae5; }
.memo-pink  { background: #fce7f3; }
.memo-yellow { background: #fdffd1; }

.memo-content {
  flex: 1;
}
.memo-date {
  font-size: 11px;
  color: #64748b;
  margin-bottom: 4px;
}
.memo-text {
  font-size: 13px;
  color: #374151;
  white-space: pre-line;
  line-height: 1.5;
}
.memo-del-btn {
  font-size: 13px;
  color: #94a3b8;
  padding: 0;
  min-height: auto;
  height: auto;
  flex-shrink: 0;
}
.memo-del-btn:hover {
  color: #ef4444;
}

/* ────────────────────────────────────────────
   ElementPlus 오버라이드 (Main Dashboard와 동일)
──────────────────────────────────────────── */
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}
:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}

/* ────────────────────────────────────────────
   테이블 오버플로우 처리 (반응형 핵심)
──────────────────────────────────────────── */
.table-scroll-wrap {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}
:deep(.el-table) {
  overflow-x: auto;
}
:deep(.el-table .el-table__inner-wrapper) {
  min-width: 0;
}

/* ────────────────────────────────────────────
   반응형 — 태블릿 (≤ 1024px)
──────────────────────────────────────────── */
@media (max-width: 1024px) {
  .proj-top {
    grid-template-columns: 1fr;
  }
  .proj-right-col {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  /* mb-5 클래스가 붙은 카드 margin 제거 (grid가 gap으로 처리) */
  .proj-right-col .card {
    margin-bottom: 0 !important;
  }
}

/* ────────────────────────────────────────────
   반응형 — 모바일 (≤ 768px)
──────────────────────────────────────────── */
@media (max-width: 768px) {
  /* 타이틀 행 세로 정렬 */
  .proj-title-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .proj-name-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  .proj-name {
    font-size: 15px;
  }

  /* 우측 컬럼 1단 */
  .proj-right-col {
    grid-template-columns: 1fr;
  }

  /* 공지사항 날짜 숨김 */
  .notice-date {
    display: none;
  }

  /* 설정 버튼 전체 폭 */
  .setting-btn {
    width: 100%;
    justify-content: center;
  }

  /* 카드 헤더 wrap 처리 */
  .card-header {
    flex-wrap: wrap;
    gap: 8px;
  }

  /* 버튼들 폭 조정 */
  .view-task-btn,
  .add-sub-btn {
    font-size: 12px;
    padding: 0 10px;
  }
}

/* ────────────────────────────────────────────
   반응형 — 소형 모바일 (≤ 480px)
──────────────────────────────────────────── */
@media (max-width: 480px) {
  .proj-name {
    font-size: 13px;
    word-break: keep-all;
  }
  .member-role-badge {
    display: none;
  }
  .sub-pl {
    font-size: 11px;
  }
  .memo-body {
    padding: 10px 12px;
  }
}

.table-scroll-wrap {
  padding: 0 10px 10px 10px;
}

.table-scroll-wrap :deep(.el-table) {
  border: 1px solid #d9d9d9;
  border-radius: 0;
  font-size: 12px;
}

.table-scroll-wrap :deep(.el-table th.el-table__cell) {
  background: #f3f4f6 !important;
  padding: 6px 0;
}

.table-scroll-wrap :deep(.el-table td.el-table__cell) {
  padding: 6px 8px;
}

.table-scroll-wrap :deep(.el-table .cell) {
  line-height: 1.2;
}

.table-scroll-wrap :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.table-scroll-wrap :deep(.el-table--border::after),
.table-scroll-wrap :deep(.el-table--group::after),
.table-scroll-wrap :deep(.el-table::before) {
  display: none;
}
</style>
