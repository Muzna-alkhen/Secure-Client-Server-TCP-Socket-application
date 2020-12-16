import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        File file = new File ("C:\\Users\\HP\\Desktop\\hello.txt");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("first\n");
        writer.write("second\n");
        writer.write("third\n");
        writer.close();



    }
}
