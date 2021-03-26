package com.gavin.giframe.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件相关处理工具类
 */
public class GIFileUtil {
    private final static String FILE_EXTENSION_SEPARATOR = ".";

    /**
     * 获取根目录
     */
    public static String getRootDir(Context context) {
        // /storage/emulated/0/Android/data/package/files
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 读取文件的内容（默认utf-8编码）
     *
     * @param filePath 文件路径
     * @return 字符串
     */
    public static String readFile(String filePath) {
        return readFile(filePath, "utf-8");
    }

    /**
     * 读取文件的内容
     *
     * @param filePath    文件目录
     * @param charsetName 字符编码
     * @return String字符串
     */
    public static String readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder();
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * 读取文本文件到List字符串集合中(默认utf-8编码)
     *
     * @param filePath 文件目录
     * @return 文件不存在返回null，否则返回字符串集合
     */
    public static List<String> readFileToList(String filePath) {
        return readFileToList(filePath, "utf-8");
    }

    /**
     * 读取文本文件到List字符串集合中
     *
     * @param filePath    文件目录
     * @param charsetName 字符编码
     * @return 文件不存在返回null，否则返回字符串集合
     */
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                assert reader != null;
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath 文件目录
     * @param content  要写入的内容
     * @param append   如果为 true，则将数据写入文件末尾处，而不是写入文件开始处
     * @return 写入成功返回true， 写入失败返回false
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                fileWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 向文件中写入数据
     *
     * @param filePath 文件目录
     * @param stream   字节输入流
     * @param append   如果为 true，则将数据写入文件末尾处；为false时，清空原来的数据，从头开始写
     * @return 写入成功返回true，否则返回false
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        if (TextUtils.isEmpty(filePath))
            return false;
        if (stream == null)
            return false;
        return writeFile(new File(filePath), stream, append);
    }

    /**
     * 向文件中写入数据
     *
     * @param file   指定文件
     * @param stream 字节输入流
     * @param append 为true时，在文件开始处重新写入数据； 为false时，清空原来的数据，从头开始写
     * @return 写入成功返回true，否则返回false
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        if (file == null)
            return false;
        OutputStream out = null;
        try {
            createFile(file.getAbsolutePath());
            out = new FileOutputStream(file, append);
            byte[] data = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                out.write(data, 0, length);
            }
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获取文件名（不包含后缀）
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (GIStringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * 获取文件名（包含后缀）
     */
    public static String getFileName(String filePath) {
        if (GIStringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * 获取文件后缀
     */
    public static String getFileExtension(String filePath) {
        if (GIStringUtil.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExist(String filePath) {
        if (GIStringUtil.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * 判断文件夹是否存在
     */
    public static boolean isFolderExist(String directoryPath) {
        if (GIStringUtil.isBlank(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String path) {
        if (GIStringUtil.isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(String path) {
        if (GIStringUtil.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    /**
     * 获取指定目录的大小
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 文件拷贝
     *
     * @param source 源文件
     * @param dest   目标文件
     */
    public static boolean copyFile(String source, String dest) {
        try {
            if (GIFileUtil.isFileExist(source) && GIStringUtil.isNotBlank(dest)) {
                FileInputStream fileIn = new FileInputStream(source);
                FileOutputStream fileOut = new FileOutputStream(dest);
                byte[] buffer = new byte[8192];
                int length = 0;
                while ((length = fileIn.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, length);
                }
                fileIn.close();
                fileOut.close();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * 拷贝assets文件夹下的文件
     *
     * @param context 上下文
     * @param source  assets文件夹下源文件
     * @param dest    目标文件
     */
    public static boolean copyAssetsFile(Context context, String source, String dest) {
        try {
            if (context != null && GIFileUtil.isFileExist(source) && GIStringUtil.isNotBlank(dest)) {
                BufferedInputStream bufferIn = new BufferedInputStream(context.getResources().getAssets().open(source));
                FileOutputStream fileOut = new FileOutputStream(dest);
                byte[] buffer = new byte[8192];
                int length = 0;
                while ((length = bufferIn.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, length);
                }

                bufferIn.close();
                fileOut.close();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 格式化文件大小
     */
    public static String formetFileSize(long fileS) {
        if (fileS == 0) {
            return "0B";
        }
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取文件类型，文件类型请查看FileCst定义
     *
     * @param fileName 文件名
     */
    public static int getFileType(String fileName) {
        if (GIStringUtil.isBlank(fileName))
            return GIFileCst.TYPE_ERROR;
        fileName = fileName.toLowerCase();

        if (fileName.endsWith(GIFileCst.SUFFIX_PNG) || fileName.endsWith(GIFileCst.SUFFIX_JPG) || fileName.endsWith(GIFileCst.SUFFIX_JPE)
                || fileName.endsWith(GIFileCst.SUFFIX_JPEG) || fileName.endsWith(GIFileCst.SUFFIX_BMP) || fileName.endsWith(GIFileCst.SUFFIX_GIF)) { // 图片类型
            return GIFileCst.TYPE_IMAGE;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_MP4) || fileName.endsWith(GIFileCst.SUFFIX_3GPP) || fileName.endsWith(GIFileCst.SUFFIX_M4A)) { // 音频类型
            return GIFileCst.TYPE_AUDIO;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_VID)) { // 视频类型
            return GIFileCst.TYPE_VIDEO;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_APK) || fileName.endsWith(GIFileCst.SUFFIX_DEX)) { // 安装包类型
            return GIFileCst.TYPE_APK;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_TXT) || fileName.endsWith(GIFileCst.SUFFIX_HTM) || fileName.endsWith(GIFileCst.SUFFIX_HTML)) { // 文本类型
            return GIFileCst.TYPE_TXT;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_LOG)) { // 日志类型
            return GIFileCst.TYPE_LOG;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_RAR) || fileName.endsWith(GIFileCst.SUFFIX_ZIP)) { // 压缩包类型
            return GIFileCst.TYPE_ZIP;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_DB)) { // 数据存储类型
            return GIFileCst.TYPE_DATA;
        } else if (fileName.endsWith(GIFileCst.SUFFIX_PDF) || fileName.endsWith(GIFileCst.SUFFIX_DOC) || fileName.endsWith(GIFileCst.SUFFIX_DOCX)
                || fileName.endsWith(GIFileCst.SUFFIX_WPD) || fileName.endsWith(GIFileCst.SUFFIX_XLS) || fileName.endsWith(GIFileCst.SUFFIX_XLSX)
                || fileName.endsWith(GIFileCst.SUFFIX_ET) || fileName.endsWith(GIFileCst.SUFFIX_PPS) || fileName.endsWith(GIFileCst.SUFFIX_PPT)
                || fileName.endsWith(GIFileCst.SUFFIX_PPTX) || fileName.endsWith(GIFileCst.SUFFIX_DPS)) { // 办公文件类型
            return GIFileCst.TYPE_DOC;
        } else { // 未知
            return GIFileCst.TYPE_UNKNOWN;
        }
    }

    /**
     * 获取文件目录名, 相对目录请查看FileCst定义
     */
    public static String getFileDir(Context context, int fileType) {
        String rootDir = getRootDir(context);
        if (GIStringUtil.isEmpty(rootDir))
            return null;

        String directory;
        switch (fileType) {
            case GIFileCst.TYPE_IMAGE:
                directory = GIFileCst.DIR_IMAGE;
                break;
            case GIFileCst.TYPE_AUDIO:
                directory = GIFileCst.DIR_AUDIO;
                break;
            case GIFileCst.TYPE_VIDEO:
                directory = GIFileCst.DIR_VIDEO;
                break;
            case GIFileCst.TYPE_APK:
                directory = GIFileCst.DIR_APK;
                break;
            case GIFileCst.TYPE_TXT:
                directory = GIFileCst.DIR_TXT;
                break;
            case GIFileCst.TYPE_LOG:
                directory = GIFileCst.DIR_LOG;
                break;
            case GIFileCst.TYPE_ZIP:
                directory = GIFileCst.DIR_ZIP;
                break;
            case GIFileCst.TYPE_DATA:
                directory = GIFileCst.DIR_DATA;
                break;
            case GIFileCst.TYPE_DOC:
                directory = GIFileCst.DIR_DOC;
                break;
            case GIFileCst.TYPE_UNKNOWN:
            case GIFileCst.TYPE_ERROR:
            default:
                directory = GIFileCst.DIR_UNKNOWN;
                break;
        }
        if (!GIStringUtil.isBlank(directory)) {
            return rootDir + File.separator + directory;
        } else {
            return null;
        }
    }

    /**
     * 根据文件名获取文件目录名, 相对目录请查看FileCst定义
     */
    public static String getFileDir(Context context, String fileName) {
        int fileType = getFileType(fileName);
        return getFileDir(context, fileType);
    }

    /**
     * 获取文件的绝对路径
     */
    public static String getFilePath(Context context, String fileName) {
        String fileDir = getFileDir(context, fileName);
        return GIStringUtil.isEmpty(fileDir) ? null : fileDir + File.separator + fileName;
    }

    /**
     * 获取文件夹全路径（不包含文件名）
     */
    private static String getFolderName(String filePath) {
        if (GIStringUtil.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * 根据文件名创建文件的目录
     *
     * @param fileName 文件名
     */
    private static String createFileDir(Context context, String fileName) {
        int fileType = getFileType(fileName);
        String fileDir = getFileDir(context, fileType);
        return GIFileUtil.createFileDir(fileDir + File.separator) ? fileDir : null;
    }

    /**
     * 根据文件路径，创建目录
     *
     * @param filePath 文件路径
     */
    private static boolean createFileDir(String filePath) {
        String folderName = getFolderName(filePath);
        if (GIStringUtil.isEmpty(folderName)) {
            return false;
        }
        // 判断文件夹名路径是否以文件分隔符结束，如果没有则追加
        if (!folderName.endsWith(File.separator)) {
            folderName += File.separator;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     * 创建文件
     *
     * @param filePath 文件全路径
     */
    public static boolean createFile(String filePath) {
        if (GIStringUtil.isEmpty(filePath))
            return false;

        File file = new File(filePath);
        if (file.exists())
            return true;

        boolean result = false;
        try {
            if (createFileDir(filePath))
                result = file.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 创建文件（自动生成路径）
     *
     * @param fileName 文件名
     */
    public static String createFile(Context context, String fileName) {
        String fileDir = createFileDir(context, fileName);

        if (GIStringUtil.isEmpty(fileDir)) {
            return null;
        } else {
            String filePath = fileDir + File.separator + fileName;
            return GIFileUtil.createFile(filePath) ? filePath : null;
        }
    }

    /**
     * 创建文件路径（创建目录，不创建文件）
     */
    public static String createFilePath(Context context, String fileName) {
        String fileDir = createFileDir(context, fileName);

        if (GIStringUtil.isEmpty(fileDir)) {
            return null;
        } else {
            String filePath = fileDir + File.separator + fileName;
            return filePath;
        }
    }
}
