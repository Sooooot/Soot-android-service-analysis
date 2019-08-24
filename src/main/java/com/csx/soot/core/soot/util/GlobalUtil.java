package com.csx.soot.core.soot.util;

import soot.*;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.util.Chain;

/**
 * <p>Title: GlobalUtil</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/7/9 15:36
 */
public class GlobalUtil {

    private static final SootMethod SYSTEM_OUT_TO_CALL = Scene.v().getSootClass("java.io.PrintStream")
            .getMethod("void println(java.lang.String)");

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

    // 插装sout
    public static void insertSystemOut(String insertString, Body body, Unit unit) {

        Chain<Unit> units = body.getUnits();
        SootClass logClass = Scene.v().getSootClass("android.util.Log");
        SootMethod sootMethod=logClass.getMethod("int i(java.lang.String,java.lang.String)");
        StaticInvokeExpr staticInvokeExpr=Jimple.v()
                .newStaticInvokeExpr(sootMethod.makeRef(),StringConstant.v("SootTest: "),StringConstant.v(insertString));
        InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(staticInvokeExpr);
        units.insertBefore(invokeStmt, unit);
//        Local IORef = addTmpRef(body);
//        Local stringRef = addTmpString(body);
//        units.insertBefore(Jimple.v().newAssignStmt(stringRef, StringConstant.v(insertString)), unit);
//        units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(IORef,
//                SYSTEM_OUT_TO_CALL.makeRef(), stringRef)), unit);
    }
}
