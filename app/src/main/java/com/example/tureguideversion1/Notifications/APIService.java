package com.example.tureguideversion1.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAgbNQHYY:APA91bGdhyJtQep7JFxDZYxDu3VF0-bV1H_lSLX4Lyvek8QViU0Y0N_x42ftIf85luF0UEQEidMJj0vJBD7MqniuDWMOZzK2SjgQswp4RmFCse38wbx3nbJ0WguBbQ-SYXmi-O1r1bcY"
            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
