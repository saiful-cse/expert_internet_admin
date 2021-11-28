package com.creativesaif.expert_internet_admin.Network;

import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.Model.Trns;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    String base_url = "http://192.168.1.3:8012/";

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/registered_client.php")
    Call<ClientWrapper> getRegistered_client(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/unregistered_client.php")
    Call<ClientWrapper> getUnRegistered_client(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/expired_client.php")
    Call<ClientWrapper> getExpired_client(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/disabled_client.php")
    Call<ClientWrapper> getDisabled_client(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/enabled_client.php")
    Call<ClientWrapper> getEnabled_client(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/online_client.php")
    Call<ClientWrapper> getOnline_client(@Body Client client);


    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/client_details.php")
    Call<DetailsWrapper> getClientDetails(@Body Client client);
    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/client_details_id.php")
    Call<DetailsWrapper> getClientDetailsId(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/ppp_action.php")
    Call<DetailsWrapper> getPPPAction(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/txn/admin_make_payment.php")
    Call<Trns> adminMakePayment(@Body Trns trns);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/client_details_update.php")
    Call<DetailsWrapper> updateDetails(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/client_registration_update.php")
    Call<DetailsWrapper> updateRegistration(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/client_registration.php")
    Call<DetailsWrapper> clientRegistration(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/sms/bill_expire_warning.php")
    Call<DetailsWrapper> bilExpireWarningSend(@Body Client client);

    @POST(base_url+"api/expert_internet_api/exp-v4.1/client/expired_client_disconnect.php")
    Call<DetailsWrapper> expiredClientDisconnect(@Body Client client);


}
