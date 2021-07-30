package com.camunda.vacation_application.vacation_application;

import java.time.*;
import java.util.*;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.camunda.spin.Spin;

public class ApplicantInformationDelegate implements JavaDelegate 
{

	@Override
	public void execute(DelegateExecution execution) throws Exception 
	{
		IdentityService identityService = execution.getProcessEngine().getIdentityService();			
		
		User applicant = identityService.createUserQuery().userId(
				identityService.getCurrentAuthentication().getUserId()).singleResult();
		
		execution.setVariable("applicant", applicant.getId());
		execution.setVariable("applicantName", applicant.getFirstName());	
		execution.setVariable("applicantSurname", applicant.getLastName());
		
		Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		execution.setVariable("vacationStart", new Date(today.getTime() + (1000 * 60 * 60 * 24)));
		execution.setVariable("vacationEnd", new Date(today.getTime() + 7 * (1000 * 60 * 60 * 24)));
		
		List<User> userList = identityService.createUserQuery().list();
		Map<String, String> usersMap = new HashMap<String, String>();
		for(int i=0; i<userList.size(); i++)
		{
			User user = userList.get(i);
			if(user.getId() != applicant.getId())
			{
				usersMap.put(user.getId(), user.getFirstName() + " " + user.getLastName());
			}
		}		
		execution.setVariable("usersMap", Spin.JSON(usersMap));  		
	}
}
