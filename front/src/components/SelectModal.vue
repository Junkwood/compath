<template>
  <VueFinalModal
    v-model="modalOpenModel"
    class="flex items-center justify-center"
    content-class="bg-white rounded-lg shadow-xl w-96 p-5 border border-gray-200"
  >
    <div class="flex justify-between items-center mb-4 border-b pb-2">
      <span class="font-bold text-gray-700">{{ title }}</span>
      <button
        @click="modalOpenModel = false"
        class="text-gray-400 hover:text-black"
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
          class="p-3 hover:bg-[#F5F5F5] cursor-pointer border-b last:border-none transition-colors rounded-md"
        >
          <span class="text-sm text-gray-800">{{
            item.name || item.codeName
          }}</span>
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
      class="flex justify-center items-center gap-2 mt-5"
    >
      <button
        @click="page--"
        :disabled="page === 1"
        class="px-2 py-1 rounded hover:bg-gray-100 disabled:opacity-20 transition-opacity"
      >
        ＜
      </button>

      <button
        v-for="n in totalPages"
        :key="n"
        @click="page = n"
        :class="[
          'px-3 py-1 rounded text-sm font-medium transition-all',
          page === n
            ? 'bg-gray-800 text-white'
            : 'hover:bg-gray-100 text-gray-500',
        ]"
      >
        {{ n }}
      </button>

      <button
        @click="page++"
        :disabled="page === totalPages"
        class="px-2 py-1 rounded hover:bg-gray-100 disabled:opacity-20 transition-opacity"
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

// 1. v-model 양방향
const modalOpenModel = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

// 2. 페이지네이션 로직
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
  return props.items.slice(start, start + perPage);
});

// 3. 부모에게 값 전달
const selectItem = (item) => {
  const displayName = item.name || item.codeName;
  emit("select", { name: displayName, value: item.id || item.codeValue });
  modalOpenModel.value = false;
};
</script>
