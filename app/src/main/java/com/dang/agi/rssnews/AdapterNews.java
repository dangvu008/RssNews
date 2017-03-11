package com.dang.agi.rssnews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by DANG on 2/25/2017.
 */

public class AdapterNews extends RecyclerView.Adapter<AdapterNews.BaiVietViewHolder> {
    Context context;
    List<BaiViet> list;

    public AdapterNews(Context context, List<BaiViet> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public BaiVietViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_tintuc,parent,false);
        return new BaiVietViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaiVietViewHolder holder, int position) {
        final BaiViet baiViet = list.get(position);
        holder.textViewTitle.setText(baiViet.getTitle());
        holder.textViewPubDate.setText(baiViet.getPubDate());
        holder.textViewDescription.setText(baiViet.getDescription());
        new LoadImageAsynTask(holder.img).execute(baiViet.getImage());

        holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(context,Detail.class);
                inten.putExtra("link",baiViet.getLink());
                context.startActivity(inten);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }


    class BaiVietViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle,textViewDescription,textViewPubDate;
        ImageView img;
        public BaiVietViewHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescript);
            textViewPubDate = (TextView) itemView.findViewById(R.id.textViewTime);
            img = (ImageView) itemView.findViewById(R.id.imageViewNew);
        }
    }
    class LoadImageAsynTask extends AsyncTask<String,Bitmap,Bitmap>{

        ImageView img;

        public LoadImageAsynTask(ImageView img) {
            this.img = img;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String strUrl = params[0];
            Bitmap mImage = null;
            try {
                URL url = new URL(strUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                mImage = BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return mImage;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            img.setImageBitmap(bitmap);
        }
    }
}
