package com.qd.longchat.bean;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/4 下午2:14
 */
public class QDPreviewBean {
    /**
     * photos : ["https://hiphotos.baidu.com/feed/pic/item/ae51f3deb48f8c544cc15e6a37292df5e0fe7f61.jpg","https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3578956416,4126640024&fm=173&app=49&f=JPEG?w=640&h=670&s=E0127E9E069767E946770C6103007073","https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=438228856,26198140&fm=173&app=25&f=JPG?w=640&h=468&s=217252864CDB28DC24F1B08D0300D002","https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3295041192,3186611689&fm=173&app=49&f=JPEG?w=640&h=538&s=54BA27755547E54D42B091E203002031","https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3868126370,3564906639&fm=173&app=49&f=JPEG?w=550&h=397&s=1F1269CA585A87DE03E0B00A0300E0D3","https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=50509829,2570062848&fm=173&app=49&f=JPEG?w=550&h=404&s=40905D91768D2AEC74B10DE40100E0B1","https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1502308434,3010313019&fm=173&app=49&f=JPEG?w=639&h=401&s=2D004B95C6520FD205B504BD0300B00A","https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=1777293396,2636616745&fm=173&app=49&f=JPEG?w=500&h=251&s=FECA7A2342C1830528B0E8D30100E0B1","https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=52245238,4276895715&fm=173&app=49&f=JPEG?w=640&h=346&s=6D40DA03C4E38CBCA62429DB0300D010","https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=1489022369,2885046701&fm=173&app=49&f=JPEG?w=640&h=850&s=17236CA69A36A6DE97F4ECA30300700B","https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3654725841,462133829&fm=173&app=49&f=JPEG?w=640&h=449&s=2A105287044210F420199D0A0300A011"]
     * index : 0
     */

    private int index;
    private List<String> photos;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
