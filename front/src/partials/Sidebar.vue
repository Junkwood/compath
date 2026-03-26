<template>
  <div class="min-w-fit">
    <!-- Sidebar backdrop (mobile only) -->
    <div
      class="fixed inset-0 bg-gray-900/30 z-40 lg:hidden lg:z-auto transition-opacity duration-200"
      :class="sidebarOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'"
      aria-hidden="true"
    ></div>

    <!-- Sidebar -->
    <div
      id="sidebar"
      ref="sidebar"
      class="flex lg:flex! flex-col absolute z-40 left-0 top-0 lg:static lg:left-auto lg:top-auto lg:translate-x-0 h-[100dvh] overflow-y-scroll lg:overflow-y-auto no-scrollbar w-64 lg:w-20 lg:sidebar-expanded:!w-64 2xl:w-64! shrink-0 bg-white dark:bg-gray-800 p-4 transition-all duration-200 ease-in-out"
      :class="[
        variant === 'v2'
          ? 'border-r border-gray-200 dark:border-gray-700/60'
          : 'rounded-r-2xl shadow-xs',
        sidebarOpen ? 'translate-x-0' : '-translate-x-64',
      ]"
    >
      <!-- Sidebar header -->
      <div class="flex justify-between mb-10 pr-3 sm:px-2">
        <!-- Close button (mobile) -->
        <button
          ref="trigger"
          class="lg:hidden text-gray-500 hover:text-gray-400"
          @click.stop="$emit('close-sidebar')"
          aria-controls="sidebar"
          :aria-expanded="sidebarOpen"
        >
          <span class="sr-only">Close sidebar</span>
          <svg
            class="w-6 h-6 fill-current"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M10.7 18.7l1.4-1.4L7.8 13H20v-2H7.8l4.3-4.3-1.4-1.4L4 12z"
            />
          </svg>
        </button>
        <!-- Logo -->
        <router-link class="block" to="/">
          <div class="flex items-center justify-center gap-0">
            <img
              src="/src/images/comPath.png"
              class="w-20 h-auto object-contain shrink-0"
            />
            <span
              class="text-2xl font-bold text-gray-800 dark:text-gray-100 lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
            >
              ComPath
            </span>
          </div>
        </router-link>
      </div>

      <!-- Links -->
      <div class="space-y-8">
        <!-- PAGES group -->
        <div>
          <h3
            class="text-xs uppercase text-gray-400 dark:text-gray-500 font-semibold pl-3"
          >
            <span
              class="hidden lg:block lg:sidebar-expanded:hidden 2xl:hidden text-center w-6"
              aria-hidden="true"
              >•••</span
            >
            <span class="lg:hidden lg:sidebar-expanded:block 2xl:block"
              >메뉴</span
            >
          </h3>
          <ul class="mt-3">
            <!-- 메인 -->
            <router-link
              to="/"
              custom
              v-slot="{ href, navigate, isExactActive }"
            >
              <li
                class="pl-4 pr-3 py-2 rounded-lg mb-0.5 last:mb-0 bg-linear-to-r"
                :class="
                  isExactActive &&
                  'from-violet-500/[0.12] dark:from-violet-500/[0.24] to-violet-500/[0.04]'
                "
              >
                <a
                  class="block text-gray-800 dark:text-gray-100 truncate transition"
                  :class="
                    isExactActive
                      ? ''
                      : 'hover:text-gray-900 dark:hover:text-white'
                  "
                  :href="href"
                  @click="navigate"
                >
                  <div class="flex items-center">
                    <!-- 홈 아이콘 -->
                    <svg
                      class="shrink-0 fill-current"
                      :class="
                        isExactActive
                          ? 'text-violet-500'
                          : 'text-gray-400 dark:text-gray-500'
                      "
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      viewBox="0 0 16 16"
                    >
                      <path
                        d="M6.753 2.659a1 1 0 0 0-1.506-1.317L2.451 4.537l-.744-.744A1 1 0 1 0 .293 5.207l1.5 1.5a1 1 0 0 0 1.46-.048l3.5-4ZM6.753 10.659a1 1 0 1 0-1.506-1.317l-2.796 3.195-.744-.744a1 1 0 0 0-1.414 1.414l1.5 1.5a1 1 0 0 0 1.46-.049l3.5-4ZM8 4.5a1 1 0 0 1 1-1h6a1 1 0 1 1 0 2H9a1 1 0 0 1-1-1ZM9 11.5a1 1 0 1 0 0 2h6a1 1 0 1 0 0-2H9Z"
                      />
                    </svg>
                    <span
                      class="text-sm font-medium ml-4 lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                      >메인</span
                    >
                  </div>
                </a>
              </li>
            </router-link>

            <!-- 전체 프로젝트 목록 -->
            <router-link
              to="/projects"
              custom
              v-slot="{ href, navigate, isExactActive }"
            >
              <li
                class="pl-4 pr-3 py-2 rounded-lg mb-0.5 last:mb-0 bg-linear-to-r"
                :class="
                  isExactActive &&
                  'from-violet-500/[0.12] dark:from-violet-500/[0.24] to-violet-500/[0.04]'
                "
              >
                <a
                  class="block text-gray-800 dark:text-gray-100 truncate transition"
                  :class="
                    isExactActive
                      ? ''
                      : 'hover:text-gray-900 dark:hover:text-white'
                  "
                  :href="href"
                  @click="navigate"
                >
                  <div class="flex items-center">
                    <!-- 리스트 아이콘 -->
                    <svg
                      class="shrink-0 fill-current"
                      :class="
                        isExactActive
                          ? 'text-violet-500'
                          : 'text-gray-400 dark:text-gray-500'
                      "
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      viewBox="0 0 16 16"
                    >
                      <path
                        d="M1 2.5A1.5 1.5 0 0 1 2.5 1h11A1.5 1.5 0 0 1 15 2.5v11a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 1 13.5v-11ZM2.5 2a.5.5 0 0 0-.5.5v11a.5.5 0 0 0 .5.5h11a.5.5 0 0 0 .5-.5v-11a.5.5 0 0 0-.5-.5h-11ZM4 5.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5Zm0 3a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5Zm0 3a.5.5 0 0 1 .5-.5h4a.5.5 0 0 1 0 1h-4a.5.5 0 0 1-.5-.5Z"
                      />
                    </svg>
                    <span
                      class="text-sm font-medium ml-4 lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                      >전체 프로젝트 목록</span
                    >
                  </div>
                </a>
              </li>
            </router-link>

            <!-- 프로젝트 (드롭다운) -->
            <SidebarLinkGroup
              v-slot="parentLink"
              :activeCondition="currentRoute.fullPath.includes('/project/')"
            >
              <a
                class="block text-gray-800 dark:text-gray-100 truncate transition"
                :class="
                  currentRoute.fullPath.includes('/project/')
                    ? ''
                    : 'hover:text-gray-900 dark:hover:text-white'
                "
                href="#0"
                @click.prevent="
                  parentLink.handleClick();
                  sidebarExpanded = true;
                "
              >
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <!-- 폴더 아이콘 -->
                    <svg
                      class="shrink-0 fill-current"
                      :class="
                        currentRoute.fullPath.includes('/project/')
                          ? 'text-violet-500'
                          : 'text-gray-400 dark:text-gray-500'
                      "
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      viewBox="0 0 16 16"
                    >
                      <path
                        d="M.5 3l.04.87a1.99 1.99 0 0 0-.342 1.311l.637 7A2 2 0 0 0 2.826 14H9v-1H2.826a1 1 0 0 1-.995-.91l-.637-7A1 1 0 0 1 2.19 4h11.62a1 1 0 0 1 .996 1.09L14.54 8h1.005l.256-2.819A2 2 0 0 0 13.81 3H9.828a2 2 0 0 1-1.414-.586l-.828-.828A2 2 0 0 0 6.172 1H2.5a2 2 0 0 0-2 2Zm5.672-1a1 1 0 0 1 .707.293L7.586 3H2.19c-.24 0-.47.042-.683.12L1.5 3a1 1 0 0 1 1-1h3.672Z"
                      />
                      <path
                        d="M13.5 9a.5.5 0 0 1 .5.5V11h1.5a.5.5 0 1 1 0 1H14v1.5a.5.5 0 1 1-1 0V12h-1.5a.5.5 0 1 1 0-1H13V9.5a.5.5 0 0 1 .5-.5Z"
                      />
                    </svg>
                    <span
                      class="text-sm font-medium ml-4 lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                      >프로젝트</span
                    >
                  </div>
                  <!-- 화살표 -->
                  <div class="flex shrink-0 ml-2">
                    <svg
                      class="w-3 h-3 shrink-0 ml-1 fill-current text-gray-400 dark:text-gray-500"
                      :class="parentLink.expanded && 'rotate-180'"
                      viewBox="0 0 12 12"
                    >
                      <path d="M5.9 11.4L.5 6l1.4-1.4 4 4 4-4L11.3 6z" />
                    </svg>
                  </div>
                </div>
              </a>
              <div class="lg:hidden lg:sidebar-expanded:block 2xl:block">
                <ul class="pl-8 mt-1" :class="!parentLink.expanded && 'hidden'">
                  <router-link
                    to="/project/dashboard"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >대시보드</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    :to="{ name: 'taskList', params: { id: '1' } }"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          @click="goTaskList"
                          >업무 목록</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/task/TaskRegister"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >업무 생성</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/history"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >변경이력</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/gantt"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >간트차트</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/report"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >업무 보고서</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/meeting"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >회의록</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/time"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >소요시간</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/notice"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >공지사항</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/docs"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >문서</span
                        >
                      </a>
                    </li>
                  </router-link>

                  <router-link
                    to="/project/milestone"
                    custom
                    v-slot="{ href, navigate, isExactActive }"
                  >
                    <li class="mb-1 last:mb-0">
                      <a
                        class="block transition truncate"
                        :class="
                          isExactActive
                            ? 'text-violet-500'
                            : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'
                        "
                        :href="href"
                        @click="navigate"
                      >
                        <span
                          class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200"
                          >마일스톤</span
                        >
                      </a>
                    </li>
                  </router-link>
                </ul>
              </div>
            </SidebarLinkGroup >
            <SidebarLinkGroup
                v-if="auth.isAdmin"
                v-slot="parentLink"
                :activeCondition="currentRoute.fullPath.includes('/admin/')"
            >
              <a
                  class="block text-gray-800 dark:text-gray-100 truncate transition"
                  :class="
      currentRoute.fullPath.includes('/admin/')
        ? ''
        : 'hover:text-gray-900 dark:hover:text-white'
    "
                  href="#0"
                  @click.prevent="
      parentLink.handleClick();
      sidebarExpanded = true;
    "
              >
                <div class="flex items-center justify-between">
                  <div class="flex items-center">
                    <!-- 설정(톱니바퀴) 아이콘 -->
                    <svg
                        class="shrink-0 fill-current"
                        :class="
            currentRoute.fullPath.includes('/admin/')
              ? 'text-violet-500'
              : 'text-gray-400 dark:text-gray-500'
          "
                        xmlns="http://www.w3.org/2000/svg"
                        width="16"
                        height="16"
                        viewBox="0 0 16 16"
                    >
                      <path d="M14.3.3L5 9.6 1.7 6.3c-.4-.4-1-.4-1.4 0-.4.4-.4 1 0 1.4l4 4c.2.2.4.3.7.3.3 0 .5-.1.7-.3l10-10c.4-.4.4-1 0-1.4-.4-.4-1-.4-1.4 0z" />
                      <!-- 좀 더 톱니바퀴스러운 아이콘을 원하시면 svg path를 변경하세요 -->
                    </svg>
                    <span class="text-sm font-medium ml-4 lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200">
          설정 (관리자)
        </span>
                  </div>
                  <!-- 화살표 -->
                  <div class="flex shrink-0 ml-2">
                    <svg
                        class="w-3 h-3 shrink-0 ml-1 fill-current text-gray-400 dark:text-gray-500"
                        :class="parentLink.expanded && 'rotate-180'"
                        viewBox="0 0 12 12"
                    >
                      <path d="M5.9 11.4L.5 6l1.4-1.4 4 4 4-4L11.3 6z" />
                    </svg>
                  </div>
                </div>
              </a>

              <!-- 1뎁스 서브 메뉴 (계정, 그룹, 업무, 역할) -->
              <div class="lg:hidden lg:sidebar-expanded:block 2xl:block">
                <ul class="pl-8 mt-1" :class="!parentLink.expanded && 'hidden'">
                  <!-- 계정 -->
                  <router-link to="/admin/emp" custom v-slot="{ href, navigate, isExactActive }">
                    <li class="mb-1 last:mb-0">
                      <a
                          class="block transition truncate"
                          :class="isExactActive ? 'text-violet-500' : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'"
                          :href="href"
                          @click="navigate"
                      >
                        <span class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200">계정</span>
                      </a>
                    </li>
                  </router-link>

                  <!-- 그룹 -->
                  <router-link to="/admin/group" custom v-slot="{ href, navigate, isExactActive }">
                    <li class="mb-1 last:mb-0">
                      <a
                          class="block transition truncate"
                          :class="isExactActive ? 'text-violet-500' : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'"
                          :href="href"
                          @click="navigate"
                      >
                        <span class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200">그룹</span>
                      </a>
                    </li>
                  </router-link>

                  <!-- 역할 -->
                  <router-link to="/admin/role" custom v-slot="{ href, navigate, isExactActive }">
                    <li class="mb-1 last:mb-0">
                      <a
                          class="block transition truncate"
                          :class="isExactActive ? 'text-violet-500' : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'"
                          :href="href"
                          @click="navigate"
                      >
                        <span class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200">역할</span>
                      </a>
                    </li>
                  </router-link>

                  <!-- 업무 (커스텀 토글 방식 - SidebarLinkGroup 제거) -->
                  <li class="mb-1 last:mb-0">
                    <a
                        class="block transition w-full"
                        :class="currentRoute.fullPath.includes('/admin/task/') ? 'text-violet-500' : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'"
                        href="#0"
                        @click.prevent="taskExpanded = !taskExpanded"
                    >
                      <div class="flex items-center justify-between">
                        <!-- 텍스트 영역: 이제 '역할', '그룹'과 완벽히 같은 라인에 섭니다 -->
                        <span class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200 truncate">
        업무
      </span>
                        <!-- 화살표 영역 -->
                        <svg
                            class="w-3 h-3 shrink-0 ml-1 fill-current text-gray-400 dark:text-gray-500 transition-transform duration-200"
                            :class="taskExpanded ? 'rotate-180' : ''"
                            viewBox="0 0 12 12"
                        >
                          <path d="M5.9 11.4L.5 6l1.4-1.4 4 4 4-4L11.3 6z" />
                        </svg>
                      </div>
                    </a>

                    <!-- 2뎁스 서브 메뉴 (업무상태, 업무유형) -->
                    <ul class="pl-4 mt-1" :class="!taskExpanded && 'hidden'">
                      <!-- 업무상태 -->
                      <router-link to="/admin/task/status" custom v-slot="{ href, navigate, isExactActive }">
                        <li class="mb-1 last:mb-0">
                          <a class="block transition truncate" :class="isExactActive ? 'text-violet-500' : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'" :href="href" @click="navigate">
                            <span class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200">- 업무 상태</span>
                          </a>
                        </li>
                      </router-link>

                      <!-- 업무유형 -->
                      <router-link to="/admin/task/type" custom v-slot="{ href, navigate, isExactActive }">
                        <li class="mb-1 last:mb-0">
                          <a class="block transition truncate" :class="isExactActive ? 'text-violet-500' : 'text-gray-500/90 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-200'" :href="href" @click="navigate">
                            <span class="text-sm font-medium lg:opacity-0 lg:sidebar-expanded:opacity-100 2xl:opacity-100 duration-200">- 업무 유형</span>
                          </a>
                        </li>
                      </router-link>
                    </ul>
                  </li>
                </ul>
              </div>
            </SidebarLinkGroup>
          </ul>

        </div>
      </div>

      <!-- Expand / collapse button -->
      <div class="pt-3 hidden lg:inline-flex 2xl:hidden justify-end mt-auto">
        <div class="w-12 pl-4 pr-3 py-2">
          <button
            class="text-gray-400 hover:text-gray-500 dark:text-gray-500 dark:hover:text-gray-400"
            @click.prevent="sidebarExpanded = !sidebarExpanded"
          >
            <span class="sr-only">Expand / collapse sidebar</span>
            <svg
              class="shrink-0 fill-current text-gray-400 dark:text-gray-500 sidebar-expanded:rotate-180"
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              viewBox="0 0 16 16"
            >
              <path
                d="M15 16a1 1 0 0 1-1-1V1a1 1 0 1 1 2 0v14a1 1 0 0 1-1 1ZM8.586 7H1a1 1 0 1 0 0 2h7.586l-2.793 2.793a1 1 0 1 0 1.414 1.414l4.5-4.5A.997.997 0 0 0 12 8.01M11.924 7.617a.997.997 0 0 0-.217-.324l-4.5-4.5a1 1 0 0 0-1.414 1.414L8.586 7M12 7.99a.996.996 0 0 0-.076-.373Z"
              />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, watch } from "vue";
import { useRouter } from "vue-router";
import SidebarLinkGroup from "./SidebarLinkGroup.vue";
import {useAuthStore} from "../stores/auth";


const router = useRouter();

const goTaskList = () => {
  router.push({ name: "taskList", params: { id: "PJT-001" } });
};

export default {
  name: "Sidebar",
  props: ["sidebarOpen", "variant"],
  components: { SidebarLinkGroup },
  setup(props, { emit }) {
    const auth = useAuthStore();
    const trigger = ref(null);
    const sidebar = ref(null);

    const storedSidebarExpanded = localStorage.getItem("sidebar-expanded");
    const sidebarExpanded = ref(
      storedSidebarExpanded === null ? false : storedSidebarExpanded === "true",
    );

    const currentRoute = useRouter().currentRoute.value;
    const taskExpanded = ref(currentRoute.fullPath.includes('/admin/task/'));
    const clickHandler = ({ target }) => {
      if (!sidebar.value || !trigger.value) return;
      if (
        !props.sidebarOpen ||
        sidebar.value.contains(target) ||
        trigger.value.contains(target)
      )
        return;
      emit("close-sidebar");
    };

    const keyHandler = ({ keyCode }) => {
      if (!props.sidebarOpen || keyCode !== 27) return;
      emit("close-sidebar");
    };

    onMounted(() => {
      document.addEventListener("click", clickHandler);
      document.addEventListener("keydown", keyHandler);
    });

    onUnmounted(() => {
      document.removeEventListener("click", clickHandler);
      document.removeEventListener("keydown", keyHandler);
    });

    watch(sidebarExpanded, () => {
      localStorage.setItem("sidebar-expanded", sidebarExpanded.value);
      if (sidebarExpanded.value) {
        document.querySelector("body").classList.add("sidebar-expanded");
      } else {
        document.querySelector("body").classList.remove("sidebar-expanded");
      }
    });

    return { trigger, sidebar, sidebarExpanded, currentRoute, auth, taskExpanded};
  },
};
</script>
