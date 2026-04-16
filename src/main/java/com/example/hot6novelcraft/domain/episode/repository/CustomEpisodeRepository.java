package com.example.hot6novelcraft.domain.episode.repository;

import com.example.hot6novelcraft.domain.episode.dto.response.EpisodeListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomEpisodeRepository {

    // 회차 목록 조회 (QueryDSL + 인덱싱)
    Page<EpisodeListResponse> findEpisodeListByNovelId(Long novelId, Pageable pageable);

}
