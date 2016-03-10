package app.clientplanner.record.contact_choose.intface;

import android.view.View;

public interface IPinnedHeader {

    int PINNED_HEADER_GONE = 0;

    int PINNED_HEADER_VISIBLE = 1;

    int PINNED_HEADER_PUSHED_UP = 2;

    int getPinnedHeaderState(int position);

    void configurePinnedHeader(View header, int position);

}


