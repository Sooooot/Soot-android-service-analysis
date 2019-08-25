package com.csx.soot.core.soot.insertion;

import com.csx.soot.core.soot.util.GlobalUtil;
import soot.*;
import soot.jimple.*;
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
 */
public class ServiceInsertion {

    private String insertString;

    public void serviceInsertion(final Map<String, List<String>> manifestMap, final Map<String, String> serviceCheckMap) {
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.service.insertion", new BodyTransformer() {
            @Override
            protected void internalTransform(Body body, String s, Map<String, String> map) {
                String className = body.getMethod().getDeclaringClass().getName();
                if (className.lastIndexOf("$") > -1) {
                    className = className.substring(0, className.lastIndexOf("$"));
                }
                if (manifestMap.get("services").contains(className)) {

                    System.out.println("Find service: " + body.getMethod().getDeclaringClass().getName());
                    System.out.println("Inserting: " + body.getMethod().getName());

                    //获取一个方法体内的所有语句
                    Chain<Unit> units = body.getUnits();

                    for (Unit unit : units) {
                        if (!((unit instanceof IdentityStmt) || (unit instanceof AssignStmt))) {
                            // 插装ENTERED语句
                            insertString = body.getMethod().getDeclaringClass().getName()
                                    + "." + body.getMethod().getName() + " @ENTERED";
                            GlobalUtil.insertLogOut(insertString, body, unit);
                            break;
                        }

                    }

                    // 插装FINISHED语句
                    insertString = body.getMethod().getDeclaringClass().getName()
                            + "#" + body.getMethod().getName() + " @FINISHED";
                    GlobalUtil.insertLogOut(insertString, body, units.getLast());
                    serviceCheckMap.put(
                            body.getMethod().getDeclaringClass().getName() + "#" + body.getMethod().getName(),
                            "UNREACHED");
                    //验证注入是否合法，否则不准许执行
                    body.validate();
                }
            }
        }));

    }


}
