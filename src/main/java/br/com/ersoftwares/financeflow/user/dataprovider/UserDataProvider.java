package br.com.ersoftwares.financeflow.user.dataprovider;

import br.com.ersoftwares.financeflow.user.dataprovider.mapper.UserEntityMapper;
import br.com.ersoftwares.financeflow.user.domain.User;
import br.com.ersoftwares.financeflow.user.gateway.UserGateway;
import br.com.ersoftwares.financeflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDataProvider implements UserGateway {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        var entity = UserEntityMapper.toEntity(user);
        var saved = userRepository.save(entity);

        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntityMapper::toDomain);
    }
}
