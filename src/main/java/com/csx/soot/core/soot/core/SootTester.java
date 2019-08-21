package com.csx.soot.core.soot.core;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: SootTester</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/7/9 15:40
 */
public interface SootTester {
    Map<String, List<String>> runSoot(String apkPath);
}
