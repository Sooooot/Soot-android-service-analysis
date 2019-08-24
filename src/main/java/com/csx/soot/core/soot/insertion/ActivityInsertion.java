package com.csx.soot.core.soot.insertion;

import com.csx.soot.core.soot.util.GlobalUtil;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.internal.ImmediateBox;
import soot.jimple.internal.JimpleLocalBox;
import soot.util.Chain;

import java.util.*;

/**
 * <p>Title: SootActivityCheck</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 */
@Deprecated
public class ActivityInsertion {
    private static String insertString;

    public static void activityCheck(final Map<String, List<String>> manifestMap) {
        PackManager.v().getPack("jtp").add(new Transform("jtp.my.activity.insertion",
                new BodyTransformer() {
                    @Override
                    protected void internalTransform(Body body, String s, Map<String, String> map) {
                        Map<Unit, String> insertMap = new HashMap<>();
                        if (body.getMethod().getDeclaringClass().getName().endsWith("MainActivity")
                                && body.getMethod().getName().equals("onClick")) {
                            Chain<Unit> bodyUnits = body.getUnits();

                            // 遍历方法体中每一条语句
                            for (Unit unit : bodyUnits) {
                                List<UnitBox> unitBoxes = unit.getUnitBoxes();
                                List<ValueBox> defBoxes = unit.getDefBoxes();
                                List<ValueBox> useBoxes = unit.getUseBoxes();

                                // 如果存在外调函数，并且将Intent传过去了，那么可以一定程度上说明这里存在service调用
                                if (!unitBoxes.isEmpty()) {
                                    for (UnitBox unitBox : unitBoxes) {
                                        // 强转至Stmt以获取args
                                        Stmt stmt = (Stmt) (unitBox.getUnit());
                                        if(!stmt.containsInvokeExpr()) continue;
                                        List<Value> invokingArgs = stmt.getInvokeExpr().getArgs();

                                        for (Value arg : invokingArgs) {
                                            // 此处是Intent调用
                                            if (arg.getType().toString().equals("android.content.Intent")) {
                                                List<ValueBox> unitBoxes1 = arg.getUseBoxes();
                                                String argName = arg.toString();
                                                System.out.println("Find activity: " + body.getMethod().getDeclaringClass().getName());
                                                System.out.println("Inserting: " + body.getMethod().getName());
                                                // System.out.println(argName);
                                                for (Unit searchingUnit : bodyUnits) {
                                                    List<ValueBox> searchingUnitBoxes = searchingUnit.getUseAndDefBoxes();
//                                                    if(searchingUnitBoxes.size() < 2) continue;
//
//                                                    String defArgName = searchingUnitBoxes.get(0).getValue().toString();
//                                                    String defArgValue = searchingUnitBoxes.get(1).getValue().toString();
//                                                    if(defArgName.equals(argName)){
//                                                        System.out.println(defArgValue);
//                                                    }
                                                    for (ValueBox valueBox : searchingUnitBoxes) {
                                                        // 判断参数名
                                                        if (valueBox instanceof JimpleLocalBox
                                                                && valueBox.getValue().toString().equals(argName)) {

                                                            //获取service名
                                                            for (ValueBox box : searchingUnitBoxes) {
                                                                if (box instanceof ImmediateBox
                                                                        && !box.getValue().toString().startsWith("$")) {
                                                                    String serviceName = box.getValue().toString();
                                                                    int lastSep = serviceName.lastIndexOf(".");
                                                                    if(lastSep > -1){
                                                                        serviceName = serviceName.substring(lastSep + 1, serviceName.length() - 1);
                                                                    }else{
                                                                        lastSep = serviceName.lastIndexOf("/");
                                                                        serviceName = serviceName.substring(lastSep + 1, serviceName.length() - 2);

                                                                    }


                                                                    // 对获取Service名会有多种处理 在此进行完善
                                                                    // 2019年7月5日 完成通过构造函数传入service的判断
                                                                    HashSet<String> serviceNameSet = new HashSet<>();
                                                                    for (String service : manifestMap.get("services")) {
                                                                        int lastDot = service.lastIndexOf(".");
                                                                        serviceNameSet.add(service.substring(lastDot + 1));
                                                                    }

                                                                    if (serviceNameSet.contains(serviceName)) {
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
                            GlobalUtil.insertLogOut(body.getMethod().getDeclaringClass().getName()
                                    + "." + body.getMethod().getName() + " INVOKED", body, unit);
                        }
                        body.validate();
                    }

                }));
    }
}
