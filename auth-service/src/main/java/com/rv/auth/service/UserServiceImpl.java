package com.rv.auth.service;

import com.rv.auth.dto.ResetPwdDto;
import com.rv.auth.dto.ShippingAddressDto;
import com.rv.auth.dto.UserDto;
import com.rv.auth.entity.RoleEntity;
import com.rv.auth.entity.ShippingAddressEntity;
import com.rv.auth.entity.UserEntity;
import com.rv.auth.mapper.AddressMapper;
import com.rv.auth.mapper.UserMapper;
import com.rv.auth.repository.RoleRepository;
import com.rv.auth.repository.ShippingAddressRepository;
import com.rv.auth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public UserDto saveUser(UserDto userDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("User already exists with this email: " + userDto.getEmail());
        }

        String randomPwd = generateRandomPwd(5);
        userDto.setPwd(randomPwd);
        userDto.setPwdUpdated("NO");

        UserEntity userEntity = UserMapper.dtoToEntity(userDto);
        userEntity.setPassword(randomPwd);
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
                "Password: " + savedEntity.getPassword() + "\n\n" +
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

        if (userEntity != null) {
            return UserMapper.entityToDto(userEntity);
        }

        return null;
    }

    @Override
    public UserDto getUserByEmail(String email) {

        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);

        if (userEntity != null) {
            return UserMapper.entityToDto(userEntity);
        }

        return null;
    }

    @Override
    public ResetPwdDto resetPassword(ResetPwdDto resetPwdDto) {

        if (resetPwdDto == null || resetPwdDto.getEmail() == null) {
            return null;
        }

        UserEntity userEntity = userRepository.findByEmail(resetPwdDto.getEmail()).orElse(null);

        if (userEntity != null) {
            if (resetPwdDto.getNewPwd() != null && resetPwdDto.getNewPwd().equals(resetPwdDto.getConfirmPwd())) {
                userEntity.setPassword(resetPwdDto.getNewPwd());
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
