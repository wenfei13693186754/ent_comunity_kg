package com.kg.platform.util;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class FileUtils {

    private static final Logger log = Logger.getLogger(FileUtils.class.getName());

    public static void main(String[] args) {
        FileUtils.filesParse("E:\\知识图谱\\知识图谱.doc");
    }

    /**
     * 创建多级文件夹
     *
     * @param path 待创建文件夹路径；
     * @return 创建是否成功
     */
    public static boolean createFileDirec(String path) {
        File file = new File(path);
        boolean success = false;
        try {
            if (!file.exists()) {
                success = file.mkdirs();
            } else {
                success = true;
                log.info(path + "已经存在");
            }
        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
        }
        log.info(path + "创建成功");
        return success;
    }

    /**
     * 创建文件并写入给定内容。可以同时创建多级文件夹
     *
     * @param fileName 待创建文件路径，如a/a.txt
     * @param info     待写入到新创建文件中的信息
     */
    public static void createFile(String fileName, String info) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                String parentPath = fileName.substring(0, fileName.lastIndexOf("/"));
                new File(parentPath).mkdirs();
                file.createNewFile();
            }
            FileWriter rf = new FileWriter(file);
            PrintWriter pw = new PrintWriter(rf);
            pw.println(info);
            rf.close();
        } catch (Exception e) {
            System.out.println("新建文件操作出错");
            e.printStackTrace();
        }
    }


    /**
     * 删除文件
     *
     * @param fileName 待删除文件名
     * @return 删除是否成功
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        boolean deleted = false;
        try {
            if (file.exists()) {
                deleted = file.delete();
            } else {
                log.warning(fileName + "不存在");
                deleted = true;
            }
        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
        }
        return deleted;
    }

    /**
     * 从文本文件中读取数据
     *
     * @param path 文件路径
     * @return 文件内容
     */
    public static Stream<String> readDataByLine(String path) {
        log.info("从文件中读取数据。。。");
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(path), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("从文件中读取数据完成");
        return lines;
    }

    /**
     * 一次性读取文件中所有内容
     *
     * @param fileName
     * @return
     */
    public static String readAllData(String fileName) {
        String encoding = "utf-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        String str = "";
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            str = new String(filecontent, encoding);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * read file【读取文件内容】
     * 读取的是文件的全部内容，但是是将每行进行了拼接，形成了StringBuilder返回的
     *
     * @param filePath：文件路径
     * @param charsetName：The name of a supported {@link Charset </code>charset<code>}指定编码集
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);//构造一个指定编码集的InputStreamReader类
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                //如果已读取的文本内容不为空，则每读取一行则在本行的末尾添加一个换行符
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 结果输出到文件
     *
     * @param segInfo 结果
     * @param outPath 文件路径
     */
    public static void sendDataToDoc(JSONObject segInfo, String outPath) {
        Path path = Paths.get(outPath);
        try {
            if (!Files.exists(path))
                Files.createFile(path);
            BufferedWriter bw = Files.newBufferedWriter(path);
            segInfo.forEach((x, y) -> {
                try {
                    bw.write(x + "    " + y + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bw.flush();
            bw.close();
        } catch (IOException e) {
            log.warning("数据输出到文件失败");
            e.printStackTrace();
        }
        log.info("数据输出到文件完成");
    }


    /**
     * 文件属性读取，测试用
     *
     * @param filePath 文件路径
     */
    private void readFileProperty(String filePath) {
        // 文件属性的取得
        File f = new File(filePath);
        if (f.exists()) {
            System.out.println(f.getName() + "的属性如下： 文件长度为：" + f.length());
            System.out.println(f.isFile() ? "是文件" : "不是文件");
            System.out.println(f.isDirectory() ? "是目录" : "不是目录");
            System.out.println(f.canRead() ? "可读取" : "不");
            System.out.println(f.canWrite() ? "是隐藏文件" : "");
            System.out.println("文件夹的最后修改日期为：" + new Date(f.lastModified()));
        } else {
            System.out.println(f.getName() + "的属性如下：");
            System.out.println(f.isFile() ? "是文件" : "不是文件");
            System.out.println(f.isDirectory() ? "是目录" : "不是目录");
            System.out.println(f.canRead() ? "可读取" : "不");
            System.out.println(f.canWrite() ? "是隐藏文件" : "");
            System.out.println("文件的最后修改日期为：" + new Date(f.lastModified()));
        }
    }


    /**
     * 写入属性，测试用
     *
     * @param filePath 文件路径
     */
    private void writeFileProperty(String filePath) {
        File filereadonly = new File(filePath);
        try {
            boolean b = filereadonly.setReadOnly();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从给定的excel文件中读取数据，返回指定sheet
     * 注意：excel需要为xls格式的
     *
     * @param path      excel文件路径
     * @param sheetName 要读取的excel的sheet名称
     * @return excel的对应sheetName的Sheet对象
     */
    public static Sheet readDataFromExcel(String path, String sheetName) {
        // 创建输入流，读取Excel
        InputStream is = null;
        // Sheet对象
        Sheet sheet = null;
        // jxl提供的Workbook类
        Workbook wb = null;
        try {
            is = new FileInputStream(new File(path).getAbsolutePath());
            //获取Workbook对象
            wb = Workbook.getWorkbook(is);
            //获取sheet对象
            sheet = wb.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sheet;
    }


    /**
     * 将给定的pdf格式文件转化为doc格式文件，并保存到次级目录；
     * 将给定的doc、docx格式文件转化为txt格式，并保存到次级目录。
     * @param filePath
     */
    private static void filesParse(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                parseMethodInvoke(f);
            }
        } else {
            parseMethodInvoke(file);
        }
    }

    /**
     * 根据文件或者文件夹来解析文件，并调用对应的解析方法
     * @param file
     * @throws IOException
     */
    public static void parseMethodInvoke(File file) {
        String fileType = null;
        try {
            fileType = Files.probeContentType(file.toPath());
            switch (fileType) {
                case "application/pdf":
                    parsePdf(file);
                    break;
                case "application/msword":
                    parseDoc(file);
                    break;
                case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                    parseDocx(file);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 读取pdf文件，并将内容输出到同目录下新创建的docFile文件夹下的doc文本文件中
     * @param file 待转化的pdf文件
     */
    private static void parsePdf(File file) {
        String abPath = file.getAbsolutePath();
        int index = abPath.lastIndexOf("\\");
        String stem = abPath.substring(index + 1, abPath.length());
        String outputPath = abPath.replace(stem, "docFile\\" + stem.replace(".pdf", ".doc"));
        String outputFolder = abPath.replace(stem, "docFile\\");
        File outDirec = new File(outputFolder);
        if (!outDirec.exists()){
            outDirec.mkdirs();
        }
        PdfReader reader = null;
        try {
            reader = new PdfReader(new FileInputStream(file));
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            PrintWriter out = new PrintWriter(new FileOutputStream(outputPath));
            TextExtractionStrategy strategy;
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
                out.println(strategy.getResultantText());
            }
            out.flush();
            out.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取doc文件，并将内容输出到同目录下新创建的txtFile文件夹下的txt文本文件中
     * @param file 待转化的doc文件，格式要求是.doc的而不是.docs
     */
    private static void parseDoc(File file) {
        String abPath = file.getAbsolutePath();
        int i = abPath.lastIndexOf("\\");
        String stem = abPath.substring(i + 1, abPath.length());
        String outputPath = abPath.replace(stem, "txtFile\\" + stem.replace(".doc", ".txt"));
        String outputFolder = abPath.replace(stem, "txtFile\\");
        File txtFile = new File(outputFolder);
        if (!txtFile.exists()) {
            txtFile.mkdir();
        }
        POIFSFileSystem fs = null;
        try {
            fs = new POIFSFileSystem(new FileInputStream(file));
            HWPFDocument doc = new HWPFDocument(fs);
            WordExtractor we = new WordExtractor(doc);
            String text = we.getText();
            File fil = new File(outputPath);
            Writer output = new BufferedWriter(new FileWriter(fil));
            output.write(text);
            output.close();
        } catch (Exception exep) {
            System.out.println(exep);
        }
    }

    /**
     * 读取docx文件，并将内容输出到同目录下新创建的txtFile文件夹下的txt文本文件中
     * @param file  docx文件路径
     */
    private static void parseDocx(File file) {
        String abPath = file.getAbsolutePath();
        int i = abPath.lastIndexOf("\\");
        String stem = abPath.substring(i, abPath.length());
        String outputPath = abPath.replace(stem, "\\txtFile" + stem.replace(".docx", ".txt"));
        String outputFolder = abPath.replace(stem, "\\txtFile\\");
        File txtFile = new File(outputFolder);
        if (!txtFile.exists()) {
            txtFile.mkdir();
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            XWPFDocument doc = new XWPFDocument(fis);
            XWPFWordExtractor ex = new XWPFWordExtractor(doc);
            String text = ex.getText();
            File fil = new File(outputPath);
            Writer output = new BufferedWriter(new FileWriter(fil));
            output.write(text);
            output.close();
        } catch (Exception exep) {
            System.out.println(exep);
        }
    }
}