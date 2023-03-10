package manager;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import marine.structure.SpaceMarine;




import java.io.*;

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
            return new Scanner(new File(PATH + fileName));
        }
        catch (Exception e){
            throw new FileNotFoundException(String.format("Can't locate file %s in %s", fileName, PATH));
        }
    }


}
