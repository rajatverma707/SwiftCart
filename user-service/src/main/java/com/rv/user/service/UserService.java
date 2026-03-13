package com.rv.user.service;

import com.rv.user.dto.ResetPwdDto;
import com.rv.user.dto.ShippingAddressDto;
import com.rv.user.dto.UserDto;

public interface UserService {

    public UserDto saveUser(UserDto userDto);

    public UserDto login(String email, String password);

    public UserDto getUserByEmail(String email);

    public ResetPwdDto resetPassword(ResetPwdDto resetPwdDto);

    public ShippingAddressDto saveShippingAddress(Integer userId, ShippingAddressDto shippingAddressDto);

    public ShippingAddressDto deleteShippingAddress(Integer shippingAddressId);

}
