package com.rv.auth.service;

import com.rv.auth.dto.ResetPwdDto;
import com.rv.auth.dto.ShippingAddressDto;
import com.rv.auth.dto.UserDto;

public interface UserService {

    public UserDto saveUser(UserDto userDto);

    public UserDto login(String email, String password);

    public UserDto getUserByEmail(String email);

    public ResetPwdDto resetPassword(ResetPwdDto resetPwdDto);

    public ShippingAddressDto saveShippingAddress(Integer userId, ShippingAddressDto shippingAddressDto);

    public ShippingAddressDto deleteShippingAddress(Integer shippingAddressId);

}
