package com.cashback.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cashback.R;
import com.cashback.models.Advertisement;
import com.cashback.utils.Common;

import java.util.ArrayList;

public class ImageSliderAdapter extends PagerAdapter {

    public static final String TAG = ImageSliderAdapter.class.getSimpleName();
    ArrayList<Advertisement> moAdvertisementList;
    private Context moContext;

    public ImageSliderAdapter(Context foContext, ArrayList<Advertisement> foAdvertisementList) {
        moContext = foContext;
        moAdvertisementList = foAdvertisementList;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        ((ViewPager) container).removeView((ScrollView) object);
    }

    @Override
    public int getCount() {
        return moAdvertisementList.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int fiPosition) {

        Advertisement loAdvertisement = moAdvertisementList.get(fiPosition);


        LayoutInflater inflater = (LayoutInflater) moContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_advert, null);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.svRoot);
        LinearLayout llTop = (LinearLayout) scrollView.findViewById(R.id.llTop);
        ImageView loIvBanner = (ImageView) llTop.findViewById(R.id.ivBanner);

        String lsImageUrl = loAdvertisement.getImageUrl();

        Drawable loPlaceHolder = ActivityCompat.getDrawable(moContext, R.drawable.iv_place_holder);
        Drawable loError = ActivityCompat.getDrawable(moContext, R.drawable.iv_place_holder);

        Common.loadImage(loIvBanner, lsImageUrl, loError, loPlaceHolder);

        loIvBanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (miSliderType == Constants.Slider.DETAIL_SLIDER) {
//                        SharedPreferenceManager loSharedPreferenceManager = new SharedPreferenceManager(moContext);
//                        loSharedPreferenceManager.setSrNumber(moProduct.getSerialNumber());
//                        Intent loIntent = new Intent(moContext, ZoomImageActivity.class);
//                        Bundle loBundle = new Bundle();
//                        loBundle.putSerializable(Constants.IntentKey.OBJ_PRODUCT, moProduct);
//                        loIntent.putExtras(loBundle);
//                        loIntent.putExtra(Constants.IntentKey.POSITION, fiPosition);
//                        moContext.startActivity(loIntent);
//                    }
                }
            });

        ((ViewPager) container).addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == ((ScrollView) object);
    }

}
