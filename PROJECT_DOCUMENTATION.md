# Tài liệu Thiết kế, Tối ưu và Kiểm thử Dự án Sale Management System

## 1. Tổng quan Dự án
**Sale Management System** là ứng dụng quản lý bán hàng (Console Application) viết bằng ngôn ngữ Java. Hệ thống tập trung vào việc quản lý Sản phẩm (Product), Khách hàng (Customer), Giao dịch (Transaction) và cung cấp các Báo cáo thống kê (Report) chuyên sâu.

Dự án được xây dựng dựa trên nguyên lý hướng đối tượng (OOP) chặt chẽ, tách biệt các tầng xử lý (Model - Manager - View) giúp dễ dàng bảo trì và mở rộng.

---

## 2. Kiến trúc và Tối ưu hóa Code (Code Optimization & Design)

Hệ thống được thiết kế với các quyết định tối ưu hóa cụ thể nhằm đảm bảo hiệu suất, tính đúng đắn của dữ liệu và dễ dàng nâng cấp trong tương lai:

### 2.1. Tối ưu hóa bằng Cấu trúc dữ liệu (Data Structures)
* **`ArrayList` cho việc lưu trữ chính**: Các danh sách sản phẩm, khách hàng và giao dịch được quản lý bằng `ArrayList`. Vì đây là hệ thống chạy trên Console, việc đọc/duyệt dữ liệu diễn ra thường xuyên hơn là chèn ở giữa danh sách. `ArrayList` cung cấp thời gian truy cập $O(1)$ qua index và duyệt rất nhanh do dữ liệu nằm liên tiếp trên bộ nhớ.
* **`HashMap` cho các xử lý Báo cáo và Thống kê**: 
  * Trong các nghiệp vụ tính toán báo cáo như *Top Customers* hay *Best Selling Products*, hệ thống sử dụng `HashMap<Product, Integer>` và `HashMap<Customer, Double>`.
  * **Lý do chọn**: Thay vì phải viết các vòng lặp lồng nhau phức tạp với độ phức tạp $O(n^2)$ để đếm số lượng bán ra của từng sản phẩm, `HashMap` cho phép nhóm (group by) và cộng dồn dữ liệu với thời gian $O(1)$ cho mỗi phép tra cứu/cập nhật, giúp các báo cáo xuất ra ngay lập tức dù số lượng giao dịch lớn.

### 2.2. Áp dụng các nguyên lý OOP (Object-Oriented Programming)
* **Polymorphism (Đa hình) trong Giao dịch & Khách hàng**:
  * Hàm `calculateTotal()` được định nghĩa khác biệt giữa `InStoreTransaction` (áp dụng chiết khấu) và `OnlineTransaction` (cộng thêm phí vận chuyển). Nhờ tính đa hình, lớp `TransactionManager` khi tính tổng doanh thu chỉ cần gọi `t.calculateTotal()` mà không cần quan tâm (không cần dùng if-else `instanceof`) đó là loại giao dịch nào.
  * Việc hiển thị danh sách (`displayInfo()`) cũng được đa hình hóa, tự động gọi đúng phương thức của lớp con giúp giảm thiểu code trùng lặp.
* **Encapsulation (Đóng gói) & Validation dữ liệu**:
  * Mọi thuộc tính đều là `private`. Việc thay đổi trạng thái (như trừ số lượng tồn kho `stockQuantity`, thay đổi điểm VIP) đều được đưa vào các phương thức có kiểm tra hợp lệ (Validation).
  * Ví dụ: Không thể set số lượng tồn kho `< 0`, không thể đặt discount ngoài khoảng `0.0 - 1.0`.

### 2.3. Quản lý Lỗi (Exception Handling)
* **Sử dụng RuntimeExceptions cụ thể**: Thay vì trả về `boolean` (`true/false`) khi một tác vụ thất bại (như tạo giao dịch sai ID, không tìm thấy sản phẩm), hệ thống ném ra các lỗi `IllegalArgumentException` hoặc `IllegalStateException` kèm theo message rõ ràng.
* **Lý do chọn**: Giúp tách biệt logic kiểm tra lỗi (ở tầng Manager/Model) khỏi logic giao diện (tầng View), đảm bảo lỗi không bị bỏ qua âm thầm (fail-fast) và UI có thể bắt lỗi bằng block `try-catch` để hiển thị thông báo thân thiện.

### 2.4. Soft Delete (Xóa mềm)
* Việc xóa sản phẩm không dùng lệnh `.remove()` khỏi danh sách mà sử dụng cơ chế đổi trạng thái (`active = false`).
* **Lý do chọn**: Đảm bảo tính toàn vẹn dữ liệu (Data Integrity). Các giao dịch cũ đã tham chiếu đến sản phẩm đó sẽ không bị lỗi khi in báo cáo doanh thu hay lịch sử mua hàng, giúp ứng dụng không bao giờ bị crash do "NullPointerException" khi truy xuất lịch sử.

---

## 3. Kịch bản Kiểm thử chi tiết (Test Cases)

Dưới đây là các Test Case chi tiết từ end-to-end giúp bạn kiểm thử toàn bộ hệ thống.

### Phân hệ 1: Quản lý Sản phẩm (Product Management)
| Test Case ID | Hành động | Input giả định | Kết quả mong đợi |
|---|---|---|---|
| **TC-PROD-01** | Thêm sản phẩm mới hợp lệ | ID: P100, Tên: Bàn phím cơ, Danh mục: Phụ kiện, Giá: 1500000, Tồn: 10 | Thêm thành công. |
| **TC-PROD-02** | Thêm sản phẩm trùng ID | ID: P100 (đã có ở trên) | Báo lỗi `IllegalArgumentException` hoặc hiển thị thông báo "ID đã tồn tại". |
| **TC-PROD-03** | Validate giá trị âm | Giá: -50000 hoặc Tồn: -5 | Báo lỗi dữ liệu đầu vào không hợp lệ. |
| **TC-PROD-04** | Cập nhật tồn kho (Update) | Cập nhật P100 tồn kho thành 4 | Tồn kho mới là 4. Nếu xem báo cáo `Low Stock`, P100 phải xuất hiện vì tồn kho < 5. |
| **TC-PROD-05** | Xóa mềm sản phẩm | Chọn tính năng Xóa, nhập P100 | Sản phẩm P100 bị đổi thành `inactive`. Khi chọn "Xem tất cả", sản phẩm sẽ không hiển thị (hoặc đánh dấu là inactive). |

### Phân hệ 2: Quản lý Khách hàng (Customer Management)
| Test Case ID | Hành động | Input giả định | Kết quả mong đợi |
|---|---|---|---|
| **TC-CUST-01** | Thêm khách hàng mới | Tên: Tran B, SĐT: 0912345678, Địa chỉ: Hanoi | Thêm thành công, loại là Regular Customer. |
| **TC-CUST-02** | Validate SĐT khách hàng | SĐT: 09123 (quá ngắn) | Báo lỗi sai định dạng SĐT (phải đủ 10 số). |
| **TC-CUST-03** | Tự động nâng hạng VIP | Mua hàng cho KH trên và confirm > 5 giao dịch | Xem thông tin KH, hạng sẽ được tự động đổi thành **SILVER**. |

### Phân hệ 3: Quản lý Giao dịch (Transaction Management) - QUAN TRỌNG
| Test Case ID | Hành động | Input giả định | Kết quả mong đợi |
|---|---|---|---|
| **TC-TRAN-01** | Tạo In-Store Transaction | Mua P01 (10tr), Discount: 10% (0.1), Người duyệt: Admin | Tạo thành công (Trạng thái Pending). Tổng tiền = 9 triệu. |
| **TC-TRAN-02** | Tạo Online Transaction | Mua P01, Shipping: 50000 VND | Tạo thành công. Tổng tiền = Giá P01 + 50000. |
| **TC-TRAN-03** | Thêm sản phẩm vượt số lượng tồn | Thêm 20 cái P01 (trong khi tồn chỉ có 10) | Hệ thống chặn lại báo lỗi không đủ số lượng tồn kho. |
| **TC-TRAN-04** | Xác nhận giao dịch (Confirm) | Chọn Confirm giao dịch TC-TRAN-01 | Giao dịch đổi thành **Confirmed**. Tồn kho của P01 bị trừ đi tương ứng số lượng đã mua. |
| **TC-TRAN-05** | Chỉnh sửa sau khi Confirm | Sửa số lượng sản phẩm trong giao dịch TC-TRAN-01 | Hệ thống chặn: Không được sửa giao dịch đã Confirmed. |
| **TC-TRAN-06** | Hủy giao dịch (Cancel) | Đặt 1 giao dịch mới rồi Cancel | Trạng thái thành **Cancelled**. Tồn kho KHÔNG bị trừ. |

### Phân hệ 4: Báo cáo Thống kê (Reports)
*Để test phần này, hãy chắc chắn đã chạy qua các Test Case giao dịch (Confirm thành công vài đơn hàng).*

| Test Case ID | Hành động | Kết quả mong đợi |
|---|---|---|
| **TC-REP-01** | Xem Top Selling Products | Danh sách các sản phẩm bán chạy nhất, sắp xếp theo số lượng đã được bán (nhờ sử dụng `HashMap` cộng dồn). |
| **TC-REP-02** | Xem Top Customers | Danh sách các khách hàng chi nhiều tiền nhất, xếp từ cao xuống thấp. |
| **TC-REP-03** | Xem Daily/Monthly Revenue | Tính chính xác tổng tiền từ các giao dịch đã **Confirmed** trong khoản thời gian đó. Giao dịch Cancelled/Pending không được cộng vào. |
| **TC-REP-04** | Xem Inactive Products | Các sản phẩm đã bị xóa ở Test Case `TC-PROD-05` sẽ xuất hiện ở danh sách này. |

---

## 4. Hướng dẫn cách Test toàn bộ dự án từ A-Z
1. **Khởi động ứng dụng**: Chạy ứng dụng từ NetBeans hoặc dòng lệnh. (Chắc chắn dữ liệu mẫu đã được nạp).
2. **Kiểm tra tính Validation**: Cố tình nhập sai kiểu dữ liệu (chữ thay vì số cho giá cả), nhập số lượng âm, ID bị trùng xem chương trình có hiển thị đúng câu thông báo lỗi hay bị crash (văng lỗi đỏ lòm).
3. **Thực hiện một luồng mua hàng trọn vẹn**:
   - Vào `Manager Products` -> Tạo 1 sản phẩm "Iphone 15" tồn kho: 10 cái.
   - Vào `Manager Customers` -> Tạo 1 khách hàng mới tên "Nguyen Van Test".
   - Vào `Manager Transactions`:
     - Chọn `Create Online Transaction` cho khách hàng vừa tạo.
     - Add sản phẩm "Iphone 15" với số lượng: 2.
     - Xác nhận đơn hàng (`Confirm transaction`).
4. **Kiểm chứng Data thay đổi**:
   - Quay lại `Manager Products` -> View lại "Iphone 15", số lượng tồn kho lúc này **phải là 8** (vì đã trừ 2).
   - Vào `Reports` -> Chọn `Daily Revenue`, số tiền bán 2 iPhone 15 cộng phí ship phải xuất hiện.
5. **Thử nghiệm tính năng Cancel**: Tạo thêm 1 giao dịch In-store nhưng lần này chọn `Cancel transaction`. Kiểm tra lại kho xem sản phẩm có bị trừ nhầm không (Kỳ vọng: Không bị trừ).

*Với tài liệu này, bạn có thể giải thích cực kỳ cặn kẽ về độ chuyên nghiệp trong cấu trúc code của dự án cũng như test không bỏ sót bất kỳ tính năng nào.*
