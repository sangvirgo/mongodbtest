// Kiểm tra xem trình duyệt có hỗ trợ Geolocation API không
if (navigator.geolocation) {
  // Gọi hàm getCurrentPosition để lấy vị trí hiện tại
  navigator.geolocation.getCurrentPosition(
    // Hàm callback khi lấy vị trí thành công
    function(position) {
      // Lấy kinh độ và vĩ độ từ đối tượng position
      const latitude = position.coords.latitude;
      const longitude = position.coords.longitude;
      const accuracy = position.coords.accuracy;

      console.log("Vị trí của bạn:");
      console.log("Vĩ độ (Latitude): " + latitude);
      console.log("Kinh độ (Longitude): " + longitude);
      console.log("Độ chính xác (Accuracy): " + accuracy + " mét");

      // Tại đây, bạn có thể sử dụng kinh độ và vĩ độ để:
      // 1. Hiển thị vị trí trên bản đồ (ví dụ: Google Maps, Leaflet, Mapbox)
      // 2. Gửi lên server để tìm các địa điểm gần đó
      // 3. Tự động điền vào ô tìm kiếm vị trí
      // ... và nhiều ứng dụng khác cho website đặt vé của bạn
    },
    // Hàm callback khi có lỗi xảy ra hoặc người dùng từ chối
    function(error) {
      switch (error.code) {
        case error.PERMISSION_DENIED:
          console.error("Người dùng từ chối yêu cầu định vị.");
          alert("Bạn đã từ chối chia sẻ vị trí. Vui lòng cho phép để sử dụng tính năng này.");
          break;
        case error.POSITION_UNAVAILABLE:
          console.error("Thông tin vị trí không khả dụng.");
          alert("Không thể xác định vị trí của bạn. Vui lòng thử lại.");
          break;
        case error.TIMEOUT:
          console.error("Yêu cầu định vị đã hết thời gian chờ.");
          alert("Hết thời gian chờ khi cố gắng lấy vị trí của bạn.");
          break;
        case error.UNKNOWN_ERROR:
          console.error("Đã xảy ra lỗi không xác định.");
          alert("Đã có lỗi xảy ra khi lấy vị trí.");
          break;
      }
    },
    // Tùy chọn (options) cho việc lấy vị trí
    {
      enableHighAccuracy: true, // Cố gắng lấy vị trí chính xác nhất (có thể tốn pin hơn)
      timeout: 10000,           // Thời gian chờ tối đa (ms) để lấy vị trí
      maximumAge: 0             // Thời gian tối đa (ms) của vị trí được lưu trong cache mà vẫn chấp nhận được
                                // (0 nghĩa là luôn yêu cầu vị trí mới)
    }
  );
} else {
  console.error("Trình duyệt của bạn không hỗ trợ Geolocation API.");
  alert("Rất tiếc, trình duyệt của bạn không hỗ trợ tính năng định vị.");
}