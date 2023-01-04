package com.mtx.activiti;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.io.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

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
                .taskAssignee("赵六")
                .list();

        for (Task task : taskList) {
            System.out.println("流程实例id：" + task.getProcessInstanceId());
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());

            // 获取任务之后并且完成任务
            taskService.complete(task.getId());
        }

    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask() {

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        taskService.complete("2505");

    }

    @Test
    public void deployProcessByZip() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        InputStream is = this.getClass()
                .getClassLoader()
                .getResourceAsStream("bpmn/evection.zip");

        ZipInputStream zipInputStream = new ZipInputStream(is);
        // 使用压缩包进行流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    /**
     * 查询流程定义
     * act_re_procdef
     */
    @Test
    public void queryProcessDefinition() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        // 返回流程定义信息集合
        List<ProcessDefinition> evectionList = processDefinitionQuery.processDefinitionKey("evection")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        for (ProcessDefinition processDefinition : evectionList) {
            System.out.println("流程定义id：" + processDefinition.getId());
            System.out.println("流程定义key：" + processDefinition.getKey());
            System.out.println("流程定义名称：" + processDefinition.getName());
            System.out.println("流程定义版本：" + processDefinition.getVersion());
            System.out.println("流程部署id：" + processDefinition.getDeploymentId());
        }

    }

    /**
     * 删除流程
     * 如果流程没有完成，就是当前如果还有运行中任务，则无法删除，只能通过级联删除的形式
     */
    @Test
    public void deleteDeploy() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        repositoryService.deleteDeployment("1");

    }

    /**
     * 下载资源文件
     * 1.使用activiti的api来下载文件
     * 2.自己写代码下载
     */
    @Test
    public void getDeployment() throws IOException {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("evection")
                .singleResult();

        String deploymentId = processDefinition.getDeploymentId();

        // 获取png名称
        String pngName = processDefinition.getDiagramResourceName();
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, pngName);
        // 获取bpmn文件名称
        String bpmnName = processDefinition.getResourceName();
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, bpmnName);

        File pngFile = new File("evectionDown.png");
        File bpmnFile = new File("evectionDown.bpmn");

        FileOutputStream pngOutput = new FileOutputStream(pngFile);
        FileOutputStream bpmnOutput = new FileOutputStream(bpmnFile);

        IOUtils.copy(pngInput, pngOutput);
        IOUtils.copy(bpmnInput, bpmnOutput);

        pngOutput.close();
        bpmnOutput.close();
        pngInput.close();
        bpmnInput.close();

    }

    /**
     * 获取历史信息
     */
    @Test
    public void getHistory() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();

        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();

        // 通过运行实例id查询
//        historicActivityInstanceQuery.processInstanceId("2501");

        // 通过流程定义id查询
//        historicActivityInstanceQuery.processDefinitionId("evection:1:4");

        historicActivityInstanceQuery.executionId("15002");

        List<HistoricActivityInstance> activityInstances = historicActivityInstanceQuery.list();

        for (HistoricActivityInstance activityInstance : activityInstances) {
            System.out.println(activityInstance.getActivityId());
            System.out.println(activityInstance.getActivityName());
            System.out.println(activityInstance.getProcessInstanceId());
            System.out.println(activityInstance.getProcessDefinitionId());
        }

    }

    /**
     * 全部流程挂起
     * 流程定义，当前运行流程实例，当前任务全部挂起
     */
    @Test
    public void suspendAllProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        //通过流程定义key查询流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("evecion")
                .singleResult();
        // 获取流程是否已经挂起
        boolean suspended = processDefinition.isSuspended();
        String definitionId = processDefinition.getId();
        if (suspended) {
            // 如果已经挂起，就激活流程
            repositoryService.activateProcessDefinitionById(definitionId, true, null);
        } else {
            repositoryService.suspendProcessDefinitionById(definitionId, true, null);
        }

    }

    /**
     * 单笔实例挂起
     * 当前流程实例，流程任务挂起
     */
    @Test
    public void suspendProcessInstance(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("evecion")
                .list();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("15001")
                .singleResult();

        if (processInstance.isSuspended()){
            runtimeService.activateProcessInstanceById(processInstance.getId());
        }else{
            runtimeService.suspendProcessInstanceById(processInstance.getId());
        }

    }

}
