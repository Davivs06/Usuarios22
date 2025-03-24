package com.example.usuarios2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchUsers();
    }

    private void fetchUsers() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RandomUserApi api = retrofit.create(RandomUserApi.class);
        Call<UserResponse> call = api.getUsers(20);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getResults();
                    adapter = new UserAdapter(users, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    // API Interface
    interface RandomUserApi {
        @GET("api/")
        Call<UserResponse> getUsers(@Query("results") int count);
    }

    // Retrofit Response Models
    static class UserResponse {
        private List<User> results;
        public List<User> getResults() {
            return results;
        }
    }

    static class User {
        Name name;
        Location location;
        String email;
        String phone;
        String nat;
        Picture picture;

        static class Name {
            String first, last;
        }

        static class Location {
            String country;
            String city;
            Coordinates coordinates;

            static class Coordinates {
                String latitude;
                String longitude;
            }
        }

        static class Picture {
            String thumbnail;
            String large;
        }
    }

    // RecyclerView Adapter
    static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


        private final List<User> users;
        private final Context context;

        public UserAdapter(List<User> users, Context context) {
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = users.get(position);
            String fullName = user.name.first + " " + user.name.last;

            holder.nameTextView.setText(fullName);
            holder.countryTextView.setText(user.location.country);
            holder.emailTextView.setText(user.email);
            Glide.with(context).load(user.picture.thumbnail).into(holder.imageView);

            // ✅ Aquí va el código para abrir la nueva actividad
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra("name", fullName);
                intent.putExtra("email", user.email);
                intent.putExtra("phone", user.phone);
                intent.putExtra("location", user.location.city + ", " + user.location.country);
                intent.putExtra("photo", user.picture.large);
                intent.putExtra("countryCode", user.nat);
                intent.putExtra("latitude", user.location.coordinates.latitude);
                intent.putExtra("longitude", user.location.coordinates.longitude);
                context.startActivity(intent);
            });
        }


        @Override
        public int getItemCount() {
            return users.size();
        }

        static class UserViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView nameTextView, countryTextView, emailTextView;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageUser);
                nameTextView = itemView.findViewById(R.id.textName);
                countryTextView = itemView.findViewById(R.id.textCountry);
                emailTextView = itemView.findViewById(R.id.textEmail);
            }
        }
    }

}

