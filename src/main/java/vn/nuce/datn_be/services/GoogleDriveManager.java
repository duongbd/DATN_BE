package vn.nuce.datn_be.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential.Builder;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.nuce.datn_be.repositories.RoomRepository;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class GoogleDriveManager {

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "datn-2022";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/datn-2022-342616-43a54422b459.p12";
    private static final String SERVICE_ACCOUNT_ID = "serviceaccount@datn-2022-342616.iam.gserviceaccount.com";
    @Value("${datn.google.rootFolder.id}")
    private static String ROOT_FOLDER_ID = "12d5PSJxlpdmDI93q3lva50nmryJmgar2";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static HttpRequestInitializer getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, GeneralSecurityException {
        // Load client secrets.
        InputStream in = GoogleDriveManager.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        // Build flow and trigger user authorization request.
        HttpRequestInitializer getCredentials = new Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(SERVICE_ACCOUNT_ID)
                .setServiceAccountPrivateKeyFromP12File(in)
                .setServiceAccountScopes(SCOPES)
                .build();
        //returns an authorized Credential object.
        return getCredentials;
    }

    public static Drive googleDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        var credential = getCredentials(HTTP_TRANSPORT);
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<File> listFolderContent(String parentId) throws IOException, GeneralSecurityException {
        if (parentId == null) {
            parentId = "root";
        }
        String query = "'" + parentId + "' in parents";
        FileList result = googleDriveService().files().list()
                .setQ(query)
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        return result.getFiles();
    }

    public String searchFolderId(String parentId, String folderName) throws Exception {
        String folderId = null;
        String pageToken = null;
        FileList result = null;
        File fileMetadata = new File();
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setName(folderName);
        do {
            String query = "mimeType = 'application/vnd.google-apps.folder'";
            if (parentId != null) {
                query = query + " and '" + parentId + "' in parents";
            }
//            String spaces = parentId != null ? parentId : "drive";
            result = googleDriveService().files().list().setQ(query)
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                if (file.getName().equalsIgnoreCase(folderName)) {
                    folderId = file.getId();
                }
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null && folderId == null);
        return folderId;
    }

    public String findOrCreateFolder(String parentId, String folderName) throws Exception {
        String folderId = searchFolderId(parentId, folderName);
        // Folder already exists, so return id
        if (folderId != null) {
            return folderId;
        }
        //Folder dont exists, create it and return folderId
        File fileMetadata = new File();
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setName(folderName);

        if (parentId != null) {
            fileMetadata.setParents(Collections.singletonList(parentId));
        }
        return googleDriveService().files().create(fileMetadata)
                .setFields("id")
                .execute()
                .getId();
    }

    public String getFolderId(String path) throws Exception {
        String parentId = null;
        String[] folderNames = path.split("/");
        for (String name : folderNames) {
            parentId = findOrCreateFolder(parentId, name);
        }
        return parentId;
    }

    public String uploadFile(MultipartFile file, String filePath) throws GeneralSecurityException, IOException {
        try {
            String folderId = getFolderId(filePath);
            if (null != file) {
                File fileMetadata = new File();
                fileMetadata.setParents(Collections.singletonList(folderId));
                fileMetadata.setName(file.getOriginalFilename());
                File uploadFile = googleDriveService()
                        .files()
                        .create(fileMetadata, new InputStreamContent(
                                file.getContentType(),
                                new ByteArrayInputStream(file.getBytes()))
                        )
                        .setFields("id").execute();
                return uploadFile.getId();
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return null;
    }

    public void downloadFile(String fileId, OutputStream outputStream) throws IOException, GeneralSecurityException {
        if (fileId != null) {
            googleDriveService().files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }
    }

    public void deleteFile(String fileId) throws Exception {
        googleDriveService().files().delete(fileId).execute();
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Print the names and IDs for up to 10 files.
        GoogleDriveManager googleDriveManager = new GoogleDriveManager();

        try {
            googleDriveManager.deleteFile("12AKIrUz5lC5QFxGVteIpZ4wBief4Zq6Z");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<File> files = googleDriveService().files().list().execute().getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }
}
