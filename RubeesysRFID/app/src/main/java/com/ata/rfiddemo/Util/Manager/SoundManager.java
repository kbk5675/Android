package com.ata.rfiddemo.Util.Manager;

import android.content.Context;
import android.media.SoundPool;

import com.ata.rfiddemo.R;

public class SoundManager {

    private SoundPool soundPool;
    private int successSoundId;
    private int failSoundId;

    public SoundManager(Context context) {
        // setMaxStreams : 음원 동시 컨트롤 수
        this.soundPool = new SoundPool.Builder().setMaxStreams(2).build();

        // sound resource load
        successSoundId = this.soundPool.load(context, R.raw.success, 1);
        failSoundId    = this.soundPool.load(context, R.raw.fail, 1);
    }

    public void playSuccess() {
        if (soundPool != null) {
            soundPool.play(successSoundId, 1, 1, 0, 0, 1);
        }
    }

    public void playFail() {
        if (soundPool != null) {
            soundPool.play(failSoundId, 1, 1, 0, 0, 1);
        }
    }

    public void close() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
