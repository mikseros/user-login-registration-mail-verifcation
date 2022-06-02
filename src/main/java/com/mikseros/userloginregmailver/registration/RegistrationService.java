package com.mikseros.userloginregmailver.registration;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikseros.userloginregmailver.appuser.AppUser;
import com.mikseros.userloginregmailver.appuser.AppUserRole;
import com.mikseros.userloginregmailver.appuser.AppUserService;
import com.mikseros.userloginregmailver.registration.token.ConfirmationToken;
import com.mikseros.userloginregmailver.registration.token.ConfirmationTokenService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {

	private final AppUserService appUserService;
	private final EmailValidator emailValidator;
	private final ConfirmationTokenService confirmationTokenService;
	
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
	
	@Transactional
	public String confirmToken(String token) {
		ConfirmationToken confirmationToken = confirmationTokenService
				.getToken(token)
				.orElseThrow(() ->
						new IllegalStateException("Token Not Found"));
		if (confirmationToken.getConfirmedAt() != null) {
			throw new IllegalStateException("Email already confirmed");
		}
		
		LocalDateTime expiredAt = confirmationToken.getExpiresAt();
		
		if (expiredAt.isBefore(LocalDateTime.now())) {
			throw new IllegalStateException("Token Expired");
		}
		
		confirmationTokenService.setConfirmedAt(token);
		appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
		return "Confirmed";
	}

}
