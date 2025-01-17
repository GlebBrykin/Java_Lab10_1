import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Для 1 задачи введите 1, для второй - 2.");
        System.out.println(">>> ");
        Scanner scanner = new Scanner(System.in);
        if(scanner.nextInt() == 1)
        {
            Restaurant restaurant = new Restaurant();
            for(int i = 1; i <= 10; i++)
            {
                new Visitor(restaurant, "Посетитель " + i).start();
            }
        }
        else
        {
            List<String> urls = Arrays.asList(
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg/2728px-Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/c/c5/Edvard_Munch%2C_1893%2C_The_Scream%2C_oil%2C_tempera_and_pastel_on_cardboard%2C_91_x_73_cm%2C_National_Gallery_of_Norway.jpg",
                    "https://upload.wikimedia.org/wikipedia/commons/8/82/Francis_Picabia%2C_1913%2C_Udnie_%28Young_American_Girl%2C_The_Dance%29%2C_oil_on_canvas%2C_290_x_300_cm%2C_Musée_National_d’Art_Moderne%2C_Centre_Georges_Pompidou%2C_Paris..jpg"
            );
            CompletableFuture[] downloadFutures = urls.stream()
                                                  .map(Main::downloadFile)
                                                  .toArray(CompletableFuture[]::new);
            CompletableFuture<Void> allOf = CompletableFuture.allOf(downloadFutures);
            allOf.whenComplete((result, throwable) ->
            {
                if(throwable != null)
                {
                    System.err.println("Ошибка при скачивании файлов: " + throwable.getMessage());
                }
                else
                {
                    System.out.println("Все файлы успешно скачаны!");
                }
            });
            allOf.join();
        }
    }

    private static CompletableFuture<Void> downloadFile(String fileUrl)
    {
        return CompletableFuture.runAsync(() ->
        {
            try
            {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if(connection.getResponseCode() != 200)
                {
                    throw new IOException("Ошибка при скачивании файла: " + connection.getResponseCode());
                }
                String fileName = Paths.get(url.getPath()).getFileName().toString();
                try(InputStream in = new BufferedInputStream(connection.getInputStream());
                    FileOutputStream out = new FileOutputStream(fileName))
                {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while((bytesRead = in.read(buffer, 0, buffer.length)) != -1)
                    {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Файл скачан: " + fileName);
            }
            catch(IOException e)
            {
                throw new RuntimeException("Ошибка при скачивании файла: " + fileUrl, e);
            }
        });
    }
}