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
	static ArrayList<Pair<Integer,ArrayList<Member>>> results = new ArrayList<>();
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
				String[] fileInfo = line.split(" ");
				files.add(new Pair<Integer,File>(Integer.valueOf(fileInfo[0]), new File(fileInfo[1])));
			}
			
			int fileCount=0;

			/**Iterates through the files found, reads them to save the customers to a SLL,
			 * it runs the four methods of serving and outputs the results.
			 */
			for (Pair<Integer, File> f: files){
				fileCount++;
				String file = f.getValue().toString();
				readFile(f.getValue());
				
				runCalculations(f.getKey());
				
				writer = new BufferedWriter(new FileWriter("output"+fileCount+".txt"));
				writeOutput();
				
				writer.flush();
			}
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		System.out.println("Finished Run");
	}
	
	private static void readFile(File file) throws IOException {
		members = new PonziSchemeTree();
		bReader = new BufferedReader(new FileReader(file));
		String line = "";
		String[] memberInfo;
		Member newMember;
		while((line = bReader.readLine()) != null) {
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
	
	private static void runCalculations(int num) {
		
		results.clear();
		for(Position pos: members.positions()) {
			checkedMembers.clear();
			calcValue(num,pos,new ArrayList<Member>(), 0);
		}
	}
	private static ArrayList<Position> checkedMembers = new ArrayList<>();

	private static void calcValue(int num, Position pos, ArrayList<Member> trace, int value) {
		trace.add(pos.getElement());
		value+=pos.getElement().getValue();
		checkedMembers.add(pos);
		if(num>1) {
			if(members.isInternal(pos)) {
				for(Position child : members.children(pos)) {
					if(checkedMembers.indexOf(child) == -1) {
						calcValue(num-1,child,(ArrayList<Member>) trace.clone(),value);
					}
				}
			}
			if(members.mentor(pos) != null && checkedMembers.indexOf(members.mentor(pos)) == -1) {
				calcValue(num-1,members.mentor(pos),(ArrayList<Member>) trace.clone(),value);
			}
			if(members.sponsor(pos) != null && checkedMembers.indexOf(members.sponsor(pos)) == -1){
				calcValue(num-1,members.sponsor(pos),(ArrayList<Member>) trace.clone(),value);
			}
		}
		else if(num==1) {
			addTraceResult(trace, value);
		}
	}
	
	public static void addTraceResult(ArrayList<Member> trace, int value) {
		if(results.isEmpty()) {
			results.add(new Pair<Integer, ArrayList<Member>>(value, trace));
		}
		else {
			if(results.get(0).getKey() < value) {
				results.clear();
				results.add(new Pair<Integer, ArrayList<Member>>(value, trace));
			}
			else if(results.get(0).getKey() == value) {
				results.add(new Pair<Integer, ArrayList<Member>>(value, trace));
			}
		}
	}
	
	private static void writeOutput() throws IOException {
		int lineCount=0;
		writer.append("Maximum seized assets: "+ results.get(0).getKey());
		for(Pair<Integer, ArrayList<Member>> pair : results) {
			lineCount++;
			writer.append("\n");
			writer.append("List "+lineCount+": ");
			Iterator it = pair.getValue().iterator();
			while(it.hasNext()) {
				writer.append(((Member) it.next()).getName());
				if(it.hasNext()) {
					writer.append(", ");
				}
			}
		}
	}

}
