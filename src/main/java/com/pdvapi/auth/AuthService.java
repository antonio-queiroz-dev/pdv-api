package com.pdvapi.auth;

import com.pdvapi.common.EmailAlreadyExistsException;
import com.pdvapi.common.InvalidCredentialsException;
import com.pdvapi.config.JwtService;
import com.pdvapi.tenant.Tenant;
import com.pdvapi.tenant.TenantRepository;
import com.pdvapi.user.Role;
import com.pdvapi.user.User;
import com.pdvapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        Tenant tenant = tenantRepository.save(Tenant.create(request.companyName()));
        int code = userRepository.findMaxCodeByTenantId(tenant.getId()) + 1;
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userRepository.save(
                User.create(tenant.getId(), code, request.userName(), request.email(), encodedPassword, Role.ADMIN)
        );

        String token = jwtService.generateToken(user.getId(), tenant.getId(), user.getRole());
        return new RegisterResponse(token, user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.getTenantId(), user.getRole());
        return new LoginResponse(token, user.getName(), user.getEmail());
    }
}