package ru.practicum.item;

import java.time.Instant;

public interface UrlMetaDataRetriever {
    UrlMetadata retrieve(String uri);
    interface UrlMetadata {
        String getNormalUrl();
        String getResolvedUrl();
        String getMimeType();
        String getTitle();
        boolean isHasImage();
        boolean isHasVideo();
        Instant getDateResolved();
    }
}
