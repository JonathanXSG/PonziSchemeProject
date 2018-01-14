import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.util.Pair;

/**
 * @author Jonathan
 */
public class PonziSchemeMain {

	private static PonziSchemeTree members = new PonziSchemeTree();
	static ArrayList<Pair<Integer ,File>> files = new ArrayList<>();
	static ArrayList<ArrayList<Member>> results = new ArrayList<>();
	static Integer maxValue=0;
	static BufferedReader bReader;
	static BufferedWriter writer;
	int fileCount;
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		String line = "";
		try {
			if(args.length!=0){
				bReader = new BufferedReader(new FileReader(args[0]));
			}
			else{
				bReader = new BufferedReader(new FileReader("input.txt"));
			}
			
			//Read all the txt files from the input.txt
			while((line = bReader.readLine()) != null) {
				if(!line.isEmpty()) {
					String[] fileInfo = line.split(" ");
					files.add(new Pair<Integer,File>(Integer.valueOf(fileInfo[0]), new File(fileInfo[1])));
				}
			}
			
			int fileCount=0;
			/**
			 * Iterates through the files found, reads them to save the Members to a tree,
			 */
			for (Pair<Integer, File> f: files){
				if(!f.getValue().isFile()) {
					System.out.println("##### "+f.getValue().toString() + " Doesn't exist. ####");
					continue;
				}
				maxValue=0;
				fileCount++;
				writer = new BufferedWriter(new FileWriter("output"+fileCount+".txt"));

				if(f.getKey()>0 && readFile(f.getValue())) {
					runCalculations(f.getKey());
					writeOutput();
				}
				else if(f.getKey()==0) {
					writer.write("Maximum seized assets: 0");
				}
				else {
					writer.write("Maximum seized assets: 0");
					writer.newLine();
					writer.write("List is Empty");
				}
				writer.flush();
				System.out.println("output"+fileCount+".txt");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		long endTime = System.nanoTime();
		System.out.println("Finished Run ");
		System.out.println((endTime - startTime)/1000000 + " milliseconds");
	}
	
	/**
	 * Method to ready the contents of the log files
	 * @param file to be read
	 * @return true if the files had data, false if it is empty
	 * @throws IOException if it was unable to read the file
	 */
	private static boolean readFile(File file) throws IOException {
		members = new PonziSchemeTree();
		bReader = new BufferedReader(new FileReader(file));
		String line = "";
		String[] memberInfo;
		Member newMember;
		boolean notEmpty = false;
		while ((line = bReader.readLine()) != null) {
			if (!line.isEmpty()) {
				notEmpty = true;
				memberInfo = line.split("#");
				if (memberInfo.length == 3) {
					newMember = new Member(memberInfo[0], Integer.valueOf(memberInfo[1]), memberInfo[2]);
				} else {
					newMember = new Member(memberInfo[0], Integer.valueOf(memberInfo[1]), null);
				}
				members.addChildToParent(newMember);
			}
		}
		return notEmpty;
	}
	
	/**
	 * Method to run the recursive method of finding the traces on the whole tree
	 * @param num integer representing the max arrests possible
	 */
	private static void runCalculations(int num) {
		results.clear();
		for(Position pos: members.positions()) {
			calcValue(num,pos,new ArrayList<Member>(), 0);
		}
	}

	/**
	 * Recursive method to run through all the possible traces from the first Position provided
	 * @param num integer of the remaining arrests to make
	 * @param pos current Position in the tree
	 * @param trace list of members previously arrested
	 * @param value total value of assets of the arrested members
	 */
	private static void calcValue(int num, Position pos, ArrayList<Member> trace, int value) {
		trace.add(pos.getElement());
		value+=pos.getElement().getValue();
		//There are still arrests to be done
		if(num>1) {
			if(members.isInternal(pos)) {
				//Iterate through children and see if they haven't been arrested
				for(Position child : members.children(pos)) {
					if(trace.indexOf(child.getElement()) == -1) {
						calcValue(num-1,child,new ArrayList<Member>(trace),value);
					}
				}
			}
			//Check the mentor
			if(members.mentor(pos) != null && trace.indexOf(members.mentor(pos).getElement()) == -1) {
				calcValue(num-1,members.mentor(pos),new ArrayList<Member>(trace),value);
			}
			//Check the Sponsor
			if(members.sponsor(pos) != null && trace.indexOf(members.sponsor(pos).getElement()) == -1 
					&& members.sponsor(pos) != (members.mentor(pos))){
				calcValue(num-1,members.sponsor(pos),new ArrayList<Member>(trace),value);
			}
		}
		if(value>= maxValue){
			addTraceResult(trace, value);
		}
	}
	
	/**
	 * Method to add the trace to the list of results 
	 * @param trace list of Members that were arrested
	 * @param value total value of assets of the Members
	 */
	public static void addTraceResult(ArrayList<Member> trace, int value) {
		if(results.isEmpty()) {
			maxValue=value;
			results.add((trace));
		}
		else {
			if(maxValue < value) {
				maxValue=value;
				results.clear();
				results.add((trace));
			}
			else if(maxValue == value ) {
				results.add((trace));
			}
		}
	}
	
	/**
	 * Method to write the output to a file
	 * @throws IOException if it is unable to write to the file
	 */
	private static void writeOutput() throws IOException {
		int lineCount=0;
		writer.append("Maximum seized assets: "+ maxValue);
		for(ArrayList<Member> traces : results) {
			lineCount++;
			writer.newLine();
			writer.append("List "+lineCount+": ");
			Iterator<Member> it = traces.iterator();
			while(it.hasNext()) {
				writer.append(((Member) it.next()).getName());
				if(it.hasNext()) {
					writer.append(", ");
				}
			}
		}
	}

}
