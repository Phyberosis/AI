public class lang {
	
	//removes punctuation
		public static String rmvPunc(String word) {
			
			String ret = "-1";
			word = word.trim();
			
			if (word.endsWith(",") || word.endsWith(".") || word.endsWith("?")
					|| word.endsWith("!")) {
				
				ret = word.substring(0,word.length()-1);
			}else if(word.endsWith("...")) {
				ret = word.substring(0,word.length()-3);
			}else{
				ret = word;
			}
			if (word.endsWith("'s") || word.endsWith("s'")) {
				if (!word.toLowerCase().equals("it's") && !word.toLowerCase().equals("that's") && !word.toLowerCase().equals("thats")) {
					ret = word.substring(0, word.length()-2);
				}
			}

			return ret;
		}
}
