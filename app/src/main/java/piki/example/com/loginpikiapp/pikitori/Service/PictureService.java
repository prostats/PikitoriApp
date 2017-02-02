package piki.example.com.loginpikiapp.pikitori.Service;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.File;
import java.util.List;

import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.domain.PictureVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;

import static piki.example.com.loginpikiapp.pikitori.core.Utils.fromJSON;

/**
 * Created by joohan on 2017-01-19.
 */

public class PictureService {


    private static final String TAG = "PictureService";

    public void makeMovie(List<File> fileList, PostVo post){

        String url = BasicInfo.domain+"mecavo/picture/makemovie";

        HttpRequest request = HttpRequest.post(url);

        for (File f : fileList) {
            request.part("fileList", f.getName(), f);
        }

        for(int i =  0; i< post.getPictureList().size(); i++){
            request.part("pictureNoList",post.getPictureList().get(i).getPicture_no());
            System.out.println(post.getPictureList().get(i).getPicture_no());
        }
        request.part("postNo",post.getPost_no());

        int response = request.code();
        request.ok();
    }

    public List<PictureVo> getPicture(Long user_no){
        String url = BasicInfo.domain+"mecavo/picture/getpictureuseingmap";
        HttpRequest request = HttpRequest.post(url);
        request.part("user_no",user_no);
        int response = request.code();
        PictureService.JSONResultPictureList jsonResult = fromJSON(request, PictureService.JSONResultPictureList.class,TAG);
        return jsonResult.getData();
    }

    private class JSONResultPictureList extends JSONResult<List<PictureVo>>{};
}
