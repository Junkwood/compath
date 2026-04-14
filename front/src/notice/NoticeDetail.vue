<template>
  <div class="flex min-h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- Header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <el-alert
            v-if="noticeInfo.isDeleted === 'Q1'"
            title="비활성화된 게시글입니다."
            type="warning"
            description="관리자만 열람 가능하며 일반 사용자에게는 노출되지 않습니다."
            show-icon
            :closable="false"
            class="mb-4"
          />

          <!-- projectDashboard.vue와 동일한 제목 영역 -->
          <div class="mb-6 proj-title-row flex justify-between mt-5">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                공지사항 상세
              </h2>

              <div class="proj-name-row">
                <span class="proj-name"
                  >【 {{ noticeInfo.projectName }} 】</span
                >
                <span class="proj-period">
                  {{ noticeInfo.startDate }} ~ {{ noticeInfo.endDate }}
                </span>
              </div>
            </div>
          </div>

          <div
            class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mb-0 p-6"
          >
            <div
              class="text-xl md:text-2xl text-gray-800 dark:text-gray-100 font-bold mb-8"
            >
              <h4>
                {{ noticeInfo.title
                }}<span
                  class="text-base"
                  v-if="noticeInfo.isPinned == 'B1' ? true : false"
                  >[긴급🚨]</span
                >
              </h4>
            </div>
            <div class="grid grid-cols-3 gap-6 mb-8">
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1"
                  >카테고리</label
                >
                <span>{{ noticeInfo.roleName }}</span>
              </div>
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1">작성자</label>
                <span>{{ noticeInfo.userName }}</span>
              </div>
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1">등록일</label>
                <span>{{ noticeInfo.createdAt }}</span>
              </div>
            </div>

            <div class="mb-6">
              <label class="block text-base font-semibold mb-1"
                >프로젝트 설명</label
              >
              <textarea
                rows="5"
                class="input w-full"
                :value="noticeInfo.content"
                disabled
              />
            </div>

            <div class="flex flex-row justify-between">
              <div class="flex flex-row gap-10">
                <button @click="goBack" type="button" class="btn-navy">
                  ← 목록으로
                </button>
              </div>
              <div class="flex flex-row gap-2">
                <button
                  v-if="noticeInfo.isDeleted === 'O2'"
                  @click="modifyNotice"
                  class="btn-green"
                >
                  수정
                </button>
                <button @click="lockNotice" class="btn-red">
                  <el-icon><Lock /></el-icon
                  >{{ noticeInfo.isDeleted == "O1" ? "비활성 해제" : "비활성" }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref, watch } from "vue";
import { useProjectKJHStore } from "../stores/projectKJH";
import { useNoticeStore } from "../stores/notice";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { Lock } from "@element-plus/icons-vue";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const noticeStore = useNoticeStore();
const projectStore = useProjectKJHStore();

const noticeInfo = ref({});
const noticeId = route.params.noticeId;
const projectId = route.params.projectId;

const memberList = ref([]); // 구성원 테이블

// 목록으로
const goBack = () => {
  console.log(projectId);
  router.push({
    name: "noticeList",
    params: { projectId: projectId },
  });
};

// 수정 버튼
const modifyNotice = () => {
  router.push({
    name: "noticeRegister",
    params: { projectId: projectId, noticeId: noticeId },
  });
};

// 비활성 및 해제 버튼
const lockNotice = async () => {
  let lock = noticeInfo.value.isDeleted == "Q2" ? true : false;
  let isDeleted = noticeInfo.value.isDeleted == "Q2" ? "Q1" : "Q2";

  if (lock) {
    const result = await Swal.fire({
      title: "정말 비활성하시겠습니까?",
      text: "비활성한 공지사항은 목록에서 보이지 않습니다.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "비활성",
      cancelButtonText: "취소",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
  } else {
    const result = await Swal.fire({
      title: "정말 비활성화를 해제하시겠습니까?",
      text: "활성화된 공지사항은 목록에서 다시 보이게됩니다.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "활성화",
      cancelButtonText: "취소",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
  }
  await noticeStore.modifyNoticeLock(noticeId, isDeleted);
  noticeInfo.value = noticeStore.registeredNotice;
};

onBeforeMount(async () => {
  // 공지사항 및 프로젝트 정보
  await noticeStore.getNoticeById(noticeId);
  noticeInfo.value = noticeStore.noticeInfo;
});

watch(
  () => projectStore.insertedList,
  () => {
    memberList.value = projectStore.insertedList;
  },
);
</script>
<style scoped>
/* 상단 */
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
  gap: 8px;
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
  color: #0f172a;
  letter-spacing: -0.02em;
}

.proj-period {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
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
/* 목록으로 */
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
  box-shadow: 0 4px 10px rgba(30, 58, 95, 0.3);
  transform: translateY(-1px);
}
/* 수정버튼 */
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
  box-shadow: 0 2px 6px rgba(22, 163, 74, 0.25);
  letter-spacing: 0.01em;
}
.btn-green:hover {
  background: #60aee2;
  box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3);
  transform: translateY(-1px);
}

/* 비활성 버튼 */
.btn-red {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #dc2626;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.25);
  letter-spacing: 0.01em;
}
.btn-red:hover {
  background: #b91c1c;
  box-shadow: 0 4px 10px rgba(220, 38, 38, 0.3);
  transform: translateY(-1px);
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
</style>
