package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CoKhiCheTao {

    public static void main(String[] args) {
// --- Cấu hình sẵn giá trị để test ---
        String inputImage = "phoi/phoiCoKhiCheTao.jpg"; // file JPG gốc
        String outputPdf = "E:\\HocTap\\LapTrinh\\CreateGiayTo\\output"; // file PDF kết quả
        String noiSinh = "Quảng Ninh "; // giới tính cần in
        String ngheDaoTao = "Cơ khí chế tạo";
        String toNghiepLoai = "Khá";
        String ngayThangNam = "12-10-2018";
        String capBangSo = "411";
        String fontPath = "font/timesbd.ttf"; // font hỗ trợ tiếng Việt
        String filePath = "input/phoiCoKhiCheTao.csv";
        Map<String, String> map = readCsvToMap(filePath);


        try {
            CoKhiCheTao coKhiCheTao = new CoKhiCheTao();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String name = entry.getKey();
                String ngaySinh = entry.getValue();
                CoKhiCheTao.createPdfWithImageAndStamp(
                        inputImage,
                        outputPdf,
                        fontPath,
                        name,
                        ngaySinh,
                        ngheDaoTao,
                        toNghiepLoai,
                        ngayThangNam,
                        noiSinh,
                        coKhiCheTao
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
                                                  String toNghiepLoai,
                                                  String ngayThangNam,
                                                  String noiSinh,
                                                  CoKhiCheTao coKhiCheTao
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
                contents.setFont(font, 37);


// --- Toạ độ trường "Tên English" ---

                float r = 56f/255f;
                float g = 56f/255f;
                float b = 56f/255f;

                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
//                contents.setNonStrokingColor(Color.black); // màu #3d3c34
                contents.newLineAtOffset(756, height - 762); // ví dụ: cách trái 120, cách trên 150
                contents.showText(removeVietnameseAccents(name));
                contents.endText();


// --- Toạ độ trường "Tên " ---
                contents.beginText();
//                contents.setNonStrokingColor(74f/255f, 74f/255f, 74f/255f); // màu #3d3c34
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1592, height - 758); // ví dụ: cách trái 120, cách trên 150
                contents.showText(name);
                contents.endText();




// --- Toạ độ trường "Ngày sinh" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(693, height - 825); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngaySinh);
                contents.endText();


// --- Toạ độ trường "Ngày sinh English" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1690, height - 822); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngaySinh);
                contents.endText();




                // --- Toạ độ trường "Nghề đào tạo English" ---
//                contents.beginText();
//                contents.newLineAtOffset(562, height - 623); // ví dụ: cách trái 400, cách trên 180
//                contents.showText("Mechanical engineering");
//                contents.endText();
//
                // --- Toạ độ trường "Gioi tinh" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(2172, height - 756); // ví dụ: cách trái 400, cách trên 180
                contents.showText("Nam");
                contents.endText();


                // --- Toạ độ trường "Xep loai tot nghiep English" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(737, height - 888); // ví dụ: cách trái 400, cách trên 180
                contents.showText("Good");
                contents.endText();


                // --- Toạ độ trường "Xep loai tot nghiep" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1736, height - 888); // ví dụ: cách trái 400, cách trên 180
                contents.showText("Khá");
                contents.endText();


                // --- Toạ độ trường "Hinh thuc dao tao english" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(700, height - 948); // ví dụ: cách trái 400, cách trên 180
                contents.showText("Full - time");
                contents.endText();



                // --- Toạ độ trường "Tốt nghiệp loại" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1709, height - 946); // ví dụ: cách trái 400, cách trên 180
                contents.showText("Chính quy");
                contents.endText();


                // --- Toạ độ trường "Ngay thang nam" ---
                Map<String,String > mapDate = generateDateMap();
                String dateEng = mapDate.get("EN");
                String dateVN = mapDate.get("VN");

                // --- Toạ độ trường "DATE ENGLISH" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(901, height - 1064); // ví dụ: cách trái 400, cách trên 180
                contents.showText(dateEng);
                contents.endText();

                // --- Toạ độ trường "DATE ENGLISH" ---
                String[] dateVNArr = dateVN.split("-");
                String day = dateVNArr[0];
                String month = dateVNArr[1];
                String year = dateVNArr[2];

                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1923, height - 1059); // ví dụ: cách trái 400, cách trên 180
                contents.showText(day);
                contents.endText();

                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(2068, height - 1059); // ví dụ: cách trái 400, cách trên 180
                contents.showText(month);
                contents.endText();

                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(2188, height - 1059); // ví dụ: cách trái 400, cách trên 180
                contents.showText(year);
                contents.endText();


                // --- Toạ độ trường "So hieu bang" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1560, height - 1331); // ví dụ: cách trái 400, cách trên 180
                contents.showText(soHieuBang());
                contents.endText();

              // --- Toạ độ trường "Vào so cap bang so" ---
                String capbangSo = capBangSo();
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1715, height - 1379); // ví dụ: cách trái 400, cách trên 180
                contents.showText(capbangSo);
                contents.endText();


              // --- Toạ độ trường "Reg. NO" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(476, height - 1380); // ví dụ: cách trái 400, cách trên 180
                contents.showText(capbangSo);
                contents.endText();


            }


//            File out = new File(buildPdfPath(outputPdfPath,name,ngayThangNam));
//            out.getParentFile().mkdirs();
//            document.save(out);

            String imagePath = coKhiCheTao.buildPdfPath(outputPdfPath,name,ngayThangNam);
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
    public int countSttName = 1;
    public  String buildPdfPath(String basePath, String name, String ngayThangNam) {
        // Bỏ dấu '-' trong ngày tháng năm -> 16022025
        String cleanDate = ngayThangNam.replace("-", "");

        // Ghép tên file mới
        String fileName =String.valueOf(countSttName)+"_" + name + "_" + cleanDate ;
        countSttName ++;
        // Trả về full path
        return Paths.get(basePath, fileName).toString();
    }

    public static Map<String, String> generateDateMap() {
        Random random = new Random();

        // Sinh ngày và tháng ngẫu nhiên
        int day = random.nextInt(28) + 1;    // 1-30
        int month = random.nextInt(12) + 1;  // 1-12
        int year = 2020;

        // Tạo LocalDate
        LocalDate date = LocalDate.of(year, month, day);

        // Định dạng
        DateTimeFormatter vnFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter enFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        // Tạo map kết quả
        Map<String, String> result = new HashMap<>();
        result.put("VN", date.format(vnFormatter));
        result.put("EN", date.format(enFormatter));

        return result;
    }

    public static String removeVietnameseAccents(String input) {
        if (input == null) return null;

        // Chuyển về Unicode chuẩn NFD (tách chữ và dấu)
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Loại bỏ các ký tự dấu
        temp = temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Thay các ký tự đặc biệt khác nếu muốn (ví dụ: đ -> d, Đ -> D)
        temp = temp.replaceAll("đ", "d");
        temp = temp.replaceAll("Đ", "D");

        return temp;
    }

    private static final Random RANDOM = new Random();

    public static String soHieuBang() {
        int number = RANDOM.nextInt(10000000); // random từ 0 -> 9999
        return "B"+String.format("%07d", number); // định dạng đủ 4 số
    }
    public static String capBangSo() {
        int number = RANDOM.nextInt(1000); // random từ 0 -> 9999
        int number2 = RANDOM.nextInt(100); // random từ 0 -> 9999
        return String.format("%04d", number)+"-K"+String.format("%02d", number2); // định dạng đủ 4 số
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