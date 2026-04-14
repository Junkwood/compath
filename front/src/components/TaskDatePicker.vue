<template>
  <div class="relative">
    <flat-pickr
      :modelValue="modelValue"
      @update:modelValue="$emit('update:modelValue', $event)"
      :config="config"
      :disabled="disabled"
      class="w-full pl-9 pr-3 py-2 text-sm text-gray-700 bg-white border border-gray-200 rounded-[10px] outline-none transition focus:border-slate-400 focus:shadow-[0_0_0_3px_rgba(148,163,184,0.15)] disabled:bg-slate-100 disabled:text-slate-400 cursor-pointer"
      placeholder="날짜를 선택하세요"
    />
    <div
      class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none"
    >
      <svg
        class="w-4 h-4 text-gray-400"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
        />
      </svg>
    </div>
  </div>
</template>

<script setup>
import flatPickr from "vue-flatpickr-component";
import "flatpickr/dist/flatpickr.min.css";
import { Korean } from "flatpickr/dist/l10n/ko.js";

defineProps({
  modelValue: String,
  disabled: Boolean,
});

defineEmits(["update:modelValue", "change"]);

// 헤더를 "2026년 4월" → "2026 ▾  4월" 순으로 재배치
const reorderHeader = (instance) => {
  const monthEl = instance.monthNav;
  if (!monthEl) return;
  const year = monthEl.querySelector(".numInputWrapper");
  const month = monthEl.querySelector(".flatpickr-month");
  if (year && month && month.firstChild !== year) {
    month.insertBefore(year, month.firstChild);
  }
};

const config = {
  dateFormat: "Y-m-d",
  disableMobile: true,
  locale: Korean,
  disable: [(date) => date.getDay() === 0 || date.getDay() === 6],
};
</script>

<style>
.flatpickr-calendar {
  border-radius: 10px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
  border: 1px solid #e2e8f0 !important;
  font-family: "Pretendard", "Noto Sans KR", sans-serif !important;
  padding: 14px !important;
  width: 280px !important;
}

.flatpickr-months {
  background: transparent !important;
  padding-bottom: 10px !important;
  border-bottom: 1px solid #f1f5f9 !important;
}
.flatpickr-month {
  height: 36px !important;
}
.flatpickr-current-month {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 6px !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  color: #1e293b !important;
  padding: 0 !important;
  width: 100% !important;
  position: static !important;
  left: unset !important;
}
.flatpickr-current-month .numInputWrapper {
  width: 52px !important;
}
.flatpickr-current-month input.cur-year {
  color: #1e293b !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  width: 52px !important;
  padding: 0 !important;
}
.flatpickr-current-month select.flatpickr-monthDropdown-months {
  color: #1e293b !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  appearance: auto !important;
  padding: 0 4px !important;
  margin: 0 !important;
  background: transparent !important;
  border: none !important;
  cursor: pointer !important;
}
.numInputWrapper span {
  display: none !important;
}
.flatpickr-prev-month svg,
.flatpickr-next-month svg {
  fill: #94a3b8 !important;
  width: 13px !important;
  height: 13px !important;
}
.flatpickr-prev-month:hover svg,
.flatpickr-next-month:hover svg {
  fill: #334155 !important;
}

.flatpickr-weekdays {
  background: transparent !important;
  margin-top: 8px !important;
}
.flatpickr-weekday {
  color: #94a3b8 !important;
  font-weight: 600 !important;
  font-size: 11px !important;
}

.flatpickr-days,
.dayContainer {
  width: 252px !important;
  min-width: 252px !important;
  max-width: 252px !important;
}
.flatpickr-day {
  border-radius: 6px !important;
  color: #334155 !important;
  font-size: 13px !important;
  border: none !important;
  height: 36px !important;
  line-height: 36px !important;
  max-width: 36px !important;
}
.flatpickr-day:hover:not(.flatpickr-disabled) {
  background: #f1f5f9 !important;
}
.flatpickr-day.selected {
  background: #2563eb !important;
  color: #fff !important;
  font-weight: 600 !important;
}
.flatpickr-day.today {
  border: 1.5px solid #2563eb !important;
  color: #2563eb !important;
  font-weight: 700 !important;
  background: transparent !important;
}
.flatpickr-day.today.selected {
  background: #2563eb !important;
  color: #fff !important;
}
.flatpickr-day.flatpickr-disabled,
.flatpickr-day.flatpickr-disabled:hover {
  color: #e2e8f0 !important;
  background: transparent !important;
  cursor: not-allowed !important;
}
.flatpickr-day.prevMonthDay,
.flatpickr-day.nextMonthDay {
  color: #cbd5e1 !important;
}
</style>
