import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.util.Pair;

public class PonziSchemeMain {

	private static PonziSchemeTree members = new PonziSchemeTree();
	static ArrayList<Pair<Integer ,File>> files = new ArrayList<>();
	static ArrayList<ArrayList<Member>> results = new ArrayList<>();
	static Integer maxValue=0;
	static BufferedReader bReader;
	static BufferedWriter writer;
	
	public static void main(String[] args) {
		String line = "";
		try {
			if(args.length!=0){
				bReader = new BufferedReader(new FileReader(args[0]));
			}
			else{
				bReader = new BufferedReader(new FileReader("input.txt"));
			}
			
			//Read all the csv files from the input.txt
			while((line = bReader.readLine()) != null) {
				if(!line.isEmpty()) {
					String[] fileInfo = line.split(" ");
					files.add(new Pair<Integer,File>(Integer.valueOf(fileInfo[0]), new File(fileInfo[1])));
				}
			}
			
			int fileCount=0;
			/**Iterates through the files found, reads them to save the customers to a SLL,
			 * it runs the four methods of serving and outputs the results.
			 */
			for (Pair<Integer, File> f: files){
				maxValue=0;
				fileCount++;
				writer = new BufferedWriter(new FileWriter("output"+fileCount+".txt"));

				if(readFile(f.getValue())) {
					runCalculations(f.getKey());
					writeOutput();
				}
				else {
					writer.write("Maximum seized assets: 0");
					writer.newLine();
					writer.write("List is Empty");
				}
				writer.flush();

//				if(fileCount==18) {
//					members.display();
//				}
				System.out.println("output"+fileCount+".txt");
			}
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		System.out.println("Finished Run");
	}
	
	private static boolean readFile(File file) throws IOException {
		members = new PonziSchemeTree();
		bReader = new BufferedReader(new FileReader(file));
		String line = "";
		String[] memberInfo;
		Member newMember;
		boolean notEmpty=false;
		while((line = bReader.readLine()) != null) {
			if(!line.isEmpty()) {
				notEmpty=true;
				memberInfo = line.split("#");
				if(memberInfo.length==3) {
					newMember = new Member(memberInfo[0], Integer.valueOf(memberInfo[1]), memberInfo[2]);
				}
				else {
					newMember = new Member(memberInfo[0], Integer.valueOf(memberInfo[1]),null);
				}
				members.addChildAfter(newMember);
			}
		}
		return notEmpty;
	}
	
	private static void runCalculations(int num) {
		results.clear();
		for(Position pos: members.positions()) {
			calcValue(num,pos,new ArrayList<Member>(), 0);
		}
	}

	private static void calcValue(int num, Position pos, ArrayList<Member> trace, int value) {
		trace.add(pos.getElement());
		value+=pos.getElement().getValue();
		if(num>1) {
			if(members.isInternal(pos)) {
				for(Position child : members.children(pos)) {
					if(trace.indexOf(child.getElement()) == -1) {
						calcValue(num-1,child,(ArrayList<Member>) trace.clone(),value);
					}
				}
			}
			if(members.mentor(pos) != null && trace.indexOf(members.mentor(pos).getElement()) == -1) {
				calcValue(num-1,members.mentor(pos),(ArrayList<Member>) trace.clone(),value);
			}
			if(members.sponsor(pos) != null && trace.indexOf(members.sponsor(pos).getElement()) == -1){
				calcValue(num-1,members.sponsor(pos),(ArrayList<Member>) trace.clone(),value);
			}
		}
		else {
			addTraceResult(trace, value);
		}
	}
	
	public static void addTraceResult(ArrayList<Member> trace, int value) {
		if(results.isEmpty()) {
			results.add(new ArrayList<Member>(trace));
		}
		else {
			if(maxValue < value) {
				maxValue=value;
				results.clear();
				results.add(new ArrayList<Member>(trace));
			}
			else if(maxValue == value && !results.contains(trace)) {
				results.add(new ArrayList<Member>(trace));
			}
		}
	}
	
	private static void writeOutput() throws IOException {
		int lineCount=0;
		writer.append("Maximum seized assets: "+ maxValue);
		for(ArrayList<Member> traces : results) {
			lineCount++;
			writer.append("\n");
			writer.append("List "+lineCount+": ");
			Iterator it = traces.iterator();
			while(it.hasNext()) {
				writer.append(((Member) it.next()).getName());
				if(it.hasNext()) {
					writer.append(", ");
				}
			}
		}
	}

}
