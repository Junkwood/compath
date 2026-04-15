<template>
  <div class="flex h-screen overflow-hidden bg-gray-50 dark:bg-gray-900">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />
    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <Header :sidebarOpen="sidebarOpen" @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <main class="page">
        <!-- 서브헤더 -->
        <div class="sub-header">
          <div class="breadcrumb">
            <span>홈</span><span class="bc-sep">›</span>
            <span>프로젝트</span><span class="bc-sep">›</span>
            <span>업무 보고서</span><span class="bc-sep">›</span>
            <span class="bc-cur">통계 상세</span>
          </div>
          <button class="btn-back" @click="goBack">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M19 12H5M11 6l-6 6 6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            목록으로
          </button>
        </div>

        <div class="page-inner">
          <!-- 로딩 -->
          <div v-if="loading" class="loading-wrap">
            <div class="loading-spinner"></div>
            <span>데이터를 불러오는 중...</span>
          </div>

          <template v-else>
            <!-- 프로젝트 헤더 바 -->
            <div class="proj-header-bar">
              <div class="proj-info">
                <span class="proj-label">프로젝트</span>
                <div class="select-wrap">
                  <select v-model="selectedProject" @change="onProjectChange">
                    <option value="">전체 프로젝트</option>
                    <option v-for="p in projectOptions" :key="p.value" :value="p.value">
                      {{ p.label }}
                    </option>
                  </select>
                  <span class="select-arrow">▾</span>
                </div>
                <span class="proj-label" style="margin-left:12px">기간</span>
                <input v-model="filterStart" type="date" class="date-input" />
                <span class="date-sep">~</span>
                <input v-model="filterEnd" type="date" class="date-input" />
                <button class="btn-apply" @click="applyFilter">적용</button>
              </div>
              <div class="period-wrap">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" style="color:#64748b">
                  <rect x="3" y="4" width="18" height="18" rx="2" stroke="currentColor" stroke-width="2"/>
                  <path d="M16 2v4M8 2v4M3 10h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                </svg>
                <span class="period-text">{{ periodLabel }}</span>
              </div>
            </div>

            <!-- 데이터 없음 -->
            <div v-if="allTasks.length === 0" class="empty-wrap">
              <svg width="40" height="40" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="7" rx="1.5" fill="#cbd5e1"/><rect x="14" y="3" width="7" height="7" rx="1.5" fill="#cbd5e1"/><rect x="3" y="14" width="7" height="7" rx="1.5" fill="#cbd5e1"/><rect x="14" y="14" width="7" height="7" rx="1.5" fill="#cbd5e1"/></svg>
              <p>조회된 업무 데이터가 없습니다.</p>
            </div>

            <template v-else>
              <!-- 요약 KPI 카드 -->
              <div class="kpi-row">
                <div class="kpi-card" v-for="kpi in kpiCards" :key="kpi.label">
                  <div class="kpi-icon" :style="{ background: kpi.bg }">
                    <span v-html="kpi.icon"></span>
                  </div>
                  <div class="kpi-body">
                    <div class="kpi-val">{{ kpi.value }}</div>
                    <div class="kpi-lbl">{{ kpi.label }}</div>
                  </div>
                  <div class="kpi-trend" :class="kpi.trendUp ? 'up' : 'neutral'">
                    {{ kpi.sub }}
                  </div>
                </div>
              </div>

              <!-- 메인 차트 그리드 -->
              <div class="dashboard-grid">
                <!-- 왼쪽: 차트 2×2 -->
                <div class="charts-left">

                  <!-- Row 1 -->
                  <div class="chart-row">
                    <!-- 진척도 분포 (도넛) -->
                    <div class="chart-card">
                      <div class="chart-card-header">
                        <span class="chart-card-title">진척도 분포</span>
                        <span class="chart-unit">{{ selectedProjectLabel }}</span>
                      </div>
                      <div style="display:flex;gap:16px;align-items:center;height:150px">
                        <div style="position:relative;width:130px;flex-shrink:0;height:130px">
                          <canvas ref="progressDonut"></canvas>
                          <div class="donut-center">
                            <span class="donut-total">{{ filteredTasks.length }}</span>
                            <span class="donut-total-lbl">건</span>
                          </div>
                        </div>
                        <div class="donut-legend" style="flex:1;flex-direction:column;gap:6px;align-items:flex-start">
                          <span v-for="item in progressBands" :key="item.label" class="donut-leg-item">
                            <span class="legend-sq" :style="{background:item.color}"></span>
                            <span style="flex:1">{{ item.label }}</span>
                            <span style="font-weight:700;color:#0f172a">{{ item.count }}건</span>
                          </span>
                        </div>
                      </div>
                    </div>

                    <!-- 업무 유형별 현황 (막대) -->
                    <div class="chart-card">
                      <div class="chart-card-header">
                        <span class="chart-card-title">업무 유형별 현황</span>
                        <span class="chart-unit">건수 기준</span>
                      </div>
                      <div class="chart-canvas-wrap">
                        <canvas ref="typeBar"></canvas>
                      </div>
                      <div class="chart-legend">
                        <span class="legend-item">
                          <span class="legend-sq" style="background:#3b82f6"></span>업무 건수
                        </span>
                      </div>
                    </div>
                  </div>

                  <!-- Row 2 -->
                  <div class="chart-row">
                    <!-- 담당자별 업무량 (가로 막대) -->
                    <div class="chart-card">
                      <div class="chart-card-header">
                        <span class="chart-card-title">담당자별 업무량</span>
                        <span class="chart-unit">건수 기준</span>
                      </div>
                      <div class="chart-canvas-wrap">
                        <canvas ref="assigneeHBar"></canvas>
                      </div>
                      <div class="hbar-footer">
                        <span class="hbar-count">담당자 합계 = {{ uniqueAssignees }}명</span>
                        <div class="mini-pager">
                          <button class="mpg" @click="aPage = Math.max(1, aPage-1)">‹</button>
                          <button v-for="p in aTotalPages" :key="p" class="mpg" :class="{on: p===aPage}" @click="aPage=p">{{ p }}</button>
                          <button class="mpg" @click="aPage = Math.min(aTotalPages, aPage+1)">›</button>
                        </div>
                      </div>
                    </div>

                    <!-- 날짜별 업무 등록 추이 (라인) -->
                    <div class="chart-card">
                      <div class="chart-card-header">
                        <span class="chart-card-title">날짜별 업무 추이</span>
                        <span class="chart-unit">마감일 기준</span>
                      </div>
                      <div class="chart-canvas-wrap">
                        <canvas ref="dateLine"></canvas>
                      </div>
                      <div class="chart-legend">
                        <span class="legend-item">
                          <span class="legend-sq" style="background:#6366f1;border-radius:50%"></span>일별 업무 수
                        </span>
                      </div>
                    </div>
                  </div>

                  <!-- Row 3: 프로젝트별 진척도 -->
                  <div class="chart-card" style="grid-column:1/-1">
                    <div class="chart-card-header">
                      <span class="chart-card-title">프로젝트별 진척도 현황</span>
                      <span class="chart-unit">평균 진척도 %</span>
                    </div>
                    <div class="chart-canvas-wrap" style="height:160px">
                      <canvas ref="projectProgressBar"></canvas>
                    </div>
                  </div>

                </div>

                <!-- 오른쪽: 도넛 + 통계 -->
                <div class="charts-right">
                  <!-- 업무 유형 비중 도넛 -->
                  <div class="chart-card donut-card">
                    <div class="chart-card-header" style="justify-content:center;flex-direction:column;align-items:center;gap:2px">
                      <span class="chart-card-title">업무 유형 비중</span>
                      <span style="font-size:11px;color:#94a3b8">{{ selectedProjectLabel }}</span>
                    </div>
                    <div class="donut-canvas-wrap">
                      <canvas ref="typeDonut"></canvas>
                      <div class="donut-center">
                        <span class="donut-total">{{ filteredTasks.length }}</span>
                        <span class="donut-total-lbl">건</span>
                      </div>
                    </div>
                    <div class="donut-legend">
                      <span v-for="item in typeDonutData" :key="item.label" class="donut-leg-item">
                        <span class="legend-sq" :style="{background:item.color}"></span>
                        <span>{{ item.label }}</span>
                      </span>
                    </div>
                  </div>

                  <!-- 상태별 통계 카드 -->
                  <div class="stat-grid">
                    <div class="stat-card" v-for="s in statusStats" :key="s.label">
                      <div class="stat-val" :style="{color: s.color}">{{ s.value }}</div>
                      <div class="stat-lbl">{{ s.label }}</div>
                    </div>
                  </div>

                  <!-- 평균 진척도 게이지 -->
                  <div class="chart-card" style="padding:16px">
                    <div class="gauge-title">전체 평균 진척도</div>
                    <div class="gauge-wrap">
                      <div class="gauge-bar-bg">
                        <div class="gauge-bar-fill" :style="{width: avgProgress + '%', background: progressColor(avgProgress)}"></div>
                      </div>
                      <span class="gauge-pct">{{ avgProgress }}%</span>
                    </div>
                    <div class="gauge-sub">{{ completedCount }}건 완료 / {{ filteredTasks.length }}건 전체</div>
                  </div>
                </div>
              </div>
            </template>
          </template>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed, watch, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from '../partials/Sidebar.vue'
import Header from '../partials/Header.vue'
import Chart from 'chart.js/auto'
import api from '../utils/api'

export default defineComponent({
  name: 'TaskReportStats',
  components: { Sidebar, Header },

  setup() {
    const route = useRoute()
    const router = useRouter()
    const projectId = route.params.projectId

    /* ── canvas refs ── */
    const progressDonut = ref(null)
    const typeBar = ref(null)
    const assigneeHBar = ref(null)
    const dateLine = ref(null)
    const projectProgressBar = ref(null)
    const typeDonut = ref(null)

    /* ── state ── */
    const sidebarOpen = ref(false)
    const loading = ref(false)
    const allTasks = ref([])
    const projectList = ref([])
    const selectedProject = ref('')
    const filterStart = ref('')
    const filterEnd = ref('')
    const aPage = ref(1)
    const HB_PER = 6

    const TYPE_COLORS = {
      '개발': '#3b82f6',
      '기획': '#f59e0b',
      '디자인': '#f97316',
      '테스트': '#10b981',
      '운영': '#8b5cf6',
      '기타': '#94a3b8',
    }
    const PROGRESS_BANDS = [
      { label: '완료 (100%)',      min: 100, max: 100, color: '#10b981' },
      { label: '진행중 (50~99%)', min: 50,  max: 99,  color: '#3b82f6' },
      { label: '시작 (1~49%)',    min: 1,   max: 49,  color: '#f59e0b' },
      { label: '미시작 (0%)',     min: 0,   max: 0,   color: '#e5e7eb' },
    ]

    /* ── fetch ── */
    const fetchData = async () => {
      loading.value = true
      try {
        const { data } = await api.get('/taskReport')

        allTasks.value = data.map(r => ({
          taskId: r.taskId,
          title: r.title ?? '-',
          projectId: String(r.projectId ?? ''),
          projectName: r.projectName ?? '-',
          typeName: r.typeName ?? '기타',
          assignee: r.assigneeName ?? r.userName ?? '-',
          progressRate: Number(r.progressRate ?? 0),
          startDate: r.startDate ?? null,
          dueDate: r.dueDate ?? null,
          status: r.taskStatusId ?? '-',
        }))

        // reportList에서 projectList dedupe 추출
        const seen = new Set()
        projectList.value = data
          .filter(r => {
            if (!r.projectId || seen.has(r.projectId)) return false
            seen.add(r.projectId)
            return true
          })
          .map(r => ({ projectId: String(r.projectId), projectName: r.projectName }))

        // route에서 넘어온 projectId가 있으면 초기 선택
        if (projectId) {
          selectedProject.value = String(projectId)
        }

      } catch (e) {
        console.error('업무 통계 조회 실패', e)
        allTasks.value = []
        projectList.value = []
      } finally {
        loading.value = false
      }
    }

    /* ── computed ── */
    const projectOptions = computed(() =>
      projectList.value.map(p => ({ value: String(p.projectId), label: p.projectName }))
    )
    const selectedProjectLabel = computed(() => {
      if (!selectedProject.value) return '전체 프로젝트'
      return projectOptions.value.find(p => p.value === selectedProject.value)?.label ?? '전체'
    })

    const periodLabel = computed(() => {
      if (filterStart.value && filterEnd.value)
        return `${filterStart.value} ~ ${filterEnd.value}`
      return '전체 기간'
    })

    const filteredTasks = computed(() => {
      let list = allTasks.value
      if (selectedProject.value) list = list.filter(t => t.projectId === selectedProject.value)
      if (filterStart.value) list = list.filter(t => t.dueDate && t.dueDate >= filterStart.value)
      if (filterEnd.value) list = list.filter(t => t.dueDate && t.dueDate <= filterEnd.value)
      return list
    })

    const uniqueAssignees = computed(() => new Set(filteredTasks.value.map(t => t.assignee)).size)
    const completedCount = computed(() => filteredTasks.value.filter(t => t.progressRate === 100).length)
    const avgProgress = computed(() => {
      if (!filteredTasks.value.length) return 0
      const sum = filteredTasks.value.reduce((s, t) => s + t.progressRate, 0)
      return Math.round(sum / filteredTasks.value.length)
    })

    /* 진척도 밴드 */
    const progressBands = computed(() =>
      PROGRESS_BANDS.map(b => ({
        ...b,
        count: filteredTasks.value.filter(t =>
          b.min === b.max ? t.progressRate === b.min : t.progressRate >= b.min && t.progressRate <= b.max
        ).length,
      }))
    )

    /* 유형별 */
    const typeCountMap = computed(() => {
      const map = {}
      filteredTasks.value.forEach(t => { map[t.typeName] = (map[t.typeName] || 0) + 1 })
      return Object.entries(map).sort((a, b) => b[1] - a[1])
    })
    const typeDonutData = computed(() =>
      typeCountMap.value.map(([label, val]) => ({ label, val, color: TYPE_COLORS[label] || '#94a3b8' }))
    )

    /* 담당자별 */
    const allAssigneeCounts = computed(() => {
      const map = {}
      filteredTasks.value.forEach(t => { map[t.assignee] = (map[t.assignee] || 0) + 1 })
      return Object.entries(map).sort((a, b) => b[1] - a[1])
    })
    const aTotalPages = computed(() => Math.max(1, Math.ceil(allAssigneeCounts.value.length / HB_PER)))
    const pagedAssignee = computed(() => {
      const s = (aPage.value - 1) * HB_PER
      return allAssigneeCounts.value.slice(s, s + HB_PER)
    })

    /* 날짜별 */
    const dateCounts = computed(() => {
      const map = {}
      filteredTasks.value.forEach(t => {
        const d = t.dueDate ? String(t.dueDate).substring(0, 10) : null
        if (d) map[d] = (map[d] || 0) + 1
      })
      return Object.entries(map).sort((a, b) => a[0].localeCompare(b[0]))
    })

    /* 프로젝트별 평균 진척도 */
    const projectProgressData = computed(() => {
      const map = {}
      allTasks.value.forEach(t => {
        if (!map[t.projectName]) map[t.projectName] = []
        map[t.projectName].push(t.progressRate)
      })
      return Object.entries(map).map(([name, arr]) => ({
        name,
        avg: Math.round(arr.reduce((s, v) => s + v, 0) / arr.length),
      })).sort((a, b) => b.avg - a.avg)
    })

    /* KPI 카드 */
    const kpiCards = computed(() => [
      {
        label: '전체 업무',
        value: filteredTasks.value.length + '건',
        sub: '조회 결과',
        bg: '#eff6ff',
        trendUp: true,
        icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="7" rx="1.5" fill="#3b82f6"/><rect x="14" y="3" width="7" height="7" rx="1.5" fill="#3b82f6"/><rect x="3" y="14" width="7" height="7" rx="1.5" fill="#3b82f6"/><rect x="14" y="14" width="7" height="7" rx="1.5" fill="#3b82f6"/></svg>',
      },
      {
        label: '완료 업무',
        value: completedCount.value + '건',
        sub: Math.round(completedCount.value / (filteredTasks.value.length || 1) * 100) + '% 완료율',
        bg: '#f0fdf4',
        trendUp: true,
        icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" fill="#10b981"/><path d="M8 12l3 3 5-5" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
      },
      {
        label: '평균 진척도',
        value: avgProgress.value + '%',
        sub: '전체 업무 기준',
        bg: '#fef3c7',
        trendUp: avgProgress.value >= 50,
        icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M3 17l4-4 4 4 4-6 4-4" stroke="#f59e0b" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
      },
      {
        label: '참여 담당자',
        value: uniqueAssignees.value + '명',
        sub: '활성 담당자',
        bg: '#faf5ff',
        trendUp: true,
        icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><circle cx="9" cy="7" r="4" fill="#8b5cf6"/><path d="M3 21v-2a4 4 0 014-4h4a4 4 0 014 4v2" stroke="#8b5cf6" stroke-width="2" stroke-linecap="round"/><path d="M16 3c1.66 0 3 1.34 3 3s-1.34 3-3 3" stroke="#8b5cf6" stroke-width="1.8" stroke-linecap="round"/><path d="M21 21v-2a4 4 0 00-3-3.87" stroke="#8b5cf6" stroke-width="1.8" stroke-linecap="round"/></svg>',
      },
    ])

    const statusStats = computed(() => [
      { label: '미시작', value: filteredTasks.value.filter(t => t.progressRate === 0).length + '건', color: '#94a3b8' },
      { label: '진행중', value: filteredTasks.value.filter(t => t.progressRate > 0 && t.progressRate < 100).length + '건', color: '#3b82f6' },
      { label: '완료', value: completedCount.value + '건', color: '#10b981' },
      { label: '지연', value: filteredTasks.value.filter(t => t.dueDate && String(t.dueDate).substring(0, 10) < new Date().toISOString().substring(0, 10) && t.progressRate < 100).length + '건', color: '#ef4444' },
    ])

    const progressColor = (pct) => {
      if (pct >= 80) return '#10b981'
      if (pct >= 50) return '#3b82f6'
      if (pct >= 20) return '#f59e0b'
      return '#ef4444'
    }

    /* ── chart instances ── */
    let ci = {}
    const destroy = (k) => { if (ci[k]) { ci[k].destroy(); delete ci[k] } }

    const CD = { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
    const AX = { ticks: { color: '#94a3b8', font: { size: 10 } }, grid: { color: 'rgba(0,0,0,0.05)' }, border: { display: false } }

    const buildProgressDonut = () => {
      destroy('pdnt')
      if (!progressDonut.value) return
      const data = progressBands.value
      ci.pdnt = new Chart(progressDonut.value, {
        type: 'doughnut',
        data: {
          labels: data.map(d => d.label),
          datasets: [{ data: data.map(d => d.count), backgroundColor: data.map(d => d.color), borderWidth: 2, borderColor: '#fff' }],
        },
        options: { ...CD, cutout: '60%', plugins: { legend: { display: false }, tooltip: { callbacks: { label: (c) => ` ${c.label}: ${c.parsed}건` } } } },
      })
    }

    const buildTypeBar = () => {
      destroy('tbar')
      if (!typeBar.value) return
      const data = typeCountMap.value
      ci.tbar = new Chart(typeBar.value, {
        type: 'bar',
        data: {
          labels: data.map(([t]) => t),
          datasets: [{ data: data.map(([, c]) => c), backgroundColor: data.map(([t]) => TYPE_COLORS[t] || '#94a3b8'), borderRadius: 4, barThickness: 30 }],
        },
        options: { ...CD, scales: { x: AX, y: { ...AX, min: 0, ticks: { ...AX.ticks, callback: v => v + '건' } } } },
      })
    }

    const buildAssigneeHBar = () => {
      destroy('ahbar')
      if (!assigneeHBar.value) return
      const data = pagedAssignee.value
      ci.ahbar = new Chart(assigneeHBar.value, {
        type: 'bar',
        data: {
          labels: data.map(([a]) => a),
          datasets: [{ data: data.map(([, c]) => c), backgroundColor: '#6366f1', borderRadius: 3, barThickness: 16 }],
        },
        options: { ...CD, indexAxis: 'y', scales: { x: { ...AX, min: 0, ticks: { ...AX.ticks, callback: v => v + '건' } }, y: { ...AX, grid: { display: false } } } },
      })
    }

    const buildDateLine = () => {
      destroy('dline')
      if (!dateLine.value) return
      const data = dateCounts.value
      ci.dline = new Chart(dateLine.value, {
        type: 'line',
        data: {
          labels: data.map(([d]) => d),
          datasets: [{
            data: data.map(([, c]) => c),
            borderColor: '#6366f1',
            backgroundColor: 'rgba(99,102,241,0.08)',
            pointBackgroundColor: '#6366f1',
            pointRadius: 4,
            tension: 0.3,
            fill: true,
          }],
        },
        options: { ...CD, scales: { x: AX, y: { ...AX, min: 0, ticks: { ...AX.ticks, callback: v => v + '건' } } } },
      })
    }

    const buildProjectProgressBar = () => {
      destroy('ppbar')
      if (!projectProgressBar.value) return
      const data = projectProgressData.value
      ci.ppbar = new Chart(projectProgressBar.value, {
        type: 'bar',
        data: {
          labels: data.map(d => d.name),
          datasets: [{
            label: '평균 진척도',
            data: data.map(d => d.avg),
            backgroundColor: data.map(d => {
              const p = d.avg
              if (p >= 80) return 'rgba(16,185,129,0.8)'
              if (p >= 50) return 'rgba(59,130,246,0.8)'
              return 'rgba(245,158,11,0.8)'
            }),
            borderRadius: 4,
            barThickness: 32,
          }],
        },
        options: {
          ...CD,
          scales: {
            x: AX,
            y: { ...AX, min: 0, max: 100, ticks: { ...AX.ticks, callback: v => v + '%' } },
          },
          plugins: { ...CD.plugins, tooltip: { callbacks: { label: c => ` 평균 ${c.parsed.y}%` } } },
        },
      })
    }

    const buildTypeDonut = () => {
      destroy('tdnt')
      if (!typeDonut.value) return
      const data = typeDonutData.value
      ci.tdnt = new Chart(typeDonut.value, {
        type: 'doughnut',
        data: {
          labels: data.map(d => d.label),
          datasets: [{ data: data.map(d => d.val), backgroundColor: data.map(d => d.color), borderWidth: 2, borderColor: '#fff' }],
        },
        options: { ...CD, cutout: '58%', plugins: { legend: { display: false }, tooltip: { callbacks: { label: c => ` ${c.label}: ${c.parsed}건` } } } },
      })
    }

    const rebuildAll = () => nextTick(() => {
      buildProgressDonut()
      buildTypeBar()
      buildAssigneeHBar()
      buildDateLine()
      buildProjectProgressBar()
      buildTypeDonut()
    })

    /* ── watchers ── */
    watch(filteredTasks, () => { aPage.value = 1; rebuildAll() })
    watch(aPage, () => nextTick(buildAssigneeHBar))

    /* ── lifecycle ── */
    onMounted(async () => {
      await fetchData()
      rebuildAll()
    })

    const onProjectChange = () => { aPage.value = 1 }
    const applyFilter = () => { aPage.value = 1 }
    const goBack = () => router.push({ name: 'TaskReport', params: { projectId } })

    return {
      progressDonut, typeBar, assigneeHBar, dateLine, projectProgressBar, typeDonut,
      sidebarOpen, loading, selectedProject, selectedProjectLabel, filterStart, filterEnd,
      periodLabel, projectOptions, filteredTasks, allTasks,
      uniqueAssignees, completedCount, avgProgress,
      progressBands, typeDonutData, pagedAssignee, aTotalPages, aPage,
      kpiCards, statusStats, progressColor,
      onProjectChange, applyFilter, goBack,
    }
  },
})
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.page { color: #0f172a; font-family: 'Pretendard', 'Noto Sans KR', sans-serif; }

/* ── 로딩 ── */
.loading-wrap {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 12px; padding: 80px 0; color: #64748b; font-size: 14px;
}
.loading-spinner {
  width: 28px; height: 28px; border: 3px solid #e2e8f0;
  border-top-color: #3b82f6; border-radius: 50%; animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── 데이터 없음 ── */
.empty-wrap {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 12px; padding: 80px 0; color: #94a3b8; font-size: 14px;
}

/* ── 서브헤더 ── */
.sub-header {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; padding: 12px 32px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky; top: 0; z-index: 30;
}
.btn-back {
  display: inline-flex; align-items: center; gap: 5px;
  height: 30px; padding: 0 12px; font-size: 13px; font-weight: 600;
  background: #fff; color: #334155; border: 1px solid #e2e8f0;
  border-radius: 6px; cursor: pointer; white-space: nowrap; transition: all 0.15s;
}
.btn-back:hover { background: #f1f5f9; border-color: #94a3b8; color: #0f172a; }
.breadcrumb { display: flex; align-items: center; gap: 8px; font-size: 14px; color: #475569; }
.bc-sep { color: #cbd5e1; }
.bc-cur { color: #0f172a; font-weight: 700; }

/* ── 페이지 내부 ── */
.page-inner { padding: 20px 28px 32px; display: flex; flex-direction: column; gap: 14px; }

/* ── 프로젝트 헤더 바 ── */
.proj-header-bar {
  display: flex; align-items: center; justify-content: space-between;
  background: #fff; border: 1px solid #e2e8f0; border-radius: 6px; padding: 12px 18px;
}
.proj-info { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.proj-label { font-size: 13px; font-weight: 700; color: #334155; white-space: nowrap; }
.select-wrap { position: relative; }
.select-wrap select {
  appearance: none; padding: 6px 28px 6px 10px; font-size: 13px; font-weight: 500;
  color: #1e293b; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 4px;
  cursor: pointer; outline: none; min-width: 160px; transition: border-color 0.15s;
}
.select-wrap select:focus { border-color: #3b82f6; background: #fff; }
.select-arrow { position: absolute; right: 9px; top: 50%; transform: translateY(-50%); color: #64748b; font-size: 10px; pointer-events: none; }
.date-input {
  padding: 6px 10px; font-size: 13px; border: 1px solid #e2e8f0;
  border-radius: 4px; outline: none; background: #f8fafc; color: #1e293b; transition: border-color 0.15s;
}
.date-input:focus { border-color: #3b82f6; background: #fff; }
.date-sep { font-size: 13px; color: #64748b; }
.btn-apply {
  padding: 6px 16px; font-size: 13px; font-weight: 600;
  background: #334155; color: #fff; border: none; border-radius: 4px; cursor: pointer; transition: background 0.15s;
}
.btn-apply:hover { background: #1e293b; }
.period-wrap { display: flex; align-items: center; gap: 6px; }
.period-text { font-size: 14px; color: #1e293b; font-weight: 600; }

/* ── KPI 카드 ── */
.kpi-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.kpi-card {
  background: #fff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px 16px;
  display: flex; align-items: center; gap: 12px; transition: box-shadow 0.15s;
}
.kpi-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.06); }
.kpi-icon { width: 38px; height: 38px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.kpi-body { flex: 1; }
.kpi-val { font-size: 20px; font-weight: 800; color: #0f172a; letter-spacing: -0.3px; }
.kpi-lbl { font-size: 12px; color: #64748b; margin-top: 1px; }
.kpi-trend { font-size: 11px; font-weight: 600; white-space: nowrap; }
.kpi-trend.up { color: #10b981; }
.kpi-trend.neutral { color: #64748b; }

/* ── 대시보드 그리드 ── */
.dashboard-grid { display: grid; grid-template-columns: 1fr 280px; gap: 14px; align-items: start; }
.charts-left { display: flex; flex-direction: column; gap: 14px; }
.chart-row { display: grid; grid-template-columns: 1fr 1fr; gap: 14px; }

.chart-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 6px; padding: 14px 16px; }
.chart-card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 10px; }
.chart-card-title { font-size: 14px; font-weight: 700; color: #0f172a; }
.chart-unit { font-size: 12px; color: #64748b; }
.chart-canvas-wrap { position: relative; height: 150px; }
.chart-canvas-wrap canvas { width: 100% !important; }

.chart-legend { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 8px; }
.legend-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #475569; }
.legend-sq { width: 8px; height: 8px; border-radius: 1px; flex-shrink: 0; }

.hbar-footer { display: flex; align-items: center; justify-content: space-between; margin-top: 8px; padding-top: 6px; border-top: 1px solid #f1f5f9; }
.hbar-count { font-size: 12px; color: #64748b; }
.mini-pager { display: flex; gap: 3px; }
.mpg {
  width: 20px; height: 20px; display: flex; align-items: center; justify-content: center;
  font-size: 12px; border: 1px solid #e2e8f0; border-radius: 3px; background: #fff; color: #475569; cursor: pointer;
}
.mpg:hover { background: #f1f5f9; }
.mpg.on { background: #1e40af; color: #fff; border-color: #1e40af; }

/* ── 오른쪽 ── */
.charts-right { display: flex; flex-direction: column; gap: 10px; }
.donut-card { display: flex; flex-direction: column; }
.donut-canvas-wrap { position: relative; height: 170px; margin: 4px 0; }
.donut-center { position: absolute; inset: 0; display: flex; flex-direction: column; align-items: center; justify-content: center; pointer-events: none; }
.donut-total { font-size: 20px; font-weight: 800; color: #0f172a; }
.donut-total-lbl { font-size: 12px; color: #64748b; }
.donut-legend { display: flex; flex-wrap: wrap; justify-content: center; gap: 8px; margin-top: 8px; }
.donut-leg-item { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #475569; }

.stat-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
.stat-card { background: #fff; border: 1px solid #e2e8f0; border-radius: 6px; padding: 12px 14px; text-align: center; }
.stat-val { font-size: 20px; font-weight: 800; letter-spacing: -0.3px; }
.stat-lbl { font-size: 12px; color: #475569; margin-top: 2px; }

/* ── 게이지 ── */
.gauge-title { font-size: 13px; font-weight: 700; color: #0f172a; margin-bottom: 10px; }
.gauge-wrap { display: flex; align-items: center; gap: 10px; }
.gauge-bar-bg { flex: 1; height: 10px; background: #f1f5f9; border-radius: 999px; overflow: hidden; }
.gauge-bar-fill { height: 100%; border-radius: 999px; transition: width 0.6s ease; }
.gauge-pct { font-size: 14px; font-weight: 800; color: #0f172a; white-space: nowrap; }
.gauge-sub { font-size: 12px; color: #64748b; margin-top: 6px; }
</style>