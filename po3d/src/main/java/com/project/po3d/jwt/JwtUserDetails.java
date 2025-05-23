package com.project.po3d.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.po3d.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtUserDetails implements UserDetails {

	private UUID id;
	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;

	public JwtUserDetails(UUID id, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public static JwtUserDetails create(User user) {
		List<GrantedAuthority> authoritiesList = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authoritiesList.add(new SimpleGrantedAuthority(role.getName()));
		});
		return new JwtUserDetails(user.getId(), user.getUsername(), user.getPassword(), authoritiesList);
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
		return true;
	}

	@Override
	public String getUsername() {
		return username; // Değiştirildi, doğru kullanıcı adını döndürüyor
	}
}