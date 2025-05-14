package com.withmongodb.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface IImageService {
    String addPhoto(String title, MultipartFile file) throws IOException;
    PhotoData getPhoto(String id) throws IllegalStateException, IOException;

    // Triển khai đầy đủ class với getter/setter thay vì dùng Lombok
    public static class PhotoData {
        private String title;
        private String contentType;
        private InputStream stream;

        // Getter và Setter thủ công
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public InputStream getStream() {
            return stream;
        }

        public void setStream(InputStream stream) {
            this.stream = stream;
        }
    }
}