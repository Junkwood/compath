<template>
  <div class="flex min-h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-8">
            회의록 상세
          </h1>
          <el-alert
            v-if="meetingInfo.isDeleted === 'O1'"
            title="비활성화된 게시글입니다."
            type="warning"
            description="관리자만 열람 가능하며 일반 사용자에게는 노출되지 않습니다."
            show-icon
            :closable="false"
            class="mb-4"
          />

          <!-- ① 상단 헤더 카드: 제목 + 메타정보 -->
          <div class="detail-header-card mb-5">
            <h2 class="detail-main-title">{{ meetingInfo.title }}</h2>
            <div class="detail-meta-row grid grid-cols-4 gap-6">
              <div class="detail-meta-item">
                <span class="detail-meta-label">회의 일시</span>
                <span class="detail-meta-value">{{
                  meetingInfo.createdAt
                }}</span>
              </div>
              <div class="detail-meta-item">
                <span class="detail-meta-label">회의 장소</span>
                <span class="detail-meta-value">{{
                  meetingInfo.roleName == null ? "전체" : meetingInfo.roleName
                }}</span>
              </div>
              <div class="detail-meta-item">
                <span class="detail-meta-label">참석자</span>
                <span class="detail-meta-value">{{
                  meetingInfo.userName
                }}</span>
              </div>
              <div class="detail-meta-item">
                <span class="detail-meta-label">작성자</span>
                <span class="detail-meta-value">{{
                  meetingInfo.userName
                }}</span>
              </div>
            </div>
          </div>

          <!-- ② 하단 2단 레이아웃 -->
          <div class="detail-body-row">
            <!-- 좌측: 회의 내용 -->
            <div class="detail-content-card">
              <h3 class="detail-section-title">회의 내용</h3>
              <div class="detail-content-body">
                <textarea
                  rows="12"
                  class="input w-full"
                  :value="meetingInfo.content"
                  disabled
                />
              </div>

              <!-- 첨부파일 -->
              <div class="mt-6">
                <label class="block text-sm font-semibold mb-2 text-gray-600"
                  >첨부파일</label
                >
                <div
                  class="border-t border-b border-gray-200 divide-y divide-gray-100"
                >
                  <div
                    v-for="(file, index) in attachmentList"
                    :key="index"
                    class="py-2 flex items-center justify-between group"
                    v-if="attachmentList && attachmentList.length > 0"
                  >
                    <div
                      class="flex items-center gap-2 flex-1 cursor-pointer"
                      @click="attachmentDownload(file)"
                    >
                      <svg
                        class="w-4 h-4 text-gray-400 shrink-0"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"
                        />
                      </svg>
                      <span
                        class="text-[13px] text-gray-700 group-hover:text-blue-600 transition-colors"
                      >
                        {{ file.fileName }}
                      </span>
                    </div>
                    <button
                      @click="attachmentDownload(file)"
                      class="text-[12px] text-gray-500 hover:text-blue-600 flex items-center gap-1"
                    >
                      다운로드 <span class="text-[10px] text-gray-300">〉</span>
                    </button>
                  </div>
                  <div class="py-11 flex justify-center">
                    <span>첨부파일이 존재하지 않습니다.</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 우측: 연결된 일감 -->
            <div class="detail-task-card">
              <div class="detail-task-header">
                <h3 class="detail-section-title">연결된 일감</h3>
                <div class="detail-task-actions">
                  <button class="task-btn-secondary">연결일감 추가</button>
                  <button
                    class="task-btn-primary"
                    type="button"
                    @click="goRegister()"
                  >
                    일감 생성
                  </button>
                </div>
              </div>

              <div class="detail-task-list">
                <div
                  v-for="(item, index) in []"
                  :key="index"
                  class="task-item"
                  @click="goMilestoneDetail(item)"
                >
                  <span class="task-name">{{ item.name }}</span>
                  <span class="task-status">진행중</span>
                  <span class="task-arrow">〉</span>
                </div>

                <!-- 예시 더미 (데이터 없을 때 빈 상태 표시) -->
                <div v-if="true" class="task-empty">
                  연결된 일감이 없습니다.
                </div>
              </div>
            </div>
          </div>

          <!-- 하단 버튼 -->
          <div class="detail-footer-row mt-4">
            <button @click="goBack" type="button" class="btn-navy">
              ← 목록으로
            </button>
            <button @click="modifyDocument" class="btn-green">수정</button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref } from "vue";
import { useMeetingStore } from "../stores/meeting";
import { useAttachmentStore } from "../stores/attachment";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const route = useRoute();
const router = useRouter();
const meetingStore = useMeetingStore();
const attachmentStore = useAttachmentStore();
const sidebarOpen = ref(false);

const meetingInfo = ref({});
const attachmentList = ref([]);
const meetingId = route.params.meetingId;
const projectId = route.params.projectId;

// 업무생성 페이지 이동
const goRegister = () => {
  router.push({
    name: "taskRegister",
    params: { projectId: projectId },
  });
};

// 목록으로
const goBack = () => {
  console.log(projectId);
  router.push({
    name: "meetingList",
    params: { projectId: projectId },
  });
};

// 수정 버튼
const modifyDocument = () => {
  router.push({
    name: "meetingRegister",
    params: { projectId: projectId, meetingId: meetingId },
  });
};

// 파일 다운로드
const attachmentDownload = async (file) => {
  console.log(file);
  await attachmentStore.downloadFile(file);
};

onBeforeMount(async () => {
  // 문서 및 프로젝트 정보
  await meetingStore.getMeetingById(meetingId);
  meetingInfo.value = meetingStore.meetingDetail.meetingList;
  attachmentList.value = meetingStore.meetingDetail.attachmentList;
});
</script>
<style scoped>
/* ── 상단 헤더 카드 ── */
.detail-header-card {
  background: #fff;
  border: 1px solid #e8edf2;
  border-radius: 16px;
  padding: 28px 32px 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.detail-main-title {
  font-size: 25px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 20px 0;
  letter-spacing: -0.02em;
}

.detail-meta-row {
  display: grid;
  gap: 48px;
  grid-column: 4;
}

.detail-meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-meta-label {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
  letter-spacing: 0.02em;
}

.detail-meta-value {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
}

/* ── 하단 2단 레이아웃 ── */
.detail-body-row {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 16px;
  align-items: start;
}

@media (max-width: 1024px) {
  .detail-body-row {
    grid-template-columns: 1fr;
  }
}

/* ── 회의 내용 카드 ── */
.detail-content-card {
  background: #fff;
  border: 1px solid #e8edf2;
  border-radius: 16px;
  padding: 24px 28px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  min-height: 360px;
}

.detail-section-title {
  font-size: 16px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 16px 0;
}

.detail-textarea {
  border-radius: 10px !important;
  border: none !important;
  background: transparent !important;
  resize: none;
  font-size: 14px;
  color: #334155;
  line-height: 1.75;
  padding: 0 !important;
}

/* ── 연결된 일감 카드 ── */
.detail-task-card {
  background: #fff;
  border: 1px solid #e8edf2;
  border-radius: 16px;
  padding: 24px 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  min-height: 566.5px;
}

.detail-task-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-task-actions {
  display: flex;
  gap: 8px;
}

.task-btn-secondary {
  height: 30px;
  padding: 0 14px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: 1.5px solid #64b5f6;
  background: #e3f2fd;
  color: #1565c0;
  transition: all 0.15s;
}

.task-btn-secondary:hover {
  background: #bbdefb;
}

.task-btn-primary {
  height: 30px;
  padding: 0 14px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  background: #2e7d32;
  color: #fff;
  transition: all 0.15s;
}

.task-btn-primary:hover {
  background: #1b5e20;
}

/* ── 일감 목록 ── */
.detail-task-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.task-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border: 1px solid #e8edf2;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.15s;
  background: #f8fafc;
}

.task-item:hover {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.task-name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  flex: 1;
}

.task-status {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 999px;
  background: #fef3c7;
  color: #92400e;
  margin-right: 10px;
}

.task-arrow {
  font-size: 12px;
  color: #94a3b8;
}

.task-empty {
  padding: 200px 20px;
  text-align: center;
  font-size: 13px;
  color: #94a3b8;
}

/* ── 하단 버튼 영역 ── */
.detail-footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* ── 공통 버튼 ── */
.btn-navy {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1e3a5f;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(30, 58, 95, 0.25);
}

.btn-navy:hover {
  background: #162d4a;
  transform: translateY(-1px);
}

.btn-green {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1882c9;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(24, 130, 201, 0.25);
}

.btn-green:hover {
  background: #60aee2;
  transform: translateY(-1px);
}

/* 인풋 전체 라운드 */
:deep(.input) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
}
:deep(.input:focus) {
  border-color: #94a3b8 !important;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15) !important;
  background: #fff !important;
  outline: none;
}
:deep(.input:disabled) {
  background: #f1f5f9 !important;
  color: #94a3b8 !important;
}
:deep(select.input) {
  border-radius: 10px !important;
  appearance: auto !important;
  -webkit-appearance: auto !important;
  padding-right: 28px !important;
}
:deep(textarea.input) {
  border-radius: 10px !important;
}
:deep(.input:disabled) {
  background: #f1f5f9 !important;
  color: #475569 !important; /* #94a3b8 → #475569 으로 변경! */
}

/* ── 반응형 ── */
@media (max-width: 768px) {
  .detail-meta-row {
    gap: 20px;
  }
  .detail-header-card {
    padding: 20px;
  }
  .detail-content-card,
  .detail-task-card {
    padding: 18px;
  }
}
</style>
