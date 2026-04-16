package com.example.hot6novelcraft.domain.episode.repository;

import com.example.hot6novelcraft.domain.episode.dto.response.EpisodeListResponse;
import com.example.hot6novelcraft.domain.episode.entity.QEpisode;
import com.example.hot6novelcraft.domain.episode.entity.enums.EpisodeStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomEpisodeRepositoryImpl implements CustomEpisodeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<EpisodeListResponse> findEpisodeListByNovelId(Long novelId, Pageable pageable) {

        QEpisode episode = QEpisode.episode;

        List<EpisodeListResponse> content = queryFactory
                .select(Projections.constructor(EpisodeListResponse.class,
                        episode.id,
                        episode.episodeNumber,
                        episode.title,
                        episode.isFree,
                        episode.pointPrice,
                        episode.likeCount,
                        episode.publishedAt
                ))
                .from(episode)
                .where(
                        episode.novelId.eq(novelId),
                        episode.status.eq(EpisodeStatus.PUBLISHED), // 발행한것만
                        episode.isDeleted.eq(false) // 삭제 된건지 확인
                )
                .orderBy(episode.episodeNumber.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(episode.count())
                .from(episode)
                .where(
                        episode.novelId.eq(novelId),
                        episode.status.eq(EpisodeStatus.PUBLISHED),
                        episode.isDeleted.eq(false)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}