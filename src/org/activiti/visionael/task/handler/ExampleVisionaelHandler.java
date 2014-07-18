package org.activiti.visionael.task.handler;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.ConfigurationException;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.explorer.ExplorerApp;

import com.vaadin.terminal.ExternalResource;
import com.visionael.api.GuestApiFactory;
import com.visionael.api.project.ProjectApiRemote;
import com.visionael.api.project.dto.DetachedProject;
import com.visionael.api.project.dto.DetachedTask;
import com.visionael.api.vnd.exception.VndPersistenceException;
import com.visionael.api.vnd.query.FindResult;
import com.visionael.api.vnd.query.Query;

public class ExampleVisionaelHandler extends AbstractVisionaelPerformHandler {

	@Override
	public void handle(String taskId) {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		TaskEntity taskEntity = (TaskEntity) processEngine.getTaskService()
				.createTaskQuery().taskId(taskId).list().iterator().next();
		String processInstanceId = taskEntity.getProcessInstanceId();
		if (processInstanceId != null) {
			List<ProcessInstance> processInstances = processEngine
					.getRuntimeService().createProcessInstanceQuery()
					.processInstanceId(processInstanceId)
					.includeProcessVariables().list();
			ProcessInstance processInstance = processInstances.iterator()
					.next();
			Map<String, Object> processVariables = processInstance
					.getProcessVariables();
			for (Entry<String, Object> variable : processVariables.entrySet()) {
				System.out.println(variable.getKey());
				System.out.println(variable.getValue());
			}

			GuestApiFactory apiFactory;
			try {
				apiFactory = new GuestApiFactory("localhost", 3700);
				ProjectApiRemote projectApi = apiFactory.getProjectApi();
				Query qTask = Query.find(DetachedTask.class).matching("taskId",taskId);
				DetachedTask nrmTask = (DetachedTask) projectApi.find(qTask).getFirst();
				if (nrmTask == null) {
					Query qProject = Query.find(DetachedProject.class).matching("name", "activiti_tasks");
					FindResult result = projectApi.find(qProject);
					DetachedProject project = (DetachedProject) result.getFirst();
					nrmTask = projectApi.createTask(project, taskEntity.getName()+"-activiti", taskId);
				}
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} catch (VndPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String prefix = "http://10.3.3.123:8080/nrm/vfd/MainPage.iface?None/Graphic/HierarchyTab/";
		com.vaadin.terminal.Resource link = new ExternalResource(prefix + "CartTaskTab:task="+taskId);
		ExplorerApp.get().getWindows().iterator().next().open(link, "_newTab");

	}
}
