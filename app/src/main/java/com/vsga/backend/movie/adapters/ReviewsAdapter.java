package com.vsga.backend.movie.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.vsga.backend.movie.R;
import com.vsga.backend.movie.network.review.Review;

/**
 * Created by oiDutS on 07/12/2017.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>{
    Context context;
    List<Review>reviewList;

    public ReviewsAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String author = reviewList.get(position).getAuthor();
        String content = reviewList.get(position).getContent();
        holder.author.setText(author);
        holder.content.setText(content);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public List<Review> getReviewData() {
        return this.reviewList;
    }

    public void setReview(List<Review> movie) {
        reviewList.addAll(movie);
        notifyDataSetChanged();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)TextView author;
        @BindView(R.id.tv_content)TextView content;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
