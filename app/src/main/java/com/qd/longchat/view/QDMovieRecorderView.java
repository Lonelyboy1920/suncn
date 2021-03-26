package com.qd.longchat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.qd.longchat.R;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDDateUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by skynight on 2016/12/5.
 */
@SuppressLint("NewApi")
public class QDMovieRecorderView extends LinearLayout implements MediaRecorder.OnErrorListener {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private final static int SIZE_1 = 640;
    private final static int SIZE_2 = 480;

    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mRecordFile = null;// 文件
    private int cameraPosition = 1;//1代表后置摄像头，2代表前置

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口
    private Context context;
    private OnNoPermissionClickListener noPermissionClickListener;

    public void setNoPermissionClickListener(OnNoPermissionClickListener noPermissionClickListener){
        this.noPermissionClickListener = noPermissionClickListener;
    }

    public QDMovieRecorderView(Context context) {
        this(context, null);
    }

    public QDMovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public QDMovieRecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs, defStyleAttr);
    }

    /**
     * 初始化各项组件
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyleAttr, 0);
        mWidth = typedArray.getInteger(R.styleable.MovieRecorderView_video_width, 640);// 默认640
        mHeight = typedArray.getInteger(R.styleable.MovieRecorderView_video_height, 480);// 默认480
        isOpenCamera = typedArray.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);// 默认打开
        mRecordMaxTime = typedArray.getInteger(R.styleable.MovieRecorderView_record_max_time, 800);

        View view = LayoutInflater.from(context).inflate(R.layout.im_movie_recorder_view, this);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.shoot_surfaceview);

//        setSurfaceParams(mSurfaceView);
        doStartSize();
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        typedArray.recycle();
    }

    private class CustomCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if (!isOpenCamera)
                return;
            try {
                initCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }
    }

    /**
     * 初始化摄像头
     *
     * @throws IOException
     */
    @SuppressLint("NewApi")
    private void initCamera() throws IOException {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        if (mCamera != null)
            freeCameraResource();
        try {
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                if (cameraPosition == 1) {
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
                        mCamera = Camera.open(i);
                } else if (cameraPosition == 2) {
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                        mCamera = Camera.open(i);
                }
            }
        } catch (Exception e) {
            freeCameraResource();
        }
        try {
            if (mCamera == null)
                return;
            setCameraParams();
            mCamera.enableShutterSound(false);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            mCamera.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置相机各尺寸
     */
    private void setCameraParams() {
        if (cameraPosition==2){
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : sizes) {
            int width = size.width;
            int height = size.height;
            if ((width/height) == (SIZE_1)/(SIZE_2)) {
                if (width >= SIZE_1 && height >= SIZE_2) {
                    mWidth = width;
                    mHeight = height;
                    break;
                }
            }
        }
//        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
//        List<Camera.Size> tsizeList = parameters.getSupportedPictureSizes();
//        Camera.Size previewSize = QDCameraUtil.getInstance().getPreviewSize(sizeList, 250, getRate());
//        Camera.Size pictureSize = QDCameraUtil.getInstance().getPictureSize(tsizeList, 700, getRate());
        parameters.setPreviewSize(SIZE_1, SIZE_2); //获得摄像区域的大小
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH); //闪光灯
        parameters.setRotation(90);
//        parameters.setPictureSize(pictureSize.width, pictureSize.height);//设置拍出来的屏幕大小
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(parameters);
    }

    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.lock();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (cameraPosition == 1) {
            cameraPosition = 2;
        } else if (cameraPosition == 2) {
            cameraPosition = 1;
        }
        try {
            initCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 录制参数初始化
     *
     * @throws IOException
     */
    private void initRecorder() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null) {
            mMediaRecorder.setCamera(mCamera);
        }
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式+-
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// 视频录制格式
        mMediaRecorder.setOutputFile(mRecordFile.getAbsolutePath());
//             设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
//        if ("MI 5s".equalsIgnoreCase(android.os.Build.MODEL)) {
//            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
//            mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
//            mMediaRecorder.setVideoEncodingBitRate(3 * 1280 * 960);// 设置帧频率，然后就清晰了
//        } else {
//            mMediaRecorder.setVideoSize(SIZE_1, SIZE_2);// 设置分辨率：
//            mMediaRecorder.setVideoFrameRate(20);// 帧
//            mMediaRecorder.setVideoEncodingBitRate(3 * SIZE_1 * SIZE_2);// 设置帧频率，然后就清晰了
//        }
//        mMediaRecorder.setVideoSize(mWidth, mHeight);
//        mMediaRecorder.setVideoEncodingBitRate(3 * mWidth * mHeight);// 设置帧频率，然后就清晰了
        setSize();

        if (cameraPosition == 1) {
            mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
        } else {
            mMediaRecorder.setOrientationHint(270);
        }
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSize() {
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
            mMediaRecorder.setVideoEncodingBitRate(5 * profile.videoFrameWidth * profile.videoFrameHeight);// 设置帧频率，然后就清晰了
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
            mMediaRecorder.setVideoEncodingBitRate(5 * profile.videoFrameWidth * profile.videoFrameHeight);// 设置帧频率，然后就清晰了
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_QVGA)) {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
            mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
            mMediaRecorder.setVideoEncodingBitRate(5 * profile.videoFrameWidth * profile.videoFrameHeight);// 设置帧频率，然后就清晰了
        } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_CIF)) {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_CIF);
            mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
            mMediaRecorder.setVideoEncodingBitRate(5 * profile.videoFrameWidth * profile.videoFrameHeight);// 设置帧频率，然后就清晰了
        } else {
            mMediaRecorder.setVideoSize(SIZE_1, SIZE_2);// 设置分辨率：
            mMediaRecorder.setVideoFrameRate(20);// 帧
            mMediaRecorder.setVideoEncodingBitRate(5 * SIZE_1 * SIZE_2);// 设置帧频率，然后就清晰了
        }
    }

    /**
     * 创建文件
     */
    private void createRecordDir() {
        File sampleDir = new File(QDStorePath.MSG_VIDEO_PATH);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        } else if (sampleDir.isFile()) {
            sampleDir.delete();
            sampleDir.mkdirs();
        }
        // 创建文件
        try {
            String fileName = QDDateUtil.dateToString(new Date(), "yyyyMMddhhmmss") + ".mp4";
            mRecordFile = new File(sampleDir, fileName);
        } catch (Exception e) {
        }
    }

    /**
     * 开始录制，达到指定时间后回调接口
     *
     * @param onRecordFinishListener
     */
    public void startRecorder(final OnRecordFinishListener onRecordFinishListener) {
        this.mOnRecordFinishListener = onRecordFinishListener;
        createRecordDir();
        try {
            if (mCamera == null) {
                initCamera();
            }
            initRecorder();
            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    ++mTimeCount;
                    if (mTimeCount == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止拍摄
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMediaRecorder.setPreviewDisplay(null);
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public File getmRecordFile() {
        return mRecordFile;
    }


    /**
     * 录制完成回调接口
     */
    public interface OnRecordFinishListener {
        void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏幕的宽度px
     *
     * @param context 上下文
     * @return 屏幕宽px
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度px
     *
     * @param context 上下文
     * @return 屏幕高px
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }


    public static void setViewSize(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (null == layoutParams)
            return;
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    /**
     * 因为录制改分辨率的比例可能和屏幕比例一直，所以需要调整比例显示
     */
    private void doStartSize() {
        int screenWidth = getScreenWidth(context);
        int screenHeight = getScreenHeight(context);
        setViewSize(mSurfaceView, screenWidth * SIZE_1 / SIZE_2, screenHeight);
    }

    /**
     * 设置SurfaceView宽高
     */
    private float getRate() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        float width = wm.getDefaultDisplay().getWidth() + 0.00f;
        float height = wm.getDefaultDisplay().getHeight() + 0.00f;
        float rate = height / width;
        return (float) (Math.round(rate * 1000)) / 1000;
    }

    /**
     * 无录像权限时通知关闭界面的监听接口
     */
    public interface OnNoPermissionClickListener {
        void onNoPermissionClick();
    }
}
