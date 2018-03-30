package tm.mr.tmtv;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

/**
 * Created by viridis on 29.03.2018.
 */

public class Dialog extends AppCompatDialogFragment {

    RecyclerView rv;
    RvAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_layout_b, container, false);
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        return view;
    }

    public void show(FragmentManager fragmentManager, Map<String, Object> map, RvAdapter.listenerUri listenerUri) {
        super.show(fragmentManager, "");
        adapter = new RvAdapter(map, listenerUri);
    }
}
