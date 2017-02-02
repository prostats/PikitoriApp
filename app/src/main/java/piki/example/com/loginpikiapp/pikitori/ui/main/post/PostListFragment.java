package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PostService;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;

/**
 * Created by admin on 2017-01-22.
 */
public class PostListFragment extends ListFragment {

    ListView listView;
    private PostListArrayAdapter postListArrayAdapter;
    private PostService postService = new PostService();
    private UserService userService = new UserService();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        postListArrayAdapter = new PostListArrayAdapter(getActivity());
        setListAdapter(postListArrayAdapter);

        return inflater.inflate(R.layout.fragment_post_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchUserListAsyncTask(getActivity()).execute();

    }

    private class FetchUserListAsyncTask extends SafeAsyncTask<List<PostVo>> {
        Context context;
        public FetchUserListAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        public List<PostVo> call() throws Exception {
            /* 실제 네트워크 통신 코드 작성 */
            /* 통신은 내부적으로 쓰레드 기반의 비동기 통신 */

            getActivity().findViewById(R.id.progress).setVisibility(View.VISIBLE);
            return postService.fetchPostInfo(context);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
            //throw new RuntimeException();
        }

        @Override
        protected void onSuccess(List<PostVo> postList) throws Exception {
            super.onSuccess(postList);
            postListArrayAdapter.add(postList);
            getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
