package com.csx.soot.core.soot;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Iterator;
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
    public static void insertTest(final Map<String, List<String>> manifestMap){
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.insertion", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                if(manifestMap.get("services").contains(body.getMethod().getDeclaringClass().getName())){
                    System.out.println("Find service: " + body.getMethod().getDeclaringClass().getName());
                    System.out.println("Inserting: " + body.getMethod().getName());
                    //建立本地变量（未赋值）
                    Local tmpIORef = addTmpRef(body);
                    Local tmpString = addTmpString(body);

                    //获取一个方法体内的所有语句
                    Chain<Unit> units = body.getUnits();

                    SootMethod toCall;
                    String insertString;

                    for (Unit unit : units) {
                        if(!((unit instanceof IdentityStmt) || (unit instanceof AssignStmt))){
                            //建立IO库与tempIORef的引用
                            units.insertBefore(Jimple.v().newAssignStmt(tmpIORef, Jimple.v().newStaticFieldRef(Scene.v()
                                            .getField("<java.lang.System: java.io.PrintStream out>").makeRef())),
                                    unit);

                            //建立注入语句与temp的引用
                            insertString = "SootTest: " + body.getMethod().getDeclaringClass().getName()
                                    + "." + body.getMethod().getName() + " ENTERED";
                            units.insertBefore(Jimple.v().newAssignStmt(tmpString, StringConstant.v(insertString)), unit);
                            toCall = Scene.v().getSootClass("java.io.PrintStream")
                                    .getMethod("void println(java.lang.String)");
                            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpIORef, toCall.makeRef()
                                    , tmpString)), unit);
                            break;
                        }

                    }

                    //建立注入语句与temp的引用
                    insertString = "SootTest: " + body.getMethod().getDeclaringClass().getName()
                            + "." + body.getMethod().getName() + " FINISHED";
                    units.insertBefore(Jimple.v().newAssignStmt(tmpString, StringConstant.v(insertString))
                            , units.getLast());

                    //注入输出语句（获取方法的模式有点像反射）
                    toCall = Scene.v().getSootClass("java.io.PrintStream")
                            .getMethod("void println(java.lang.String)");
                    units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpIORef, toCall.makeRef()
                    , tmpString)), units.getLast());

                    //验证注入是否合法，否则不准许执行
                    body.validate();
                }
            }
        }));
    }

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
}
