package com.pixelized.ordiscord.network.client;

import com.pixelized.ordiscord.network.dto.worldstate.WorldStateDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface WarframeAPI {

    @GET("dynamic/worldState.php")
    Call<WorldStateDto> worldState();
}
