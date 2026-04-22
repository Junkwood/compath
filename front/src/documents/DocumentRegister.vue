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
            <span>홈</span><span class="bc-sep">›</span> ><span
              v-for="info in taskPjList"
              :key="info"
              >{{ info }} › </span
            ><span class="bc-sep">›</span>
            <span class="bc-cur">{{
              !isModified ? "문서 생성" : "문서 수정"
            }}</span>
          </div>
          <button class="btn-back" @click="goBack">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path
                d="M19 12H5M11 6l-6 6 6 6"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
            목록으로
          </button>
        </div>

        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
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
              <div class="grid grid-cols-3 gap-6">
                <div>
                  <el-form-item
                    label="문서 유형"
                    prop="roleId"
                    class="block text-sm font-medium mb-1"
                  >
                    <el-select
                      v-model="form.typeId"
                      class="input flex-1"
                      placeholder="유형을 선택하세요"
                    >
                      <el-option value="전체" label="전체">전체</el-option>
                      <el-option
                        v-for="role in type"
                        :label="role.typeName"
                        :value="role.taskTypeId"
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
                      disabled
                      class="input w-full"
                      v-model="form.date"
                    />
                  </el-form-item>
                </div>
              </div>
              <div class="grid grid-cols-20 gap-4">
                <div
                  :class="
                    !isModified || (isModified && form.commentCount == 0)
                      ? 'col-span-17'
                      : 'col-span-18'
                  "
                >
                  <el-form-item
                    label="제목"
                    class="block text-sm font-medium mb-1"
                    prop="title"
                  >
                    <el-input
                      placeholder="업무 제목을 적으세요"
                      class="w-full"
                      v-model="form.title"
                    />
                  </el-form-item>
                </div>
                <div
                  :class="
                    !isModified || (isModified && form.commentCount == 0)
                      ? 'self-center col-span-3'
                      : 'self-center col-span-2'
                  "
                >
                  <label class="mx-2">
                    <input
                      type="checkbox"
                      :value="form.isPinned"
                      :checked="form.isPinned"
                      @change="checkedPin($event)"
                    />
                    <span class="text-lg">📌</span><span>상단고정</span>
                  </label>
                  <label
                    v-if="!isModified || (isModified && form.commentCount == 0)"
                  >
                    <input
                      type="checkbox"
                      :value="form.isComment"
                      :checked="form.isComment"
                      @change="checkedComment($event)"
                    />
                    <span class="text-lg">🔒</span><span>댓글잠금</span>
                  </label>
                </div>
              </div>
              <div>
                <el-form-item label="내용" prop="content">
                  <div class="editor-wrap">
                    <Editor
                      :modelValue="form.content"
                      :isRead="isRead"
                      @update:current-page="form.content = $event"
                    />
                  </div>
                </el-form-item>
              </div>
              <div class="mb-8">
                <div class="flex items-center gap-3 mb-3">
                  <span class="text-sm font-semibold text-gray-700"
                    >첨부 파일</span
                  >
                  <el-upload
                    v-model:file-list="fileList"
                    action="#"
                    :auto-upload="false"
                    :show-file-list="false"
                    multiple
                    :on-change="handleChange"
                  >
                    <template #trigger>
                      <button type="button" class="btn-select-custom text-xs">
                        파일 추가
                      </button>
                    </template>
                  </el-upload>
                </div>
                <div class="border rounded-lg overflow-hidden border-gray-200">
                  <div
                    v-for="(file, index) in fileList"
                    :key="index"
                    class="flex items-center justify-between p-3 bg-white border-b last:border-b-0 hover:bg-gray-50 transition-colors"
                  >
                    <div class="flex items-center gap-3 overflow-hidden">
                      <span class="text-red-500 font-bold text-xs uppercase">{{
                        file.name.split(".").pop()
                      }}</span>
                      <span class="text-sm text-gray-700 truncate">{{
                        file.name
                      }}</span>
                    </div>
                    <div class="flex items-center gap-4 text-xs text-gray-400">
                      <span>{{
                        file.size ? (file.size / 1024).toFixed(1) + " KB" : ""
                      }}</span>
                      <button
                        type="button"
                        @click="removeFile(file, index)"
                        class="text-gray-400 hover:text-red-500"
                      >
                        <i class="el-icon-delete"></i> 삭제
                      </button>
                    </div>
                  </div>
                  <div
                    v-if="fileList.length === 0"
                    class="p-4 text-center text-xs text-gray-400"
                  >
                    첨부된 파일이 없습니다.
                  </div>
                </div>
              </div>
              <div v-if="!isModified" class="notification-area">
                <el-button
                  type="button"
                  class="btn-select-custom"
                  @click="openModal"
                >
                  알림대상 선택
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
                <div></div>
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
      </main>
    </div>
  </div>

  <DocumentNotificationModal
    v-model="modalOpen"
    :memberList="memberList"
    :alarmList="alarmList"
    @member-insert="memberInsert"
  />
</template>

<script setup>
import { ref, onBeforeMount, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useAuthStore } from "../stores/auth";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useNoticeStore } from "../stores/notice";

import { useProjectKJHStore } from "../stores/projectKJH";
import { changeDate } from "../utils/commonFunc"; // 날짜 변경 함수(utils/commonFunc 에 있음)
import Swal from "sweetalert2";
import { useDocumentStore } from "../stores/document";
import { useAttachmentStore } from "../stores/attachment";
import DocumentNotificationModal from "./DocumentNotificationModal.vue";
import Editor from "../components/Editor.vue";

const isRead = false;
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const noticeStore = useNoticeStore();
const documentStore = useDocumentStore();
const taskStore = usetaskKJHStore();
const attachmentStore = useAttachmentStore();

const projectStore = useProjectKJHStore();
const sidebarOpen = ref(false);

let taskPjList = ref([]);
const id = route.params.projectId;
const subId = route.params.subProjectId;
const documentId = route.params.documentId;
const userInfo = ref(); // 글 작성자 정보
const type = ref([]);
const form = reactive({
  typeId: "전체",
  author: "",
  date: "",
  title: "",
  content: "",
  isPinned: false,
  isComment: false,
}); // 작성내용 담을 곳

let isModified = ref(false); // 수정, 생성 구분
let modalOpen = ref(false); // 알림대상 모달창
const memberList = ref([]); // 구성원 테이블
const alarmList = ref([]); // 알림대상 추가된 회원 목록

// 상단고정 체크시
const checkedPin = (event) => {
  form.isPinned = event.target.checked;
};

// 댓글잠금 체크시
const checkedComment = (event) => {
  form.isComment = event.target.checked;
};

// 공지사항 생성 버튼
const submitForm = async (formEl) => {
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // 공지사항 등록
      if (!isModified.value) {
        let obj = {
          projectId: subId ? subId : id,
          title: form.title,
          content: form.content,
          isPinned: form.isPinned == true ? "O1" : "O2",
          isComment: form.isComment == true ? "O2" : "O1",
          category: form.typeId == "전체" ? null : form.typeId,
          createdBy: userInfo.value.userId,
        };

        // formData에 게시글 정보 담기
        const formData = new FormData();
        formData.append(
          "obj",
          new Blob([JSON.stringify(obj)], {
            type: "application/json",
          }),
        );

        // 첨부파일 있을 경우 담기
        if (fileList.value && fileList.value.length > 0) {
          fileList.value.forEach((file) => {
            formData.append("files", file.raw);
          });
        }

        await documentStore.registerDocument(formData);

        if (documentStore.registeredDocument.documentId > 0) {
          let alarmArr = [
            {
              targetId: documentStore.registeredDocument.documentId,
              title: "문서를 등록되었습니다.",
              message: "문서를 확인해주세요",
              createdBy: userInfo.value.userId,
            },
          ];

          if (alarmList.value.length > 0) {
            alarmList.value.forEach((al) => {
              alarmArr.push({
                receiverId: al.userId,
                notificationId: "",
              });
            });
          } else {
            memberList.value.forEach((al) => {
              alarmArr.push({
                receiverId: al.userId,
                notificationId: "",
              });
            });
          }

          await documentStore.registerDocumentAlarm(alarmArr);

          await Swal.fire({
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
          documentId: documentId,
          title: form.title,
          content: form.content,
          isPinned: form.isPinned == true ? "O1" : "O2",
          isComment: form.isComment == true ? "O2" : "O1",
          category: form.typeId == "전체" ? null : form.typeId,
          isEditorUserId: userInfo.value.userId,
          attachmentGroupId:
            fileList.value.length > 0 ? form.attachmentGroupId : null,
        };

        const formData = new FormData();
        formData.append(
          "obj",
          new Blob([JSON.stringify(obj)], {
            type: "application/json",
          }),
        );

        if (fileList.value && fileList.value.length > 0) {
          fileList.value.forEach((file) => {
            if (file.isExisting == null) {
              console.log(file);
              formData.append("files", file.raw);
            }
          });
        }

        await documentStore.modifyDocument(formData);
      }

      router.push({
        name: "documentDetail",
        params: {
          projectId: id,
          subProjectId: subId,
          documentId:
            isModified == true
              ? documentId
              : documentStore.registeredDocument.documentId,
        },
      });
    } else {
      // 안내 메세지 나옴
      console.err("error submit!", fields);
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
  // 수정일때
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
    let attachment = documentStore.documentDetail.attachmentList;
    let documentInfo = documentStore.documentDetail.documentInfo.documentInfo;
    let commentCount =
      documentStore.documentDetail.documentInfo.commentInfo.length;
    // 폼에 대입
    form.typeId =
      documentInfo.category == null ? "전체" : documentInfo.category;
    form.author = documentInfo.userName;
    form.date = documentInfo.createdAt;
    form.title = documentInfo.title;
    form.content = documentInfo.content;
    form.isPinned = documentInfo.isPinned == "O1" ? true : false;
    form.isComment = documentInfo.isComment == "O1" ? false : true;
    form.attachmentGroupId = documentInfo.attachmentGroupId;
    form.commentCount = commentCount;

    if (attachment != null) {
      attachment.forEach((att) => {
        let obj = {
          name: att.fileName,
          uid: att.attachmentId,
          url: att.filePath,
          status: "success",
          isExisting: true,
          attId: att.attachmentId,
          attGId: att.attachmentGroupId,
        };
        fileList.value.push(obj);
      });
    }

    Swal.close();
  }
  // 생성일 때
  userInfo.value = authStore.user; // 작성자 정보 받아오기

  form.author = userInfo.value.name;

  form.date = changeDate(new Date()); // 작성 당일 날짜 생성

  await noticeStore.getProjectType(id);
  type.value = noticeStore.taskType; // 전체 역할정보

  await projectStore.getAllMembers(id); // 프로젝트 구성원 정보
  memberList.value = projectStore.memberList;

  if (subId) {
    await taskStore.getProjectName(subId);
  } else {
    await taskStore.getProjectName(id);
  }
  const projectInfo = taskStore.projectName;

  if (projectInfo.parentProjectName != null) {
    taskPjList.value = [projectInfo.parentProjectName, projectInfo.projectName];
  } else {
    taskPjList.value = [projectInfo.projectName];
  }
});

// 알림대상 선택
const openModal = () => {
  modalOpen.value = true;
};

// 목록으로 버튼
const goBack = () => {
  router.push({
    name: "documentList",
    params: { projectId: id, subProjectId: subId },
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
  content: [
    {
      required: true,
      message: "내용을 입력해주세요",
      trigger: "change",
    },
  ],
});

const resetForm = (formEl) => {
  if (!formEl) return;
  formEl.resetFields();
};

// 첨부파일api
const fileList = ref([]);

const handleChange = (uploadFile, uploadFiles) => {
  fileList.value = fileList.value.slice(-3);
};

/// 첨부파일 삭제
const removeFile = async (file, index) => {
  if (file.isExisting == null) {
    fileList.splice(index, 1);
  } else {
    let obj = { attachmentId: file.attId, attachmentGroupId: file.attGId };
    await attachmentStore.removeFile(obj);

    fileList.value = [];
    attachmentStore.removeResult.forEach((att) => {
      let obj = {
        name: att.fileName,
        uid: att.attachmentId,
        url: att.filePath,
        status: "success",
        isExisting: true,
        attId: att.attachmentId,
        attGId: att.attachmentGroupId,
      };
      fileList.value.push(obj);
    });
  }
};
</script>

<style scoped>
.sub-header {
  background: #ffffff;
  padding: 12px 32px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sub-header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
}

/* 목록 */
.btn-back {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 30px;
  padding: 0 12px;
  font-size: 13px;
  font-weight: 600;
  background: #ffffff;
  color: #334155;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
  flex-shrink: 0;
}

.btn-back:hover {
  background: #f1f5f9;
  border-color: #94a3b8;
  color: #0f172a;
}

.bc-sep {
  color: #cbd5e1;
}

.bc-cur {
  color: #0f172a;
  font-weight: 600;
}
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

.btn-select-custom {
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 6px;
  background: #fff;
  border: 1.5px solid #475569;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-select-custom:hover {
  background: #f8fafc;
  color: #1e293b;
  border-color: #1e293b;
}
.editor-wrap {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

:deep(.el-form-item__content) {
  display: contents;
}
</style>
