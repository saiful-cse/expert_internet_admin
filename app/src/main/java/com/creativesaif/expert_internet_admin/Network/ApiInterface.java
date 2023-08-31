package com.creativesaif.expert_internet_admin.Network;

import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.Model.Salary;
import com.creativesaif.expert_internet_admin.Model.SalaryWrapper;
import com.creativesaif.expert_internet_admin.Model.Trns;
import com.creativesaif.expert_internet_admin.URL_config;

import java.net.URL;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface{
//
    @POST(URL_config.BASE_URL+URL_config.REGISTER_CLIENT)
    Call<ClientWrapper> getRegistered_client(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.UNREGISTER_CLIENT)
    Call<ClientWrapper> getUnRegistered_client(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.EXPIRED_CLIENT)
    Call<ClientWrapper> getExpired_client(@Body Client client);

    @POST("---")
    Call<ClientWrapper> getOnline_client();

    @POST(URL_config.BASE_URL+URL_config.CLIENT_DETAILS_ID)
    Call<DetailsWrapper> getClientDetailsId(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.EMPLOYEE_MAKE_PAYMENT)
    Call<Trns> employeeMakePayment(@Body Trns trns);

    @POST(URL_config.BASE_URL+URL_config.CLIENT_DETAILS_UPDATE)
    Call<DetailsWrapper> updateDetails(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.CLIENT_REG_UPDATE)
    Call<DetailsWrapper> updateRegistration(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.CLIENT_REG)
    Call<DetailsWrapper> clientRegistration(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.EXPIRING_WARNING_SMS)
    Call<DetailsWrapper> bilExpiringWarningSms(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.SALARY_LIST)
    Call<SalaryWrapper> getSalary(@Body Salary salary);

    @POST(URL_config.BASE_URL+URL_config.SALARY_ADD)
    Call<SalaryWrapper> addSalary(@Body Salary salary);

    @POST(URL_config.BASE_URL + URL_config.SEARCH)
    Call<ClientWrapper> search_data(@Body Client client);

    @POST(URL_config.BASE_URL+URL_config.EXPIRED_CLIENT_DISCONNECT)
    Call<DetailsWrapper> expiredClientDisconnect(@Body Client client);

    @POST("http://103.134.39.218/expnet_api/pppAction.php")
    Call<DetailsWrapper> getPPPAction(@Body Client client);

}
