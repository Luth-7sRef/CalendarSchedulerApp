import java.io.*;
import java.util.*;
import java.util.zip.*;

public class FileHandler {
    private static final String DATA_DIR = "data";
    public static final String EVENT_FILE = DATA_DIR + "/event.csv";
    public static final String RECUR_FILE = DATA_DIR + "/recurrent.csv";

    public FileHandler() {
        new File(DATA_DIR).mkdirs(); // Ensure folder exists
    }

    public List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) { // Skip header in logic if needed
                if(!line.trim().isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public void writeLines(String filePath, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (String line : lines) pw.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // REQUIREMENT 4: Backup (Zip)
    public void backup(String backupPath) {
        try (FileOutputStream fos = new FileOutputStream(backupPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            addToZip(EVENT_FILE, zos);
            addToZip(RECUR_FILE, zos);
            System.out.println("Backup completed to: " + backupPath);
        } catch (IOException e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    private void addToZip(String srcFile, ZipOutputStream zos) throws IOException {
        File file = new File(srcFile);
        if (!file.exists()) return;
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName()); // Store just filename, not path
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) zos.write(bytes, 0, length);
            zos.closeEntry();
        }
    }

    // REQUIREMENT 4: Restore
    public void restore(String backupPath) {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(backupPath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(DATA_DIR + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            System.out.println("Restore successful. Please restart application to load new data.");
        } catch (IOException e) {
            System.out.println("Restore failed: " + e.getMessage());
        }
    }
}