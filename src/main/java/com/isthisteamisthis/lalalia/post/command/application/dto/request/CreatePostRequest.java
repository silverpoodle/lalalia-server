package com.isthisteamisthis.lalalia.post.command.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class CreatePostRequest {

    @JsonProperty("compose_song_no")
    private final Long composeSongNo;

    @JsonProperty("perfect_score_no")
    private final Long perfectScoreNo;

    @JsonProperty("title")
    private final String title;

    public Long getComposeSongNo() {
        return composeSongNo;
    }

    public Long getPerfectScoreNo() {
        return perfectScoreNo;
    }

    public String getTitle() {
        return title;
    }
}