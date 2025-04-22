    package com.project.po3d.business.concretes;


    import java.sql.Timestamp;
    import java.util.HashSet;
    import java.util.Optional;
    import java.util.Set;
    import java.util.UUID;
import java.util.stream.Collectors;

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
import com.project.po3d.dto.response.UserDetailResponse;
import com.project.po3d.entity.PasswordResetToken;
import com.project.po3d.entity.Role;
    import com.project.po3d.entity.User;
    import com.project.po3d.entity.UserDetail;
    import com.project.po3d.jwt.JwtTokenProvider;
import com.project.po3d.repository.PasswordResetTokenRepository;
import com.project.po3d.repository.RoleRepository;
    import com.project.po3d.repository.UserDetailRepository;
    import com.project.po3d.repository.UserRepository;
import com.project.po3d.business.abstracts.EmailService;
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
private final PasswordResetTokenRepository passwordResetTokenRepository;
private final EmailService emailService;

        
    public UserManager(UserDetailRepository userDetailRepository,EmailService emailService,RoleRepository roleRepository,PasswordResetTokenRepository passwordResetTokenRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        
            this.passwordEncoder = passwordEncoder;
            this.authenticationManager = authenticationManager;
            this.jwtTokenProvider = jwtTokenProvider;
            this.roleRepository = roleRepository;
            this.userDetailRepository =userDetailRepository;
            this.emailService = emailService;
            this.passwordResetTokenRepository = passwordResetTokenRepository;
            
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
            UUID userId = request.getUserId();
            Optional<UserDetail> existingUserDetail = userDetailRepository.findByUserId(userId);
        
            if (existingUserDetail.isPresent()) {
                UserDetail userDetail = existingUserDetail.get();
        
                // Eğer istek içinde bir değer varsa onu güncelle, yoksa eski değeri koru.
                if (request.getEducation() != null) userDetail.setEducation(request.getEducation());
                if (request.getHomeAddress() != null) userDetail.setHomeAddress(request.getHomeAddress());
                if (request.getPreviousExperience() != null) userDetail.setPreviousExperience(request.getPreviousExperience());
                if (request.getPhoneNumber() != null) userDetail.setPhoneNumber(request.getPhoneNumber());
                if (request.getLinkedinProfile() != null) userDetail.setLinkedinProfile(request.getLinkedinProfile());
                if (request.getSkills() != null) userDetail.setSkills(request.getSkills());
                if (request.getMaritalStatus() != null) userDetail.setMaritalStatus(request.getMaritalStatus());
                if (request.getEmergencyContactName() != null) userDetail.setEmergencyContactName(request.getEmergencyContactName());
                if (request.getEmergencyContactPhone() != null) userDetail.setEmergencyContactPhone(request.getEmergencyContactPhone());
                if (request.getDateOfBirth() != null) userDetail.setDateOfBirth(request.getDateOfBirth());
                if (request.getNationality() != null) userDetail.setNationality(request.getNationality());
        
                userDetailRepository.save(userDetail);
                return true;
            }
        
            return false;
        }
        

      

        @Override
        public Optional<UserDetail> getUserDetailsById(UUID userId) {
            return userDetailRepository.findByUserId(userId);
        }





    
        public void createUserDetails(UUID userId, UserDetailUpdateRequest request) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
            UserDetail userDetail = new UserDetail();
            userDetail.setUser(user);  // Kullanıcı detayına user atanıyor
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
        
            userDetailRepository.save(userDetail);
        }
        
        @Override
        public boolean updateUserDetails(UUID userId, UserDetailUpdateRequest request) {
            Optional<UserDetail> userDetailOpt = userDetailRepository.findByUserId(userId);
    
            if (userDetailOpt.isPresent()) {
                UserDetail userDetail = userDetailOpt.get();
    
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
    
                userDetail.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
    
                userDetailRepository.save(userDetail);
                return true;
            }
    
            return false;
        }
    
        @Override
        public boolean deleteUserDetails(UUID userId) {
            Optional<UserDetail> existingUserDetail = userDetailRepository.findByUserId(userId);
        
            if (existingUserDetail.isPresent()) {
                userDetailRepository.delete(existingUserDetail.get());
                return true;
            }
        
            return false;
        }
        


@Override
public Optional<UserDetailResponse> getUserDetailsByUserId(UUID userId) {
    Optional<UserDetail> userDetailOpt = userDetailRepository.findByUserId(userId);
    
    if (userDetailOpt.isEmpty()) {
        return Optional.empty();
    }

    UserDetail userDetail = userDetailOpt.get();
    User user = userDetail.getUser();

    Set<String> roleNames = user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet());

    UserDetailResponse response = new UserDetailResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            userDetail.getPhoneNumber(),
            userDetail.getEducation(),
            userDetail.getHomeAddress(),
            roleNames
    );

    return Optional.of(response);
}


@Override
public void createPasswordResetToken(String email) {
    // 🔍 1. Kullanıcıyı email ile bul
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email adresi sistemde kayıtlı değil."));

    // 🔑 2. Token oluştur (UUID ile benzersiz)
    String token = UUID.randomUUID().toString();

    // ⏰ 3. Token geçerlilik süresi (örneğin 15 dakika)
    Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000); // 15 dk

    // 💾 4. Token kaydet (PasswordResetToken entity’sine)
    PasswordResetToken resetToken = new PasswordResetToken(null, user.getEmail(),token,user, expiresAt);
    passwordResetTokenRepository.save(resetToken);

    // 🔗 5. Şifre sıfırlama bağlantısı oluştur
    String resetLink = "http://localhost:3000/reset-password?token=" + token;

    // 📧 6. E-postayı gönder (EmailService aracılığıyla)
    emailService.send(
        user.getEmail(),
        "Şifre Sıfırlama Bağlantısı",
        "Merhaba " + user.getUsername() + ",\n\n" +
        "Şifrenizi sıfırlamak için aşağıdaki bağlantıya tıklayın:\n\n" +
        resetLink + "\n\n" +
        "Bağlantı 15 dakika geçerlidir.\n\n" +
        "İyi günler dileriz."
    );
}



@Override
public void resetPassword(String token, String newPassword) {
    PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Geçersiz veya süresi dolmuş token."));

    if (resetToken.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
        throw new IllegalArgumentException("Token süresi dolmuş.");
    }

    User user = resetToken.getUser();

    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    // Token tek kullanımlık olduğu için siliniyor
    passwordResetTokenRepository.delete(resetToken);
}



    }
