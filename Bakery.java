public class BakeryAlgorithm {
    private static final int NUM_THREADS = 3;

    // Arrays para armazenar flags e tickets
    private final boolean[] choosing = new boolean[NUM_THREADS];
    private final int[] number = new int[NUM_THREADS];

    // Função auxiliar para encontrar o maior número atual
    private int maxNumber() {
        int max = 0;
        for (int num : number) {
            if (num > max) max = num;
        }
        return max;
    }

    // Implementação da entrada na seção crítica
    public void lock(int id) {
        choosing[id] = true;
        number[id] = maxNumber() + 1; // pega o próximo "ticket"
        choosing[id] = false;

        for (int j = 0; j < NUM_THREADS; j++) {
            if (j == id) continue;

            // Espera até que o outro thread termine de escolher
            while (choosing[j]) {
                Thread.yield();
            }

            // Espera se o outro tem prioridade
            while (number[j] != 0 && 
                  (number[j] < number[id] ||
                  (number[j] == number[id] && j < id))) {
                Thread.yield();
            }
        }
    }

    // Liberação da seção crítica
    public void unlock(int id) {
        number[id] = 0;
    }

    // Classe de trabalho das threads
    static class Worker extends Thread {
        private final BakeryAlgorithm bakery;
        private final int id;

        Worker(BakeryAlgorithm bakery, int id) {
            this.bakery = bakery;
            this.id = id;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                bakery.lock(id);

                // Seção crítica
                System.out.println("Thread " + id + " entrando na seção crítica");
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                System.out.println("Thread " + id + " saindo da seção crítica");

                bakery.unlock(id);

                // Seção não crítica
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
        }
    }

    // Execução
    public static void main(String[] args) {
        BakeryAlgorithm bakery = new BakeryAlgorithm();

        for (int i = 0; i < NUM_THREADS; i++) {
            new Worker(bakery, i).start();
        }
    }
}
