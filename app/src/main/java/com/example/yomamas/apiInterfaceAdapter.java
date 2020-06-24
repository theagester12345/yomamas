package com.example.yomamas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class apiInterfaceAdapter extends RecyclerView.Adapter<apiInterfaceAdapter.apiInterfaceViewHolder> {


    private List<apiInterface> apiInterfaceList;
    private Context context;
    private ItemClickListener onitemclick;
    private View itemView;

    public apiInterfaceAdapter(List<apiInterface> apiInterfaceList,Context context) {
        this.context = context;
        this.apiInterfaceList = apiInterfaceList;
    }

    @NonNull
    @Override
    public apiInterfaceAdapter.apiInterfaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      itemView  = LayoutInflater.from(context).inflate(R.layout.order_layout,parent,false);


        return new apiInterfaceAdapter.apiInterfaceViewHolder(itemView);
    }

    public void setClicklistener(ItemClickListener itemClickListener){
        onitemclick= itemClickListener;


    }

    @Override
    public void onBindViewHolder(@NonNull apiInterfaceAdapter.apiInterfaceViewHolder holder, int position) {
        holder.store_id.setText(apiInterfaceList.get(position).getStore_id());
        holder.order_id.setText(apiInterfaceList.get(position).getOrder_id());
        holder.total_price__number.setText(apiInterfaceList.get(position).getTotal_price__number());
    }

    @Override
    public int getItemCount() {
        return apiInterfaceList.size();
    }

    public class apiInterfaceViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView store_id,order_id,total_price__number;


        public apiInterfaceViewHolder (View view){
            super(view);
            store_id = view.findViewById(R.id.store_id);
            order_id = view.findViewById(R.id.order_id);
            total_price__number = view.findViewById(R.id.total_price__number);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onitemclick != null){
                        onitemclick.onClick(v,getAdapterPosition());
                    }
                }
            });


        }






    }
}
