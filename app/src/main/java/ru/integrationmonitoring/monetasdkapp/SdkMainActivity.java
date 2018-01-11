package ru.integrationmonitoring.monetasdkapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class SdkMainActivity extends AppCompatActivity implements OnClickListener {

    private MonetaSdk monetasdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_main);
        monetasdk = new MonetaSdk((WebView) findViewById(R.id.webView), this);
    }

    @Override
    public void onClick(View view) {
        // payment form
        Double mntAmount = 12.00;
        String mntPaymentSystem = "plastic";
        String mntOrderId = MonetaSdk.getOrderId();

        monetasdk.showPaymentFrom(mntOrderId, mntAmount, MonetaSdk.Currency.RUB, mntPaymentSystem);
    }
}
