package okestro.internproject.domain.auth.jwt.repository;

import okestro.internproject.domain.auth.jwt.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
}
