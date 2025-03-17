package com.pragati.security.utility;

import java.util.List;

import com.pragati.entity.User;
import org.springframework.security.core.userdetails.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtils {

	public User convert(User user) {
		return new User(user.getEmailId(), user.getPassword(), List.of());
	}

}