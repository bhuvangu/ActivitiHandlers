package org.activiti.visionael.task.handler;

import javax.naming.ConfigurationException;

import org.activiti.explorer.ui.task.listener.TaskPerformHandlerDetail;
import org.activiti.explorer.ui.task.listener.VisionaelTaskPerformHandler;

import com.visionael.api.GuestApiFactory;
import com.visionael.api.project.ProjectApiRemote;
import com.visionael.api.project.dto.DetachedTask;
import com.visionael.api.project.dto.TaskStatus;
import com.visionael.api.vnd.exception.VndPersistenceException;
import com.visionael.api.vnd.query.Query;


public abstract class AbstractVisionaelPerformHandler implements VisionaelTaskPerformHandler{

	TaskPerformHandlerDetail taskPerformHandlerDetail = null;
	
	@Override
	public void complete(String taskId) {
		GuestApiFactory apiFactory;
		try {
			apiFactory = new GuestApiFactory("localhost", 3700);
			ProjectApiRemote projectApi = apiFactory.getProjectApi();
			Query qTask = Query.find(DetachedTask.class).matching("taskId",taskId);
			DetachedTask nrmTask = (DetachedTask) projectApi.find(qTask).getFirst();
			if (nrmTask != null) {
				nrmTask.setStatus(TaskStatus.FINISHED);
				projectApi.updateTask(nrmTask);
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (VndPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void setDetails(TaskPerformHandlerDetail taskPerformHandlerDetail) {
		this.taskPerformHandlerDetail = taskPerformHandlerDetail;
		
	}

	@Override
	public TaskPerformHandlerDetail getDetails() {
		return taskPerformHandlerDetail;
	}

}
