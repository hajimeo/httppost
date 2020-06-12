/**
 * Trying to do same as below curl command with Java:
 * curl -v -u admin:admin123 -X POST "http://`hostname -f`:8081/service/rest/v1/components?repository=${_REPO_NAME}" \
 *    -F maven2.groupId=com.mycompany \
 *    -F maven2.artifactId=project-abc \
 *    -F maven2.version=2.1.1 \
 *    -F maven2.asset1=@project-abc-2.1.1.jar \
 *    -F maven2.asset1.extension=jar
 *
 * @see: https://www.baeldung.com/httpclient-post-http-request
 */

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpPostExample
{
  public static void main(String[] args) {
    String user = "admin";
    String pwd = "admin123";

    if (args.length == 0) {
      // TODO: Add proper usage/help message.
      System.out.println("Example:\n    java -jar target/httppost-1.0-SNAPSHOT.jar \"http://localhost:8081/service/rest/v1/components?repository=${_REPO_NAME}\" \"com.example\" \"httppost\" \"1.0-SNAPSHOT\" ./target/httppost-1.0-SNAPSHOT.jar");
      System.exit(1);
    }
    String nexusUrlWithRepoName = args[0];
    String groupId = args[1];
    String artifactId = args[2];
    String version = args[3];
    String filePath = args[4];
    String ext = "jar";
    if(args.length > 5) {
      ext = args[5];
    }

    try {
      File file = new File(filePath);
      String fileName = file.getName();

      // Handle credential and Basic authentication
      //String creds = Base64Encoder.encode(user + ":" + pwd);
      UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, pwd);
      HttpPost httpPost = new HttpPost(nexusUrlWithRepoName);
      httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

      // Create multipart request
      HttpEntity entity = MultipartEntityBuilder.create()
          .addTextBody("maven2.groupId", groupId)
          .addTextBody("maven2.artifactId", artifactId)
          .addTextBody("maven2.version", version)
          .addTextBody("maven2.asset1.extension", ext)
          .addBinaryBody("maven2.asset1", file, ContentType.APPLICATION_OCTET_STREAM, fileName)
          //.addTextBody("maven2.generate-pom", "true")
          .build();
      httpPost.setEntity(entity);

      CloseableHttpClient client = HttpClients.createDefault();
      HttpResponse response = client.execute(httpPost);
      int statusCode = response.getStatusLine().getStatusCode();
      System.err.println("INFO: statusCode: " + statusCode);
    }
    catch (IOException e) {
      System.err.println("WARN: IOException e: " + e);
    }
    catch (Exception ex) {
      System.err.println("ERROR: Exception ex: " + ex);
    }
  }
}
