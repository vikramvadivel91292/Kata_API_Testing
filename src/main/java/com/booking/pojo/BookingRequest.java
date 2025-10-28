package com.booking.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRequest {

    private Object roomid;
    private Object firstname;
    private Object lastname;
    private Object depositpaid;
    private BookingDates bookingdates;
    private String email;
    private String phone;

    public void setFirstnameRaw(Object obj) {
        this.firstname = obj;       // for objects like {"value":"John"}
    }

    public void setLastnameRaw(Object obj) {
        this.lastname = obj;       // for objects like {"value":"John"}
    }

    // For invalid input types (like strings, booleans, nulls)
    public void setRoomidRaw(Object value) {
        this.roomid = value;
    }

    public void setDepositpaidRaw(Object value) {
        this.depositpaid = value;
    }
}
