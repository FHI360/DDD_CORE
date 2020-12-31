package org.fhi360.ddd.webservice;


import org.fhi360.ddd.domain.*;
import org.fhi360.ddd.dto.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ClientAPI {

    @Headers("Content-Type: application/json")
    @GET("resources/webservice/mobile/activate/{deviceId}/{pin}")
    Call<User> getUsernamePasswordFromLamis(@Path("deviceId") String deviceId, @Path("pin") String pin);


    @Headers("Content-Type: application/json")
    @GET("resources/webservice/mobile/activate/{userName}/{pin}/{deviceId}/{accountUserName}/{accountPassword}")
    Call<User> activateCPARP(@Path("userName") String userName,
                             @Path("pin") String pin,
                             @Path("deviceId")
                                     String deviceId,
                             @Path("accountUserName") String accountUserName,
                             @Path("accountPassword") String accountPassword);


    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/pharmacy")
    Call<org.fhi360.ddd.dto.Response> saveAccount(@Body PharmacyDto pharmacy);

    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/sync/patient")
    Call<Response> syncPatient(@Body List<PatientDto> patientDto);

    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/save/patient")
    Call<Response> savePatient(@Body PatientDto patient);


    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/update/patient")
    Call<Response> updatePatient(@Body PatientDto patient);

    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/save/arv")
    Call<Response> saveARVRefill(@Body ARV arvs);

    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/sync/arv")
    Call<Response> syncARVRefill(@Body List<ARV> arvDtos);

    @Headers("Content-Type: application/json")
    @GET("api/ddd/mobile/pharmacy/{pin}")
    Call<Data> activatePharmacy(@Path("pin") String pin);


    @Headers("Content-Type: application/json")
    @GET("api/ddd/mobile/login/{username}/{password}/{role}")
    Call<Response> login(@Path("username") String username, @Path("password") String password, @Path("role") String role);

    //mobile/patient/{deviceId}/{pin}/{accountUserName}/{accountPassword}
    @Headers("Content-Type: application/json")
    @GET("api/ddd/mobile/patient/{deviceId}/{pin}/{accountUserName}/{accountPassword}")
    Call<Data2> downLoad(@Path("deviceId") String deviceId,
                         @Path("pin") String pin,
                         @Path("accountUserName") String accountUserName,
                         @Path("accountPassword") String accountPassword);


    @Headers("Content-Type: application/json")
    @GET("api/ddd/mobile/facility/{deviceId}/{accountUserName}/{accountPassword}")
    Call<Data> getFacilityCode(@Path("deviceId") String deviceId,
                               @Path("accountUserName") String accountUserName,
                               @Path("accountPassword") String accountPassword);


    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/discontinue/{dateDiscontinue}/{reasonDiscontinued}/{id}")
    Call<Response> discontinued(@Path("dateDiscontinue") String dateDiscontinue,
                                @Path("reasonDiscontinued") String reasonDiscontinued,
                                @Path("id") Long id);


    @Headers("Content-Type: application/json")
    @GET("api/ddd/mobile-patient/all")
    Call<Data> getAllPatients();
//http://20.68.120.202:8080/api/ddd/mobile-patient-id/delete/126}

    @Headers("Content-Type: application/json")
    @DELETE("/api/ddd/mobile-patient-id/delete/{id}")
    Call<Void> deletePatient(@Path("id") Long id);


    @Headers("Content-Type: application/json")
    @GET("/api/ddd/mobile-patient/update-date")
    Call<Void> update(@Query(value = "dateNextRefill") String dateNextRefill,
                      @Query(value = "id") Long id);

    @Headers("Content-Type: application/json")
    @GET("/api/ddd/get-facility-all")
    Call<List<FacilityDto>> getFacility();

    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/save/drug")
    Call<Response> saveDrug(@Body Drug drug);

    @Headers("Content-Type: application/json")
    @POST("api/ddd/mobile/save/inventory")
    Call<Response> saveInventory(@Body IssuedDrug issuedDrug);
}
