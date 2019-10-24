package com.ipms.arena.interfaces;

/**
 * Created by t-ebcruz on 9/12/2016.
 */
public interface OnReceiveListener {

    /**
     * @param status response code from HTTP request
     * @param result response data from HTTP request
     * @return return true to show default library dialog
     */
    boolean onReceive(int status, String result);
}

