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
public class SootInsertion {
    public static void insertTest(){
        PackManager.v().getPack("jtp").add(new Transform("jtp.myinsertion", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
                if(body.getMethod().getName().equals("onStartCommand") && body.getMethod().getDeclaringClass().getName().contains("ServiceTheSimple")){
                    System.out.println("find service !");

                    Local tmpRef = addTmpRef(body);
                    Local tmpString = addTmpString(body);

                    Chain<Unit> units = body.getUnits();

                    units.insertBefore(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(Scene.v()
                            .getField("<java.lang.System: java.io.PrintStream out>").makeRef())),
                            units.getLast());

                    units.insertBefore(Jimple.v().newAssignStmt(tmpString, StringConstant.v("HELLO")), units.getLast());

                    SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream")
                            .getMethod("void println(java.lang.String)");
                    units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef()
                    , tmpString)), units.getLast());

                    body.validate();
                }
//                while(unitIterator.hasNext()){
//                    Stmt stmt = (Stmt)unitIterator.next();
//                    if(stmt.containsInvokeExpr()){
//                        String declaringClass = stmt.getInvokeExpr().getMethod().getDeclaringClass().getName();
//                        String methodName = stmt.getInvokeExpr().getMethod().getName();
//                        if(declaringClass.contains("ServiceTheSimple") && (methodName.contains("onStartCommand") || methodName.contains("onDestroy"))){
//                            List<Unit> unitsList=new ArrayList<Unit>();
//                            //插入语句Log.i("test",toast);
//                            SootClass logClass=Scene.v().getSootClass("android.util.Log");//获取android.util.Log类
//                            SootMethod sootMethod=logClass.getMethod("int i(java.lang.String,java.lang.String)");
//                            StaticInvokeExpr staticInvokeExpr=Jimple.v().newStaticInvokeExpr(sootMethod.makeRef(),StringConstant.v("test"),StringConstant.v("SootTest"));
//                            InvokeStmt invokeStmt=Jimple.v().newInvokeStmt(staticInvokeExpr);
//                            unitsList.add(invokeStmt);
//                            body.getUnits().insertAfter(unitsList, stmt);
//                            System.out.println("insert successful");
//                        }
//                    }
//                }
            }
        }));
    }

    private static Local addTmpRef(Body body)
    {
        Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }

    private static Local addTmpString(Body body)
    {
        Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String"));
        body.getLocals().add(tmpString);
        return tmpString;
    }
}
