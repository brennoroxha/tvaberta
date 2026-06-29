package com.example.util;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.wortise.ads.RevenueData;

public class PopUpAds {
    public static void showInterstitialAds(Context context, int adapterPosition, RvOnClickListener clickListener) {
        if (Constant.isInterstitial) {
            Constant.AD_COUNT += 1;
            if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                switch (Constant.interstitialAdType) {
                    case "admob":
                        AdRequest.Builder builder = new AdRequest.Builder();
                        Constant.AD_COUNT = 0;
                        InterstitialAd.load(context, Constant.interstitialId, builder.build(), new InterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                super.onAdLoaded(interstitialAd);
                                interstitialAd.show((Activity) context);
                                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                        super.onAdFailedToShowFullScreenContent(adError);
                                        clickListener.onItemClick(adapterPosition);
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent();
                                        clickListener.onItemClick(adapterPosition);
                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                super.onAdFailedToLoad(loadAdError);
                                clickListener.onItemClick(adapterPosition);
                            }
                        });
                        break;
                    case "facebook":
                        Constant.AD_COUNT = 0;
                        com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(context, Constant.interstitialId);
                        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                            @Override
                            public void onInterstitialDisplayed(Ad ad) {

                            }

                            @Override
                            public void onInterstitialDismissed(Ad ad) {
                                clickListener.onItemClick(adapterPosition);
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                clickListener.onItemClick(adapterPosition);
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                interstitialAd.show();
                            }

                            @Override
                            public void onAdClicked(Ad ad) {

                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {

                            }
                        };
                        com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).withCacheFlags(CacheFlag.ALL).build();
                        interstitialAd.loadAd(loadAdConfig);
                        break;
                    case "wortise":
                        Constant.AD_COUNT = 0;
                        com.wortise.ads.interstitial.InterstitialAd mInterstitial = new com.wortise.ads.interstitial.InterstitialAd(context, Constant.interstitialId);
                        mInterstitial.setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                            @Override
                            public void onInterstitialRevenuePaid(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd, @NonNull RevenueData revenueData) {

                            }

                            @Override
                            public void onInterstitialImpression(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {

                            }

                            @Override
                            public void onInterstitialFailedToShow(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd, @NonNull com.wortise.ads.AdError adError) {
                                clickListener.onItemClick(adapterPosition);
                            }

                            @Override
                            public void onInterstitialFailedToLoad(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd, @NonNull com.wortise.ads.AdError adError) {
                                clickListener.onItemClick(adapterPosition);
                            }

                            @Override
                            public void onInterstitialClicked(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {

                            }

                            @Override
                            public void onInterstitialDismissed(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                clickListener.onItemClick(adapterPosition);
                            }

                            @Override
                            public void onInterstitialLoaded(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                if (mInterstitial.isAvailable()) {
                                    mInterstitial.showAd();
                                }
                            }

                            @Override
                            public void onInterstitialShown(@NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {

                            }
                        });
                        mInterstitial.loadAd();
                        break;
                }
            } else {
                clickListener.onItemClick(adapterPosition);
            }
        } else {
            clickListener.onItemClick(adapterPosition);
        }
    }
}
