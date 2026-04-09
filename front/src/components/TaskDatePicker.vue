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
    <div class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
      <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
    </div>
  </div>
</template>

<script setup>
import flatPickr from 'vue-flatpickr-component';
import 'flatpickr/dist/flatpickr.min.css';

defineProps({
  modelValue: String,
  disabled: Boolean,
});

defineEmits(['update:modelValue']);

const config = {
  dateFormat: 'Y-m-d',
  disableMobile: true,
  disable: [
    (date) => date.getDay() === 0 || date.getDay() === 6,
  ],
};
</script>

<style>
.flatpickr-calendar {
  border-radius: 12px !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12) !important;
  border: 1px solid #e2e8f0 !important;
  font-family: inherit !important;
  overflow: hidden;
}

.flatpickr-months {
  background: #1e3a5f !important;
  padding: 8px 0 !important;
  border-radius: 12px 12px 0 0 !important;
}
.flatpickr-month {
  color: #fff !important;
  fill: #fff !important;
}
.flatpickr-current-month {
  color: #fff !important;
  font-size: 15px !important;
  font-weight: 600 !important;
}
.flatpickr-current-month select,
.flatpickr-current-month input.cur-year {
  color: #fff !important;
}
.flatpickr-prev-month svg,
.flatpickr-next-month svg {
  fill: #fff !important;
}
.flatpickr-prev-month:hover svg,
.flatpickr-next-month:hover svg {
  fill: #93c5fd !important;
}

.flatpickr-weekdays {
  background: #f1f5f9 !important;
}
.flatpickr-weekday {
  color: #64748b !important;
  font-weight: 600 !important;
  font-size: 12px !important;
}

.flatpickr-day {
  border-radius: 8px !important;
  color: #1e293b !important;
  font-size: 13px !important;
}
.flatpickr-day:hover:not(.flatpickr-disabled) {
  background: #e0f2fe !important;
  border-color: #e0f2fe !important;
}
.flatpickr-day.selected {
  background: #1882c9 !important;
  border-color: #1882c9 !important;
  color: #fff !important;
  font-weight: 600 !important;
}
.flatpickr-day.today {
  border-color: #1882c9 !important;
  color: #1882c9 !important;
  font-weight: 700 !important;
}
.flatpickr-day.today.selected {
  color: #fff !important;
}
.flatpickr-day.flatpickr-disabled,
.flatpickr-day.flatpickr-disabled:hover {
  color: #cbd5e1 !important;
  text-decoration: line-through !important;
  background: transparent !important;
  cursor: not-allowed !important;
}
.flatpickr-day.prevMonthDay,
.flatpickr-day.nextMonthDay {
  color: #cbd5e1 !important;
}
</style>