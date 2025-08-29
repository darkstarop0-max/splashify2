package com.curosoft.splashify.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WallpaperResponse {
    @SerializedName("results")
    public List<Deviation> results;

    public static class Deviation {
        @SerializedName("deviationid")
        public String id;

        @SerializedName("title")
        public String title;

        @SerializedName("content")
        public Content content;

        @SerializedName("author")
        public Author author;
    }

    public static class Content {
        @SerializedName("src")
        public String src;
    }

    public static class Author {
        @SerializedName("username")
        public String username;
    }
}
