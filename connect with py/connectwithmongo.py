import pymongo
from bson.binary import Binary, STANDARD # STANDARD là subtype 0, nhưng có thể dùng các subtype khác
import os # Để lấy kích thước file

# --- Thông tin kết nối ---
MONGO_URI = "mongodb://myAdmin:22072004@localhost:27017/?authSource=admin"
DATABASE_NAME = "movie_theater" # Đổi thành tên database của bạn
COLLECTION_NAME = "posters"
FILE_PATH = "/home/soang/mongodb/connect with py/Screenshot from 2025-05-12 23-33-11.png" # Đổi thành đường dẫn file của bạn

# --- Kết nối tới MongoDB ---
try:
    client = pymongo.MongoClient(MONGO_URI)
    db = client[DATABASE_NAME]
    collection = db[COLLECTION_NAME]
    print("Kết nối MongoDB thành công!")

    # --- Đọc file ảnh dưới dạng binary ---
    try:
        with open(FILE_PATH, "rb") as f: # 'rb' = read binary
            image_bytes = f.read() # Đọc toàn bộ nội dung file vào biến image_bytes
        print(f"Đã đọc thành công file: {FILE_PATH}")

        # --- Kiểm tra kích thước file (Quan trọng cho việc quyết định dùng Binary hay GridFS) ---
        file_size_bytes = os.path.getsize(FILE_PATH)
        file_size_mb = file_size_bytes / (1024 * 1024)
        print(f"Kích thước file: {file_size_mb:.2f} MB")

        # !!! CẢNH BÁO QUAN TRỌNG !!!
        # MongoDB BSON document có giới hạn kích thước là 16MB.
        # Nếu file của bạn LỚN HƠN 16MB, bạn BẮT BUỘC phải sử dụng GridFS.
        # Việc lưu trực tiếp file lớn hơn 16MB bằng Binary() sẽ thất bại.
        if file_size_mb >= 16:
            print("!!! CẢNH BÁO: File quá lớn (>16MB). Hãy sử dụng GridFS thay vì Binary().")
            # Ở đây bạn nên dừng lại hoặc chuyển sang logic GridFS (không có trong ví dụ này)
            exit() # Thoát ví dụ đơn giản này

        # --- Tạo đối tượng BSON Binary ---
        # Subtype 0 (Binary.STANDARD hoặc chỉ cần Binary()) là subtype chung, phổ biến nhất.
        binary_data = Binary(image_bytes, subtype=0)
        # Hoặc đơn giản là: binary_data = Binary(image_bytes)

        # --- Chuẩn bị document để chèn ---
        # Nên lưu thêm metadata như filename, content_type
        document_to_insert = {
            "filename": os.path.basename(FILE_PATH),
            "content_type": "image/jpeg", # Hoặc tự động xác định MIME type
            "data": binary_data,
            "description": "Ảnh ví dụ lưu bằng PyMongo"
        }

        # --- Chèn document vào MongoDB ---
        insert_result = collection.insert_one(document_to_insert)
        print(f"Đã chèn document thành công với _id: {insert_result.inserted_id}")

        # --- Truy xuất và lưu lại file (ví dụ) ---
        retrieved_doc = collection.find_one({"_id": insert_result.inserted_id})
        if retrieved_doc:
            retrieved_binary_data = retrieved_doc['data'] # Đây vẫn là đối tượng Binary
            # Lấy raw bytes: retrieved_binary_data thực chất hoạt động như bytes
            output_filename = f"retrieved_{retrieved_doc['filename']}"
            try:
                with open(output_filename, "wb") as f_out: # 'wb' = write binary
                    f_out.write(retrieved_binary_data)
                print(f"Đã truy xuất và lưu thành công vào file: {output_filename}")
            except IOError as e:
                print(f"Lỗi khi ghi file truy xuất: {e}")
        else:
            print("Không tìm thấy document đã chèn.")

    except FileNotFoundError:
        print(f"Lỗi: Không tìm thấy file tại đường dẫn '{FILE_PATH}'")
    except IOError as e:
        print(f"Lỗi I/O khi đọc file: {e}")
    except pymongo.errors.OperationFailure as e:
        print(f"Lỗi thao tác MongoDB: {e.details}")
    except Exception as e:
        print(f"Đã xảy ra lỗi không mong muốn: {e}")

finally:
    if 'client' in locals() and client:
        client.close()
        print("Đã đóng kết nối MongoDB.")