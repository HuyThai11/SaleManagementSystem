# 📦 Sale Management System — Mô tả tính năng

## Tổng quan

**Sale Management System** là ứng dụng quản lý bán hàng dạng Console (CLI) được xây dựng bằng Java. Hệ thống hỗ trợ quản lý sản phẩm, khách hàng, giao dịch mua bán (tại cửa hàng & online), và tạo các báo cáo thống kê kinh doanh.

---

## Kiến trúc hệ thống

```
src/
├── model/       → Các lớp dữ liệu (Entity)
├── manager/     → Các lớp xử lý nghiệp vụ (Business Logic)
├── report/      → Dịch vụ báo cáo & thống kê
└── view/        → Giao diện người dùng (Console Menu)
```

### Sơ đồ kế thừa (Inheritance)

```
Person (abstract)
  └── Customer (abstract)
        ├── RegularCustomer
        └── VIPCustomer → sử dụng VIPTier (enum)

Transaction (abstract)
  ├── InStoreTransaction
  └── OnlineTransaction
```

---

## 1. 🛍️ Quản lý sản phẩm (Product Management)

### Các tính năng

| Chức năng | Mô tả |
|-----------|--------|
| **Thêm sản phẩm** | Tạo sản phẩm mới với ID, tên, danh mục, giá, số lượng tồn kho |
| **Cập nhật sản phẩm** | Chỉnh sửa tên, danh mục, giá, số lượng tồn kho theo Product ID |
| **Xóa sản phẩm** | Vô hiệu hóa sản phẩm (soft delete — đánh dấu `inactive`, không xóa khỏi hệ thống) |
| **Xem tất cả sản phẩm** | Hiển thị danh sách toàn bộ sản phẩm dạng bảng |
| **Tìm kiếm sản phẩm** | Tìm theo ID, tên, hoặc danh mục (không phân biệt hoa/thường) |

### Thuộc tính sản phẩm

- `productId` — Mã sản phẩm (bắt buộc, không rỗng)
- `productName` — Tên sản phẩm
- `category` — Danh mục
- `price` — Giá (> 0)
- `stockQuantity` — Số lượng tồn kho (≥ 0)
- `active` — Trạng thái hoạt động (true/false)
- `priceHistory` — Lịch sử thay đổi giá (tự động ghi nhận khi giá thay đổi)

### Tính năng nâng cao

- **Kiểm tra tồn kho thấp**: Sản phẩm có `stockQuantity < 5` được đánh dấu là "Low Stock"
- **Lịch sử giá**: Mỗi lần cập nhật giá, giá cũ tự động được lưu vào `priceHistory`
- **Soft Delete**: Sản phẩm bị xóa chỉ chuyển sang trạng thái `inactive`, không bị xóa vĩnh viễn
- **Trùng ID**: Không cho phép thêm sản phẩm có ID đã tồn tại và đang active

---

## 2. 👥 Quản lý khách hàng (Customer Management)

### Các tính năng

| Chức năng | Mô tả |
|-----------|--------|
| **Thêm khách hàng** | Thêm khách hàng mới (mặc định là Regular Customer) |
| **Cập nhật khách hàng** | Chỉnh sửa tên, số điện thoại, địa chỉ |
| **Xóa khách hàng** | Xóa khách hàng khỏi danh sách |
| **Xem tất cả khách hàng** | Hiển thị danh sách toàn bộ khách hàng (polymorphic display) |

### Phân loại khách hàng

#### Regular Customer (Khách hàng thường)
- Không được giảm giá (`discountRate = 0%`)

#### VIP Customer (Khách hàng VIP)
- Được giảm giá theo hạng VIP
- Tự động nâng hạng khi đạt đủ số giao dịch đã xác nhận

### Hệ thống VIP Tier

| Hạng VIP | Chiết khấu | Số giao dịch yêu cầu |
|----------|-----------|----------------------|
| 🥈 **SILVER** | 5% | ≥ 5 giao dịch |
| 🥇 **GOLD** | 10% | ≥ 10 giao dịch |
| 💎 **PLATINUM** | 15% | ≥ 20 giao dịch |

### Tính năng nâng cao

- **Nâng cấp lên VIP**: Cho phép chuyển Regular Customer thành VIP Customer với hạng chỉ định
- **Tự động nâng hạng**: Khi xác nhận giao dịch, hệ thống tự động kiểm tra và nâng hạng VIP nếu đủ điều kiện
- **Thông tin hạng tiếp theo**: Hiển thị số giao dịch cần thiết để đạt hạng VIP tiếp theo
- **Xác thực số điện thoại**: Phải đúng 10 chữ số

---

## 3. 💳 Quản lý giao dịch (Transaction Management)

### Các tính năng

| Chức năng | Mô tả |
|-----------|--------|
| **Tạo giao dịch tại cửa hàng** | Tạo In-Store Transaction (có thể áp dụng chiết khấu) |
| **Tạo giao dịch online** | Tạo Online Transaction (có phí vận chuyển) |
| **Thêm sản phẩm vào giao dịch** | Thêm sản phẩm với số lượng chỉ định |
| **Cập nhật số lượng** | Thay đổi số lượng sản phẩm trong giao dịch |
| **Xóa sản phẩm khỏi giao dịch** | Loại bỏ sản phẩm ra khỏi giao dịch |
| **Xác nhận giao dịch** | Hoàn tất giao dịch, trừ tồn kho |
| **Hủy giao dịch** | Đánh dấu giao dịch bị hủy |
| **Xem lịch sử giao dịch** | Hiển thị toàn bộ giao dịch đã tạo |

### Loại giao dịch

#### 🏪 In-Store Transaction (Giao dịch tại cửa hàng)
- Có thể áp dụng **chiết khấu trực tiếp** (discount rate: 0% – 100%)
- Nếu có chiết khấu, bắt buộc phải có **người phê duyệt** (`approvedBy`)
- **Công thức**: `Total = Subtotal - (Subtotal × discountRate)`

#### 🌐 Online Transaction (Giao dịch trực tuyến)
- Yêu cầu **địa chỉ giao hàng** (`shippingAddress`)
- Phí vận chuyển mặc định: **30,000 VND** (có thể tùy chỉnh)
- **Công thức**: `Total = Subtotal + shippingFee`

### Trạng thái giao dịch

```
Pending ──→ Confirmed (xác nhận thành công)
   │
   └──────→ Cancelled (bị hủy)
```

- **Pending**: Mới tạo, có thể chỉnh sửa
- **Confirmed**: Đã xác nhận, không thể chỉnh sửa, tồn kho đã bị trừ
- **Cancelled**: Đã hủy, không thể chỉnh sửa

### Validation khi xác nhận

- Giao dịch phải có ít nhất 1 sản phẩm
- Tất cả sản phẩm phải ở trạng thái `active`
- Phải đủ tồn kho cho mỗi sản phẩm
- Tự động trừ tồn kho sau khi xác nhận
- Tự động cập nhật hạng VIP (nếu khách hàng là VIP)

---

## 4. 📊 Báo cáo & Thống kê (Reports)

Hệ thống cung cấp **13 loại báo cáo** chi tiết:

### Báo cáo doanh thu

| # | Báo cáo | Mô tả |
|---|---------|--------|
| 1 | **Daily Revenue** | Doanh thu theo ngày (nhập ngày dd/MM/yyyy) |
| 2 | **Monthly Revenue** | Doanh thu theo tháng/năm |
| 6 | **Revenue After Discount** | Chi tiết doanh thu sau chiết khấu cho từng giao dịch |
| 8 | **Revenue by Customer Type** | So sánh doanh thu giữa Regular và VIP Customer (số lượng, tỷ lệ %) |
| 9 | **Total Discount Given** | Tổng chiết khấu đã áp dụng cho giao dịch In-Store |

### Báo cáo sản phẩm

| # | Báo cáo | Mô tả |
|---|---------|--------|
| 3 | **Best Selling Products** | Xếp hạng sản phẩm bán chạy nhất theo số lượng |
| 10 | **Low Stock Products** | Danh sách sản phẩm có tồn kho < 5 |
| 11 | **Active Products** | Danh sách sản phẩm đang hoạt động |
| 12 | **Inactive Products** | Danh sách sản phẩm đã ngừng hoạt động |
| 13 | **Product Price History** | Lịch sử thay đổi giá của từng sản phẩm |

### Báo cáo khách hàng

| # | Báo cáo | Mô tả |
|---|---------|--------|
| 4 | **Top Customers** | Xếp hạng khách hàng chi tiêu nhiều nhất |
| 5 | **VIP Customer List** | Danh sách khách hàng VIP kèm hạng và mức chiết khấu |
| 7 | **VIP Tier Distribution** | Phân bố số lượng khách VIP theo từng hạng (Silver/Gold/Platinum) |

---

## 5. ⚙️ Tính năng kỹ thuật

### OOP Principles được áp dụng

| Nguyên tắc | Áp dụng |
|------------|---------|
| **Abstraction** | `Person`, `Customer`, `Transaction` là abstract class |
| **Inheritance** | `RegularCustomer`, `VIPCustomer` kế thừa `Customer`; `InStoreTransaction`, `OnlineTransaction` kế thừa `Transaction` |
| **Polymorphism** | `displayInfo()`, `calculateTotal()`, `calculateDiscount()`, `getCustomerType()` |
| **Encapsulation** | Tất cả fields đều `private`, truy cập qua getter/setter có validation |

### Data Validation

- Tất cả input đều được kiểm tra trước khi xử lý
- Số điện thoại phải đúng 10 chữ số
- Giá sản phẩm > 0, tồn kho ≥ 0
- Discount rate trong khoảng 0.0 – 1.0
- Shipping fee ≥ 0
- Các ID và tên không được để trống

### Error Handling

- Sử dụng `IllegalArgumentException` cho lỗi dữ liệu đầu vào
- Sử dụng `IllegalStateException` cho lỗi trạng thái nghiệp vụ
- Input console có try-catch cho `NumberFormatException`

---

## 6. 📋 Menu hệ thống

```
========== SaleManagement ==========
1. Manager Products
   ├── 1. Add product
   ├── 2. Update product
   ├── 3. Remove product
   ├── 4. View all products
   └── 5. Search products

2. Manager Customers
   ├── 1. Add customer
   ├── 2. Update customer
   ├── 3. Remove customer
   └── 4. View all customers

3. Manager Transactions
   ├── 1. Create In-Store Transaction
   ├── 2. Create Online Transaction
   ├── 3. Add product to transaction
   ├── 4. Update product quantity
   ├── 5. Remove product from transaction
   ├── 6. Confirm transaction
   ├── 7. Cancel transaction
   └── 8. View transaction history

4. Reports
   ├── 1. Daily Revenue
   ├── 2. Monthly Revenue
   ├── 3. Best Selling Products
   ├── 4. Top Customers
   ├── 5. VIP Customer List
   ├── 6. Revenue After Discount
   ├── 7. VIP Tier Distribution
   ├── 8. Revenue by Customer Type
   ├── 9. Total Discount Given
   ├── 10. Low Stock Products
   ├── 11. Active Products
   ├── 12. Inactive Products
   └── 13. Product Price History

5. Exit
```

---

## 7. 📦 Dữ liệu mẫu (Sample Data)

Hệ thống tự động tạo dữ liệu mẫu khi khởi chạy:

| Loại | ID | Tên | Chi tiết |
|------|----|-----|----------|
| Product | P01 | Laptop | Electronics, 15,000,000 VND, Stock: 10 |
| Product | P02 | Mouse | Accessories, 200,000 VND, Stock: 30 |
| Customer | C01 | Nguyen Van A | SĐT: 0901234567, Địa chỉ: HCM |

---

## 8. 🛠️ Công nghệ sử dụng

- **Ngôn ngữ**: Java (Console Application)
- **IDE**: NetBeans
- **Build Tool**: Apache Ant (`build.xml`)
- **Data Storage**: In-memory (ArrayList, HashMap)
- **Version Control**: Git
