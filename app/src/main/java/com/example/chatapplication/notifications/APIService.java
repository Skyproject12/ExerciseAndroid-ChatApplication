package com.example.chatapplication.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAmcbTwiQ:APA91bFxRVFk_MY03MYSMIeU5x40rugyg_V9lSdEceZmlSvdzKaLZhtR6aWRjeKA6MS_BxvSoOtPwsRoLFQVDn_kU-5AkRS_uRKreelPma5FQM5uN6A-19IbBzu7FZmSVzVN7QIbhpXB"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
