package br.com.ersoftwares.financeflow.user.gateway;

import br.com.ersoftwares.financeflow.user.domain.User;

import java.util.Optional;

public interface UserGateway {

    User save(User user);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
