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
                        label="회의 일시"
                        class="block text-sm font-medium mb-1"
                        prop="date"
                      >
                        <el-input
                          type="date"
                          v-model="form.date"
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

                  <div class="grid grid-cols-10 gap-4">
                    <div
                      :class="
                        isAiSummary || form.aiSummary != ''
                          ? 'col-span-6'
                          : 'col-span-10'
                      "
                    >
                      <el-form-item label="내용" prop="content">
                        <el-input
                          :rows="15"
                          class="input w-full"
                          v-model="form.content"
                          type="textarea"
                        />
                      </el-form-item>
                    </div>
                    <div
                      v-if="isAiSummary || form.aiSummary != ''"
                      class="col-span-4"
                    >
                      <el-form-item label="AI 요약 내용" prop="content">
                        <el-input
                          :rows="15"
                          class="input w-full"
                          v-model="form.aiSummary"
                          type="textarea"
                          :readonly="isModified"
                        />
                      </el-form-item>
                    </div>
                  </div>
                  <div class="mb-6">
                    <el-upload
                      v-model:file-list="fileList"
                      action="#"
                      :auto-upload="false"
                      class="w-full"
                      multiple
                      :on-change="handleChange"
                    >
                      <template #trigger>
                        <div
                          class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 rounded-md shadow-sm hover:bg-gray-50 cursor-pointer"
                        >
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
            <div class="side-col" v-if="!isModified">
              <!-- 업무연결 -->
              <!-- 추천업무 -->
              <div v-if="todoList.length > 0" class="card mb-5">
                <div class="card-header">
                  <span class="card-title">연결 업무 목록</span>
                  <span class="member-count"
                    >{{ connectTaskList.length }}건</span
                  >
                </div>

                <div class="member-body">
                  <template v-if="todoList.length > 0">
                    <div
                      v-for="(task, idx) in connectTaskList"
                      :key="idx"
                      class="member-item"
                    >
                      <div class="member-info">
                        <el-tooltip
                          class="box-item"
                          effect="dark"
                          :content="task.content"
                          placement="left"
                        >
                          <span class="member-name">{{ task.title }}</span>
                        </el-tooltip>
                      </div>
                      <button type="button" @click="delTask(task)">x</button>
                    </div>
                  </template>
                </div>
              </div>
              <div v-if="!isModified">
                <div class="card mb-2">
                  <div class="card-header">
                    <span class="card-title">음성파일로 내용작성</span>
                  </div>
                  <div>
                    <el-upload
                      class="upload-demo"
                      drag
                      action="#"
                      v-model:voice-list="vocieList"
                      :auto-upload="false"
                      multiple
                      :on-change="voiceChange"
                    >
                      <el-icon class="el-icon--upload"
                        ><upload-filled
                      /></el-icon>
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
                    <button
                      type="button"
                      class="btn-voice"
                      @click="getVoiceByGemmini()"
                    >
                      음성파일로 내용 작성
                    </button>
                  </div>
                </div>
              </div>

              <div class="card" v-if="!isModified">
                <div class="news-btn">
                  <button
                    type="button"
                    class="btn-sub"
                    @click="getContentByGemmini(form)"
                  >
                    AI 요약 및 업무 추천
                  </button>
                </div>
              </div>
              <!-- 추천업무 -->
              <div v-if="todoList.length > 0 && !isModified" class="card mb-5">
                <div class="card-header">
                  <span class="card-title">추천 업무</span>
                  <span class="member-count">{{ todoList.length }}건</span>
                </div>

                <div class="member-body">
                  <template v-if="todoList.length > 0">
                    <div
                      v-for="(file, idx) in todoList"
                      :key="idx"
                      class="member-item"
                    >
                      <div class="member-info">
                        <el-tooltip
                          class="box-item"
                          effect="dark"
                          :content="Object.values(file)[0]"
                          placement="left"
                        >
                          <span class="member-name">{{
                            Object.keys(file)[0]
                          }}</span>
                        </el-tooltip>
                      </div>
                      <button
                        type="button"
                        class="btn-create"
                        @click="openCreateModal(file)"
                      >
                        업무 생성
                      </button>
                    </div>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <meetingNotifirationModal
    v-model="modalOpen"
    :memberList="memberList"
    :alarmList="alarmList"
    @member-insert="memberInsert"
  />
  <meetingCreateTaskModal
    v-model="createModalOpen"
    :taskInfo="taskInfo"
    @close-create-modal="closeCreateModal"
    @register-task="registerTask"
  />
</template>

<script setup>
import { ref, onBeforeMount, reactive, watch } from "vue";
import { storeToRefs } from "pinia";
import { useRouter, useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useAuthStore } from "../stores/auth";
import { useTaskStore } from "../stores/useTaskStore";

import { useProjectKJHStore } from "../stores/projectKJH";
import { useMeetingStore } from "../stores/meeting";
import Swal from "sweetalert2";
import meetingCreateTaskModal from "./meetingCreateTaskModal.vue";
import meetingNotifirationModal from "./meetingNotificationModal.vue";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const meetingStore = useMeetingStore();
const store = useTaskStore();

const projectStore = useProjectKJHStore();
const sidebarOpen = ref(false);

const id = route.params.projectId;
const subId = route.params.subProjectId;
const meetingId = route.params.meetingId;
const userInfo = ref(); // 글 작성자 정보
const meetingType = ref([]);
const form = reactive({
  meetingType: "",
  author: "",
  date: "",
  title: "",
  content: "",
  meetingRoom: "",
  aiSummary: "",
}); // 작성내용 담을 곳

const { recommandTask } = storeToRefs(store);

let isModified = ref(false); // 수정, 생성 구분
let modalOpen = ref(false); // 알림대상 모달창
const memberList = ref([]); // 구성원 테이블
const alarmList = ref([]); // 알림대상 추가된 회원 목록
const isAiSummary = ref(false); // ai 사용했는지 확인 여부
const todoList = ref([{ 1: "1번" }, { 2: "2번" }]); // 업무 추천 목록
const isVoice = ref(false); // 음성요약 파일 잇는지 확인
const createModalOpen = ref(false); // 추천업무 생성 모달창 여는 거
const taskInfo = ref([]);
const connectTaskList = ref([]);

watch(
  () => recommandTask.value,
  (newVal) => {
    connectTaskList.value = newVal;
  },
);

// 공지사항 생성 버튼
const submitForm = async (formEl) => {
  console.log(formEl.validate);
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      // 공지사항 등록
      if (!isModified.value) {
        let obj = {
          projectId: subId != null ? subId : id,
          title: form.title,
          content: form.content,
          meetingTypeCode: form.meetingType,
          meetingDate: form.date,
          place: form.meetingRoom,
          createdBy: userInfo.value.userId,
          aiSummary: isVoice.value ? null : form.aiSummary,
          sttText: isVoice.value ? form.aiSummary : null,
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
            formData.append("files", file.raw);
            console.log("file정체", file.raw);
          });
        }
        await meetingStore.registerMeeting(formData);

        if (meetingStore.registeredMeeting.meetingLogId > 0) {
          let alarmArr = [
            {
              targetId: meetingStore.registeredMeeting.meetingLogId,
              title: "회의록이 등록되었습니다.",
              message: "등록된 회의록을 확인해주세요",
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

          await meetingStore.registerMeetingAlarm(alarmArr);

          const result = await Swal.fire({
            title: "회의록 등록 및 알림 전송이 완료되었습니다.",
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
          meetingLogId: meetingId,
          title: form.title,
          content: form.content,
          meetingTypeCode: form.meetingType,
          editorUserId: userInfo.value.userId,
          meetingDate: form.meetingDate,
          place: form.meetingRoom,
          attachmentGroupId: form.attachmentGroupId,
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

        await meetingStore.modifyMeeting(formData);
      }

      router.push({
        name: "meetingDetail",
        params: {
          projectId: subId != null ? subId : id,
          meetingId:
            isModified == true
              ? meetingId
              : meetingStore.registeredMeeting.meetingLogId,
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

// 회의록 내용 요약 받기
const getContentByGemmini = async (val) => {
  const formData = new FormData();
  if (!isVoice.value) {
    console.log(val);
    if (val.content == "" || val.content == " ") {
      const result = await Swal.fire({
        title: "내용을 작성해주세요",
        text: "",
        icon: "warning",
        confirmButtonText: "확인",
        reverseButtons: true,
      });

      return;
    }

    let prompt = val.content;

    formData.append("prompt", prompt);
  } else {
    voiceList.value.forEach((vo) => {
      formData.append("files", vo.raw);
    });
  }

  Swal.fire({
    title: "잠시만 기다려주세요...",
    html: "내용 요약 및 추천 업무를 생성 중입니다.",
    allowOutsideClick: false,
    showConfirmButton: false,
    showCancelButton: false,
    didOpen: () => {
      Swal.showLoading();
    },
  });

  await meetingStore.getContentByGemmini(formData);
  form.aiSummary = meetingStore.geminiContent;
  const todoRegex = /\[\s*{.*}\s*\]/s;

  // 2. 목록 추출 (match)
  const match = form.aiSummary.match(todoRegex);
  if (match) {
    console.log(match);
    const jsonStr = match[0].replace(/'/g, '"');
    console.log(jsonStr);
    todoList.value = JSON.parse(jsonStr);
  }

  form.aiSummary = form.aiSummary.replace(
    todoRegex,
    "(추천 업무는 우측 목록에서 확인 가능합니다)",
  );
  isAiSummary.value = true;

  Swal.close();
};

const getVoiceByGemmini = async () => {
  isVoice.value = voiceList.value.length > 0 ? true : false;

  if (!isVoice.value) {
    const result = await Swal.fire({
      title: "파일을 선택해주세요",
      text: "",
      icon: "warning",
      confirmButtonText: "확인",
      reverseButtons: true,
    });

    return;
  }

  getContentByGemmini();
};

onBeforeMount(async () => {
  userInfo.value = authStore.user; // 작성자 정보 받아오기

  // 수정 및 생성 구분
  if (meetingId !== "" && meetingId !== undefined && meetingId !== null) {
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

    await meetingStore.getMeetingById(meetingId);
    let meetingInfo = meetingStore.meetingDetail;

    // 폼에 대입
    form.meetingType = meetingInfo.meetingList.meetingTypeCode;
    form.author = meetingInfo.meetingList.userName;
    form.date = meetingInfo.meetingList.meetingDate;
    form.title = meetingInfo.meetingList.title;
    form.content = meetingInfo.meetingList.content;
    form.meetingRoom = meetingInfo.meetingList.place;
    form.attachmentGroupId = meetingInfo.meetingList.attachmentGroupId;
    form.aiSummary =
      meetingInfo.meetingList.aiSummary == null
        ? meetingInfo.meetingList.sttText
        : meetingInfo.meetingList.aiSummary;
    if (meetingInfo.attachmentList) {
      meetingInfo.attachmentList.forEach((att) => {
        let obj = {
          name: att.fileName,
          uid: att.attachmentId,
          url: att.filePath,
          status: "success",
          isExisting: true,
        };
        fileList.value.push(obj);
      });
    }

    Swal.close();
  } else {
    form.author = userInfo.value.name;
  }

  await meetingStore.getMeetingType();
  meetingType.value = meetingStore.meetingType;

  await projectStore.getAllMembers(id); // 프로젝트 구성원 정보
  memberList.value = projectStore.memberList;
});

// 추천 업무 생성 버튼
const openCreateModal = (value) => {
  taskInfo.value = {
    meetingLogId:
      connectTaskList.value.length > 0
        ? meetingStore.connectTaskList.meetingLogId
        : null,
    ...value,
  };
  createModalOpen.value = true;
};

// 추천 업무 생성 모달 취소버튼
const closeCreateModal = () => {
  createModalOpen.value = false;
};

// 추천 업무 생성 모달 생성 버튼
const registerTask = async () => {
  closeCreateModal();

  await Swal.fire({
    title: "추천업무생성이 완료되었습니다.",
    text: "",
    icon: "success",
    confirmButtonText: "확인",
    reverseButtons: true,
  });
};

// 연결 업무 x 버튼
const delTask = async (task) => {
  console.log(task);
  await meetingStore.removeConnectTask(task);
  connectTaskList.value = meetingStore.connectTaskList;
};
// 알림대상 선택
const openModal = () => {
  modalOpen.value = true;
};

// 목록으로 버튼
const goBack = () => {
  console.log(id);
  router.push({
    name: "meetingList",
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
  date: [
    {
      required: true,
      message: "회의 날짜를 선택해주세요",
      trigger: "change",
    },
  ],
});

const resetForm = (formEl) => {
  if (!formEl) return;
  formEl.resetFields();
};

// 첨부파일api(좌측)
const fileList = ref([]);

const handleChange = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles);
};

// 첨부파일api(우측)
const voiceList = ref([]);

const voiceChange = (uploadFile, uploadFiles) => {
  voiceList.value.push(uploadFile);
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
.btn-voice {
  width: 100%;
  flex: 1;
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
.btn-voice:hover {
  background: #162d4a;
  box-shadow: 0 4px 10px rgba(30, 58, 95, 0.3);
  transform: translateY(-1px);
}
/* 업무 생성 */
.btn-create {
  height: 20px;
  padding: 0 10px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 5px;
  cursor: pointer;
  border: none;
  background: #1e3a5f;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(30, 58, 95, 0.25);
}
.btn-create:hover {
  background: #162d4a;
  box-shadow: 0 4px 10px rgba(30, 58, 95, 0.3);
  transform: translateY(-1px);
}
.btn-delTask {
  height: 20px;
  padding: 0 10px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 5px;
  cursor: pointer;
  border: none;
  background: #dc2626;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.25);
}
.btn-delTask:hover {
  background: #b91c1c;
  box-shadow: 0 4px 10px rgba(220, 38, 38, 0.3);
  transform: translateY(-1px);
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

/* ────────────────────────────────────────────
   프로젝트 구성원
──────────────────────────────────────────── */
.member-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  /* padding: 10px 12px; */
}

.member-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 15px;
  flex-shrink: 0;
}

.member-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.member-name {
  font-size: 13px;
  color: #000000;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.member-role-badge {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 700;
  padding: 4px 9px;
  border-radius: 999px;
  letter-spacing: 0.02em;
  border: 1px solid transparent;
}

.role-pm {
  background: #dbeafe;
  color: #1d4ed8;
  border-color: #bfdbfe;
}

.role-pl {
  background: #ede9fe;
  color: #6d28d9;
  border-color: #ddd6fe;
}

.role-dev {
  background: #d1fae5;
  color: #065f46;
  border-color: #a7f3d0;
}

.role-qa {
  background: #fee2e2;
  color: #b91c1c;
  border-color: #fecaca;
}

.role-mgr {
  background: #e0e7ff;
  color: #3730a3;
  border-color: #c7d2fe;
}

.member-empty-row {
  padding: 18px 12px;
  text-align: center;
  border: 1px dashed #dbe2ea;
  border-radius: 12px;
  background: #fafcff;
}

.member-empty-text {
  font-size: 12px;
  color: #94a3b8;
}

.tooltip-base-box {
  width: 600px;
}
.tooltip-base-box .row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tooltip-base-box .center {
  justify-content: center;
}
.tooltip-base-box .box-item {
  width: 110px;
  margin-top: 10px;
}
</style>
