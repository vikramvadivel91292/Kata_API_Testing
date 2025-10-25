package com.booking.pojo;

import lombok.Data;

@Data
public class BookingRequest {
    private int roomid;
    private String firstname;
    private String lastname;
    private boolean depositpaid;
    private BookingDates bookingdates;
    private String email;
    private String phone;
}
