package manager;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import manager.structure.SpaceMarine;



import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A Class for all file operations and XML marshalling
 */
public class FileOperator {

    private final String PATH;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public FileOperator(String path) {
        PATH = path;
        try {
            JAXBContext context = JAXBContext.newInstance(SpaceMarine.class, SpaceMarine.SpaceMarineContainer.class);
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void exportToXML(Object marineObject) {
        try {
            marshaller.marshal(marineObject, new FileWriter(PATH + "marines.xml"));
        } catch (IOException | JAXBException e) {
            System.out.println(e.getMessage());
        }

    }

    public Object importFromXML() throws FileNotFoundException {
        try {
            Scanner unnecessaryScanner = new Scanner(new File(PATH + "marines.xml"));
            StringWriter sw = new StringWriter();

            while(unnecessaryScanner.hasNextLine()){
                sw.append(unnecessaryScanner.nextLine());
            }
            StringReader sr = new StringReader(sw.toString());
            return unmarshaller.unmarshal(sr);

            //And that's what I could've done, if I wasn't obliged to use bloody Scanner for file reading
            //return unmarshaller.unmarshal(new FileReader(PATH + "marines.xml"));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(String.format("Couldn't import Space Marines Data from %s, because marines.xml doesn't exist there.", PATH));
        } catch (JAXBException e) {
            System.out.println(e.getMessage());
        }
        return null;
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
