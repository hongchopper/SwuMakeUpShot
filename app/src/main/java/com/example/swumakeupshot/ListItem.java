package com.example.swumakeupshot;

public class ListItem {
    /* 아이템의 정보를 담기 위한 클래스 */

    String cos_name;
    String caution_count;
    String allergy_count;
    String good_count;

    public String getCos_name() {
        return cos_name;
    }

    public void setCos_name(String cos_name) {
        this.cos_name = cos_name;
    }

    public String getCaution_count() {
        return caution_count;
    }

    public void setCaution_count(String caution_count) {
        this.caution_count = caution_count;
    }

    public String getAllergy_count() {
        return allergy_count;
    }

    public void setAllergy_count(String allergy_count) {
        this.allergy_count = allergy_count;
    }

    public String getGood_count() {
        return good_count;
    }

    public void setGood_count(String good_count) {
        this.good_count = good_count;
    }
}