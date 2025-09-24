package org.example;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class coKhiThaiBinh {
    public int currentMonth = 1;
    public int countSttName = 1;

    public static void main(String[] args) {
// --- Cấu hình sẵn giá trị để test ---
        String inputImage = "phoi/coKhiThaiBinh.jpg"; // file JPG gốc
        String outputPdf = "E:\\HocTap\\LapTrinh\\CreateGiayTo\\output"; // file PDF kết quả
        String noiSinh = "Quảng Ninh "; // giới tính cần in
        String ngheDaoTao = "Cơ khí";
        String khoaHocTu = "10-2016";
        String khoaHocDen = "10-2018";
        String toNghiepLoai = "Khá";
        String ngayThangNam = "12-10-2018";
        String soHieuBang = "TC0714";
        String capBangSo = "714/TCN2DH";
        String fontPath = "font/timesbd.ttf"; // font hỗ trợ tiếng Việt

        String filePath = "input/coKhiThaiBinh.csv";
        Map<String, String> map = readCsvToMap(filePath);

        coKhiThaiBinh coKhiThaiBinh = new coKhiThaiBinh();

        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String name = entry.getKey();
                String ngaySinh = entry.getValue();
                coKhiThaiBinh.createPdfWithImageAndStamp(
                        inputImage,
                        outputPdf,
                        fontPath,
                        name,
                        ngaySinh,
                        ngheDaoTao,
                        khoaHocTu,
                        khoaHocDen,
                        toNghiepLoai,
                        ngayThangNam,
                        soHieuBang,
                        capBangSo,
                        noiSinh,
                        coKhiThaiBinh
                );
                System.out.println("Export success "+ name + "-" + ngaySinh);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create PDF: " + e.getMessage());
            System.exit(1);
        }
    }


    public static void createPdfWithImageAndStamp(String inputImagePath,
                                                  String outputPdfPath,
                                                  String fontFilePath,
                                                  String name,
                                                  String ngaySinh,
                                                  String ngheDaoTao,
                                                  String khoaHocTu,
                                                  String khoaHocDen,
                                                  String toNghiepLoai,
                                                  String ngayThangNam,
                                                  String soHieuBang,
                                                  String capBangSo,
                                                  String noiSinh,
                                                  coKhiThaiBinh coKhiThaiBinh
    ) throws IOException {

        File imageFile = new File(inputImagePath);
        if (!imageFile.exists()) throw new IOException("Input image not found: " + inputImagePath);


        BufferedImage bimg = ImageIO.read(imageFile);
        float width = bimg.getWidth();
        float height = bimg.getHeight();


        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);


// nhúng ảnh JPG vào PDF
            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imageFile, document);
            try (PDPageContentStream contents = new PDPageContentStream(document, page, AppendMode.APPEND, true)) {
                contents.drawImage(pdImage, 0, 0, width, height);
            }


// load font
            File fontFile = new File(fontFilePath);
            if (!fontFile.exists()) {
                throw new IOException("Font file not found: " + fontFilePath);
            }
            PDFont font = PDType0Font.load(document, fontFile);


// ghi text đè lên ảnh
            try (PDPageContentStream contents = new PDPageContentStream(document, page, AppendMode.APPEND, true)) {
                contents.setFont(font, 40);


// --- Toạ độ trường "Tên" ---
                contents.beginText();
                contents.setNonStrokingColor(74f/255f, 74f/255f, 74f/255f); // màu #3d3c34
                contents.newLineAtOffset(680, height - 1409); // ví dụ: cách trái 120, cách trên 150
                contents.showText(name);
                contents.endText();




// --- Toạ độ trường "Ngày sinh" ---
                contents.beginText();
                contents.newLineAtOffset(564, height - 1491); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngaySinh);
                contents.endText();


                // --- Toạ độ trường "Nơi sinh" ---
                contents.beginText();
                contents.newLineAtOffset(614, height - 1579); // ví dụ: cách trái 400, cách trên 180
                contents.showText(noiSinh);
                contents.endText();


                // --- Toạ độ trường "Nghề đào tạo" ---
                contents.beginText();
                contents.newLineAtOffset(684, height - 1660); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngheDaoTao);
                contents.endText();


                // --- Toạ độ trường "Khoa hoc tu thang" ---

                String[] khTuThangNam = coKhiThaiBinh.getNextMonthYear(2016).split("-");
                String khTuNgay = khTuThangNam[0];
                String khTuNam = khTuThangNam[1];
                contents.beginText();
                contents.newLineAtOffset(624, height - 1752); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khTuNgay);
                contents.endText();

                contents.beginText();
                contents.newLineAtOffset(824, height - 1752); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khTuNam);
                contents.endText();


                // --- Toạ độ trường "Khoa hoc den thang" ---
                String[] khdenThangNam = coKhiThaiBinh.getNextMonthYear(2018).split("-");
                String khDenNgay = khdenThangNam[0];
                String khDenNam = khdenThangNam[1];
                contents.beginText();
                contents.newLineAtOffset(1154, height - 1752); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khDenNgay);
                contents.endText();

                contents.beginText();
                contents.newLineAtOffset(1354, height - 1752); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khDenNam);
                contents.endText();


                // --- Toạ độ trường "Tốt nghiệp loại" ---
                contents.beginText();
                contents.newLineAtOffset(656, height - 1837); // ví dụ: cách trái 400, cách trên 180
                contents.showText(toNghiepLoai);
                contents.endText();


                // --- Toạ độ trường "Ngay thang nam" ---
                String[] ngayThangNamSTR = ngayThangNam.split("-");
                String ngay = ngayThangNamSTR[0];


                contents.beginText();
                contents.newLineAtOffset(968, height - 1986); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngay);
                contents.endText();

                 contents.beginText();
                contents.newLineAtOffset(1201, height - 1986); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khDenNgay);
                contents.endText();

                contents.beginText();
                contents.newLineAtOffset(1413, height - 1986); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khDenNam);
                contents.endText();


                // --- Toạ độ trường "So hieu bang" ---
                contents.beginText();
                contents.newLineAtOffset(536, height - 2317); // ví dụ: cách trái 400, cách trên 180
                contents.showText(soHieuBang());
                contents.endText();

              // --- Toạ độ trường "Vào so cap bang so" ---
                contents.beginText();
                contents.newLineAtOffset(625, height - 2404); // ví dụ: cách trái 400, cách trên 180
                contents.showText(capBangSo);
                contents.endText();


              // --- Toạ độ trường "Ngay thang nam o cuoicung" ---
                contents.beginText();
                contents.newLineAtOffset(337, height - 2489); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngay);
                contents.endText();

                contents.beginText();
                contents.newLineAtOffset(523, height - 2489); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khDenNgay);
                contents.endText();

                contents.beginText();
                contents.newLineAtOffset(693, height - 2489); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khDenNam);
                contents.endText();

            }


//            File out = new File(buildPdfPath(outputPdfPath,name,ngayThangNam));
//            out.getParentFile().mkdirs();
//            document.save(out);

            String imagePath = coKhiThaiBinh.buildPdfPath(outputPdfPath,name,ngayThangNam);
            // đảm bảo có đuôi .jpg
            if (!imagePath.toLowerCase().endsWith(".jpg")) {
                imagePath = imagePath + ".jpg";
            }

            File outFile = new File(imagePath);
            if (outFile.getParentFile() != null) {
                outFile.getParentFile().mkdirs();
            }

// render PDF thành ảnh
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bim = renderer.renderImageWithDPI(0, 300, ImageType.RGB); // trang 0, DPI 300

// lưu ra file JPG
            ImageIO.write(bim, "jpg", outFile);
        }


    }

    public String buildPdfPath(String basePath, String name, String ngayThangNam) {

        // Bỏ dấu '-' trong ngày tháng năm -> 16022025
        String cleanDate = ngayThangNam.replace("-", "");

        // Ghép tên file mới
        String fileName = countSttName + "_"+name + "_" + cleanDate;
        countSttName ++;
        // Trả về full path
        return Paths.get(basePath, fileName).toString();
    }

    public String getNextMonthYear(int year) {
        // Format tháng 2 chữ số
        String result = String.format("%02d-%d", currentMonth, year);

        // Tăng tháng cho lần gọi tiếp theo
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1; // quay lại tháng 1
        }

        return result;
    }
    private static final String PREFIX = "TC";
    private static final Random RANDOM = new Random();

    public static String soHieuBang() {
        int number = RANDOM.nextInt(10000); // random từ 0 -> 9999
        return PREFIX + String.format("%04d", number); // định dạng đủ 4 số
    }

    public static Map<String, String> readCsvToMap(String filePath) {
        Map<String, String> dataMap = new LinkedHashMap<>(); // giữ nguyên thứ tự đọc
        String splitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // bỏ dòng trống

                String[] parts = line.split(splitBy);
                if (parts.length >= 2) {
                    String name = parts[0].replace("\uFEFF", "").trim(); // loại bỏ BOM
                    String dob  = parts[1].trim();

                    dataMap.put(name.toUpperCase(), dob);
                } else {
                    System.err.println("⚠️ Bỏ qua dòng sai định dạng: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataMap;
    }


}