package piki.example.com.loginpikiapp.pikitori.domain;

/**
 * Created by bit1 on 2017-01-19.
 */

public class CommentVo {

    private String comment_content;
    private String comment_regdate;
    private Long post_no;
    private Long user_no;
    private String user_profile_url;

    @Override
    public String toString() {
        return "Comment{" +
                "comment_content='" + comment_content + '\'' +
                ", comment_regdate='" + comment_regdate + '\'' +
                ", post_no=" + post_no +
                ", user_no=" + user_no +
                ", user_profile_url='" + user_profile_url + '\'' +
                '}';
    }

    public String getUser_profile_url() {
        return user_profile_url;
    }

    public void setUser_profile_url(String user_profile_url) {
        this.user_profile_url = user_profile_url;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_regdate() {
        return comment_regdate;
    }

    public void setComment_regdate(String comment_regdate) {
        this.comment_regdate = comment_regdate;
    }

    public Long getPost_no() {
        return post_no;
    }

    public void setPost_no(Long post_no) {
        this.post_no = post_no;
    }

    public Long getUser_no() {
        return user_no;
    }

    public void setUser_no(Long user_no) {
        this.user_no = user_no;
    }
}
