package com.model.deliverAll;

import java.io.Serializable;

/**
 * Created by Esite on 21-05-2018.
 */

public class orderItemDetailDataModel implements Serializable {
    String iOrderDetailId;
    String itemName;
    String subItemName;
    String itemQuantity;
    String itemPrice;
    String eAvailable;
    String itemImg;
    String isGenie;
    String itemDetailsUpdated;
    String fTotPriceWithoutSymbol ;
    String vImageUploaded;

    public String getvImageUploaded() {
        return vImageUploaded;
    }

    public void setvImageUploaded(String vImageUploaded) {
        this.vImageUploaded = vImageUploaded;
    }

    public String geteDecline() {
        return eDecline;
    }

    public void seteDecline(String eDecline) {
        this.eDecline = eDecline;
    }

    String eDecline;


    public String getfTotPriceWithoutSymbol() {
        return fTotPriceWithoutSymbol;
    }

    public void setfTotPriceWithoutSymbol(String fTotPriceWithoutSymbol) {
        this.fTotPriceWithoutSymbol = fTotPriceWithoutSymbol;
    }

    public String getItemDetailsUpdated() {
        return itemDetailsUpdated;
    }

    public void setItemDetailsUpdated(String itemDetailsUpdated) {
        this.itemDetailsUpdated = itemDetailsUpdated;
    }

    public String getIsGenie() {
        return isGenie;
    }

    public void setIsGenie(String isGenie) {
        this.isGenie = isGenie;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public String getvImage() {
        return vImage;
    }

    public void setvImage(String vImage) {
        this.vImage = vImage;
    }

    String vImage;

    public String getTotalDiscountPrice() {
        return TotalDiscountPrice;
    }

    public void setTotalDiscountPrice(String totalDiscountPrice) {
        TotalDiscountPrice = totalDiscountPrice;
    }

    String TotalDiscountPrice;


    public String geteAvailable() {
        return eAvailable;
    }

    public void seteAvailable(String eAvailable) {
        this.eAvailable = eAvailable;
    }


    public String getSubItemName() {
        return subItemName;
    }

    public void setSubItemName(String subItemName) {
        this.subItemName = subItemName;
    }

    public String getiOrderDetailId() {

        return iOrderDetailId;
    }

    public void setiOrderDetailId(String iOrderDetailId) {
        this.iOrderDetailId = iOrderDetailId;
    }

    public String getItemTotalPrice() {
        return itemTotalPrice;
    }

    public void setItemTotalPrice(String itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

    String itemTotalPrice;
    String itemAvability;
    String eItemAvailable;

    String extrapayment;

    public String getExtrapayment() {
        return extrapayment;
    }

    public void setExtrapayment(String extrapayment) {
        this.extrapayment = extrapayment;
    }

    public String geteItemAvailable() {
        return eItemAvailable;
    }

    public void seteItemAvailable(String eItemAvailable) {
        this.eItemAvailable = eItemAvailable;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemAvability() {
        return itemAvability;
    }

    public void setItemAvability(String itemAvability) {
        this.itemAvability = itemAvability;
    }
}
