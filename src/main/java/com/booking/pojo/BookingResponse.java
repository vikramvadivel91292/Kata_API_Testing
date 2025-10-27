package com.booking.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingResponse {
    private int bookingid;
    private int roomid;
    private String firstname;
    private String lastname;
    private BookingDates bookingdates;
    private boolean depositpaid;
    private String email;
    private String phone;
}
