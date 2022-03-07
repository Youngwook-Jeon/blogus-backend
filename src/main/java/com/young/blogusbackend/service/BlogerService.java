package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.UpdateBlogerRequest;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.repository.BlogerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogerService {

    private final BlogerRepository blogerRepository;
    private final AuthService authService;

    public void updateUserProfile(UpdateBlogerRequest updateBlogerRequest) {
        Bloger currentUser = authService.getCurrentUser();
        currentUser.setName(updateBlogerRequest.getName());
        currentUser.setAvatar(updateBlogerRequest.getAvatar());
        currentUser.setUpdatedAt(Instant.now());
        blogerRepository.save(currentUser);
    }
}
