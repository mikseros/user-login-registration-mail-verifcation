package com.mikseros.userloginregmailver.registration;

import org.springframework.stereotype.Service;

import com.mikseros.userloginregmailver.appuser.AppUser;
import com.mikseros.userloginregmailver.appuser.AppUserRole;
import com.mikseros.userloginregmailver.appuser.AppUserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {

	private final AppUserService appUserService;
	private final EmailValidator emailValidator;
	
	public String register(RegistrationRequest request) {
		boolean isValidEmail = emailValidator.
				test(request.getEmail());
		if (!isValidEmail) {
			throw new IllegalStateException("Email not valid");
		}
		
		return appUserService.signUpUser(
				new AppUser(
					request.getFirstName(),
					request.getLastName(),
					request.getEmail(),
					request.getPassword(),
					AppUserRole.USER
				)
		);
	}

}
