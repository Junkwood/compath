<template>
  <div class="proj-name-row">
    <span class="proj-name">【 {{ projectName }} 】</span>
    <span class="proj-period">{{ startDate }} - {{ endDate }}</span>
  </div>
</template>

<script setup>
defineProps({
  projectName: { type: String, required: true },
  startDate: { type: String, default: "" },
  endDate: { type: String, default: "" },
});
</script>

<style scoped>
.proj-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  /* 여백이 필요하면 아래 주석 해제 */
  /* margin-bottom: 1rem; */
}

.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e; /* 다크 네이비 톤 */
}

.proj-period {
  font-size: 13px;
  color: #64748b; /* 슬레이트 그레이 */
  letter-spacing: -0.02em;
}

@media (max-width: 768px) {
  .proj-name-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  .proj-name {
    font-size: 15px;
  }
}
</style>
