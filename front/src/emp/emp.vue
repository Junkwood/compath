<template>
  <div class="flex h-[100dvh] overflow-hidden">
    <!-- 1. 사이드바 -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <!-- 2. 헤더 -->
      <Header :sidebarOpen="sidebarOpen" @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <!-- 3. 메인 콘텐츠 (여기에 표가 들어갑니다!) -->
  <main class="grow">
  <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">

    <!-- Page header -->
    <div class="sm:flex sm:justify-between sm:items-center mb-8">

      <!-- Left: Title -->
      <div class="mb-4 sm:mb-0">
        <h1 class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold">계정 관리 ✨</h1>
      </div>

      <!-- Right: Actions -->
      <div class="grid grid-flow-col sm:auto-cols-max justify-start sm:justify-end gap-2">

        <!-- Search form (UI only) -->
        <form class="relative">
          <label for="action-search" class="sr-only">검색</label>
          <input id="action-search" class="form-input pl-9 bg-white dark:bg-gray-800" type="search" placeholder="이름 또는 아이디 검색" />
          <button class="absolute inset-0 right-auto group" type="submit" aria-label="Search">
            <svg class="w-4 h-4 shrink-0 fill-current text-gray-400 dark:text-gray-500 group-hover:text-gray-500 dark:group-hover:text-gray-400 ml-3 mr-2" viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg">
              <path d="M7 14c-3.86 0-7-3.14-7-7s3.14-7 7-7 7 3.14 7 7-3.14 7-7 7zM7 2C4.243 2 2 4.243 2 7s2.243 5 5 5 5-2.243 5-5-2.243-5-5-5z" />
              <path d="M15.707 14.293L13.314 11.9a8.019 8.019 0 01-1.414 1.414l2.393 2.393a.997.997 0 001.414 0 .999.999 0 000-1.414z" />
            </svg>
          </button>
        </form>

        <!-- Add button -->
        <button class="btn bg-violet-500 hover:bg-violet-600 text-white">
          <svg class="w-4 h-4 fill-current opacity-50 shrink-0" viewBox="0 0 16 16">
            <path d="M15 7H9V1c0-.6-.4-1-1-1S7 .4 7 1v6H1c-.6 0-1 .4-1 1s.4 1 1 1h6v6c0 .6.4 1 1 1s1-.4 1-1V9h6c.6 0 1-.4 1-1s-.4-1-1-1z" />
          </svg>
          <span class="hidden xs:block ml-2">계정 추가</span>
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 relative">
      <header class="px-5 py-4">
        <h2 class="font-semibold text-gray-800 dark:text-gray-100">전체 계정 <span class="text-gray-400 dark:text-gray-500 font-medium">{{ accounts.length }}</span></h2>
      </header>

      <div>
        <div class="overflow-x-auto">
          <table class="table-auto w-full dark:text-gray-300">
            <!-- Table header -->
            <thead class="text-xs font-semibold uppercase text-gray-500 dark:text-gray-400 bg-gray-50 dark:bg-gray-900/20 border-t border-b border-gray-200 dark:border-gray-700/60">
            <tr>
              <th class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"><div class="font-semibold text-left">사번(ID)</div></th>
              <th class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"><div class="font-semibold text-left">이름</div></th>
              <th class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"><div class="font-semibold text-left">이메일</div></th>
              <th class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"><div class="font-semibold text-center">권한</div></th>
              <th class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"><div class="font-semibold text-center">활성화 상태</div></th>
              <th class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"><div class="font-semibold text-center">관리</div></th>
            </tr>
            </thead>

            <!-- Table body -->
            <tbody class="text-sm divide-y divide-gray-200 dark:divide-gray-700/60">
            <tr v-for="account in accounts" :key="account.userId">
              <!-- ID -->
              <td class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap">
                <div class="font-medium text-sky-500">#{{ account.userId }}</div>
              </td>
              <!-- Name -->
              <td class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap">
                <div class="font-medium text-gray-800 dark:text-gray-100">{{ account.name }}</div>
              </td>
              <!-- Email -->
              <td class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap">
                <div class="text-left">{{ account.email }}</div>
              </td>
              <!-- Role -->
              <td class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap">
                <div class="text-center">{{ account.userType === 'ADMIN' ? '관리자' : '일반 사용자' }}</div>
              </td>
              <!-- Status (Toggle Switch) -->
              <td class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap">
                <div class="text-center flex justify-center items-center">
                  <label class="relative inline-flex items-center cursor-pointer">
                    <!-- v-model과 true/false-value로 데이터 직접 연결 -->
                    <input
                        type="checkbox"
                        class="sr-only peer"
                        v-model="account.isActive"
                        true-value="Y"
                        false-value="N"
                    />
                    <!-- 토글 배경 및 동그라미 (스위치 UI) -->
                    <div class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:ring-4 peer-focus:ring-emerald-300 dark:peer-focus:ring-emerald-800 dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-emerald-500"></div>
                  </label>
                </div>
              </td>
              <!-- Actions -->
              <td class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap w-px">
                <div class="space-x-1 text-center">
                  <!-- 수정 버튼 -->
                  <button class="text-gray-400 hover:text-gray-500 dark:text-gray-500 dark:hover:text-gray-400 rounded-full">
                    <span class="sr-only">Edit</span>
                    <svg class="w-8 h-8 fill-current" viewBox="0 0 32 32">
                      <path d="M19.7 8.3c-.4-.4-1-.4-1.4 0l-10 10c-.2.2-.3.4-.3.7v4c0 .6.4 1 1 1h4c.3 0 .5-.1.7-.3l10-10c.4-.4.4-1 0-1.4l-4-4zM12.6 22H10v-2.6l6-6 2.6 2.6-6 6zm7.4-7.4L17.4 12l1.6-1.6 2.6 2.6-1.6 1.6z" />
                    </svg>
                  </button>

                </div>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  </main>
    </div>
  </div>

</template>

<script>
import {onMounted, ref} from 'vue';
import Sidebar from '../partials/Sidebar.vue'; // 경로 주의!
import Header from '../partials/Header.vue';   // 경로 주의!
import { useEmpStore } from '../stores/emp.js';
export default {
  name: 'AccountList',
  components: {
    Header,
    Sidebar,
  },
  setup() {
    const sidebarOpen = ref(false);
    const empStore = useEmpStore();
    // 백엔드 연결 전 UI 확인용 더미 데이터 (아까 본 구조와 동일)
    const accounts = ref([
      {
        userId: '1001',
        name: '김관리',
        email: 'admin01@email.com',
        userType: 'ADMIN',
        isActive: '활성',
      },
      {
        userId: '1002',
        name: '이주임',
        email: 'user02@email.com',
        userType: 'USER',
        isActive: '활성',
      },
      {
        userId: '1003',
        name: '박대리',
        email: 'park03@email.com',
        userType: 'USER',
        isActive: '비활성',
      },
      {
        userId: '1004',
        name: '최사원',
        email: 'choi04@email.com',
        userType: 'USER',
        isActive: '활성',
      }
    ]);
    onMounted(() => {
      empStore.getEmpList();
      accounts.value = empStore.userList;
    })
    return {
      accounts,
      sidebarOpen,
      empStore,
    };
  }
}
</script>