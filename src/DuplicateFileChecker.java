import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;
import org.json.JSONObject;
import org.json.JSONArray;

public class DuplicateFileChecker {
	public static void checkFiles(Path directory, HashMap<String, ArrayList<String>> map) {

		try (Stream<Path> list = Files.walk(directory)) {
			list.filter(Files::isRegularFile).forEach(path -> {
				try {
					if (map.containsKey(path.getFileName().toString())) {
						map.get(path.getFileName().toString()).add(path.getParent().toString());
					} else {
						ArrayList<String> newList = new ArrayList<String>();
						newList.add(path.getParent().toString());
						map.put(path.getFileName().toString(), newList);
					}
				} catch (Exception e) {
					System.out.println("Error processing file: " + path + " Message: " + e.getMessage());
				} 

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UncheckedIOException e){
			System.out.println("Error processing file or directory: " + e.getMessage());
		}
		return;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		System.out.print("Enter directory to scan: ");
		String inputDir = input.nextLine();
		Path directory = Paths.get(inputDir);
		if (!Files.isDirectory(directory)) {
			System.out.println("File not a directory");
		}
		HashMap<String, ArrayList<String>> mainMap = new HashMap<String, ArrayList<String>>();
		checkFiles(directory, mainMap);
		JSONObject json = new JSONObject("{}");
		for (String value : mainMap.keySet()) {
			ArrayList<String> paths = mainMap.get(value);
			if (paths.size() > 1) {
				System.out.println(value);
				json.put(value, new JSONArray(paths));
				paths.forEach(dir -> {
					System.out.println("\t" + dir);
				});
				System.out.println();
			}
		}
		Path saveFile = directory.resolve("output.json");
		try (FileWriter file = new FileWriter(saveFile.toFile())) {
            file.write(json.toString(4));  // pretty print with 4-space indent
            file.flush();
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
		System.out.println("Check finished");
	}

}
