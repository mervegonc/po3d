package com.project.po3d.business.abstracts;

import java.util.Optional;
import java.util.UUID;

import com.project.po3d.dto.auth.request.UserSigninRequest;
import com.project.po3d.dto.auth.request.UserSignupRequest;
import com.project.po3d.dto.request.UserDetailUpdateRequest;
import com.project.po3d.entity.UserDetail;

public interface UserService {









    String login(UserSigninRequest userSigninRequest);

	void signup(UserSignupRequest userSignupRequest);

	void signupAndAssignRole(UserSignupRequest userSignupRequest, String roleName);
    boolean isUserExist(String username);

    UUID getUserIdByUsername(String usernameOrEmail);

     boolean updateUserDetails(UserDetailUpdateRequest request);

     Optional<UserDetail> getUserDetailsById(UUID userId);

    void createUserDetails(UserDetailUpdateRequest request);


}
