<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <!-- Content area -->
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
          <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-8">
            업무 생성
          </h1>

          <div class="bg-white dark:bg-gray-800 rounded-xl shadow p-6">
            <!--프로젝트명 / 하위 프로젝트명 -->
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >프로젝트 명 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <input
                    v-model="form.projectName"
                    disabled
                    class="input flex-1"
                  />
                  <button @click="openProjectModal">선택</button>
                </div>
              </div>
              <ProjectSelectModal
                v-model="projectModal"
                :items="projectList"
                @select="
                  (val) => {
                    form.projectName = val;
                  }
                "
              />
              <div>
                <label class="block text-sm font-medium mb-1"
                  >하위 프로젝트 명</label
                >
                <div class="flex gap-2">
                  <input
                    v-model="form.subProjectName"
                    disabled
                    class="input flex-1"
                  />
                  <button class="btn-outline">선택</button>
                </div>
              </div>
            </div>

            <!--업무유형 / 담당자 -->
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >업무 유형 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <select v-model="form.taskType" class="input flex-1">
                    <option value="">업무 유형을 선택하세요</option>
                    <option
                      v-for="item in taskTypeList"
                      :key="item.taskTypeId"
                      :value="item.taskTypeId"
                    >
                      {{ item.typeName }}
                    </option>
                  </select>
                  <button class="btn-outline">확인</button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >담당자 지정 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <input
                    v-model="form.assignee"
                    disabled
                    class="input flex-1"
                  />
                  <button class="btn-outline">확인</button>
                </div>
              </div>
            </div>

            <!--  업무명 -->
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1"
                >업무 명 <span class="text-red-500">*</span></label
              >
              <input
                v-model="form.taskName"
                placeholder="업무 제목을 적으세요"
                class="input w-full"
              />
            </div>

            <!-- 프로젝트 설명 -->
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1"
                >프로젝트 설명</label
              >
              <textarea
                v-model="form.description"
                rows="5"
                class="input w-full"
              />
              <button class="btn-outline mt-2">파일 선택</button>
            </div>

            <!-- 업무상태 / 우선순위 / 마일스톤 -->
            <div class="grid grid-cols-3 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >업무 상태 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <input value="시작 전" disabled class="input flex-1" />
                  <button class="btn-outline">확인</button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >우선순위 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <select
                    v-model="form.priority"
                    @change="onPriorityChange"
                    class="input flex-1"
                  >
                    <option value="">우선순위를 선택하세요</option>
                    <option
                      v-for="item in priorityList"
                      :key="item.codeValue"
                      :value="item.codeValue"
                    >
                      {{ item.codeName }}
                    </option>
                  </select>
                  <button class="btn-outline">선택</button>
                </div>
                <p class="text-xs text-gray-400 mt-1">
                  우선순위 선택 시 마감기한이 자동 설정됩니다.
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">마일스톤</label>
                <div class="flex gap-2">
                  <input v-model="form.milestone" class="input flex-1" />
                  <button class="btn-outline">선택</button>
                </div>
              </div>
            </div>

            <!--  예정시작일 / 예정종료일 / 추정시간 -->
            <div class="grid grid-cols-3 gap-6 mb-8">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >예정 시작 일</label
                >
                <input
                  type="date"
                  v-model="form.startDate"
                  @change="calcEstTime"
                  class="input w-full"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >예정 종료일</label
                >
                <input
                  type="date"
                  v-model="form.endDate"
                  @change="calcEstTime"
                  class="input w-full"
                />
                <p class="text-xs text-gray-400 mt-1">
                  시작일과 종료일이 입력되면 자동 계산 됩니다.
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">추정 시간</label>
                <div class="flex gap-2">
                  <input v-model="form.estTime" class="input flex-1" />
                  <button class="btn-outline">확인</button>
                </div>
              </div>
            </div>

            <!-- 하단 버튼 -->
            <div class="flex justify-between">
              <button @click="goBack" class="btn-outline">← 목록으로</button>
              <div class="flex gap-2">
                <button @click="resetForm" class="btn-outline">초기화</button>
                <button @click="submitForm" class="btn-primary">
                  프로젝트 등록
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
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectSelectModal from "../components/SelectModal.vue";

const router = useRouter();
const sidebarOpen = ref(false);

// 공통 코드 리스트를 담을 변수들
const taskTypeList = ref([]); // 업무 유형
const priorityList = ref([]); // 우선순위
const statusList = ref([]); // 업무 상태

// 화면이 열릴 때 API 호출
onMounted(async () => {
  try {
    // 1. 업무 유형 리스트 가져오기
    const typeRes = await axios.get("http://localhost:8080/api/taskType");
    taskTypeList.value = typeRes.data;

    // 2. 우선순위 리스트 가져오기
    const priorityRes = await axios.get(
      "http://localhost:8080/api/code?groupValue=0H&groupValue=0G",
      // {
      //   groupValue: ["0H", "0S"],
      // },
    );
    priorityList.value = priorityRes.data.c0H; //우선순위
    statusList.value = priorityRes.data.c0G; //업무 상태
  } catch (e) {
    console.error("공통 코드를 불러오는데 실패했습니다.", e);
  }
});

const initialForm = {
  projectName: "",
  subProjectName: "",
  taskType: "",
  taskName: "",
  assignee: "",
  description: "",
  status: "",
  priority: "",
  milestone: "",
  startDate: "",
  endDate: "",
  estTime: "",
};

const form = ref({ ...initialForm });
const projectModal = ref(false);
const projectList = ref([]);

const openProjectModal = async () => {
  try {
    const res = await axios.get("/api/projectList");
    projectList.value = res.data;
    projectModal.value = true;
  } catch (e) {
    alert("프로젝트 목록을 불러오는데 실패했습니다.");
  }
};

// 우선순위 변경 시 로직
const onPriorityChange = () => {
  const val = form.value.priority;
  const today = new Date();

  // DB에서 넘어오는 코드값
  if (val.includes("상") || val === "H1") today.setDate(today.getDate() + 3);
  else if (val.includes("중") || val === "H2")
    today.setDate(today.getDate() + 7);
  else today.setDate(today.getDate() + 14);

  form.value.endDate = today.toISOString().split("T")[0];
  calcEstTime();
};

const submitForm = async () => {
  try {
    await axios.post("/api/tasks", form.value);
    alert("등록 완료!");
    router.push("/tasks");
  } catch (e) {
    alert("등록 실패");
  }
};

const resetForm = () => {
  form.value = { ...initialForm };
};

const goBack = () => router.back();
</script>
