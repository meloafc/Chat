package modelo;

import java.util.HashMap;
import java.util.Map;

public class Emoticon {

    public static final Map<String, String> emoticons;
    
    static {
        emoticons = new HashMap<>();
        emoticons.put(":)", "icon/emoticon_smile.gif");
        emoticons.put(":D", "icon/emoticon_laugh.gif");        
        emoticons.put(":(", "icon/emoticon_sad.gif");        
        emoticons.put(":_(", "icon/emoticon_cry.gif");
        emoticons.put(";)", "icon/emoticon_wink.gif");        
        emoticons.put(":/", "icon/emoticon_meh.gif");
        emoticons.put(":P", "icon/emoticon_tongue.gif");        
        emoticons.put(":O", "icon/emoticon_O.gif");        
        emoticons.put(":dog", "icon/emoticon_dog.gif");
    }
    
    public static boolean isValid(String key) {
        return emoticons.get(key) != null;
    }
}
