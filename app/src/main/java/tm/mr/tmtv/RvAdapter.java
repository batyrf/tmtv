package tm.mr.tmtv;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by viridis on 30.03.2018.
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvViewHolder> {

    Map<String, Object> map = new HashMap<>();
    listenerUri listenerUri;

    public RvAdapter(Map<String, Object> map, listenerUri listenerUri) {
        this.map = map;
        this.listenerUri = listenerUri;
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new RvViewHolder(itemView, new listenerIndex() {
            @Override
            public void onItemClick(int position) {
                String sKey = map.keySet().toArray()[position].toString();
                Uri uri = Uri.parse(map.get(sKey).toString());
                listenerUri.onChannelChange(uri);
            }
        });
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, int position) {
        String s = map.keySet().toArray()[position].toString();
        holder.tv.setText(s);
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    public class RvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv;
        listenerIndex listenerIndex;

        public RvViewHolder(View itemView, listenerIndex listenerIndex) {
            super(itemView);
            this.listenerIndex = listenerIndex;
            tv = itemView.findViewById(R.id.tv);
            tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerIndex.onItemClick(getAdapterPosition());
        }
    }

    interface listenerIndex {
        void onItemClick(int position);
    }

    interface listenerUri {
        void onChannelChange(Uri uri);
    }

}
