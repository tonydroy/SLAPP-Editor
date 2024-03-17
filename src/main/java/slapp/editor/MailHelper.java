package slapp.editor;

public class MailHelper {
    public static void generate(String receiver, String subject, String body) {

        try {
            //Open mail client with "receiver", "subject", "message"
            composeEmail(receiver, subject, body);
        }
        catch (Exception err) {EditorAlerts.showSimpleAlert("Cannot Open", "Could not open default email application.  You may still send a message to messaging@slappservices.net with 'SLAPP' in subject line.");

//            err.printStackTrace();
        }
//        System.out.println("Done!");
    }

    public static void composeEmail(String receiver, String subject, String body) throws Exception {
        //Generating mailto-URI. Subject and body (message) has to encoded.
        String mailto = "mailto:" + receiver;
        mailto += "?subject=" + uriEncode(subject);
        mailto += "&body=" + uriEncode(body);

        //Create OS-specific run command
        String cmd = "";
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            cmd = "cmd.exe /c start \"\" \"" + mailto + "\"";
        }
        else if (os.contains("mac")){
            cmd = "open " + mailto;
        }
        else if (os.contains("nix") || os.contains("aix") || os.contains("nux")){
            cmd = "xdg-open " + mailto;
        }
        //Call default mail client with paramters
        Runtime.getRuntime().exec(cmd);

    }

    private static String uriEncode(String in) {
        String out = new String();
        for (char ch : in.toCharArray()) {
            out += Character.isLetterOrDigit(ch) ? ch : String.format("%%%02X", (int)ch);
        }
        return out;
    }


}
