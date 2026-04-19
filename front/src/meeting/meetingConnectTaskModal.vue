<template>
  <!-- 모달 -->
  <el-dialog
    v-model="dialogVisible"
    title="일감 연결"
    width="660px"
    :close-on-click-modal="false"
    class="task-link-dialog"
    @open="onOpen"
  >
    <!-- 서브 타이틀 -->
    <template #header="{ titleId }">
      <div class="flex flex-col gap-0.5">
        <span :id="titleId" class="text-[15px] font-medium text-gray-800"
          >일감 연결</span
        >
      </div>
    </template>

    <!-- 검색 + 정렬 -->
    <div class="flex gap-2 mb-3">
      <el-input
        v-model="searchQuery"
        placeholder="일감 제목, 담당자로 검색..."
        :prefix-icon="Search"
        clearable
        class="flex-1"
        @input="handleSearch"
      />
      <button type="button" @click="searchTitle">검색</button>
    </div>

    <!-- 결과 수 + 전체 선택 -->
    <div class="flex items-center justify-between mb-2">
      <span class="text-xs text-gray-400">총 {{ listLength }}건</span>
      <el-checkbox
        v-model="isPageAllSelected"
        :indeterminate="isPageIndeterminate"
        label="현재 페이지 전체 선택"
        class="!text-xs !text-gray-500"
        @change="toggleSelectAll"
      />
    </div>

    <!-- 일감 목록 -->
    <div
      class="border border-gray-100 rounded-lg overflow-hidden mb-3"
      v-loading="loading"
      element-loading-text="목록을 불러오는 중..."
    >
      <div
        v-if="allTasks.length === 0"
        class="py-10 text-center text-sm text-gray-400"
      >
        검색 결과가 없습니다.
      </div>
      <div></div>

      <div
        v-for="(task, idx) in allTasks"
        :key="task.id"
        class="flex items-center gap-3 px-4 py-3 cursor-pointer transition-colors duration-100"
        :class="[
          selectedIds.has(task.taskId)
            ? 'bg-blue-50 border-l-2 border-l-blue-500'
            : 'hover:bg-gray-50 border-l-2 border-l-transparent',
          idx !== allTasks.length - 1 ? 'border-b border-b-gray-100' : '',
        ]"
        @click="toggleTask(task.taskId)"
      >
        <el-checkbox
          :model-value="selectedIds.has(task.taskId)"
          @change="toggleTask(task.taskId)"
          @click.stop
        />
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 flex-wrap">
            <span
              class="text-sm font-medium text-gray-800 truncate max-w-[200px]"
            >
              {{ task.title }} #{{ task.taskId }}
            </span>
            <el-tag
              :type="task.taskStatusType"
              size="small"
              effect="light"
              class="!rounded-full"
            >
              {{ task.statusName }}
            </el-tag>
            <span class="text-xs text-gray-400">{{ task.projectName }}</span>
          </div>
          <p class="text-xs text-gray-400 mt-0.5">
            담당자: {{ task.userName }} · 마감: {{ task.dueDate }}
          </p>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div class="flex justify-center mb-4" v-if="totalPages > 1">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="PAGE_SIZE"
        :total="allTasks[0]?.taskCounts ?? 0"
        layout="prev, pager, next"
        small
        @current-change="onPageChange"
      />
    </div>

    <!-- 선택된 일감 칩 -->
    <transition name="fade">
      <div
        v-if="selectedIds.size > 0"
        class="border-t border-gray-100 pt-3 mb-1"
      >
        <p class="text-xs text-gray-400 mb-2">
          선택된 일감 ({{ selectedIds.size }}건)
        </p>
        <div class="flex gap-2 flex-wrap">
          <el-tag
            v-for="id in [...selectedIds].slice(0, 5)"
            :key="id"
            closable
            type="primary"
            effect="light"
            class="!rounded-full"
            @close="removeSelected(id)"
          >
            {{ truncate(getTask(id)?.taskId ?? "", 14) }}
          </el-tag>
          <span
            v-if="selectedIds.size > 5"
            class="text-xs text-gray-400 self-center"
          >
            +{{ selectedIds.size - 5 }}건 더
          </span>
        </div>
      </div>
    </transition>

    <!-- 푸터 -->
    <template #footer>
      <div class="flex items-center justify-between w-full">
        <span class="text-sm text-gray-400">
          {{
            selectedIds.size > 0
              ? `${selectedIds.size}개 선택됨`
              : "선택된 일감 없음"
          }}
        </span>
        <div class="flex gap-2">
          <el-button @click="closeModalTask()">취소</el-button>
          <el-button
            type="primary"
            :disabled="selectedIds.size === 0"
            @click="handleConnect"
          >
            연결하기
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, reactive, defineProps, defineEmits, watch } from "vue";

import { Search } from "@element-plus/icons-vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { changeDate } from "../utils/commonFunc";
import { useMeetingStore } from "../stores/meeting";

const taskStore = usetaskKJHStore();
const meetingStore = useMeetingStore();

const props = defineProps({
  projectInfo: Object,
  connectList: Array,
});

const emit = defineEmits(["closeModal"]);

const allTasks = ref([]);
const loading = ref(false);

/* ─── 상태 ─── */
const PAGE_SIZE = 8;
const dialogVisible = ref(false);
const searchQuery = ref("");
const sortKey = ref("recent");
const currentFilter = ref("all");
const currentPage = ref(1);
const selectedIds = reactive(new Set());
const listLength = ref(0);

const totalPages = computed(() => {
  if (!allTasks.value || allTasks.value.length === 0) return 1;
  return Math.max(1, Math.ceil(allTasks.value[0].taskCounts / PAGE_SIZE));
});

const closeModalTask = () => {
  emit("closeModal");
  onOpen();
};

/* ─── 전체 선택 상태 ─── */
const isPageAllSelected = computed(
  () =>
    allTasks.value.length > 0 &&
    allTasks.value.every(t => selectedIds.has(t.taskId)),
);
const isPageIndeterminate = computed(
  () =>
    allTasks.value.some(t => selectedIds.has(t.taskId)) &&
    !isPageAllSelected.value,
);

function handleSearch() {
  currentPage.value = 1;
}

const searchTitle = async () => {
  await handleCurrentChange(1);
};

function onPageChange(p) {
  handleCurrentChange(p);
}

function toggleTask(id) {
  if (selectedIds.has(id)) selectedIds.delete(id);
  else selectedIds.add(id);
}

function toggleSelectAll(val) {
  allTasks.value.forEach(t => {
    if (val) selectedIds.add(t.taskId);
    else selectedIds.delete(t.taskId);
  });
}

function removeSelected(id) {
  selectedIds.delete(id);
}

function getTask(id) {
  return allTasks.value.find(t => t.taskId === id);
}

function truncate(str, len) {
  return str.length > len ? str.slice(0, len) + "…" : str;
}

function onOpen() {
  searchQuery.value = "";
  sortKey.value = "";
  currentFilter.value = "all";
  currentPage.value = 1;
  selectedIds.clear();
}

const handleConnect = async () => {
  const ids = [...selectedIds];
  console.log(ids);
  let arr = [];
  ids.forEach(id => {
    arr.push({
      projectId: props.projectInfo.projectId,
      meetingLogId: props.projectInfo.meetingLogId,
      taskId: id,
    });
  });

  console.log(arr);
  await meetingStore.registerDetailConnect(arr);
  emit("closeModal", ids);
  await onOpen();
};

/* ─── 상태 뱃지 타입 ─── */
function statusType(status) {
  return { 1: "", 2: "warning", 3: "success", 4: "info" }[status] ?? "info";
}

// 페이지네이션
const handleCurrentChange = async val => {
  currentPage.value = val;
  loading.value = true;

  let start = (val - 1) * PAGE_SIZE + 1;
  let end = val * PAGE_SIZE;

  // 페이지 변환 목록 조회
  let obj = {
    projectId: props.projectInfo.projectId,
    startNum: start,
    endNum: end,
    search: searchQuery.value == null ? "" : searchQuery.value,
  };

  try {
    await taskStore.getAllTask(obj);

    allTasks.value = [...taskStore.taskAllList].filter(
      task => !props.connectList?.some(c => c.taskId === task.taskId),
    );
  } catch (err) {
  } finally {
  }

  if (allTasks.value.length > 0) {
    await changeDateType(allTasks.value);
    listLength.value = allTasks.value[0].taskCounts;
  }
  loading.value = false;
};

// 날짜 null 일 경우 형식 변경
const changeDateType = val => {
  for (let i = 0; i < val.length; i++) {
    val.taskStatusType = statusType(val.taskStatusId);
    // 담당자 미지정
    if (val[i].userName == null) {
      val[i].userName = "미지정";
    }
    // 날짜 형식 변경
    if (val[i].startDate != null) {
      val[i].startDate = changeDate(val[i].startDate);
    } else {
      val[i].startDate = "-";
    }
    if (val[i].dueDate != null) {
      val[i].dueDate = changeDate(val[i].dueDate);
    } else {
      val[i].dueDate = "-";
    }
  }
};

watch(
  () => props.projectInfo?.projectId,
  newId => {
    if (newId) {
      console.log("프로젝트 ID 확인됨:", newId);
      handleCurrentChange(1);
      dialogVisible.value = true;
    }
  },
  { immediate: true },
);

watch(
  () => props.connectList,
  newId => {
    if (newId) {
      handleCurrentChange(1);
      allTasks.value = taskStore.taskAllList.filter(
        task => !newId.some(id => id.taskId == task.taskId),
      );

      console.log(taskStore.taskAllList.length);
      listLength.value =
        taskStore.taskAllList.length != 0
          ? taskStore.taskAllList[0].taskCounts - newId.length
          : 0;
    }
  },
  { immediate: true },
);
</script>

<style scoped>
/* El Dialog 헤더 패딩 미세 조정 */
:deep(.el-dialog__header) {
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 0;
}
:deep(.el-dialog__body) {
  padding: 20px 24px 8px;
}
:deep(.el-dialog__footer) {
  padding: 12px 24px 20px;
  border-top: 1px solid #f0f0f0;
}

/* 선택 칩 영역 트랜지션 */
.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.2s,
    transform 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>
