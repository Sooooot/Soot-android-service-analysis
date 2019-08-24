package com.csx.soot.core.soot.insertion;

import com.csx.soot.core.soot.util.GlobalUtil;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.JimpleLocalBox;
import soot.util.Chain;

import java.util.HashMap;
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
public class ServiceInsertion {

    private static String insertString;

    public static void serviceInsertion(final Map<String, List<String>> manifestMap, final Map<String, String> checkMap) {
        final Map<String, String> methodMap = new HashMap<String, String>();
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.service.insertion", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                if (manifestMap.get("services").contains(body.getMethod().getDeclaringClass().getName())) {

                    System.out.println("Find service: " + body.getMethod().getDeclaringClass().getName());
                    System.out.println("Inserting: " + body.getMethod().getName());

                    //获取一个方法体内的所有语句
                    Chain<Unit> units = body.getUnits();

                    for (Unit unit : units) {
                        if (!((unit instanceof IdentityStmt) || (unit instanceof AssignStmt))) {
                            // 插装ENTERED语句
                            insertString = "SootTest: " + body.getMethod().getDeclaringClass().getName()
                                    + "." + body.getMethod().getName() + " ENTERED";
                            GlobalUtil.insertSystemOut(insertString, body, unit);
                            break;
                        }

                    }

                    // 插装FINISHED语句
                    insertString = "SootTest: " + body.getMethod().getDeclaringClass().getName()
                            + "." + body.getMethod().getName() + " FINISHED";
                    GlobalUtil.insertSystemOut(insertString, body, units.getLast());

                    checkMap.put(body.getMethod().getDeclaringClass().getName() + "." + body.getMethod().getName(), "UNREACHED");

                    //验证注入是否合法，否则不准许执行
                    body.validate();
                }
            }
        }));
    }


}
