public class Direcao {
    // Valores recebidos:índice da porta, delta linha, delta coluna
    public static final Direcao NORTE = new Direcao(0, -1, 0);
    public static final Direcao SUL = new Direcao(1, 1, 0);
    public static final Direcao LESTE = new Direcao(2, 0, 1);
    public static final Direcao OESTE = new Direcao(3, 0, -1);

    private final int indicePorta;
    private final int deltaLinha;
    private final int deltaColuna;

    private Direcao(int indicePorta, int deltaLinha, int deltaColuna) {
        this.indicePorta = indicePorta;
        this.deltaLinha = deltaLinha;
        this.deltaColuna = deltaColuna;
    }

    public int getIndicePorta() {
        return this.indicePorta;
    }

    public int getDeltaLinha() {
        return this.deltaLinha;
    }

    public int getDeltaColuna() {
        return this.deltaColuna;
    }

    // isso vai ser usado para saber qual direção é oposta a outra. Ex: o setor [3,3] tem uma porta para o leste
    // então o setor [3,4] tem uma porta para o oeste. Isso vale pq as portas são compartilhadas
    public Direcao oposta() {
        if (this == NORTE) {
            return SUL;
        } else if (this == SUL) {
            return NORTE;
        } else if (this == LESTE) {
            return OESTE;
        } else {
            return LESTE;
        }
    }

    // isso é utilizado assumindo que o usuário vai digitar uma letra para indicar a direção

    public static Direcao daLetra(String letra) {
        if (letra == null) {
            throw new ArgumentoInvalido("Digite uma direção: N, S, L ou O.");
        }

        String limpa = letra.trim().toUpperCase();

        if (limpa.equals("N")) {
            return NORTE;
        } else if (limpa.equals("S")) {
            return SUL; 
        } else if (limpa.equals("L")) {
            return LESTE;
        } else if (limpa.equals("O")) {
            return OESTE;
        } else {
            throw new ArgumentoInvalido("Direção inválida: \"" + letra + "\". Use N, S, L ou O.");
        }
    }
}
