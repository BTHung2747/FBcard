package com.example.englishflashcards.api;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Nếu dùng máy ảo Android (Emulator): Dùng "http://10.0.2.2:3000/"
    // Nếu dùng điện thoại thật: Dùng IP máy tính (VD: "http://192.168.1.5:3000/")
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private static Retrofit retrofit = null;

    public static ApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}