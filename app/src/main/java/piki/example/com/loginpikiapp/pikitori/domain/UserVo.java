package piki.example.com.loginpikiapp.pikitori.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class UserVo implements Parcelable {
    private Long user_no;
    private String user_id;
    private String user_password;
    private String user_name;
    private String user_reg_date;
    private Long user_role;
    private Long user_status;
    private String user_profile_url;
    private String user_profile_msg;
    private Long user_post_count;
    private Long user_follower_count;
    private Long user_following_count;
    private int user_social_index;
    private String user_social_id;

    public UserVo(){

    }

    public UserVo(Parcel source) {
        readFromParcel(source);
    }

    public Long getUser_no() {
        return user_no;
    }

    public void setUser_no(Long user_no) {
        this.user_no = user_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_reg_date() {
        return user_reg_date;
    }

    public void setUser_reg_date(String user_reg_date) {
        this.user_reg_date = user_reg_date;
    }

    public Long getUser_role() {
        return user_role;
    }

    public void setUser_role(Long user_role) {
        this.user_role = user_role;
    }

    public Long getUser_status() {
        return user_status;
    }

    public void setUser_status(Long user_status) {
        this.user_status = user_status;
    }

    public String getUser_profile_url() {
        return user_profile_url;
    }

    public void setUser_profile_url(String user_profile_url) {
        this.user_profile_url = user_profile_url;
    }

    public String getUser_profile_msg() {
        return user_profile_msg;
    }

    public void setUser_profile_msg(String user_profile_msg) {
        this.user_profile_msg = user_profile_msg;
    }

    public Long getUser_post_count() {
        return user_post_count;
    }

    public void setUser_post_count(Long user_post_count) {
        this.user_post_count = user_post_count;
    }

    public Long getUser_follower_count() {
        return user_follower_count;
    }

    public void setUser_follower_count(Long user_follower_count) {
        this.user_follower_count = user_follower_count;
    }

    public Long getUser_following_count() {
        return user_following_count;
    }

    public void setUser_following_count(Long user_following_count) {
        this.user_following_count = user_following_count;
    }

    public int getUser_social_index() {
        return user_social_index;
    }

    public void setUser_social_index(int user_social_index) {
        this.user_social_index = user_social_index;
    }

    public String getUser_social_id() {
        return user_social_id;
    }

    public void setUser_social_id(String user_social_id) {
        this.user_social_id = user_social_id;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "user_no=" + user_no +
                ", user_id='" + user_id + '\'' +
                ", user_password='" + user_password + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_reg_date='" + user_reg_date + '\'' +
                ", user_role=" + user_role +
                ", user_status=" + user_status +
                ", user_profile_url='" + user_profile_url + '\'' +
                ", user_profile_msg='" + user_profile_msg + '\'' +
                ", user_post_count=" + user_post_count +
                ", user_follower_count=" + user_follower_count +
                ", user_following_count=" + user_following_count +
                ", user_social_index=" + user_social_index +
                ", user_social_id='" + user_social_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeLong(user_no);
        dest.writeString(user_id);
        dest.writeString(user_password);
        dest.writeString(user_name);
        dest.writeString(user_reg_date);
        dest.writeLong(user_role);
        dest.writeLong(user_status);
        dest.writeString(user_profile_url);
        dest.writeString(user_profile_msg);
        dest.writeLong(user_post_count);
        dest.writeLong(user_follower_count);
        dest.writeLong(user_following_count);
        dest.writeInt(user_social_index);
        dest.writeString(user_social_id);
    }

    private void readFromParcel(Parcel source) {
        user_no = source.readLong();
        user_id = source.readString();
        user_password = source.readString();
        user_name = source.readString();
        user_reg_date = source.readString();
        user_role = source.readLong();
        user_status = source.readLong();
        user_profile_url = source.readString();
        user_profile_msg = source.readString();
        user_post_count = source.readLong();
        user_follower_count = source.readLong();
        user_following_count = source.readLong();
        user_social_index = source.readInt();
        user_social_id = source.readString();
    }

    //Parcelable 객체로 구현하기 위한 Parcelable Method ArrayList구현 등..
    public static final Parcelable.Creator<UserVo> CREATOR = new Parcelable.Creator<UserVo>() {

        @Override
        public UserVo createFromParcel(Parcel source) {
            return new UserVo(source);
        }

        @Override
        public UserVo[] newArray(int size) {
            return new UserVo[size];
        }
    };
}
