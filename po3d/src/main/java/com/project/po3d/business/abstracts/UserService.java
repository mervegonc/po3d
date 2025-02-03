package com.project.po3d.business.abstracts;

import java.util.Optional;
import java.util.UUID;

import com.project.po3d.dto.auth.request.UserSigninRequest;
import com.project.po3d.dto.auth.request.UserSignupRequest;
import com.project.po3d.dto.request.UserDetailUpdateRequest;
import com.project.po3d.entity.UserDetail;

public interface UserService {






    Optional<UserDetail> getUserDetailsByUserId(UUID userId);
    void createUserDetails(UserDetailUpdateRequest request);
    boolean updateUserDetails(UserDetailUpdateRequest request);



    String login(UserSigninRequest userSigninRequest);

	void signup(UserSignupRequest userSignupRequest);

	void signupAndAssignRole(UserSignupRequest userSignupRequest, String roleName);
    boolean isUserExist(String userName);

    UUID getUserIdByUsername(String usernameOrEmail);


     Optional<UserDetail> getUserDetailsById(UUID userId);

     boolean deleteUserDetails(UUID userId);
    void createUserDetails(UUID userId, UserDetailUpdateRequest request);

    boolean updateUserDetails(UUID userId, UserDetailUpdateRequest request);


}
