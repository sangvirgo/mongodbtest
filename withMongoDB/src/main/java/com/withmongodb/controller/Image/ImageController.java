package com.withmongodb.controller.Image;

import com.withmongodb.service.image.IImageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private IImageService iImageService;


    // Endpoint để Upload ảnh
    @PostMapping("/add")
    public ResponseEntity<String> addPhoto(@RequestParam("title") String title,
                                           @RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng chọn file ảnh để upload.");
        }
        try {
            String id = iImageService.addPhoto(title, image);
            return ResponseEntity.ok("Ảnh đã được upload thành công với ID: " + id);
        } catch (IOException e) {
            // Log lỗi chi tiết hơn ở đây
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi upload ảnh: " + e.getMessage());
        }
    }

    // Endpoint để xem/tải ảnh theo ID
    @GetMapping("/{id}")
    public void getPhoto(@PathVariable String id, HttpServletResponse response) throws IOException {
        try {
            IImageService.PhotoData photoData = iImageService.getPhoto(id);


            if (photoData == null || photoData.getStream() == null) {
                // Ném lỗi 404 nếu không tìm thấy ảnh
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ảnh với ID: " + id);
            }


            // Thiết lập kiểu nội dung (quan trọng để trình duyệt hiển thị đúng)
            response.setContentType(photoData.getContentType());


            // Thiết lập header để trình duyệt hiển thị ảnh thay vì tải xuống (tùy chọn)
            // response.setHeader("Content-Disposition", "inline; filename=\"" + photoData.getTitle() + "\"");


            // Copy dữ liệu từ InputStream của ảnh vào OutputStream của response
            try (InputStream imageStream = photoData.getStream()) {
                StreamUtils.copy(imageStream, response.getOutputStream());
            }


        } catch (IllegalStateException e) {
            // Có thể xảy ra nếu file GridFS bị lỗi
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi đọc dữ liệu ảnh", e);
        }
        // IOException đã được khai báo throws nên Spring sẽ xử lý hoặc bạn có thể bắt cụ thể
    }


}
