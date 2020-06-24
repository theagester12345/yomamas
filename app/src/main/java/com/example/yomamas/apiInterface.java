package com.example.yomamas;

public class apiInterface {

    private String store_id;
    private String total_price__number;
    private String field_delivery_location;
    private String order_id;
    private String field_del;

    public apiInterface(String store_id, String total_price__number, String order_id, String field_del, String field_delivery_location) {
        this.store_id = store_id;
        this.total_price__number = total_price__number;
        this.order_id = order_id;
        this.field_del = field_del;
        this.field_delivery_location = field_delivery_location;
    }

    public String getField_del() {
        return field_del;
    }

    public void setField_del(String field_del) {
        this.field_del = field_del;
    }

    public String getField_delivery_location() {
        return field_delivery_location;
    }

    public void setField_delivery_location(String field_delivery_location) {
        this.field_delivery_location = field_delivery_location;
    }





    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getTotal_price__number() {
        return total_price__number;
    }

    public void setTotal_price__number(String total_price__number) {
        this.total_price__number = total_price__number;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }





}
