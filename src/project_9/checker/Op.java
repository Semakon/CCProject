package project_9.checker;

public class Op {
	
	private String instr;
	
	//Class that converts argument of strings to SprIL instructions.
	
	public Op(String operation, String... strings) {
		switch(operation) {
		case "compute":
			compute(strings);
			break;
		case "ldconst":
			ldconst(strings);
			break;
		case "load":
			load(strings);
			break;
		case "jump":
			jump(strings);
			break;
		case "branch":
			branch(strings);
			break;
		case "store":
			store(strings);
			break;
		case "push":
			push(strings);
			break;
		case "pop":
			pop(strings);
			break;
		case "receive":
			receive(strings);
			break;
		case "read":
			read(strings);
			break;
		case "testandset":
			tas(strings);
			break;
		case "write":
			write(strings);
			break;
		}
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
		this.instr = "Load "
				+ strings[0] + " " 
				+ strings[1];
	}

	private void jump(String...strings) {
		this.instr = "Jump " + strings[0];
	}
	
	private void branch(String...strings) {
		this.instr = "Branch " + strings[0]
				+ " " + strings[1];
	}
	
	private void store(String...strings) {
		this.instr = "Store "
				+ strings[0] + " "
				+ strings[1];
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
		this.instr = "ReadInstr " + strings[0];
	}
	
	private void tas(String...strings) {
		this.instr = "TestAndSet " + strings[0];
	}
	
	private void write(String...strings) {
		this.instr = "WriteInstr " + strings[0]
				+ " " + strings[1];
	}
}
