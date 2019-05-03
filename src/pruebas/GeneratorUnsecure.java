package pruebas;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class GeneratorUnsecure {

	private LoadGenerator generator;

	public GeneratorUnsecure() {
		Task work = createTask();
		int numberOfTasks = 10;
		int gapBetweenTasks = 20;
		generator = new LoadGenerator("Client - Server Load Test", numberOfTasks, work, gapBetweenTasks);
		this.generator.generate();
	}


	private Task createTask() {
		return new ClientServerUnsecureTask();
	}

	public static void main (String[] args) {

		@SuppressWarnings("unused")
		GeneratorUnsecure gen = new GeneratorUnsecure();
	}
}
