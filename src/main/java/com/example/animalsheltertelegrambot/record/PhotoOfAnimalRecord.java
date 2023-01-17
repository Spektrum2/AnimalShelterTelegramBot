package com.example.animalsheltertelegrambot.record;

public class PhotoOfAnimalRecord {
    private long id;
    private String mediaType;
    private String url;

    public PhotoOfAnimalRecord(long id, String mediaType, String url) {
        this.id = id;
        this.mediaType = mediaType;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
