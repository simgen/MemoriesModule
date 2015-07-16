package com.wishnuu.photoweaver.events;

import android.os.Bundle;

public interface DialogListener {

    public void onStart(Bundle values);

    /**
     * Called when a dialog completes. Executed by the thread that initiated the
     * dialog.
     *
     * @param values Key-value string pairs extracted from the response.
     */
    public void onComplete(Bundle values);

    /**
     * Called when a dialog has an error. Executed by the thread that initiated
     * the dialog.
     */
    public void onError(ListenerError e);

    /**
     * Called when a dialog is canceled by the user. Executed by the thread that
     * initiated the dialog.
     */
    public void onCancel();

    /**
     * Called when a dialog is closed by user by pressing back key
     */
    public void onBack();
}
