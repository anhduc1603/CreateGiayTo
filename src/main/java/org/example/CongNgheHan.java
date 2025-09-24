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
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class CongNgheHan {
    public static int currentMonth = 1;

    public static void main(String[] args) {
// --- Cấu hình sẵn giá trị để test ---
        String inputImage = "phoi/congNgheHan.jpg"; // file JPG gốc
        String outputPdf = "E:\\HocTap\\LapTrinh\\CreateGiayTo\\output"; // file PDF kết quả
        String noiSinh = "Quảng Ninh "; // giới tính cần in
        String ngheDaoTao = "Công nghệ hàn";
        String toNghiepLoai = "Khá";
        String ngayThangNam = "12-10-2018";
        String capBangSo = "411";
        String fontPath = "font/timesbd.ttf"; // font hỗ trợ tiếng Việt
        String filePath = "input/congNgheHan.csv";
        Map<String, String> map = readCsvToMap(filePath);
        CongNgheHan coKhiLapRap = new CongNgheHan();
        int stt = 1;
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String name = entry.getKey();
                String ngaySinh = entry.getValue();
                CongNgheHan.createPdfWithImageAndStamp(
                        inputImage,
                        outputPdf,
                        fontPath,
                        name,
                        ngaySinh,
                        ngheDaoTao,
                        toNghiepLoai,
                        ngayThangNam,
                        noiSinh,
                        coKhiLapRap
                );
                System.out.println(stt+"-"+"Export success "+ name + "-" + ngaySinh);
                stt++;
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
                                                  CongNgheHan coKhiLapRap
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
                contents.setFont(font, 43);



// --- Toạ độ trường "Tên " ---
                float r = 18f/255f;
                float g = 18f/255f;
                float b = 18f/255f;
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(613, height - 1391); // ví dụ: cách trái 120, cách trên 150
                contents.showText(name);
                contents.endText();




// --- Toạ độ trường "Ngày sinh" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(566, height - 1475); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngaySinh);
                contents.endText();



                // --- Toạ độ trường "Noi Sinh" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(558, height - 1555); // ví dụ: cách trái 400, cách trên 180
                contents.showText(noiSinh);
                contents.endText();

                // --- Toạ độ trường "Nghề đào tạo " ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(664, height - 1628); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngheDaoTao);
                contents.endText();



                // --- Toạ độ trường "Khoa hoc tu thang" ---
                String ngayThangNamSTR = getNextMonthYear(2015);
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(679, height - 1705); // ví dụ: cách trái 400, cách trên 180
                contents.showText(ngayThangNamSTR);
                contents.endText();

                // --- Toạ độ trường "Khoa hoc den thang" ---
                String khoaHocDenThang = getNextMonthYear(2017);
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1149, height - 1704); // ví dụ: cách trái 400, cách trên 180
                contents.showText(khoaHocDenThang);
                contents.endText();

                // --- Toạ độ trường "Tot nghiep loai" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(704, height - 1786); // ví dụ: cách trái 400, cách trên 180
                contents.showText(toNghiepLoai);
                contents.endText();

             // --- Toạ độ trường "Noi cap Quang Ninh" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(594, height - 1917); // ví dụ: cách trái 400, cách trên 180
                contents.showText("Quảng Ninh");
                contents.endText();



                // --- Toạ độ trường "Ngay" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(970, height - 1913); // ví dụ: cách trái 400, cách trên 180
                contents.showText("15");
                contents.endText();


                // --- Toạ độ trường "Thang" ---
                String[] dateTimeArr = khoaHocDenThang.split("/");
                String month = dateTimeArr[0];
                String year = dateTimeArr[1];
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1183, height - 1914); // ví dụ: cách trái 400, cách trên 180
                contents.showText(month);
                contents.endText();


                // --- Toạ độ trường "Nam" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(1379, height - 1912); // ví dụ: cách trái 400, cách trên 180
                contents.showText(year);
                contents.endText();

               // --- Toạ độ trường "Số hiệu bằng" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(616, height - 2194); // ví dụ: cách trái 400, cách trên 180
                contents.showText(soHieuBang());
                contents.endText();

             // --- Toạ độ trường "Vào sổ cấp bằng số" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(693, height - 2272); // ví dụ: cách trái 400, cách trên 180
                contents.showText(capBangSo());
                contents.endText();



                // --- Toạ độ trường "Ngay" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(413, height - 2359); // ví dụ: cách trái 400, cách trên 180
                contents.showText("15");
                contents.endText();


                // --- Toạ độ trường "Thangg" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(601, height - 2358); // ví dụ: cách trái 400, cách trên 180
                contents.showText(month);
                contents.endText();


                // --- Toạ độ trường "Ngay" ---
                contents.beginText();
                contents.setNonStrokingColor(r,g , b);
                contents.newLineAtOffset(768, height - 2355); // ví dụ: cách trái 400, cách trên 180
                contents.showText(year);
                contents.endText();


            }


//            File out = new File(buildPdfPath(outputPdfPath,name,ngayThangNam));
//            out.getParentFile().mkdirs();
//            document.save(out);

            String imagePath = coKhiLapRap.buildPdfPath(outputPdfPath,name,ngayThangNam);
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
    public String buildPdfPath(String basePath, String name, String ngayThangNam) {
        // Bỏ dấu '-' trong ngày tháng năm -> 16022025
        String cleanDate = ngayThangNam.replace("-", "");

        // Ghép tên file mới
        String fileName =String.valueOf(countSttName)+"_" + name + "_" + cleanDate ;
        countSttName++;
        // Trả về full path
        return Paths.get(basePath, fileName).toString();
    }

    public static String getNextMonthYear(int year) {
        // Format tháng 2 chữ số
        String result = String.format("%02d/%d", currentMonth, year);

        // Tăng tháng cho lần gọi tiếp theo
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1; // quay lại tháng 1
        }

        return result;
    }
    private static final Random RANDOM = new Random();

    public static String soHieuBang() {
        int number = RANDOM.nextInt(10000); // random từ 0 -> 9999
        return "B"+String.format("%05d", number); // định dạng đủ 4 số
    }
    public static String capBangSo() {
        int number = RANDOM.nextInt(1000); // random từ 0 -> 9999
        return String.format("%03d", number); // định dạng đủ 4 số
    }

    public static Map<String, String> generateDateMap() {
        Random random = new Random();

        // Sinh ngày và tháng ngẫu nhiên
        int day = random.nextInt(30) + 1;    // 1-30
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