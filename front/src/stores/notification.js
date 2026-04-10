import { defineStore } from "pinia";
import api from "../utils/api";
import { useAuthStore } from "../stores/auth";
import { EventSourcePolyfill } from "event-source-polyfill";

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [],
    unreadCount: 0,
    eventSource: null,
  }),

  actions: {
    //알림 목록 조회
    async fetchNotifications(userId) {
      try {
        const res = await api.get(`/notifications/list/${userId}`);
        this.notifications = res.data;
        this.unreadCount = this.notifications.filter(
          (n) => n.isRead === "N",
        ).length;
      } catch (err) {
        console.error("알림 목록 로드 실패:", err);
      }
    },

    setupSSE(userId) {
      const token = localStorage.getItem("ACCESS_TOKEN");
      if (this.eventSource) {
        this.eventSource.close();
      }

      const eventSource = new EventSourcePolyfill(
        `${import.meta.env.VITE_API_BASE_URL}/notifications/subscribe/${userId}`,
        {
          headers: {
            // 헤더에 토큰 넣어야함.
            Authorization: `Bearer ${token}`,
          },
          // SSE 연결 유지 시간 (1시간세팅함)
          heartbeatTimeout: 60 * 60 * 1000,
        },
      );

      this.eventSource = eventSource;

      eventSource.addEventListener("notification", (event) => {
        const newNotif = JSON.parse(event.data);
        this.notifications.unshift(newNotif);
        this.unreadCount++;
      });

      eventSource.onerror = () => {
        eventSource.close();
        setTimeout(() => this.setupSSE(userId), 5000);
      };
    },

    // 단건 읽음 처리

    setupSSE(userId) {
      return;
    },

    async readOne(notif, userId) {
      if (notif.isRead === "Y") return;
      try {
        await api.put("/notifications/read", {
          notificationTargetNo: notif.notificationTargetNo,
        });
        await this.fetchNotifications(userId); // 상태 갱신
      } catch (err) {
        console.error("읽음 처리 실패:", err);
      }
    },

    //전체 읽음 처리
    async readAll(userId) {
      try {
        await api.put("/notifications/read-all", {
          receiverId: userId,
        });
        await this.fetchNotifications(userId);
      } catch (err) {
        console.error("전체 읽음 처리 실패:", err);
      }
    },
    closeSSE() {},
  },
});
