package magic.slocksocket.receiver;

import com.amap.api.maps.model.LatLng;


/**
 * Created by chenhaoyu on 2017/4/16.
 */

public interface UpdateUIListenner {

    void updateinfo(String str);

    void setcutdown();

    void shownewmarker(LatLng latLng, String id);


}