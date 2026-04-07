<template>
  <VueFinalModal
    v-model="modalOpenModel"
    class="flex items-center justify-center"
    content-class="bg-white rounded-xl shadow-xl w-96 p-5 border border-gray-200"
    teleport-to="body"
    :z-index="9999"
  >
    <div class="flex justify-between items-center mb-4 border-b pb-3">
      <span class="font-semibold text-gray-700 text-sm">{{ title }}</span>
      <button
        @click="modalOpenModel = false"
        class="text-gray-400 hover:text-gray-700 text-lg leading-none transition-colors"
      >
        ✕
      </button>
    </div>

    <div class="min-h-[200px]">
      <ul v-if="props.items.length > 0">
        <li
          v-for="item in pagedList"
          :key="item.id || item.codeValue"
          @click="selectItem(item)"
          class="px-3 py-2.5 hover:bg-slate-50 cursor-pointer border-b last:border-none transition-colors rounded-lg"
        >
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-700">{{
              item.name || item.codeName
            }}</span>
            <span
              v-if="item.userType"
              :class="[
                'text-xs font-semibold px-2 py-0.5 rounded-full',
                item.userType === 'PM'
                  ? 'bg-blue-100 text-blue-600'
                  : item.userType === 'PL'
                    ? 'bg-purple-100 text-purple-600'
                    : item.userType === '개발자'
                      ? 'bg-green-100 text-green-600'
                      : 'bg-gray-100 text-gray-500',
              ]"
            >
              {{ item.userType }}
            </span>
          </div>
        </li>
      </ul>

      <div
        v-else
        class="flex flex-col items-center justify-center py-10 text-gray-400"
      >
        <span class="text-sm">데이터가 존재하지 않습니다.</span>
      </div>
    </div>

    <div
      v-if="totalPages > 1"
      class="flex justify-center items-center gap-1 mt-4 pt-3 border-t"
    >
      <button
        @click="page--"
        :disabled="page === 1"
        class="w-8 h-8 rounded-lg hover:bg-gray-100 disabled:opacity-20 transition-all text-gray-500 text-sm"
      >
        ＜
      </button>

      <button
        v-for="n in totalPages"
        :key="n"
        @click="page = n"
        :class="[
          'w-8 h-8 rounded-lg text-sm font-medium transition-all',
          page === n
            ? 'bg-[#1e3a5f] text-white'
            : 'hover:bg-gray-100 text-gray-500',
        ]"
      >
        {{ n }}
      </button>

      <button
        @click="page++"
        :disabled="page === totalPages"
        class="w-8 h-8 rounded-lg hover:bg-gray-100 disabled:opacity-20 transition-all text-gray-500 text-sm"
      >
        ＞
      </button>
    </div>
  </VueFinalModal>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { VueFinalModal } from "vue-final-modal";

const props = defineProps({
  modelValue: Boolean,
  items: { type: Array, default: () => [] },
  title: { type: String, default: "선택" },
});

const emit = defineEmits(["update:modelValue", "select"]);

const modalOpenModel = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const page = ref(1);
const perPage = 5;

watch(
  () => props.modelValue,
  (isOpen) => {
    if (isOpen) page.value = 1;
  },
);

const totalPages = computed(() => Math.ceil(props.items.length / perPage) || 1);

const pagedList = computed(() => {
  const start = (page.value - 1) * perPage;
  const result = props.items.slice(start, start + perPage);
  console.log("pagedList items:", result);

  return props.items.slice(start, start + perPage);
});

const selectItem = (item) => {
  const displayName = item.name || item.codeName;
  emit("select", {
    name: displayName,
    value: item.id || item.codeValue,
    userType: item.userType,
  });
  modalOpenModel.value = false;
};

const getRoleClass = (roleType) => {
  const role = (roleType ?? "").toUpperCase();
  if (role === "PM") return "bg-blue-100 text-blue-600";
  if (role === "PL") return "bg-purple-100 text-purple-600";
  return "bg-green-100 text-green-600"; // 일반 멤버 등
};
</script>
