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
            {{ isModified == false ? "공지사항 생성" : "공지사항 수정" }}
          </h1>
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
                    label="업무 유형"
                    prop="roleId"
                    class="block text-sm font-medium mb-1"
                  >
                    <el-select v-model="form.roleId" class="input flex-1">
                      <el-option
                        v-for="role in roles"
                        :label="role.roleName"
                        :value="role.roleId"
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
                      placeholder="업무 제목을 적으세요"
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
                  <el-input
                    :rows="15"
                    class="input w-full"
                    v-model="form.content"
                    type="textarea"
                  />
                </el-form-item>
              </div>
              <div class="mb-6">
                <el-upload
                  v-model:file-list="fileList"
                  class="upload-demo"
                  action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
                  :on-change="handleChange"
                >
                  <el-button type="primary">파일선택</el-button>
                  <template #tip>
                    <div class="el-upload__tip">
                      jpg/png 파일은 최대 500kb까지 가능합니다.
                    </div>
                  </template>
                </el-upload>
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
import { useRoleStore } from "../stores/roleSJW";
import { useNoticeStore } from "../stores/notice";
import { changeDate } from "../utils/commonFunc"; // 날짜 변경 함수(utils/commonFunc 에 있음)
import Swal from "sweetalert2";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const roleStore = useRoleStore();
const noticeStore = useNoticeStore();
const sidebarOpen = ref(false);

const id = route.params.projectId;
const noticeId = route.params.noticeId;
const userInfo = ref(); // 글 작성자 정보
const roles = ref([
  // 전체 역할 정보
  { roleId: 1, roleName: "PM" },
  { roleId: 2, roleName: "상PL" },
  { roleId: 3, roleName: "하PL" },
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
const checkedBox = (event) => {
  console.log("targetvalue", event.target.checked);
  form.isPinned = event.target.checked;
  console.log(form.isPinned);
};

// 공지사항 생성 버튼
const submitForm = async (formEl) => {
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      console.log(form);
      // 공지사항 등록
      if (!isModified.value) {
        let obj = {
          projectId: id,
          title: form.title,
          content: form.content,
          isPinned: form.isPinned == true ? "B1" : "B2",
          category: form.roleId,
          createdBy: userInfo.value.userId,
        };
        await noticeStore.registerNotice(obj);
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
        };
        await noticeStore.modifyNotice(obj);
      }

      router.push({
        name: "noticeDetail",
        params: {
          projectId: id,
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
    let noticeInfo = noticeStore.noticeInfo;
    Swal.close();

    // 폼에 대입
    form.roleId = noticeInfo.category;
    form.author = noticeInfo.userName;
    form.date = noticeInfo.createdAt;
    form.title = noticeInfo.title;
    form.content = noticeInfo.content;
    form.isPinned = noticeInfo.isPinned == "B1" ? true : false;
  }
  // 생성일 때
  userInfo.value = authStore.user; // 작성자 정보 받아오기

  form.author = userInfo.value.name;

  form.date = changeDate(new Date()); // 작성 당일 날짜 생성

  await noticeStore.getProjectRoles(id);
  roles.value = noticeStore.projectRoles; // 전체 역할정보
});

const goBack = () => {
  console.log(id);
  router.push({
    name: "noticeList",
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
</style>
