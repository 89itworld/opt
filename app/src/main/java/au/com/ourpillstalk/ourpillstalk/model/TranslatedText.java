package au.com.ourpillstalk.ourpillstalk.model;

/**
 * Created by dev on 17/5/17.
 */

public class TranslatedText {

    private String statusCode;
    private String statusMessage;
    private String translatedText;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getTranslatedText() {return translatedText;}

    public void setTranslatedText(String translatedText) {this.translatedText = translatedText;}
}
