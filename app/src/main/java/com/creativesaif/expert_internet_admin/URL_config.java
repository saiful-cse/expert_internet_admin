package com.creativesaif.expert_internet_admin;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class URL_config {


    public static final String BASE_URL = "https://baycombd.com/exp-v12.0/";
    //public static final String BASE_URL = "http://192.168.8.80/API/expert_internet_api/exp-v12.0/";

    public static final String PAYBILL_URL = "https://baycombd.com/paybill/info.php?mobile_no=";
    public static final String SEARCH = "client/search.php";
    public static final String PAYMENT_DETAILS = "txn/payment_details.php";
    public static final String DATEWISE_ALL_TXN = "txn/all_txn.php";
    public static final String TOTAL_DEBIT_CREDIT_CASH = "txn/total_credit_debit.php";
    public static final String EMPLOYEE_MAKE_TXN = "txn/employee_make_txn.php";
    public static final String TXN_DETAILS = "txn/txn_details.php";
    public static final String TXN_DELETE = "txn/txn_delete.php";
    public static final String TXN_UPDATE = "txn/txn_update.php";
    public static final String NOTE_VIEW = "note/note_view.php";
    public static final String NOTE_EDIT = "note/note_edit.php";
    public static final String DASHBOARD_READ = "dashboard/read.php";
    public static final String DASHBOARD_VIEW = "dashboard/view.php";
    public static final String AREAWISE_SMS = "sms/areawise_sms_service.php";
    public static final String ENABLE_CLIENT_SMS = "sms/enabled_clients_sms.php";
    public static final String IDWISE_CLIENT_SMS = "sms/idwise_sms_service.php";
    public static final String PHONE_VERIFY = "sms/phone_verify.php";
    public static final String AREA_LOAD = "area/area_list.php";
    public static final String SMS_HISTORY = "sms/sms_history.php";
    public static final String LOGIN = "login/login.php";
    public static final String REGISTER_CLIENT = "client/registered_client.php";
    public static final String UNREGISTER_CLIENT = "client/unregistered_client.php";
    public static final String EXPIRED_CLIENT = "client/expired_client.php";
    public static final String CLIENT_DETAILS_ID = "client/client_details_id.php";
    public static final String EMPLOYEE_MAKE_PAYMENT = "txn/employee_make_payment.php";
    public static final String CLIENT_DETAILS_UPDATE = "client/client_details_update.php";
    public static final String ONU_MAC_UPDATE = "client/onu_mac_update.php";
    public static final String  CLIENT_REG_UPDATE = "client/client_registration_update.php";
    public static final String CLIENT_REG = "client/client_registration.php";
    public static final String EXPIRING_WARNING_SMS = "sms/expiring_clients_sms.php";
    public static final String SALARY_LIST = "txn/salary_list.php";
    public static final String SALARY_ADD = "txn/add_salary.php";
    public static final String EXPIRED_CLIENT_DISCONNECT = "client/expired_client_disconnect.php";
    public static final String UPSTREAM_BILL_LIST = "txn/upstream_bill_list.php?emp_id=";
    public static final String EMPLOYEE_LIST = "employee/employee_list.php";
    public static final String EMPLOYEE_DETAILS = "employee/employee_details.php";
    public static final String EMPLOYEE_DETAILS_UPDATE = "employee/employee_details_update.php";
    public static final String EMPLOYEE_DELETE = "employee/employee_delete.php";
    public static final String DEVICE_URL = "device/device_url.php";
    public static final String DEVICE_URL_UPDATE = "device/device_url_update.php";
    public static final String EMPLOYEE_ADD = "employee/employee_add.php";
    public static final String TASK_PENDING = "task/pending_task.php?employee_id=";
    public static final String TASK_COMPLETED = "task/completed_task.php?employee_id=";
    public static final String TASK_DONE_UPDATE = "task/done_task.php?taskId=";
    public static final String TASK_DELETE = "task/delete_task.php?taskId=";
    public static final String TASK_UPDATE = "task/update_task.php";
    public static final String TASK_ADD = "task/add_task.php";
}
