package com.booking.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRequest {

    private Object roomid;        // changed from int → Object
    private String firstname;
    private String lastname;
    private Object depositpaid;   // changed from boolean → Object
    private BookingDates bookingdates;
    private String email;
    private String phone;

    // Optional helpers for valid inputs
    public void setRoomid(Integer roomid) {
        this.roomid = roomid;
    }

    public void setDepositpaid(Boolean depositpaid) {
        this.depositpaid = depositpaid;
    }

    // For invalid input types (like strings, booleans, nulls)
    public void setRoomidRaw(Object value) {
        this.roomid = value;
    }

    public void setDepositpaidRaw(Object value) {
        this.depositpaid = value;
    }
}
