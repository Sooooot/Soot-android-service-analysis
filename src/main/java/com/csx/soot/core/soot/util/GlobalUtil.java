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
 */
public class GlobalUtil {

    // 插装log
    public static void insertLogOut(String insertString, Body body, Unit unit) {

        Chain<Unit> units = body.getUnits();
        SootClass logClass = Scene.v().getSootClass("android.util.Log");
        SootMethod sootMethod=logClass.getMethod("int i(java.lang.String,java.lang.String)");
        StaticInvokeExpr staticInvokeExpr=Jimple.v()
                .newStaticInvokeExpr(sootMethod.makeRef(),StringConstant.v("SootTest: "),StringConstant.v(insertString));
        InvokeStmt invokeStmt = Jimple.v().newInvokeStmt(staticInvokeExpr);
        units.insertBefore(invokeStmt, unit);
    }
}
