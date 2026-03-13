package com.rv.user.service;

import com.rv.user.dto.ResetPwdDto;
import com.rv.user.dto.ShippingAddressDto;
import com.rv.user.dto.UserDto;
import com.rv.user.entity.RoleEntity;
import com.rv.user.entity.ShippingAddressEntity;
import com.rv.user.entity.UserEntity;
import com.rv.user.exception.UserNotFoundException;
import com.rv.user.mapper.AddressMapper;
import com.rv.user.mapper.UserMapper;
import com.rv.user.repository.RoleRepository;
import com.rv.user.repository.ShippingAddressRepository;
import com.rv.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShippingAddressRepository shippingAddressrepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto saveUser(UserDto userDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("User already exists with this email: " + userDto.getEmail());
        }

        String randomPwd = generateRandomPwd(5);
        userDto.setPwd(randomPwd);
        userDto.setPwdUpdated("NO");

        // Encrypt the password before saving
        String encryptedPwd = passwordEncoder.encode(randomPwd);

        UserEntity userEntity = UserMapper.dtoToEntity(userDto);
        userEntity.setPassword(encryptedPwd);
        userEntity.setPhoneNumber(userDto.getPhno());
        userEntity.setPwdUpdated("NO");

        Set<RoleEntity> roleSet = new HashSet<>();

        if (userDto.getRoleName() != null) {
            RoleEntity role = roleRepository.findByName(userDto.getRoleName()).orElse(null);
            if (role != null) {
                roleSet.add(role);
            } else {
                throw new RuntimeException("Role not found: " + userDto.getRoleName());
            }
        }
        userEntity.setRoles(roleSet);
        UserEntity savedEntity = userRepository.save(userEntity);
                    
        String emailBody = "Dear " + savedEntity.getName() + ",\n\n" +
            "Welcome to our SwiftMart Platform!\n\n" +
            "Your account has been successfully completed. Here are your login credentials:\n\n" +
            "Email: " + savedEntity.getEmail() + "\n" +
            "Password: " + randomPwd + "\n\n" +
            "For your security, we recommend that you change your password immediately upon your first login.\n\n" +
            "If you have any questions or need assistance,please contact our support team at support@swiftmart.com\n\n" +
            "Best regards,\n" +
            "SwiftMart Team\n\n" +
            "Note: This is an automated message. Please do not reply to this email.";
        emailService.sendEmail(savedEntity.getEmail(), "Account Created - Welcome to SwiftMart Platform", emailBody);
        
        return UserMapper.entityToDto(savedEntity);
    }

    @Override
    public UserDto login(String email, String pwd) {
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if (userEntity != null && passwordEncoder.matches(pwd, userEntity.getPassword())) {
            return UserMapper.entityToDto(userEntity);
        }
        return null;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if (userEntity != null) {
            UserDto dto = UserMapper.entityToDto(userEntity);
            dto.setPwd(null); // Remove password from response
            return dto;
        } else {
            throw new UserNotFoundException("User does not exist with email: " + email);
        }
    }

    @Override
    public ResetPwdDto resetPassword(ResetPwdDto resetPwdDto) {

        if (resetPwdDto == null || resetPwdDto.getEmail() == null) {
            return null;
        }

        UserEntity userEntity = userRepository.findByEmail(resetPwdDto.getEmail()).orElse(null);

        if (userEntity != null) {
            if (resetPwdDto.getNewPwd() != null && resetPwdDto.getNewPwd().equals(resetPwdDto.getConfirmPwd())) {
                String encryptedPwd = passwordEncoder.encode(resetPwdDto.getNewPwd());
                userEntity.setPassword(encryptedPwd);
                userEntity.setPwdUpdated("YES");
                userRepository.save(userEntity);
                return resetPwdDto;
            }
        }

        return null;
    }

    @Override
    public ShippingAddressDto saveShippingAddress(Integer userId, ShippingAddressDto shippingAddressDto) {
        ShippingAddressEntity addressEntity = AddressMapper.dtoToEntity(shippingAddressDto);

        // Check for duplicate address type using repository query
        Optional<ShippingAddressEntity> existingAddress = shippingAddressrepository.findByUserIdAndAddrType(userId, addressEntity.getAddrType());
        if (existingAddress.isPresent()) {
            throw new RuntimeException("Address of this type already exists for the user. Please add/update with a different address type.");
        }

        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            addressEntity.setUser(userEntity);
            ShippingAddressEntity savedAddrEntity = shippingAddressrepository.save(addressEntity);
            return AddressMapper.entityToDto(savedAddrEntity);
        }

        return null;
    }

    @Override
    public ShippingAddressDto deleteShippingAddress(Integer shippingAddressId) {

        Optional<ShippingAddressEntity> address = shippingAddressrepository.findById(Long.valueOf(shippingAddressId));
        if (address.isPresent()) {
            ShippingAddressEntity addressEntity = address.get();
            shippingAddressrepository.delete(addressEntity);
            return AddressMapper.entityToDto(addressEntity);
        }
        return null;
    }


    private String generateRandomPwd(int pwdLength) {
        Random random = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        StringBuilder buffer = new StringBuilder(pwdLength);
        for (int i = 0; i < pwdLength; i++) {
            int randomIndex = random.nextInt(chars.length());
            char charAt = chars.charAt(randomIndex);
            buffer.append(charAt);
        }
        return buffer.toString();
    }

}
