/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.crystals.set10.amfcall;

//import javassist.CannotCompileException;
//import javassist.ClassPool;
//import javassist.CtClass;
//import javassist.CtField;
//import javassist.CtMethod;
//import javassist.NotFoundException;

/**
 *
 * @author A.Dashkovskiy
 */
public class AMFConnectorStubGenerator {

//    private static ClassPool pool = ClassPool.getDefault();
//    private final static Object lock = new Object();
//
//    public AMFConnectorStubGenerator() {
//        //  this.conn = conn;
//    }
//
//    public <T extends Object> T getStub(AMFRequester conn, Class<T> className) throws Exception {
//        return (T) getStub(conn, className.getName());
//    }
//
//    public <T extends Object> T getStub(AMFRequester conn, Class<T> className, String notDefaultName) throws Exception {
//        return (T) getStub(conn, className.getName(), notDefaultName);
//    }
//
//    public Object getStub(AMFRequester conn, String className) throws Exception {
//        return getStub(conn, className, className);
//    }
//
//    public Object getStub(AMFRequester requester, String className, String notDefaultName) throws Exception {
//        Object obj;
//        synchronized (lock) {
////            System.out.println("GET STUB - " + className);
//            String cName = className;
//            String path = cName + "_Stub";
//
////            obj = stubs.get(path);
////            if (obj != null) {
////                System.out.println("END STORED - " + className);
////                return obj;
////            }
//            try {
//                Class<?> clazz = Class.forName(path, false, this.getClass().getClassLoader());
//                obj = clazz.newInstance();
////                stubs.put(path, obj);
//                ((AMFStubInterface) obj)._setRequester(requester);
//                return obj;
//            } catch (Exception ex) {
//            }
//
//            Class<?> clazz = Class.forName(cName, false, this.getClass().getClassLoader());
//            Method[] meth = clazz.getDeclaredMethods();
//            List<String> methods = new ArrayList();
//            methods.add("public void _setRequester(" + AMFRequester.class.getName() + " requester) {\n"
//                    + "    this.___serverRequester = requester;\n"
//                    + "}\n");
//
//            String src;
//            for (Method m : meth) {
//                src = generateMethod(clazz, m, notDefaultName);
//                methods.add(src);
//            }
//
//            Class cl = compile(methods, path, cName, clazz.isInterface());
//            obj = cl.newInstance();
//
////            stubs.put(path, obj);
//            ((AMFStubInterface) obj)._setRequester(requester);
////            System.out.println("END CREATE - " + className);
//        }
//        return obj;
//    }
//
//    private String generateMethod(Class clazz, Method source_method, String className) {
//        String src = source_method.toGenericString();
//
////        System.out.println("SRCTSET - "+src+"    "+(source_method.getReturnType()==null?"void":source_method.getReturnType().getName()));
//        int index = src.lastIndexOf(")");
//        String throwStr = src.substring(index + 1, src.length());
//
//        int i = 0;
//        StringBuilder vals = new StringBuilder();
//        String type;
//        StringBuilder types = new StringBuilder("java.lang.String[] ____types=new java.lang.String[]{");
//        for (Type t : source_method.getGenericParameterTypes()) {
//            type = t.toString();
//            types.append("\"");
//            types.append(type);
//            types.append("\"");
//            types.append(",");
////            System.out.println("TYPE - "+type);
//            index = type.indexOf("<");
//            if (index != -1) {
//                type = type.substring(0, index);
//            }
//            if (type.startsWith("interface ")) {
//                type = type.replace("interface ", "");
//            }
//            if (type.startsWith("class ")) {
//                type = type.replace("class ", "");
//            }
//            vals.append(testArrayParam(type));
//            vals.append(" val");
//            vals.append(i);
//            vals.append(",");
//            i++;
//        }
//        if (source_method.getGenericParameterTypes().length > 0) {
//            types.deleteCharAt(types.length() - 1);
//            types.append("};");
//        } else {
//            types = new StringBuilder("java.lang.String[] ____types=new java.lang.String[0];");
//        }
//
//        if (vals.length() > 0) {
//            vals.deleteCharAt(vals.length() - 1);
//        }
////        String retVal = src.substring(src.indexOf(" "), src.indexOf(clazz.getName())).replaceAll("abstract", "").trim();
////
////        index = retVal.indexOf("<");
////        if (index != -1) {
////            retVal = retVal.substring(0, index);
////        }
//
//        String string = "public " + (source_method.getReturnType() == null ? "void" : testArrayParam(source_method.getReturnType().getName())) + " " + source_method.getName() + "(" + vals + ") " + throwStr + "{\n"
//                + "    " + types + "\n"
//                + (source_method.getReturnType() == null ? "    " : "    return ($r)") + "___serverRequester.call(\"" + className + "\",\"" + source_method.getName() + "\",$args,____types);\n"
//                + "}\n";
//
////        System.out.println(string);
////        System.out.println("");
//        return string;
//    }
//
//    private String testArrayParam(String param) {
//        if (!param.startsWith("[")) {
//            return param;
//        }
//        if (param.startsWith("[[")) {
//            String prim = testPrimitiveArray(2, param);
//            if (prim != null) {
//                return prim + "[][]";
//            }
//            return (param.substring(3, param.length() - 1)) + "[][]";
//        }
//        String prim = testPrimitiveArray(1, param);
//        if (prim != null) {
//            return prim + "[]";
//        }
//        return (param.substring(2, param.length() - 1)) + "[]";
//    }
//
//    private String testPrimitiveArray(int arraySize, String param) {
//        if (param.length() == arraySize + 1) {//это примитив
//            String t = param.substring(arraySize, arraySize + 1);
//            if (t.equals("B")) {
//                return "byte";
//            } else if (t.equals("B")) {
//                return "byte";
//            } else if (t.equals("S")) {
//                return "short";
//            } else if (t.equals("I")) {
//                return "int";
//            } else if (t.equals("L")) {
//                return "long";
//            } else if (t.equals("F")) {
//                return "float";
//            } else if (t.equals("D")) {
//                return "double";
//            } else if (t.equals("C")) {
//                return "char";
//            } else {
//                System.out.println("Unknown primitive type - " + param);
//            }
//        }
//        return null;
//    }
//
//    private Class compile(List<String> methods, String path, String beanInterface, boolean isInterface) throws NotFoundException, CannotCompileException {
//        CtClass clazz = pool.makeClass(path);
//        CtClass interf = pool.get(beanInterface);
//        //CtClass stubInterf = pool.get("ru.crystals.httpclient.StubInterface");
//        CtClass stubInterf = pool.get(AMFStubInterface.class.getName());
//
//        if (isInterface) {
//            clazz.addInterface(interf);
//        } else {
//            clazz.setSuperclass(interf);
//        }
//
//        clazz.addInterface(stubInterf);
//
//        //clazz.addField(CtField.make("public int z = 0;", clazz));
//        clazz.addField(CtField.make("public " + AMFRequester.class.getName() + " ___serverRequester;", clazz));
//
////
////        clazz.addMethod(CtMethod.make("public java.util.Map test(){return null;}", clazz));
//        for (String src : methods) {
////            System.out.println("SRC - " + src);
//            clazz.addMethod(CtMethod.make(src, clazz));
//        }
//        return clazz.toClass();
//    }
}
