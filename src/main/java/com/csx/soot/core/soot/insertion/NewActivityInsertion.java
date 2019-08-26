package com.csx.soot.core.soot.insertion;

import com.csx.soot.core.soot.util.GlobalUtil;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.*;
import soot.util.Chain;

import java.util.*;

/**
 * <p>Title: NewActivityInsertion</p>
 * <p>Description: 新Activity插装</p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 */
public class NewActivityInsertion{

    public void activityInsertion(final Map<String, List<String>> manifestMap,
                                  final Map<String, String> activityCheckMap){
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.activity.insertion", new BodyTransformer(){
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map){
                Set<String> activitySet = new HashSet<>(manifestMap.get("activities"));

                // 处理生成的内部类名称以能够匹配上Activity列表内的名称
                String className = body.getMethod().getDeclaringClass().getName();
                if (className.lastIndexOf("$") > -1){
                    className = className.substring(0, className.lastIndexOf("$"));
                }

                // 当ManifestMap中的activities存在本包名才执行分析
                if (activitySet.contains(className)){
                    List<JInvokeStmt> invokeExprList = searchForServiceInvoked(body);
                    Map<JInvokeStmt, String> intentMap = searchForIntent(invokeExprList);
                    Map<JInvokeStmt, String> insertionMap = searchForInvokeServiceName(body, intentMap);

                    // insertionMap > 0时才执行插装
                    if (insertionMap.size() > 0){
                        System.out.println("Find Activity: " + body.getMethod().getDeclaringClass().getName());
                        System.out.println("Inserting: " + body.getMethod().getName());
                        activityCheckMap
                                .put(body.getMethod().getDeclaringClass().getName() + "#" + body.getMethod().getName(),
                                     "UNREACHED");
                        logInsertion(body, insertionMap);
                    }
                    body.validate();
                }

                // 调试用例
//                        if (body.getMethod().getDeclaringClass().getName().contains("BindActivity") &&
//                            body.getMethod().getName().contains("onClick")) {
//                            List<JInvokeStmt> invokeExprList = searchForServiceInvoked(body);
//                            Map<JInvokeStmt, String> intentMap = searchForIntent(invokeExprList);
//                            Map<JInvokeStmt, String> insertionMap = searchForInvokeServiceName(body, intentMap);
//                            logInsertion(body, insertionMap);
//                            body.validate();
//                        }
            }
        }));
    }


    /**
     * 寻找与Service相关的调用语句
     *
     * @param body 遍历的方法体
     *
     * @return 寻找服务调用，返回一个InvokeExpr数组
     */
    private List<JInvokeStmt> searchForServiceInvoked(Body body){
        Chain<Unit> bodyStatements = body.getUnits();
        List<JInvokeStmt> result = new ArrayList<>();

        // 寻找拥有InvokeExpr，即拥有调用的语句
        for (Unit statement : bodyStatements){
            // 是一个调用语句
            if (statement instanceof JInvokeStmt){
                JInvokeStmt invokeStmt = (JInvokeStmt) statement;
                InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
                if (isServiceInvoke(invokeExpr)){
                    result.add(invokeStmt);
                }
            }
        }
        return result;
    }

    /**
     * 寻找于Service调用有关Intent的本地变量名
     *
     * @param unitList 与Service有关的调用
     *
     * @return 调用Stmt -> Intent本地变量名 键值对
     */
    private Map<JInvokeStmt, String> searchForIntent(List<JInvokeStmt> unitList){
        Map<JInvokeStmt, String> intentMap = new HashMap<>();
        for (JInvokeStmt unit : unitList){
            InvokeExpr invokeExpr = unit.getInvokeExpr();
            List<Value> argValues = invokeExpr.getArgs();
            for (Value arg : argValues){
                // 如果类型匹配，则将Intent的本地变量名添加进键值对
                if ("android.content.Intent".equals(arg.getType().toString())){
                    if (arg instanceof JimpleLocal){
                        intentMap.put(unit, ((JimpleLocal) arg).getName());
                    }
                }
            }
        }
        return intentMap;
    }

    /**
     * 寻找Intent所注入的Service
     *
     * @param body      方法体
     * @param intentMap 调用Stmt -> Intent本地变量名 键值对
     *
     * @return 调用Stmt -> Service名 键值对
     */
    private Map<JInvokeStmt, String> searchForInvokeServiceName(Body body, Map<JInvokeStmt, String> intentMap){
        Map<JInvokeStmt, String> serviceInvokeMap = new HashMap<>();
        intentMap.forEach((key, value) -> {

            // 获取当前方法体中所有的UseBoxes，从而寻找于Intent创建有关的Boxes
            List<ValueBox> useBoxes = body.getUseBoxes();
            List<ValueBox> relativeBoxes = new ArrayList<>();
            for (ValueBox useBox : useBoxes){
                // Intent创建相关都为调用语句，故寻找InvokeExprBox
                if (useBox instanceof InvokeExprBox){
                    InvokeExprBox invokeExprBox = (InvokeExprBox) useBox;
                    List<ValueBox> invokeExprUseBoxes = invokeExprBox.getValue().getUseBoxes();
                    // 遍历当前的InvokeExprBox中的UseBoxes，查看是否含有当前Intent的本地变量名
                    for (ValueBox invokeExprUseBox : invokeExprUseBoxes){
                        // 如果是本地变量，自然会有JimpleLocalBox
                        if (invokeExprUseBox instanceof JimpleLocalBox){
                            String argName = invokeExprUseBox.getValue().toString();
                            if (value.equals(argName)){
                                relativeBoxes.add(invokeExprBox);
                            }
                        }
                    }
                }
            }

            // 解析Service名称
            String serviceName = "";
            for (ValueBox relativeBox : relativeBoxes){
                List<ValueBox> relativeUseBoxes = relativeBox.getValue().getUseBoxes();
                for (ValueBox relativeUseBox : relativeUseBoxes){
                    if (relativeUseBox instanceof ImmediateBox &&
                        !relativeUseBox.getValue().toString().startsWith("$")){
                        serviceName = relativeUseBox.getValue().toString();
                        int lastSep = serviceName.lastIndexOf(".");
                        // 是直接以字符串赋值，则分隔符为.
                        if (lastSep > -1){
                            serviceName = serviceName.substring(lastSep + 1, serviceName.length() - 1);
                            break;
                        } else{ // 如果是以.class赋值，则分隔符为/并且
                            lastSep = serviceName.lastIndexOf("/");
                            serviceName = serviceName.substring(lastSep + 1, serviceName.length() - 2);
                            break;
                        }
                    }
                }
            }
            serviceInvokeMap.put(key, serviceName);
        });

        return serviceInvokeMap;
    }


    /**
     * log语句插装
     *
     * @param body       方法体
     * @param serviceMap 服务调用相关Stmt -> 服务名 键值对
     */
    private void logInsertion(Body body, Map<JInvokeStmt, String> serviceMap){
        serviceMap.forEach((key, value) -> {
            String insertString = body.getMethod().getDeclaringClass().getName() + "#" + body.getMethod().getName() +
                                  " -> " + value + " @INVOKED";
            GlobalUtil.insertLogOut(insertString, body, key);
        });
    }

    /**
     * 判断一个调用是否是服务相关调用
     *
     * @param invokeExpr 要判断的invokeExpr
     *
     * @return 是否是一个服务调用
     */
    private boolean isServiceInvoke(InvokeExpr invokeExpr){
        return "startService".equals(invokeExpr.getMethod().getName()) ||
               "stopService".equals(invokeExpr.getMethod().getName()) ||
               "bindService".equals(invokeExpr.getMethod().getName());
    }
}
