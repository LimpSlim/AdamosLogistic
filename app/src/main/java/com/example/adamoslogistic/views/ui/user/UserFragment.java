package com.example.adamoslogistic.views.ui.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.adamoslogistic.R;
import com.example.adamoslogistic.generic.DB;
import com.example.adamoslogistic.models.User;

public class UserFragment extends Fragment {

    private LruCache<String, Bitmap> memoryCache;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        TextView name = root.findViewById(R.id.item_attributeName);
        TextView id = root.findViewById(R.id.id);
        ImageView photo = root.findViewById(R.id.photo);
        try {
            User curUser = DB.GetCurrentUser();
            /*if (avatar.type == "url") {
                Picasso.get().load(avatar.avatar).into(photo);
                bitmap = ((BitmapDrawable) photo.getDrawable()).getBitmap();
                Log.d("MyLog", bitmap.toString());
                DB.SetAvatar(bitmap.toString());
            }
            else {
                photo.setImageBitmap(BitmapFactory.decodeFile(avatar.avatar));
            }*/
            Log.d("MyLog", DB.GetAvatar());
            photo.setImageBitmap(decodeBase64(DB.GetAvatar()));
            name.setText(curUser.Name);
            id.setText(curUser.ID.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return root;
    }

    /*public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }*/

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}