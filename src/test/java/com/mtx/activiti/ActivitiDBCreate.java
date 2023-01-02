package com.mtx.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.junit.jupiter.api.Test;

/**
 * 创建actitivi支持表
 */
public class ActivitiDBCreate {

    /**
     * 使用actitivi提供的默认方式创建mysql表
     */
    @Test
    public void createDB(){
        // 使用activiti提供的processEngines工具类创建，使用方法 getDefaultProcessEngine
        // getDefaultProcessEngine默认读取activiti.cfg.xml,并且创建基础表
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        repositoryService.
        System.out.println(processEngine);
    }

}
