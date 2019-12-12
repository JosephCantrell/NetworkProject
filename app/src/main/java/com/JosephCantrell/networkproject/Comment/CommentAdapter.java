package com.JosephCantrell.networkproject.Comment;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import androidx.annotation.LayoutRes;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;

        import com.JosephCantrell.networkproject.R;

        import java.util.ArrayList;
        import java.util.List;

public class CommentAdapter extends ArrayAdapter<CommentDataSet> {

    private Context mContext;
    private List<CommentDataSet> commentData = new ArrayList<>();

    public CommentAdapter(@NonNull Context context, @LayoutRes ArrayList<CommentDataSet> list) {
        super(context, 0, list);
        mContext = context;
        commentData = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.listview_comment,parent,false);

        CommentDataSet currentItem = commentData.get(position);


        TextView email = (TextView)listItem.findViewById(R.id.TV_LV_Comment_Email);
        email.setText(currentItem.getEmail());

        TextView title = (TextView)listItem.findViewById(R.id.TV_LV_Comment_Title);
        title.setText(currentItem.getTitle());

        TextView body = (TextView)listItem.findViewById(R.id.TV_LV_Comment_Body);
        body.setText(currentItem.getBody());

        return listItem;
    }

}
