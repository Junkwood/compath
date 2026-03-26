<!-- SelectModal.vue -->
<template>
  <VueFinalModal
    v-model="modalOpenModel"
    class="flex items-center justify-center"
    content-class="bg-white rounded-lg shadow-lg w-96 p-4"
  >
    <div class="font-bold mb-3">프로젝트 선택</div>

    <!-- 목록 -->
    <ul>
      <li
        v-for="item in pagedList"
        :key="item.id"
        @click="selectItem(item)"
        class="p-2 hover:bg-gray-100 cursor-pointer border-b"
      >
        {{ item.name }}
      </li>
    </ul>

    <!-- 페이지네이션 -->
    <div class="flex justify-center items-center gap-2 mt-3">
      <button
        @click="page--"
        :disabled="page === 1"
        class="px-2 py-1 rounded hover:bg-gray-100 disabled:opacity-30"
      >
        ＜
      </button>

      <button
        v-for="n in totalPages"
        :key="n"
        @click="page = n"
        :class="[
          'px-3 py-1 rounded',
          page === n ? 'bg-red-500 text-white' : 'hover:bg-gray-100',
        ]"
      >
        {{ n }}
      </button>

      <button
        @click="page++"
        :disabled="page === totalPages"
        class="px-2 py-1 rounded hover:bg-gray-100 disabled:opacity-30"
      >
        ＞
      </button>
    </div>
  </VueFinalModal>
</template>

<script setup>
import { ref, computed } from "vue";
import { VueFinalModal } from "vue-final-modal";

const props = defineProps({
  modelValue: Boolean,
  items: Array,
});

const emit = defineEmits(["update:modelValue", "select"]);

// v-model 연결
const modalOpenModel = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const page = ref(1);
const perPage = 5;
const totalPages = computed(() => Math.ceil(props.items.length / perPage));
const pagedList = computed(() => {
  const start = (page.value - 1) * perPage;
  return props.items.slice(start, start + perPage);
});

const selectItem = (item) => {
  emit("select", item.name);
  modalOpenModel.value = false;
};
</script>
