package org.va.transformer;



import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PerformanceTransformer implements ClassFileTransformer {
    private final List<String> trackedPackages;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        // Convert the class name to Java format
        String javaClassName = className.replace('/', '.');

        // Skip system classes and other packages you don't want to instrument
        if (javaClassName.startsWith("java.") ||
                javaClassName.startsWith("javax.") ||
                javaClassName.startsWith("sun.")) {
            return null;
        }
        if (!shouldTrackPackage(javaClassName)) {
            return null;
        }

        try {
            ClassPool cp = ClassPool.getDefault();
            ByteArrayInputStream bis = new ByteArrayInputStream(classfileBuffer);
            CtClass cc = cp.makeClass(bis);

            // Get all methods of the class
            CtMethod[] methods = cc.getDeclaredMethods();

            for (CtMethod method : methods) {
                // Skip native and abstract methods
                if (method.isEmpty()) continue;

                method.addLocalVariable("startTime", CtClass.longType);
                method.addLocalVariable("endTime", CtClass.longType);
                method.addLocalVariable("memoryBefore", CtClass.longType);
                method.addLocalVariable("memoryAfter", CtClass.longType);

                // Insert timing and memory tracking code at the beginning of the method
                method.insertBefore(
                        "startTime = System.nanoTime();" +
                                "memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();"
                );

                // Insert timing and memory tracking code at the end of the method
                method.insertAfter(
                        "endTime = System.nanoTime();" +
                                "memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();" +
                                "System.out.println(\"" + javaClassName + "." + method.getName() +
                                " - Execution time: \" + (endTime - startTime) + " +
                                "\" ns, Memory used: \" + (memoryAfter - memoryBefore) + " +
                                "\" bytes\");"
                );
            }

            return cc.toBytecode();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PerformanceTransformer(String packages) {
        this.trackedPackages = packages != null ?
                Arrays.asList(packages.split(",")) :
                new ArrayList<>();
    }

    private boolean shouldTrackPackage(String className) {
        return trackedPackages.isEmpty() ||
                trackedPackages.stream().anyMatch(pkg ->
                        className.startsWith(pkg.trim()));
    }
}
