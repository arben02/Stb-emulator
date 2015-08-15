package com.grishberg.stb;

import com.grishberg.interfaces.IInput;
import com.grishberg.interfaces.IOnTickListener;
import com.grishberg.interfaces.IPairing;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import java.text.DateFormat;
import java.util.*;

/**
 * Created by g on 13.08.15.
 */
public class Input implements IInput, RequestHandler, IOnTickListener {
    private static final int STATE_PRESSED = 1;
    private static final int STATE_RELEASED = 2;
    private Player mPlayer;
    private IPairing mParing;
    private final String COMMAND_MUTE = "Input.mute";
    private final String COMMAND_SUBS = "Input.subtitle";
    private final String COMMAND_PLAY_PAUSE = "Input.playPause";
    private final String COMMAND_VOLUME_DOWN = "Input.volumeDown";
    private final String COMMAND_VOLUME_UP = "Input.volumeUp";
    private final String COMMAND_SELECT = "Input.select";
    private final String COMMAND_RIGHT = "Input.right";
    private final String COMMAND_LEFT = "Input.left";
    private final String COMMAND_HOME = "Input.home";
    private final String COMMAND_UP = "Input.up";
    private final String COMMAND_AUDIO = "Input.audio";
    private final String COMMAND_FULLSCREEN = "Input.fullscreen";
    private final String COMMAND_PREV = "Input.prev";
    private final String COMMAND_NEXT = "Input.next";
    private final String COMMAND_MENU = "Input.menu";
    private Timer mTimer;
    private ScheduledTask mTimerTask;
    private String mLastCmd;
    private Map<String, Object> mResultRPC;

    public Input(Player player, IPairing pairing) {
        mPlayer = player;
        mParing = pairing;
        mTimerTask = new ScheduledTask(this);
        mTimer = new Timer();
    }

    @Override
    public void back() {
        mPlayer.stop();
    }

    @Override
    public void up() {

    }

    @Override
    public void down() {

    }

    @Override
    public void home() {

    }

    @Override
    public void left(int state) {
        switch (state) {
            case STATE_PRESSED:
                // start cycle
                mLastCmd = COMMAND_LEFT;
                mTimer.schedule(mTimerTask, 0, 200);
                break;
            case STATE_RELEASED:
                // stop cycle
                mTimer.cancel();
                mPlayer.doSeek();
                break;
        }
    }

    @Override
    public void right(int state) {
        switch (state) {
            case STATE_PRESSED:
                // start cycle
                mLastCmd = COMMAND_RIGHT;
                mTimer.schedule(mTimerTask, 0, 200);
                break;
            case STATE_RELEASED:
                // stop cycle
                mTimer.cancel();
                mPlayer.doSeek();
                break;
        }
    }

    @Override
    public void select() {

    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void volumeUp() {
        mPlayer.volumeUp();
    }

    @Override
    public void volumeDown() {
        mPlayer.volumeDown();
    }

    @Override
    public void playPause() {
        mPlayer.playPause();
    }

    @Override
    public void subtitle() {

    }

    @Override
    public void mute() {
        mPlayer.mute();
    }

    @Override
    public void audio() {

    }

    @Override
    public void fullscreen() {
        mPlayer.fullscreen();
    }

    @Override
    public void prev() {

    }

    @Override
    public void next() {

    }

    @Override
    public void menu() {

    }

    @Override
    public void onTick() {
        switch (mLastCmd) {
            case COMMAND_LEFT:
                mPlayer.left();
                break;

            case COMMAND_RIGHT:
                mPlayer.right();
                break;
        }
    }

    public void release() {

    }
    //--------------- JSONRPC2------------------

    @Override
    public String[] handledRequests() {
        return new String[]{COMMAND_MUTE, COMMAND_SUBS, COMMAND_PLAY_PAUSE, COMMAND_VOLUME_DOWN
                , COMMAND_VOLUME_UP, COMMAND_SELECT, COMMAND_RIGHT, COMMAND_LEFT, COMMAND_HOME
                , COMMAND_UP, COMMAND_AUDIO, COMMAND_FULLSCREEN, COMMAND_PREV, COMMAND_NEXT, COMMAND_MENU};
    }

    @Override
    public JSONRPC2Response process(JSONRPC2Request req, MessageContext messageContext) {
        List params = (List) req.getParams();
        int state = -1;
        switch (req.getMethod()) {
            case COMMAND_AUDIO:
                audio();
                break;
            case COMMAND_FULLSCREEN:
                fullscreen();
                break;
            case COMMAND_HOME:
                home();
                break;
            case COMMAND_MENU:
                menu();
                break;
            case COMMAND_MUTE:
                mute();
                break;
            case COMMAND_NEXT:
                next();
                break;
            case COMMAND_PLAY_PAUSE:
                playPause();
                break;
            case COMMAND_PREV:
                prev();
                break;
            case COMMAND_LEFT:
                state = (int)((long)params.get(0));
                left(state);
                break;
            case COMMAND_RIGHT:
                state = (int)((long)params.get(0));
                right(state);
                break;
            case COMMAND_SELECT:
                select();
                break;
            case COMMAND_SUBS:
                subtitle();
                break;
            case COMMAND_UP:
                up();
                break;
            case COMMAND_VOLUME_DOWN:
                volumeDown();
                break;
            case COMMAND_VOLUME_UP:
                volumeUp();
                break;
            default:
                return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
        }
        return new JSONRPC2Response(mResultRPC, req.getID());
    }

    // timer
    public static class ScheduledTask extends TimerTask {
        private IOnTickListener listener;

        public ScheduledTask(IOnTickListener listener) {
            super();
            this.listener = listener;
        }

        // Add your task here
        public void run() {
            if (listener != null) {
                listener.onTick();
            }
        }
    }
}
