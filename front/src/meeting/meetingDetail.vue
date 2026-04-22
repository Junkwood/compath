<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="sub-header">
          <div class="breadcrumb">
            <span class="bc-home">홈</span>
            <span class="bc-sep">›</span>
            <span v-for="info in taskPjList" :key="info">{{ info }} › </span>
            <span class="bc-cur">회의록 상세</span>
          </div>
        </div>
        <div class="page-container">
          <div class="pg-row">
            <div class="pg-left">
              <div class="proj-meta">
                <span class="proj-name">{{ name }}</span>
                <span class="proj-period">
                  {{ projectStartDate }} ~ {{ projectendDate }}
                </span>
              </div>
            </div>
            <div class="flex gap-2 self-end">
              <button
                v-if="isAssignee"
                @click="modifyMeeting"
                class="btn-modify"
              >
                수정
              </button>
              <button v-if="isAssignee" class="btn-lock" @click="lockMeeting">
                삭제
              </button>
              <button @click="goBack" type="button" class="btn-back">
                ← 돌아가기
              </button>
            </div>
          </div>

          <!-- ① 상단 헤더 카드: 제목 + 메타정보 -->
          <div class="detail-header-card">
            <h2 class="detail-main-title">{{ meetingInfo.title }}</h2>
            <div class="detail-meta-row grid grid-cols-4 gap-6">
              <div class="detail-meta-item">
                <span class="detail-meta-label">회의 일시</span>
                <span class="detail-meta-value">{{
                  meetingInfo.meetingDate
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
                <span class="detail-meta-value"
                  >{{ meetingInfo.userName }} 외
                  {{ meetingInfo.memberCount }}명</span
                >
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
                <Editor
                  class="w-full"
                  :modelValue="meetingInfo.content"
                  :isRead="isRead"
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
                  <div
                    class="py-11 flex justify-center"
                    v-if="attachmentList.length == 0"
                  >
                    <span>첨부파일이 존재하지 않습니다.</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 우측: 연결된 업무 -->
            <div class="detail-task-card">
              <div class="detail-task-header">
                <h3 class="detail-section-task-title">연결된 업무</h3>
                <div class="detail-task-actions">
                  <button
                    v-if="isAssignee"
                    type="button"
                    @click="openModal()"
                    class="task-btn-secondary"
                  >
                    연결업무 추가
                  </button>
                  <button
                    v-if="isAssignee"
                    class="task-btn-primary"
                    type="button"
                    @click="goRegister()"
                  >
                    업무 생성
                  </button>
                </div>
              </div>

              <div class="detail-task-list">
                <div
                  v-for="(item, index) in connectList"
                  :key="index"
                  class="task-item"
                  @click="gotaskDetail(item)"
                >
                  <span class="task-name">{{ item.title }}</span>
                  <span class="task-status">{{ item.statusName }}</span>
                  <button
                    v-if="isAssignee"
                    class="task-arrow"
                    @click.stop="delDtailConnect(item)"
                  >
                    x
                  </button>
                </div>

                <!-- 예시 더미 (데이터 없을 때 빈 상태 표시) -->
                <div v-if="connectList.length == 0" class="task-empty">
                  연결된 업무가 없습니다.
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
  <meetingConnectTaskModal
    v-model="openConnectModal"
    :projectInfo="projectInfo"
    :connectList="connectList"
    @close-modal="closeModal"
  />
</template>

<script setup>
import { onBeforeMount, ref, computed } from "vue";
import { useMeetingStore } from "../stores/meeting";
import { useAttachmentStore } from "../stores/attachment";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useAuthStore } from "../stores/auth";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import meetingConnectTaskModal from "./meetingConnectTaskModal.vue";
import Swal from "sweetalert2";
import Editor from "../components/Editor.vue";

const isRead = true;
const route = useRoute();
const router = useRouter();
const meetingStore = useMeetingStore();
const authStore = useAuthStore();
const attachmentStore = useAttachmentStore();
const taskStore = usetaskKJHStore();
const sidebarOpen = ref(false);

let taskPjList = ref([]);
const meetingInfo = ref({});
const attachmentList = ref([]);
const meetingId = route.params.meetingId;
const projectId = route.params.projectId;
const subId = route.params.subProjectId;
const openConnectModal = ref(false);
const connectList = ref([]);

const projectInfo = ref({
  projectId: subId != "" ? subId : projectId,
  meetingLogId: meetingId,
});

let name = ref(); // 프로젝트명
let projectStartDate = ref(); // 프로젝트 날짜
let projectendDate = ref(); // 프로젝트 날짜

// 업무생성 페이지 이동
const goRegister = () => {
  router.push({
    name: "taskRegister",
    params: { projectId: projectId },
  });
};

// 일감 연결 버튼
const openModal = () => {
  openConnectModal.value = true;
};

// 모달창 연결버튼
const closeModal = (val) => {
  openConnectModal.value = false;
  connectList.value = meetingStore.detailConnectList;
};

// 목록으로
const goBack = () => {
  router.push({
    name: "meetingList",
    params: { projectId: projectId, subProjectId: subId },
  });
};

// 수정 버튼
const modifyMeeting = () => {
  router.push({
    name: "meetingRegister",
    params: { projectId: projectId, subProjectId: subId, meetingId: meetingId },
  });
};

// 삭제 버튼
const lockMeeting = async () => {
  const result = await Swal.fire({
    title: "정말 삭제하시겠습니까?",
    text: "삭제된 회의록은 목록에서 확인 불가능합니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "삭제",
    cancelButtonText: "취소",
    reverseButtons: true,
  });

  if (!result.isConfirmed) return;

  await meetingStore.removeMeeting(meetingId);

  if (meetingStore.removeResult > 0) {
    const result = await Swal.fire({
      title: "삭제가 완료되었습니다.",
      text: "회의록 목록으로 이동합니다.",
      icon: "success",
      confirmButtonText: "확인",
      reverseButtons: true,
    });

    router.push({
      name: "meetingList",
      params: { projectId: projectId, subProjectId: subId },
    });
  }
};

// 연결 업무 상세페이지 이동
const gotaskDetail = (task) => {
  router.push({
    name: "taskDetail",
    params: { projectId: projectId, subProjectId: subId, taskId: task.taskId },
  });
};

// 연결 업무 해제
const delDtailConnect = async (task) => {
  let obj = {
    meetingtaskId: task.meetingtaskId,
    meetingLogId: task.meetingLogId,
  };
  await meetingStore.removeDetailConnectTask(obj);
  connectList.value = meetingStore.connectTaskList;
};

// 파일 다운로드
const attachmentDownload = async (file) => {
  await attachmentStore.downloadFile(file);
};

onBeforeMount(async () => {
  // 문서 및 프로젝트 정보
  await meetingStore.getMeetingById(meetingId);
  meetingInfo.value = meetingStore.meetingDetail.meetingList.meetingDetail;
  attachmentList.value =
    meetingStore.meetingDetail.attachmentList != null
      ? meetingStore.meetingDetail.attachmentList
      : [];
  connectList.value = meetingStore.meetingDetail.meetingList.connectDetail;

  let id = subId ? subId : projectId;
  await taskStore.getProjectName(id);
  let projectInfo = taskStore.projectName;
  if (projectInfo.parentProjectName != null) {
    taskPjList.value = [projectInfo.parentProjectName, projectInfo.projectName];
  } else {
    taskPjList.value = [projectInfo.projectName];
  }

  let roleObj = { projectId: id, subProjectId: subId };
  await taskStore.getProjectRole(roleObj);

  name.value = projectInfo.projectName;
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.endDate;
});

// 권한 파악
const isAssignee = computed(() => {
  const currentUserId = authStore.user?.userId || authStore.user?.id;
  if (!currentUserId) return false;

  const isPmPl = (taskStore.plPmList?.projectRoleList || []).some(
    (item) => Number(item.userId) === Number(currentUserId),
  );
  const isManager = (taskStore.plPmList?.empList || []).some(
    (item) => Number(item.userId) === Number(currentUserId),
  );
  return isPmPl || isManager;
});
</script>
<style scoped>
/* ── 상단 헤더 카드 ── */

.sub-header {
  background: #fff;
  padding: 15px 24px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.bc-home {
  color: #9ca3af;
}

.bc-sep {
  color: #d1d5db;
}

.bc-cur {
  color: #111827;
  font-weight: 600;
}

/* ── 페이지 전체 간격 ── */
.page-container {
  padding: 24px 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ── 프로젝트 상단 영역 ── */
.pg-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.proj-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.proj-name {
  font-size: 15px;
  font-weight: 700;
  color: #1b5c9c;
}

.proj-period {
  font-size: 13px;
  color: #6b7280;
}

/* ── 상단 헤더 카드 ── */
.detail-header-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 22px 28px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.detail-main-title {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 14px;
}

.detail-meta-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.detail-meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-meta-label {
  font-size: 12px;
  color: #9ca3af;
}

.detail-meta-value {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

/* ── 하단 레이아웃 ── */
.detail-body-row {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 20px;
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
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 22px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.detail-section-title {
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 12px;
}

/* 내용 영역 강조 */
.detail-content-body {
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #f1f5f9;
}

/* ── 첨부파일 ── */
.detail-content-card label {
  font-size: 13px;
  color: #6b7280;
}

.mt-6 {
  margin-top: 20px;
}

/* ── 연결된 업무 카드 ── */
.detail-task-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.detail-task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.detail-section-task-title {
  font-size: 15px;
  font-weight: 700;
}

.detail-task-actions {
  display: flex;
  gap: 6px;
}

/* 버튼 */
.task-btn-secondary {
  height: 28px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 6px;
  border: 1px solid #bfdbfe;
  background: #eff6ff;
  color: #2563eb;
}

.task-btn-secondary:hover {
  background: #dbeafe;
}

.task-btn-primary {
  height: 28px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 6px;
  background: #16a34a;
  color: #fff;
}

.task-btn-primary:hover {
  background: #15803d;
}

/* ── 업무 리스트 ── */
.detail-task-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  border: 1px solid #eef2f7;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.15s;
}

.task-item:hover {
  background: #f8fafc;
  border-color: #c7d2fe;
}

.task-name {
  font-size: 13px;
  color: #1e293b;
  flex: 1;
}

.task-status {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 999px;
  background: #eef2ff;
  color: #4f46e5;
  margin-right: 8px;
}

.task-arrow {
  font-size: 12px;
  color: #94a3b8;
}

/* 빈 상태 */
.task-empty {
  padding: 120px 10px;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
}

/* ── 버튼 (상단) ── */
.btn-modify {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(27, 92, 156, 0.22);
  height: 38px;
  padding: 0 16px;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  height: 38px;
  padding: 0 16px;
  border-radius: 10px;
  border: none;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-modify:hover {
  transform: translateY(-1px);
  filter: brightness(1.05);
}

.btn-lock {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #ef4444;
  color: #fff;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
  height: 38px;
  padding: 0 16px;
  border-radius: 10px;
  border: none;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-lock:hover {
  transform: translateY(-1px);
  background: #dc2626;
}

.btn-back {
  height: 40px;
  padding: 0 18px;
  border-radius: 10px;
  border: 1px solid #dbe4f0;
  background: #fff;
  color: #1b5c9c;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}
.btn-back:hover {
  background: #eff6ff;
  border-color: #bfd3f6;
}
</style>
