import { defineStore } from "pinia";
import axios from "axios";

export const useGanttChartStore = defineStore("ganttChart", {
  state: () => ({
    rawTasks: [],
    rawProjects: [],
    tasksData: [],
    priorityList: [],
    filteredTasksData: null,
  }),
  getters: {
    // 필터 적용된 데이터
    displayData: (state) => state.filteredTasksData ?? state.tasksData,
  },

  actions: {
    async fetchGanttData(projectId) {
      try {
        const [taskRes, projectRes, codeRes] = await Promise.all([
          fetch(`/api/task/gantt?projectId=${projectId}`),
          fetch(`/api/task-total-info?projectId=${projectId}`),
          axios.get("/api/code", { params: { groupValue: ["0H"] } }),
        ]);

        const tasks = await taskRes.json();
        const totalInfo = await projectRes.json();
        //공통 코드 저장
        this.priorityList = codeRes.data.c0H || [];
        this.rawTasks = tasks || [];
        this.rawProjects = totalInfo.projectList || [];
        // 트리 구조 생성
        this.buildTree(projectId ? Number(projectId) : null);
      } catch (error) {
        console.error(error);
      }
    },

    buildTree(projectId) {
      if (!this.rawProjects || !Array.isArray(this.rawProjects)) return;

      const rootProjects = projectId
        ? this.rawProjects.filter(
            (p) => Number(p.projectId) === Number(projectId),
          )
        : this.rawProjects.filter((p) => !p.parentProjectId);
      this.tasksData = rootProjects.map((root) => {
        const rootId = Number(root.projectId);

        const subProjects = this.rawProjects.filter(
          (p) => Number(p.parentProjectId) === rootId,
        );
        const countNodes = (nodes) => {
          return nodes.reduce(
            (acc, node) =>
              acc + 1 + (node.children ? countNodes(node.children) : 0),
            0,
          );
        };
        const subChildren = subProjects.map((sub) => {
          const subId = Number(sub.projectId);
          const subTasks = this.rawTasks
            .filter((t) => Number(t.projectId) === subId)
            .map((task) => this.taskToNode(task));

          return {
            id: `p_${subId}`,
            name: sub.projectName,
            expanded: true,
            children: subTasks,
          };
        });

        const rootTasks = this.rawTasks
          .filter((t) => Number(t.projectId) === rootId)
          .map((task) => this.taskToNode(task));
        return {
          id: `root_${rootId}`,
          name: root.projectName,
          expanded: true,
          children: [...subChildren, ...rootTasks],
        };
      });
    },
    filterBySubProject(subProjectId) {
      const numId = Number(subProjectId);
      const tasks = this.rawTasks
        .filter((t) => Number(t.projectId) === numId)
        .map((task) => this.taskToNode(task));

      const project = this.rawProjects.find(
        (p) => Number(p.projectId) === numId,
      );

      this.filteredTasksData = [
        {
          id: `p_${numId}`,
          name: project?.projectName ?? "하위 프로젝트",
          expanded: true,
          children: tasks,
        },
      ];
    },

    taskToNode(task) {
      const priorityObj = this.priorityList.find(
        (p) => p.codeValue === task.priorityCode,
      );

      const priorityName = priorityObj
        ? priorityObj.codeName
        : task.priorityCode;
      return {
        id: task.taskId,
        name: task.title,
        startDate: task.estStartDate ?? task.startDate,
        endDate: task.estEndDate ?? task.dueDate,
        assignee: task.assigneeName || "미지정",
        priority: priorityName,
        priorityCode: task.priorityCode,
        percentDone: task.progressRate ?? 0,
      };
    },
  },
});
