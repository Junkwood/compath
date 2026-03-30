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
            <!-- 프로젝트명 / 하위 프로젝트명 -->
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1">
                  프로젝트 명 <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <input
                    v-model="form.projectName"
                    disabled
                    class="input flex-1"
                  />
                </div>
              </div>
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
                </div>
              </div>
            </div>

            <!-- 업무유형 / 담당자 -->
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1">
                  업무 유형 <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select v-model="form.taskTypeId" class="input flex-1">
                    <option value="">업무 유형을 선택하세요</option>
                    <option
                      v-for="item in taskTypeList"
                      :key="item.taskTypeId"
                      :value="item.taskTypeId"
                    >
                      {{ item.typeName }}
                    </option>
                  </select>
                  <button class="btn-confirm">확인</button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >담당자 지정</label
                >
                <div class="flex gap-2">
                  <input
                    v-model="form.assigneeName"
                    disabled
                    class="input flex-1"
                  />
                  <button
                    type="button"
                    @click="openUserModal"
                    class="btn-select"
                  >
                    선택
                  </button>
                </div>
              </div>

              <ProjectSelectModal
                v-model="userModal"
                title="담당자 선택"
                :items="userList"
                @select="
                  (val) => {
                    form.assigneeName = val.name;
                    form.assigneeUserId = val.value;
                  }
                "
              />
            </div>

            <!-- 업무명 -->
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1">
                업무 명 <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.title"
                placeholder="업무 제목을 적으세요"
                class="input w-full"
              />
            </div>

            <!-- 프로젝트 설명 -->
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1"
                >프로젝트 설명</label
              >
              <textarea v-model="form.content" rows="5" class="input w-full" />
              <button class="btn-select mt-2">파일 선택</button>
            </div>

            <!-- 업무상태 / 우선순위 / 마일스톤 -->
            <div class="grid grid-cols-3 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1">
                  업무 상태 <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select v-model="form.taskStatusId" class="input flex-1">
                    <option value="">업무 상태를 선택하세요</option>
                    <option
                      v-for="item in statusList"
                      :key="item.codeValue"
                      :value="item.codeValue"
                    >
                      {{ item.codeName }}
                    </option>
                  </select>
                  <button class="btn-confirm">확인</button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">
                  우선순위 <span class="text-red-500">*</span>
                </label>
                <div class="flex gap-2">
                  <select
                    v-model="form.priorityCode"
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
                  <button class="btn-confirm">확인</button>
                </div>
                <p class="text-xs text-gray-400 mt-1">
                  우선순위 선택 시 마감기한이 자동 설정됩니다.
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >마일스톤 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <input
                    v-model="form.milestone"
                    disabled
                    class="input flex-1 bg-gray-100"
                    placeholder="자동 선택됨"
                  />
                </div>
              </div>

              <ProjectSelectModal
                v-model="milestoneModal"
                title="마일스톤 선택"
                :items="milestoneList"
                @select="
                  (val) => {
                    form.milestone = val.name;
                    form.milestoneId = val.value;
                  }
                "
              />
            </div>

            <!-- 예정시작일 / 예정종료일 / 추정시간 -->
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
                  <button class="btn-confirm">확인</button>
                </div>
              </div>
            </div>

            <!-- 하단 버튼 -->
            <div class="flex justify-between">
              <button @click="goBack" class="btn-navy">← 목록으로</button>
              <div class="flex gap-2">
                <button @click="resetForm" class="btn-red">초기화</button>
                <button @click="submitForm" class="btn-green">
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
import { useRoute } from "vue-router";
import axios from "axios";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectSelectModal from "../components/SelectModal.vue";

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);

const taskTypeList = ref([]);
const priorityList = ref([]);
const statusList = ref([]);

const milestoneList = ref([]);

onMounted(async () => {
  const pId = route.params.projectId || route.query.projectId;
  try {
    if (pId) {
      const res = await axios.get(`/api/projectDetail/${pId}`);
      const d = res.data;

      // 프로젝트 정보
      form.value.projectName = d.displayProjectName;
      form.value.subProjectName = d.displaySubProjectName;
      form.value.projectId = d.parentProjectId || d.projectId;
      form.value.subProjectId = d.parentProjectId ? d.projectId : null;

      await fetchMilestones(form.value.subProjectId || form.value.projectId);
    }

    // 공통 코드
    const [typeRes, codeRes] = await Promise.all([
      axios.get("/api/taskType"),
      axios.get("/api/code", { params: { groupValue: ["0H", "0G"] } }),
    ]);

    taskTypeList.value = typeRes.data;
    priorityList.value = codeRes.data.c0H;
    statusList.value = codeRes.data.c0G;

    statusList.value = codeRes.data.c0G.filter(
      (status) => status.codeName === "시작 전" || status.codeName === "진행중",
    );

    // 초기값 세팅
    if (statusList.value?.length > 0)
      form.value.taskStatusId = statusList.value[0].codeValue;
    if (taskTypeList.value?.length > 0)
      form.value.taskTypeId = taskTypeList.value[0].taskTypeId;
  } catch (e) {
    console.error("초기 로드 실패:", e);
  }
});
const initialForm = {
  projectId: "",
  subProjectId: "",
  projectName: "",
  subProjectName: "",
  taskTypeId: "",
  title: "",
  assigneeUserId: "",
  content: "",
  taskStatusId: "",
  priorityCode: "",
  milestoneId: "",
  startDate: "",
  endDate: "",
  estTime: "",
};
const form = ref({ ...initialForm });
const userModal = ref(false);
const userList = ref([]);

// 담당자 목록 불러오기
const openUserModal = async () => {
  try {
    const res = await axios.get("/api/taskUser");
    userList.value = res.data.map((user) => ({
      ...user,
      name: user.userName,
      id: user.userId,
    }));

    userModal.value = true;
  } catch (e) {
    console.error(e);
  }
};

// 마일스톤 목록 불러오기 함수 (독립적으로 분리)
const fetchMilestones = async (pId) => {
  if (!pId) return;
  try {
    const res = await axios.get("/api/taskMileStone", {
      params: { projectId: pId },
    });

    console.log("받아온 마일스톤 데이터:", res.data);

    milestoneList.value = res.data.map((m) => ({
      name: m.milestoneName,
      value: m.milestoneId,
    }));

    // 데이터가 있다면 첫 번째 마일스톤을 기본값으로 세팅
    if (milestoneList.value.length > 0) {
      form.value.milestone = milestoneList.value[0].name;
      form.value.milestoneId = milestoneList.value[0].value;
    } else {
      // 데이터가 없을 경우 초기화
      form.value.milestone = "등록된 마일스톤 없음";
      form.value.milestoneId = "";
    }
  } catch (e) {
    console.error("마일스톤 로드 실패:", e);
  }
};

//우선순위
const onPriorityChange = () => {
  const val = form.value.priorityCode;
  const today = new Date();
  if (val.includes("상") || val === "H1") today.setDate(today.getDate() + 3);
  else if (val.includes("중") || val === "H2")
    today.setDate(today.getDate() + 7);
  else today.setDate(today.getDate() + 14);
  form.value.endDate = today.toISOString().split("T")[0];
  calcEstTime();
};
//예상 시간 계산
const calcEstTime = () => {
  if (form.value.startDate && form.value.endDate) {
    const diff = new Date(form.value.endDate) - new Date(form.value.startDate);
    const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
    form.value.estTime = `${days * 8}시간`;
  }
};
//등록
const submitForm = async () => {
  try {
    const postData = {
      ...form.value,
      projectId: Number(form.value.projectId),
      subProjectId: form.value.subProjectId
        ? Number(form.value.subProjectId)
        : null,
      milestoneId: form.value.milestoneId
        ? Number(form.value.milestoneId)
        : null,
      taskTypeId: Number(form.value.taskTypeId),
      taskStatusId: Number(
        String(form.value.taskStatusId).replace(/[^0-9]/g, ""),
      ),
      assigneeUserId: form.value.assigneeUserId
        ? Number(form.value.assigneeUserId)
        : null,
      estimatedHours: parseInt(form.value.estTime) || 0,
      estStartDate: form.value.startDate,
      estEndDate: form.value.endDate,
      startDate: form.value.startDate,
      dueDate: form.value.endDate,
    };

    await axios.post("/api/tasks", postData);
    alert("등록 완료!");
    router.push("/tasks");
  } catch (e) {
    alert("등록에 실패했습니다. 입력값을 확인해 주세요.");
  }
};
const resetForm = () => {
  const savedProjectInfo = {
    //유지할 정보 보관
    projectId: form.value.projectId,
    subProjectId: form.value.subProjectId,
    projectName: form.value.projectName,
    subProjectName: form.value.subProjectName,
  };
  form.value = { ...initialForm };
  //유지할 정보 복원
  form.value.projectId = savedProjectInfo.projectId;
  form.value.subProjectId = savedProjectInfo.subProjectId;
  form.value.projectName = savedProjectInfo.projectName;
  form.value.subProjectName = savedProjectInfo.subProjectName;
};

const goBack = () => router.back();
</script>

<style scoped>
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

/* 선택 버튼 */
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

/* 확인 버튼 */
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

/* 하단 버튼 */
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
  letter-spacing: 0.01em;
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
  letter-spacing: 0.01em;
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
  letter-spacing: 0.01em;
}
.btn-green:hover {
  background: #60aee2;
  box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3);
  transform: translateY(-1px);
}
</style>
