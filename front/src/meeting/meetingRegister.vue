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
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-8">
            회의록{{ isModified == false ? " 생성" : " 수정" }}
          </h1>
          <div class="dashboard-top mb-5">
            <div class="card main-col">
              <el-form
                ref="ruleFormRef"
                style="max-width: 100%"
                :model="form"
                status-icon
                :rules="rules"
                label-width="auto"
                label-position="top"
              >
                <div class="bg-white dark:bg-gray-800 rounded-xl shadow p-6">
                  <div>
                    <el-form-item
                      label="제목"
                      class="block text-sm font-medium mb-1"
                      prop="title"
                    >
                      <el-input
                        placeholder="회의록 제목을 적으세요"
                        class="w-full"
                        v-model="form.title"
                      />
                    </el-form-item>
                  </div>
                  <div class="grid grid-cols-2 gap-6">
                    <div>
                      <el-form-item
                        label="회의 유형"
                        prop="meetingType"
                        class="block text-sm font-medium mb-1"
                      >
                        <el-select
                          v-model="form.meetingType"
                          class="input flex-1"
                          placeholder="유형을 선택하세요"
                        >
                          <el-option
                            v-for="type in meetingType"
                            :label="type.typeName"
                            :value="type.typeCode"
                          />
                        </el-select>
                      </el-form-item>
                    </div>
                    <div>
                      <el-form-item
                        label="작성자"
                        class="block text-sm font-medium mb-1"
                      >
                        <el-input
                          disabled
                          class="input w-full"
                          v-model="form.author"
                        />
                      </el-form-item>
                    </div>

                    <div>
                      <el-form-item
                        label="등록일"
                        class="block text-sm font-medium mb-1"
                      >
                        <el-input
                          type="date"
                          v-model="form.estStartDate"
                          class="w-full"
                        />
                      </el-form-item>
                    </div>
                    <div>
                      <el-form-item
                        label="회의 장소"
                        prop="meetingRoom"
                        class="block text-sm font-medium mb-1"
                      >
                        <el-input
                          placeholder="회의 장소를 적으세요"
                          class="w-full"
                          v-model="form.meetingRoom"
                        />
                      </el-form-item>
                    </div>
                  </div>

                  <div>
                    <el-form-item label="내용" prop="content">
                      <el-input
                        :rows="15"
                        class="input w-full"
                        v-model="form.content"
                        type="textarea"
                      />
                    </el-form-item>
                  </div>
                  <div class="mb-6">
                    <el-upload action="#" :auto-upload="false" class="w-full">
                      <template #trigger>
                        <div
                          class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 cursor-pointer"
                        >
                          <i class="el-icon-paperclip text-gray-500"></i>
                          <span class="text-sm font-medium text-gray-700"
                            >파일 선택</span
                          >
                        </div>
                      </template>

                      <template #tip>
                        <div class="text-xs text-gray-400 mt-2">
                          MP3, WAV 파일 최대 50MB까지 가능
                        </div>
                      </template>
                    </el-upload>
                  </div>
                  <div v-if="!isModified" class="notification-area">
                    <el-button
                      type="button"
                      class="btn-select-custom"
                      @click="openModal"
                    >
                      참석자 선택
                    </el-button>

                    <div class="flex flex-wrap gap-2">
                      <el-tag
                        v-for="tag in alarmList"
                        :key="tag.userId"
                        closable
                        :disable-transitions="true"
                        @close="handleClose(tag)"
                        class="custom-alarm-tag"
                      >
                        {{ tag.userName }}
                      </el-tag>
                      <span
                        v-if="alarmList.length === 0"
                        class="text-sm text-gray-400 font-medium"
                      >
                        대상을 선택하면 여기에 표시됩니다.
                      </span>
                    </div>
                  </div>

                  <div class="flex justify-between">
                    <button @click="goBack" type="button" class="btn-navy">
                      ← 목록으로
                    </button>
                    <div class="flex gap-2">
                      <button @click="resetForm" type="button" class="btn-red">
                        초기화
                      </button>
                      <button
                        type="button"
                        @click="submitForm(ruleFormRef)"
                        class="btn-green"
                      >
                        {{ isModified == false ? "등록" : "수정" }}
                      </button>
                    </div>
                  </div>
                </div>
              </el-form>
            </div>
            <!-- 우측 카드 묶음 -->
            <div class="side-col">
              <!-- 소요시간 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">업무연결</span>
                </div>
                <div class="h-32 place-items-center">
                  <div class="h-20"></div>
                  <div>
                    <button @click="registerActualTime" class="btn-navy">
                      <span class="text-lg"></span>업무 추가
                    </button>
                  </div>
                </div>
              </div>
              <div class="card">
                <div class="card-header">
                  <span class="card-title">음성파일로 내용작성</span>
                </div>
                <div>
                  <el-upload
                    class="upload-demo"
                    drag
                    action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                    multiple
                  >
                    <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                    <div class="el-upload__text">
                      Drop file here or <em>click to upload</em>
                    </div>
                    <template #tip class="flex items-center">
                      <div class="el-upload__tip">
                        음성파일은 최대 500kb까지 가능합니다.
                      </div>
                    </template>
                  </el-upload>
                </div>
              </div>
              <div class="card">
                <div class="news-btn">
                  <button type="button" class="btn-sub">
                    AI 요약 및 업무 추천
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <!-- <meetingNotifirationModal
    v-model="modalOpen"
    :memberList="memberList"
    :alarmList="alarmList"
    @member-insert="memberInsert"
  /> -->
</template>

<script setup>
import { ref, onBeforeMount, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useAuthStore } from "../stores/auth";

import { useProjectKJHStore } from "../stores/projectKJH";
import { useMeetingStore } from "../stores/meeting";
import { changeDate } from "../utils/commonFunc"; // 날짜 변경 함수(utils/commonFunc 에 있음)
import Swal from "sweetalert2";
import { useDocumentStore } from "../stores/document";
// import { meetingNotifirationModal } from "./meetingNotificationModal.vue";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const documentStore = useDocumentStore();
const meetingStore = useMeetingStore();

const projectStore = useProjectKJHStore();
const sidebarOpen = ref(false);

const id = route.params.projectId;
const documentId = route.params.documentId;
const userInfo = ref(); // 글 작성자 정보
const meetingType = ref([]);
const form = reactive({
  meetingType: "",
  author: "",
  date: "",
  title: "",
  content: "",
  meetingRoom: "",
}); // 작성내용 담을 곳

let isModified = ref(false); // 수정, 생성 구분
let modalOpen = ref(false); // 알림대상 모달창
const memberList = ref([]); // 구성원 테이블
const alarmList = ref([]); // 알림대상 추가된 회원 목록

// 공지사항 생성 버튼
const submitForm = async (formEl) => {
  console.log(formEl.validate);
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // 공지사항 등록
      if (!isModified.value) {
        let obj = {
          projectId: id,
          title: form.title,
          content: form.content,
          isPinned: form.isPinned == true ? "O1" : "O2",
          isComment: form.isComment == true ? "O2" : "O1",
          category: form.roleId == "전체" ? null : form.roleId,
          createdBy: userInfo.value.userId,
        };
        await documentStore.registerDocument(obj);

        if (
          documentStore.registeredDocument.documentId > 0 &&
          alarmList.value.length > 0
        ) {
          let alarmArr = [
            {
              targetId: documentStore.registeredDocument.documentId,
              title: "문서를 등록되었습니다.",
              message: "문서를 확인해주세요",
              createdBy: userInfo.value.userId,
            },
          ];

          alarmList.value.forEach((al) => {
            alarmArr.push({
              receiverId: al.userId,
              notificationId: "",
            });
          });

          await documentStore.registerDocumentAlarm(alarmArr);

          const result = await Swal.fire({
            title: "등록 및 알림 전송이 완료되었습니다.",
            text: "상세페이지로 이동합니다.",
            icon: "success",
            confirmButtonText: "확인",
            reverseButtons: true,
          });
        }
      } else {
        const result = await Swal.fire({
          title: "정말 수정하시겠습니까?",
          text: "",
          icon: "warning",
          showCancelButton: true,
          confirmButtonText: "수정",
          cancelButtonText: "취소",
          reverseButtons: true,
        });

        if (!result.isConfirmed) return;
        // 공지사항 수정
        let obj = {
          documentId: id,
          title: form.title,
          content: form.content,
          isPinned: form.isPinned == true ? "O1" : "O2",
          isComment: form.isComment == true ? "O2" : "O1",
          category: form.roleId == "전체" ? null : form.roleId,
          isEditorUserId: userInfo.value.userId,
        };
        await documentStore.modifyDocument(obj);
      }

      router.push({
        name: "documentDetail",
        params: {
          projectId: id,
          documentId:
            isModified == true
              ? documentId
              : documentStore.registeredDocument.documentId,
        },
      });
    } else {
      // 안내 메세지 나옴
      console.log("error submit!", fields);
    }
  });
};

// 알림대상 모달 추가버튼 데이터 받기\
const memberInsert = (mem) => {
  modalOpen.value = false;
  alarmList.value = mem;
};

const handleClose = (tag) => {
  alarmList.value.splice(alarmList.value.indexOf(tag), 1);
};

onBeforeMount(async () => {
  // 수정 및 생성 구분
  if (documentId !== "" && documentId !== undefined && documentId !== null) {
    isModified.value = true;
    Swal.fire({
      title: "잠시만 기다려주세요...",
      html: "데이터를 불러오는 중입니다.",
      allowOutsideClick: false,
      showConfirmButton: false,
      showCancelButton: false,
      didOpen: () => {
        Swal.showLoading();
      },
    });

    await documentStore.getDocumentById(documentId);
    let documentInfo = documentStore.documentDetail.documentInfo;

    // 폼에 대입
    form.roleId =
      documentInfo.category == null ? "전체" : documentInfo.category;
    form.author = documentInfo.userName;
    form.date = documentInfo.createdAt;
    form.title = documentInfo.title;
    form.content = documentInfo.content;
    form.isPinned = documentInfo.isPinned == "O1" ? true : false;
    form.isComment = documentInfo.isComment == "O1" ? false : true;

    Swal.close();
  }
  userInfo.value = authStore.user; // 작성자 정보 받아오기

  form.author = userInfo.value.name;

  form.date = changeDate(new Date()); // 작성 당일 날짜 생성

  await meetingStore.getMeetingType();
  meetingType.value = meetingStore.meetingType;

  await projectStore.getAllMembers(id); // 프로젝트 구성원 정보
  memberList.value = projectStore.memberList;
});

// 알림대상 선택
const openModal = () => {
  modalOpen.value = true;
};

// 목록으로 버튼
const goBack = () => {
  console.log(id);
  router.push({
    name: "meetingList",
    params: { projectId: id },
  });
};

// 유효성 체크
const ruleFormRef = ref();

const rules = reactive({
  title: [
    {
      required: true,
      message: "제목을 입력해주세요",
      trigger: "change",
    },
  ],
  meetingType: [
    {
      required: true,
      message: "회의유형을 선택해주세요",
      trigger: "change",
    },
  ],
  content: [
    {
      required: true,
      message: "내용을 입력해주세요",
      trigger: "change",
    },
  ],
  meetingRoom: [
    {
      required: true,
      message: "회의 장소를 입력해주세요",
      trigger: "change",
    },
  ],
});

const resetForm = (formEl) => {
  if (!formEl) return;
  formEl.resetFields();
};

// 첨부파일api
const fileList = ref([
  {
    name: "food.jpeg",
    url: "https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100",
  },
]);

const handleChange = (uploadFile, uploadFiles) => {
  fileList.value = fileList.value.slice(-3);
};
</script>

<style scoped>
:deep(.el-input__inner) {
  height: 42px;
}
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  box-shadow: none !important; /* 기본 shadow 제거 */
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
  height: 42px;
}

:deep(.el-textarea__inner) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  box-shadow: none !important; /* 기본 shadow 제거 */
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
}

/* 2. 포커스 시 스타일 */
:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus),
:deep(.el-select__wrapper.is-focused) {
  border-color: #94a3b8 !important;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15) !important;
  background: #fff !important;
}
.el-form-item {
  --font-size: 14px;
  margin-bottom: 18px;
}

/* 3. 비활성화(disabled) 상태 */
:deep(.el-input.is-disabled .el-input__wrapper) {
  background: #f1f5f9 !important;
  border-color: #e2e8f0 !important;
}

:deep(.el-input.is-disabled .el-input__inner) {
  color: #475569 !important;
  -webkit-text-fill-color: #475569 !important; /* iOS/Safari 대응 */
}

/* 4. 텍스트 영역(textarea) 높이 및 스타일 */
:deep(.el-textarea__inner) {
  padding: 12px !important;
}
.btn-select {
  height: 38px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  white-space: nowrap;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.2s;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}
.btn-select:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
}
.btn-confirm {
  height: 38px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  white-space: nowrap;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.2s;
}
.btn-confirm:hover {
  background: #e2e8f0;
  color: #1e293b;
}
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
}
.btn-red:hover {
  background: #b91c1c;
  box-shadow: 0 4px 10px rgba(220, 38, 38, 0.3);
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
  box-shadow: 0 2px 6px rgba(22, 163, 74, 0.25);
}
.btn-green:hover {
  background: #60aee2;
  box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3);
  transform: translateY(-1px);
}

.btn-select-custom {
  height: 40px !important;
  padding: 0 16px !important;
  font-size: 13px !important;
  font-weight: 700 !important; /* 글씨 더 두껍게 */
  border-radius: 8px !important;
  background-color: #ffffff !important;
  /* 이미지의 '등록/수정' 버튼 컬러 계열로 테두리 지정 */
  border: 1.5px solid #374151 !important;
  color: #374151 !important;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-select-custom:hover {
  transform: translateY(-1px);
}
/* 1. 알림 영역 전체 박스 (더 깔끔한 느낌) */
.notification-area {
  margin-bottom: 24px;
  padding: 16px 20px;
  background-color: #f8fafc; /* 매우 연한 회색 */
  border: 1px solid #e2e8f0; /* 점선 대신 실선으로 더 견고하게 */
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 2. 알림대상 선택 버튼 (기존 어두운 버튼과 조화) */
.btn-select-custom {
  height: 36px !important;
  padding: 0 14px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  border-radius: 8px !important;
  background-color: #ffffff !important;
  border: 1px solid #cbd5e1 !important;
  color: #475569 !important;
  transition: all 0.2s;
}

.btn-select-custom:hover {
  background-color: #f1f5f9 !important;
  border-color: #94a3b8 !important;
  color: #1e293b !important;
}

.notification-area {
  margin-bottom: 24px;
  padding: 16px 20px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 태그: 진한 테두리와 차분한 배경 */
:deep(.custom-alarm-tag) {
  height: 32px;
  padding: 0 12px;
  border-radius: 6px;
  background-color: #ffffff !important; /* 깔끔한 흰색 배경 */
  border: 1.5px solid #475569 !important; /* 진한 슬레이트(남회색) 테두리 */
  color: #1e293b !important; /* 진한 텍스트 */
  font-weight: 600;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
}

/* 태그 우측 X 버튼 기본 설정 */
:deep(.custom-alarm-tag .el-tag__close) {
  color: #64748b !important; /* 기본 X 색상 */
  font-size: 14px;
  margin-left: 6px;
  transition: transform 0.2s ease-in-out !important; /* 확대 애니메이션 설정 */
}

/* X 호버 시: 색상 변화 없이 크기만 살짝 확대 */
:deep(.custom-alarm-tag .el-tag__close:hover) {
  background-color: transparent !important; /* 배경색 변화 제거 */
  color: #0f172a !important; /* 호버 시에만 조금 더 진하게 */
  transform: scale(1.3); /* 1.3배 확대 */
}

/* (참고) 알림대상 선택 버튼도 진한 테두리로 통일 */
.btn-select-custom {
  height: 36px !important;
  padding: 0 14px !important;
  font-size: 13px !important;
  font-weight: 700 !important;
  border-radius: 8px !important;
  background-color: #ffffff !important;
  border: 1.5px solid #374151 !important;
  color: #374151 !important;
  cursor: pointer;
}
/* ── 상단 레이아웃 ── */
.dashboard-top {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 230px;
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

/* 우측에 버튼들 */
.btn-sub {
  width: 100%;
  flex: 1;
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #7c3aed;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(124, 58, 237, 0.25);
}
.btn-sub:hover {
  background: #6d28d9;
}
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
  color: #475569 !important;
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
</style>
