package com.wenglam.baking_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogPostUpdateData extends BlogPostBaseData {
    private String imageUrl;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BlogPostUpdateData other = (BlogPostUpdateData) obj;
        if (imageUrl == null) {
            if (other.imageUrl != null)
                return false;
        } else if (!imageUrl.equals(other.imageUrl))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BlogPostUpdateData [imageUrl=" + imageUrl + ", title=" + title + ", content=" + content + ", author="
                + author + ", imageFile=" + imageFile + "]";
    }
}
