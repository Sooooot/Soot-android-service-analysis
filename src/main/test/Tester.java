package com.csx.soot.test;

import com.csx.soot.core.soot.ManifestChecker;
import org.junit.Test;

/**
 * <p>Title: Tester</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019版权</p>
 * <p>Company: </p>
 *
 * @author Zwiebeln_Chan
 * @version V1.0
 * @date 2019/6/16 10:44
 */

public class Tester {

    @Test
    public void getManifest(){
        ManifestChecker manifestChecker = new ManifestChecker();
        manifestChecker.getManifest(null);
    }

}
