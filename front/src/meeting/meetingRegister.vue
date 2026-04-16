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
          <div class="grid grid-cols-10 gap-6">
            <div :class="isModified ? 'col-span-10 lg' : 'col-span-7'">
              <el-form
                ref="ruleFormRef"
                :model="form"
                status-icon
                :rules="rules"
                label-width="auto"
                label-position="top"
              >
                <div
                  class="bg-white dark:bg-gray-800 rounded-xl shadow-md p-6 border border-gray-100"
                >
                  <el-form-item label="제목" prop="title" class="mb-6">
                    <el-input
                      placeholder="회의 제목을 입력하세요"
                      v-model="form.title"
                      class="w-full"
                    />
                  </el-form-item>

                  <div class="grid grid-cols-2 gap-6">
                    <el-form-item label="회의 유형" prop="meetingType">
                      <el-select
                        v-model="form.meetingType"
                        placeholder="유형을 선택하세요"
                        class="w-full"
                      >
                        <el-option
                          v-for="type in meetingType"
                          :key="type.typeCode"
                          :label="type.typeName"
                          :value="type.typeCode"
                        />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="작성자">
                      <el-input disabled v-model="form.author" class="w-full" />
                    </el-form-item>
                  </div>

                  <div class="grid grid-cols-2 gap-6">
                    <el-form-item label="회의 일시" prop="date">
                      <TaskDatePicker
                        placeholder="시작일 선택"
                        v-model="form.date"
                        @change="calcEstTime(true)"
                      />
                    </el-form-item>
                    <el-form-item label="회의 장소" prop="meetingRoom">
                      <el-input
                        placeholder="회의 장소를 입력하세요"
                        v-model="form.meetingRoom"
                        class="w-full"
                      />
                    </el-form-item>
                  </div>

                  <div class="mb-6">
                    <el-form-item label="내용" prop="content">
                      <el-input
                        :rows="15"
                        v-model="form.content"
                        type="textarea"
                        class="w-full"
                      />
                    </el-form-item>
                  </div>

                  <div v-if="!isModified" class="mb-8">
                    <div class="flex flex-wrap gap-2 mb-2">
                      <span class="text-sm font-semibold text-gray-700"
                        >참석자 선택</span
                      >
                      <button
                        type="button"
                        class="btn-select-custom px-3 py-1"
                        @click="openModal"
                      >
                        추가
                      </button>
                    </div>
                    <div
                      class="flex flex-wrap gap-2 p-3 bg-gray-50 border border-dashed border-gray-300 rounded-lg min-h-[50px]"
                    >
                      <el-tag
                        v-for="tag in alarmList"
                        :key="tag.userId"
                        closable
                        class="custom-alarm-tag"
                        @close="handleClose(tag)"
                      >
                        {{ tag.userName }}
                      </el-tag>
                      <span
                        v-if="alarmList.length === 0"
                        class="text-xs text-gray-400 self-center"
                        >대상을 선택하면 여기에 표시됩니다.</span
                      >
                    </div>
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
                          <button
                            type="button"
                            class="btn-select-custom text-xs"
                          >
                            파일 추가
                          </button>
                        </template>
                      </el-upload>
                    </div>
                    <div
                      class="border rounded-lg overflow-hidden border-gray-200"
                    >
                      <div
                        v-for="(file, index) in fileList"
                        :key="index"
                        class="flex items-center justify-between p-3 bg-white border-b last:border-b-0 hover:bg-gray-50 transition-colors"
                      >
                        <div class="flex items-center gap-3 overflow-hidden">
                          <span
                            class="text-red-500 font-bold text-xs uppercase"
                            >{{ file.name.split(".").pop() }}</span
                          >
                          <span class="text-sm text-gray-700 truncate">{{
                            file.name
                          }}</span>
                        </div>
                        <div
                          class="flex items-center gap-4 text-xs text-gray-400"
                        >
                          <span>{{
                            file.size
                              ? (file.size / 1024).toFixed(1) + " KB"
                              : "Existing"
                          }}</span>
                          <button
                            type="button"
                            @click="fileList.splice(index, 1)"
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

                  <div class="flex justify-between pt-4 border-t">
                    <div></div>
                    <div class="flex gap-2">
                      <button
                        @click="resetForm(ruleFormRef)"
                        type="button"
                        class="btn-red"
                      >
                        초기화
                      </button>
                      <button
                        @click="submitForm(ruleFormRef)"
                        type="button"
                        class="btn-green"
                      >
                        {{ isModified == false ? "등록" : "수정" }}
                      </button>
                    </div>
                  </div>
                </div>
              </el-form>
            </div>

            <div class="col-span-10 lg:col-span-3 flex flex-col gap-5">
              <div
                v-if="!isModified"
                class="card shadow-md border border-gray-100"
              >
                <div
                  class="card-header bg-gray-50 flex items-center justify-between py-3 px-4"
                >
                  <div class="flex items-center gap-2">
                    <span class="card-title">연결된 업무</span>
                    <span
                      class="bg-gray-200 text-gray-600 text-[10px] px-2 py-0.5 rounded-full"
                      >{{ connectTaskList.length }}건</span
                    >
                  </div>
                  <button
                    type="button"
                    class="text-xs font-bold text-blue-600 hover:underline"
                    @click="openConnectTaskModal()"
                  >
                    업무 추가
                  </button>
                </div>
                <div class="p-4 min-h-[60px]">
                  <div
                    v-for="(task, idx) in connectTaskList"
                    :key="idx"
                    class="flex items-center justify-between mb-2 pb-2 border-b last:border-0 border-gray-50"
                  >
                    <span
                      class="text-sm text-gray-600 truncate max-w-[150px]"
                      >{{ task.title }}</span
                    >
                    <button
                      type="button"
                      class="text-gray-400 hover:text-red-500"
                      @click="delTask(task)"
                    >
                      ×
                    </button>
                  </div>
                  <div
                    v-if="connectTaskList.length === 0"
                    class="text-center text-xs text-gray-400 py-4 font-light"
                  >
                    연결된 업무가 없습니다.
                  </div>
                </div>
              </div>

              <div
                v-if="!isModified"
                class="card shadow-md border border-purple-100"
              >
                <div class="card-header bg-purple-50 py-3 px-4">
                  <span class="card-title text-purple-700">AI 회의 도우미</span>
                </div>
                <div class="p-4 flex flex-col gap-4">
                  <el-upload
                    class="upload-demo"
                    drag
                    action="#"
                    :auto-upload="false"
                    multiple
                    :on-change="voiceChange"
                  >
                    <el-icon class="el-icon--upload text-purple-400"
                      ><upload-filled
                    /></el-icon>
                    <div class="el-upload__text text-xs">
                      드래그 앤 드롭 또는 <em>클릭하여 업로드</em>
                      <p class="text-[10px] text-gray-400 mt-1">
                        (최대 50MB, MP3/WAV/AAC)
                      </p>
                    </div>
                  </el-upload>

                  <button
                    type="button"
                    class="btn-voice w-full"
                    @click="getVoiceByGemmini()"
                  >
                    음성파일로 내용 작성
                  </button>
                  <button
                    type="button"
                    class="btn-sub w-full"
                    @click="getContentByGemmini(form)"
                  >
                    AI 요약 및 업무 추천
                  </button>
                </div>
              </div>

              <div
                v-if="isAiSummary || form.aiSummary != ''"
                class="card shadow-md border border-gray-100"
              >
                <div class="card-header bg-gray-50 py-3 px-4">
                  <span class="card-title text-blue-600">AI 요약 결과</span>
                </div>
                <div class="p-4">
                  <el-input
                    :rows="8"
                    v-model="form.aiSummary"
                    type="textarea"
                    :readonly="isModified"
                    class="text-xs"
                  />
                </div>
              </div>

              <div
                v-if="todoList.length > 0 && !isModified"
                class="card shadow-md border border-gray-100"
              >
                <div
                  class="card-header bg-gray-50 flex items-center justify-between py-3 px-4"
                >
                  <span class="card-title">추천 업무</span>
                  <span class="text-xs text-gray-400"
                    >{{ todoList.length }}건</span
                  >
                </div>
                <div class="p-4 flex flex-col gap-3">
                  <div
                    v-for="(file, idx) in todoList"
                    :key="idx"
                    class="p-3 border rounded-lg bg-gray-50 hover:bg-white transition-all"
                  >
                    <div class="flex justify-between items-start mb-2">
                      <span
                        class="text-sm font-bold text-gray-700 truncate w-[70%]"
                        >{{ Object.keys(file)[0] }}</span
                      >
                    </div>
                    <button
                      type="button"
                      class="btn-create w-full text-xs py-1"
                      @click="openCreateModal(file)"
                    >
                      + 업무 생성
                    </button>
                  </div>
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
  <!-- <meetingCreateTaskModal
    v-model="createModalOpen"
    :taskInfo="taskInfo"
    @close-create-modal="closeCreateModal"
    @register-task="registerTask"
  /> -->
  <meetingConnectTaskModal
    v-model="openConnectModal"
    :projectInfo="projectInfo"
    :connectList="connectTaskList"
    @close-modal="closeModal"
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
import { usetaskKJHStore } from "../stores/taksKJH";
import TaskDatePicker from "../components/TaskDatePicker.vue";

import { useProjectKJHStore } from "../stores/projectKJH";
import { useMeetingStore } from "../stores/meeting";
import Swal from "sweetalert2";
import meetingCreateTaskModal from "./meetingCreateTaskModal.vue";
import meetingNotifirationModal from "./meetingNotificationModal.vue";
import meetingConnectTaskModal from "./meetingConnectTaskModal.vue";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const meetingStore = useMeetingStore();
const taskStore = usetaskKJHStore();
const store = useTaskStore();

const projectStore = useProjectKJHStore();
const sidebarOpen = ref(false);

const id = route.params.projectId;
const subId = route.params.subProjectId;
const meetingId = route.params.meetingId;
const userInfo = ref(); // 글 작성자 정보
const meetingType = ref([]);
let taskPjList = ref([]);
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
const todoList = ref([]); // 업무 추천 목록
const isVoice = ref(false); // 음성요약 파일 잇는지 확인
const createModalOpen = ref(false); // 추천업무 생성 모달창 여는 거
const taskInfo = ref([]);
const connectTaskList = ref([]);
const openConnectModal = ref(false);
const projectInfo = ref({
  projectId: subId != "" ? subId : id,
  meetingLogId:
    connectTaskList.value.length > 0
      ? connectTaskList.value[0].meetingLogId
      : null,
});

watch(
  () => recommandTask.value,
  (newVal) => {
    connectTaskList.value = newVal;
  },
);

// 업무 연결 버튼
const openConnectTaskModal = () => {
  openConnectModal.value = true;
};

// 모달창 연결버튼
const closeModal = (val) => {
  console.log(val);
  openConnectModal.value = false;
  connectTaskList.value = meetingStore.detailConnectList;
};

// 공지사항 생성 버튼
const submitForm = async (formEl) => {
  console.log(formEl.validate);
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      if (!isModified.value) {
        // 회의록 등록
        let obj = {
          projectId: subId != "" ? subId : id,
          title: form.title,
          content: form.content,
          meetingTypeCode: form.meetingType,
          meetingDate: form.date,
          place: form.meetingRoom,
          createdBy: userInfo.value.userId,
          aiSummary: isVoice.value ? null : form.aiSummary,
          sttText: isVoice.value ? form.aiSummary : null,
          meetingLogId:
            connectTaskList.value.length > 0
              ? connectTaskList.value[0].meetingLogId
              : null,
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
              projectId: id,
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
        // 회의록 수정
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
          meetingDate: form.date,
          place: form.meetingRoom,
          attachmentGroupId: form.attachmentGroupId,
        };

        console.log(obj);

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
          projectId: subId != "" ? subId : id,
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

  form.content = form.aiSummary.replace(
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
    let meetingInfo = meetingStore.meetingDetail.meetingList.meetingDetail;

    // 폼에 대입
    form.meetingType = meetingInfo.meetingTypeCode;
    form.author = meetingInfo.userName;
    form.date = meetingInfo.meetingDate;
    form.title = meetingInfo.title;
    form.content = meetingInfo.content;
    form.meetingRoom = meetingInfo.place;
    form.attachmentGroupId = meetingInfo.attachmentGroupId;

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
    ...value,
    meetingLogId:
      connectTaskList.value.length > 0
        ? connectTaskList.value[0].meetingLogId
        : null,
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
/* 1. Element Plus 입력창/텍스트영역 기본 스타일 (요청하신 deep 유지) */
:deep(.el-input__inner) {
  height: 42px;
}
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  box-shadow: none !important;
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
  box-shadow: none !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
  padding: 12px !important;
}

/* 포커스 상태 스타일 */
:deep(.el-input__wrapper.is-focus),
:deep(.el-textarea__inner:focus),
:deep(.el-select__wrapper.is-focused) {
  border-color: #94a3b8 !important;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15) !important;
  background: #fff !important;
}

/* 비활성화 상태 */
:deep(.el-input.is-disabled .el-input__wrapper) {
  background: #f1f5f9 !important;
  border-color: #e2e8f0 !important;
}

/* 2. 카드 및 사이드바 공통 스타일 */
.card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: transform 0.2s ease;
}

.card-header {
  border-bottom: 1px solid #f1f5f9;
}

.card-title {
  font-weight: 700;
  font-size: 14px;
  color: #1e293b;
}

/* 3. 버튼 스타일 (시안의 색감 반영) */
.btn-navy {
  height: 40px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 8px;
  background: #1e3a5f;
  color: #fff;
  transition: all 0.2s;
}
.btn-navy:hover {
  background: #162d4a;
  transform: translateY(-1px);
}

.btn-red {
  height: 40px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 8px;
  background: #ef4444;
  color: #fff;
}

.btn-green {
  height: 40px;
  padding: 0 24px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 8px;
  background: #2563eb; /* 시안의 등록 버튼 색상 계열 */
  color: #fff;
  box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2);
}

/* 4. 파일 추가 / 참석자 추가 버튼 (테두리 강조형) */
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

/* 5. AI 관련 특수 버튼 */
.btn-voice {
  height: 40px;
  background: #334155;
  color: #fff;
  border-radius: 8px;
  font-weight: 600;
  font-size: 13px;
}

.btn-sub {
  height: 40px;
  background: #4f46e5; /* AI 요약 포인트 컬러 */
  color: #fff;
  border-radius: 8px;
  font-weight: 600;
  font-size: 13px;
}

/* 6. 태그 스타일 (참석자) */
:deep(.custom-alarm-tag) {
  height: 30px;
  padding: 0 10px;
  border-radius: 6px;
  background-color: #ffffff !important;
  border: 1.5px solid #475569 !important;
  color: #1e293b !important;
  font-weight: 600;
}

/* 7. 애니메이션 효과 */
.hover-card:hover {
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

/* 드래그 앤 드롭 영역 커스텀 */
:deep(.el-upload-dragger) {
  padding: 20px !important;
  background-color: #f8fafc;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
}
:deep(.el-upload-dragger:hover) {
  border-color: #4f46e5;
}
:deep(.el-form-item__content) {
  display: flex;
  flex-direction: column; /* 자식 요소들을 세로로 정렬 (너비 100% 확보) */
  align-items: stretch; /* 자식들이 부모 너비에 꽉 차게 늘림 */
  width: 100% !important;
  flex: 1; /* 그리드 내에서 할당받은 공간을 다 씀 */
}

/* 추가로 TaskDatePicker 컴포넌트 자체가 100%인지 확인 */
:deep(.el-form-item__content > *) {
  width: 100% !important;
}
</style>
