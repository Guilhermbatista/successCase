package com.version.demov2.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.version.demov2.Entities.User;
import com.version.demov2.Services.Exceptions.ForbiddenException;

@Service
public class AuthService {

	@Autowired
	private UserService userService;

	public void validateSerlfOrAdmin(Long userId) {
		User me = userService.authenticated();
		if (me.hasRole("ROLE_ADMIN")) {
			return;
		}
		if (!me.getId().equals(userId)) {
			throw new ForbiddenException("Access denied. Should be self or admin");
		}
	}

}
