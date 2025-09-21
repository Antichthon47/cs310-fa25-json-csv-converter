package edu.jsu.mcis.cs310;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            // Read CSV into OpenCSV
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> rows = reader.readAll();

            if (rows == null || rows.isEmpty()) {
                return "{}";
            }

            // First row is header
            String[] header = rows.get(0);

            JsonArray colHeadings = new JsonArray();
            for (String h : header) {
                colHeadings.add(h);
            }

            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                // ProdNum is first column
                prodNums.add(row[0]);

                JsonArray drow = new JsonArray();
                drow.add(row[1]);                                // Title
                drow.add(Integer.parseInt(row[2]));              // Season as int
                drow.add(Integer.parseInt(row[3]));              // Episode as int
                drow.add(row[4]);                                // Stardate
                drow.add(row[5]);                                // OriginalAirdate
                drow.add(row[6]);                                // RemasteredAirdate

                data.add(drow);
            }

            JsonObject json = new JsonObject();
            json.put("ProdNums", prodNums);
            json.put("ColHeadings", colHeadings);
            json.put("Data", data);

            result = Jsoner.serialize(json);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            // Parse JSON
            JsonObject json = (JsonObject) Jsoner.deserialize(jsonString);

            JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
            JsonArray prodNums = (JsonArray) json.get("ProdNums");
            JsonArray data = (JsonArray) json.get("Data");

            StringWriter sw = new StringWriter();
            CSVWriter writer = new CSVWriter(sw,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            // Write header row
            String[] header = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                header[i] = (String) colHeadings.get(i);
            }
            writer.writeNext(header);

            // Write data rows
            for (int i = 0; i < prodNums.size(); i++) {
                String prodNum = (String) prodNums.get(i);
                JsonArray drow = (JsonArray) data.get(i);

                String[] row = new String[header.length];
                row[0] = prodNum;
                row[1] = (String) drow.get(0);
                row[2] = String.valueOf(drow.get(1));  // Season
                // Pad episode with leading zero
                row[3] = String.format("%02d", ((Number) drow.get(2)).intValue());
                row[4] = (String) drow.get(3);
                row[5] = (String) drow.get(4);
                row[6] = (String) drow.get(5);

                writer.writeNext(row);
            }

            writer.close();
            result = sw.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
