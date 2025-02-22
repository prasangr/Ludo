package org.example.ludoo.Repository;


import org.example.ludoo.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    List<Token> findByPosition(int position);

    List<Token> findByPlayerId(UUID playerId);
}
