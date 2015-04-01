package tmpkg;


public class MatcherHelper {
	    
	    public String getKeyword(String text) {
	    	String ret = new String(text);
	    	if(text.contains("is")) {
	    		ret = "is";
	    	} else if(text.contains("am")) {
	    		ret = "am";
	    	} else if(text.contains("are")) {
	    		ret = "are";
	    	} else {
	    		ret = "None";
	    	}
			return ret;
		}
}
