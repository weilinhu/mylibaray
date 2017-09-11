package com.zhanle.upgrade;

/**
 * @author weilinhu
 */

public interface UpgradeListener {
    void onUpgradeListener(int progress, Error error);
}
