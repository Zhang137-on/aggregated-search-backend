package com.zhang.project.common;


/**
 * 路径工具类
 * @author zhang
 */
public class PathUtils {
    /**
     * 通配符路径匹配（支持 /* 和 /**）
     */
    public static boolean matchPath(String pattern, String path) {
        // 处理 /** 匹配多级路径
        if (pattern.endsWith("/**")) {
            String basePattern = pattern.substring(0, pattern.length() - 3);
            System.out.println("1:path:" + path);
            System.out.println("1:basePattern:" + basePattern);
            return path.startsWith(basePattern);
        }
        // 处理 /* 匹配单级路径
        else if (pattern.endsWith("/*")) {
            String basePattern = pattern.substring(0, pattern.length() - 2);
            System.out.println("2:path:" + path);
            System.out.println("2:basePattern:" + basePattern);
            return path.startsWith(basePattern) &&
                    path.split("/").length - basePattern.split("/").length == 1;
        }
        // 精确匹配
        else {
            System.out.println("3:path:" + path);
            return pattern.equals(path);
        }
    }
}
