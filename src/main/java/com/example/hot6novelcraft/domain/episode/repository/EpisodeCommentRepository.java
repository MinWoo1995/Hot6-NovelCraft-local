package com.example.hot6novelcraft.domain.episode.repository;

import com.example.hot6novelcraft.domain.episode.entity.EpisodeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EpisodeCommentRepository extends JpaRepository<EpisodeComment, Long> {

    // 본인 댓글 확인용
    Optional<EpisodeComment> findByIdAndUserId(Long id, Long userId);

}