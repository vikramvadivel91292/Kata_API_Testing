package com.booking.utils;

import com.booking.pojo.BookingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Load booking data from a JSON file.
     * @param filePath path to the JSON file
     * @return BookingRequest object
     * @throws IOException if reading/parsing fails
     */
    public static BookingRequest loadBookingData(String filePath) throws IOException {
        return mapper.readValue(new File(filePath), BookingRequest.class);
    }
}
