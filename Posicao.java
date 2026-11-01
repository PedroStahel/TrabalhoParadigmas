public class Posicao {
    private static final int MINIMO = 1;
    private static final int MAXIMO = 5;

    private final int x;
    private final int y;

    public Posicao(int x, int y) {
        if (!ehValida(x, y)) {
            throw new ArgumentoInvalido("Posição fora do tabuleiro: [" + x + "," + y + "].");
        }

        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    // isso vai ser usado para saber qual setor está do outro lado de uma porta
    public Posicao vizinha(Direcao direcao) {
        int novaLinha = this.x + direcao.getDeltaLinha();
        int novaColuna = this.y + direcao.getDeltaColuna();

        if (!ehValida(novaLinha, novaColuna)) {
            return null;
        }

        return new Posicao(novaLinha, novaColuna);
    }

    //essa checagem acontece em 2 lugares, então é melhor fazer isso para evitar duplicação de código
    public static boolean ehValida(int x, int y) {
        return x >= MINIMO && x <= MAXIMO && y >= MINIMO && y <= MAXIMO;
    }

    //isso eh usado para verificar se a posição atual é igual a outra posição, 
    // para saber se o jogador está no mesmo setor que outro jogador
    
    @Override
    public boolean equals(Object outro) {
        if (!(outro instanceof Posicao)) {
            return false;
        }

        Posicao outraPosicao = (Posicao) outro;

        return this.x == outraPosicao.x && this.y == outraPosicao.y;
    }

    // número que resume a posição ([3,4] vira 34). O Java exige que posições iguais no equals
    // tenham o mesmo hashCode; ele é usado por coleções como HashSet e HashMap
    @Override
    public int hashCode() {
        return this.x * 10 + this.y;
    }

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }
}
