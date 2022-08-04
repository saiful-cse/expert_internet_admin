package com.creativesaif.expert_internet_admin.Network;

import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.Model.Trns;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

//    String base_url = "http://192.168.1.5/";
//    String second_path = "api/expert_internet_api/";
//    String api_version = "exp-v4.4/";

    String base_url = "https://creativesaif.com/";
    String second_path = "api/";
    String api_version = "exp-v4.4/";

    @POST(base_url+second_path+api_version+"client/registered_client.php")
    Call<ClientWrapper> getRegistered_client(@Body Client client);

    @POST(base_url+second_path+api_version+"client/unregistered_client.php")
    Call<ClientWrapper> getUnRegistered_client(@Body Client client);

    @POST(base_url+second_path+api_version+"client/expired_client.php")
    Call<ClientWrapper> getExpired_client(@Body Client client);

    @POST(base_url+second_path+api_version+"client/online_client.php")
    Call<ClientWrapper> getOnline_client(@Body Client client);

    @POST(base_url+second_path+api_version+"client/client_details.php")
    Call<DetailsWrapper> getClientDetails(@Body Client client);

    @POST(base_url+second_path+api_version+"client/client_details_id.php")
    Call<DetailsWrapper> getClientDetailsId(@Body Client client);

    @POST(base_url+second_path+api_version+"txn/admin_make_payment.php")
    Call<Trns> adminMakePayment(@Body Trns trns);

    @POST(base_url+second_path+api_version+"client/client_details_update.php")
    Call<DetailsWrapper> updateDetails(@Body Client client);

    @POST(base_url+second_path+api_version+"client/client_registration_update.php")
    Call<DetailsWrapper> updateRegistration(@Body Client client);

    @POST(base_url+second_path+api_version+"client/client_registration.php")
    Call<DetailsWrapper> clientRegistration(@Body Client client);

    @POST(base_url+second_path+api_version+"/sms/expired_clients_sms.php")
    Call<DetailsWrapper> bilExpireWarningSend(@Body Client client);

    @POST(base_url+second_path+api_version+"search/search.php")
    Call<ClientWrapper> search_data(@Body Client client);

    @POST(base_url+second_path+api_version+"/sms/expired_client_disconnect_sms.php")
    Call<DetailsWrapper> expiredClientDisconnectSms(@Body Client client);

    @POST("http://mt.baycombd.com/expnet_api/pppAction.php")
    Call<DetailsWrapper> getPPPAction(@Body Client client);


}
