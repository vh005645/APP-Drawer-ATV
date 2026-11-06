# AppDrawer TV

Ứng dụng danh sách ứng dụng (App Drawer) đơn giản dành cho Android TV, được tùy chỉnh với giao diện Tiếng Việt.

## Tính năng
- Hiển thị toàn bộ các ứng dụng đã cài đặt trên thiết bị.
- Giao diện tối giản, phù hợp cho màn hình TV.
- Ngôn ngữ: Tiếng Việt.

## Tải về phiên bản mới nhất
Bạn có thể tải về tệp APK trực tiếp từ liên kết dưới đây:

👉 **[Tải xuống AppDrawer.apk](https://github.com/daivietpda/AppDrawerTV/raw/master/AppDrawer.apk)**

### Cài đặt nhanh qua Downloader App
Nếu bạn sử dụng ứng dụng **Downloader** trên Android TV, hãy nhập mã code sau để tải nhanh:
👉 **Code: `8297116`**

## Hình ảnh ứng dụng
*Tiêu đề "Tất Cả APP" đã được căn giữa và biểu tượng ứng dụng đã được cập nhật mới.*

---
*Phát triển bởi daivietpda.*


## Phiên bản 1.2.0 - chạy làm launcher mặc định và gọi TV Guard dậy
- Thêm khai báo launcher (`HOME` + `DEFAULT`) để đặt làm màn hình chính của TV.
- Mỗi lần màn hình này hiện ra (bật TV, thức dậy từ chế độ chờ, về Home), nó mở mục khởi động trong suốt của TV Guard
  (`com.tvguard.app/.BootTrigger`, cần TV Guard từ bản 1.21 trở lên) tối đa 1 lần mỗi 2 phút. Không có TV Guard thì bỏ qua.
- Bấm Back không thoát nữa (màn hình chính).
- Không hiện chính nó trong lưới ứng dụng.
- Bản build debug có tên gói `org.lineageos.appdrawer`; bản release thêm hậu tố `.AppDrawer`.

Đặt làm launcher mặc định (sau khi cài):
`adb shell cmd package set-home-activity org.lineageos.appdrawer/org.lineageos.appdrawer.AppDrawerActivity`
(bản release: `org.lineageos.appdrawer.AppDrawer/org.lineageos.appdrawer.AppDrawerActivity`).
