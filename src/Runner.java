import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Runner {
	static final int Q4 = 4;
	static final String Q4name = "Q4";
	static final int Q8 = 8;
	static final String Q8name = "Q8";
	static final int FCFS = -1;
	static final String FCFSname = "FCFS";

	static Queue<Process> quantum4 = new LinkedList<Process>();
	static Queue<Process> quantum8 = new LinkedList<Process>();
	static Queue<Process> fcfs = new LinkedList<Process>();

	static HashMap<Integer, Process> arrival = new HashMap<Integer, Process>();

	static {
		// arrival.put(0, new Process("P1", 15, 0, Q4));
		// arrival.put(6, new Process("P2", 20, 0, Q4));
		// arrival.put(10, new Process("P3", 2, 0, Q4));
		// arrival.put(16, new Process("P4", 16, 0, Q4));
		// arrival.put(32, new Process("P5", 6, 0, Q4));
		arrival.put(0, new Process("P1", 25, 0, Q4));
		arrival.put(18, new Process("P2", 10, 0, Q4));
		arrival.put(2, new Process("P3", 42, 0, Q4));
		arrival.put(10, new Process("P4", 35, 0, Q4));
		arrival.put(21, new Process("P5", 6, 0, Q4));
		arrival.put(30, new Process("P6", 12, 0, Q4));

	}

	static Process curProcess;
	static Process oldProcess;

	private static int getTotalTime(HashMap<Integer, Process> h) {
		int sum = 0;
		for (int key : h.keySet()) {
			System.out.println(h.get(key));
			sum += h.get(key).taskTimeRemaining;
		}
		return sum;
	}

	private static void printQueues() {
		System.out.print(Q4name + ": [");
		for (Process p : quantum4) {
			System.out.print(p.name + ", ");
		}
		System.out.println("]");

		System.out.print(Q8name + ": [");
		for (Process p : quantum8) {
			System.out.print(p.name + ", ");
		}
		System.out.println("]");

		System.out.print(FCFSname + ": [");
		for (Process p : fcfs) {
			System.out.print(p.name + ", ");
		}
		System.out.println("]");
	}

	static boolean debug = false;

	public static void main(String[] args) throws CloneNotSupportedException {
		curProcess = arrival.get(0);
		oldProcess = null;
		int totalTime = getTotalTime(arrival);

		for (int t = 0; t <= totalTime; t++) {
			if (debug)
				System.out.println("t=" + t);
			// check if current process has used up time
			if (curProcess != null) {
				if (curProcess.quantumUsedUp()) {
					int quantum = curProcess.queueAllottedTime;

					if (!curProcess.finished()) {
						// quantum used up, but process not finished
						// move it lower
						Process removed = null;
						switch (quantum) {
						case Q4:
							removed = quantum4.poll();
							assert (curProcess.equals(removed));
							removed.queueUsedUpTime = 0;
							removed.queueAllottedTime = Q8;
							quantum8.add(removed);
							if (debug)
								System.out.println("\tMoved [" + removed.name
										+ "] from " + Q4name + " to " + Q8name);
							break;
						case Q8:
							removed = quantum8.poll();
							assert (curProcess.equals(removed));
							removed.queueUsedUpTime = 0;
							removed.queueAllottedTime = FCFS;
							if (debug)
								System.out.println("\tMoved [" + removed.name
										+ "] from " + Q8name + " to "
										+ FCFSname);

							fcfs.add(removed);
							break;
						case FCFS:
							removed = fcfs.poll();
							assert (curProcess.equals(removed));
							fcfs.add(removed);
							if (debug)
								System.out.println("Moved [" + removed.name
										+ "] to back of " + FCFSname);

							break;
						}
						curProcess = null;
						assert (removed != null);
					} else {
						// quantum used up, but task is done? remove it.
						Process removed = null;
						switch (quantum) {
						case Q4:
							removed = quantum4.poll();
							assert (curProcess.equals(removed));

							break;
						case Q8:
							removed = quantum8.poll();
							assert (curProcess.equals(removed));
							break;
						case FCFS:
							removed = fcfs.poll();
							assert (curProcess.equals(removed));
							break;
						}
						assert (removed != null);
						if (debug)
							System.out.println("[" + removed.name
									+ "] complete");
						curProcess = null;
					}
				} else {
					// quantum NOT used up, and finished
					if (curProcess.finished()) {
						int quantum = curProcess.queueAllottedTime;

						Process removed = null;
						switch (quantum) {
						case Q4:
							removed = quantum4.poll();
							assert (curProcess.equals(removed));
							break;
						case Q8:
							removed = quantum8.poll();
							assert (curProcess.equals(removed));
							break;
						case FCFS:
							removed = fcfs.poll();
							assert (curProcess.equals(removed));
							break;
						}
						assert (removed != null);
						if (debug)
							System.out.println("[" + removed.name
									+ "] complete");
						curProcess = null;
					}
				}
			}

			if (arrival.containsKey(t)) {
				Process incoming = arrival.get(t);
				if (debug)
					System.out.println("\tIncoming: " + incoming);
				if (curProcess != null) {
					int quantum = curProcess.queueAllottedTime;
					switch (quantum) {
					case Q4:
						quantum4.add(incoming);
						break;
					case Q8:
						assert (quantum4.isEmpty());
						quantum4.add(incoming);
						curProcess = incoming;
						break;
					case FCFS:
						assert (quantum4.isEmpty());
						assert (quantum8.isEmpty());
						quantum4.add(incoming);
						curProcess = incoming;
						break;
					}
				} else {
					assert (quantum4.isEmpty());
					quantum4.add(incoming);
					curProcess = incoming;
				}
			}

			if (curProcess == null) {
				// curProcess is null
				// but incoming into q4
				// check if any in q4, q8, fcfs
				if (!quantum4.isEmpty()) {
					curProcess = quantum4.peek();
				} else if (!quantum8.isEmpty()) {
					curProcess = quantum8.peek();
				} else if (!fcfs.isEmpty()) {
					curProcess = fcfs.peek();
				}

			}

			if (curProcess != null) {
				if (debug) {
					System.out.println("\tcur=" + curProcess);
					System.out.println();
					printQueues();
					System.out.println();
				}
				curProcess.queueUsedUpTime++;
				curProcess.taskTimeRemaining--;
			} else {
				System.out.println("t=" + t + "--DONE");
			}

			if (!debug
					&& (curProcess != null)
					&& ((oldProcess == null) || !oldProcess.equals(curProcess) || (oldProcess
							.equals(curProcess) && oldProcess.queueAllottedTime != curProcess.queueAllottedTime)))
				System.out.println("t=" + t + "\tcur=" + curProcess);
			if (curProcess != null)
				oldProcess = (Process) curProcess.clone();

		}
	}
}
