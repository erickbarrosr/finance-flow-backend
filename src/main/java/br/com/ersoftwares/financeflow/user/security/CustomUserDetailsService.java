package br.com.ersoftwares.financeflow.user.security;

import br.com.ersoftwares.financeflow.user.gateway.UserGateway;
import br.com.ersoftwares.financeflow.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserGateway userGateway;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userGateway.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}

