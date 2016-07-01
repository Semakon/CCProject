package project_9.checker;

public class Op {
	
	private String instr;
	
	//Class that converts argument of strings to SprIL instructions.
	
	public Op(String operation, String... strings) {
		switch(operation) {
		case "Compute":
			compute(strings);
			break;
		case "LdConst":
			ldconst(strings);
			break;
		case "Load":
			load(strings);
			break;
		case "Jump":
			jump(strings);
			break;
		case "Branch":
			branch(strings);
			break;
		case "Store":
			store(strings);
			break;
		case "Push":
			push(strings);
			break;
		case "Pop":
			pop(strings);
			break;
		case "Receive":
			receive(strings);
			break;
		case "Read":
			read(strings);
			break;
		case "Testandset":
			tas(strings);
			break;
		case "Write":
			write(strings);
			break;
		case "Nop":
			this.instr = "Nop";
			break;
		case "EndProg":
			this.instr = "EndProg";
			break;
		}
	}
	
	public void setInstr(String instruc) {
		this.instr = instruc;
	}
	
	public String getInstr() {
		return this.instr;
	}
	
	private void compute(String... strings) {
		this.instr = "Compute " 
				+ strings[0] + " " 
				+ strings[1] + " " 
				+ strings[2] + " "
				+ strings[3];
	}
	
	private void ldconst(String...strings) {
		this.instr = "LdConst " 
				+ strings[0] + " "
				+ strings[1];
	}
	
	private void load(String...strings) {
		this.instr = "Load ("
				+ strings[0] + ") " 
				+ strings[1];
	}

	private void jump(String...strings) {
		this.instr = "Jump (" + strings[0] + ")";
	}
	
	private void branch(String...strings) {
		this.instr = "Branch " + strings[0]
				+ " (" + strings[1] + ")";
	}
	
	private void store(String...strings) {
		this.instr = "Store "
				+ strings[0] + " ("
				+ strings[1] + ")";
	}
	
	private void push(String...strings) {
		this.instr = "Push " + strings[0];
	}
	
	private void pop(String...strings) {
		this.instr = "Pop " + strings[0];
	}
	
	private void receive(String...strings) {
		this.instr = "Receive " + strings[0];
	}
	
	private void read(String...strings) {
		this.instr = "ReadInstr (" + strings[0] + ")";
	}
	
	private void tas(String...strings) {
		this.instr = "TestAndSet (" + strings[0] + ")";
	}
	
	private void write(String...strings) {
		this.instr = "WriteInstr " + strings[0]
				+ " (" + strings[1] + ")";
	}
}
