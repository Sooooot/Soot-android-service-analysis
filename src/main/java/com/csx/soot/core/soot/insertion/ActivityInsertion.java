package com.csx.soot.core.soot.insertion;

import com.csx.soot.core.soot.util.GlobalUtil;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.JimpleLocalBox;
import soot.util.Chain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: SootActivityCheck</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/7/9 15:38
 */
public class ActivityInsertion {
    private static String insertString;

    public static void activityCheck(final Map<String, List<String>> manifestMap) {
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.activity.insertion",
                new BodyTransformer() {
                    @Override
                    protected void internalTransform(Body body, String s, Map<String, String> map) {
                        Map<Unit, String> insertMap = new HashMap<Unit, String>();
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

                                        // TODO: 根据Intent不同的构造方法，去进行不同的参数判断。目前只提供了MainActivity里的一种判断
                                        for (Value arg : invokingArgs) {

                                            // 此处是Intent调用
                                            if (arg.getType().toString().equals("android.content.Intent")) {
                                                System.out.println(stmt.getInvokeExpr().getMethod().toString());
                                                String argName = arg.toString();
                                                System.out.println(argName);
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
                                                                        insertMap.put(stmt, serviceName);
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
                        for (Unit unit : insertMap.keySet()) {
                            GlobalUtil.insertSystemOut("SootTest: " + body.getMethod().getDeclaringClass().getName()
                                    + "." + body.getMethod().getName() + " INVOKED", body, unit);
                        }
                        body.validate();
                    }

                }));
    }
}
