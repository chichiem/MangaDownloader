
public class StringHelper {
	public String ucFirst(String string){
		String newString = string;
		
		String[] stringParts = newString.split(" ");
		
		for(String stringPart : stringParts){

			newString += stringPart.substring(0,1).toUpperCase() + stringPart.substring(1)+" ";
		}
		
		return newString;
	}
}
