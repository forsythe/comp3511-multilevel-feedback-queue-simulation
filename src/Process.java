public class Process {
	int taskTimeRemaining;
	int queueUsedUpTime;
	int queueAllottedTime;
	String name;

	protected Process(String name, int taskTimeRemaining, int queueUsedUpTime,
			int queueAllottedTime) {
		this.name = name;
		this.taskTimeRemaining = taskTimeRemaining;
		this.queueUsedUpTime = queueUsedUpTime;
		this.queueAllottedTime = queueAllottedTime;
	}

	public boolean quantumUsedUp() {
		if (queueAllottedTime == -1) {
			return false;
		} else {
			return queueUsedUpTime == queueAllottedTime;
		}
	}

	public boolean finished() {
		return taskTimeRemaining == 0;
	}

	@Override
	public boolean equals(Object o) {
		return ((Process) o).name.equals(this.name);
	}

	@Override
	public String toString() {
		return String.format("[%s]: remain[%d], \tquanta[%d/%d]", name,
				taskTimeRemaining, queueUsedUpTime, queueAllottedTime);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Process(this.name, this.taskTimeRemaining,
				this.queueUsedUpTime, this.queueAllottedTime);
	}
}
