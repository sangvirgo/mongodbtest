package com.withmongodb.service.image;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils; // Cần thêm dependency commons-io
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageService implements IImageService {

    @Autowired
    private GridFsTemplate gridFsTemplate; // Dùng để thực thi thao tác GridFS cơ bản

    @Autowired
    private GridFsOperations operations; // Dùng để truy vấn phức tạp hơn hoặc lấy tài nguyên

    // --- Lưu ảnh ---
    public String addPhoto(String title, MultipartFile file) throws IOException {
        // Tạo metadata cho file (có thể thêm các thông tin khác)
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "image");
        metaData.put("title", title);
        metaData.put("contentType", file.getContentType());
        metaData.put("originalFilename", file.getOriginalFilename());
        metaData.put("size", file.getSize());

        // Lưu file vào GridFS, trả về ObjectId của file đã lưu
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), // Lấy InputStream từ MultipartFile
                file.getOriginalFilename(), // Tên file gốc
                file.getContentType(), // Loại nội dung (MIME type)
                metaData // Metadata tùy chỉnh
        );
        return id.toString(); // Trả về ID dưới dạng String
    }

    // --- Lấy ảnh ---
    public PhotoData getPhoto(String id) throws IllegalStateException, IOException {
        // Tìm file metadata dựa trên ID
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));

        if (file == null) {
            return null; // Hoặc ném Exception nếu muốn
        }

        PhotoData photoData = new PhotoData();
        photoData.setTitle(file.getMetadata().get("title").toString());
        photoData.setContentType(file.getMetadata().get("contentType").toString());

        // Lấy nội dung file dưới dạng InputStream và đọc vào byte array
        // operations.getResource(file) trả về GridFsResource
        photoData.setStream(operations.getResource(file).getInputStream());
        // Hoặc đọc ngay vào byte array nếu cần (cẩn thận với file lớn)
        // byte[] data = IOUtils.toByteArray(operations.getResource(file).getInputStream());
        // photoData.setImageBytes(data);

        return photoData;
    }

}
