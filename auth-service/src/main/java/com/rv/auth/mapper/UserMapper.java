package com.rv.auth.mapper;

import com.rv.auth.dto.UserDto;
import com.rv.auth.dto.UserResponseDTO;
import com.rv.auth.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class UserMapper {

        public static final ModelMapper mapper = new ModelMapper();

        static {
            // Map UserEntity to UserDto
            mapper.createTypeMap(UserEntity.class, UserDto.class)
                    .addMapping(UserEntity::getPassword, UserDto::setPwd)
                    .addMapping(UserEntity::getPhoneNumber, UserDto::setPhno);
            
            // Map UserDto to UserEntity
            mapper.createTypeMap(UserDto.class, UserEntity.class)
                    .addMapping(UserDto::getPwd, UserEntity::setPassword)
                    .addMapping(UserDto::getPhno, UserEntity::setPhoneNumber);
        }

        public static UserDto entityToDto(UserEntity entity) {
                UserDto userDto = mapper.map(entity, UserDto.class);
                // Set roleName from first role if exists
                if (entity.getRoles() != null && !entity.getRoles().isEmpty()) {
                    userDto.setRoleName(entity.getRoles().stream().findFirst().get().getName());
                }
                return userDto;
        }

        public static UserDto toDto(UserEntity entity) {
                return entityToDto(entity);
        }

        public static UserResponseDTO toUserResponseDto(UserEntity entity) {
                return mapper.map(entity, UserResponseDTO.class);
        }

        public static UserEntity dtoToEntity(UserDto dto) {
                return mapper.map(dto, UserEntity.class);
        }
}
