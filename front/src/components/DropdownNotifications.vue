<template>
  <div class="relative inline-flex">
    <button
      ref="trigger"
      class="w-8 h-8 flex items-center justify-center hover:bg-gray-100 lg:hover:bg-gray-200 dark:hover:bg-gray-700/50 dark:lg:hover:bg-gray-800 rounded-full"
      :class="{ 'bg-gray-200 dark:bg-gray-800': dropdownOpen }"
      aria-haspopup="true"
      @click.prevent="
        () => {
          dropdownOpen = !dropdownOpen;
          if (dropdownOpen) updatePosition();
        }
      "
      :aria-expanded="dropdownOpen"
    >
      <span class="sr-only">Notifications</span>
      <svg
        class="w-4 h-4 fill-current text-gray-500/80 dark:text-gray-400/80"
        viewBox="0 0 16 16"
        xmlns="http://www.w3.org/2000/svg"
      >
        <path
          d="M8 0C4.686 0 2 2.686 2 6v4.586l-1.707 1.707C0.105 12.481 0 12.731 0 13a1 1 0 0 0 1 1h14a1 1 0 0 0 1-1c0-.269-.105-.519-.293-.707L14 10.586V6c0-3.314-2.686-6-6-6ZM8 2c2.206 0 4 1.794 4 4v5H4V6c0-2.206 1.794-4 4-4Zm0 14c1.105 0 2-.895 2-2H6a2 2 0 0 0 2 2Z"
        />
      </svg>
      <div
        v-if="unreadCount > 0"
        class="absolute top-0 right-0 w-2.5 h-2.5 bg-red-500 border-2 border-white dark:border-gray-900 rounded-full"
      ></div>
    </button>

    <Teleport to="body">
      <transition
        enter-active-class="transition ease-out duration-150 transform"
        enter-from-class="opacity-0 -translate-y-1"
        enter-to-class="opacity-100 translate-y-0"
        leave-active-class="transition ease-in duration-100"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div
          v-show="dropdownOpen"
          class="notif-dropdown"
          :style="dropdownStyle"
        >
          <!-- 헤더 -->
          <div class="notif-header">
            <span class="notif-title">알림</span>
            <button
              v-if="unreadCount > 0"
              @click="handleReadAll"
              class="notif-readall"
            >
              모두 읽음
            </button>
          </div>

          <!-- 리스트 -->
          <ul ref="dropdown" class="notif-list">
            <li v-if="notifications.length === 0" class="notif-empty">
              새로운 알림이 없습니다.
            </li>

            <li
              v-for="notif in notifications"
              :key="notif.notificationTargetNo"
              class="notif-item"
              :class="{ 'is-read': notif.isRead === 'Y' }"
              @click="handleReadOne(notif)"
            >
              <div class="notif-dot-wrap">
                <span v-if="notif.isRead !== 'Y'" class="notif-dot"></span>
              </div>
              <div class="notif-body">
                <p class="notif-msg-title">{{ notif.title }}</p>
                <p class="notif-msg">{{ notif.message }}</p>
                <span class="notif-time">{{ notif.createdAt }}</span>
              </div>
            </li>
          </ul>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useNotificationStore } from "../stores/notification";
import { useAuthStore } from "../stores/auth";

export default {
  name: "DropdownNotifications",
  props: ["align"],
  setup() {
    const dropdownOpen = ref(false);
    const trigger = ref(null);
    const dropdown = ref(null);
    const dropdownStyle = ref({ top: "0px", right: "0px" });

    const updatePosition = () => {
      if (!trigger.value) return;
      const rect = trigger.value.getBoundingClientRect();
      dropdownStyle.value = {
        top: `${rect.bottom + 8}px`,
        right: `${window.innerWidth - rect.right}px`,
      };
    };

    const notificationStore = useNotificationStore();
    const authStore = useAuthStore();
    const userId = computed(() => authStore.user.userId);

    const notifications = computed(() => notificationStore.notifications);
    const unreadCount = computed(() => notificationStore.unreadCount);

    const handleReadOne = (notif) => {
      notificationStore.readOne(notif, userId.value);
    };

    const handleReadAll = () => {
      notificationStore.readAll(userId.value);
    };

    const clickHandler = ({ target }) => {
      if (
        !dropdownOpen.value ||
        dropdown.value?.contains(target) ||
        trigger.value?.contains(target)
      )
        return;
      dropdownOpen.value = false;
    };

    onMounted(() => {
      notificationStore.fetchNotifications(userId.value);
      notificationStore.setupSSE(userId.value);
      document.addEventListener("click", clickHandler);
    });

    onUnmounted(() => {
      document.removeEventListener("click", clickHandler);
      notificationStore.closeSSE();
    });

    return {
      dropdownOpen,
      trigger,
      dropdown,
      dropdownStyle,
      updatePosition,
      notifications,
      unreadCount,
      handleReadOne,
      handleReadAll,
    };
  },
};
</script>

<style scoped>
.notif-dropdown {
  position: fixed;
  width: 320px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.12),
    0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  z-index: 99999;
}

.notif-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px 10px;
  border-bottom: 1px solid #f1f5f9;
}

.notif-title {
  font-size: 13px;
  font-weight: 600;
  color: #0f172a;
}

.notif-readall {
  font-size: 11px;
  font-weight: 500;
  color: #3b82f6;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.notif-readall:hover {
  color: #1d4ed8;
}

.notif-list {
  max-height: 360px;
  overflow-y: auto;
  list-style: none;
  margin: 0;
  padding: 4px 0;
}

.notif-list::-webkit-scrollbar {
  width: 4px;
}
.notif-list::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 2px;
}

.notif-empty {
  padding: 32px 16px;
  text-align: center;
  font-size: 13px;
  color: #94a3b8;
}

.notif-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f8fafc;
  transition: background 0.12s;
}

.notif-item:last-child {
  border-bottom: none;
}

.notif-item:hover {
  background: #f8fafc;
}

.notif-item.is-read {
  opacity: 0.5;
}

.notif-dot-wrap {
  width: 8px;
  flex-shrink: 0;
  padding-top: 5px;
}

.notif-dot {
  display: block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #3b82f6;
}

.notif-body {
  flex: 1;
  min-width: 0;
}

.notif-msg-title {
  font-size: 12.5px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notif-msg {
  font-size: 12px;
  color: #64748b;
  margin: 0 0 4px;
  line-height: 1.4;
}

.notif-time {
  font-size: 11px;
  color: #cbd5e1;
}
</style>
