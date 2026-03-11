package com.malgn.cmsserver.contents.repository;

import com.malgn.cmsserver.contents.domain.Contents;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@NullMarked
public interface ContentsRepository extends JpaRepository<Contents, Long> {

    Slice<Contents> findAllByOrderByCreatedDateDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE Contents c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void increaseViewCount(@Param("id") Long id);
}
