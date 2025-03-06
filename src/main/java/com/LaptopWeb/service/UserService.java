package com.LaptopWeb.service;

import com.LaptopWeb.dto.request.CreateUserRequest;
import com.LaptopWeb.dto.request.UpdateUserRequest;
import com.LaptopWeb.entity.EmailDetails;
import com.LaptopWeb.entity.Role;
import com.LaptopWeb.entity.User;
import com.LaptopWeb.enums.AuthProvider;
import com.LaptopWeb.exception.AppException;
import com.LaptopWeb.exception.ErrorApp;
import com.LaptopWeb.mapper.UserMapper;
import com.LaptopWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableMethodSecurity
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final EmailService emailService;

    public User createUser(CreateUserRequest request) throws Exception{
        // Kiểm tra tên đăng nhập và email trước khi tiếp tục
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorApp.USERNAME_EXISTED);
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorApp.EMAIL_EXISTED);

        // Lấy role và tạo user từ request
        Role role = roleService.getRole("USER");
        User user = userMapper.toUser(request);

        // Mã hóa mật khẩu và thiết lập role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setAuthProvider(AuthProvider.LOCAL);

        // Lưu user vào database
        User savedUser = userRepository.save(user);

        // Tạo nội dung email sau khi lưu thành công
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(savedUser.getEmail());
        emailDetails.setSubject("Chào mừng bạn đến với hệ thống của chúng tôi!");

        String msgBody = String.format(
                "Xin chào %s,\n\n"
                        + "Tài khoản của bạn đã được tạo thành công với thông tin sau:\n"
                        + "Tên đăng nhập: %s\n"
                        + "Mật khẩu: %s\n"
                        + "Email: %s\n"
                        + "Số điện thoại: %s\n"
                        + "Ngày sinh: %s\n\n"
                        + "Vui lòng đăng nhập và đổi mật khẩu để đảm bảo an toàn.\n\n"
                        + "Trân trọng,\nĐội ngũ hỗ trợ",
                savedUser.getFullName(), savedUser.getUsername(),
                request.getPassword(), savedUser.getEmail(),
                savedUser.getPhoneNumber(), savedUser.getDob()
        );
        emailDetails.setMsgBody(msgBody);

        // Gửi email thông báo
        emailService.sendSimpleMail(emailDetails);

        return savedUser;
    }

    public User createUserByGoogleOAuth2(OAuth2AuthenticationToken oAuth2AuthenticationToken) throws Exception{
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElse(null);

        Role role = roleService.getRole("USER");

        if(user == null) {
            user = new User();

            user.setRole(role);
            user.setEmail(email);
            user.setFullName(name);
            user.setAuthProvider(AuthProvider.GOOGLE);

            return userRepository.save(user);
        }

        return user;
    }

    public User createUserByFacebookOAuth2(OAuth2AuthenticationToken oAuth2AuthenticationToken) throws Exception {
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
//        String username = email.split("@")[0];

        User user = userRepository.findByEmail(email).orElse(null);

        Role role = roleService.getRole("USER");

        if(user == null) {
            user = new User();

            user.setRole(role);
            user.setEmail(email);
            user.setFullName(name);
            user.setAuthProvider(AuthProvider.FACEBOOK);
//            user.setUsername(username);

            return userRepository.save(user);
        }

        return user;
    }

    public User createUserByOAuth2(OAuth2AuthenticationToken authToken) throws Exception {
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        if ("google".equals(registrationId)) {
            return createUserByGoogleOAuth2(authToken);
        } else if ("facebook".equals(registrationId)) {
            return createUserByFacebookOAuth2(authToken);
        }
        throw new IllegalArgumentException("Unsupported OAuth2 provider");
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    public User getById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorApp.USER_NOTFOUND));

        return  user;
    }

    public User getByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new AppException(ErrorApp.USER_NOTFOUND));

        return user;
    }

    public User getByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorApp.USER_NOTFOUND));

        return user;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<User> getPageUser(Integer pageNum, Integer size, String sortField, String keyWord) {
        Sort sort = sortField != null ? Sort.by(sortField).ascending() : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageNum, size, sort);

        if(keyWord == null) {
            return userRepository.findAll(pageable);
        } else {
            return userRepository.findAllUser(keyWord, pageable);
        }

    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    public User updateUser(Integer id, UpdateUserRequest request) {
        User user = getById(id);

        User user1 = userMapper.toUserUpdate(request);

        user1.setId(user.getId());
        user1.setUsername(user.getUsername());
        user1.setPassword(user.getPassword());
        user1.setRole(user.getRole());

        return userRepository.save(user1);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.claims['data']['id']")
    public User changePassword(Integer id, String password) {
        User user = getById(id);

        String encodePassword = passwordEncoder.encode(password);

        user.setPassword(encodePassword);

        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Integer id) {
        User user = getById(id);

        userRepository.deleteById(id);
    }
}
