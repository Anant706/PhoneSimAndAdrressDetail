package bankingservice.model;

import java.io.Serializable;

/**
 * Created by maa on 07-May-17.
 */

public class BankModel implements Serializable {
    String bank_id = null;
    String bank_name = null;
    String bank_inquiry = null;
    String bank_care = null;
    String bank_img = null;
    public BankModel(){
    }

    public BankModel(String id,String name,String inquiry, String care, String img){
        bank_id = id;
        bank_name = name;
        bank_inquiry= inquiry;
        bank_care = care;
        bank_img = img;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_inquiry() {
        return bank_inquiry;
    }

    public void setBank_inquiry(String bank_inquiry) {
        this.bank_inquiry = bank_inquiry;
    }

    public String getBank_care() {
        return bank_care;
    }

    public void setBank_care(String bank_care) {
        this.bank_care = bank_care;
    }

    public String getBank_id() {
        return bank_id;
    }

    public void setBank_id(String bank_id) {
        this.bank_id = bank_id;
    }

    public void setBank_img(String img_name){
        this.bank_img = img_name;
    }
    public String getBank_img() {
        return bank_img;
    }
}
