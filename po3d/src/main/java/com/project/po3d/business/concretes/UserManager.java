package com.project.po3d.business.concretes;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.po3d.dto.auth.request.UserSigninRequest;
import com.project.po3d.dto.auth.request.UserSignupRequest;
import com.project.po3d.dto.request.UserDetailUpdateRequest;
import com.project.po3d.entity.Role;
import com.project.po3d.entity.User;
import com.project.po3d.entity.UserDetail;
import com.project.po3d.jwt.JwtTokenProvider;
import com.project.po3d.repository.RoleRepository;
import com.project.po3d.repository.UserDetailRepository;
import com.project.po3d.repository.UserRepository;
import com.project.po3d.business.abstracts.UserService;

import org.springframework.security.core.Authentication;

@Service
public class UserManager implements UserService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleRepository roleRepository;
	private final UserDetailRepository userDetailRepository;

    
  public UserManager(UserDetailRepository userDetailRepository,RoleRepository roleRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
	
		this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleRepository = roleRepository;
		this.userDetailRepository =userDetailRepository;
        
	}



	@Override
	public String login(UserSigninRequest userSigninRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userSigninRequest.getUsernameOrEmail(), userSigninRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtTokenProvider.generateToken(authentication);

		return token;
	}

	@Override
	public void signup(UserSignupRequest userSignupRequest) {
		if (isUserExist(userSignupRequest.getUsername())) {
			throw new RuntimeException("User already exists!");
		}

		User user = new User();
		user.setUsername(userSignupRequest.getUsername());
		user.setEmail(userSignupRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userSignupRequest.getPassword()));

        user.setCreatedTime(new Timestamp(System.currentTimeMillis()));

		Role userRole = roleRepository.findByName("USER");
		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);
	}

	@Override
	public void signupAndAssignRole(UserSignupRequest userSignupRequest, String roleName) {
		User user = new User();
		user.setUsername(userSignupRequest.getUsername());
		user.setEmail(userSignupRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userSignupRequest.getPassword()));

		Role userRole = roleRepository.findByName(roleName);
		if (userRole == null) {
			throw new RuntimeException("Role not found: " + roleName);
		}

		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);
	}

    @Override
	public boolean isUserExist(String username) {
		return userRepository.existsByUsername(username);
	}


	@Override
	public UUID getUserIdByUsername(String usernameOrEmail) {
		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
				() -> new UsernameNotFoundException("User not found by username or email: " + usernameOrEmail));
		return user.getId();
	}


	@Override
    public void createUserDetails(UserDetailUpdateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        userDetail.setEducation(request.getEducation());
        userDetail.setHomeAddress(request.getHomeAddress());
        userDetail.setPreviousExperience(request.getPreviousExperience());
        userDetail.setPhoneNumber(request.getPhoneNumber());
        userDetail.setLinkedinProfile(request.getLinkedinProfile());
        userDetail.setSkills(request.getSkills());
        userDetail.setMaritalStatus(request.getMaritalStatus());
        userDetail.setEmergencyContactName(request.getEmergencyContactName());
        userDetail.setEmergencyContactPhone(request.getEmergencyContactPhone());
        userDetail.setDateOfBirth(request.getDateOfBirth());
        userDetail.setNationality(request.getNationality());

        // Set created time
        userDetail.setCreatedTime(new Timestamp(System.currentTimeMillis()));

        userDetailRepository.save(userDetail);
    }

    @Override
    public boolean updateUserDetails(UserDetailUpdateRequest request) {
        Optional<UserDetail> userDetailOpt = userDetailRepository.findByUserId(request.getUserId());

        if (userDetailOpt.isPresent()) {
            UserDetail userDetail = userDetailOpt.get();

            // Yeni değerlerle alanları güncelle
            userDetail.setEducation(request.getEducation());
            userDetail.setHomeAddress(request.getHomeAddress());
            userDetail.setPreviousExperience(request.getPreviousExperience());
            userDetail.setPhoneNumber(request.getPhoneNumber());
            userDetail.setLinkedinProfile(request.getLinkedinProfile());
            userDetail.setSkills(request.getSkills());
            userDetail.setMaritalStatus(request.getMaritalStatus());
            userDetail.setEmergencyContactName(request.getEmergencyContactName());
            userDetail.setEmergencyContactPhone(request.getEmergencyContactPhone());
            userDetail.setDateOfBirth(request.getDateOfBirth());
            userDetail.setNationality(request.getNationality());

            // Güncelleme zamanı
            userDetail.setUpdatedTime(new Timestamp(System.currentTimeMillis()));

            userDetailRepository.save(userDetail);
            return true;
        }

        return false;  // Kullanıcı detayı bulunamadı
    }

    @Override
    public Optional<UserDetail> getUserDetailsById(UUID userId) {
        return userDetailRepository.findByUserId(userId);
    }

}
