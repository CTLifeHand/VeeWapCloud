
import com.veewap.util.JPushUtil;
import org.junit.Test;


public class JPushUtilTest {


	// 业务
    @Test
    public void productionPush() {
        JPushUtil.PUST("15217363900","天气",true, null);
    }

    @Test
    public void testBroadcast() {
//        JPushUtil.PUST(null,"测试广播",false, "VWInfoMessageViewController");
    }

    @Test
    public void testPush() {
        JPushUtil.PUST("15217363900","天气");
    }

    @Test
    public void testPushPage() {
//        JPushUtil.PUST("15217363900","天气",false,"VWInfoMessageViewController");
    }
}
