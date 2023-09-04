package com.mta.greenguardianapplication.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mta.greenguardianapplication.R;
import com.mta.greenguardianapplication.databinding.ItemContainerUserBinding;
import com.mta.greenguardianapplication.listeners.UserListener;
import com.mta.greenguardianapplication.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private final List<User> users;
    private final UserListener userListener;

    public UserAdapter(List<User> users, UserListener userListener){
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserDate(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserDate(User user){
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            String profileImageUrl = user.image;
            if (profileImageUrl != null && !user.image.isEmpty()) {
                // If there's no profile image, set a default profile picture
                Picasso.get()
                        .load(profileImageUrl)
                        .placeholder(R.drawable.ic_account_profile2) // Placeholder image while loading
                        .error(R.drawable.ic_account_profile2) // Error image if the URL is invalid or loading fails
                        .into(binding.imageProfile);
            }
            else {
                binding.imageProfile.setImageResource(R.drawable.ic_account_profile2);
            }
            binding.getRoot().setOnClickListener(v->userListener.onUserClicked(user));
        }
    }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
