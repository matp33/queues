

package utilities;

import dto.ClientArrivalEventDTO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FileAnalyzer {

    public static SortedSet<ClientArrivalEventDTO> analyze(File file) throws IOException{
        
        SortedSet<ClientArrivalEventDTO> clientArrivalEventDTOS = new TreeSet<>(Comparator.comparing(ClientArrivalEventDTO::getArrivalTime));

        int lineNumber = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = br.readLine())!=null){
                if (!line.contains(",")){
                    throw new IllegalArgumentException(String.format("Line %d does not contain comma", lineNumber));
                }
                String[] values = line.split(",");
                if (values.length!=3){
                    throw new IllegalArgumentException(String.format("Line %d contains %d values, but should be exactly 3", lineNumber, values.length));
                }
                double arrivalTime = Double.parseDouble(values[0]);
                int queueNumber = Integer.parseInt(values[1]);
                double timeInCheckout = Double.parseDouble(values[2]);
                clientArrivalEventDTOS.add(new ClientArrivalEventDTO(timeInCheckout, arrivalTime, queueNumber));
                lineNumber++;
            }
        }

        return clientArrivalEventDTOS;

    }

}
