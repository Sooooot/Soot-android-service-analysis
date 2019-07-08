package com.csx.soot.core.soot;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.JimpleLocalBox;
import soot.util.Chain;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: SootInsertion</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/6/2 13:13
 */
public class SootServiceMethodInsertion {

    private static final SootMethod SYSTEM_OUT_TO_CALL = Scene.v().getSootClass("java.io.PrintStream")
            .getMethod("void println(java.lang.String)");
    private static String insertString;

    public static void serviceInsertion(final Map<String, List<String>> manifestMap) {
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.service.insertion", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                if (manifestMap.get("services").contains(body.getMethod().getDeclaringClass().getName())) {

                    System.out.println("Find service: " + body.getMethod().getDeclaringClass().getName());
                    System.out.println("Inserting: " + body.getMethod().getName());

                    //建立本地变量（未赋值）
                    Local tmpIORef = addTmpRef(body);
                    Local tmpString = addTmpString(body);

                    //获取一个方法体内的所有语句
                    Chain<Unit> units = body.getUnits();


                    for (Unit unit : units) {
                        if (!((unit instanceof IdentityStmt) || (unit instanceof AssignStmt))) {
                            //建立IO库与tempIORef的引用
                            insertString = "SootTest: " + body.getMethod().getDeclaringClass().getName()
                                    + "." + body.getMethod().getName() + " ENTERED";
                            insertSystemOut(insertString, body, unit);
                            break;
                        }

                    }

                    //建立注入语句与temp的引用
                    insertString = "SootTest: " + body.getMethod().getDeclaringClass().getName()
                            + "." + body.getMethod().getName() + " FINISHED";
                    insertSystemOut(insertString, body, units.getLast());

                    //验证注入是否合法，否则不准许执行
                    body.validate();
                }
            }
        }));
    }

    public static void activityCheck(final Map<String, List<String>> manifestMap) {
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.activity.insertion", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                if (body.getMethod().getDeclaringClass().getName().endsWith("MainActivity")
                        && body.getMethod().getName().equals("onClick")) {
                    Chain<Unit> bodyUnits = body.getUnits();
                    for (Unit unit : bodyUnits) {
                        List<UnitBox> unitBoxes = unit.getUnitBoxes();

                        // 如果存在外调函数，并且将Intent传过去了，那么可以一定程度上说明这里存在service调用
                        if (!unitBoxes.isEmpty()) {
                            for (UnitBox unitBox : unitBoxes) {
                                // 强转至Stmt以获取args
                                Stmt stmt = (Stmt) (unitBox.getUnit());
                                List<Value> invokingArgs = stmt.getInvokeExpr().getArgs();

                                for (Value arg : invokingArgs) {
                                    if (arg.getType().toString().equals("android.content.Intent")) {
                                        String argName = arg.toString();
                                        System.out.println(argName);
                                        synchronized (this){
                                            for (Unit searchingUnit : bodyUnits) {
                                                List<ValueBox> searchingUnitBoxes = searchingUnit.getUseAndDefBoxes();
                                                for (ValueBox valueBox : searchingUnitBoxes) {
                                                    // 判断参数名
                                                    if (valueBox instanceof JimpleLocalBox
                                                            && valueBox.getValue().toString().equals(argName)) {

                                                        //获取service名
                                                        for (ValueBox box : searchingUnitBoxes) {
                                                            if (box instanceof ImmediateBox
                                                                    && !box.getValue().toString().startsWith("$")) {
                                                                String serviceName = box.getValue().toString();
                                                                serviceName = serviceName
                                                                        .substring(8, serviceName.length() - 2)
                                                                        .replaceAll("/", ".");

                                                                // 对获取Service名会有多种处理 在此进行完善
                                                                // 2019年7月5日 完成通过构造函数传入service的判断

                                                                if (manifestMap.get("services").contains(serviceName)) {
                                                                    System.out.println("SOOT: FIND INVOKED METHOD");

                                                                    // insertion
                                                                    insertString = "SootTest: " + serviceName + " INVOKED";
                                                                    insertSystemOut(insertString, body, stmt);


                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                body.validate();
            }

        }));
    }

    //用于构建sout中临时变量的方法

    private static Local addTmpRef(Body body) {
        Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }

    private static Local addTmpString(Body body) {
        Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }

    private static synchronized void insertSystemOut(String insertString, Body body, Unit unit) {

        Chain<Unit> units = body.getUnits();

        Local IORef = addTmpRef(body);
        Local stringRef = addTmpString(body);
        units.insertBefore(Jimple.v().newAssignStmt(stringRef, StringConstant.v(insertString)), unit);
        units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(IORef,
                SYSTEM_OUT_TO_CALL.makeRef()
                , stringRef)), unit);
    }
}
