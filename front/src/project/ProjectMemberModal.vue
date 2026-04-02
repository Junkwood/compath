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
        style="max-width: 100%"
        placeholder="이름을 입력해주세요"
        class="input-with-select"
      >
        <template #append>
          <el-button :icon="Search" @click="searchUsers" />
        </template>
      </el-input>
    </div>
    <!-- 구성원목록 -->

    <div v-if="search">
      <el-checkbox
        v-for="mem in projectStore.groupMem"
        :key="mem"
        :label="mem"
        :value="mem"
        v-model="searchedChecked"
      >
        {{ mem.userName }}
      </el-checkbox>
    </div>

    <div v-else class="flex flex-row gap-5 mb-3">
      <div v-for="group in groupData">
        <el-checkbox
          v-model="group.Allcheck"
          :indeterminate="isIndeterminate"
          @change="handleCheckAllChange(group)"
          :disabled="group.disabled == true"
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
              :disabled="mem.disabled == true"
            >
              {{ mem.userName }}
            </el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
    </div>
    <!-- 역할부여 -->

    <div class="h-64"></div>
    <div>
      <el-checkbox-group
        v-model="checkList"
        :options="options"
        :props="props"
        :max="1"
        :value="options"
      />
    </div>

    <!-- 푸터 버튼 -->
    <template #footer>
      <div class="modal-footer">
        <div class="footer-right">
          <el-button class="btn-submit" @click="insertUsers"> 추가 </el-button>
          <el-button class="btn-cancel" @click="cancelInsert">취소</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, watch, defineEmits } from "vue";
import { Search } from "@element-plus/icons-vue";
import { User } from "@element-plus/icons-vue";
import { useProjectKJHStore } from "../stores/projectKJH";

const projectStore = useProjectKJHStore();

const emit = defineEmits(["memberInsert", "memberCancel"]);
const prop = defineProps({
  groupList: { type: Array },
  memberList: { type: Array },
  roleList: { type: Array },
});

const input3 = ref("");

const checkList = ref([]);
const props = { label: "name", value: "id", disabled: "unable" };
const options = [];
const groupData = ref([]);

watch(
  () => [prop.groupList, prop.memberList],
  async (mNewVal) => {
    console.log("그룹", mNewVal[0]);
    console.log("멤버", mNewVal[1]);

    const data = [...mNewVal[0]];

    for (let i = 0; i < data.length; i++) {
      await projectStore.getAllGroupMem(data[i].groupId);
      groupData.value[i] = {
        groupId: data[i].groupId,
        groupName: data[i].groupName,
        Allcheck: false,
        members: projectStore.groupMem,
        checkedusers: [],
        disabled: false,
      };

      let count = 0;
      groupData.value[i].members.forEach((li) => {
        for (let j = 0; j < mNewVal[1].length; j++) {
          if (li.userId == mNewVal[1][j].userId) {
            li.disabled = true;
            count++;
            console.log(count, groupData.value[i].members.length);

            if (groupData.value[i].members.length == count) {
              groupData.value[i].disabled = true;
            }
            return;
          } else {
            li.disabled = false;
          }
        }
      });
    }
  },
);

watch(
  () => prop.roleList,
  async (mNewVal) => {
    console.log("그룹", mNewVal);

    const roleList = [...mNewVal];

    roleList.forEach((role) => {
      let list = { name: role.roleName, id: role.roleId };
      options.push(list);
    });
  },
);

// 그룹전체 선택시
const isIndeterminate = ref(false);
const handleCheckAllChange = (val) => {
  console.log(val);
  let count = 0;
  groupData.value.forEach((li) => {
    if (val.groupId == li.groupId) {
      if (li.members.length == 0) {
        li.Allcheck = false;
        count++;
        return;
      }

      li.members.forEach((gr) => {
        if (li.Allcheck) {
          if (!gr.disabled) {
            li.checkedusers.push(gr);
          }
        } else {
          li.checkedusers = [];
        }
      });
    }
  });
};

// 멤버 체크박스 선택시
const handleCheckedCitiesChange = (value, group) => {
  console.log(value, group);
  if (value.length > 0) {
    let id = value[0].groupId;
    groupData.value.forEach((li) => {
      if (li.groupId == id) {
        let len = li.checkedusers.length;
        let groupLen = li.members.length;

        // 멤버 체크박스가 disabled일 경우 groupLen 감소
        li.members.forEach((mem) => {
          if (mem.disabled) {
            groupLen--;
          }
        });
        if (len == groupLen) {
          li.Allcheck = true;
        } else {
          li.Allcheck = false;
        }
      }
    });
  } else {
    let id = group.groupId;
    groupData.value.forEach((li) => {
      if (li.groupId == id) {
        li.Allcheck = false;
      }
    });
  }
};

// 검색창
const search = ref(false);
const searchedChecked = ref([]); // 검색시 나오는 회원 체크

const searchUsers = async () => {
  if (input3.value == "" && search.value == false) {
    alert("이름을 입력해주세요.");
    return;
  }
  if (input3.value == "" && search.value == true) {
    search.value = false;
    return;
  }
  search.value = true;
  await projectStore.getAllGroupMem("", input3.value);
};

// 추가버튼
const insertUsers = () => {
  let count = 0;
  let InsertList = ref([]);
  groupData.value.forEach((group) => {
    if (group.checkedusers.length > 0) {
      count++;
    }
    group.checkedusers.forEach((mem) => {
      InsertList.value.push({ id: mem.userId, role: checkList.value[0] });
    });
  });

  if (count == 0 && searchedChecked.length == 0) {
    alert("추가할 회원을 선택해주세요");
    return;
  }

  if (checkList.value.length == 0) {
    alert("역할을 선택해주세요");
    return;
  }

  console.log(InsertList.value);
  emit("memberInsert", InsertList.value);

  reset();
};

// 취소 버튼
const cancelInsert = () => {
  reset();

  emit("memberCancel");
};

// 초기화
const reset = () => {
  groupData.value.forEach((group) => {
    if (group.checkedusers.length > 0) {
      group.checkedusers.length = 0;
    }
    group.Allcheck = false;
  });
  searchedChecked.value.length = 0; // 검색해서 나온 유저
  checkList.value.length = 0; // 역할 분배 초기화
  search.value = false;
  input3.value = "";
  options.value = [];
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

.infinite-list {
  height: 300px;
  padding: 0;
  margin: 0;
  list-style: none;
}
.infinite-list .infinite-list-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  background: var(--el-color-primary-light-9);
  margin: 10px;
  color: var(--el-color-primary);
}
.infinite-list .infinite-list-item + .list-item {
  margin-top: 10px;
}
</style>
