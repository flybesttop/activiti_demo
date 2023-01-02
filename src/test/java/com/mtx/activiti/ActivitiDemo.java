package com.mtx.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ActivitiDemo {

    /**
     * 测试部署
     */
    @Test
    public void testDeployment() {
        //1.创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.使用service进行流程部署
        Deployment deployment = repositoryService.createDeployment()
                .name("出差申请流程")
                .addClasspathResource("bpmn/evection.bpmn20.xml")
                .addClasspathResource("bpmn/evection.png")
                .deploy();

        System.out.println("流程id：" + deployment.getId());
        System.out.println("流程名称：" + deployment.getName());

    }

    /**
     * 测试启动流程实例
     */
    @Test
    public void testStart() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("evection");

        System.out.println("流程定义ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID：" + processInstance.getId());
        System.out.println("当前活动ID：" + processInstance.getActivityId());
    }

    /**
     * 查询用户当前任务
     */
    @Test
    public void findPersonalTaskList() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        // 根据流程key和任务负责人查对应任务

        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("evection")
                .taskAssignee("张三")
                .list();

        for (Task task : taskList) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        taskService.complete("2505");

    }

}
