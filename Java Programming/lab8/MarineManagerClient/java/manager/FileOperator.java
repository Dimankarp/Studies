package manager;

import java.io.*;

import java.nio.file.Path;
import java.util.Scanner;

/**
 * A Class for all file operations
 */
public class FileOperator {

    private final String PATH;


    public FileOperator(String path){
        PATH = path;
    }

    public Scanner getScanner(String fileName) throws FileNotFoundException {
        try {

            return new Scanner(new File(Path.of(PATH, fileName).toUri()));
        }
        catch (Exception e){
            throw new FileNotFoundException(String.format("Can't locate file %s in %s", fileName, PATH));
        }
    }


}
