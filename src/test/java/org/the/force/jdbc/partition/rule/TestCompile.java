package org.the.force.jdbc.partition.rule;

import sun.misc.Launcher;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.SortedSet;

/**
 * Created by xuji on 2017/5/21.
 */
public class TestCompile {
    public void test1() throws Exception {
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("package com.stone.generate;").append("public class Hello {").append("public static void main(String[] args) {")
            .append("System.out.println(\"Hello World !\");").append("}").append("}");
        String userDir = System.getProperty("user.dir");
        File distDir = new File(userDir + File.separator + "partition-jdbc-matchQueryCompiler");
        if (!distDir.exists()) {
            boolean b =  distDir.mkdirs();
            if(b){

            }
        }
        Launcher.getBootstrapClassPath().addURL(distDir.toURI().toURL());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //        JavaFileObject javaFileObject = new JavaFileObjectFromString("Hello", writer.toString());
        JavaFileObject javaFileObject = new JavaFileObjectFromString("com.stone.generate.Hello", sourceCode.toString());
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, Arrays.asList("-d", distDir.getAbsolutePath()), null, Arrays.asList(javaFileObject));
        boolean compileSuccess = task.call();
        if (!compileSuccess) {
            System.out.println("编译失败");
        } else {
            //动态执行 (反射执行)
            System.out.println("编译成功");
            URL[] urls = new URL[] {distDir.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls);
            Class dynamicClass = classLoader.loadClass("com.stone.generate.Hello");
            // Class.forName("com.stone.generate.Hello");
            //Class dynamicClass = classLoader.loadClass("com.stone.generate.Hello");
            Method method = dynamicClass.getDeclaredMethod("main", String[].class);
            String[] arguments = {null};
            method.invoke(dynamicClass, arguments);
        }
    }

    public void test2() throws Exception {
        StringBuilder sourceCode = new StringBuilder();
        sourceCode.append("package com.stone.generate;");
        sourceCode.append("import PartitionRule;\n import PartitionColumnValue;\n"
            + "import LogicDbConfig;\n" + "import org.xuji.jdbc.partition.rule.config.Partition;\n" + "\n" + "import java.util.Iterator;\n"
            + "import java.util.TreeSet;");
        sourceCode.append("public class TestImpl implements PartitionRule {");
        sourceCode.append("public Partition selectPartitions(String logicTableName, LogicDbConfig logicDbConfig, TreeSet<PartitionColumnValue> partitionValueSet) {");
        sourceCode.append("return new Partition(\"test\",\"test2\",null);");
        sourceCode.append("}");
        sourceCode.append("}");
        String userDir = System.getProperty("user.dir");
        File distDir = new File(userDir + File.separator + "partition-jdbc-matchQueryCompiler");
        if (!distDir.exists()) {
            boolean b = distDir.mkdirs();
            if(b){

            }
        }
        Launcher.getBootstrapClassPath().addURL(distDir.toURI().toURL());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //        JavaFileObject javaFileObject = new JavaFileObjectFromString("Hello", writer.toString());
        JavaFileObject javaFileObject = new JavaFileObjectFromString("com.stone.generate.TestImpl", sourceCode.toString());
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, Arrays.asList("-d", distDir.getAbsolutePath()), null, Arrays.asList(javaFileObject));
        boolean compileSuccess = task.call();
        if (!compileSuccess) {
            System.out.println("编译失败");
        } else {
            //动态执行 (反射执行)
            System.out.println("编译成功");
            URL[] urls = new URL[] {distDir.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls);
            Class dynamicClass = classLoader.loadClass("com.stone.generate.TestImpl");
            Object rule = dynamicClass.newInstance();
            if (rule instanceof PartitionRule) {
                PartitionRule prule = (PartitionRule) rule;
                SortedSet<Partition> partitions = prule.selectPartitions(null, null);
                System.out.println(partitions);
            }
        }
    }
}
