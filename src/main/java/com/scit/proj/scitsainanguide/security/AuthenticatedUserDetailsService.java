package com.scit.proj.scitsainanguide.security;

import com.scit.proj.scitsainanguide.domain.entity.MemberEntity;
import com.scit.proj.scitsainanguide.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {

	private final MemberJpaRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MemberEntity entity = repository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("아이디가 없습니다."));

		// 권한 설정
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		// adminYn 값에 따라 권한을 설정 (변경된 부분)
		if (entity.getAdminYn() != null && entity.getAdminYn()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // 기본 권한 설정
		}

		AuthenticatedUser user = AuthenticatedUser.builder()
				.id(entity.getMemberId())
				.password(entity.getPassword())
				.nickname(entity.getNickname())
				.authorities(authorities) // 설정된 권한
				.enabled(!entity.getWithdraw()) // Withdraw가 true일 때 비활성화 처리
				.build();

		log.debug("인증정보 : {}", user);

		return user;
	}
}
