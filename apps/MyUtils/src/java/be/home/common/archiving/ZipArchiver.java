package be.home.common.archiving;

import be.home.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ZipArchiver extends Archiver {
    private FileSystem zipfs;
    private ExecutorService es = Executors.newFixedThreadPool(4);
    private Visitor visitor = new Visitor();
    private Path basePath;

    class Callable implements java.util.concurrent.Callable<Integer> {
        private Path file;
        private BasicFileAttributes attrs;

        Callable(Path file, BasicFileAttributes attrs) {
            super();
            this.file = file;
            this.attrs = attrs;
        }

        @Override
        public Integer call()  {
            // copy input file to ZipFileSystem
            if (attrs.isDirectory()){
                Path absolutePath = file;
                Path relativePath = basePath.relativize(absolutePath);
                try {
                    Files.createDirectories(zipfs.getPath(relativePath.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Path absolutePath = file.getParent();
                Path relativePath = basePath.relativize(absolutePath);
                // absoluteFilename = file.getFileName().toString();
                Path zipFile = zipfs.getPath(relativePath + File.separator + file.getFileName().toString());
                try {
                    Files.copy(file, zipFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return 0;
        }
    }

    class Visitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            es.submit(new Callable(file, attrs));
            return FileVisitResult.CONTINUE;
        }

        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){
            es.submit(new Callable(dir, attrs));
            return FileVisitResult.CONTINUE;
        }

    }

    public ZipArchiver(String inputDir, String outputFile) {
        super(inputDir, outputFile);
    }

    private void createZipFileSystem() throws IOException {
        // setup ZipFileSystem
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        Path outputPath = Paths.get(this.getOutputFile());
        URI fileUri = outputPath.toUri();
        try {
            URI zipUri = new URI("jar:" + fileUri.getScheme(), fileUri.getPath(), null);
            zipfs = FileSystems.newFileSystem(zipUri, env);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setEs(ExecutorService es) {
        this.es = es;
    }

    @Override
    public void run() {
        try {
            this.createZipFileSystem();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileSystem fs = FileSystems.getDefault();
        FileSystemProvider provider = fs.provider();

        System.out.println("Provider: " + provider.toString());
        System.out.println("Open: " + fs.isOpen());
        System.out.println("Read Only: " + fs.isReadOnly());

        Iterable<Path> rootDirectories = fs.getRootDirectories();

        System.out.println("\nRoot Directories \n-----------------");

        for (Path path : rootDirectories)
            System.out.println(path);

        Iterable<FileStore> fileStores = fs.getFileStores();

        System.out.println("\nFile Stores \n-------------");

        for (FileStore fileStore : fileStores)
            System.out.println(fileStore.name());


        if (Files.isDirectory(fs.getPath(this.getInputDir()))) {
            basePath = Paths.get(this.getInputDir());

            // walk input directory using our visitor class
            try {
                Files.walkFileTree(fs.getPath(this.getInputDir()), visitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            basePath = Paths.get(this.getInputDir()).getParent();
            Path file = fs.getPath(this.getInputDir());
            BasicFileAttributes attrs = null;
            try {
                attrs = Files.readAttributes(file, BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            es.submit(new Callable(file, attrs));
        }

        // shutdown ExecutorService and block till tasks are complete
        es.shutdown();
        try {
            es.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            zipfs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}