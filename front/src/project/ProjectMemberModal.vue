<template>
  <el-dialog
    v-model="visible"
    title="구성원 추가"
    width="1000"
    height="100"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <!-- 검색창 -->
    <div class="mt-4 mb-3">
      <el-input
        v-model="input3"
        style="max-width: 600px"
        placeholder="Please input"
        class="input-with-select"
      >
        <template #prepend>
          <el-select v-model="select" placeholder="Select" style="width: 115px">
            <el-option label="Restaurant" value="1" />
            <el-option label="Order No." value="2" />
            <el-option label="Tel" value="3" />
          </el-select>
        </template>
        <template #append>
          <el-button :icon="Search" />
        </template>
      </el-input>
    </div>
    <!-- 구성원목록 -->

    <div class="flex flex-row gap-5 mb-3">
      <div v-for="group in groupData">
        <el-checkbox
          v-model="group.Allcheck"
          :indeterminate="isIndeterminate"
          @change="handleCheckAllChange(group.groupId, e)"
        >
          <el-icon> <User /></el-icon>{{ group.groupName }}
        </el-checkbox>
        <el-checkbox-group
          v-model="group.checkedusers"
          @change="(val) => handleCheckedCitiesChange(val, group)"
        >
          <div class="flex flex-col">
            <el-checkbox
              v-for="mem in group.members"
              :key="mem"
              :label="mem"
              :value="mem"
            >
              {{ mem.userName }}
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
    </div>
    <!-- 역할부여 -->

    <div>
      <el-checkbox-group
        v-model="checkList"
        :options="options"
        :props="props"
      />
    </div>

    <!-- 푸터 버튼 -->
    <template #footer>
      <div class="modal-footer">
        <div class="footer-right">
          <el-button class="btn-submit" @click="$emit('memberInsert')">
            추가
          </el-button>
          <el-button class="btn-cancel" @click="$emit('memberCancel')"
            >취소</el-button
          >
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, watch, reactive } from "vue";
import { Search } from "@element-plus/icons-vue";
import { User } from "@element-plus/icons-vue";
import { useProjectKJHStore } from "../stores/projectKJH";

const projectStore = useProjectKJHStore();

const prop = defineProps({
  groupList: { type: Array },
});

const input3 = ref("");
const select = ref("");

const checkList = ref(["Value selected and disabled", "Value A"]);
const props = { label: "name", value: "id", disabled: "unable" };
const options = [];

const groupData = ref([]);

watch(
  () => prop.groupList,
  (newVal) => {
    console.log(newVal);

    const data = [...newVal];

    data.forEach(async (li) => {
      console.log(li);
      let list = { name: li.groupName, id: li.groupId };
      options.push(list);

      await projectStore.getAllGroupMem(li.groupId);
      groupData.value[li.groupId - 1] = {
        groupId: li.groupId,
        groupName: li.groupName,
        members: projectStore.groupMem,
        checkedusers: [],
      };
    });
  },
);

const isIndeterminate = ref(false);
const handleCheckAllChange = (val) => {
  groupData.value[val - 1].members.forEach((gr) => {
    if (groupData.value[val - 1].Allcheck) {
      groupData.value[val - 1].checkedusers.push(gr);
    } else {
      groupData.value[val - 1].checkedusers = [];
    }
  });
};
const handleCheckedCitiesChange = (value, group) => {
  console.log(group);

  if (value.length > 0) {
    let id = value[0].groupId;
    let len = groupData.value[id - 1].checkedusers.length;
    let groupLen = groupData.value[id - 1].members.length;
    if (len == groupLen) {
      groupData.value[id - 1].Allcheck = true;
    } else {
      groupData.value[id - 1].Allcheck = false;
    }
  } else {
    groupData.value[id - 1].Allcheck = false;
  }
};
</script>

<style scoped>
/* 인라인 필드 묶음 */
.row-fields {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}
.pl-field {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.pl-label {
  font-size: 13px;
  color: #374151;
  white-space: nowrap;
}

/* 날짜 행 */
.date-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}
.date-sep {
  color: #9ca3af;
  flex-shrink: 0;
}

/* 스위치 행 */
.switch-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.switch-desc {
  font-size: 13px;
  color: #374151;
  line-height: 1.6;
}
.switch-sub {
  font-size: 12px;
  color: #9ca3af;
}

/* 푸터 */
.modal-footer {
  display: flex;
  justify-content: end;
  align-items: center;
}
.footer-right {
  display: flex;
}
.btn-list {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #374151;
  font-size: 13px;
}
.btn-cancel {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #374151;
  font-size: 13px;
  margin-left: 3px;
}
.btn-submit {
  background: #1d4ed8;
  border: none;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}
.btn-submit:hover {
  background: #1e40af;
}

/* el-dialog 오버라이드 */
:deep(.el-dialog__title) {
  font-weight: 700;
  font-size: 15px;
  color: #1a1a2e;
}
:deep(.el-form-item__label) {
  font-size: 13px;
  color: #374151;
}

.input-with-select .el-input-group__prepend {
  background-color: var(--el-fill-color-blank);
}
</style>
