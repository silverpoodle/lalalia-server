package com.isthisteamisthis.lalalia.like.command.infrastructure.service;

import com.isthisteamisthis.lalalia.user.command.domain.aggregate.entity.User;
import com.isthisteamisthis.lalalia.user.query.application.dto.response.UserResponse;
import com.isthisteamisthis.lalalia.user.query.application.service.UserQueryService;
import com.isthisteamisthis.lalalia.user.query.domain.repository.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiUserLikeCommandService {

    private final UserQueryService userQueryService;
    private final UserQueryRepository userQueryRepository;

    public UserResponse getUser(String authorizationHeader) {

        Long userId = userQueryService.getUserFromToken(authorizationHeader);

        User user = userQueryRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid UserId"));

        return UserResponse.from(user);
    }

}