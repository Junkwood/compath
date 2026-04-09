<template>
  <div class="relative inline-flex">
    <button
      ref="trigger"
      class="w-8 h-8 flex items-center justify-center hover:bg-gray-100 lg:hover:bg-gray-200 dark:hover:bg-gray-700/50 dark:lg:hover:bg-gray-800 rounded-full"
      :class="{ 'bg-gray-200 dark:bg-gray-800': dropdownOpen }"
      aria-haspopup="true"
      @click.prevent="dropdownOpen = !dropdownOpen"
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

    <transition
      enter-active-class="transition ease-out duration-200 transform"
      enter-from-class="opacity-0 -translate-y-2"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-out duration-200"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-show="dropdownOpen"
        class="origin-top-right z-10 absolute top-full -mr-48 sm:mr-0 min-w-80 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700/60 py-1.5 rounded-lg shadow-lg overflow-hidden mt-1"
        :class="align === 'right' ? 'right-0' : 'left-0'"
      >
        <div class="flex items-center justify-between py-1.5 px-4">
          <!-- <div
            class="text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase"
          >
            Notifications 
          </div> -->
          <button
            v-if="unreadCount > 0"
            @click="handleReadAll"
            class="text-[10px] text-blue-500 hover:text-blue-600 font-medium"
          >
            모두 읽음
          </button>
        </div>

        <ul ref="dropdown" class="max-h-96 overflow-y-auto">
          <li
            v-if="notifications.length === 0"
            class="py-4 px-4 text-sm text-gray-500 text-center"
          >
            새로운 알림이 없습니다.
          </li>

          <li
            v-for="notif in notifications"
            :key="notif.notificationTargetNo"
            class="border-b border-gray-200 dark:border-gray-700/60 last:border-0"
          >
            <div
              class="block py-2 px-4 hover:bg-gray-50 dark:hover:bg-gray-700/20 cursor-pointer"
              @click="handleReadOne(notif)"
              :class="{ 'opacity-60': notif.isRead === 'Y' }"
            >
              <span class="block text-sm mb-1">
                <span class="font-medium text-gray-800 dark:text-gray-100">{{
                  notif.title
                }}</span>
                <p class="text-gray-600 dark:text-gray-400 leading-tight">
                  {{ notif.message }}
                </p>
              </span>
              <span class="block text-xs font-medium text-gray-400">{{
                notif.createdAt
              }}</span>
            </div>
          </li>
        </ul>
      </div>
    </transition>
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

    const notificationStore = useNotificationStore();

    // 로그인한 사용자 ID
    const authStore = useAuthStore();
    const userId = computed(() => authStore.user.userId);

    const notifications = computed(() => notificationStore.notifications);
    const unreadCount = computed(() => notificationStore.unreadCount);

    // 단건 읽음 호출
    const handleReadOne = (notif) => {
      notificationStore.readOne(notif, userId);
    };

    // 전체 읽음 호출
    const handleReadAll = () => {
      notificationStore.readAll(userId);
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
      notifications,
      unreadCount,
      handleReadOne,
      handleReadAll,
    };
  },
};
</script>
