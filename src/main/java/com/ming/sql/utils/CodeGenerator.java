package com.ming.sql.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2018/6/14.
 */
public class CodeGenerator {

    /**
     * 生成代码前必须将相关的文件夹建好
     * @param packageName       实体类的包名
     * @param mapperPackage     mapper的包名
     * @param servicePackage    service的包名
     */
    public static void generate(String packageName,String mapperPackage,String servicePackage) {
        try {
            List<String> fileNames = recursiveFiles(convertPackageToPath(packageName));
            for (String fileName : fileNames) {
                Class<?> aClass = Class.forName(packageName + "." + fileName.replace(".java", ""));
                generateMapper(aClass,mapperPackage,convertPackageToPath(mapperPackage));
                generateService(aClass,servicePackage,mapperPackage,convertPackageToPath(servicePackage));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertPackageToPath(String packageName) {
        return System.getProperty("user.dir") + "/src/main/java/" + packageName.replace(".","/") + "/";
    }

    private static List<String> recursiveFiles(String path){
        // 创建 File对象
        List<String> fileNames = new ArrayList<String>();
        File file = new File(path);
        // 取 文件/文件夹
        File files[] = file.listFiles();
        // 对象为空 直接返回
        if(files == null){
            return fileNames;
        }

        // 目录下文件
        if(files.length == 0){
            System.out.println(path + "该文件夹下没有文件");
        }

        // 存在文件 遍历 判断
        for (File f : files) {
            // 判断是否为 文件夹
            if(f.isDirectory()){
                System.out.print("文件夹: ");
                System.out.println(f.getAbsolutePath());

                // 为 文件夹继续遍历
                recursiveFiles(f.getAbsolutePath());

                // 判断是否为 文件
            } else if(f.isFile()){
                System.out.println(f.getName());
                fileNames.add(f.getName());
            } else {
                System.out.print("未知错误文件");
            }
        }

        return fileNames;
    }

    private static void generateMapper(Class clazz,String packageName,String dir) throws Exception {
        String classSimpleName = clazz.getSimpleName();
        String mapperName = classSimpleName + "Mapper";
        File file = new File(dir + "/" + mapperName + ".java");

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        String content = mapperTemplate
                .replace("${classFullName}",clazz.getName())
                .replace("${packageName}",packageName)
                .replace("${classSimpleName}", classSimpleName)
                .replace("${mapperName}",mapperName);
        byte[] bytes = content.getBytes();

        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private static void generateService(Class clazz,String packageName,String mapperPackageName, String dir) throws Exception {
        String classSimpleName = clazz.getSimpleName();
        String serviceName = classSimpleName + "Service";
        String mapperName = classSimpleName + "Mapper";
        File file = new File(dir + "/" + serviceName + ".java");

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        String content = serviceTemplate
                .replace("${classFullName}",clazz.getName())
                .replace("${packageName}",packageName)
                .replace("${classSimpleName}", classSimpleName)
                .replace("${mapperPackageName}", mapperPackageName)
                .replace("${serviceName}",serviceName)
                .replace("${mapperName}",mapperName);
        byte[] bytes = content.getBytes();

        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private static String mapperTemplate = "package ${packageName};\n" +
            "\n" +
            "import ${classFullName};\n" +
            "import com.ming.sql.BaseMapper;\n" +
            "import org.apache.ibatis.annotations.Mapper;\n" +
            "\n" +
            "/**\n" +
            " * @author mmeCodeGenerator\n" +
            " * Created by mmeCodeGenerator.\n" +
            " */\n" +
            "@Mapper\n" +
            "public interface ${mapperName} extends BaseMapper<${classSimpleName}> {\n" +
            "\n" +
            "}";

    private static String serviceTemplate = "package ${packageName};\n" +
            "\n" +
            "import ${classFullName};\n" +
            "import ${mapperPackageName}.${mapperName};\n" +
            "import com.ming.sql.BaseService;\n" +
            "import org.springframework.stereotype.Service;\n" +
            "\n" +
            "/**\n" +
            " * @author mmeCodeGenerator\n" +
            " * Created by mmeCodeGenerator.\n" +
            " */\n" +
            "@Service\n" +
            "public class ${serviceName} extends BaseService<${classSimpleName},${mapperName}>{\n" +
            "}";
}
