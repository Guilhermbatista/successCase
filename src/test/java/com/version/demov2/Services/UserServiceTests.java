package com.version.demov2.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.version.demov2.DTO.UserDTO;
import com.version.demov2.Entities.User;
import com.version.demov2.Projections.UserDetailsProjection;
import com.version.demov2.Repositories.UserRepository;
import com.version.demov2.Tests.UserDetailsFactory;
import com.version.demov2.Tests.UserFactory;
import com.version.demov2.Util.CustomUserUtil;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil userUtil;

	private String existingUserName, nonExistingUserName;
	private User user;

	private List<UserDetailsProjection> userDetails;

	@BeforeEach
	private void SetUp() throws Exception {

		existingUserName = "alex@gmail.com";
		nonExistingUserName = "user@gmail.com";

		user = UserFactory.createCustomClientUser(2L, existingUserName);

		userDetails = UserDetailsFactory.createCustomAdminUser(existingUserName);

		Mockito.when(repository.searchUserAndRolesByEmail(existingUserName)).thenReturn(userDetails);
		Mockito.when(repository.searchUserAndRolesByEmail(nonExistingUserName)).thenReturn(new ArrayList<>());

		Mockito.when(repository.findByEmail(existingUserName)).thenReturn(Optional.of(user));
		Mockito.when(repository.findByEmail(nonExistingUserName)).thenReturn(Optional.empty());

	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(existingUserName);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUserName);
		;
	}

	@Test
	public void loadUserByUsernameShouldThrowsUsernameNotFoundExceptionWhenDoesNotUserExist() {

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {

			service.loadUserByUsername(nonExistingUserName);

		});
	}

	@Test
	public void authenticatedShouldReturnUserWhenUserExists() {
		Mockito.when(userUtil.getLoggedUserName()).thenReturn(existingUserName);

		User result = service.authenticated();
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUserName);
	}

	@Test
	public void authenticatedShouldReturnUserWhenUserDoesNotExists() {
		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUserName();

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();

		});
	}

	@Test
	public void getMeShouldReturnUserDTOWhenUserAuthenticated() {
		UserService spyUserService = Mockito.spy(service);
		Mockito.doReturn(user).when(spyUserService).authenticated();

		UserDTO result = spyUserService.getMe();
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getEmail(), existingUserName);
	}

	@Test
	public void getMeShouldReturnThrowsUsernameNotFoundExceptionWhenUserNotAuthenticated() {
		UserService spyUserService = Mockito.spy(service);
		Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticated();

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			spyUserService.getMe();

		});
	}

}
