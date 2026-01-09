package ru.practicum.my_blog_back_app.model;

import java.util.List;

/**
 * Ответ с пагинированным списком постов.
 */
public class PagedPostsResponse {

    private List<Post> posts;
    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;

    public PagedPostsResponse() {
    }

    public PagedPostsResponse(List<Post> posts, boolean hasPrev, boolean hasNext, int lastPage) {
        this.posts = posts;
        this.hasPrev = hasPrev;
        this.hasNext = hasNext;
        this.lastPage = lastPage;
    }

    public PagedPostsResponse(List<Post> posts) {
        this.posts = posts;
    }

    // Геттеры и сеттеры
    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public boolean isHasPrev() {
        return hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
}
