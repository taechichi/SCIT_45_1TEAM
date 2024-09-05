package com.scit.proj.scitsainanguide.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// Lombok 어노테이션을 사용한 UserDetails 구현 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticatedUser implements UserDetails {

	private String id;
	private String password;
	private String name;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean enabled;

	// UserDetails 인터페이스의 메서드 구현
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return id;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
