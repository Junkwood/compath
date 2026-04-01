import { defineStore } from "pinia";

export const useGanttChartStore = defineStore("ganttChart", {
  state: () => ({
    rawTasks: [],
    rawProjects: [],
    tasksData: [],
  }),

  actions: {
    async fetchGanttData(projectId) {
      const [taskRes, projectRes] = await Promise.all([
        fetch("/api/task/gantt"),
        fetch("/api/projectList"),
      ]);
      this.rawTasks = await taskRes.json();
      this.rawProjects = await projectRes.json();

      this.buildTree(projectId ? Number(projectId) : null);
    },

    //프로젝트
    buildTree(projectId) {
      const rootProjects = projectId
        ? this.rawProjects.filter((p) => Number(p.projectId) === projectId)
        : this.rawProjects.filter((p) => !p.parentProjectId);

      this.tasksData = rootProjects.map((root) => {
        const rootId = Number(root.projectId);

        const subProjects = this.rawProjects.filter(
          (p) => Number(p.parentProjectId) === rootId,
        );

        //하위 프로젝트
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

    //업무
    taskToNode(task) {
      return {
        id: task.taskId,
        name: task.title,
        startDate: task.estStartDate ?? task.startDate,
        endDate: task.estEndDate ?? task.dueDate,
        assignee: task.assigneeName,
        priority: task.priorityCode,
        percentDone: task.progressRate ?? 0,
      };
    },
  },
});
