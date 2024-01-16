package manager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;

import java.util.Scanner;

/**
 * A Class for all file operations and XML marshalling
 */
public class FileOperator {

    private static final Logger logger = LoggerFactory.getLogger("manager.FileOperator");
    private final String PATH;


    public FileOperator(String path){
        PATH = path;

    }
    public Scanner getScanner(String fileName) throws FileNotFoundException, FileSystemException {
        try {
            File file = new File(PATH + fileName);
            if(Files.isReadable(file.toPath())){
                return new Scanner(new File(PATH + fileName));
            }
            else
            {
                logger.warn("Have no permission to read file {}",PATH + fileName);
                throw new FileSystemException("Have no permission to read the file");
            }

        }
        catch (Exception e){
            logger.warn("Can't locate file {}",PATH + fileName);
            throw new FileNotFoundException(String.format("Can't locate file %s in %s", fileName, PATH));
        }
    }


}
