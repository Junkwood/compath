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
              >{{ info }} ›
            </span>
            <span class="bc-cur">{{
              !isModified ? "공지사항 생성" : "공지사항 수정"
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
                    label="카테고리"
                    prop="roleId"
                    class="block text-sm font-medium mb-1"
                  >
                    <el-select
                      v-model="form.roleId"
                      class="input flex-1"
                      placeholder="선택해주세요"
                    >
                      <el-option
                        v-for="type in types"
                        :label="type.typeName"
                        :value="type.taskTypeId"
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
              <div class="grid grid-cols-13 gap-4">
                <div class="col-span-12">
                  <el-form-item
                    label="제목"
                    class="block text-sm font-medium mb-1"
                    prop="title"
                  >
                    <el-input
                      placeholder="제목을 입력해주세요"
                      class="w-full"
                      v-model="form.title"
                    />
                  </el-form-item>
                </div>
                <div class="self-center">
                  <label>
                    <input
                      type="checkbox"
                      :value="form.isPinned"
                      :checked="form.isPinned"
                      @change="checkedBox($event)"
                    />
                    🚨 긴급
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
</template>

<script setup>
import { ref, onBeforeMount, reactive } from "vue";
import { useRouter, useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useAuthStore } from "../stores/auth";
import { useNoticeStore } from "../stores/notice";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useAttachmentStore } from "../stores/attachment";
import { changeDate } from "../utils/commonFunc"; // 날짜 변경 함수(utils/commonFunc 에 있음)
import Swal from "sweetalert2";
import Editor from "../components/Editor.vue";

const isRead = false;
const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const noticeStore = useNoticeStore();
const taskStore = usetaskKJHStore();
const attachmentStore = useAttachmentStore();
const sidebarOpen = ref(false);

const id = route.params.projectId;
const subId = route.params.subProjectId;
const noticeId = route.params.noticeId;
const userInfo = ref(); // 글 작성자 정보
let taskPjList = ref([]);
const projectInfo = ref([]);

const types = ref([
  // 전체 역할 정보
  { roleId: 1, roleName: "개발일정" },
  { roleId: 2, roleName: "산출물" },
  { roleId: 3, roleName: "디자인" },
  { roleId: 4, roleName: "개발" },
  { roleId: 5, roleName: "QA" },
]);

const form = reactive({
  roleId: "",
  author: "",
  date: "",
  title: "",
  content: "",
  isPinned: false,
}); // 작성내용 담을 곳

let isModified = ref(false); // 수정, 생성 구분

// 긴급 체크시
const checkedBox = event => {
  console.log("targetvalue", event.target.checked);
  form.isPinned = event.target.checked;
  console.log(form.isPinned);
};

// 공지사항 생성 버튼
const submitForm = async formEl => {
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      console.log(form);
      // 공지사항 등록
      if (!isModified.value) {
        let obj = {
          projectId: subId ? subId : id,
          title: form.title,
          content: form.content,
          isPinned: form.isPinned == true ? "B1" : "B2",
          category: form.roleId,
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
          fileList.value.forEach(file => {
            formData.append("files", file.raw);
          });
        }

        await noticeStore.registerNotice(formData);
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
          noticeId: noticeId,
          title: form.title,
          content: form.content,
          isPinned: form.emerency == true ? "B1" : "B2",
          category: form.roleId,
          isEditorUserId: userInfo.value.userId,
          attachmentGroupId:
            fileList.value.length > 0 ? form.attachmentGroupId : null,
        };

        console.log(obj);
        console.log(fileList.value.length > 0);

        const formData = new FormData();
        formData.append(
          "obj",
          new Blob([JSON.stringify(obj)], {
            type: "application/json",
          }),
        );

        if (fileList.value && fileList.value.length > 0) {
          fileList.value.forEach(file => {
            if (file.isExisting == null) {
              console.log(file);
              formData.append("files", file.raw);
            }
          });
        }

        await noticeStore.modifyNotice(formData);
      }

      router.push({
        name: "noticeDetail",
        params: {
          projectId: id,
          subProjectId: subId,
          noticeId:
            isModified == true
              ? noticeId
              : noticeStore.registeredNotice.noticeId,
        },
      });
    } else {
      // 안내 메세지 나옴
      console.log("error submit!", fields);
    }
  });
};

onBeforeMount(async () => {
  if (noticeId !== "" && noticeId !== undefined && noticeId !== null) {
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
    // 수정일때
    isModified.value = true;
    await noticeStore.getNoticeById(noticeId);
    let noticeInfo = noticeStore.noticeInfo.noticeInfo;
    let attachment = noticeStore.noticeInfo.attachmentList;

    if (attachment != null) {
      attachment.forEach(att => {
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

    // 폼에 대입
    form.roleId = noticeInfo.category;
    form.author = noticeInfo.userName;
    form.date = noticeInfo.createdAt;
    form.title = noticeInfo.title;
    form.content = noticeInfo.content;
    form.isPinned = noticeInfo.isPinned == "B1" ? true : false;
    form.attachmentGroupId = noticeInfo.attachmentGroupId;
    Swal.close();
  }
  userInfo.value = authStore.user; // 작성자 정보 받아오기

  form.author = userInfo.value.name;

  form.date = changeDate(new Date()); // 작성 당일 날짜 생성

  if (subId) {
    await taskStore.getProjectName(subId);
  } else {
    await taskStore.getProjectName(id);
  }
  projectInfo.value = taskStore.projectName;

  if (projectInfo.value.parentProjectName != null) {
    taskPjList.value = [
      projectInfo.value.parentProjectName,
      projectInfo.value.projectName,
    ];
  } else {
    taskPjList.value = [projectInfo.value.projectName];
  }

  await noticeStore.getProjectType();
  types.value = noticeStore.taskType; // 전체 역할정보
});

const goBack = () => {
  router.push({
    name: "noticeList",
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

const resetForm = formEl => {
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
    attachmentStore.removeResult.forEach(att => {
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

.bc-sep {
  color: #cbd5e1;
}

.bc-cur {
  color: #0f172a;
  font-weight: 600;
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
