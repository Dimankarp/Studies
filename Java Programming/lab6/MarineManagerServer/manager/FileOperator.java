package manager;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import marine.structure.SpaceMarine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A Class for all file operations and XML marshalling
 */
public class FileOperator {

    private static final Logger logger = LoggerFactory.getLogger("manager.FileOperator");
    private final String PATH;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public FileOperator(String path) throws JAXBException {
        PATH = path;
        try {
            JAXBContext context = JAXBContext.newInstance(SpaceMarine.class, SpaceMarine.SpaceMarineContainer.class);
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (JAXBException e) {
            throw new JAXBException("Couldn't initialize JAXB components!");
        }

    }

    public void exportToXML(Object marineObject) throws JAXBException, IOException {
            marshaller.marshal(marineObject, new FileWriter(PATH + "marines.xml"));
            logger.info("Successfully exported Storage to {}", PATH+"marines.xml");
    }

    public Object importFromXML() throws FileNotFoundException, JAXBException, FileSystemException {
        try {
            Scanner unnecessaryScanner = getScanner("marines.xml");
            StringWriter sw = new StringWriter();

            while(unnecessaryScanner.hasNextLine()){
                sw.append(unnecessaryScanner.nextLine());
            }
            StringReader sr = new StringReader(sw.toString());
            logger.info("Successfully imported Storage from {}", PATH+"marines.xml");
            return unmarshaller.unmarshal(sr);

            //And that's what I could've done, if I wasn't obliged to use bloody Scanner for file reading
            //return unmarshaller.unmarshal(new FileReader(PATH + "marines.xml"));
        } catch (FileNotFoundException e) {
            logger.warn("Couldn't import Space Marines Data from {}, because marines.xml doesn't exist there.", PATH+"marines.xml");
            throw new FileNotFoundException(String.format("Couldn't import Space Marines Data from %s, because marines.xml doesn't exist there.", PATH));
        } catch (JAXBException e) {
            logger.warn("Couldn't import Space Marines Data from {}, because the xml couldn't be parsed.", PATH+"marines.xml");
            throw new JAXBException(String.format("Couldn't import Space Marines Data from %s, because the xml couldn't be parsed.", PATH));
        }
        catch (FileSystemException e){
            logger.warn("Couldn't read Space Marines Data from {}.", PATH+"marines.xml");
            throw new FileSystemException(String.format("Couldn't read Space Marines Data from %s.", PATH));
        }
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
