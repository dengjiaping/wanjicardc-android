package com.wjika.client.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kkkkk on 2016/10/12.
 */

public class MarketUserEntity extends Entity {
    @SerializedName("balance")
    private String balance;
    @SerializedName("ifRecharge")
    private boolean ifRecharge;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public boolean isIfRecharge() {
        return ifRecharge;
    }

    public void setIfRecharge(boolean ifRecharge) {
        this.ifRecharge = ifRecharge;
    }
}